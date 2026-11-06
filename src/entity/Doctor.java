package entity;

public class Doctor {
    private Integer docId;
    private String docName;
    private String docLevel;
    private Integer deptId;
    private Integer isWork; //1在职 0离职

    public Doctor(){}

    public Doctor(Integer docId, String docName, String docLevel, Integer deptId, Integer isWork) {
        this.docId = docId;
        this.docName = docName;
        this.docLevel = docLevel;
        this.deptId = deptId;
        this.isWork = isWork;
    }

    //getter setter
    public Integer getDocId() {
        return docId;
    }

    public void setDocId(Integer docId) {
        this.docId = docId;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public String getDocLevel() {
        return docLevel;
    }

    public void setDocLevel(String docLevel) {
        this.docLevel = docLevel;
    }

    public Integer getDeptId() {
        return deptId;
    }

    public void setDeptId(Integer deptId) {
        this.deptId = deptId;
    }

    public Integer getIsWork() {
        return isWork;
    }

    public void setIsWork(Integer isWork) {
        this.isWork = isWork;
    }
}
