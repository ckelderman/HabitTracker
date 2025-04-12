package habitTrackerApp;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * Represents a single day's health and activity log.
 * <p>
 * Contains daily lifestyle attributes (sleep, mood, screen time, etc.)
 * and a list of habit objects completed that day.
 * <p>
 * This class implements serializable to allow saving/loading logs from disk.
 */
class DailyLog implements Serializable {
    private static final long serialVersionUID = 1L;

    // List of habits performed on this day
    LinkedList<Habit> habits = new LinkedList<>();

    // Daily data attributes
    int sleepHours;         // Number of hours slept
    int wakeUpTime;         // Wake-up time in 24-hour format (e.g., 7, 8, 9)
    boolean alcoholConsumed;// Whether alcohol was consumed that day
    boolean socialized;     // Whether social interaction occurred
    int screenTime;         // Screen time in hours
    int energyLevel;        // Self-rated energy level (0–10)
    int mood;               // Mood rating (0–10)
    int motivation;         // Motivation rating (0–10)

    /**
     * Adds a habit to the day's log.
     *
     * @param habit the habit to add
     */
    public void addHabit(Habit habit) {
        habits.add(habit);
    }

    /**
     * Returns the list of habits logged for this day.
     *
     * @return list of habits
     */
    public LinkedList<Habit> getHabits() {
        return habits;
    }
}
