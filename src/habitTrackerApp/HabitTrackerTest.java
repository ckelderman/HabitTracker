package habitTrackerApp;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

public class HabitTrackerTest {

    @Test
    public void testAddAndRetrieveDailyLog() {
        HabitTracker tracker = new HabitTracker();
        LocalDate today = LocalDate.now();
        DailyLog log = new DailyLog();

        tracker.addDailyLog(today, log);
        assertSame(log, tracker.getDailyLog(today));
    }

    @Test
    public void testValidateDailyLog_Pass() {
        DailyLog log = new DailyLog();
        log.sleepHours = 8;
        log.wakeUpTime = 7;
        log.screenTime = 4;
        log.energyLevel = 6;
        log.mood = 8;
        log.motivation = 7;

        HabitTracker tracker = new HabitTracker();
        assertDoesNotThrow(() -> tracker.validateDailyLog(log));
    }

    @Test
    public void testValidateDailyLog_FailSleep() {
        DailyLog log = new DailyLog();
        log.sleepHours = -2;

        HabitTracker tracker = new HabitTracker();
        Exception exception = assertThrows(IllegalArgumentException.class, () -> tracker.validateDailyLog(log));
        assertEquals("Sleep hours must be between 0 and 24.", exception.getMessage());
    }

    @Test
    public void testCalculateHabitStreak() {
        HabitTracker tracker = new HabitTracker();
        LocalDate day1 = LocalDate.now().minusDays(2);
        LocalDate day2 = LocalDate.now().minusDays(1);
        LocalDate day3 = LocalDate.now();

        DailyLog log1 = new DailyLog(); log1.addHabit(new Habit("Drums", 480, 30));
        DailyLog log2 = new DailyLog(); log2.addHabit(new Habit("Piano", 600, 45));
        DailyLog log3 = new DailyLog(); log3.addHabit(new Habit("Vocals", 720, 60));

        tracker.addDailyLog(day1, log1);
        tracker.addDailyLog(day2, log2);
        tracker.addDailyLog(day3, log3);

        assertEquals(3, tracker.calculateHabitStreak(day3));
    }
}
