/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package mainGUIs;

import database.DBConnection;
import implementations.*;
import java.awt.Color;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import models.*;
import util.HashUtil;
import java.sql.*;
import java.util.ArrayList;
import javax.swing.JComboBox;
import javax.swing.RowFilter;
import javax.swing.Timer;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author NelsonJrLHerrera
 */
public class AdminGUI extends javax.swing.JFrame {

    AttendanceDAOImpl attendanceDAOImpl = new AttendanceDAOImpl();
    StudentDAOImpl studentDAOImpl = new StudentDAOImpl();
    UserDAOImpl userDAOImpl = new UserDAOImpl();
    TeacherDAOImpl teacherDAOImpl = new TeacherDAOImpl();
    ClassesDAOImpl classesDAOImpl = new ClassesDAOImpl();
    EventDAOImpl eventDAOImpl = new EventDAOImpl();
    User user = null;
    private static User loggedInUser;
    private Timer refreshTimer;

    /**
     * Creates new form AdminGUI
     */
    public AdminGUI(User user) {
        initComponents();
        this.loggedInUser = user;
        startAutoRefresh();
        greetUser();
        filterAndDisplayAttendance("All", "All", "All");
    }

    public void startAutoRefresh() {
        int delay = 5000; // 5 seconds
        if (refreshTimer != null && refreshTimer.isRunning()) {
            refreshTimer.stop();
        }
        refreshTimer = new Timer(delay, e -> refreshTables());
        refreshTimer.setRepeats(true);
        refreshTimer.start();
        refreshTables();  // Immediate refresh
    }

    public User getLoggedUser() {
        return this.loggedInUser;
    }

    public void stopAutoRefresh() {
        if (refreshTimer != null && refreshTimer.isRunning()) {
            refreshTimer.stop();
        }
    }

    public void greetUser() {
        greetLBL.setText("Welcome to EAM-System Teacher " + this.loggedInUser.getFirstname() + " " + this.loggedInUser.getLastname());
    }

    private void resetUsersForm() {
        usersUsernameTF.setText("");
        showCB.setSelected(false);
        usersFnTF.setText("");
        usersLnTF.setText("");
        usersEmailTF.setText("");
        roleBG.clearSelection(); // Deselects any selected radio button in the ToggleGroup
    }

    private void resetEventsForm() {
        // Clear text fields
        eventsNameTF.setText("");

        // Reset date chooser
        eventsDC.setDate(null);

        // Reset combo boxes
        eventsLocationCB.setSelectedIndex(-1); // or 0 if you want the first item selected
        eventsStartCB.setSelectedIndex(-1);    // or 0
        eventsEndCB.setSelectedIndex(-1);      // or 0

        // Clear the text pane
        eventsDescriptionTP.setText("");
    }

    public void refreshUsersTBL() {
        DefaultTableModel model = (DefaultTableModel) usersTBL.getModel();
        model.setRowCount(0);

        for (User user : userDAOImpl.getAllUsers()) {
            model.addRow(new Object[]{
                user.getUserId(),
                user.getUsername(),
                user.getLastname() + ", " + user.getFirstname(),
                user.getGender(),
                user.getRole(),
                user.getEmail(),
                user.getCreated_at(),
                user.getUpdated_at(),
                user.getIsActive()
            });
        }
    }

    public void refreshStudentsTBL() {
        DefaultTableModel model = (DefaultTableModel) studentsTBL.getModel();
        model.setRowCount(0); // clear previous data

        List<Student> students = studentDAOImpl.read_all();
        List<User> users = userDAOImpl.getAllUsers();

        // Build a lookup map for faster user access by user_id
        Map<Integer, User> userMap = new HashMap<>();
        for (User user : users) {
            userMap.put(user.getUserId(), user);
        }

        for (Student student : students) {
            User user = userMap.get(student.getUser_id());

            if (user != null) {
                String fullName = user.getLastname() + ", " + user.getFirstname();
                String gradeAndSection = student.getGradeLevel() + " - " + student.getSection();

                model.addRow(new Object[]{
                    user.getUserId(),
                    user.getUsername(),
                    student.getLrn(),
                    fullName,
                    user.getGender(),
                    gradeAndSection,
                    user.getRole(),
                    user.getEmail(),
                    user.getIsActive()
                });
            } else {
                System.err.println("No user found for student with user_id = " + student.getUser_id());
            }
        }
    }

    public void refreshTeachersTBL() {
        DefaultTableModel model = (DefaultTableModel) teachersTBL.getModel(); // ✅ correct table
        model.setRowCount(0); // Clear previous rows

        List<Teacher> teachers = teacherDAOImpl.getAllTeachers();
        List<User> users = userDAOImpl.getAllUsers();

        if (teachers == null || users == null) {
            JOptionPane.showMessageDialog(this, "Failed to load teachers or users.");
            return;
        }

        // Fast lookup for users by user_id
        Map<Integer, User> userMap = new HashMap<>();
        for (User user : users) {
            userMap.put(user.getUserId(), user);
        }

        for (Teacher teacher : teachers) {
            User user = userMap.get(teacher.getUserId());

            if (user != null) {
                String fullName = user.getLastname() + ", " + user.getFirstname();

                model.addRow(new Object[]{
                    user.getUserId(),
                    user.getUsername(),
                    fullName,
                    user.getGender(),
                    teacher.getAdvisoryClass(),
                    user.getRole(),
                    user.getEmail(),
                    user.getIsActive()
                });
            } else {
                System.err.println("No user found for teacher with user_id = " + teacher.getUserId());
            }
        }
    }

    public void refreshClassesTBL() {
        DefaultTableModel model = (DefaultTableModel) classesTBL.getModel(); // ✅ your actual classes JTable
        model.setRowCount(0); // Clear old data

        List<Classes> classList = classesDAOImpl.read_all();
        List<Teacher> teachers = teacherDAOImpl.getAllTeachers();
        List<User> users = userDAOImpl.getAllUsers();

        if (classList == null || teachers == null || users == null) {
            JOptionPane.showMessageDialog(this, "Failed to load data for classes, teachers, or users.");
            return;
        }

        // Fast lookup maps
        Map<Integer, Teacher> teacherMap = new HashMap<>();
        for (Teacher teacher : teachers) {
            teacherMap.put(teacher.getTeacherId(), teacher);
        }

        Map<Integer, User> userMap = new HashMap<>();
        for (User user : users) {
            userMap.put(user.getUserId(), user);
        }

        for (Classes cls : classList) {
            Teacher teacher = teacherMap.get(cls.getAdviser_id());
            if (teacher != null) {
                User user = userMap.get(teacher.getUserId());

                if (user != null) {
                    String fullName = user.getLastname() + ", " + user.getFirstname();
                    String gradeSection = "Grade " + cls.getGrade() + " - " + cls.getSection();

                    model.addRow(new Object[]{
                        cls.getClass_id(),
                        fullName,
                        gradeSection,
                        cls.getCreated_at(),
                        cls.getUpdated_at()
                    });
                } else {
                    System.err.println("⚠️ No user found for teacher.user_id = " + teacher.getUserId());
                }
            } else {
                System.err.println("⚠️ No teacher found for class.adviser_id = " + cls.getAdviser_id());
            }
        }
    }

