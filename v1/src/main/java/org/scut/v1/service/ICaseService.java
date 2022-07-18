package org.scut.v1.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.scut.v1.entity.Cases;
import org.scut.v1.entity.Party;
import org.scut.v1.entity.VO.CaseDigestVO;
import org.scut.v1.entity.VO.FileVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author ${author}
 * @since 2021-08-21
 */
public interface ICaseService extends IService<Cases> {

    void insertCase(Cases c);
    Boolean existCase(Integer id,Integer uid);
    List<CaseDigestVO> getDigestByLawerAndStatus(String status, Integer id,Integer pageNo,Integer pageSize);
    Boolean existParty(Integer caseId,String partyName);
    void addParty(Integer caseId, Party party);
    boolean isCreator(Integer uid,Integer caseId);
    void deleteCase(Integer caeId);
    void addFile(Integer caseId, MultipartFile file);
    void deleteFile(Integer caseId, String fileName);
    Cases getCaseDetailById(Integer caseId);
    List<FileVO> getFileList(Integer caseId, String relativePath);
}
