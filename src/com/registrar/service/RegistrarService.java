
package com.registrar.service;

import com.registrar.model.*;

import java.util.*;

/**
 * Core service layer that uses HashMaps to store students & courses.
 * Provides APIs for add, enroll, drop, and waitlist processing.
 */
public class RegistrarService {
    private final Map<String, Student> students = new HashMap<>();
    private final Map<String, Course> courses = new HashMap<>();

    // --- Student APIs ---
    public boolean addStudent(String id, String name) {
        if (students.containsKey(id)) return false;
        students.put(id, new Student(id, name));
        return true;
    }

    public Student getStudent(String id) { return students.get(id); }

    // --- Course APIs ---
    public boolean addCourse(String code, String title, int capacity, Collection<TimeSlot> meetingTimes) {
        if (courses.containsKey(code)) return false;
        Course c = new Course(code, title, capacity);
        if (meetingTimes != null) {
            for (TimeSlot t : meetingTimes) c.addMeetingTime(t);
        }
        courses.put(code, c);
        return true;
    }

    public Course getCourse(String code) { return courses.get(code); }

    public Collection<Student> listStudents() { return Collections.unmodifiableCollection(students.values()); }
    public Collection<Course> listCourses() { return Collections.unmodifiableCollection(courses.values()); }

    // --- Enrollment ---
    public enum EnrollResult {
        ENROLLED, ADDED_TO_WAITLIST, CONFLICT, ALREADY_ENROLLED, NO_SUCH_STUDENT, NO_SUCH_COURSE
    }

    /**
     * Tries to enroll student into course. If seat unavailable, adds to waitlist.
     * Uses PriorityQueue in Course. Checks Student schedule conflicts.
     */
    public EnrollResult enroll(String studentId, String courseCode, int priority) {
        Student s = students.get(studentId);
        if (s == null) return EnrollResult.NO_SUCH_STUDENT;
        Course c = courses.get(courseCode);
        if (c == null) return EnrollResult.NO_SUCH_COURSE;
        if (c.isEnrolled(studentId)) return EnrollResult.ALREADY_ENROLLED;

        // Conflict check
        if (s.hasConflict(c.getMeetingTimes())) return EnrollResult.CONFLICT;

        // Try to enroll
        if (c.seatsRemaining() > 0) {
            if (c.enroll(studentId)) {
                s.addCourse(courseCode);
                s.addToSchedule(c.getMeetingTimes());
                return EnrollResult.ENROLLED;
            }
        }

        // Add to waitlist with priority & timestamp
        c.addToWaitlist(new WaitlistEntry(studentId, priority, System.currentTimeMillis()));
        return EnrollResult.ADDED_TO_WAITLIST;
    }

    /** Drops a student from a course and attempts to auto-fill from waitlist. */
    public boolean drop(String studentId, String courseCode) {
        Student s = students.get(studentId);
        Course c = courses.get(courseCode);
        if (s == null || c == null) return false;

        if (!c.drop(studentId)) return false;

        s.removeCourse(courseCode);
        s.removeFromSchedule(c.getMeetingTimes());

        // Try to seat next from waitlist
        while (c.seatsRemaining() > 0 && c.waitlistSize() > 0) {
            WaitlistEntry next = c.pollWaitlist();
            if (next == null) break;

            Student candidate = students.get(next.getStudentId());
            if (candidate == null) continue; // stale entry

            // Skip if candidate has conflict now
            if (candidate.hasConflict(c.getMeetingTimes())) {
                // Optionally: notify candidate; for now, ignore
                continue;
            }

            // Enroll candidate
            if (c.enroll(candidate.getId())) {
                candidate.addCourse(courseCode);
                candidate.addToSchedule(c.getMeetingTimes());
            }
        }

        return true;
    }

    /** Utility: Returns a simple roster string for UI display. */
    public String getRoster(String courseCode) {
        Course c = courses.get(courseCode);
        if (c == null) return "Course not found.";
        StringBuilder sb = new StringBuilder();
        sb.append(c).append("\n");
        sb.append("Meeting Times: ").append(c.getMeetingTimes()).append("\n");
        sb.append("Enrolled:\n");
        for (String sid : c.getEnrolledStudentIds()) {
            Student s = students.get(sid);
            sb.append(" - ").append(s != null ? s : sid).append("\n");
        }
        sb.append("Waitlist size: ").append(c.waitlistSize()).append("\n");
        return sb.toString();
    }
}
