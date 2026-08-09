# Student Org System

A Java desktop application for managing a student organization — tracking members, attendance, events, and finances.

## Features

- **Member Management** — add, update, and track student org members
- **Attendance Tracking** — record and review attendance for meetings/events
- **Event Management** — create and manage organization events
- **Finance Tracking** — log dues, expenses, and organization funds

## Tech Stack

- Java
- NetBeans (project structure)
- JSON for data storage

## Project Structure

```
student-org-system/
├── src/                  # Java source files
├── build/                # Compiled output (ignored in git)
├── dist/                 # Distribution build (ignored in git)
├── attendance.json        # Attendance records
├── members.json            # Member records
├── events.json              # Event records
├── finances.json             # Financial records
└── nbproject/                 # NetBeans project config
```

## Getting Started

### Prerequisites

- Java JDK 8+
- NetBeans IDE (recommended) or any Java-compatible IDE

### Running the Project

1. Clone the repo:
   ```bash
   git clone https://github.com/<your-username>/student-org-system.git
   ```
2. Open the project in NetBeans (`File > Open Project`)
3. Build and run from the IDE, or via command line:
   ```bash
   ant build
   ant run
   ```

## Notes

Data files (`members.json`, `attendance.json`, `events.json`, `finances.json`) currently contain sample/test data.

## License

Add a license of your choice (e.g. MIT) if you plan to open this up for contributions.
