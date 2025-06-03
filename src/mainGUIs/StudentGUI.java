/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package mainGUIs;

import implementations.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import models.*;

/**
 *
 * @author NelsonJrLHerrera
 */
public class StudentGUI extends javax.swing.JFrame {
    TeacherDAOImpl teacherImpl = new TeacherDAOImpl();
    AttendanceDAOImpl attendanceDAOImpl = new AttendanceDAOImpl();
    StudentDAOImpl studentDAOImpl = new StudentDAOImpl();
    UserDAOImpl userDAOImpl = new UserDAOImpl();
    ClassesDAOImpl classDAOImpl = new ClassesDAOImpl();
    EventDAOImpl eventDAOImpl = new EventDAOImpl();
    private final User user;
    private Timer refreshTimer;

    /**
     * Creates new form StudentGUI
     */
    public StudentGUI(User user) {
        this.user = user;
        initComponents();
        greetUser();
        startAutoRefresh();
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

    public void stopAutoRefresh() {
        if (refreshTimer != null && refreshTimer.isRunning()) {
            refreshTimer.stop();
        }
    }
    
    public void greetUser() {
        greetLBL.setText("Welcome to EAM-System " + this.user.getFirstname() + " " + this.user.getLastname());
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

        // Format helper
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
                ex.printStackTrace(); // fallback: use original time values
            }

            Object[] rowData = {
                e.getEventName(),
                e.getDescription(),
                e.getVenue(),
                e.getDate(),
                formattedStart,
                formattedEnd,
                e.getStatus()
            };
            model.addRow(rowData);
        }
    }

    public void refreshAdviserTBL() {
        DefaultTableModel model = (DefaultTableModel) adviserTBL.getModel();
        model.setRowCount(0); // Clear previous rows

        // Step 1: Get student record from user_id
        Student student = studentDAOImpl.getStudentByUserId(user.getUserId());
        if (student == null) {
            JOptionPane.showMessageDialog(this, "Student record not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Step 2: Get class record from class_id
        Classes studentClass = classDAOImpl.read_one(student.getClass_id());
        if (studentClass == null) {
            JOptionPane.showMessageDialog(this, "Class record not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Step 3: Get Teacher record from adviser_id
        Teacher adviserTeacher = teacherImpl.getTeacherById(studentClass.getAdviser_id());
        if (adviserTeacher == null) {
            JOptionPane.showMessageDialog(this, "Adviser teacher record not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

// Step 4: Get User record from teacher.user_id
        User adviser = userDAOImpl.getUserById(adviserTeacher.getUserId());
        if (adviser == null) {
            JOptionPane.showMessageDialog(this, "Adviser user record not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Step 5: Populate table with adviser's info
        String adviserName = adviser.getLastname() + ", " + adviser.getFirstname();
        String gradeSection = studentClass.getGrade() + " - " + studentClass.getSection();
        model.addRow(new Object[]{adviserName, gradeSection});
    }

    public void refreshAttendanceTBL() {
        // Retrieve the logged-in student's record
        Student student = studentDAOImpl.getStudentByUserId(user.getUserId());
        if (student == null) {
            JOptionPane.showMessageDialog(this, "Student not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Clear existing rows
        DefaultTableModel model = (DefaultTableModel) attendanceTBL.getModel();
        model.setRowCount(0);

        // Fetch attendance records for the student
        List<Attendance> attendances = attendanceDAOImpl.getAttendanceByStudentId(student.getStudent_id());
        if (attendances == null || attendances.isEmpty()) {
            return;
        }

        // Fetch all events once and map them by event_id for quick access
        List<Event> allEvents = eventDAOImpl.readAllEvents();
        Map<Integer, Event> eventMap = new HashMap<>();
        for (Event event : allEvents) {
            eventMap.put(event.getEventId(), event);
        }

        // Populate the table with joined data
        for (Attendance attendance : attendances) {
            Event event = eventMap.get(attendance.getEventId());
            if (event == null) {
                continue; // Skip if the event info is missing
            }
            Object[] row = {
                event.getEventName(), // Event
                event.getDate(), // Date
                attendance.getCheck_in_time(), // Check-In (from attendance)
                attendance.getCheck_out_time(), // Check-Out (from attendance)
                eventDAOImpl.determineStatus(event.getDate(), event.getEndTime()), // Status (from events table)
                attendance.getRemark() // Remark (from attendance)
            };

            model.addRow(row);
        }
    }

    public void refreshTables() {
        refreshEventsTBL();
        refreshAdviserTBL();
        refreshAttendanceTBL();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuItem3 = new javax.swing.JMenuItem();
        jDesktopPane1 = new javax.swing.JDesktopPane();
        Dashboard = new javax.swing.JTabbedPane();
        jDesktopPane3 = new javax.swing.JDesktopPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        attendanceTBL = new javax.swing.JTable();
        eventsSearchTF = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jDesktopPane4 = new javax.swing.JDesktopPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        adviserTBL = new javax.swing.JTable();
        eventsSearchTF1 = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jDesktopPane2 = new javax.swing.JDesktopPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        eventsTBL = new javax.swing.JTable();
        eventsSearchTF2 = new javax.swing.JTextField();
        jButton3 = new javax.swing.JButton();
        jComboBox1 = new javax.swing.JComboBox<>();
        greetLBL = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        logOutMI = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();

        jMenuItem3.setText("jMenuItem3");

        javax.swing.GroupLayout jDesktopPane1Layout = new javax.swing.GroupLayout(jDesktopPane1);
        jDesktopPane1.setLayout(jDesktopPane1Layout);
        jDesktopPane1Layout.setHorizontalGroup(
            jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 605, Short.MAX_VALUE)
        );
        jDesktopPane1Layout.setVerticalGroup(
            jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 423, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        Dashboard.setBackground(new java.awt.Color(153, 255, 153));
        Dashboard.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        Dashboard.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        Dashboard.setTabPlacement(javax.swing.JTabbedPane.LEFT);

        jDesktopPane3.setBackground(new java.awt.Color(31, 125, 83));

        attendanceTBL.setBackground(new java.awt.Color(204, 255, 204));
        attendanceTBL.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Event", "Date", "Check-In", "Check-Out", "Status", "Remark"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(attendanceTBL);

        eventsSearchTF.setBackground(new java.awt.Color(204, 255, 204));
        eventsSearchTF.setText("Search:");
        eventsSearchTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eventsSearchTFActionPerformed(evt);
            }
        });

        jButton1.setBackground(new java.awt.Color(153, 255, 153));
        jButton1.setText("Search");

        jDesktopPane3.setLayer(jScrollPane2, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane3.setLayer(eventsSearchTF, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane3.setLayer(jButton1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jDesktopPane3Layout = new javax.swing.GroupLayout(jDesktopPane3);
        jDesktopPane3.setLayout(jDesktopPane3Layout);
        jDesktopPane3Layout.setHorizontalGroup(
            jDesktopPane3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDesktopPane3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jDesktopPane3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1178, Short.MAX_VALUE)
                    .addGroup(jDesktopPane3Layout.createSequentialGroup()
                        .addComponent(eventsSearchTF, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jDesktopPane3Layout.setVerticalGroup(
            jDesktopPane3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jDesktopPane3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jDesktopPane3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(eventsSearchTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1104, Short.MAX_VALUE)
                .addContainerGap())
        );

        Dashboard.addTab("Attendance", jDesktopPane3);

        jDesktopPane4.setBackground(new java.awt.Color(31, 125, 83));

        adviserTBL.setBackground(new java.awt.Color(204, 255, 204));
        adviserTBL.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Teacher", "Grade & Section"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(adviserTBL);

        eventsSearchTF1.setBackground(new java.awt.Color(204, 255, 204));
        eventsSearchTF1.setText("Search:");
        eventsSearchTF1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eventsSearchTF1ActionPerformed(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(153, 255, 153));
        jButton2.setText("Search");

        jDesktopPane4.setLayer(jScrollPane3, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane4.setLayer(eventsSearchTF1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane4.setLayer(jButton2, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jDesktopPane4Layout = new javax.swing.GroupLayout(jDesktopPane4);
        jDesktopPane4.setLayout(jDesktopPane4Layout);
        jDesktopPane4Layout.setHorizontalGroup(
            jDesktopPane4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDesktopPane4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jDesktopPane4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 1178, Short.MAX_VALUE)
                    .addGroup(jDesktopPane4Layout.createSequentialGroup()
                        .addComponent(eventsSearchTF1, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jDesktopPane4Layout.setVerticalGroup(
            jDesktopPane4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jDesktopPane4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jDesktopPane4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(eventsSearchTF1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 1104, Short.MAX_VALUE)
                .addContainerGap())
        );

        Dashboard.addTab("Adviser", jDesktopPane4);

        jDesktopPane2.setBackground(new java.awt.Color(31, 125, 83));

        eventsTBL.setBackground(new java.awt.Color(204, 255, 204));
        eventsTBL.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        eventsTBL.setForeground(new java.awt.Color(255, 255, 255));
        eventsTBL.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Event", "Description", "Location", "Date", "Start Time", "End Time", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, false, false, false, true, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(eventsTBL);

        eventsSearchTF2.setBackground(new java.awt.Color(204, 255, 204));
        eventsSearchTF2.setText("Search:");
        eventsSearchTF2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eventsSearchTF2ActionPerformed(evt);
            }
        });

        jButton3.setBackground(new java.awt.Color(153, 255, 153));
        jButton3.setText("Search");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jComboBox1.setBackground(new java.awt.Color(153, 255, 153));
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jDesktopPane2.setLayer(jScrollPane1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane2.setLayer(eventsSearchTF2, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane2.setLayer(jButton3, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane2.setLayer(jComboBox1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jDesktopPane2Layout = new javax.swing.GroupLayout(jDesktopPane2);
        jDesktopPane2.setLayout(jDesktopPane2Layout);
        jDesktopPane2Layout.setHorizontalGroup(
            jDesktopPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDesktopPane2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jDesktopPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1178, Short.MAX_VALUE)
                    .addGroup(jDesktopPane2Layout.createSequentialGroup()
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(eventsSearchTF2, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jDesktopPane2Layout.setVerticalGroup(
            jDesktopPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jDesktopPane2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jDesktopPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(eventsSearchTF2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        Dashboard.addTab("Events", jDesktopPane2);

        greetLBL.setBackground(new java.awt.Color(31, 125, 83));
        greetLBL.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        greetLBL.setText("jLabel1");

        jMenuBar1.setBackground(new java.awt.Color(144, 198, 124));
        jMenuBar1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jMenu1.setBackground(new java.awt.Color(20, 30, 20));
        jMenu1.setText("Settings");

        logOutMI.setBackground(new java.awt.Color(245, 245, 245));
        logOutMI.setText("Log out");
        logOutMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logOutMIActionPerformed(evt);
            }
        });
        jMenu1.add(logOutMI);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Others");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(Dashboard)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(greetLBL, javax.swing.GroupLayout.PREFERRED_SIZE, 637, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(greetLBL)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Dashboard, javax.swing.GroupLayout.PREFERRED_SIZE, 1149, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

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

    private void eventsSearchTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eventsSearchTFActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_eventsSearchTFActionPerformed

    private void eventsSearchTF1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eventsSearchTF1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_eventsSearchTF1ActionPerformed

    private void eventsSearchTF2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eventsSearchTF2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_eventsSearchTF2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

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
            java.util.logging.Logger.getLogger(StudentGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(StudentGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(StudentGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(StudentGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane Dashboard;
    private javax.swing.JTable adviserTBL;
    private javax.swing.JTable attendanceTBL;
    private javax.swing.JTextField eventsSearchTF;
    private javax.swing.JTextField eventsSearchTF1;
    private javax.swing.JTextField eventsSearchTF2;
    private javax.swing.JTable eventsTBL;
    private javax.swing.JLabel greetLBL;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JDesktopPane jDesktopPane1;
    private javax.swing.JDesktopPane jDesktopPane2;
    private javax.swing.JDesktopPane jDesktopPane3;
    private javax.swing.JDesktopPane jDesktopPane4;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JMenuItem logOutMI;
    // End of variables declaration//GEN-END:variables
}
