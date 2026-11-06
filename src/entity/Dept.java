package entity;

public class Dept {
    private Integer deptId;
    private String deptName;
    private Integer roomId;
    private Integer isEnable; //1启用 0停用

    public Dept(){}

    public Dept(Integer deptId, String deptName, Integer roomId, Integer isEnable) {
        this.deptId = deptId;
        this.deptName = deptName;
        this.roomId = roomId;
        this.isEnable = isEnable;
    }

    //getter setter
    public Integer getDeptId() {
        return deptId;
    }

    public void setDeptId(Integer deptId) {
        this.deptId = deptId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public Integer getIsEnable() {
        return isEnable;
    }

    public void setIsEnable(Integer isEnable) {
        this.isEnable = isEnable;
    }
}
