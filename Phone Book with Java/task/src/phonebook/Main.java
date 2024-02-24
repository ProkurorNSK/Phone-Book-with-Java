package phonebook;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        List<String> directory = Files.readAllLines(Paths.get("C:\\Users\\Pavel\\Downloads\\directory.txt"));
        List<String> find = Files.readAllLines(Paths.get("C:\\Users\\Pavel\\Downloads\\find.txt"));

        System.out.println("Start searching...");
        int founds = 0;
        LocalTime time1 = LocalTime.now();

        for (String name : find) {
            for (String entries : directory) {
                if (entries.contains(name)) {
                    founds++;
                    break;
                }

            }
        }

        LocalTime time2 = LocalTime.now();
        Duration d = Duration.between(time1, time2);
        System.out.printf("Found %d / %d entries. Time taken: %d min. %d sec. %d ms.\n", founds, find.size(), d.toMinutesPart(), d.toSecondsPart(), d.toMillisPart());
    }
}
