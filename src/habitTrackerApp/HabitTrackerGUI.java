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

    public HabitTrackerGUI() {
        frame = new JFrame("Habit Tracker");
        frame.setSize(500, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(13, 2));

        // Drop-downs
        habitDropdown = new JComboBox<>(HABITS);
        hourDropdown = new JComboBox<>(HOURS);
        minuteDropdown = new JComboBox<>(MINUTES);
        durationDropdown = new JComboBox<>(DURATIONS);
        hourDropdown.setSelectedItem("8");
        durationDropdown.setSelectedItem("45");

        // Buttons
        JButton addButton = new JButton("Add Habit");
        JButton dayCompleteButton = new JButton("Log Completed Day");
        JButton readSerButton = new JButton("Read ser Data:");
        JButton topDaysButton = new JButton("Top 3 Habit Days");

        // Log display
        logArea = new JTextArea();

        // Layout
        panel.add(new JLabel("Select Habit:")); panel.add(habitDropdown);
        panel.add(new JLabel("Start Time (Hour):")); panel.add(hourDropdown);
        panel.add(new JLabel("Start Time (Minute):")); panel.add(minuteDropdown);
        panel.add(new JLabel("Duration (Minutes):")); panel.add(durationDropdown);
        panel.add(new JLabel()); panel.add(addButton);
        panel.add(dayCompleteButton);
        // panel.add(readSerButton); // optional for testing
        panel.add(topDaysButton);

        frame.add(panel, BorderLayout.NORTH);
        frame.add(new JScrollPane(logArea), BorderLayout.CENTER);

        // Actions
        addButton.addActionListener(e -> addHabit());
        dayCompleteButton.addActionListener(e -> openDayCompleteWindow());
        readSerButton.addActionListener(e -> tracker.readSerFile());
        topDaysButton.addActionListener(e -> displayTopHabitDays());

        frame.setVisible(true);
    }

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

    private void openDayCompleteWindow() {
        String dateID = LocalDate.now().toString().replace("-", "");
        if (tracker.isDateIdPresent(dateID)) {
            JOptionPane.showMessageDialog(frame, "Daily attributes for today are already saved.");
            return;
        }

        JDialog dialog = new JDialog(frame, "Complete Your Day", true);
        dialog.setSize(400, 400);
        dialog.setLayout(new GridLayout(9, 2));

        // Inputs
        JTextField sleepField = new JTextField();
        JTextField wakeField = new JTextField();
        JTextField screenField = new JTextField();
        JTextField energyField = new JTextField();
        JTextField moodField = new JTextField();
        JTextField motivationField = new JTextField();
        JCheckBox alcoholCheck = new JCheckBox("Alcohol Consumed");
        JCheckBox socialCheck = new JCheckBox("Socialized");

        dialog.add(new JLabel("Hours Slept:")); dialog.add(sleepField);
        dialog.add(new JLabel("Wake-up Time Hour:")); dialog.add(wakeField);
        dialog.add(new JLabel("Screen Time Hours:")); dialog.add(screenField);
        dialog.add(new JLabel("Energy Level (0-10):")); dialog.add(energyField);
        dialog.add(new JLabel("Mood Level (0-10):")); dialog.add(moodField);
        dialog.add(new JLabel("Motivation Level (0-10):")); dialog.add(motivationField);
        dialog.add(new JLabel("Alcohol Consumed:")); dialog.add(alcoholCheck);
        dialog.add(new JLabel("Socialized:")); dialog.add(socialCheck);

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> {
            try {
                LocalDate today = LocalDate.now();
                DailyLog log = tracker.getDailyLog(today);

                log.sleepHours = Integer.parseInt(sleepField.getText());
                log.wakeUpTime = Integer.parseInt(wakeField.getText());
                log.screenTime = Integer.parseInt(screenField.getText());
                log.energyLevel = Integer.parseInt(energyField.getText());
                log.mood = Integer.parseInt(moodField.getText());
                log.motivation = Integer.parseInt(motivationField.getText());
                log.alcoholConsumed = alcoholCheck.isSelected();
                log.socialized = socialCheck.isSelected();

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

    private void updateLogArea() {
        logArea.setText("Daily Log:\n");
        LocalDate today = LocalDate.now();
        DailyLog log = tracker.getDailyLog(today);
        HabitSorter.sortHabitsByStartTime(log.getHabits());

        for (Habit habit : log.getHabits()) {
            logArea.append(habit.getName() + ": " + habit.getDuration() + " minutes\n");
        }
    }


    private void displayTopHabitDays() {
        tracker.readSerFile();
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(HabitTrackerGUI::new);
    }
}
