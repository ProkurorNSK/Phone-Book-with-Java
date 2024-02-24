package phonebook;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.function.ToIntBiFunction;

public class Main {
    public static void main(String[] args) throws IOException {
        List<String> directoryList = Files.readAllLines(Paths.get("C:\\Users\\Pavel\\Downloads\\directory.txt"));
        String[] find = Files.readAllLines(Paths.get("C:\\Users\\Pavel\\Downloads\\find.txt")).toArray(new String[0]);
        Person[] directory = new Person[directoryList.size()];
        for (int i = 0; i < directoryList.size(); i++) {
            String line = directoryList.get(i);
            int d = line.indexOf(" ");
            directory[i] = new Person(line.substring(d + 1), Integer.parseInt(line.substring(0, d)));
        }

        System.out.println("Start searching (linear search)...");
        LocalTime time1 = LocalTime.now();
        int founds = searchArray(find, directory, Main::linearSearch);
        LocalTime time2 = LocalTime.now();
        Duration d = Duration.between(time1, time2);
        System.out.printf("Found %d / %d entries. Time taken: %d min. %d sec. %d ms.\n", founds, find.length, d.toMinutesPart(), d.toSecondsPart(), d.toMillisPart());

        System.out.println("Start searching (bubble sort + jump search)...");
        time1 = LocalTime.now();
        boolean tooSlow = bubbleSort(directory, d.getNano() / 1_000_000);
        time2 = LocalTime.now();
        founds = searchArray(find, directory, tooSlow ? Main::linearSearch : Main::jumpSearch);
        LocalTime time3 = LocalTime.now();
        d = Duration.between(time1, time3);
        System.out.printf("Found %d / %d entries. Time taken: %d min. %d sec. %d ms.\n", founds, find.length, d.toMinutesPart(), d.toSecondsPart(), d.toMillisPart());
        Duration dSort = Duration.between(time1, time2);
        Duration dSearch = Duration.between(time2, time3);
        System.out.printf("Sorting time: %d min. %d sec. %d ms.%s\n", dSort.toMinutesPart(), dSort.toSecondsPart(), dSort.toMillisPart(), tooSlow ? " - STOPPED, moved to linear search" : "");
        System.out.printf("Searching time: %d min. %d sec. %d ms.\n", dSearch.toMinutesPart(), dSearch.toSecondsPart(), dSearch.toMillisPart());
    }

    private static boolean bubbleSort(Person[] array, long dMilli) {
        long start = System.currentTimeMillis();
        long current;
        boolean swapped = true;
        while (swapped) {
            swapped = false;
            for (int i = 1; i < array.length - 1; i++) {
                if (array[i - 1].name().compareTo(array[i].name()) > 0) {
                    swap(array, i - 1, i);
                    swapped = true;
                    current = System.currentTimeMillis();
                    if (current - start > dMilli * 10) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static void swap(Person[] array, int i, int j) {
        Person temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    private static int jumpSearch(Person[] array, String target) {

        if (array.length == 0) {
            return -1;
        }

        int curr = 0;
        int prev = 0;
        int last = array.length - 1;
        int step = (int) Math.floor(Math.sqrt(array.length));

        while (array[curr].name().compareTo(target) < 0) {
            if (curr == last) {
                return -1;
            }
            prev = curr;
            curr = Math.min(curr + step, last);
        }

        while (array[curr].name().compareTo(target) > 0) {
            curr--;
            if (curr <= prev) {
                return -1;
            }
        }

        if (Objects.equals(array[curr].name(), target)) {
            return curr;
        }

        return -1;
    }

    private static int linearSearch(Person[] array, String target) {
        for (int i = 0; i < array.length; i++) {
            if (Objects.equals(array[i].name(), target)) {
                return i;
            }

        }
        return -1;
    }

    private static int searchArray(String[] find, Person[] directory, ToIntBiFunction<Person[], String> function) {
        int founds = 0;
        for (String name : find) {
            if (function.applyAsInt(directory, name) != -1) {
                founds++;
            }
        }
        return founds;
    }

    private record Person(String name, int number) {
    }
}
