
package com.registrar.model;

import java.util.*;

/**
 * Student with a schedule tracked via a TreeSet of TimeSlot for conflict detection.
 */
public class Student {
    private final String id;
    private String name;

    // Course codes the student is currently enrolled in
    private final Set<String> enrolledCourses = new HashSet<>();

    // Schedule blocks from enrolled courses, for conflict detection
    private final TreeSet<TimeSlot> schedule = new TreeSet<>();

    public Student(String id, String name) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = Objects.requireNonNull(name); }

    public Set<String> getEnrolledCourses() { return Collections.unmodifiableSet(enrolledCourses); }
    public NavigableSet<TimeSlot> getSchedule() { return Collections.unmodifiableNavigableSet(schedule); }

    /** Checks if any meeting time conflicts with the student's existing schedule. */
    public boolean hasConflict(Collection<TimeSlot> meetingTimes) {
        for (TimeSlot t : meetingTimes) {
            // TreeSet allows efficient navigation, but we’ll do simple iteration here
            for (TimeSlot existing : schedule) {
                if (existing.conflictsWith(t)) return true;
            }
        }
        return false;
    }

    /** Adds time slots to the schedule when enrolled. */
    public void addToSchedule(Collection<TimeSlot> meetingTimes) {
        schedule.addAll(meetingTimes);
    }

    /** Removes time slots from the schedule when dropped. */
    public void removeFromSchedule(Collection<TimeSlot> meetingTimes) {
        schedule.removeAll(meetingTimes);
    }

    public void addCourse(String courseCode) { enrolledCourses.add(courseCode); }
    public void removeCourse(String courseCode) { enrolledCourses.remove(courseCode); }

    @Override
    public String toString() {
        return id + " - " + name;
    }
}
