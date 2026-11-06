package com.lwf.hospital.dao.impl;

import com.lwf.hospital.dao.DoctorDao;
import com.lwf.hospital.dao.JdbcHelper;
import com.lwf.hospital.domain.dto.DoctorView;
import com.lwf.hospital.domain.entity.Doctor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * 医生 DAO 的 JDBC 实现。
 *
 * @author LWF-123
 * @since 1.0.0
 */
public class DoctorDaoImpl implements DoctorDao {

    private static final String VIEW = """
            SELECT d.id, d.doctor_no, d.name, d.title, d.enabled, dep.name AS dept_name
            FROM doctor d
            JOIN department dep ON dep.id = d.department_id
            """;

    private final JdbcHelper jdbc;

    public DoctorDaoImpl(JdbcHelper jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<DoctorView> findAll() {
        return jdbc.query(VIEW + " ORDER BY dep.id, d.doctor_no", DoctorDaoImpl::mapView);
    }

    @Override
    public List<DoctorView> findByDepartment(long departmentId) {
        return jdbc.query(VIEW + " WHERE d.department_id = ? ORDER BY d.doctor_no",
                DoctorDaoImpl::mapView, departmentId);
    }

    @Override
    public Optional<Doctor> findById(long id) {
        return jdbc.queryOne("""
                        SELECT id, doctor_no, name, title, department_id, enabled
                        FROM doctor WHERE id = ?
                        """,
                DoctorDaoImpl::mapEntity, id);
    }

    @Override
    public Optional<Doctor> findByNo(String doctorNo) {
        return jdbc.queryOne("""
                        SELECT id, doctor_no, name, title, department_id, enabled
                        FROM doctor WHERE doctor_no = ?
                        """,
                DoctorDaoImpl::mapEntity, doctorNo);
    }

    @Override
    public long insert(String doctorNo, String name, String title, long departmentId) {
        return jdbc.insert("""
                        INSERT INTO doctor (doctor_no, name, title, department_id, enabled)
                        VALUES (?, ?, ?, ?, 1)
                        """, doctorNo, name, title, departmentId);
    }

    @Override
    public int update(long id, String doctorNo, String name, String title, long departmentId) {
        return jdbc.update("""
                        UPDATE doctor SET doctor_no = ?, name = ?, title = ?, department_id = ?
                        WHERE id = ?
                        """, doctorNo, name, title, departmentId, id);
    }

    @Override
    public int setEnabled(long id, boolean enabled) {
        return jdbc.update("UPDATE doctor SET enabled = ? WHERE id = ?", enabled ? 1 : 0, id);
    }

    private static DoctorView mapView(ResultSet rs) throws SQLException {
        return new DoctorView(
                rs.getLong("id"),
                rs.getString("doctor_no"),
                rs.getString("name"),
                rs.getString("title"),
                rs.getString("dept_name"),
                rs.getInt("enabled") == 1);
    }

    private static Doctor mapEntity(ResultSet rs) throws SQLException {
        Doctor doctor = new Doctor();
        doctor.setId(rs.getLong("id"));
        doctor.setDoctorNo(rs.getString("doctor_no"));
        doctor.setName(rs.getString("name"));
        doctor.setTitle(rs.getString("title"));
        doctor.setDepartmentId(rs.getLong("department_id"));
        doctor.setEnabled(rs.getInt("enabled") == 1);
        return doctor;
    }
}