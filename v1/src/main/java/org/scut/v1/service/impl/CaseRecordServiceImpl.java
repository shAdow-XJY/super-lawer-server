package org.scut.v1.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.scut.v1.entity.CaseRecord;
import org.scut.v1.entity.VO.CaseRecordVO;
import org.scut.v1.mapper.CaseRecordMapper;
import org.scut.v1.service.ICaseRecordService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2021-08-21
 */
@Service
public class CaseRecordServiceImpl extends ServiceImpl<CaseRecordMapper, CaseRecord> implements ICaseRecordService {


    @Override
    public void deleteRecordsByCaseId(Integer caseId) {
        QueryWrapper<CaseRecord> caseRecordQueryWrapper = new QueryWrapper<>();
        caseRecordQueryWrapper.eq("case_id", caseId);
        baseMapper.delete(caseRecordQueryWrapper);
    }


    @Override
    public void addCaseRecord( CaseRecordVO caseRecordVO) {
        CaseRecord caseRecord=new CaseRecord();
        caseRecord.setCaseId(caseRecordVO.getCaseId());
        caseRecord.setDigest(caseRecordVO.getDigest());
        caseRecord.setDetail(caseRecordVO.getDetail());
        baseMapper.insert(caseRecord);
    }

    @Override
    public void deleteByRecordId(Integer caseRecordId) {
        baseMapper.deleteById(caseRecordId);
    }

    @Override
    public List<CaseRecord> getRecordByCaseId(Integer caseId,Integer pageNo,Integer pageSize) {
        QueryWrapper<CaseRecord> caseRecordQueryWrapper=new QueryWrapper<>();
        caseRecordQueryWrapper.eq("case_id",caseId).orderBy(true,false,"create_time");
        Page<CaseRecord> page=new Page<>(pageNo,pageSize);
        return baseMapper.selectPage(page,caseRecordQueryWrapper).getRecords();

    }
}
