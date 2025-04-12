package habitTrackerApp;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

/**
 * Core logic class for the Habit Tracker application.
 *
 * @author Cody Cosmo
 * @version 1.0
 * @since 1.0
 */

/**
 * Main logic class for the Habit Tracker application.
 * <p>
 * Stores and manages DailyLog entries for each date,
 * supports validation, serialization to `.ser` files,
 * CSV logging for habits and daily data, and provides
 * analysis features like top 3 habit days and streak tracking.
 */
class HabitTracker {

    // Map storing logs keyed by date
    HashMap<LocalDate, DailyLog> logs = new HashMap<>();


    /**
     * Adds a daily log entry for the current date.
     *
     * @param date the date of the log
     * @param log  the daily log object
     */
    public void addDailyLog(LocalDate date, DailyLog log) {
        logs.put(date, log);
    }

    /**
     * Retrieves a daily log for the given date. If none exists, returns a new empty log.
     *
     * @param date the date to retrieve
     * @return existing or new DailyLog
     */
    public DailyLog getDailyLog(LocalDate date) {
        return logs.getOrDefault(date, new DailyLog());
    }

    /**
     * Validates that all numeric fields in the log are within expected bounds.
     *
     * @param log the log to validate
     * @throws IllegalArgumentException if any value is out of range
     */
    public void validateDailyLog(DailyLog log) throws IllegalArgumentException {
        if (log.sleepHours < 0 || log.sleepHours > 24)
            throw new IllegalArgumentException("Sleep hours must be between 0 and 24.");
        if (log.wakeUpTime < 0 || log.wakeUpTime > 24)
            throw new IllegalArgumentException("Wake-up time must be between 0 and 24.");
        if (log.screenTime < 0 || log.screenTime > 24)
            throw new IllegalArgumentException("Screen time must be between 0 and 24.");
        if (log.energyLevel < 0 || log.energyLevel > 10)
            throw new IllegalArgumentException("Energy level must be between 0 and 10.");
        if (log.mood < 0 || log.mood > 10)
            throw new IllegalArgumentException("Mood must be between 0 and 10.");
        if (log.motivation < 0 || log.motivation > 10)
            throw new IllegalArgumentException("Motivation must be between 0 and 10.");
    }

    /**
     * Checks whether a specific Date ID (in "yyyyMMdd" format) already exists in daily_logs.csv.
     *
     * @param dateId the date ID to search for
     * @return true if found, false otherwise
     */
    public boolean isDateIdPresent(String dateId) {
        File dailyLogFile = new File("daily_logs.csv");
        if (!dailyLogFile.exists()) return false;

        try (BufferedReader reader = new BufferedReader(new FileReader(dailyLogFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(dateId + ",")) return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Saves the in-memory logs map to a serialized file (.ser).
     */
    public void saveToSerFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("habit_logs.ser"))) {
            oos.writeObject(logs);
            System.out.println("Data saved to habit_logs.ser");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Appends a single habit entry to "habits.csv".
     * Adds headers if the file doesn't exist yet.
     *
     * @param habit the habit to write
     */
    public void saveHabitToCSV(Habit habit) {
        boolean habitFirstWrite = !new File("habits.csv").exists();
        String dateID = LocalDate.now().toString().replace("-", "");

        try (PrintWriter habitWriter = new PrintWriter(new FileWriter("habits.csv", true))) {
            if (habitFirstWrite) {
                habitWriter.println("Date ID,Habit Name,Start Time,End Time,Duration,Completed");
            }

            habitWriter.println(dateID + "," + habit.getName() + "," + habit.getStartTime() + ","
                    + habit.getEndTime() + "," + habit.getDuration() + "," + habit.isCompleted());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes the current day's daily log to "daily_logs.csv",
     * if not already present (checked via Date ID).
     * Then also saves to the serialized file.
     */
    public void saveDailyLogToCSV() {
        boolean logFirstWrite = !new File("daily_logs.csv").exists();
        String dateID = LocalDate.now().toString().replace("-", "");

        try (PrintWriter logWriter = new PrintWriter(new FileWriter("daily_logs.csv", true))) {
            if (logFirstWrite) {
                logWriter.println("Date ID,Sleep Hours,Wake Up Time,Alcohol Consumed,Socialized,Screen Time,Energy Level,Mood,Motivation");
            }

            if (!isDateIdPresent(dateID)) {
                DailyLog log = logs.get(LocalDate.now());
                if (log != null) {
                    logWriter.println(dateID + "," + log.sleepHours + "," + log.wakeUpTime + ","
                            + log.alcoholConsumed + "," + log.socialized + "," + log.screenTime + ","
                            + log.energyLevel + "," + log.mood + "," + log.motivation);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        saveToSerFile();
    }

    /**
     * Returns the top 3 dates with the highest total habit duration,
     * sorted in descending order using insertion sort.
     *
     * @return list of top 3 dates
     */
    public List<LocalDate> getTop3HabitDays() {
        List<Map.Entry<LocalDate, Integer>> durationsPerDay = new ArrayList<>();
        for (Map.Entry<LocalDate, DailyLog> entry : logs.entrySet()) {
            int total = 0;
            for (Habit habit : entry.getValue().getHabits()) {
                total += habit.getDuration();
            }
            durationsPerDay.add(new AbstractMap.SimpleEntry<>(entry.getKey(), total));
        }

        HabitSorter.sortByTotalHabitDuration(durationsPerDay);

        List<LocalDate> topDays = new ArrayList<>();
        for (int i = 0; i < Math.min(3, durationsPerDay.size()); i++) {
            topDays.add(durationsPerDay.get(i).getKey());
        }
        return topDays;
    }

    /**
     * Loads habit logs from the serialized file "habit_logs.ser" into memory.
     * Also prints them to the console for the developer.
     */
    @SuppressWarnings("unchecked")
    public void readSerFile() {
        File serFile = new File("habit_logs.ser");
        if (!serFile.exists()) {
            System.out.println("No serialized file found.");
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(serFile))) {
            logs = (HashMap<LocalDate, DailyLog>) ois.readObject();
            System.out.println("Data successfully loaded from habit_logs.ser");

            for (Map.Entry<LocalDate, DailyLog> entry : logs.entrySet()) {
                LocalDate date = entry.getKey();
                DailyLog log = entry.getValue();

                System.out.println("Date: " + date);
                System.out.println("Sleep Hours: " + log.sleepHours);
                System.out.println("Wake Up Time: " + log.wakeUpTime);
                System.out.println("Screen Time: " + log.screenTime);
                System.out.println("Energy Level: " + log.energyLevel);
                System.out.println("Mood: " + log.mood);
                System.out.println("Motivation: " + log.motivation);

                System.out.println("Habits:");
                for (Habit habit : log.getHabits()) {
                    System.out.println(" - " + habit.getName() + ", Start Time: " + habit.getStartTime()
                            + ", Duration: " + habit.getDuration() + " mins, Completed: " + habit.isCompleted());
                }
                System.out.println("--------------------");
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Calculates the length of a habit streak which includes the given date.
     * A streak is consecutive days with at least 1 habit completed on each day
     *
     * @param date the end date to check streak backward from
     * @return number of consecutive days with at least one habit
     */
    public int calculateHabitStreak(LocalDate date) {
        int streak = 0;
        LocalDate current = date;

        while (logs.containsKey(current)) {
            DailyLog log = logs.get(current);
            if (log.getHabits().isEmpty()) break;
            streak++;
            current = current.minusDays(1);
        }

        return streak;
    }
}
