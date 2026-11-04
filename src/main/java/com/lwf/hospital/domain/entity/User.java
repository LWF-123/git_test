package com.lwf.hospital.domain.entity;

import com.lwf.hospital.domain.enums.RoleType;

import java.time.LocalDateTime;

/**
 * 系统用户（登录凭据 + 角色 + 业务主体引用 + 登录风控状态）。
 *
 * @author LWF-123
 * @since 1.0.0
 */
public class User {

    private Long id;
    private String username;
    private String passwordHash;
    private String salt;
    private RoleType role;
    private Long refId;
    private String realName;
    private boolean enabled;
    private int failedAttempts;
    private LocalDateTime lockedUntil;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public RoleType getRole() {
        return role;
    }

    public void setRole(RoleType role) {
        this.role = role;
    }

    public Long getRefId() {
        return refId;
    }

    public void setRefId(Long refId) {
        this.refId = refId;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getFailedAttempts() {
        return failedAttempts;
    }

    public void setFailedAttempts(int failedAttempts) {
        this.failedAttempts = failedAttempts;
    }

    public LocalDateTime getLockedUntil() {
        return lockedUntil;
    }

    public void setLockedUntil(LocalDateTime lockedUntil) {
        this.lockedUntil = lockedUntil;
    }

    /**
     * 当前是否仍处于锁定状态。
     *
     * @return 锁定未到期返回 true
     */
    public boolean isLocked() {
        return lockedUntil != null && lockedUntil.isAfter(LocalDateTime.now());
    }

    @Override
    public String toString() {
        return "User{username='" + username + "', role=" + role + ", realName='" + realName + "'}";
    }
}