create database StudentDB;
USE StudentDB;
-- Bảng 1: Student
CREATE TABLE Student (
    student_id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    class_id VARCHAR(50),
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);
-- Bảng 2: Subject
CREATE TABLE Subject (
    subject_id VARCHAR(50) PRIMARY KEY,
    student_id VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    instructor VARCHAR(255) NOT NULL,
    FOREIGN KEY (student_id) REFERENCES Student(student_id)
);
-- Bảng 3: Assignment
CREATE TABLE Assignment (
    assignment_id VARCHAR(50) PRIMARY KEY,
    subject_id VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    deadline DATETIME,
    status ENUM ('Chưa hoàn thành', 'Hoàn thành') NOT NULL default 'Chưa hoàn thành',
    FOREIGN KEY (subject_id) REFERENCES Subject(subject_id)
);
-- Bảng 4: Note
CREATE TABLE Note (
	note_id VARCHAR(50) PRIMARY KEY,
    subject_id VARCHAR(50) NOT NULL,
    title VARCHAR(255),
    content TEXT,
    creation_date DATETIME,
    FOREIGN KEY (subject_id) REFERENCES Subject(subject_id)
);
-- Bảng 5: Schedule
CREATE TABLE Schedule (
    schedule_id VARCHAR(50) PRIMARY KEY,
    student_id VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    FOREIGN KEY (student_id) REFERENCES Student(student_id)
);
-- Bảng 6: Event
CREATE TABLE Event (
    event_id VARCHAR(50) PRIMARY KEY,
    schedule_id VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    time DATETIME,
    FOREIGN KEY (schedule_id) REFERENCES Schedule(schedule_id)
);

-- Chèn dữ liệu 
-- Bảng 1: Student
INSERT INTO Student (student_id, name, class_id, email, password) 
VALUES
('ST006', 'Nguyễn Thị Nhung', 'B23CQCE04', 'ntn27112005@gmail.com', 'nhung@123');

INSERT INTO Subject (subject_id, student_id, name, instructor) 
VALUES
('SB001', 'ST006', 'Kinh tế chính trị', 'Đinh Mạnh Ninh'), 
('SB002', 'ST006', 'Cơ sở an toàn thông tin', 'Đỗ Xuân Chợ'),
('SB003', 'ST006', 'Cơ sở dữ liệu', 'Nguyễn Trọng Khánh'),
('SB004', 'ST006', 'Hệ điều hành', 'Trần Tiến Công'),
('SB005', 'ST006', 'Mạng máy tính', 'Dương Trần Đức'),
('SB006', 'ST006', 'Lập trình hướng đối tượng', 'Đỗ Thị Liên');

INSERT INTO Assignment (assignment_id, subject_id, name, deadline) VALUES
('AS001', 'SB002', 'Bài tập lớn chủ đề Blockchain', '2025-12-3 07:00:00'),
('AS002', 'SB002', 'Thuật toán DES', '2025-12-1 08:00:00'),

('AS003', 'SB003', 'Đại số quan hệ', '2025-12-02 09:00:00'),
('AS004', 'SB003', 'Bài tập cá nhân ERD', '2025-12-03 10:00:00'),
('AS005', 'SB003', 'Bài tập ERD 2', '2025-12-04 11:00:00'),
('AS006', 'SB003', 'Phụ thuộc hàm, bao đóng và khoá', '2025-12-05 12:00:00'),
('AS007', 'SB003', 'Bài tập chuẩn hoá', '2025-12-06 09:01:31'),

('AS008', 'SB004', 'Lab 01', '2025-12-7 15:37:09'),
('AS009', 'SB004', 'Lab 02', '2025-12-8 20:33:27'),
('AS010', 'SB004', 'Lab 03', '2025-12-9 21:26:45'),
('AS011', 'SB004', 'Lab 04', '2025-12-10 21:07:23'),
('AS012', 'SB004', 'Lab 05', '2025-12-11 20:58:54'),
('AS013', 'SB004', 'BT1', '2025-12-12 05:34:15'),
('AS014', 'SB004', 'BT2', '2025-12-13 15:34:22'),

('AS015', 'SB005', 'Transport Layer Homework', '2025-06-14 14:18:39'),
('AS016', 'SB005', 'HTTP Exercise', '2025-12-11 15:51:04'),
('AS017', 'SB005', 'Application Exercise', '2025-12-12 16:51:09'),
('AS018', 'SB005', 'Network Layer', '2025-12-15 09:25:31'),

