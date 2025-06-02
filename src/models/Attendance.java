/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

/**
 *
 * @author NelsonJrLHerrera
 */
public class Attendance {

    private int recordId;
    private int eventId;
    private int studentId;
    private String check_in_time;
    private String check_out_time;
    private String remark;

    public Attendance(int recordId, int eventId, int studentId, String check_in_time, String check_out_time, String remark) {
        this.recordId = recordId;
        this.eventId = eventId;
        this.studentId = studentId;
        this.check_in_time = check_in_time;
        this.check_out_time = check_out_time;
        this.remark = remark;
    }

    public int getRecordId() {
        return recordId;
    }

    public int getEventId() {
        return eventId;
    }

    public int getStudentId() {
        return studentId;
    }

    public String getCheck_in_time() {
        return check_in_time;
    }

    public String getCheck_out_time() {
        return check_out_time;
    }

    public String getRemark() {
        return remark;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public void setCheck_in_time(String check_in_time) {
        this.check_in_time = check_in_time;
    }

    public void setCheck_out_time(String check_out_time) {
        this.check_out_time = check_out_time;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
