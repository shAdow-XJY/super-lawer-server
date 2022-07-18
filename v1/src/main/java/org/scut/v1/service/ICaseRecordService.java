package org.scut.v1.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.scut.v1.entity.CaseRecord;
import org.scut.v1.entity.VO.CaseRecordVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ${author}
 * @since 2021-08-21
 */
public interface ICaseRecordService extends IService<CaseRecord> {

    void deleteRecordsByCaseId(Integer caeId);

    void addCaseRecord( CaseRecordVO caseRecordVO);

    void deleteByRecordId(Integer caseRecordId);

    List<CaseRecord> getRecordByCaseId(Integer caseId,Integer pageNo,Integer pageSize);
}
