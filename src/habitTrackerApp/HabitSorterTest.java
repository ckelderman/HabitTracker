package habitTrackerApp;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class HabitSorterTest {

    @Test
    public void testSortHabitsByStartTime() {
        Habit h1 = new Habit("Piano", 600, 30);   // 10:00am
        Habit h2 = new Habit("Drums", 480, 45);   // 8:00am
        Habit h3 = new Habit("Vocals", 540, 60);  // 9:00am

        LinkedList<Habit> habits = new LinkedList<>(Arrays.asList(h1, h2, h3));
        HabitSorter.sortHabitsByStartTime(habits);

        assertEquals("Drums", habits.get(0).getName());
        assertEquals("Vocals", habits.get(1).getName());
        assertEquals("Piano", habits.get(2).getName());
    }

    @Test
    public void testSortByTotalHabitDuration() {
        LocalDate d1 = LocalDate.of(2024, 4, 1);
        LocalDate d2 = LocalDate.of(2024, 4, 2);
        LocalDate d3 = LocalDate.of(2024, 4, 3);

        Map.Entry<LocalDate, Integer> e1 = new AbstractMap.SimpleEntry<>(d1, 30);
        Map.Entry<LocalDate, Integer> e2 = new AbstractMap.SimpleEntry<>(d2, 90);
        Map.Entry<LocalDate, Integer> e3 = new AbstractMap.SimpleEntry<>(d3, 60);

        List<Map.Entry<LocalDate, Integer>> entries = new ArrayList<>(Arrays.asList(e1, e2, e3));
        HabitSorter.sortByTotalHabitDuration(entries);

        assertEquals(d2, entries.get(0).getKey()); // 90 mins
        //System.out.println(entries.get(0).getKey());
        assertEquals(d3, entries.get(1).getKey()); // 60 mins
        //System.out.println(entries.get(1).getKey());
        assertEquals(d1, entries.get(2).getKey()); // 30 mins
        //System.out.println(entries.get(2).getKey());

    }
}
