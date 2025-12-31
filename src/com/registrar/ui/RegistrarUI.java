
package com.registrar.ui;

import com.registrar.model.TimeSlot;
import com.registrar.service.RegistrarService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Minimal Swing UI to interact with RegistrarService.
 * Tabs: Students, Courses, Enrollments.
 */
public class RegistrarUI extends JFrame {
    private final RegistrarService service;
    private final JTextArea outputArea = new JTextArea(12, 50);

    public RegistrarUI(RegistrarService service) {
        super("Campus Registrar");
        this.service = service;
        initUI();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("Students", buildStudentsPanel());
        tabs.addTab("Courses", buildCoursesPanel());
        tabs.addTab("Enrollments", buildEnrollmentPanel());

        outputArea.setEditable(false);
        outputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.add(new JScrollPane(outputArea), BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tabs, outputPanel);
        split.setResizeWeight(0.6);

        setLayout(new BorderLayout());
        add(split, BorderLayout.CENTER);
    }

    private JPanel buildStudentsPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = baseGbc();

        JTextField idField = new JTextField(10);
        JTextField nameField = new JTextField(15);
        JButton addBtn = new JButton("Add Student");
        JButton listBtn = new JButton("List Students");

        gbc.gridx = 0; gbc.gridy = 0; p.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1; p.add(idField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; p.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; p.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; p.add(addBtn, gbc);
        gbc.gridx = 1; p.add(listBtn, gbc);

        addBtn.addActionListener((ActionEvent e) -> {
            boolean ok = service.addStudent(idField.getText().trim(), nameField.getText().trim());
            appendOutput(ok ? "Student added.\n" : "Student already exists or invalid.\n");
        });

        listBtn.addActionListener((ActionEvent e) -> {
            StringBuilder sb = new StringBuilder("Students:\n");
            service.listStudents().forEach(s -> sb.append(" - ").append(s).append("\n"));
            appendOutput(sb.toString());
        });

        return p;
    }

    private JPanel buildCoursesPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = baseGbc();

        JTextField codeField = new JTextField(8);
        JTextField titleField = new JTextField(15);
        JSpinner capSpinner = new JSpinner(new SpinnerNumberModel(2, 1, 500, 1));

        JComboBox<TimeSlot.Day> dayCombo = new JComboBox<>(TimeSlot.Day.values());
        JSpinner startHour = new JSpinner(new SpinnerNumberModel(9, 0, 23, 1));
        JSpinner startMin = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));
        JSpinner endHour = new JSpinner(new SpinnerNumberModel(10, 0, 23, 1));
        JSpinner endMin = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));

        List<TimeSlot> tmpTimes = new ArrayList<>();
        JButton addTimeBtn = new JButton("Add Meeting Time");
        JButton addCourseBtn = new JButton("Add Course");
        JButton listCoursesBtn = new JButton("List Courses");

        gbc.gridx = 0; gbc.gridy = 0; p.add(new JLabel("Code:"), gbc);
        gbc.gridx = 1; p.add(codeField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; p.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1; p.add(titleField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; p.add(new JLabel("Capacity:"), gbc);
        gbc.gridx = 1; p.add(capSpinner, gbc);

        gbc.gridx = 0; gbc.gridy = 3; p.add(new JLabel("Day:"), gbc);
        gbc.gridx = 1; p.add(dayCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 4; p.add(new JLabel("Start (H:M):"), gbc);
        JPanel startPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        startPanel.add(startHour); startPanel.add(new JLabel(":")); startPanel.add(startMin);
        gbc.gridx = 1; p.add(startPanel, gbc);

        gbc.gridx = 0; gbc.gridy = 5; p.add(new JLabel("End (H:M):"), gbc);
        JPanel endPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        endPanel.add(endHour); endPanel.add(new JLabel(":")); endPanel.add(endMin);
        gbc.gridx = 1; p.add(endPanel, gbc);

        gbc.gridx = 0; gbc.gridy = 6; p.add(addTimeBtn, gbc);
        gbc.gridx = 1; p.add(addCourseBtn, gbc);

        gbc.gridx = 1; gbc.gridy = 7; p.add(listCoursesBtn, gbc);

        addTimeBtn.addActionListener((ActionEvent e) -> {
            int sH = (Integer) startHour.getValue();
            int sM = (Integer) startMin.getValue();
            int eH = (Integer) endHour.getValue();
            int eM = (Integer) endMin.getValue();
            int s = sH * 60 + sM;
            int end = eH * 60 + eM;
            try {
                TimeSlot t = new TimeSlot((TimeSlot.Day) dayCombo.getSelectedItem(), s, end);
                tmpTimes.add(t);
                appendOutput("Added meeting time: " + t + "\n");
            } catch (IllegalArgumentException ex) {
                appendOutput("Invalid time slot: " + ex.getMessage() + "\n");
            }
        });

        addCourseBtn.addActionListener((ActionEvent e) -> {
            boolean ok = service.addCourse(
                codeField.getText().trim(),
                titleField.getText().trim(),
                (Integer) capSpinner.getValue(),
                tmpTimes
            );
            appendOutput(ok ? "Course added.\n" : "Course exists or invalid.\n");
            tmpTimes.clear();
        });

        listCoursesBtn.addActionListener((ActionEvent e) -> {
            StringBuilder sb = new StringBuilder("Courses:\n");
            service.listCourses().forEach(c -> sb.append(" - ").append(c).append("\n"));
            appendOutput(sb.toString());
        });

        return p;
    }

    private JPanel buildEnrollmentPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = baseGbc();

        JTextField studentIdField = new JTextField(10);
        JTextField courseCodeField = new JTextField(8);
        JSpinner prioritySpinner = new JSpinner(new SpinnerNumberModel(1, 0, 10, 1));

        JButton enrollBtn = new JButton("Enroll");
        JButton dropBtn = new JButton("Drop");
        JButton rosterBtn = new JButton("Show Roster");

        gbc.gridx = 0; gbc.gridy = 0; p.add(new JLabel("Student ID:"), gbc);
        gbc.gridx = 1; p.add(studentIdField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; p.add(new JLabel("Course Code:"), gbc);
        gbc.gridx = 1; p.add(courseCodeField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; p.add(new JLabel("Priority (0=high):"), gbc);
        gbc.gridx = 1; p.add(prioritySpinner, gbc);

        gbc.gridx = 0; gbc.gridy = 3; p.add(enrollBtn, gbc);
        gbc.gridx = 1; p.add(dropBtn, gbc);
        gbc.gridx = 1; gbc.gridy = 4; p.add(rosterBtn, gbc);

        enrollBtn.addActionListener((ActionEvent e) -> {
            String sid = studentIdField.getText().trim();
            String code = courseCodeField.getText().trim();
            int priority = (Integer) prioritySpinner.getValue();
            RegistrarService.EnrollResult result = service.enroll(sid, code, priority);
            appendOutput("Enroll result: " + result + "\n");
        });

        dropBtn.addActionListener((ActionEvent e) -> {
            String sid = studentIdField.getText().trim();
            String code = courseCodeField.getText().trim();
            boolean ok = service.drop(sid, code);
            appendOutput(ok ? "Dropped.\n" : "Drop failed.\n");
        });

        rosterBtn.addActionListener((ActionEvent e) -> {
            String code = courseCodeField.getText().trim();
            appendOutput(service.getRoster(code) + "\n");
        });

        return p;
    }

    private GridBagConstraints baseGbc() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.anchor = GridBagConstraints.WEST;
        return gbc;
    }

    private void appendOutput(String text) {
        outputArea.append(text);
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }
}
