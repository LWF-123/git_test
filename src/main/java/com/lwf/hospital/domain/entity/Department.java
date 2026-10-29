package com.lwf.hospital.domain.entity;

/**
 * 科室。
 *
 * @author LWF-123
 * @since 1.0.0
 */
public class Department {

    private Long id;
    private String deptCode;
    private String name;
    private String location;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeptCode() {
        return deptCode;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return name + "(" + deptCode + ")";
    }
}