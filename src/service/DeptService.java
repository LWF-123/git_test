package service;

import entity.Dept;
import entity.Doctor;
import java.util.List;

public class DeptService {
    // ==========科室管理==========
    /**
     * 新增科室
     * @param dept 科室实体
     * 数据库层：insert into department
     * @return 成功true
     */
    public boolean addDept(Dept dept){
        // return deptMapper.insert(dept) > 0;
        return false;
    }

    /**
     * 修改科室
     */
    public boolean updateDept(Dept dept){
        // return deptMapper.updateById(dept) >0;
        return false;
    }

    /**
     * 查询全部科室
     */
    public List<Dept> getAllDept(){
        // return deptMapper.selectList(null);
        return null;
    }

    /**
     * 启用/停用科室
     */
    public boolean changeDeptStatus(Integer deptId, Integer isEnable){
        // Dept dept = new Dept();
        // dept.setDeptId(deptId);
        // dept.setIsEnable(isEnable);
        // return deptMapper.updateById(dept)>0;
        return false;
    }

    // ==========医生管理==========
    /**
     * 新增医生
     */
    public boolean addDoctor(Doctor doctor){
        // return doctorMapper.insert(doctor)>0;
        return false;
    }

    /**
     * 修改医生信息
     */
    public boolean updateDoctor(Doctor doctor){
        // return doctorMapper.updateById(doctor)>0;
        return false;
    }

    /**
     * 查询全部医生
     */
    public List<Doctor> getAllDoctor(){
        // return doctorMapper.selectList(null);
        return null;
    }

    /**
     * 根据科室id查询该科室下所有医生
     */
    public List<Doctor> getDoctorByDeptId(Integer deptId){
        // return doctorMapper.selectListByDeptId(deptId);
        return null;
    }

    /**
     * 修改医生在职状态（离职/复职）
     */
    public boolean changeDoctorWorkStatus(Integer docId, Integer isWork){
        // Doctor d = new Doctor();
        // d.setDocId(docId);
        // d.setIsWork(isWork);
        // return doctorMapper.updateById(d)>0;
        return false;
    }
}
