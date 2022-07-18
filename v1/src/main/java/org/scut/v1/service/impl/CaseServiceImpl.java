package org.scut.v1.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.scut.v1.entity.Cases;
import org.scut.v1.entity.Party;
import org.scut.v1.entity.Response.ResponseEnum;
import org.scut.v1.entity.VO.CaseDigestVO;
import org.scut.v1.entity.VO.FileVO;
import org.scut.v1.exception.exceptionEntity.BusinessException;
import org.scut.v1.mapper.CaseMapper;
import org.scut.v1.mapper.PartyMapper;
import org.scut.v1.service.ICaseRecordService;
import org.scut.v1.service.ICaseService;
import org.scut.v1.util.ArrayUtil;
import org.scut.v1.util.FileUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2021-08-21
 */
@Service
public class CaseServiceImpl extends ServiceImpl<CaseMapper, Cases> implements ICaseService {

    @Resource
    private CaseMapper caseMapper;

    @Resource
    private PartyMapper partyMapper;

    @Resource
    private ICaseRecordService caseRecordService;

    @Value("${case.file.path.prefix}")
    private String caseFilePathPrefix;


    @Override
    @Transactional
    public void insertCase(Cases c) {
        c.setCaseStatus("在办");
        caseMapper.insert(c);
        c.setCaseFilesPath(caseFilePathPrefix + c.getId() + "/");
        caseMapper.update(c, null);
    }

    @Override
    public Boolean existCase(Integer id, Integer uid) {
        QueryWrapper<Cases> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id).eq("lawer_id", uid);
        return baseMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public List<CaseDigestVO> getDigestByLawerAndStatus(String status, Integer id, Integer pageNo, Integer pageSize) {
        QueryWrapper<Cases> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "case_type", "case_application", "case_reason", "case_biaodi_amount").
                eq("lawer_id", id);
        switch (status) {
            case "在办":
            case "结案":
            case "归档":
                queryWrapper.eq("case_status", status);
                break;
            case "":
                break;
            default:
                throw new BusinessException(ResponseEnum.CASE_STATUS_NULL);
        }
        Page<Cases> page = new Page<>(pageNo, pageSize);
        List<Cases> caseList = caseMapper.selectPage(page, queryWrapper).getRecords();
        List<CaseDigestVO> digestVOList = new ArrayList<>();
        for (Cases c : caseList) {
            CaseDigestVO caseDigestVO = new CaseDigestVO();
            BeanUtils.copyProperties(c, caseDigestVO);
            if (StringUtils.isEmpty(c.getCaseName())) {
                //查询双方当事人
                List<String> plaintiffs = new ArrayList<>();
                List<String> defendants = new ArrayList<>();

                QueryWrapper<Party> queryWrapper1 = new QueryWrapper<>();
                queryWrapper1.eq("case_id", c.getId());
                List<Party> parties = partyMapper.selectList(queryWrapper1);
                for (Party party : parties) {
                    switch (party.getPartyAttribute()) {
                        case "原告":
                        case "上诉人":
                        case "申请人":
                        case "再审申请人":
                        case "原审原告":
                            plaintiffs.add(party.getPartyName());
                            break;
                        case "被告":
                        case "被上诉人":
                        case "被申请人":
                        case "再审被申请人":
                        case "原审被告":
                            defendants.add(party.getPartyName());
                            break;
                    }
                }
                String caseName = ArrayUtil.joinElements(plaintiffs) + " vs " + ArrayUtil.joinElements(defendants);
                caseDigestVO.setCaseName(caseName);
            }
            digestVOList.add(caseDigestVO);

        }
        return digestVOList;
    }

    @Override
    public Boolean existParty(Integer caseId, String partyName) {

        QueryWrapper<Party> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("party_name", partyName).eq("case_id", caseId);
        return partyMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    @Transactional
    public void addParty(Integer caseId, Party party) {
        partyMapper.insert(party);
    }

    @Override
    public boolean isCreator(Integer uid, Integer caseId) {
        Cases cases = caseMapper.selectById(caseId);
        if (cases != null) return cases.getLawerId().equals(uid);
        return false;
    }

    @Override
    @Transactional
    public void deleteCase(Integer caseId) {
        Cases cases = caseMapper.selectById(caseId);
        //1。删除案件
        caseMapper.deleteById(caseId);
        //2。删除案件当事人
        QueryWrapper<Party> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("case_id", caseId);
        partyMapper.delete(queryWrapper);
        //3。删除案件相关记录
        caseRecordService.deleteRecordsByCaseId(caseId);
        //4.删除案件文件文件
        FileUtil.delete(cases.getCaseFilesPath());
    }

    @Override
    public void addFile(Integer caseId, MultipartFile file) {
        Cases cases = caseMapper.selectById(caseId);
        try {
            boolean result = FileUtil.putFile(cases.getCaseFilesPath(), file);
            if (!result) throw new BusinessException(ResponseEnum.CASE_FILE_EXIST);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException(ResponseEnum.CASE_FILE_UPLOAD_FAIL);
        }
    }

    @Override
    public void deleteFile(Integer caseId, String fileName) {
        Cases cases = caseMapper.selectById(caseId);
        if (!FileUtil.deleteFile(cases.getCaseFilesPath() + fileName))
            throw new BusinessException(ResponseEnum.CASE_FILE_DELETE_FAIL);
    }

    @Override
    public Cases getCaseDetailById(Integer caseId) {
        Cases cases = caseMapper.selectById(caseId);
        return cases;
    }

    @Override
    public List<FileVO> getFileList(Integer caseId, String relativePath) {
        QueryWrapper<Cases> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("case_files_path").eq("id", caseId);
        Cases cases = caseMapper.selectOne(queryWrapper);
        List<FileVO> list = new ArrayList<>();
        String dirPath = cases.getCaseFilesPath() + relativePath;
        dirPath = dirPath.substring(0, dirPath.length() - 2);
        if (!FileUtil.existFile(dirPath)) return list;
        File file;
        for (String fileName : Objects.requireNonNull(new File(dirPath).list())) {
            FileVO fileVO = new FileVO();
            fileVO.setFileName(fileName);
            file = new File(dirPath + fileName);
            if (file.isDirectory()) fileVO.setFileType(0);
            else fileVO.setFileType(1);
            list.add(fileVO);
        }
        return list;
    }


}
