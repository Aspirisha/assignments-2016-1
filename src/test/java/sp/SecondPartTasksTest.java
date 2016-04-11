package sp;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static sp.SecondPartTasks.*;

public class SecondPartTasksTest {

    private static String generateString(Random rng, int length) {
        char[] text = new char[length];
        for (int i = 0; i < length; i++) {
            text[i] = (char) (rng.nextInt() % 27 + 'a');
        }
        return new String(text);
    }

    @Test
    public void testFindQuotes() throws IOException {
        List<String> bananaStrings = Arrays.asList("This string contains banana",
                "in fact, bananas cost too much nowadays!", "much of bananas");
        ImmutableMap<String, List<String>> files = ImmutableMap.of(
                "test/file1", Arrays.asList(bananaStrings.get(0),
                "and this doesn't", bananaStrings.get(1)),
                "test/file2", Arrays.asList("This string contains oranges",
                bananaStrings.get(2), "no yellow fruits here"),
                "test/empty", new LinkedList<>());

        for (Map.Entry<String, List<String>> entry : files.entrySet()) {
            Files.write(Paths.get(entry.getKey()), entry.getValue());
        }

        String unexistentFile = "NEVER_EVER_EXISTED_FILE!!!!";

        File f = new File(unexistentFile);
        Random rng = new Random();
        while (f.exists()) {
            unexistentFile = generateString(rng, 10);
            f = new File(unexistentFile);
        }

        List<String> fileNames = new LinkedList<>();
        fileNames.addAll(files.keySet());
        fileNames.add(unexistentFile);

        List<String> result = findQuotes(fileNames, "banana");

        assertEquals(result.size(), 3);
        for (String s : bananaStrings) {
            assertTrue(result.contains(s));
        }
    }

    @Test

    public void testPiDividedBy4() {
        double piDiv4 = Math.PI / 4;
        double dif = Double.MAX_VALUE;
        Random rng = new Random(0xBEEFDEAD); // this random is ok for tests, diff will decrease
        for (int shots = 10; shots < 1000_000_000; shots *= 1000) {
            double res = piDividedBy4(shots, rng);
            double newDif = Math.abs(res - piDiv4);
            assertTrue(newDif < dif);
            dif = newDif;
        }
    }

    @Test
    public void testFindPrinter() {
        ImmutableMap<String, List<String>> compositions = ImmutableMap.of("Shakespear",
                Arrays.asList("My mistresses eyes are nothing lik the sun!", "To be or not to be?",
                        "Niggard of question; but, of our demands,\n" +
                        "Most free in his reply."), "Брюсов", Arrays.asList("О закрой свои бледные ноги!"),
                "Пушкин", Arrays.asList("Мой дядя самых честных правил", "Когда не в шутку занемог"),
                "Неплодовитый", ImmutableList.of());

        assertEquals("Shakespear", findPrinter(compositions));
        assertEquals(null, findPrinter(ImmutableMap.of()));
    }

    @Test
    public void testCalculateGlobalOrder() {
        List<Map<String, Integer>> l1 = ImmutableList.of(
                ImmutableMap.of("cabbage", 3, "carrots", 5, "bananas", 100500),
                ImmutableMap.of("potatoes", 12, "carrots", 2, "bananas", 1),
                ImmutableMap.of());

        Map<String, Integer> res = calculateGlobalOrder(l1);
        assertEquals(3, (int)res.get("cabbage"));
        assertEquals(7, (int)res.get("carrots"));
        assertEquals(100501, (int)res.get("bananas"));

        assertTrue(calculateGlobalOrder(ImmutableList.of()).isEmpty());
    }
}