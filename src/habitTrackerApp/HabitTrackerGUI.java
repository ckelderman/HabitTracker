package habitTrackerApp;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;


/**
 * The GUI class for the Habit Tracker application.
 * <p>
 * Provides a user interface for logging habits, daily metrics,
 * viewing top habit days, and reading ser data
 */
class HabitTrackerGUI {

    private HabitTracker tracker = new HabitTracker();

    // GUI components
    private JFrame frame;
    private JComboBox<String> habitDropdown, hourDropdown, minuteDropdown, durationDropdown;
    private JTextArea logArea;

    // Predefined options for drop-downs
    private static final String[] HABITS = {"Piano", "Vocals", "Drums", "Stretching", "Exercising", "Studying"};
    private static final String[] DURATIONS = {"15", "30", "45", "60"};
    private static final String[] HOURS = {
    	    "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11",
    	    "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"
    	};
    private static final String[] MINUTES = {"00", "15", "30", "45"};

    /**
     * Initializes the GUI components and layout.
     * Sets up action listeners for habit logging, reading data, and viewing stats.
     */
    public HabitTrackerGUI() {
        frame = new JFrame("Habit Tracker");
        frame.setSize(500, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(13, 2));

        // Drop-downs for selecting habit details
        habitDropdown = new JComboBox<>(HABITS);
        hourDropdown = new JComboBox<>(HOURS);
        minuteDropdown = new JComboBox<>(MINUTES);
        durationDropdown = new JComboBox<>(DURATIONS);
        hourDropdown.setSelectedItem("8"); // default start time
        durationDropdown.setSelectedItem("45");

        // Buttons for interaction
        JButton addButton = new JButton("Add Habit");
        JButton dayCompleteButton = new JButton("Log Completed Day");
        JButton readSerButton = new JButton("Read ser Data:");
        JButton topDaysButton = new JButton("Top 3 Habit Days");

        // Text area to display logs and basic statistics
        logArea = new JTextArea();

        // Layout components
        panel.add(new JLabel("Select Habit:")); panel.add(habitDropdown);
        panel.add(new JLabel("Start Time (Hour):")); panel.add(hourDropdown);
        panel.add(new JLabel("Start Time (Minute):")); panel.add(minuteDropdown);
        panel.add(new JLabel("Duration (Minutes):")); panel.add(durationDropdown);
        panel.add(new JLabel()); panel.add(addButton);
        panel.add(dayCompleteButton);
        // panel.add(readSerButton); For testing purposes. Uncomment to read ser manually
        panel.add(topDaysButton);

        frame.add(panel, BorderLayout.NORTH);
        frame.add(new JScrollPane(logArea), BorderLayout.CENTER);

        // Attach button actions
        addButton.addActionListener(e -> addHabit());
        dayCompleteButton.addActionListener(e -> openDayCompleteWindow());
        readSerButton.addActionListener(e -> tracker.readSerFile());
        topDaysButton.addActionListener(e -> displayTopHabitDays());

        frame.setVisible(true);
    }

    /**
     * Adds a selected habit to today's log and updates the display.
     */
    private void addHabit() {
        String selectedHabit = (String) habitDropdown.getSelectedItem();
        int hour = Integer.parseInt((String) hourDropdown.getSelectedItem());
        int minute = Integer.parseInt((String) minuteDropdown.getSelectedItem());
        int duration = Integer.parseInt((String) durationDropdown.getSelectedItem());
        int startTime = hour * 60 + minute;

        Habit habit = new Habit(selectedHabit, startTime, duration);
        LocalDate today = LocalDate.now();
        DailyLog log = tracker.getDailyLog(today);
        log.addHabit(habit);
        tracker.addDailyLog(today, log);
        tracker.saveHabitToCSV(habit);

        updateLogArea();
    }

