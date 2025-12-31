
package com.registrar;

import com.registrar.model.TimeSlot;
import com.registrar.service.RegistrarService;
import com.registrar.ui.RegistrarUI;

import javax.swing.*;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        // Create service and seed some sample data
        RegistrarService service = new RegistrarService();

        service.addStudent("S001", "Alice");
        service.addStudent("S002", "Bob");
        service.addStudent("S003", "Hamza"); // 👋

        service.addCourse(
            "CS101", "Intro to CS", 2,
            Arrays.asList(
                new TimeSlot(TimeSlot.Day.MON, 9*60, 10*60+15),
                new TimeSlot(TimeSlot.Day.WED, 9*60, 10*60+15)
            )
        );

        service.addCourse(
            "MATH201", "Discrete Math", 1,
            Arrays.asList(
                new TimeSlot(TimeSlot.Day.TUE, 11*60, 12*60+15),
                new TimeSlot(TimeSlot.Day.THU, 11*60, 12*60+15)
            )
        );

        // Start Swing UI on EDT
        SwingUtilities.invokeLater(() -> {
            RegistrarUI ui = new RegistrarUI(service);
            ui.setVisible(true);
        });
    }
}
