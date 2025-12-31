
package com.registrar.model;

/**
 * Entry for the course waitlist. Lower priority value = higher priority.
 * If priority ties, earlier requestTime gets precedence.
 */
public class WaitlistEntry implements Comparable<WaitlistEntry> {
    private final String studentId;
    private final int priority;      // e.g., 0 = highest; 1 = normal; 2 = low
    private final long requestTimeMs; // System.currentTimeMillis() at request

    public WaitlistEntry(String studentId, int priority, long requestTimeMs) {
        this.studentId = studentId;
        this.priority = priority;
        this.requestTimeMs = requestTimeMs;
    }

    public String getStudentId() { return studentId; }
    public int getPriority() { return priority; }
    public long getRequestTimeMs() { return requestTimeMs; }

    @Override
    public int compareTo(WaitlistEntry o) {
        int c = Integer.compare(this.priority, o.priority);
        if (c != 0) return c;
        return Long.compare(this.requestTimeMs, o.requestTimeMs);
    }

    @Override
    public String toString() {
        return "WaitlistEntry{studentId='" + studentId + "', priority=" + priority + ", time=" + requestTimeMs + "}";
    }
}