    /**
     * Opens a pop-up dialog for the user to enter their daily metrics.
     * Validates input and saves to both memory and CSV.
     */
    private void openDayCompleteWindow() {
        String dateID = LocalDate.now().toString().replace("-", "");
        if (tracker.isDateIdPresent(dateID)) {
            JOptionPane.showMessageDialog(frame, "Daily attributes for today are already saved.");
            return;
        }

        JDialog dialog = new JDialog(frame, "Complete Your Day", true);
        dialog.setSize(400, 300);
        dialog.setLayout(new GridLayout(7, 2));

        // Input fields
        JTextField sleepField = new JTextField();
        JTextField wakeField = new JTextField();
        JTextField screenField = new JTextField();
        JTextField energyField = new JTextField();
        JTextField moodField = new JTextField();
        JTextField motivationField = new JTextField();

        dialog.add(new JLabel("Hours Slept:")); dialog.add(sleepField);
        dialog.add(new JLabel("Wake-up Time Hour:")); dialog.add(wakeField);
        dialog.add(new JLabel("Screen Time Hours:")); dialog.add(screenField);
        dialog.add(new JLabel("Energy Level (0-10):")); dialog.add(energyField);
        dialog.add(new JLabel("Mood Level (0-10):")); dialog.add(moodField);
        dialog.add(new JLabel("Motivation Level (0-10):")); dialog.add(motivationField);

        // Submit button and validation
        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> {
            try {
                LocalDate today = LocalDate.now();
                DailyLog log = tracker.getDailyLog(today);

                // Parse strings and assign numeric inputs
                log.sleepHours = Integer.parseInt(sleepField.getText());
                log.wakeUpTime = Integer.parseInt(wakeField.getText());
                log.screenTime = Integer.parseInt(screenField.getText());
                log.energyLevel = Integer.parseInt(energyField.getText());
                log.mood = Integer.parseInt(moodField.getText());
                log.motivation = Integer.parseInt(motivationField.getText());

                tracker.validateDailyLog(log);
                tracker.addDailyLog(today, log);
                tracker.saveDailyLogToCSV();
                updateLogArea();
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter valid numeric values.");
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage());
            }
        });

        dialog.add(new JLabel()); dialog.add(submitButton);
        dialog.setVisible(true);
    }

    /**
     * Updates the text area with today's habit log.
     */
    private void updateLogArea() {
        logArea.setText("Daily Log:\n");
        LocalDate today = LocalDate.now();
        for (Habit habit : tracker.getDailyLog(today).getHabits()) {
            logArea.append(habit.getName() + ": " + habit.getDuration() + " minutes\n");
        }
    }

    /**
     * Displays the top 3 days (by total habit duration) in the text area,
     * along with sleep, alcohol, socialization, and habit streak info with fun emojis.
     */
    private void displayTopHabitDays() {
        tracker.readSerFile(); // Load logs
        List<LocalDate> topDays = tracker.getTop3HabitDays();

        StringBuilder sb = new StringBuilder("Top 3 Habit Days (by total duration):\n\n");

        for (LocalDate date : topDays) {
            DailyLog log = tracker.getDailyLog(date);
            int streak = tracker.calculateHabitStreak(date);

            sb.append("📅 Date: ").append(date).append("\n")
              .append("😴 Sleep Hours: ").append(log.sleepHours).append("\n")
              .append("🍷 Alcohol Consumed: ").append(log.alcoholConsumed ? "Yes" : "No").append("\n")
              .append("🗣️ Socialized: ").append(log.socialized ? "Yes" : "No").append("\n")
              .append("📈 Habit Streak: ").append(streak).append(" day(s)\n")
              .append("✅ Habits:\n");

            for (Habit habit : log.getHabits()) {
                sb.append("   - ").append(habit.getName())
                  .append(" (").append(habit.getDuration()).append(" mins)\n");
            }
            sb.append("\n");
        }

        logArea.setText(sb.toString());
    }

    /**
     * Main method — launches the GUI
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(HabitTrackerGUI::new);
    }
}
