package habitTrackerApp;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Random;


/**
 * Class to generate fake habits and daily log data
 * and serialize it to a file ("habit_logs.ser") for testing purposes.
 * It randomly generates:
 * - 10 days of logs
 * - Daily attributes (sleep, wake time, screen time, etc.)
 * - 0 to 5 habits per day
 */

public class FakeDataGenerator {
	
    /**
     * Main method that creates a fake dataset of daily logs
     * and writes it to a serialized file
     */
    public static void main(String[] args) {
        HashMap<LocalDate, DailyLog> logs = new HashMap<>();
        Random rand = new Random();

        String[] habitNames = {"Piano", "Vocals", "Drums", "Stretching", "Exercising", "Studying"};
        
        // Generate logs for the past 10 days
        for (int i = 0; i < 10; i++) {
            LocalDate date = LocalDate.now().minusDays(9 - i);
            DailyLog log = new DailyLog();

            // Daily daily attributes
            log.sleepHours = rand.nextInt(4) + 6; // 6–9 hours
            log.wakeUpTime = rand.nextInt(4) + 6; // 6–9am
            log.screenTime = rand.nextInt(5) + 2; // 2–6 hours
            log.energyLevel = rand.nextInt(11);   // 0–10
            log.mood = rand.nextInt(11);          // 0–10
            log.motivation = rand.nextInt(11);    // 0–10
            log.alcoholConsumed = rand.nextBoolean();
            log.socialized = rand.nextBoolean();

            // Randomly add habits (0–5 per day)
            int habitCount = rand.nextInt(6);
            for (int j = 0; j < habitCount; j++) {
                String habitName = habitNames[rand.nextInt(habitNames.length)];
                int start = rand.nextInt(12) * 60; // start time between 0–11 AM just for testing
                int duration = (rand.nextInt(4) + 1) * 15; // 15–60 mins
                Habit habit = new Habit(habitName, start, duration);
                log.addHabit(habit);
            }

            logs.put(date, log);
        }

        // Serialize the file
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("habit_logs.ser"))) {
            oos.writeObject(logs);
            System.out.println("Fake data written to habit_logs.ser");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
