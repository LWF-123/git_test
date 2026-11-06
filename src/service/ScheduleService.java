package service;

import entity.Schedule;
import java.util.List;

public class ScheduleService {

    /**
     * 新增排班
     * @param schedule 排班实体
     * @return 成功true
     * 数据库层：insert into schedule
     */
    public boolean addSchedule(Schedule schedule){
        // return scheduleMapper.insert(schedule) > 0;
        return false;
    }

    /**
     * 修改排班全部信息（出诊星期、号源上限）
     */
    public boolean updateSchedule(Schedule schedule){
        // return scheduleMapper.updateById(schedule) > 0;
        return false;
    }

    /**
     * 修改最大号源容量（加号功能）
     */
    public boolean changeMaxNum(Integer scheduleId, Integer newMaxNum){
        // Schedule sch = new Schedule();
        // sch.setScheduleId(scheduleId);
        // sch.setMaxNum(newMaxNum);
        // return scheduleMapper.updateById(sch) > 0;
        return false;
    }

    /**
     * 停诊：把指定星期改为0休息
     * weekIndex:1周一，2周二，3周三，4周四，5周五，6周六，7周日
     */
    public boolean stopDiagnosis(Integer scheduleId, int weekIndex){
        // Schedule sch = scheduleMapper.selectById(scheduleId);
        // if(sch == null) return false;
        // switch (weekIndex){
        //     case 1: sch.setMonday(0);break;
        //     case 2: sch.setTuesday(0);break;
        //     case 3: sch.setWednesday(0);break;
        //     case 4: sch.setThursday(0);break;
        //     case 5: sch.setFriday(0);break;
        //     case 6: sch.setSaturday(0);break;
        //     case 7: sch.setSunday(0);break;
        // }
        // return scheduleMapper.updateById(sch) > 0;
        return false;
    }

    /**
     * 查询全部排班
     */
    public List<Schedule> getAllSchedule(){
        // return scheduleMapper.selectList(null);
        return null;
    }

    /**
     * 根据医生id查询排班
     */
    public List<Schedule> getScheduleByDocId(Integer docId){
        // return scheduleMapper.selectByDocId(docId);
        return null;
    }
}
