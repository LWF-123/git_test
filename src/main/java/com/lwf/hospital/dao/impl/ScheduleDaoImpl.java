package com.lwf.hospital.dao.impl;

import com.lwf.hospital.dao.JdbcHelper;
import com.lwf.hospital.dao.ScheduleDao;
import com.lwf.hospital.domain.dto.ScheduleView;
import com.lwf.hospital.domain.entity.Department;
import com.lwf.hospital.domain.entity.Doctor;
import com.lwf.hospital.domain.entity.DoctorSchedule;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 科室 / 医生 / 排班 DAO 的 JDBC 实现。
 *
 * @author LWF-123
 * @since 1.0.0
 */
public class ScheduleDaoImpl implements ScheduleDao {

    private static final String SCHEDULE_VIEW = """
            SELECT s.id AS schedule_id, d.name AS doctor_name, d.title AS doctor_title,
                   dep.name AS dept_name, s.work_date, s.period,
                   s.quota_total, s.quota_used, s.status
            FROM doctor_schedule s
            JOIN doctor d ON d.id = s.doctor_id
            JOIN department dep ON dep.id = d.department_id
            """;

    private final JdbcHelper jdbc;

    public ScheduleDaoImpl(JdbcHelper jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<Department> findDepartments() {
        return jdbc.query("SELECT id, dept_code, name, location FROM department ORDER BY id",
                rs -> {
                    Department department = new Department();
                    department.setId(rs.getLong("id"));
                    department.setDeptCode(rs.getString("dept_code"));
                    department.setName(rs.getString("name"));
                    department.setLocation(rs.getString("location"));
                    return department;
                });
    }

    @Override
    public List<Doctor> findDoctorsByDepartment(long departmentId) {
        return jdbc.query("""
                        SELECT id, doctor_no, name, title, department_id
                        FROM doctor WHERE department_id = ? ORDER BY doctor_no
                        """,
                rs -> {
                    Doctor doctor = new Doctor();
                    doctor.setId(rs.getLong("id"));
                    doctor.setDoctorNo(rs.getString("doctor_no"));
                    doctor.setName(rs.getString("name"));
                    doctor.setTitle(rs.getString("title"));
                    doctor.setDepartmentId(rs.getLong("department_id"));
                    return doctor;
                }, departmentId);
    }

    @Override
    public List<ScheduleView> findAvailable(long departmentId) {
        String condition = "WHERE s.status = 'OPEN' AND s.work_date >= CURDATE() ";
        if (departmentId > 0) {
            condition += "AND dep.id = ? ";
            return jdbc.query(SCHEDULE_VIEW + condition
                    + "ORDER BY s.work_date, dep.name, s.period, d.name",
                    ScheduleDaoImpl::mapView, departmentId);
        }
        return jdbc.query(SCHEDULE_VIEW + condition + "ORDER BY s.work_date, dep.name, s.period, d.name",
                ScheduleDaoImpl::mapView);
    }

    @Override
    public List<ScheduleView> findByDoctorAndDate(long doctorId, LocalDate date) {
        return jdbc.query(SCHEDULE_VIEW + "WHERE d.id = ? AND s.work_date = ? ORDER BY s.period",
                ScheduleDaoImpl::mapView, doctorId, date);
    }

    @Override
    public Optional<DoctorSchedule> lockForUpdate(Connection connection, long scheduleId) {
        return jdbc.queryOne(connection, """
                        SELECT id, doctor_id, work_date, period, quota_total, quota_used, status
                        FROM doctor_schedule WHERE id = ? FOR UPDATE
                        """,
                ScheduleDaoImpl::mapSchedule, scheduleId);
    }

    @Override
    public int tryIncreaseQuota(Connection connection, long scheduleId) {
        return jdbc.update(connection, """
                        UPDATE doctor_schedule SET quota_used = quota_used + 1
                        WHERE id = ? AND quota_used < quota_total AND status = 'OPEN'
                        """, scheduleId);
    }

    private static ScheduleView mapView(ResultSet rs) throws SQLException {
        return new ScheduleView(
                rs.getLong("schedule_id"),
                rs.getString("doctor_name"),
                rs.getString("doctor_title"),
                rs.getString("dept_name"),
                rs.getDate("work_date").toLocalDate(),
                rs.getString("period"),
                rs.getInt("quota_total"),
                rs.getInt("quota_used"),
                rs.getString("status"));
    }

    private static DoctorSchedule mapSchedule(ResultSet rs) throws SQLException {
        DoctorSchedule schedule = new DoctorSchedule();
        schedule.setId(rs.getLong("id"));
        schedule.setDoctorId(rs.getLong("doctor_id"));
        schedule.setWorkDate(rs.getDate("work_date").toLocalDate());
        schedule.setPeriod(rs.getString("period"));
        schedule.setQuotaTotal(rs.getInt("quota_total"));
        schedule.setQuotaUsed(rs.getInt("quota_used"));
        schedule.setStatus(rs.getString("status"));
        return schedule;
    }
}