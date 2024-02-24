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
            int del = line.indexOf(" ");
            directory[i] = new Person(line.substring(del + 1), Integer.parseInt(line.substring(0, del)));
        }

        System.out.println("Start searching (linear search)...");
        LocalTime time1 = LocalTime.now();
        int founds = searchArray(find, directory, Main::linearSearch);
        LocalTime time2 = LocalTime.now();
        Duration d = Duration.between(time1, time2);
        System.out.printf("Found %d / %d entries. Time taken: %d min. %d sec. %d ms.\n\n", founds, find.length, d.toMinutesPart(), d.toSecondsPart(), d.toMillisPart());

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
        System.out.printf("Searching time: %d min. %d sec. %d ms.\n\n", dSearch.toMinutesPart(), dSearch.toSecondsPart(), dSearch.toMillisPart());

        System.out.println("Start searching (quick sort + binary search)...");
        for (int i = 0; i < directoryList.size(); i++) {
            String line = directoryList.get(i);
            int del = line.indexOf(" ");
            directory[i] = new Person(line.substring(del + 1), Integer.parseInt(line.substring(0, del)));
        }
        time1 = LocalTime.now();
        quickSort(directory, 0, directory.length - 1);
        time2 = LocalTime.now();
        founds = searchArray(find, directory, Main::binarySearch);
        time3 = LocalTime.now();
        d = Duration.between(time1, time3);
        System.out.printf("Found %d / %d entries. Time taken: %d min. %d sec. %d ms.\n", founds, find.length, d.toMinutesPart(), d.toSecondsPart(), d.toMillisPart());
        dSort = Duration.between(time1, time2);
        dSearch = Duration.between(time2, time3);
        System.out.printf("Sorting time: %d min. %d sec. %d ms.\n", dSort.toMinutesPart(), dSort.toSecondsPart(), dSort.toMillisPart());
        System.out.printf("Searching time: %d min. %d sec. %d ms.\n", dSearch.toMinutesPart(), dSearch.toSecondsPart(), dSearch.toMillisPart());
    }

    private static void quickSort(Person[] array, int l, int r) {
        if (l < r) {
            int pivot = Partition(array, l, r);
            quickSort(array, l, pivot - 1);
            quickSort(array, pivot + 1, r);
        }
    }

    private static int Partition(Person[] array, int l, int r) {
        Person x = array[r];
        int i = l - 1;
        for (int j = l; j < r; j++) {
            if (array[j].name().compareTo(x.name()) < 0) {
                i++;
                swap(array, i, j);
            }
        }
        swap(array, i + 1, r);
        return i + 1;
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

    private static int binarySearch(Person[] array, String target) {
        int left = 0;
        int right = array.length - 1;
        while (left <= right) {
            int middle = (left + right) / 2;
            if (Objects.equals(array[middle].name(), target)) {
                return middle;
            } else if (array[middle].name().compareTo(target) > 0) {
                right = middle - 1;
            } else {
                left = middle + 1;
            }
        }
        return -1;
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
