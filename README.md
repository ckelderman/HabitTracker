# Habit Tracker

A local Java-based habit tracking application with a graphical interface, designed to help users track daily habits and contextual lifestyle metrics in order to identify routine patterns and improve consistency.

## Features

- Add daily habits with specific start times and durations
- Log daily lifestyle metrics: sleep, screen time, energy, motivation, mood, alcohol, and socialization
- Automatically calculate:
  - Top 3 habit days (by total duration)
  - Habit streaks
- Data persistence using both `.csv` (for readability) and `.ser` files (for fast reloading)
- Java Swing-based GUI
- Includes mock data generator for easy testing

## How to Run

1. Clone this repository:
   ```bash
   git clone git@github.com:ckelderman/HabitTracker.git
   cd HabitTracker
