/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.util.Date;

/**
 *
 * @author NelsonJrLHerrera
 */
public class AttendanceDetail {
    private int recordId;
    private int eventId;
    private String eventName;
    private int studentId;
    private String studentName;
    private String gradeSection;
    private String adviserName;
    private Date date;
    private String checkInTime;
    private String checkOutTime;
    private String remark;

    public AttendanceDetail(int recordId, int eventId, String eventName, int studentId, String studentName, String gradeSection, String adviserName, Date date, String checkInTime, String checkOutTime, String remark) {
        this.recordId = recordId;
        this.eventId = eventId;
        this.eventName = eventName;
        this.studentId = studentId;
        this.studentName = studentName;
        this.gradeSection = gradeSection;
        this.adviserName = adviserName;
        this.date = date;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.remark = remark;
    }

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getGradeSection() {
        return gradeSection;
    }

    public void setGradeSection(String gradeSection) {
        this.gradeSection = gradeSection;
    }

    public String getAdviserName() {
        return adviserName;
    }

    public void setAdviserName(String adviserName) {
        this.adviserName = adviserName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(String checkInTime) {
        this.checkInTime = checkInTime;
    }

    public String getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(String checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
    
    
}
