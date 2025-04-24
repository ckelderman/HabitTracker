package habitTrackerApp;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class HabitTest {

    @Test
    public void testGetDuration() {
        Habit habit = new Habit("Piano", 480, 60); // 8:00 AM, 1 hour
        assertEquals(60, habit.getDuration());
    }

    @Test
    public void testIsCompleted() {
        Habit habit = new Habit("Drums", 600, 45);
        assertTrue(habit.isCompleted());
    }

    @Test
    public void testToStringFormat() {
        Habit habit = new Habit("Stretching", 300, 30);
        String expected = "Stretching,true,300,330,30";
        assertEquals(expected, habit.toString());
    }
    
}
