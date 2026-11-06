package com.lwf.hospital.dao.impl;

import com.lwf.hospital.dao.DepartmentDao;
import com.lwf.hospital.dao.JdbcHelper;
import com.lwf.hospital.domain.entity.Department;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * 科室 DAO 的 JDBC 实现。
 *
 * @author LWF-123
 * @since 1.0.0
 */
public class DepartmentDaoImpl implements DepartmentDao {

    private final JdbcHelper jdbc;

    public DepartmentDaoImpl(JdbcHelper jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<Department> findAll() {
        return jdbc.query("SELECT id, dept_code, name, location FROM department ORDER BY id",
                DepartmentDaoImpl::map);
    }

    @Override
    public Optional<Department> findById(long id) {
        return jdbc.queryOne("SELECT id, dept_code, name, location FROM department WHERE id = ?",
                DepartmentDaoImpl::map, id);
    }

    @Override
    public Optional<Department> findByCode(String deptCode) {
        return jdbc.queryOne("SELECT id, dept_code, name, location FROM department WHERE dept_code = ?",
                DepartmentDaoImpl::map, deptCode);
    }

    @Override
    public long insert(String deptCode, String name, String location) {
        return jdbc.insert("INSERT INTO department (dept_code, name, location) VALUES (?, ?, ?)",
                deptCode, name, location);
    }

    @Override
    public int update(long id, String deptCode, String name, String location) {
        return jdbc.update("UPDATE department SET dept_code = ?, name = ?, location = ? WHERE id = ?",
                deptCode, name, location, id);
    }

    @Override
    public int delete(long id) {
        return jdbc.update("DELETE FROM department WHERE id = ?", id);
    }

    @Override
    public int countDoctors(long departmentId) {
        return jdbc.queryOne("SELECT COUNT(*) AS cnt FROM doctor WHERE department_id = ?",
                rs -> rs.getInt("cnt"), departmentId).orElse(0);
    }

    private static Department map(ResultSet rs) throws SQLException {
        Department department = new Department();
        department.setId(rs.getLong("id"));
        department.setDeptCode(rs.getString("dept_code"));
        department.setName(rs.getString("name"));
        department.setLocation(rs.getString("location"));
        return department;
    }
}