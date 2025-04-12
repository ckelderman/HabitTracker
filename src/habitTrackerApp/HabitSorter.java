package habitTrackerApp;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Class for sorting habit-related data using custom sorting algorithms.
 * Currently includes:
 *  - Sorting habits within a day by start time
 *  - Sorting dates by total habit duration
 */
public class HabitSorter {

    /**
     * Sorts a list of habits by their start time using insertion sort.
     *
     * @param habits the list of habit objects to sort
     */
    public static void sortHabitsByStartTime(LinkedList<Habit> habits) {
        for (int i = 1; i < habits.size(); i++) {
            Habit key = habits.get(i);
            int j = i - 1;

            // Move elements greater than 'key' one position ahead
            while (j >= 0 && habits.get(j).getStartTime() > key.getStartTime()) {
                habits.set(j + 1, habits.get(j));
                j--;
            }

            // Insert the key into its correct position
            habits.set(j + 1, key);
        }
    }

    /**
     * Sorts a list of (date, totalDuration) entries in descending order
     * using insertion sort. This is used to rank days by total habit time completed that day
     *
     * @param entries a list of Map.Entry objects where the key is the date,
     *                and the value is the total duration of habits for that day
     */
    public static void sortByTotalHabitDuration(List<Map.Entry<LocalDate, Integer>> entries) {
        for (int i = 1; i < entries.size(); i++) {
            Map.Entry<LocalDate, Integer> key = entries.get(i);
            int j = i - 1;

            // Sort in descending order of total duration
            while (j >= 0 && entries.get(j).getValue() < key.getValue()) {
                entries.set(j + 1, entries.get(j));
                j--;
            }

            // Insert the current entry into the sorted portion
            entries.set(j + 1, key);
        }
    }
}
