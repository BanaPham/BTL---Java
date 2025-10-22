CREATE DATABASE StudentDB;
USE StudentDB;
-- Bảng 1: Student
CREATE TABLE Student (
    student_id VARCHAR(10) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    class_id VARCHAR(10),
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL
);
-- Bảng 2: Subject
CREATE TABLE Subject (
    subject_id VARCHAR(10) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    instructor VARCHAR(100) NOT NULL 
);
-- Bảng 3: Assignment
CREATE TABLE Assignment (
    assignment_id VARCHAR(10) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    deadline DATE,
    status VARCHAR(20) CHECK (status IN ('COMPLETED', 'PENDING')), -- Ràng buộc trạng thái
    subject_id VARCHAR(10),
    FOREIGN KEY (subject_id) REFERENCES Subject(subject_id)
);
-- Bảng 4: Note
CREATE TABLE Note (
    note_id VARCHAR(10) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    creation_date DATE,
    subject_id VARCHAR(10),
    FOREIGN KEY (subject_id) REFERENCES Subject(subject_id)
);
-- Bảng 5: Schedule
CREATE TABLE Schedule (
    schedule_id INT PRIMARY KEY AUTO_INCREMENT, 
    name VARCHAR(255) NOT NULL,
    subject_id VARCHAR(10),
    FOREIGN KEY (subject_id) REFERENCES Subject(subject_id)
);
-- Bảng 6: Event
CREATE TABLE Event (
    event_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    time DATETIME,
    schedule_id INT,
    FOREIGN KEY (schedule_id) REFERENCES Schedule(schedule_id)
);
-- Bảng 7: Bảng Quan hệ Student_Subject
CREATE TABLE Student_Subject (
    student_id VARCHAR(10),
    subject_id VARCHAR(10),
    PRIMARY KEY (student_id, subject_id),
    FOREIGN KEY (student_id) REFERENCES Student(student_id),
    FOREIGN KEY (subject_id) REFERENCES Subject(subject_id)
);
-- Bảng 8: Bảng Quan hệ Student_Schedule
CREATE TABLE Student_Schedule (
    student_id VARCHAR(10),
    schedule_id INT,
    PRIMARY KEY (student_id, schedule_id),
    FOREIGN KEY (student_id) REFERENCES Student(student_id),
    FOREIGN KEY (schedule_id) REFERENCES Schedule(schedule_id)
);