    public void refreshEventsTBL() {
        // Clear existing rows
        DefaultTableModel model = (DefaultTableModel) eventsTBL.getModel();
        model.setRowCount(0);

        // Fetch all events from the database
        List<Event> events = eventDAOImpl.readAllEvents();

        if (events == null) {
            JOptionPane.showMessageDialog(this, "Failed to load events from the database.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Formatter for time
        SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a");

        // Populate table with each event
        for (Event e : events) {
            String formattedStart = e.getStartTime();
            String formattedEnd = e.getEndTime();

            try {
                Date start = inputFormat.parse(e.getStartTime());
                formattedStart = outputFormat.format(start);

                Date end = inputFormat.parse(e.getEndTime());
                formattedEnd = outputFormat.format(end);
            } catch (Exception ex) {
                ex.printStackTrace(); // fallback: use original if parsing fails
            }

            Object[] rowData = {
                e.getEventId(),
                e.getEventName(),
                e.getDescription(),
                e.getDate(),
                e.getVenue(),
                formattedStart,
                formattedEnd,
                e.getStatus(),
                e.getCreated_at(),
                e.getUpdated_at()
            };
            model.addRow(rowData);
        }
    }

    public void refreshAttendanceTBL() {
        // ===== 1. Clear Table =====
        DefaultTableModel model = (DefaultTableModel) attendanceTBL.getModel();
        model.setRowCount(0);

        // ===== 2. Refresh ComboBox =====
        attendanceEventCB.removeAllItems();
        attendanceEventCB.addItem("All");

        List<Event> eventList = eventDAOImpl.readAllEvents();
        for (Event event : eventList) {
            attendanceEventCB.addItem(event.getEventName());
        }

        // ===== 3. Query Attendance Records =====
        String query = """
        SELECT 
            a.record_id,
            a.event_id,
            e.event_name,
            s.student_id,
            u.first_name, 
            u.last_name,
            c.grade,
            c.section,
            au.first_name AS adviser_first_name,
            au.last_name AS adviser_last_name,
            e.date,
            a.check_in_time,
            a.check_out_time,
            a.remark
        FROM 
            attendances a
        JOIN students s ON a.student_id = s.student_id
        JOIN users u ON s.user_id = u.user_id
        JOIN events e ON a.event_id = e.event_id
        JOIN classes c ON s.class_id = c.class_id
        JOIN teachers t ON c.adviser_id = t.teacher_id
        JOIN users au ON t.user_id = au.user_id
        ORDER BY e.date DESC, a.check_in_time ASC
    """;

        try (
                Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");

            while (rs.next()) {
                int recordId = rs.getInt("record_id");
                int eventId = rs.getInt("event_id");
                String eventName = rs.getString("event_name");
                int studentId = rs.getInt("student_id");
                String fullName = rs.getString("first_name") + " " + rs.getString("last_name");
                String adviserName = rs.getString("adviser_first_name") + " " + rs.getString("adviser_last_name");
                String gradeSection = rs.getInt("grade") + "-" + rs.getString("section");
                Date eventDate = rs.getDate("date");

                Timestamp checkIn = rs.getTimestamp("check_in_time");
                Timestamp checkOut = rs.getTimestamp("check_out_time");

                String checkInFormatted = (checkIn != null) ? timeFormat.format(checkIn) : "—";
                String checkOutFormatted = (checkOut != null) ? timeFormat.format(checkOut) : "—";
                String remark = rs.getString("remark") != null ? rs.getString("remark") : "—";

                model.addRow(new Object[]{
                    recordId,
                    eventId,
                    eventName,
                    studentId,
                    fullName,
                    gradeSection,
                    adviserName,
                    eventDate,
                    checkInFormatted,
                    checkOutFormatted,
                    remark
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Error loading attendance data:\n" + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public void updateEventsStatus() {
        String selectSql = "SELECT event_id, date, end_time, status FROM events";
        String updateSql = "UPDATE events SET status = ?, updated_at = NOW() WHERE event_id = ?";

        try (Connection conn = DBConnection.getConnection(); Statement selectStmt = conn.createStatement(); ResultSet rs = selectStmt.executeQuery(selectSql); PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {

            while (rs.next()) {
                int eventId = rs.getInt("event_id");
                String eventDate = rs.getString("date");
                String endTime = rs.getString("end_time");
                String currentStatus = rs.getString("status");

                String newStatus = eventDAOImpl.determineStatus(eventDate, endTime);

                // Only update if status has changed
                if (!newStatus.equalsIgnoreCase(currentStatus)) {
                    updateStmt.setString(1, newStatus);
                    updateStmt.setInt(2, eventId);
                    updateStmt.executeUpdate();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void refreshTables() {
        refreshUsersTBL();
        refreshStudentsTBL();
        refreshTeachersTBL();
        refreshClassesTBL();
        refreshEventsTBL();
        refreshAttendanceTBL();
    }

    private void filterAndDisplayAttendance(String eventFilter, String gradeSectionFilter, String remarkFilter) {
        // Get selected filters from your combo boxes
        // Call the DAO implementation method
        List<AttendanceDetail> filteredAttendance = attendanceDAOImpl.filterAttendance(eventFilter, gradeSectionFilter, remarkFilter);

        // Update the table display
        updateAttendanceTable(filteredAttendance);
    }

    private void updateAttendanceTable(List<AttendanceDetail> attendanceList) {
        DefaultTableModel model = (DefaultTableModel) attendanceTBL.getModel();
        model.setRowCount(0); // Clear existing rows

        for (AttendanceDetail a : attendanceList) {
            Object[] row = new Object[]{
                a.getRecordId(),
                a.getEventId(),
                a.getEventName(),
                a.getStudentId(),
                a.getStudentName(),
                a.getGradeSection(),
                a.getAdviserName(),
                a.getDate(),
                a.getCheckInTime(),
                a.getCheckOutTime(),
                a.getRemark()
            };
            model.addRow(row);
        }
    }

    private String getSafeSelectedItem(JComboBox comboBox) {
        Object selected = comboBox.getSelectedItem();
        return (selected != null) ? selected.toString() : null;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        roleBG = new javax.swing.ButtonGroup();
        usersPUM = new javax.swing.JPopupMenu();
        usersEditMI = new javax.swing.JMenuItem();
        usersDeleteMI = new javax.swing.JMenuItem();
        studentsPUM = new javax.swing.JPopupMenu();
        studentsEditMI = new javax.swing.JMenuItem();
        studentsDeleteMI = new javax.swing.JMenuItem();
        teachersPUM = new javax.swing.JPopupMenu();
        teachersEditMI = new javax.swing.JMenuItem();
        teachersDeleteMI = new javax.swing.JMenuItem();
        eventsPUM = new javax.swing.JPopupMenu();
        eventsEditMI = new javax.swing.JMenuItem();
        eventsDeleteMI = new javax.swing.JMenuItem();
        finalizeAttendanceMI = new javax.swing.JMenuItem();
        classesPUM = new javax.swing.JPopupMenu();
        classesEditMI = new javax.swing.JMenuItem();
        classesDeleteMI = new javax.swing.JMenuItem();
        classesViewStudents = new javax.swing.JMenuItem();
        genderBG = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel9 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        greetLBL = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        usersTBL = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        usersSorterCB = new javax.swing.JComboBox<>();
        jScrollBar1 = new javax.swing.JScrollBar();
        jScrollBar5 = new javax.swing.JScrollBar();
        jPanel13 = new javax.swing.JPanel();
        usersSearchBTN = new javax.swing.JButton();
        usersSearchTF = new javax.swing.JTextField();
        jPanel14 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        usersUsernameTF = new javax.swing.JTextField();
        usersFnTF = new javax.swing.JTextField();
        usersLnTF = new javax.swing.JTextField();
        usersSaveBTN = new javax.swing.JButton();
        usersDiscardBTN = new javax.swing.JButton();
        adminRB = new javax.swing.JRadioButton();
        teacherRB = new javax.swing.JRadioButton();
        studentRB = new javax.swing.JRadioButton();
        jLabel19 = new javax.swing.JLabel();
        usersEmailTF = new javax.swing.JTextField();
        usersPasswordTF = new javax.swing.JPasswordField();
        showCB = new javax.swing.JCheckBox();
        jLabel13 = new javax.swing.JLabel();
        maleRB = new javax.swing.JRadioButton();
        femaleRB = new javax.swing.JRadioButton();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        studentsTBL = new javax.swing.JTable();
        jPanel26 = new javax.swing.JPanel();
        studentsSearchBTN = new javax.swing.JButton();
        studentsSearchTF = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        studentsCB = new javax.swing.JComboBox<>();
        jLabel14 = new javax.swing.JLabel();
        jScrollBar2 = new javax.swing.JScrollBar();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        teachersTBL = new javax.swing.JTable();
        jPanel27 = new javax.swing.JPanel();
        teachersSearchBTN = new javax.swing.JButton();
        teachersSearchTF = new javax.swing.JTextField();
        jPanel12 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        teachersCB = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        jScrollBar3 = new javax.swing.JScrollBar();
        jPanel19 = new javax.swing.JPanel();
        jPanel20 = new javax.swing.JPanel();
        jPanel22 = new javax.swing.JPanel();
        jPanel28 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        classesSortCB = new javax.swing.JComboBox<>();
        jLabel15 = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        teachersTBL1 = new javax.swing.JTable();
        jPanel21 = new javax.swing.JPanel();
        classesSearchBTN = new javax.swing.JButton();
        classesSearchTF = new javax.swing.JTextField();
        jScrollPane8 = new javax.swing.JScrollPane();
        classesTBL = new javax.swing.JTable();
        jPanel7 = new javax.swing.JPanel();
        jPanel23 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        eventsTBL = new javax.swing.JTable();
        jPanel15 = new javax.swing.JPanel();
        eventsCB = new javax.swing.JComboBox<>();
        jScrollBar4 = new javax.swing.JScrollBar();
        jPanel24 = new javax.swing.JPanel();
        eventsSearchTF = new javax.swing.JTextField();
        eventsSearchBTN = new javax.swing.JButton();
        jPanel25 = new javax.swing.JPanel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        eventsNameTF = new javax.swing.JTextField();
        eventsSaveBTN = new javax.swing.JButton();
        eventsDiscardBTN = new javax.swing.JButton();
        jLabel40 = new javax.swing.JLabel();
        eventsStartCB = new javax.swing.JComboBox<>();
        eventsEndCB = new javax.swing.JComboBox<>();
        eventsLocationCB = new javax.swing.JComboBox<>();
        jScrollPane3 = new javax.swing.JScrollPane();
        eventsDescriptionTP = new javax.swing.JTextPane();
        jLabel39 = new javax.swing.JLabel();
        eventsDC = new com.toedter.calendar.JDateChooser();
        jPanel8 = new javax.swing.JPanel();
        jPanel17 = new javax.swing.JPanel();
        attendanceRemarkCB = new javax.swing.JComboBox<>();
        attendanceGradeSectionCB = new javax.swing.JComboBox<>();
        attendanceEventCB = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jPanel18 = new javax.swing.JPanel();
        attendanceSearchTF = new javax.swing.JTextField();
        attendanceSearchBTN = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        attendanceTBL = new javax.swing.JTable();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        logOutMI = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();

        roleBG.add(adminRB);
        roleBG.add(teacherRB);
        roleBG.add(studentRB);

        usersEditMI.setText("Edit");
        usersEditMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                usersEditMIActionPerformed(evt);
            }
        });
        usersPUM.add(usersEditMI);

        usersDeleteMI.setText("Delete");
        usersDeleteMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                usersDeleteMIActionPerformed(evt);
            }
        });
        usersPUM.add(usersDeleteMI);

        studentsEditMI.setText("Edit");
        studentsPUM.add(studentsEditMI);

        studentsDeleteMI.setText("Delete");
        studentsDeleteMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                studentsDeleteMIActionPerformed(evt);
            }
        });
        studentsPUM.add(studentsDeleteMI);

        teachersEditMI.setText("Edit");
        teachersPUM.add(teachersEditMI);

        teachersDeleteMI.setText("Delete");
        teachersDeleteMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                teachersDeleteMIActionPerformed(evt);
            }
        });
        teachersPUM.add(teachersDeleteMI);

        eventsEditMI.setText("Edit");
        eventsPUM.add(eventsEditMI);

