    /*
     * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
     * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
     */
    package models;

    /**
     *
     * @author NelsonJrLHerrera
     */
    public class Student {

        private int student_id;
        private int user_id;
        private long lrn;
        private int gradeLevel;
        private String section;
        private int class_id;
        private String photoPath;
        private String created_at;
        private String updated_at;

        //Constructor
        public Student(int student_id, int user_id, long lrn, int gradeLevel, String section, int adviser_id, String photoPath, String created_at, String updated_at) {
            this.student_id = student_id;
            this.user_id = user_id;
            this.lrn = lrn;
            this.gradeLevel = gradeLevel;
            this.section = section;
            this.class_id = adviser_id;
            this.photoPath = photoPath;
            this.created_at = created_at;
            this.updated_at = updated_at;
        }

        //Created At and Updated At
        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public void setUpdated_at(String updated_at) {    
            this.updated_at = updated_at;
        }

        public String getCreated_at() {
            return created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        //Getters
        public int getStudent_id() {
            return student_id;
        }

        public int getUser_id() {
            return user_id;
        }

        public long getLrn() {
            return lrn;
        }

        public int getGradeLevel() {
            return gradeLevel;
        }

        public String getSection() {
            return section;
        }

        public int getClass_id() {
            return class_id;
        }

        public String getPhotoPath() {
            return photoPath;
        }

        //Setters
        public void setStudent_id(int student_id) {
            this.student_id = student_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public void setLrn(long lrn) {
            this.lrn = lrn;
        }

        public void setGradeLevel(int gradeLevel) {
            this.gradeLevel = gradeLevel;
        }

        public void setSection(String section) {
           this.section = section;
        }

        public void setClass_id(int class_id) {
            this.class_id = class_id;
        }

        public void setPhotoPath(String photoPath) {
            this.photoPath = photoPath;
        }
    }
