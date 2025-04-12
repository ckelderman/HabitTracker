package habitTrackerApp;

import java.io.Serializable;

/**
 * Represents a single habit activity tracked by the user.
 * Stores the habit name, start and end time (in minutes since midnight),
 * and stores whether the habit was completed
 * <p>
 * Implements Serializable so it can be saved in a .ser file to make use of storage features such as linked list
 */
class Habit implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private boolean completed;
    private int startTime; // In minutes from midnight (480 = 8:00am)
    private int endTime;   // Determined as startTime + duration

    /**
     * Constructs a habit with a name, start time, and duration in minutes.
     * Automatically sets the habit as completed.
     *
     * @param name      the name of the habit (e.g. "Piano")
     * @param startTime the start time in minutes since midnight
     * @param duration  duration in minutes
     */
    public Habit(String name, int startTime, int duration) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = startTime + duration;
        this.completed = true;
    }

    /**
     * Returns the duration of the habit in minutes.
     *
     * @return duration in minutes
     */
    public int getDuration() {
        return endTime - startTime;
    }

    /**
     * Returns the name of the habit.
     *
     * @return habit name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns whether the habit was marked as completed.
     *
     * @return true if completed, false otherwise
     */
    public boolean isCompleted() {
        return completed;
    }

    /**
     * Returns the habit's start time in minutes since midnight.
     *
     * @return start time in minutes
     */
    public int getStartTime() {
        return startTime;
    }

    /**
     * Returns the habit's end time in minutes since midnight.
     *
     * @return end time in minutes
     */
    public int getEndTime() {
        return endTime;
    }

    /**
     * Returns a CSV-style string representation of the habit
     *
     * @return formatted habit string
     */
    @Override
    public String toString() {
        return name + "," + completed + "," + startTime + "," + endTime + "," + getDuration();
    }
}
