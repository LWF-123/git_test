package service;

import entity.Registration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CallService {

    /**
     * 获取该医生【待叫号候诊队列】，自动按优先级排序
     * 优先级：急诊(1) > 专家(2) > 普通(3)，同类型按排队号先后
     * @param docId 医生ID
     * @return 排好优先级的候诊列表
     */
    public List<Registration> getWaitQueue(Integer docId){
        // 从数据库查出该医生所有状态=待就诊WAIT的挂号记录
        // List<Registration> rawList = registerMapper.selectWaitByDocId(docId);
        List<Registration> rawList = new ArrayList<>();

        // 优先级排序：先按号别优先级，同级按排队号
        rawList.sort(Comparator.comparingInt(Registration::getRegTypePriority)
                .thenComparing(Registration::getQueueNo));
        return rawList;
    }

    /**
     * 叫下一位患者
     * @param docId 医生ID
     * @return 被叫到的挂号记录；无患者返回null
     */
    public Registration callNext(Integer docId){
        List<Registration> queue = getWaitQueue(docId);
        if(queue.isEmpty()){
            return null;
        }
        Registration first = queue.get(0);
        // 修改状态：待就诊 → 正在接诊
        // first.setRegStatus(Registration.RegStatus.CALLING.getCode());
        // registerMapper.updateById(first);
        return first;
    }

    /**
     * 过号操作
     * @param regId 挂号ID
     * @return 是否成功
     */
    public boolean missNumber(Integer regId){
        // Registration reg = registerMapper.selectById(regId);
        // if(reg == null || reg.getRegStatus() != Registration.RegStatus.CALLING.getCode()) return false;
        // reg.setRegStatus(Registration.RegStatus.MISS.getCode());
        // return registerMapper.updateById(reg) > 0;
        return false;
    }

    /**
     * 完成接诊
     * @param regId 挂号ID
     * @return 是否成功
     */
    public boolean finishConsult(Integer regId){
        // Registration reg = registerMapper.selectById(regId);
        // if(reg == null || reg.getRegStatus() != Registration.RegStatus.CALLING.getCode()) return false;
        // reg.setRegStatus(Registration.RegStatus.FINISH.getCode());
        // return registerMapper.updateById(reg) > 0;
        return false;
    }
}