        eventsDeleteMI.setText("Delete");
        eventsDeleteMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eventsDeleteMIActionPerformed(evt);
            }
        });
        eventsPUM.add(eventsDeleteMI);

        finalizeAttendanceMI.setText("Finalize Attendance");
        finalizeAttendanceMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                finalizeAttendanceMIActionPerformed(evt);
            }
        });
        eventsPUM.add(finalizeAttendanceMI);

        classesEditMI.setText("Edit");
        classesPUM.add(classesEditMI);

        classesDeleteMI.setText("Delete");
        classesDeleteMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                classesDeleteMIActionPerformed(evt);
            }
        });
        classesPUM.add(classesDeleteMI);

        classesViewStudents.setText("View Students");
        classesPUM.add(classesViewStudents);

        genderBG.add(maleRB);
        genderBG.add(femaleRB);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(31, 125, 83));

        jTabbedPane1.setTabPlacement(javax.swing.JTabbedPane.LEFT);

        greetLBL.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        greetLBL.setText("Welcome to EAM-System Admin user");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(353, 353, 353)
                .addComponent(greetLBL)
                .addContainerGap(1613, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(240, 240, 240)
                .addComponent(greetLBL, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(302, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Dashboard", jPanel9);

        usersTBL.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "User ID", "Username", "Name", "Gender", "Role", "Email", "Created At", "Updated At", "Status"
            }
        ));
        usersTBL.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                usersTBLMouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(usersTBL);

        jPanel3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        usersSorterCB.setBackground(new java.awt.Color(204, 204, 204));
        usersSorterCB.setFont(new java.awt.Font("Serif", 0, 12)); // NOI18N
        usersSorterCB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Admin", "Teacher", "Student", "Active", "Inactive" }));
        usersSorterCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                usersSorterCBActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(usersSorterCB, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(0, 21, Short.MAX_VALUE)
                .addComponent(usersSorterCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 777, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollBar5, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jScrollBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addGap(441, 441, 441))
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jScrollBar5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );

        jPanel13.setBackground(new java.awt.Color(204, 204, 204));
        jPanel13.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        usersSearchBTN.setText("Search");
        usersSearchBTN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                usersSearchBTNActionPerformed(evt);
            }
        });

        usersSearchTF.setForeground(new java.awt.Color(102, 102, 102));
        usersSearchTF.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                usersSearchTFFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                usersSearchTFFocusLost(evt);
            }
        });
        usersSearchTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                usersSearchTFActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(usersSearchTF)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(usersSearchBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                .addContainerGap(16, Short.MAX_VALUE)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(usersSearchTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(usersSearchBTN))
                .addGap(14, 14, 14))
        );

        jPanel14.setBackground(new java.awt.Color(204, 204, 204));
        jPanel14.setBorder(javax.swing.BorderFactory.createTitledBorder("Add Users"));

        jLabel2.setFont(new java.awt.Font("Serif", 1, 14)); // NOI18N
        jLabel2.setText("Password:");

        jLabel3.setFont(new java.awt.Font("Serif", 1, 14)); // NOI18N
        jLabel3.setText("Firstname:");

        jLabel4.setFont(new java.awt.Font("Serif", 1, 14)); // NOI18N
        jLabel4.setText("Lastname:");

        jLabel5.setFont(new java.awt.Font("Serif", 1, 14)); // NOI18N
        jLabel5.setText("Gender:");

        jLabel6.setFont(new java.awt.Font("Serif", 1, 14)); // NOI18N
        jLabel6.setText("Role:");

        usersFnTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                usersFnTFActionPerformed(evt);
            }
        });

        usersSaveBTN.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        usersSaveBTN.setText("Save");
        usersSaveBTN.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED, null, new java.awt.Color(204, 204, 204), null, null));
        usersSaveBTN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                usersSaveBTNActionPerformed(evt);
            }
        });

        usersDiscardBTN.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        usersDiscardBTN.setText("Discard");
        usersDiscardBTN.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED, null, new java.awt.Color(204, 204, 204), null, null));
        usersDiscardBTN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                usersDiscardBTNActionPerformed(evt);
            }
        });

        adminRB.setText("Admin");

        teacherRB.setText("Teacher");

        studentRB.setText("Student");

        jLabel19.setFont(new java.awt.Font("Serif", 1, 14)); // NOI18N
        jLabel19.setText("Username:");

        usersPasswordTF.setEditable(false);
        usersPasswordTF.setText("admin");

        showCB.setText("Show Password");
        showCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showCBActionPerformed(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Serif", 1, 14)); // NOI18N
        jLabel13.setText("Email:");

        maleRB.setText("Male");

        femaleRB.setText("Female");

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(showCB, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel14Layout.createSequentialGroup()
                            .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel2)
                                        .addComponent(jLabel4)
                                        .addComponent(jLabel3))
                                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING))
                                .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.LEADING))
                            .addGap(18, 18, 18)
                            .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(usersUsernameTF)
                                .addComponent(usersLnTF)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                                    .addComponent(usersSaveBTN, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(usersDiscardBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(usersPasswordTF)
                                .addComponent(usersEmailTF, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(usersFnTF, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanel14Layout.createSequentialGroup()
                                    .addComponent(maleRB)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(femaleRB))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                                    .addComponent(adminRB)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(teacherRB)
                                    .addGap(4, 4, 4)
                                    .addComponent(studentRB)
                                    .addGap(10, 10, 10)))))
                    .addComponent(jLabel5))
                .addContainerGap(23, Short.MAX_VALUE))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(usersUsernameTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(usersPasswordTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addComponent(showCB, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(usersFnTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(usersLnTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(maleRB)
                    .addComponent(femaleRB))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(usersEmailTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(studentRB)
                    .addComponent(teacherRB)
                    .addComponent(adminRB))
                .addGap(27, 27, 27)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(usersSaveBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(usersDiscardBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(59, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(1194, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel11, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 499, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 139, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Users", jPanel2);

        studentsTBL.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "User ID", "Username", "LRN", "Name", "Gender", "Grade & Section", "Role", "Email", "Status"
            }
        ));
        studentsTBL.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                studentsTBLMouseReleased(evt);
            }
        });
        jScrollPane5.setViewportView(studentsTBL);

        jPanel26.setBackground(new java.awt.Color(204, 204, 204));
        jPanel26.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        studentsSearchBTN.setText("Search");
        studentsSearchBTN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                studentsSearchBTNActionPerformed(evt);
            }
        });

        studentsSearchTF.setForeground(new java.awt.Color(102, 102, 102));
        studentsSearchTF.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                studentsSearchTFFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                studentsSearchTFFocusLost(evt);
            }
        });

        javax.swing.GroupLayout jPanel26Layout = new javax.swing.GroupLayout(jPanel26);
        jPanel26.setLayout(jPanel26Layout);
        jPanel26Layout.setHorizontalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel26Layout.createSequentialGroup()
                .addContainerGap(93, Short.MAX_VALUE)
                .addComponent(studentsSearchTF, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(studentsSearchBTN)
                .addContainerGap())
        );
        jPanel26Layout.setVerticalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel26Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(studentsSearchBTN)
                    .addComponent(studentsSearchTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        jLabel1.setFont(new java.awt.Font("Serif", 1, 12)); // NOI18N
        jLabel1.setText("Students List");

        studentsCB.setBackground(new java.awt.Color(204, 204, 204));
        studentsCB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "11-ABM", "11-HE1", "11-HE2A", "11-HE2B", "11-HUMMS", "11-ICT", "11-STEM", "12-ABM", "12-HE1", "12-HE2A", "12-HE2B", "12-HUMMS", "12-ICT", "12-STEM" }));
        studentsCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                studentsCBActionPerformed(evt);
            }
        });

        jLabel14.setText("Grade & Section:");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 511, Short.MAX_VALUE)
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(studentsCB, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(0, 23, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(studentsCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14)))
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 1152, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(1148, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 472, Short.MAX_VALUE)
                    .addComponent(jScrollBar2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(105, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Students", jPanel5);

        teachersTBL.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "User ID", "Username", "Name", "Gender", "Advisory", "Role", "Email", "Status"
            }
        ));
        teachersTBL.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                teachersTBLMouseReleased(evt);
            }
        });
        jScrollPane6.setViewportView(teachersTBL);

        jPanel27.setBackground(new java.awt.Color(204, 204, 204));
        jPanel27.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        teachersSearchBTN.setText("Search");
        teachersSearchBTN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                teachersSearchBTNActionPerformed(evt);
            }
        });

        teachersSearchTF.setForeground(new java.awt.Color(102, 102, 102));
        teachersSearchTF.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                teachersSearchTFFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                teachersSearchTFFocusLost(evt);
            }
        });

        javax.swing.GroupLayout jPanel27Layout = new javax.swing.GroupLayout(jPanel27);
        jPanel27.setLayout(jPanel27Layout);
        jPanel27Layout.setHorizontalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel27Layout.createSequentialGroup()
                .addContainerGap(97, Short.MAX_VALUE)
                .addComponent(teachersSearchTF, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(teachersSearchBTN)
                .addContainerGap())
        );
        jPanel27Layout.setVerticalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel27Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(teachersSearchBTN)
                    .addComponent(teachersSearchTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jPanel12.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        jLabel7.setFont(new java.awt.Font("Serif", 1, 12)); // NOI18N
        jLabel7.setText("Teachers List");

        teachersCB.setBackground(new java.awt.Color(204, 204, 204));
        teachersCB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "11-ABM", "11-HE1", "11-HE2A", "11-HE2B", "11-HUMMS", "11-ICT", "11-STEM", "12-ABM", " 12-HE1", " 12-HE2A", " 12-HE2B", "12-HUMMS", " 12-ICT", " 12-STEM" }));
        teachersCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                teachersCBActionPerformed(evt);
            }
        });

        jLabel8.setText("Sort Advisory:");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 478, Short.MAX_VALUE)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(teachersCB, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addGap(0, 19, Short.MAX_VALUE)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(teachersCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)))
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addComponent(jScrollPane6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollBar3, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(1194, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollBar3, javax.swing.GroupLayout.PREFERRED_SIZE, 435, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 441, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(138, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Teachers", jPanel6);

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1188, Short.MAX_VALUE)
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 544, Short.MAX_VALUE)
        );

        jPanel28.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        jLabel9.setFont(new java.awt.Font("Serif", 1, 12)); // NOI18N
        jLabel9.setText("Class List");

        classesSortCB.setBackground(new java.awt.Color(204, 204, 204));
        classesSortCB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "11-ABM", "11-HE1", "11-HE2A", "11-HE2B", "11-HUMMS", "11-ICT", "11-STEM", "12-ABM", "12-HE1", "12-HE2A", "12-HE2B", "12-HUMMS", "12-ICT", "12-STEM" }));
        classesSortCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                classesSortCBActionPerformed(evt);
            }
        });

        jLabel15.setText("Grade & Section");

        javax.swing.GroupLayout jPanel28Layout = new javax.swing.GroupLayout(jPanel28);
        jPanel28.setLayout(jPanel28Layout);
        jPanel28Layout.setHorizontalGroup(
            jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel28Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 448, Short.MAX_VALUE)
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(classesSortCB, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel28Layout.setVerticalGroup(
            jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel28Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(classesSortCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        teachersTBL1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "User ID", "Username", "Name", "Advisory", "Role", "Email", "Status"
            }
        ));
        teachersTBL1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                teachersTBL1MouseReleased(evt);
            }
        });
        jScrollPane7.setViewportView(teachersTBL1);

        jPanel21.setBackground(new java.awt.Color(204, 204, 204));
        jPanel21.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        classesSearchBTN.setText("Search");
        classesSearchBTN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                classesSearchBTNActionPerformed(evt);
            }
        });

        classesSearchTF.setForeground(new java.awt.Color(102, 102, 102));
        classesSearchTF.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                classesSearchTFFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                classesSearchTFFocusLost(evt);
            }
        });

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(classesSearchTF, javax.swing.GroupLayout.DEFAULT_SIZE, 329, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(classesSearchBTN)
                .addContainerGap())
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(classesSearchBTN)
                    .addComponent(classesSearchTF, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(29, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel22Layout = new javax.swing.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane7)
                .addGap(365, 365, 365))
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addComponent(jPanel28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel22Layout.setVerticalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 441, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        classesTBL.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Class ID", "Adviser", "Grade & Section", "Created_at", "Updated_at"
            }
        ));
        classesTBL.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                classesTBLMouseReleased(evt);
            }
        });
        jScrollPane8.setViewportView(classesTBL);

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel19Layout.createSequentialGroup()
                        .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 1169, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addComponent(jPanel22, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 451, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(21, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Classes", jPanel19);

        eventsTBL.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Event ID", "Event Name", "Description", "Date", "Location", "Start", "End", "Status", "Created at", "Updated at"
            }
        ));
        eventsTBL.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                eventsTBLMouseReleased(evt);
            }
        });
        jScrollPane4.setViewportView(eventsTBL);

        jPanel15.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        eventsCB.setBackground(new java.awt.Color(204, 204, 204));
        eventsCB.setFont(new java.awt.Font("Serif", 0, 12)); // NOI18N
        eventsCB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Upcoming", "Ongoing", "Finished" }));
        eventsCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eventsCBActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                .addContainerGap(655, Short.MAX_VALUE)
                .addComponent(eventsCB, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                .addGap(0, 25, Short.MAX_VALUE)
                .addComponent(eventsCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout jPanel23Layout = new javax.swing.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addContainerGap(30, Short.MAX_VALUE)
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel23Layout.createSequentialGroup()
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 767, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollBar4, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel23Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel23Layout.createSequentialGroup()
                        .addComponent(jScrollBar4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(43, 43, 43))
                    .addGroup(jPanel23Layout.createSequentialGroup()
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 415, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(16, Short.MAX_VALUE))))
        );

        jPanel24.setBackground(new java.awt.Color(204, 204, 204));
        jPanel24.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        eventsSearchTF.setForeground(new java.awt.Color(102, 102, 102));
        eventsSearchTF.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                eventsSearchTFFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                eventsSearchTFFocusLost(evt);
            }
        });
        eventsSearchTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eventsSearchTFActionPerformed(evt);
            }
        });

        eventsSearchBTN.setText("Search");
        eventsSearchBTN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eventsSearchBTNActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel24Layout = new javax.swing.GroupLayout(jPanel24);
        jPanel24.setLayout(jPanel24Layout);
        jPanel24Layout.setHorizontalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel24Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(eventsSearchTF)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(eventsSearchBTN)
                .addContainerGap())
        );
        jPanel24Layout.setVerticalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel24Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(eventsSearchTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(eventsSearchBTN))
                .addContainerGap(31, Short.MAX_VALUE))
        );

        jPanel25.setBackground(new java.awt.Color(204, 204, 204));
        jPanel25.setBorder(javax.swing.BorderFactory.createTitledBorder("Add Event"));

        jLabel33.setFont(new java.awt.Font("Serif", 1, 14)); // NOI18N
        jLabel33.setText("Event Name:");

        jLabel34.setFont(new java.awt.Font("Serif", 1, 14)); // NOI18N
        jLabel34.setText("Date:");

        jLabel36.setFont(new java.awt.Font("Serif", 1, 14)); // NOI18N
        jLabel36.setText("Location:");

        jLabel37.setFont(new java.awt.Font("Serif", 1, 14)); // NOI18N
        jLabel37.setText("End:");

        jLabel38.setFont(new java.awt.Font("Serif", 1, 14)); // NOI18N

        eventsSaveBTN.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        eventsSaveBTN.setText("Save Event");
        eventsSaveBTN.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED, null, new java.awt.Color(204, 204, 204), null, null));
        eventsSaveBTN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eventsSaveBTNActionPerformed(evt);
            }
        });

        eventsDiscardBTN.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        eventsDiscardBTN.setText("Discard");
        eventsDiscardBTN.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED, null, new java.awt.Color(204, 204, 204), null, null));
        eventsDiscardBTN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eventsDiscardBTNActionPerformed(evt);
            }
        });

        jLabel40.setFont(new java.awt.Font("Serif", 1, 14)); // NOI18N
        jLabel40.setText("Start:");

        eventsStartCB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "12:00 AM", "12:30 AM", "1:00 AM", "1:30 AM", "2:00 AM", "2:30 AM", "3:00 AM", "3:30 AM", "4:00 AM", "4:30 AM", "5:00 AM", "5:30 AM", "6:00 AM", "6:30 AM", "7:00 AM", "7:30 AM", "8:00 AM", "8:30 AM", "9:00 AM", "9:30 AM", "10:00 AM", "10:30 AM", "11:00 AM", "11:30 AM", "12:00 PM", "12:30 PM", "1:00 PM", "1:30 PM", "2:00 PM", "2:30 PM", "3:00 PM", "3:30 PM", "4:00 PM", "4:30 PM", "5:00 PM", "5:30 PM", "6:00 PM", "6:30 PM", "7:00 PM", "7:30 PM", "8:00 PM", "8:30 PM", "9:00 PM", "9:30 PM", "10:00 PM", "10:30 PM", "11:00 PM", "11:30 PM" }));
        eventsStartCB.setSelectedIndex(-1);

        eventsEndCB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "12:00 AM", "12:30 AM", "1:00 AM", "1:30 AM", "2:00 AM", "2:30 AM", "3:00 AM", "3:30 AM", "4:00 AM", "4:30 AM", "5:00 AM", "5:30 AM", "6:00 AM", "6:30 AM", "7:00 AM", "7:30 AM", "8:00 AM", "8:30 AM", "9:00 AM", "9:30 AM", "10:00 AM", "10:30 AM", "11:00 AM", "11:30 AM", "12:00 PM", "12:30 PM", "1:00 PM", "1:30 PM", "2:00 PM", "2:30 PM", "3:00 PM", "3:30 PM", "4:00 PM", "4:30 PM", "5:00 PM", "5:30 PM", "6:00 PM", "6:30 PM", "7:00 PM", "7:30 PM", "8:00 PM", "8:30 PM", "9:00 PM", "9:30 PM", "10:00 PM", "10:30 PM", "11:00 PM", "11:30 PM" }));
        eventsEndCB.setSelectedIndex(-1);

        eventsLocationCB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "SANHS Activity Center", "Sagbayan Municipal Gymnasium", "Sagbayan Cultural Center" }));
        eventsLocationCB.setSelectedIndex(-1);

        jScrollPane3.setViewportView(eventsDescriptionTP);

        jLabel39.setFont(new java.awt.Font("Serif", 1, 14)); // NOI18N
        jLabel39.setText("Description:");

        javax.swing.GroupLayout jPanel25Layout = new javax.swing.GroupLayout(jPanel25);
        jPanel25.setLayout(jPanel25Layout);
        jPanel25Layout.setHorizontalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel25Layout.createSequentialGroup()
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel25Layout.createSequentialGroup()
                        .addGap(117, 117, 117)
                        .addComponent(eventsSaveBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(eventsDiscardBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 3, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel25Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel25Layout.createSequentialGroup()
                                .addComponent(jLabel38)
                                .addGap(76, 76, 76))
                            .addComponent(jLabel34)
                            .addComponent(jLabel33)
                            .addComponent(jLabel36)
                            .addComponent(jLabel39))
                        .addGap(26, 26, 26)
                        .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(eventsNameTF)
                            .addComponent(eventsLocationCB, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(eventsDC, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel25Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel40)
                        .addGap(18, 18, 18)
                        .addComponent(eventsStartCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel37)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(eventsEndCB, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(28, 28, 28))
        );
        jPanel25Layout.setVerticalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel25Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel33)
                    .addComponent(eventsNameTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel34)
                    .addComponent(eventsDC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel36)
                    .addComponent(eventsLocationCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel39))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel38)
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel25Layout.createSequentialGroup()
                        .addGap(55, 55, 55)
                        .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(eventsSaveBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(eventsDiscardBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(21, Short.MAX_VALUE))
                    .addGroup(jPanel25Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(eventsEndCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel37)
                            .addComponent(eventsStartCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel40))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jPanel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(1196, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jPanel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 141, Short.MAX_VALUE))
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Events", jPanel7);

        jPanel8.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel17.setBackground(new java.awt.Color(0, 147, 68));
        jPanel17.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        attendanceRemarkCB.setBackground(new java.awt.Color(153, 255, 153));
        attendanceRemarkCB.setFont(new java.awt.Font("Serif", 0, 12)); // NOI18N
        attendanceRemarkCB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "On Time", "Late", "Absent" }));
        attendanceRemarkCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                attendanceRemarkCBActionPerformed(evt);
            }
        });

        attendanceGradeSectionCB.setBackground(new java.awt.Color(153, 255, 153));
        attendanceGradeSectionCB.setFont(new java.awt.Font("Serif", 0, 12)); // NOI18N
        attendanceGradeSectionCB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "11-ABM", "11-HE1", "11-HE2A", "11-HE2B", "11-HUMMS", "11-ICT", "11-STEM", "12-ABM", "12-HE1", "12-HE2A", "12-HE2B", "12-HUMMS", "12-ICT", "12-STEM" }));
        attendanceGradeSectionCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                attendanceGradeSectionCBActionPerformed(evt);
            }
        });

        attendanceEventCB.setBackground(new java.awt.Color(153, 255, 153));
        attendanceEventCB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));
        attendanceEventCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                attendanceEventCBActionPerformed(evt);
            }
        });

        jLabel10.setText("Event:");

        jLabel11.setText("Grade & Section:");

        jLabel12.setText("Remark:");

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(attendanceEventCB, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 401, Short.MAX_VALUE)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(attendanceRemarkCB, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(attendanceGradeSectionCB, 0, 148, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap(9, Short.MAX_VALUE)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel17Layout.createSequentialGroup()
                        .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(attendanceGradeSectionCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(attendanceRemarkCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel17Layout.createSequentialGroup()
                        .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(attendanceEventCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10))
                        .addGap(19, 19, 19))))
        );

        jPanel8.add(jPanel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 6, -1, -1));

        jPanel18.setBackground(new java.awt.Color(204, 204, 204));
        jPanel18.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        attendanceSearchTF.setForeground(new java.awt.Color(102, 102, 102));
        attendanceSearchTF.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                attendanceSearchTFFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                attendanceSearchTFFocusLost(evt);
            }
        });

        attendanceSearchBTN.setText("Search");
        attendanceSearchBTN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                attendanceSearchBTNActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(attendanceSearchTF)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(attendanceSearchBTN)
                .addGap(35, 35, 35))
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(attendanceSearchTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(attendanceSearchBTN))
                .addContainerGap(27, Short.MAX_VALUE))
        );

        jPanel8.add(jPanel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(858, 6, 344, -1));

        attendanceTBL.setBackground(new java.awt.Color(204, 255, 204));
        attendanceTBL.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Record ID", "Event ID", "Event", "Student ID", "Student", "Grade & Section", "Adviser", "Date", "Check_in_time", "Check_out_time", "Remark"
            }
        ));
        jScrollPane2.setViewportView(attendanceTBL);

        jPanel8.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 78, 1172, 499));

        jTabbedPane1.addTab("Attendance", jPanel8);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );

        jMenu1.setText("Settings");

        logOutMI.setText("Log Out");
        logOutMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logOutMIActionPerformed(evt);
            }
        });
        jMenu1.add(logOutMI);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Other");
        jMenu2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu2ActionPerformed(evt);
            }
        });

        jMenuItem2.setText("Add Geofence");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem2);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jMenu2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenu2ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        // TODO add your handling code here:
        JOptionPane.showMessageDialog(this, "This tab is still under development.", "Notification", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void eventsSearchBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eventsSearchBTNActionPerformed
        // TODO add your handling code here:
        String query = eventsSearchTF.getText().trim();

        if (query.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a search term.", "Input Required", JOptionPane.WARNING_MESSAGE);
            refreshEventsTBL();  // You should have this method to reload all events
            // Optionally restart any auto-refresh logic here if you have it
            return;
        }

        DefaultTableModel model = (DefaultTableModel) eventsTBL.getModel();
        model.setRowCount(0); // Clear the table

        List<Event> results = eventDAOImpl.searchEvents(query);

        if (results == null || results.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No events found for: " + query, "No Results", JOptionPane.INFORMATION_MESSAGE);
            startAutoRefresh();
            refreshEventsTBL();
        } else {
            for (Event event : results) {
                model.addRow(new Object[]{
                    event.getEventId(),
                    event.getEventName(),
                    event.getDescription(),
                    event.getDate(),
                    event.getVenue(),
                    event.getStartTime(),
                    event.getEndTime(),
                    event.getStatus(),
                    event.getCreated_at(),
                    event.getUpdated_at()
                });
            }
            stopAutoRefresh();
        }
        eventsSearchTF.setText("");
    }//GEN-LAST:event_eventsSearchBTNActionPerformed

    private void eventsCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eventsCBActionPerformed
        // TODO add your handling code here:
        String selectedStatus = (String) eventsCB.getSelectedItem();

        DefaultTableModel model = (DefaultTableModel) eventsTBL.getModel();
        model.setRowCount(0); // Clear current rows

        List<Event> events;

        if ("All".equalsIgnoreCase(selectedStatus)) {
            events = eventDAOImpl.readAllEvents();  // Your method to get all events
        } else {
            events = eventDAOImpl.getEventsByStatus(selectedStatus);
        }

        if (events != null && !events.isEmpty()) {
            for (Event event : events) {
                model.addRow(new Object[]{
                    event.getEventId(),
                    event.getEventName(),
                    event.getDescription(),
                    event.getDate(),
                    event.getVenue(),
                    event.getStartTime(),
                    event.getEndTime(),
                    event.getStatus(),
                    event.getCreated_at(),
                    event.getUpdated_at()
                });
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "No events found for status: " + selectedStatus,
                    "No Results",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_eventsCBActionPerformed

    private void teachersTBLMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_teachersTBLMouseReleased
        // TODO add your handling code here:
        if (evt.isPopupTrigger()) {
            int row = teachersTBL.rowAtPoint(evt.getPoint());

            if (row >= 0) {
                teachersTBL.setRowSelectionInterval(row, row);
            }

            teachersPUM.show(teachersTBL, evt.getX(), evt.getY());
        } else {
            System.out.println("Nothing happened!");
        }
    }//GEN-LAST:event_teachersTBLMouseReleased

    private void studentsTBLMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_studentsTBLMouseReleased
        // TODO add your handling code here:
        if (evt.isPopupTrigger()) {
            int row = studentsTBL.rowAtPoint(evt.getPoint());

            if (row >= 0) {
                studentsTBL.setRowSelectionInterval(row, row);
            }

            studentsPUM.show(studentsTBL, evt.getX(), evt.getY());
        } else {
            System.out.println("Nothing happened!");
        }
    }//GEN-LAST:event_studentsTBLMouseReleased

    private void showCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showCBActionPerformed
        // TODO add your handling code here:
        if (showCB.isSelected() == true) {
            usersPasswordTF.setEchoChar((char) 0);
        } else {
            usersPasswordTF.setEchoChar('*');
        }
    }//GEN-LAST:event_showCBActionPerformed

    private void usersDiscardBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_usersDiscardBTNActionPerformed
        // TODO add your handling code here:
        int option = JOptionPane.showConfirmDialog(this, "Are you sure to discard the infos above?", "Confirmation", JOptionPane.OK_CANCEL_OPTION);

        if (option == 0) {
            usersUsernameTF.setText("");
            usersFnTF.setText("");
            usersLnTF.setText("");
            usersEmailTF.setText("");
            roleBG.clearSelection();
        }
    }//GEN-LAST:event_usersDiscardBTNActionPerformed

    private void usersSaveBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_usersSaveBTNActionPerformed
        // TODO add your handling code here:
        String username = usersUsernameTF.getText().trim();
        String rawPassword = usersPasswordTF.getText().trim();
        String firstname = usersFnTF.getText().trim();
        String lastname = usersLnTF.getText().trim();
        String email = usersEmailTF.getText().trim();
        String gender = null;
        String role = null;

        //Determine gender
        if (maleRB.isSelected()) {
            gender = "Male";
        } else if (femaleRB.isSelected()) {
            gender = "Female";
        }

        // Determine role
        if (adminRB.isSelected()) {
            role = "Admin";
        } else if (teacherRB.isSelected()) {
            role = "Teacher";
        } else if (studentRB.isSelected()) {
            role = "Student";
        }

        // Validate inputs
        if (username.isEmpty() || rawPassword.isEmpty() || firstname.isEmpty()
                || lastname.isEmpty() || email.isEmpty() || role == null) {

            JOptionPane.showMessageDialog(this, "Fill out all the fields in the form.");
            return;
        }

        // Check if username already exists
        if (userDAOImpl.getUserByUsername(username) != null) {
            JOptionPane.showMessageDialog(this,
                    "Username already exists. User not added.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Hash password only after validation
        String password = HashUtil.sha256(rawPassword);

        // Create user object
        User newUser = new User(0, username, password, role, firstname, lastname, gender, email, "Inactive", "", "");

        // Insert user into database
        Integer userId = userDAOImpl.addUser(newUser);

        if (userId == null) {
            JOptionPane.showMessageDialog(this,
                    "An error occurred. User not added.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Set generated userId to the user object
        newUser.setUserId(userId);
        this.user = newUser;

        JOptionPane.showMessageDialog(this,
                "User Successfully Added", "Notification",
                JOptionPane.INFORMATION_MESSAGE);

        // Open dialog based on role
        if ("Teacher".equals(role)) {
            Teacher_Info teacherInfo = new Teacher_Info(this, true, this.user);
            teacherInfo.show(true);
        } else if ("Student".equals(role)) {
            StudentInfo studentInfo = new StudentInfo(this, true, this.user);
            studentInfo.show(true);
        }
        // No additional dialog needed for Admin

        //resetForm
        resetUsersForm();
        refreshTables();
    }//GEN-LAST:event_usersSaveBTNActionPerformed

    private void usersFnTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_usersFnTFActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_usersFnTFActionPerformed

    private void usersSearchTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_usersSearchTFActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_usersSearchTFActionPerformed

    private void usersTBLMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_usersTBLMouseReleased
        // TODO add your handling code here:
        if (evt.isPopupTrigger()) {
            int row = usersTBL.rowAtPoint(evt.getPoint());

            if (row >= 0) {
                usersTBL.setRowSelectionInterval(row, row);
            }

            usersPUM.show(usersTBL, evt.getX(), evt.getY());
        } else {
            System.out.println("Nothing happened!");
        }
    }//GEN-LAST:event_usersTBLMouseReleased

    private void teachersTBL1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_teachersTBL1MouseReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_teachersTBL1MouseReleased

    private void eventsDiscardBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eventsDiscardBTNActionPerformed
        // TODO add your handling code here:
        resetEventsForm();
    }//GEN-LAST:event_eventsDiscardBTNActionPerformed

    private void eventsSaveBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eventsSaveBTNActionPerformed
        // TODO add your handling code here:
        // Retrieve values from form fields
        String eventName = eventsNameTF.getText().trim();
        Date selectedDate = eventsDC.getDate();
        String startTime12Hr = (String) eventsStartCB.getSelectedItem();
        String endTime12Hr = (String) eventsEndCB.getSelectedItem();
        String venue = (String) eventsLocationCB.getSelectedItem();
        String description = eventsDescriptionTP.getText().trim();

        // Validate required fields
        if (eventName.isEmpty() || selectedDate == null || startTime12Hr == null || endTime12Hr == null
                || venue == null || description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please complete all fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Format date to "yyyy-MM-dd"
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = dateFormat.format(selectedDate);

        // Convert time from 12-hour (e.g., "7:00 AM") to 24-hour format (e.g., "07:00:00")
        SimpleDateFormat inputTimeFormat = new SimpleDateFormat("hh:mm a");
        SimpleDateFormat outputTimeFormat = new SimpleDateFormat("HH:mm:ss");

        String startTime24Hr, endTime24Hr;

        try {
            Date startTimeParsed = inputTimeFormat.parse(startTime12Hr);
            Date endTimeParsed = inputTimeFormat.parse(endTime12Hr);

            // Validate that start time is before end time
            if (!startTimeParsed.before(endTimeParsed)) {
                JOptionPane.showMessageDialog(this, "Start time must be before end time.", "Time Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Convert to 24-hour format
            startTime24Hr = outputTimeFormat.format(startTimeParsed);
            endTime24Hr = outputTimeFormat.format(endTimeParsed);

        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid time format.", "Parse Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String status = eventDAOImpl.determineStatus(date, endTime12Hr);

        // Create new Event object (eventId = 0 for new entries)
        Event event = new Event(0, eventName, date, startTime24Hr, endTime24Hr, venue, description, "", "", status);

        // Save the event via DAO
        boolean isCreated = eventDAOImpl.createEvent(event);

        if (isCreated) {
            JOptionPane.showMessageDialog(this, "Event created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            resetEventsForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to create event.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        refreshEventsTBL();
        resetEventsForm();
    }//GEN-LAST:event_eventsSaveBTNActionPerformed

    private void classesTBLMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_classesTBLMouseReleased
        // TODO add your handling code here:
        if (evt.isPopupTrigger()) {
            int row = classesTBL.rowAtPoint(evt.getPoint());

            if (row >= 0) {
                classesTBL.setRowSelectionInterval(row, row);
            }

            classesPUM.show(classesTBL, evt.getX(), evt.getY());
        } else {
            System.out.println("Nothing happened!");
        }
    }//GEN-LAST:event_classesTBLMouseReleased

    private void eventsTBLMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_eventsTBLMouseReleased
        // TODO add your handling code here:
        if (evt.isPopupTrigger()) {
            int row = eventsTBL.rowAtPoint(evt.getPoint());

            if (row >= 0) {
                eventsTBL.setRowSelectionInterval(row, row);
            }

            eventsPUM.show(eventsTBL, evt.getX(), evt.getY());
        } else {
            System.out.println("Nothing happened!");
        }
    }//GEN-LAST:event_eventsTBLMouseReleased

    private void logOutMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logOutMIActionPerformed
        // TODO add your handling code here:
        int option = JOptionPane.showConfirmDialog(
                this,
                "Are you sure to log out from EAM System?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION
        );

        if (option == JOptionPane.YES_OPTION) {
            LoginGUI loginGUI = new LoginGUI();
            loginGUI.setVisible(true);
            this.dispose();
        } else {
            // User clicked "No" — do nothing
        }

    }//GEN-LAST:event_logOutMIActionPerformed

    private void usersSearchTFFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_usersSearchTFFocusGained
        // TODO add your handling code here:
        usersSearchTF.setText("");
        usersSearchTF.setForeground(Color.BLACK);
    }//GEN-LAST:event_usersSearchTFFocusGained

    private void usersSearchTFFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_usersSearchTFFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_usersSearchTFFocusLost

    private void studentsSearchTFFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_studentsSearchTFFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_studentsSearchTFFocusGained

    private void studentsSearchTFFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_studentsSearchTFFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_studentsSearchTFFocusLost

    private void teachersSearchTFFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_teachersSearchTFFocusGained
        // TODO add your handling code here
    }//GEN-LAST:event_teachersSearchTFFocusGained

    private void teachersSearchTFFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_teachersSearchTFFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_teachersSearchTFFocusLost

    private void classesSearchTFFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_classesSearchTFFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_classesSearchTFFocusGained

    private void classesSearchTFFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_classesSearchTFFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_classesSearchTFFocusLost

    private void eventsSearchTFFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_eventsSearchTFFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_eventsSearchTFFocusGained

    private void eventsSearchTFFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_eventsSearchTFFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_eventsSearchTFFocusLost

    private void attendanceSearchTFFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_attendanceSearchTFFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_attendanceSearchTFFocusGained

    private void attendanceSearchTFFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_attendanceSearchTFFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_attendanceSearchTFFocusLost

    private void usersSearchBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_usersSearchBTNActionPerformed
        // TODO add your handling code here:
        String query = usersSearchTF.getText().trim();

        // Optionally ignore empty input
        if (query.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a search term.", "Input Required", JOptionPane.WARNING_MESSAGE);
            refreshUsersTBL();
            startAutoRefresh();
            return;
        }

        DefaultTableModel model = (DefaultTableModel) usersTBL.getModel();
        model.setRowCount(0); // Clear the table

        List<User> results = userDAOImpl.searchUser(query);
        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No users found for: " + query, "No Results", JOptionPane.INFORMATION_MESSAGE);
            refreshUsersTBL();
            startAutoRefresh();
        } else {
            for (User user : results) {
                model.addRow(new Object[]{
                    user.getUserId(),
                    user.getUsername(),
                    user.getLastname() + ", " + user.getFirstname(),
                    user.getGender(),
                    user.getRole(),
                    user.getEmail(),
                    user.getCreated_at(),
                    user.getUpdated_at(),
                    user.getIsActive()
                });
            }
            stopAutoRefresh(); //ensure manual search doesn't get overridden
        }

        usersSearchTF.setText("");
    }//GEN-LAST:event_usersSearchBTNActionPerformed

    private void usersSorterCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_usersSorterCBActionPerformed
        // TODO add your handling code here:
        String sorter = (String) usersSorterCB.getSelectedItem();
        DefaultTableModel model = (DefaultTableModel) usersTBL.getModel();
        model.setRowCount(0); // Clear the table first

        List<User> users = new ArrayList<>();

        if ("All".equals(sorter)) {
            users = userDAOImpl.getAllUsers(); // Make sure this method exists
            startAutoRefresh(); // Resume auto-refresh if needed
        } else if ("Active".equals(sorter) || "Inactive".equals(sorter)) {
            users = userDAOImpl.sortUsersByStatus(sorter);
            stopAutoRefresh(); // Sort filters typically pause auto-refresh
        } else {
            users = userDAOImpl.sortUsers(sorter);
            stopAutoRefresh(); // Sorting by role, so disable auto-refresh
        }

        for (User user : users) {
            model.addRow(new Object[]{
                user.getUserId(),
                user.getUsername(),
                user.getLastname() + ", " + user.getFirstname(),
                user.getGender(),
                user.getRole(),
                user.getEmail(),
                user.getCreated_at(),
                user.getUpdated_at(),
                user.getIsActive()
            });
        }

        stopAutoRefresh(); // prevent overwrite
    }//GEN-LAST:event_usersSorterCBActionPerformed

    private void studentsSearchBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_studentsSearchBTNActionPerformed
        String query = studentsSearchTF.getText().trim();

        if (query.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a search term.", "Input Required", JOptionPane.WARNING_MESSAGE);
            refreshStudentsTBL();
            startAutoRefresh();
            return;
        }

        DefaultTableModel model = (DefaultTableModel) studentsTBL.getModel();
        model.setRowCount(0); // Clear the table

        List<StudentDetail> results = studentDAOImpl.searchStudents(query);
        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No users found for: " + query, "No Results", JOptionPane.INFORMATION_MESSAGE);
            refreshStudentsTBL();
            startAutoRefresh();
        } else {
            for (StudentDetail student : results) {
                model.addRow(new Object[]{
                    student.getUserId(),
                    student.getFullName(),
                    student.getGender(),
                    student.getGradeLevel(),
                    student.getSection(),
                    student.getRole(),
                    student.getEmail(),
                    student.getStatus()
                });
            }
            stopAutoRefresh();
        }

        studentsSearchTF.setText("");
    }//GEN-LAST:event_studentsSearchBTNActionPerformed

    private void studentsCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_studentsCBActionPerformed
        // TODO add your handling code here:
        String sorter = (String) studentsCB.getSelectedItem();
        DefaultTableModel model = (DefaultTableModel) studentsTBL.getModel();
        model.setRowCount(0); // Clear current rows

        if ("All".equals(sorter)) {
            refreshStudentsTBL(); // Already includes loading all students
            return;
        }

        // Extract grade and section
        int grade = Integer.parseInt(sorter.substring(0, 2));
        String section = sorter.substring(3);

        // Get filtered students
        List<StudentDetail> students = studentDAOImpl.sortStudentsByGradeAndSection(grade, section);

        // Populate the table
        for (StudentDetail s : students) {
            model.addRow(new Object[]{
                s.getUserId(),
                s.getLrn(),
                s.getFullName(),
                s.getGender(),
                s.getGradeLevel(),
                s.getSection(),
                s.getRole(),
                s.getEmail(),
                s.getStatus()
            });
        }

        stopAutoRefresh(); // prevent overwrite
    }//GEN-LAST:event_studentsCBActionPerformed

    private void teachersCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_teachersCBActionPerformed
        // TODO add your handling code here:
        String selected = (String) teachersCB.getSelectedItem();
        DefaultTableModel model = (DefaultTableModel) teachersTBL.getModel();
        model.setRowCount(0); // Clear existing rows

        if ("All".equals(selected)) {
            refreshTeachersTBL(); // Show all teachers
            return;
        }

        List<TeacherDetail> teachers = teacherDAOImpl.sortTeachersByAdvisory(selected);

        for (TeacherDetail t : teachers) {
            model.addRow(new Object[]{
                t.getUserId(),
                t.getUsername(),
                t.getFullName(),
                t.getGender(),
                t.getAdvisory(),
                t.getRole(),
                t.getEmail(),
                t.getStatus()
            });
        }
        stopAutoRefresh(); // prevent overwrite
    }//GEN-LAST:event_teachersCBActionPerformed

    private void teachersSearchBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_teachersSearchBTNActionPerformed
        // TODO add your handling code here:
        String query = teachersSearchTF.getText().trim();

        if (query.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a search term.", "Input Required", JOptionPane.WARNING_MESSAGE);
            refreshTeachersTBL(); // fallback to full list
            startAutoRefresh();
            return;
        }

        List<TeacherDetail> results = teacherDAOImpl.searchTeachers(query);
        DefaultTableModel model = (DefaultTableModel) teachersTBL.getModel();
        model.setRowCount(0); // clear table

        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No teachers found for: " + query, "No Results", JOptionPane.INFORMATION_MESSAGE);
            refreshTeachersTBL();
            startAutoRefresh();
        } else {
            for (TeacherDetail t : results) {
                model.addRow(new Object[]{
                    t.getUserId(),
                    t.getUsername(),
                    t.getFullName(),
                    t.getGender(),
                    t.getAdvisory(),
                    t.getRole(),
                    t.getEmail(),
                    t.getStatus()
                });
            }
            stopAutoRefresh(); // prevent overwrite
        }

        teachersSearchTF.setText("");
    }//GEN-LAST:event_teachersSearchBTNActionPerformed

    private void classesSortCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_classesSortCBActionPerformed
        // TODO add your handling code here:
        String selected = (String) classesSortCB.getSelectedItem();

        if ("All".equals(selected)) {
            refreshClassesTBL();
        } else {
            int grade = Integer.parseInt(selected.substring(0, 2));
            String section = selected.substring(3);
            List<Map<String, Object>> results = classesDAOImpl.sortClassesByGradeAndSection(grade, section);

            DefaultTableModel model = (DefaultTableModel) classesTBL.getModel();
            model.setRowCount(0);

            for (Map<String, Object> row : results) {
                model.addRow(new Object[]{
                    row.get("class_id"),
                    row.get("adviser"),
                    row.get("grade_section"),
                    row.get("created_at"),
                    row.get("updated_at")
                });
            }
            stopAutoRefresh(); // prevent overwrite
        }

    }//GEN-LAST:event_classesSortCBActionPerformed

    private void classesSearchBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_classesSearchBTNActionPerformed
        String query = classesSearchTF.getText();

        List<ClassInfoDetail> results = classesDAOImpl.searchClassesByAdviserName(query);
        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No classes found for adviser: " + query, "No Results", JOptionPane.INFORMATION_MESSAGE);
            refreshClassesTBL();
            startAutoRefresh();
        } else {
            DefaultTableModel model = (DefaultTableModel) classesTBL.getModel();
            model.setRowCount(0); // Clear table

            for (ClassInfoDetail cls : results) {
                String gradeSection = "Grade " + cls.getGrade() + " - " + cls.getSection();
                model.addRow(new Object[]{
                    cls.getClassId(),
                    cls.getAdviserName() != null ? cls.getAdviserName() : "Unassigned",
                    gradeSection,
                    cls.getCreatedAt(),
                    cls.getUpdatedAt()
                });
            }
            stopAutoRefresh();
        }
        classesSearchTF.setText("");
    }//GEN-LAST:event_classesSearchBTNActionPerformed

    private void eventsSearchTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eventsSearchTFActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_eventsSearchTFActionPerformed

    private void attendanceEventCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_attendanceEventCBActionPerformed
        // TODO add your handling code here:
        String eventFilter = getSafeSelectedItem(attendanceEventCB);
        String gradeSectionFilter = getSafeSelectedItem(attendanceGradeSectionCB);
        String remarkFilter = getSafeSelectedItem(attendanceRemarkCB);

        if (eventFilter == null || gradeSectionFilter == null || remarkFilter == null) {
            // One or more selections are missing; skip filtering
            return;
        }

        filterAndDisplayAttendance(eventFilter, gradeSectionFilter, remarkFilter);
        stopAutoRefresh();
    }//GEN-LAST:event_attendanceEventCBActionPerformed

    private void attendanceGradeSectionCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_attendanceGradeSectionCBActionPerformed
        // TODO add your handling code here:
        String eventFilter = getSafeSelectedItem(attendanceEventCB);
        String gradeSectionFilter = getSafeSelectedItem(attendanceGradeSectionCB);
        String remarkFilter = getSafeSelectedItem(attendanceRemarkCB);

        if (eventFilter == null || gradeSectionFilter == null || remarkFilter == null) {
            // One or more selections are missing; skip filtering
            return;
        }

        filterAndDisplayAttendance(eventFilter, gradeSectionFilter, remarkFilter);
        stopAutoRefresh();
    }//GEN-LAST:event_attendanceGradeSectionCBActionPerformed

    private void attendanceRemarkCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_attendanceRemarkCBActionPerformed
        // TODO add your handling code here:
        String eventFilter = getSafeSelectedItem(attendanceEventCB);
        String gradeSectionFilter = getSafeSelectedItem(attendanceGradeSectionCB);
        String remarkFilter = getSafeSelectedItem(attendanceRemarkCB);

        if (eventFilter == null || gradeSectionFilter == null || remarkFilter == null) {
            // One or more selections are missing; skip filtering
            return;
        }

        filterAndDisplayAttendance(eventFilter, gradeSectionFilter, remarkFilter);
        stopAutoRefresh();
    }//GEN-LAST:event_attendanceRemarkCBActionPerformed

    private void finalizeAttendanceMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_finalizeAttendanceMIActionPerformed
        // TODO add your handling code here:
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) eventsTBL.getModel();
        int event_id = Integer.parseInt(model.getValueAt(eventsTBL.getSelectedRow(), 0).toString());
        String status = (String) model.getValueAt(eventsTBL.getSelectedRow(), 7);

        if (!("Finished".equalsIgnoreCase(status))) {
            JOptionPane.showConfirmDialog(
                    this,
                    "This event is not yet finished.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        } else {
            eventDAOImpl.finalizeAttendanceForEvent(event_id);
        }

        refreshAttendanceTBL();
    }//GEN-LAST:event_finalizeAttendanceMIActionPerformed

    private void attendanceSearchBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_attendanceSearchBTNActionPerformed
        String keyword = attendanceSearchTF.getText().trim().toLowerCase();

        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter a search term.", "Search Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DefaultTableModel model = (DefaultTableModel) attendanceTBL.getModel();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        attendanceTBL.setRowSorter(sorter);

        RowFilter<DefaultTableModel, Object> filter = new RowFilter<>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Object> entry) {
                for (int i = 0; i < entry.getValueCount(); i++) {
                    Object cellValue = entry.getValue(i);
                    if (cellValue != null && cellValue.toString().toLowerCase().contains(keyword)) {
                        return true;
                    }
                }
                return false;
            }
        };

        sorter.setRowFilter(filter);
    }//GEN-LAST:event_attendanceSearchBTNActionPerformed

    private void usersEditMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_usersEditMIActionPerformed
        // TODO add your handling code here:
        // DefaultTableModel model = (DefaultTableModel) usersTBL.getModel();
        //User user = userDAOImpl.getUserById(Integer.parseInt(model.getValueAt(usersTBL.getSelectedRow(), 0).toString()));


    }//GEN-LAST:event_usersEditMIActionPerformed

    private void usersDeleteMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_usersDeleteMIActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) usersTBL.getModel();

        if (usersTBL.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(null, "Please select a user to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = Integer.parseInt(model.getValueAt(usersTBL.getSelectedRow(), 0).toString());
        String userName = model.getValueAt(usersTBL.getSelectedRow(), 1).toString(); // Assuming second column is name or username

        int confirm = JOptionPane.showConfirmDialog(null,
                "Are you sure you want to delete user '" + userName + "'? This action cannot be undone.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = userDAOImpl.deleteUser(userId); // Handles cascade logic
            if (success) {
                JOptionPane.showMessageDialog(null, "User deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshUsersTBL();  // Your method to reload usersTBL
                refreshTables(); // Optionally refresh students, teachers, classes tables
            } else {
                JOptionPane.showMessageDialog(null, "Failed to delete user. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_usersDeleteMIActionPerformed

    private void studentsDeleteMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_studentsDeleteMIActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) studentsTBL.getModel();

        if (studentsTBL.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(null, "Please select a student to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int studentId = Integer.parseInt(model.getValueAt(studentsTBL.getSelectedRow(), 0).toString());
        String studentName = model.getValueAt(studentsTBL.getSelectedRow(), 1).toString(); // Adjust column index if needed

        int confirm = JOptionPane.showConfirmDialog(null,
                "Are you sure you want to delete student '" + studentName + "'? This will also delete all related attendance records.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = studentDAOImpl.delete(studentId);  // Handle cascade deletion
            if (success) {
                JOptionPane.showMessageDialog(null, "Student deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshStudentsTBL();  // Reload students table data
                refreshAttendanceTBL(); // Optionally refresh attendance table to reflect changes
            } else {
                JOptionPane.showMessageDialog(null, "Failed to delete student. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_studentsDeleteMIActionPerformed

    private void teachersDeleteMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_teachersDeleteMIActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) teachersTBL.getModel();

        int selectedRow = teachersTBL.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a teacher to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int teacherId = Integer.parseInt(model.getValueAt(selectedRow, 0).toString());
        String teacherName = model.getValueAt(selectedRow, 1).toString(); // Assuming column 1 holds teacher name

        int confirm = JOptionPane.showConfirmDialog(null,
                "Are you sure you want to delete teacher '" + teacherName + "'? This will remove them as adviser from any class they advise.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            // Remove adviser link from classes advised by this teacher
            teacherDAOImpl.deleteTeacher(teacherId);
            // Delete teacher record
            boolean success = teacherDAOImpl.deleteTeacher(teacherId);
            if (success) {
                JOptionPane.showMessageDialog(null, "Teacher deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshTeachersTBL();
                refreshClassesTBL();
            } else {
                JOptionPane.showMessageDialog(null, "Failed to delete teacher. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_teachersDeleteMIActionPerformed

    private void eventsDeleteMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eventsDeleteMIActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) eventsTBL.getModel();
        int selectedRow = eventsTBL.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select an event to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int eventId = Integer.parseInt(model.getValueAt(selectedRow, 0).toString());
        String eventName = model.getValueAt(selectedRow, 1).toString(); // assuming column 1 is event name
        String eventStatus = model.getValueAt(selectedRow, 7).toString(); // status at index 7

        // Check if event is ongoing or finished
        if (eventStatus.equalsIgnoreCase("Ongoing") || eventStatus.equalsIgnoreCase("Finished")) {
            JOptionPane.showMessageDialog(null,
                    "Cannot delete event '" + eventName + "' because it is " + eventStatus + ".",
                    "Delete Not Allowed",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(null,
                "Are you sure you want to delete the event '" + eventName + "'?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = eventDAOImpl.deleteEvent(eventId);
            if (success) {
                JOptionPane.showMessageDialog(null, "Event deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshEventsTBL();
            } else {
                JOptionPane.showMessageDialog(null, "Failed to delete event. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_eventsDeleteMIActionPerformed

    private void classesDeleteMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_classesDeleteMIActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) classesTBL.getModel();
    int selectedRow = classesTBL.getSelectedRow();

    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(null, "Please select a class to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
        return;
    }

    int classId = Integer.parseInt(model.getValueAt(selectedRow, 0).toString());
    String className = model.getValueAt(selectedRow, 2).toString(); // assuming class name is at index 1

    try {
        boolean adviserExists = classesDAOImpl.hasAdviser(classId);
        boolean studentsExist = classesDAOImpl.hasStudents(classId);

        if (adviserExists && studentsExist) {
            JOptionPane.showMessageDialog(null,
                "Cannot delete class '" + className + "' because it has an adviser assigned and students enrolled.",
                "Delete Not Allowed",
                JOptionPane.ERROR_MESSAGE);
            return;
        } else if (adviserExists) {
            JOptionPane.showMessageDialog(null,
                "Cannot delete class '" + className + "' because it has an adviser assigned.",
                "Delete Not Allowed",
                JOptionPane.ERROR_MESSAGE);
            return;
        } else if (studentsExist) {
            JOptionPane.showMessageDialog(null,
                "Cannot delete class '" + className + "' because it has students enrolled.",
                "Delete Not Allowed",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // If no adviser and no students, confirm delete
        int confirm = JOptionPane.showConfirmDialog(null,
                "Are you sure you want to delete the class '" + className + "'?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = classesDAOImpl.delete(classId);
            if (success) {
                JOptionPane.showMessageDialog(null, "Class deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshClassesTBL();
            } else {
                JOptionPane.showMessageDialog(null, "Failed to delete class. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(null, "Database error occurred:\n" + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }
    }//GEN-LAST:event_classesDeleteMIActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(AdminGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AdminGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AdminGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AdminGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {

            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton adminRB;
    private javax.swing.JComboBox<String> attendanceEventCB;
    private javax.swing.JComboBox<String> attendanceGradeSectionCB;
    private javax.swing.JComboBox<String> attendanceRemarkCB;
    private javax.swing.JButton attendanceSearchBTN;
    private javax.swing.JTextField attendanceSearchTF;
    private javax.swing.JTable attendanceTBL;
    private javax.swing.JMenuItem classesDeleteMI;
    private javax.swing.JMenuItem classesEditMI;
    private javax.swing.JPopupMenu classesPUM;
    private javax.swing.JButton classesSearchBTN;
    private javax.swing.JTextField classesSearchTF;
    private javax.swing.JComboBox<String> classesSortCB;
    private javax.swing.JTable classesTBL;
    private javax.swing.JMenuItem classesViewStudents;
    private javax.swing.JComboBox<String> eventsCB;
    private com.toedter.calendar.JDateChooser eventsDC;
    private javax.swing.JMenuItem eventsDeleteMI;
    private javax.swing.JTextPane eventsDescriptionTP;
    private javax.swing.JButton eventsDiscardBTN;
    private javax.swing.JMenuItem eventsEditMI;
    private javax.swing.JComboBox<String> eventsEndCB;
    private javax.swing.JComboBox<String> eventsLocationCB;
    private javax.swing.JTextField eventsNameTF;
    private javax.swing.JPopupMenu eventsPUM;
    private javax.swing.JButton eventsSaveBTN;
    private javax.swing.JButton eventsSearchBTN;
    private javax.swing.JTextField eventsSearchTF;
    private javax.swing.JComboBox<String> eventsStartCB;
    private javax.swing.JTable eventsTBL;
    private javax.swing.JRadioButton femaleRB;
    private javax.swing.JMenuItem finalizeAttendanceMI;
    private javax.swing.ButtonGroup genderBG;
    private javax.swing.JLabel greetLBL;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel26;
    private javax.swing.JPanel jPanel27;
    private javax.swing.JPanel jPanel28;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollBar jScrollBar1;
    private javax.swing.JScrollBar jScrollBar2;
    private javax.swing.JScrollBar jScrollBar3;
    private javax.swing.JScrollBar jScrollBar4;
    private javax.swing.JScrollBar jScrollBar5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JMenuItem logOutMI;
    private javax.swing.JRadioButton maleRB;
    private javax.swing.ButtonGroup roleBG;
    private javax.swing.JCheckBox showCB;
    private javax.swing.JRadioButton studentRB;
    private javax.swing.JComboBox<String> studentsCB;
    private javax.swing.JMenuItem studentsDeleteMI;
    private javax.swing.JMenuItem studentsEditMI;
    private javax.swing.JPopupMenu studentsPUM;
    private javax.swing.JButton studentsSearchBTN;
    private javax.swing.JTextField studentsSearchTF;
    private javax.swing.JTable studentsTBL;
    private javax.swing.JRadioButton teacherRB;
    private javax.swing.JComboBox<String> teachersCB;
    private javax.swing.JMenuItem teachersDeleteMI;
    private javax.swing.JMenuItem teachersEditMI;
    private javax.swing.JPopupMenu teachersPUM;
    private javax.swing.JButton teachersSearchBTN;
    private javax.swing.JTextField teachersSearchTF;
    private javax.swing.JTable teachersTBL;
    private javax.swing.JTable teachersTBL1;
    private javax.swing.JMenuItem usersDeleteMI;
    private javax.swing.JButton usersDiscardBTN;
    private javax.swing.JMenuItem usersEditMI;
    private javax.swing.JTextField usersEmailTF;
    private javax.swing.JTextField usersFnTF;
    private javax.swing.JTextField usersLnTF;
    private javax.swing.JPopupMenu usersPUM;
    private javax.swing.JPasswordField usersPasswordTF;
    private javax.swing.JButton usersSaveBTN;
    private javax.swing.JButton usersSearchBTN;
    private javax.swing.JTextField usersSearchTF;
    private javax.swing.JComboBox<String> usersSorterCB;
    private javax.swing.JTable usersTBL;
    private javax.swing.JTextField usersUsernameTF;
    // End of variables declaration//GEN-END:variables
}
