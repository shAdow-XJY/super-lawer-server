package org.scut.v1.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.scut.v1.entity.CaseType;
import org.scut.v1.entity.VO.CaseTypeVO;
import org.scut.v1.mapper.CaseTypeMapper;
import org.scut.v1.service.ICaseTypeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class CaseTypeServiceImpl extends ServiceImpl<CaseTypeMapper, CaseType> implements ICaseTypeService {

    @Resource
    private CaseTypeMapper caseTypeMapper;

    @Override
    public List<CaseTypeVO> listAllCaseType() {
        List<CaseType> caseTypeList = caseTypeMapper.selectList(null);
        List<CaseTypeVO> caseTypeVOS = new ArrayList<>();
        for (CaseType caseType : caseTypeList) {
            CaseTypeVO caseTypeVO = new CaseTypeVO();
            caseTypeVO.setCaseType(caseType.getCaseType());
            caseTypeVO.setCaeApplications(caseType.getCaseApplication().split(","));
            caseTypeVOS.add(caseTypeVO);
        }
        return caseTypeVOS;
    }
}