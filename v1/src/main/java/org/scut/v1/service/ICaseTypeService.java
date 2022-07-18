package org.scut.v1.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.scut.v1.entity.CaseType;
import org.scut.v1.entity.VO.CaseTypeVO;

import java.util.List;

public interface ICaseTypeService extends IService<CaseType> {

    List<CaseTypeVO> listAllCaseType();
}
