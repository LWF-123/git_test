package entity;

import java.util.Date;

public class Registration {
    //挂号状态枚举，匹配数据库reg_status
    public enum RegStatus {
        WAIT(1,"待就诊"),
        CALLING(2,"正在接诊"), //新增：叫号中
        FINISH(3,"已就诊"),
        CANCEL(4,"已取消"),
        MISS(5,"已过号"),     //新增：过号
        EXPIRE(6,"已过期");

        private final int code;
        private final String desc;
        RegStatus(int code,String desc){
            this.code = code;
            this.desc = desc;
        }
        public int getCode(){return code;}
    }

    //号别类型：急诊、专家、普通
    public enum RegType{
        EMERGENCY(1,"急诊"),  //急诊
        EXPERT(2,"专家"),     //专家
        NORMAL(3,"普通");      //普通

        private final int code;
        private final String desc;
        RegType(int code,String desc){
            this.code = code;
            this.desc = desc;
        }
        public int getCode(){return code;}
        public String getDesc(){return desc;}
    }

    private Integer regId;
    private Integer userId;
    private Integer deptId;
    private Integer docId;
    private String roomNo;
    private Date visitDate;
    private Date regTime;
    private Integer regStatus;
    private Integer isPay;
    private RegType regType;    //号别（不入库，业务内存标识，用于优先级排序）
    private String queueNo;     //排队号，业务生成，不存入数据库

    public Registration(){}

    public Registration(Integer regId, Integer userId, Integer deptId, Integer docId, String roomNo, Date visitDate, Date regTime, Integer regStatus, Integer isPay) {
        this.regId = regId;
        this.userId = userId;
        this.deptId = deptId;
        this.docId = docId;
        this.roomNo = roomNo;
        this.visitDate = visitDate;
        this.regTime = regTime;
        this.regStatus = regStatus;
        this.isPay = isPay;
    }

    //getter setter
    public Integer getRegId() {
        return regId;
    }

    public void setRegId(Integer regId) {
        this.regId = regId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getDeptId() {
        return deptId;
    }

    public void setDeptId(Integer deptId) {
        this.deptId = deptId;
    }

    public Integer getDocId() {
        return docId;
    }

    public void setDocId(Integer docId) {
        this.docId = docId;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public Date getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(Date visitDate) {
        this.visitDate = visitDate;
    }

    public Date getRegTime() {
        return regTime;
    }

    public void setRegTime(Date regTime) {
        this.regTime = regTime;
    }

    public Integer getRegStatus() {
        return regStatus;
    }

    public void setRegStatus(Integer regStatus) {
        this.regStatus = regStatus;
    }

    public Integer getIsPay() {
        return isPay;
    }

    public void setIsPay(Integer isPay) {
        this.isPay = isPay;
    }

    public RegType getRegType() {
        return regType;
    }

    public void setRegType(RegType regType) {
        this.regType = regType;
    }

    public String getQueueNo() {
        return queueNo;
    }

    public void setQueueNo(String queueNo) {
        this.queueNo = queueNo;
    }

    // ============ 新增：给CallService优先级排序用，解决爆红 ============
    public int getRegTypePriority() {
        if (regType == null) {
            return 99; //未知类型放到队列最后
        }
        return switch (regType) {
            case EMERGENCY -> 1; //急诊优先级最高
            case EXPERT -> 2;
            case NORMAL -> 3;
        };
    }
}
