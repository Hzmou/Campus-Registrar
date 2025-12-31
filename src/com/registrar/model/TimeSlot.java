
package com.registrar.model;

import java.util.Objects;

/**
 * Represents a single meeting time for a course, e.g., Mon 09:00–10:15.
 * Comparable so we can store in a TreeSet to detect conflicts.
 */
public class TimeSlot implements Comparable<TimeSlot> {
    public enum Day { MON, TUE, WED, THU, FRI, SAT, SUN }

    private final Day day;
    // Minutes from midnight (e.g., 9:30 AM = 9*60 + 30 = 570)
    private final int startMin;
    private final int endMin;

    public TimeSlot(Day day, int startMin, int endMin) {
        if (endMin <= startMin) throw new IllegalArgumentException("endMin must be > startMin");
        this.day = Objects.requireNonNull(day);
        this.startMin = startMin;
        this.endMin = endMin;
    }

    public Day getDay() { return day; }
    public int getStartMin() { return startMin; }
    public int getEndMin() { return endMin; }

    /** Returns true if this timeslot overlaps with another on the same day. */
    public boolean conflictsWith(TimeSlot other) {
        if (this.day != other.day) return false;
        // Overlap if start < other.end && other.start < end
        return this.startMin < other.endMin && other.startMin < this.endMin;
    }

    @Override
    public int compareTo(TimeSlot o) {
        int c = this.day.ordinal() - o.day.ordinal();
        if (c != 0) return c;
        c = Integer.compare(this.startMin, o.startMin);
        if (c != 0) return c;
        return Integer.compare(this.endMin, o.endMin);
    }

    @Override
    public String toString() {
        return day + " " + fmt(startMin) + "-" + fmt(endMin);
    }

    private static String fmt(int minutes) {
        int h = minutes / 60;
        int m = minutes % 60;
  
        return String.format("%02d:%02d", h, m);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TimeSlot)) return false;
        TimeSlot that = (TimeSlot) o;
        return startMin == that.startMin && endMin == that.endMin && day == that.day;
    }

    @Override
    public int hashCode() {
        return Objects.hash(day, startMin, endMin);
    }
}
