
# Campus Registrar Project

A medium-sized, intermediate Java project implementing a campus course registration system with enrollment, schedule conflict detection, and waitlist management.

## Overview

**Goal:** Build a desktop application where users can:
- Add students and courses.
- Enroll students in capacity-limited courses.
- Automatically place students on a prioritized waitlist if the course is full.
- Detect and prevent schedule conflicts.
- View course rosters and waitlist sizes.

**Core data structures:**
- `HashMap<String, Student>` and `HashMap<String, Course>` for fast lookups of students and courses.
- `PriorityQueue<WaitlistEntry>` inside `Course` to order the waitlist by priority and request time.
- `TreeSet<TimeSlot>` inside `Student` to store time slots and detect schedule conflicts efficiently.

## Project Structure

```
src/
  com/registrar/
    Main.java
    model/
      Student.java
      Course.java
      TimeSlot.java
      WaitlistEntry.java
    service/
      RegistrarService.java
    ui/
      RegistrarUI.java
```

## Build & Run

### Prerequisites
- Java 8+ (tested on Java 11).

### Compile
```bash
javac -d out $(find src -name "*.java")
```

### Run
```bash
java -cp out com.registrar.Main
```

## Functional Requirements

1. **Student Management**
   - Add a student (unique `id`, `name`).
   - List all students.

2. **Course Management**
   - Add a course with unique `code`, `title`, capacity, and one or more meeting `TimeSlot`s.
   - List all courses.

3. **Enrollment**
   - Enroll a student into a course **if**:
     - Student and course exist.
     - Student is not already enrolled in the course.
     - No time conflict with student’s current schedule.
     - Seats are available.
   - If the course is full, add the student to the **waitlist** with a `priority` (lower number = higher priority). Ties resolved by earlier request time.

4. **Dropping / Auto-fill from Waitlist**
   - Dropping a student from a course frees a seat.
   - The system automatically **polls the waitlist** and enrolls the next eligible student(s) who do not have a schedule conflict.

5. **Rosters & Reporting**
   - Show a course’s roster, meeting times, enrollment count, and waitlist size.

## Non-Functional Requirements

- **Usability**: Provide a minimal Swing GUI in `RegistrarUI` for common operations.
- **Modularity**: Separate model (`com.registrar.model`), service (`com.registrar.service`), and UI (`com.registrar.ui`).
- **Extensibility**: Service methods are designed for easy addition of persistence (e.g., JSON/CSV) and validation.

## Class-by-Class Summary

### `TimeSlot` (model)
Represents a single meeting block for a course (e.g., Monday 09:00–10:15).
- **Fields**:
  - `Day day`: Enum (`MON`..`SUN`).
  - `int startMin`, `int endMin`: Minutes from midnight.
- **Key Methods**:
  - `boolean conflictsWith(TimeSlot other)`: Returns `true` if overlapping on the same day.
  - `compareTo(TimeSlot)`: Orders by day, then start, then end; enables use in `TreeSet`.
- **Usage**: Used by both `Course` (meeting times) and `Student` (schedule) to detect conflicts.

### `WaitlistEntry` (model)
Represents one entry on a course’s waitlist.
- **Fields**:
  - `String studentId`
  - `int priority` (0 = highest)
  - `long requestTimeMs` (timestamp when added)
- **Key Methods**:
  - `compareTo(WaitlistEntry)`: Orders by priority, then request time.
- **Usage**: Stored inside `PriorityQueue` in `Course`.

### `Student` (model)
Represents a student with a schedule and enrolled courses.
- **Fields**:
  - `String id`, `String name`
  - `Set<String> enrolledCourses`: Course codes the student is enrolled in.
  - `TreeSet<TimeSlot> schedule`: Aggregated time slots from enrolled courses.
- **Key Methods**:
  - `boolean hasConflict(Collection<TimeSlot> meetingTimes)`: Checks for overlap against `schedule`.
  - `addToSchedule(Collection<TimeSlot>)`, `removeFromSchedule(Collection<TimeSlot>)`
  - `addCourse(String)`, `removeCourse(String)`
- **Usage**: Conflict checking during enrollment and schedule maintenance on drop.

### `Course` (model)
Represents a course with capacity and a waitlist.
- **Fields**:
  - `String code`, `String title`, `int capacity`
  - `Set<TimeSlot> meetingTimes`
  - `Set<String> enrolledStudentIds`
  - `PriorityQueue<WaitlistEntry> waitlist`
- **Key Methods**:
  - `boolean enroll(String studentId)`: Enroll if capacity permits.
  - `boolean drop(String studentId)`: Remove student from roster.
  - `void addToWaitlist(WaitlistEntry entry)`, `WaitlistEntry pollWaitlist()`
  - `int seatsRemaining()`, `boolean isEnrolled(String)`
- **Usage**: Central repository for course occupancy and waitlist management.

### `RegistrarService` (service)
Service layer orchestrating students, courses, and enrollment flows.
- **Fields**:
  - `Map<String, Student> students`
  - `Map<String, Course> courses`
- **Key Methods**:
  - **Student APIs**: `addStudent`, `getStudent`, `listStudents`
  - **Course APIs**: `addCourse`, `getCourse`, `listCourses`
  - **Enrollment**: `EnrollResult enroll(String studentId, String courseCode, int priority)`
    - Checks existence, duplicate enrollment, schedule conflicts.
    - Enrolls if seats available; otherwise places on waitlist.
  - **Dropping**: `boolean drop(String studentId, String courseCode)`
    - Removes student and auto-fills from waitlist.
  - **Reporting**: `String getRoster(String courseCode)`
- **Usage**: Business logic consumed by the Swing UI and potential CLI/tests.

### `RegistrarUI` (ui)
Swing GUI with tabs for Students, Courses, and Enrollments.
- **Students Tab**:
  - Add student (ID, Name). List students.
- **Courses Tab**:
  - Add course (Code, Title, Capacity). Add multiple meeting times via day/hour/minute spinners. List courses.
- **Enrollments Tab**:
  - Enroll student (ID, Course Code, Priority). Drop student. Show course roster.
- **Output Area**: Log of operations and results.

### `Main` (driver)
Application entry point.
- Seeds example students and courses.
- Launches `RegistrarUI` on the Event Dispatch Thread (EDT).

## User Flows

1. **Add students** via the Students tab.
2. **Create courses** and add meeting times via the Courses tab.
3. **Enroll** students via the Enrollments tab:
   - If seats available and no conflicts → enrolled.
   - If full → added to waitlist (priority controls ordering).
4. **Drop** enrolled students to free seats; system auto-enrolls next from waitlist.
5. **View rosters** at any time.

## Extension Ideas (Optional)
- **Persistence**: Save/load students & courses (JSON/CSV). Add `RegistrarPersistence`.
- **Notifications**: Observer pattern to notify when a student moves from waitlist to enrolled.
- **Advanced conflict detection**: Efficient nearest-neighbor checks using `TreeSet.ceiling`/`floor`.
- **Validation**: Stronger input validation in UI, duplicate meeting time prevention.
- **Search & filters**: Find courses by day/time or title keywords.
- **Unit tests**: With JUnit for `enroll`, `drop`, `hasConflict`.

## Coding Tasks for You
- Implement any remaining TODOs in the provided scaffolds (business rules, validations).
- Add persistence if desired.
- Improve UI (icons, tooltips, dialogs, error feedback).

## Notes
- Time representation uses minutes-from-midnight for simplicity.
- Waitlist priority: lower integer = higher priority; ties broken by earlier `requestTimeMs`.
- Conflicts are checked against the student’s aggregated schedule.
