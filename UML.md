
# Campus Registrar – UML Diagram

```mermaid
classDiagram
    direction LR

    class Main {
        +main(String[] args)
    }

    class RegistrarUI {
        -RegistrarService service
        -JTextArea outputArea
        +RegistrarUI(RegistrarService)
        -initUI()
        -buildStudentsPanel() JPanel
        -buildCoursesPanel() JPanel
        -buildEnrollmentPanel() JPanel
    }

    class RegistrarService {
        -Map~String, Student~ students
        -Map~String, Course~ courses
        +boolean addStudent(String id, String name)
        +Student getStudent(String id)
        +Collection~Student~ listStudents()
        +boolean addCourse(String code, String title, int capacity, Collection~TimeSlot~ meetingTimes)
        +Course getCourse(String code)
        +Collection~Course~ listCourses()
        +EnrollResult enroll(String studentId, String courseCode, int priority)
        +boolean drop(String studentId, String courseCode)
        +String getRoster(String courseCode)
        enum EnrollResult
    }

    class Student {
        -String id
        -String name
        -Set~String~ enrolledCourses
        -TreeSet~TimeSlot~ schedule
        +String getId()
        +String getName()
        +void setName(String name)
        +Set~String~ getEnrolledCourses()
        +NavigableSet~TimeSlot~ getSchedule()
        +boolean hasConflict(Collection~TimeSlot~)
        +void addToSchedule(Collection~TimeSlot~)
        +void removeFromSchedule(Collection~TimeSlot~)
        +void addCourse(String)
        +void removeCourse(String)
    }

    class Course {
        -String code
        -String title
        -int capacity
        -Set~TimeSlot~ meetingTimes
        -Set~String~ enrolledStudentIds
        -PriorityQueue~WaitlistEntry~ waitlist
        +String getCode()
        +String getTitle()
        +void setTitle(String)
        +int getCapacity()
        +void setCapacity(int)
        +Set~TimeSlot~ getMeetingTimes()
        +void addMeetingTime(TimeSlot)
        +Set~String~ getEnrolledStudentIds()
        +int seatsRemaining()
        +boolean isEnrolled(String)
        +boolean enroll(String)
        +boolean drop(String)
        +void addToWaitlist(WaitlistEntry)
        +WaitlistEntry pollWaitlist()
        +int waitlistSize()
    }

    class TimeSlot {
        +enum Day { MON, TUE, WED, THU, FRI, SAT, SUN }
        -Day day
        -int startMin
        -int endMin
        +boolean conflictsWith(TimeSlot)
        +int compareTo(TimeSlot)
    }

    class WaitlistEntry {
        -String studentId
        -int priority
        -long requestTimeMs
        +String getStudentId()
        +int getPriority()
        +long getRequestTimeMs()
        +int compareTo(WaitlistEntry)
    }

    Main --> RegistrarUI : launches
    RegistrarUI ..> RegistrarService : uses
    RegistrarService o--> Student : manages
    RegistrarService o--> Course : manages
    Course *--> TimeSlot : contains
    Course o--> WaitlistEntry : waitlist entries
    Student *--> TimeSlot : schedule
```

## Diagram Notes
- **Composition (`*-->`)** indicates strong ownership: `Course` holds its `TimeSlot`s; `Student` maintains a schedule of `TimeSlot`s.
- **Aggregation (`o-->`)** indicates collections without strict lifecycle ownership: `RegistrarService` aggregates `Student` and `Course`.
- **Dependency (`..>`)** indicates usage: the UI depends on the service.
- The `EnrollResult` enum is defined inside `RegistrarService` to represent outcomes of enrollment attempts.
