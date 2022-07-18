package org.scut.v1.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.scut.v1.entity.Cases;
import org.scut.v1.entity.Party;
import org.scut.v1.entity.Response.R;
import org.scut.v1.entity.Response.ResponseEnum;
import org.scut.v1.entity.VO.*;
import org.scut.v1.exception.exceptionEntity.BusinessException;
import org.scut.v1.service.ICaseService;
import org.scut.v1.service.ILawerService;
import org.scut.v1.service.IPartyService;
import org.scut.v1.util.Assert;
import org.scut.v1.util.UserUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.util.annotation.Nullable;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author ${陈月文}
 * @since 2021-08-15
 */
@RestController
@RequestMapping("/v1/case")
@Api("案件管理相关接口")
public class CaseController {


    @Resource
    private ICaseService caseService;

    @Resource
    private ILawerService lawerService;

    @Resource
    private IPartyService iPartyService;

    @Resource
    private UserUtil userUtil;

    @ApiOperation("获取案件概括列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "caseStatus", value = "案件状态"),
            @ApiImplicitParam(name = "pageNo", value = "页号"),
            @ApiImplicitParam(name = "pageSize", value = "页面数量大小")
    })
    @GetMapping("/")
    public R getCases(@RequestParam(required = false) String caseStatus, @RequestParam(required = false) @Nullable Integer pageSize,
                      @RequestParam(required = false) @Nullable Integer pageNo, HttpServletRequest request) {
        if (pageSize == null || pageSize <= 0) pageSize = 10;
        if (pageNo == null || pageNo <= 0) pageNo = 1;
        //判断是否登陆
        Integer uid = userUtil.getUid(request);
        caseStatus = caseStatus == null ? "" : caseStatus;
        List<CaseDigestVO> caseDigestVOS = caseService.getDigestByLawerAndStatus(caseStatus, uid, pageNo, pageSize);
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("page_no", pageNo);
        responseData.put("page_size", pageSize);
        responseData.put("cases", caseDigestVOS);
        return R.ok().data(responseData);
    }

    @ApiOperation("获取案件详情")
    @GetMapping("/{caseId}")
    public R getCaseDetail(@PathVariable Integer caseId) {
        Cases cases = caseService.getCaseDetailById(caseId);
        return R.ok().data("case_detail", cases);
    }

    @ApiOperation("新建案件")
    @Transactional
    @PostMapping("/")
    public R createCase(@RequestBody CaseVO caseVO, HttpServletRequest request) {
        Integer uid = userUtil.getUid(request);
        Assert.isTrue(lawerService.isLawer(uid), ResponseEnum.NOT_LAWER_ERROR);
        Cases c = new Cases();
        BeanUtils.copyProperties(caseVO, c);
        c.setLawerId(uid);
        caseService.insertCase(c);
        Party[] parties = new Party[caseVO.getPartyVOS().size()];
        for (int i = 0; i < parties.length; i++) {
            Party party = new Party();
            BeanUtils.copyProperties(caseVO.getPartyVOS().get(i), party);
            party.setCaseId(c.getId());
            parties[i] = party;
        }
        iPartyService.insertParties(parties);
        return R.ok().message("案件新建成功");
    }

    @ApiOperation("更新案件")
    @PutMapping("/")
    public R updateCase(@RequestBody Cases c, HttpServletRequest request) {
        Integer uid = userUtil.getUid(request);
        Assert.notNull(c.getId(), ResponseEnum.CASE_ID_IS_NULL);
        Assert.isTrue(caseService.existCase(c.getId(), uid), ResponseEnum.NOT_LAWER_ERROR);
        caseService.update(c, null);
        return R.ok().message("案件信息更新成功");
    }

    @ApiOperation("案件结案")
    @PostMapping("/{caseId}/settlement")
    public R settleCase(@PathVariable Integer caseId, @RequestBody CaseSettlementVO caseSettlementVO, HttpServletRequest request) {
        Integer uid = userUtil.getUid(request);
        Assert.isTrue(caseService.existCase(caseId, uid), ResponseEnum.NOT_LAWER_ERROR);
        Cases cases = new Cases();
        cases.setId(caseId);
        cases.setCaseStatus("结案");
        BeanUtils.copyProperties(caseSettlementVO, cases);
        caseService.updateById(cases);
        return R.ok().message("案件结案成功");
    }

    @ApiOperation("案件归档")
    @PostMapping("/{caseId}/archive")
    public R archiveCase(@PathVariable Integer caseId, @RequestBody CaseArchiveVO caseArchiveVO, HttpServletRequest request) {
        Integer uid = userUtil.getUid(request);
        Assert.isTrue(caseService.existCase(caseId, uid), ResponseEnum.NOT_LAWER_ERROR);
        Cases cases = new Cases();
        cases.setId(caseId);
        BeanUtils.copyProperties(caseArchiveVO, cases);
        cases.setCaseStatus("归档");
        caseService.updateById(cases);
        return R.ok().message("案件归档成功");
    }

    @ApiOperation("删除案件")
    @DeleteMapping("/")
    public R deleteCase(@RequestParam Integer caeId, HttpServletRequest request) {
        Integer uid = userUtil.getUid(request);
        Assert.notNull(caeId, ResponseEnum.CASE_ID_IS_NULL);
        Assert.isTrue(caseService.existCase(caeId, uid), ResponseEnum.CASE_NOT_EXISt);
        Assert.isTrue(caseService.isCreator(uid, caeId), ResponseEnum.AUTH_ILLEGAL);
        caseService.deleteCase(caeId);
        return R.ok().message("案件信息删除成功");
    }


    @ApiOperation("获取案件相关文件列表")
    @GetMapping("/{caseId}/files")
    public R getFiles(@PathVariable Integer caseId, @RequestParam String relativePath, HttpServletRequest request) {
        Integer uid = userUtil.getUid(request);
        Assert.isTrue(caseService.existCase(caseId, uid), ResponseEnum.CASE_NOT_EXISt);
        List<FileVO> fileVOS = caseService.getFileList(caseId, relativePath);
        return R.ok().data("files", fileVOS);
    }

    @ApiOperation("上传案件相关文件(单文件上传)")
    @PostMapping("/{caseId}/single-file")
    public R uploadSingleFile(@PathVariable Integer caseId, @RequestParam("file") MultipartFile file, HttpServletRequest request) {
        Integer uid = userUtil.getUid(request);
        Assert.isTrue(caseService.existCase(caseId, uid), ResponseEnum.CASE_NOT_EXISt);
        caseService.addFile(caseId, file);
        return R.ok().message("文件上传成功");
    }

    @ApiOperation("上传案件相关文件(多文件上传)")
    @PostMapping("/{caseId}/multi-file")
    public R uploadMultiFile(@PathVariable Integer caseId, @RequestParam("file") MultipartFile[] files, HttpServletRequest request) {
        Integer uid = userUtil.getUid(request);
        Assert.isTrue(caseService.existCase(caseId, uid), ResponseEnum.CASE_NOT_EXISt);
        for (MultipartFile file : files) {
            caseService.addFile(caseId, file);
        }
        return R.ok().message("文件上传成功");
    }

    @ApiOperation("下载案件文件")
    @GetMapping("/{caseId}/download")
    public String downloadCaseFile(@PathVariable Integer caseId, @RequestParam String relativePath, HttpServletResponse response) throws IOException {
        Cases cases = caseService.getById(caseId);
        File file = new File(cases.getCaseFilesPath() + relativePath);
        if (file.exists()) { //判断文件父目录是否存在
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            // response.setContentType("application/force-download");
            response.setHeader("Content-Disposition", "attachment;fileName=" + java.net.URLEncoder.encode(file.getName(), "UTF-8"));
            byte[] buffer = new byte[1024];
            FileInputStream fis = null; //文件输入流
            BufferedInputStream bis = null;
            OutputStream os; //输出流
            try {
                os = response.getOutputStream();
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                int i = bis.read(buffer);
                while (i != -1) {
                    os.write(buffer);
                    i = bis.read(buffer);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new BusinessException(ResponseEnum.CASE_FILE_DOWNLOAD_FAIL);
            } finally {
                if (bis != null) bis.close();
                if (fis != null) fis.close();
            }

        }
        return null;
    }

    @ApiOperation("删除案件相关文件")
    @DeleteMapping("/{caseId}/file")
    public R deleteFile(@PathVariable Integer caseId, @RequestBody String relativePath, HttpServletRequest request) {
        Integer uid = userUtil.getUid(request);
        Assert.isTrue(caseService.existCase(caseId, uid), ResponseEnum.CASE_NOT_EXISt);
        caseService.deleteFile(caseId, relativePath);
        return R.ok().message("文件删除成功");
    }


}
