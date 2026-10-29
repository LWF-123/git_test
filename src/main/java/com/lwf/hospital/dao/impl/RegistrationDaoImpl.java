package com.lwf.hospital.dao.impl;

import com.lwf.hospital.dao.JdbcHelper;
import com.lwf.hospital.dao.RegistrationDao;
import com.lwf.hospital.domain.dto.RegistrationView;
import com.lwf.hospital.domain.entity.Registration;
import com.lwf.hospital.domain.enums.RegistrationType;
import com.lwf.hospital.domain.enums.TicketStatus;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * 挂号单 DAO 的 JDBC 实现。
 *
 * <p>列表查询统一走 {@link #REGISTRATION_VIEW} 这段 join SQL，保证界面拿到的字段一致。</p>
 *
 * @author LWF-123
 * @since 1.0.0
 */
public class RegistrationDaoImpl implements RegistrationDao {

    private static final String REGISTRATION_VIEW = """
            SELECT r.id, p.name AS patient_name, dep.name AS dept_name, d.name AS doctor_name,
                   s.work_date, s.period, r.reg_type, r.queue_no, r.status, r.fee
            FROM registration r
            JOIN patient p ON p.id = r.patient_id
            JOIN doctor_schedule s ON s.id = r.schedule_id
            JOIN doctor d ON d.id = s.doctor_id
            JOIN department dep ON dep.id = d.department_id
            """;

    private final JdbcHelper jdbc;

    public RegistrationDaoImpl(JdbcHelper jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Optional<Registration> findActive(Connection connection, long patientId, long scheduleId) {
        return jdbc.queryOne(connection, """
                        SELECT id, patient_id, schedule_id, reg_type, queue_no, priority, fee, status, created_at
                        FROM registration
                        WHERE patient_id = ? AND schedule_id = ? AND status IN ('WAITING', 'CALLING')
                        LIMIT 1
                        """,
                rs -> {
                    Registration registration = new Registration();
                    registration.setId(rs.getLong("id"));
                    registration.setPatientId(rs.getLong("patient_id"));
                    registration.setScheduleId(rs.getLong("schedule_id"));
                    registration.setType(RegistrationType.fromCode(rs.getString("reg_type")));
                    registration.setQueueNo(rs.getInt("queue_no"));
                    registration.setPriority(rs.getInt("priority"));
                    registration.setFee(rs.getBigDecimal("fee"));
                    registration.setStatus(TicketStatus.fromCode(rs.getString("status")));
                    registration.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    return registration;
                }, patientId, scheduleId);
    }

    @Override
    public int nextQueueNo(Connection connection, long scheduleId) {
        return jdbc.queryOne(connection,
                "SELECT COALESCE(MAX(queue_no), 0) + 1 AS next_no FROM registration WHERE schedule_id = ?",
                rs -> rs.getInt("next_no"), scheduleId).orElse(1);
    }

    @Override
    public long insert(Connection connection, long patientId, long scheduleId, RegistrationType type, int queueNo) {
        return jdbc.insert(connection, """
                        INSERT INTO registration (patient_id, schedule_id, reg_type, queue_no, priority, fee, status)
                        VALUES (?, ?, ?, ?, ?, ?, 'WAITING')
                        """,
                patientId, scheduleId, type.getCode(), queueNo, type.getPriority(), type.getFee());
    }

    @Override
    public int updateStatus(Connection connection, long registrationId, TicketStatus status) {
        return jdbc.update(connection, "UPDATE registration SET status = ? WHERE id = ?",
                status.getCode(), registrationId);
    }

    @Override
    public List<RegistrationView> findByPatient(long patientId) {
        return jdbc.query(REGISTRATION_VIEW + "WHERE r.patient_id = ? ORDER BY r.created_at DESC",
                RegistrationDaoImpl::mapView, patientId);
    }

    @Override
    public List<RegistrationView> findQueue(long scheduleId) {
        return jdbc.query(REGISTRATION_VIEW
                        + "WHERE r.schedule_id = ? AND r.status IN ('WAITING', 'CALLING') "
                        + "ORDER BY r.priority, r.queue_no",
                RegistrationDaoImpl::mapView, scheduleId);
    }

    @Override
    public Optional<RegistrationView> findView(long registrationId) {
        return jdbc.queryOne(REGISTRATION_VIEW + "WHERE r.id = ?", RegistrationDaoImpl::mapView, registrationId);
    }

    private static RegistrationView mapView(ResultSet rs) throws SQLException {
        return new RegistrationView(
                rs.getLong("id"),
                rs.getString("patient_name"),
                rs.getString("dept_name"),
                rs.getString("doctor_name"),
                rs.getDate("work_date").toLocalDate(),
                rs.getString("period"),
                RegistrationType.fromCode(rs.getString("reg_type")),
                rs.getInt("queue_no"),
                TicketStatus.fromCode(rs.getString("status")),
                rs.getBigDecimal("fee"));
    }
}