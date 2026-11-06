package service;

import entity.Dept;
import entity.Doctor;
import entity.Registration;
import entity.Schedule;
import java.util.List;

public class RegistrationService {

    /**
     * 查询所有启用科室
     */
    public List<Dept> getEnableDeptList(){
        // return deptMapper.selectListByEnable(1);
        return null;
    }

    /**
     * 根据科室id查询在职医生
     */
    public List<Doctor> getWorkDoctorByDeptId(Integer deptId){
        // return doctorMapper.selectWorkDoctorByDept(deptId);
        return null;
    }

    /**
     * 根据医生ID查询出诊排班
     */
    public List<Schedule> getScheduleByDocId(Integer docId){
        // return scheduleMapper.selectByDocId(docId);
        return null;
    }

    /**
     * 挂号提交，生成排队号
     * @param registration 挂号实体
     * @return 成功返回带排队号的Registration，失败返回null
     * 业务逻辑：校验号源数量，插入register表，生成queueNo排队号
     */
    public Registration createRegister(Registration registration){
        //1. 查询该医生当日已挂号数量
        //int currentCount = registerMapper.countRegisterByDocAndDate(registration.getDocId(), registration.getVisitDate());
        //2. 查询该排班最大号源maxNum
        //Schedule sch = scheduleMapper.selectById(对应排班id);
        //if(currentCount >= sch.getMaxNum()){
        //    return null; //号源已满
        //}
        //3. 插入挂号记录
        //int insert = registerMapper.insert(registration);
        //if(insert <=0) return null;
        //4. 生成排队号，示例格式：E001(急诊) / X001(专家) / P001(普通)
        //String queueNo = generateQueueNo(registration);
        //registration.setQueueNo(queueNo);
        //return registration;
        return null;
    }

    /**
     * 生成排队号
     */
    private String generateQueueNo(Registration registration){
        // 数据库层查询同医生同日期，同号别当前最大序号，拼接前缀
        return "";
    }

    // ====================== 新增【功能5：我的挂号查询 + 退号】======================
    /**
     * 查询患者本人所有挂号记录
     * @param userId 患者用户ID
     * @return 挂号列表
     */
    public List<Registration> getMyRegistration(Integer userId){
        // return registerMapper.selectByUserId(userId);
        return null;
    }

    /**
     * 退号功能
     * 规则：只能退本人、状态为待就诊的挂号，已叫号/已接诊不能退
     * @param regId 挂号ID
     * @param userId 当前登录患者ID
     * @return true退号成功 false失败
     */
    public boolean cancelRegistration(Integer regId, Integer userId){
        //Registration reg = registerMapper.selectById(regId);
        //if(reg == null || !reg.getUserId().equals(userId)) return false;
        ////仅待就诊状态允许退号
        //if(reg.getRegStatus() != Registration.RegStatus.WAIT.getCode()) return false;
        //reg.setRegStatus(Registration.RegStatus.CANCEL.getCode());
        //return registerMapper.updateById(reg) > 0;
        return false;
    }

}