('AS019', 'SB006', 'Java cơ bản', '2025-12-7 06:11:33'),
('AS020', 'SB006', 'Java collections và ứng dụng', '2025-12-8 15:16:06'),
('AS021', 'SB006', 'Mảng và thao tác trên mảng', '2025-12-09 10:45:33'),
('AS022', 'SB006', 'Xâu kí tự', '2025-12-10 15:20:49'),
('AS023', 'SB006', 'Đối tượng cơ bản', '2025-12-30 06:37:17'),
('AS024', 'SB006', 'Mảng đối tượng', '2025-12-31 08:57:37'),
('AS025', 'SB006', 'Quan hệ giữa các lớp', '2025-12-1 17:57:37'),
('AS026', 'SB006', 'Vào ra file', '2025-12-03 12:29:17'),
('AS027', 'SB006', 'Bài tập lớn OOP', '2025-12-06 09:16:37');

INSERT INTO Note (note_id, subject_id, title, content, creation_date) 
VALUES
('NT001', 'SB001', 'Ghi chú 1', 'Kinh tế tư bản chủ nghĩa', '2025-11-23 12:23:56'),
('NT002', 'SB001', 'Ghi chú 2', 'Nền kinh tế hiện tại', '2025-09-21 10:00:00'),

('NT003', 'SB003', 'Ghi chú 3', 'Lệnh Join', '2025-10-21 12:00:00'),
('NT004', 'SB003', 'Ghi chú 4', 'Lược đồ mối quan hệ', '2025-11-01 10:00:00'),

('NT005', 'SB004', 'Ghi chú 5', 'Các cách phân trang', '2025-10-21 11:05:00'),
('NT006', 'SB004', 'Ghi chú 6', 'Hệ thống Fat 32', '2025-11-13 11:05:56'),

('NT007', 'SB005', 'Ghi chú 7', 'Các tầng giao thức vận chuyển', '2025-09-13 11:05:56'),

('NT008', 'SB002', 'Ghi chú 8', 'Tường lửa', '2025-10-14 09:05:56'),

('NT009', 'SB006', 'Ghi chú 9', 'File, Exception', '2025-11-25 10:05:56'),
('NT010', 'SB006', 'Ghi chú 10', 'Phương thức get, set', '2025-10-25 09:05:56');

-- Bảng 5: Schedule
INSERT INTO Schedule (schedule_id, student_id, name) VALUES 
('SC001', 'ST006', 'Thời khóa biểu Tuần 1 - Kỳ 1'),
('SC002', 'ST006', 'Thời khóa biểu Tuần 2 - Kỳ 1'),
('SC003', 'ST006', 'Thời khóa biểu Tuần 3 - Kỳ 1'),
('SC004', 'ST006', 'Thời khóa biểu Thực hành lab 1'),
('SC005', 'ST006', 'Thời khóa biểu Tuần 4 - Kỳ 1'),
('SC006', 'ST006', 'Thời khóa biểu Tuần 5 - Kỳ 1'),
('SC007', 'ST006', 'Thời khóa biểu Thực hành lab 2'),
('SC008', 'ST006', 'Thời khóa biểu Thực hành lab 3');

-- Bảng 6: Event
INSERT INTO Event (event_id, schedule_id, name, time) VALUES
('EV001', 'SC001', 'Thuyết trình bài tập lớn', '2025-11-10 08:00:00'),
('EV002', 'SC002', 'Thuyết trình bài tập lớn CSATTT', '2025-12-03 08:00:00'),
('EV003', 'SC003', 'Kiểm tra giữa kỳ', '2025-11-03 10:00:00'),
('EV004', 'SC004', 'Kiểm tra Chương 3', '2025-10-03 08:00:00'),
('EV005', 'SC005', 'Học bù môn Mạng máy tính', '2025-12-04 10:00:00'),
('EV006', 'SC006', 'Vấn đáp OOP', '2025-12-05 09:15:50'),
('EV007', 'SC007', 'Kiểm tra chương 2', '2025-10-04 10:00:00'),
('EV008', 'SC008', 'Vấn đáp CSDL', '2025-12-04 11:50:50'),
('EV009', 'SC008', 'Kiểm tra chương 1', '2025-10-04 08:50:30'),
('EV010', 'SC008', 'Kiểm tra Mạng máy tính', '2025-12-01 09:50:00');


