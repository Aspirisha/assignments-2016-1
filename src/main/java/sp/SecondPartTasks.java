package sp;

import javafx.geometry.Point2D;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class SecondPartTasks {

    private SecondPartTasks() {}

    // Найти строки из переданных файлов, в которых встречается указанная подстрока.
    public static List<String> findQuotes(List<String> paths, CharSequence sequence) {
        return paths.stream().map(fname -> {
            try {
                return (new BufferedReader(new InputStreamReader(
                        Files.newInputStream(Paths.get(fname)))).
                        lines().filter(s -> s.contains(sequence)).collect(Collectors.toList()));
            } catch (IOException e) {
                return new LinkedList<String>();
            }
        }).collect(Collectors.toList()).stream().flatMap(List::stream).collect(Collectors.toList());
    }

    public static double piDividedBy4(int shots, @Nonnull Random rng) {
        return Stream.generate(() -> new Point2D(rng.nextDouble(), rng.nextDouble())).
                limit(shots).mapToInt(point -> point.distance(0.5, 0.5) <= 0.5 ? 1 : 0).
                sum() / (double)shots;
    }

    // В квадрат с длиной стороны 1 вписана мишень.
    // Стрелок атакует мишень и каждый раз попадает в произвольную точку квадрата.
    // Надо промоделировать этот процесс с помощью класса java.util.Random и посчитать, какова вероятность попасть в мишень.
    public static double piDividedBy4() {
        return piDividedBy4(1000000, new Random());
    }

    // Дано отображение из имени автора в список с содержанием его произведений.
    // Надо вычислить, чья общая длина произведений наибольшая.
    public static String findPrinter(@Nonnull Map<String, List<String>> compositions) {
        if (compositions.isEmpty()) {
            return null;
        }
        return compositions.entrySet().stream().max((e1, e2) -> {
            Function<Map.Entry<String, List<String>>, Integer> getSumStringLength =
                    e -> e.getValue().stream().reduce(0, (a, s) -> a += s.length(), (a,b)->a+b);
            return getSumStringLength.apply(e1).compareTo(getSumStringLength.apply(e2));
        }).get().getKey();
    }

    // Вы крупный поставщик продуктов. Каждая торговая сеть делает вам заказ в виде Map<Товар, Количество>.
    // Необходимо вычислить, какой товар и в каком количестве надо поставить.
    public static Map<String, Integer> calculateGlobalOrder(List<Map<String, Integer>> orders) {
        return orders.stream().flatMap(m -> m.entrySet().stream()).reduce(new HashMap<>(),
                (m, e) ->  {
                    m.put(e.getKey(), m.getOrDefault(e.getKey(), 0) + e.getValue());
                    return m;

                },  (m1, m2) -> {
                    m2.entrySet().stream().peek(e -> m1.put(e.getKey(),
                            m1.getOrDefault(e.getKey(), 0) + e.getValue()));
                    return m1;
                });
    }
}
