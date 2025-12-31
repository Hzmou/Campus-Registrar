
package com.registrar.model;

import java.util.*;

/**
 * Course with capacity-limited enrollment and a PriorityQueue waitlist.
 */
public class Course {
    private final String code;   // e.g., "CS101"
    private String title;
    private int capacity;

    private final Set<TimeSlot> meetingTimes = new HashSet<>();
    private final Set<String> enrolledStudentIds = new HashSet<>();
    private final PriorityQueue<WaitlistEntry> waitlist = new PriorityQueue<>();

    public Course(String code, String title, int capacity) {
        this.code = Objects.requireNonNull(code);
        this.title = Objects.requireNonNull(title);
        if (capacity <= 0) throw new IllegalArgumentException("capacity must be > 0");
        this.capacity = capacity;
    }

    public String getCode() { return code; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = Objects.requireNonNull(title); }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) {
    	this.capacity = capacity; 
    	}

    public Set<TimeSlot> getMeetingTimes() { return Collections.unmodifiableSet(meetingTimes); }
    public void addMeetingTime(TimeSlot t) { meetingTimes.add(t); }

    public Set<String> getEnrolledStudentIds() { return Collections.unmodifiableSet(enrolledStudentIds); }
    public int seatsRemaining() { return Math.max(0, capacity - enrolledStudentIds.size()); }

    public boolean isEnrolled(String studentId) { return enrolledStudentIds.contains(studentId); }

    /** Attempt to enroll; caller should have checked conflicts. */
    public boolean enroll(String studentId) {
        if (enrolledStudentIds.size() < capacity) {
            return enrolledStudentIds.add(studentId);
        }
        return false;
    }

    public boolean drop(String studentId) {
        return enrolledStudentIds.remove(studentId);
    }

    public void addToWaitlist(WaitlistEntry entry) {
        waitlist.add(entry);
    }

    /** Pops next candidate from waitlist. */
    public WaitlistEntry pollWaitlist() {
        return waitlist.poll();
    }

    public int waitlistSize() {
        return waitlist.size();
    }

    @Override
    public String toString() {
        return code + " - " + title + " (cap " + capacity + ", enrolled " + enrolledStudentIds.size() + ")";
    }
}
