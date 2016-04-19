package task;

import javafx.util.Pair;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.stream.Collectors;


public class Injector {

    /**
     * Create and initialize object of `rootClassName` class using classes from
     * `implementationClassNames` for concrete dependencies.
     */
    public static Object initialize(String rootClassName, List<String> implementationClassNames) throws Exception {
        Map<String, Object> createdObjects = new HashMap<>();
        Stack<Pair<String, Set<String>>> neededObjectsAndWhoDependOnThem = new Stack<>();
        neededObjectsAndWhoDependOnThem.add(new Pair<>(rootClassName, new HashSet<>()));

        List<Pair<String, Class<?>>> existingClasses = new LinkedList<>();

        for (String c : implementationClassNames) {
            existingClasses.add(new Pair<>(c, Class.forName(c)));
        }

        while (!neededObjectsAndWhoDependOnThem.isEmpty()) {
            Pair<String, Set<String>> currentRoot = neededObjectsAndWhoDependOnThem.peek();

            if (createdObjects.containsKey(currentRoot.getKey())) {
                neededObjectsAndWhoDependOnThem.pop();
                continue;
            }

            Class<?> clazz = Class.forName(currentRoot.getKey());
            Constructor[] allConstructors = clazz.getDeclaredConstructors();
            if (allConstructors.length != 1)
                throw new ImplementationNotFoundException();

            Class<?>[] pType  = allConstructors[0].getParameterTypes();

            List<String> dependencies = new ArrayList<>();
            for (Class<?> cls : pType) {
                List<Pair<String, Class<?>>> possibleImpls = existingClasses.stream().filter( p->
                        cls.isAssignableFrom(p.getValue())).collect(Collectors.toList());
                switch (possibleImpls.size()) {
                    case 0:
                        throw new ImplementationNotFoundException();
                    case 1:
                        dependencies.add(possibleImpls.get(0).getKey());
                        break;
                    default:
                        throw new AmbiguousImplementationException();
                }
            }

            boolean hasNotCreatedDependency = dependencies.stream().anyMatch(d -> !createdObjects.containsKey(d));

            if (!hasNotCreatedDependency) {
                List<Object> Args = dependencies.stream().map(createdObjects::get).
                        collect(Collectors.toList());
                createdObjects.put(currentRoot.getKey(), allConstructors[0].newInstance(Args.toArray()));
                neededObjectsAndWhoDependOnThem.pop();
                continue;
            }

            for (String s : dependencies) {
                if (!createdObjects.containsKey(s)) {
                    if (currentRoot.getValue().contains(s)) {
                        throw new InjectionCycleException();
                    }

                    Set<String> whoDependsOnThis = new HashSet<>(currentRoot.getValue());
                    whoDependsOnThis.add(currentRoot.getKey());
                    neededObjectsAndWhoDependOnThem.push(new Pair<>(s, whoDependsOnThis));
                }
            }
        }
        return createdObjects.get(rootClassName);
    }
}