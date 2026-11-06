package service;

import entity.Registration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统计报表服务：号源、挂号量统计
 */
public class ReportService {

    /**
     * 按日期统计：当日总挂号量、急诊/专家/普通分别数量
     * @param targetDate 目标日期（骨架暂用字符串标识）
     * @return map: total(总挂号), emergency(急诊), expert(专家), normal(普通)
     */
    public Map<String, Integer> countByDate(String targetDate) {
        Map<String, Integer> result = new HashMap<>();
        result.put("total",0);
        result.put("emergency",0);
        result.put("expert",0);
        result.put("normal",0);

        // 实际项目：List<Registration> list = registerMapper.selectByDate(targetDate);
        List<Registration> list = new ArrayList<>();

        for (Registration reg : list) {
            result.put("total", result.get("total") + 1);
            switch (reg.getRegType()){
                case EMERGENCY -> result.put("emergency", result.get("emergency")+1);
                case EXPERT -> result.put("expert", result.get("expert")+1);
                case NORMAL -> result.put("normal", result.get("normal")+1);
            }
        }
        return result;
    }

    /**
     * 按科室统计挂号量
     * @param deptId 科室id
     * @return 该科室总挂号数量
     */
    public int countByDept(Integer deptId){
        int count = 0;
        // List<Registration> list = registerMapper.selectByDeptId(deptId);
        List<Registration> list = new ArrayList<>();
        for(Registration reg : list){
            count++;
        }
        return count;
    }

    /**
     * 统计号源使用率：已挂号数量 / 总号源容量
     * @param totalSource 排班总号源容量
     * @param registeredNum 已经挂号数量
     * @return 使用率 百分比
     */
    public double getSourceUsageRate(int totalSource, int registeredNum){
        if(totalSource == 0){
            return 0.0;
        }
        return (double)registeredNum / totalSource * 100;
    }

    /**
     * 获取所有状态挂号数量统计：待就诊、已就诊、已取消、过号、过期
     * @return key:状态名称 value:数量
     */
    public Map<String,Integer> countByStatus(){
        Map<String,Integer> statusMap = new HashMap<>();
        statusMap.put("wait",0);
        statusMap.put("calling",0);
        statusMap.put("finish",0);
        statusMap.put("cancel",0);
        statusMap.put("miss",0);
        statusMap.put("expire",0);

        // List<Registration> allReg = registerMapper.selectAll();
        List<Registration> allReg = new ArrayList<>();
        for(Registration reg : allReg){
            Integer code = reg.getRegStatus();
            if(code == Registration.RegStatus.WAIT.getCode()){
                statusMap.put("wait", statusMap.get("wait")+1);
            }else if(code == Registration.RegStatus.CALLING.getCode()){
                statusMap.put("calling", statusMap.get("calling")+1);
            }else if(code == Registration.RegStatus.FINISH.getCode()){
                statusMap.put("finish", statusMap.get("finish")+1);
            }else if(code == Registration.RegStatus.CANCEL.getCode()){
                statusMap.put("cancel", statusMap.get("cancel")+1);
            }else if(code == Registration.RegStatus.MISS.getCode()){
                statusMap.put("miss", statusMap.get("miss")+1);
            }else if(code == 6){
                statusMap.put("expire", statusMap.get("expire")+1);
            }
        }
        return statusMap;
    }
}
