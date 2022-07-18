package org.scut.v1.controller;


import io.swagger.annotations.ApiOperation;
import org.scut.v1.entity.CaseRecord;
import org.scut.v1.entity.Response.R;
import org.scut.v1.entity.Response.ResponseEnum;
import org.scut.v1.entity.VO.CaseRecordVO;
import org.scut.v1.service.ICaseRecordService;
import org.scut.v1.service.ICaseService;
import org.scut.v1.util.Assert;
import org.scut.v1.util.UserUtil;
import org.springframework.web.bind.annotation.*;
import reactor.util.annotation.Nullable;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2021-08-21
 */
@RestController
@RequestMapping("/v1/case-record")
public class CaseRecordController {

    @Resource
    private ICaseService caseService;

    @Resource
    private ICaseRecordService caseRecordService;
    @Resource
    private UserUtil userUtil;
    @ApiOperation("获取案件相关记录")
    @GetMapping("/")
    public R getCaseRecords(@RequestParam Integer caseId, @RequestParam(required = false) @Nullable Integer pageSize,
                            @RequestParam(required = false) @Nullable Integer pageNo) {
        if (pageSize == null || pageSize <= 0) pageSize = 10;
        if (pageNo == null || pageNo <= 0) pageNo = 1;
        List<CaseRecord> caseRecords = caseRecordService.getRecordByCaseId(caseId, pageNo, pageSize);
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("page_no", pageNo);
        responseData.put("page_size", pageSize);
        responseData.put("case_records", caseRecords);
        return R.ok().data(responseData);
    }

    @ApiOperation("获取案件相关记录详情")
    @GetMapping("/{caseRecordId}")
    public R getCaseRecordDetail(@PathVariable Integer caseRecordId) {
        return R.ok().data("record", caseRecordService.getById(caseRecordId));
    }

    @ApiOperation("更新案件相关记录")
    @PutMapping("/")
    public R updateCaseRecord(@RequestBody CaseRecord caseRecord) {
        Assert.isTrue(caseRecordService.getById(caseRecord.getId()) != null, ResponseEnum.CASE_RECORD_NOT_EXIST);
        caseRecordService.updateById(caseRecord);
        return R.ok().message("案件记录更新成功");
    }

    @ApiOperation("添加案件相关记录")
    @PostMapping("/")
    public R addCaseRecord(@RequestBody CaseRecordVO caseRecordVO, HttpServletRequest request) {
        Integer uid = userUtil.getUid(request);
        Assert.isTrue(caseService.existCase(caseRecordVO.getCaseId(), uid), ResponseEnum.CASE_NOT_EXISt);
        caseRecordService.addCaseRecord(caseRecordVO);
        return R.ok().message("案件记录添加成功");
    }

    @ApiOperation("删除案件相关记录")
    @DeleteMapping("/{caseRecordId}")
    public R deleteCaseRecord(@PathVariable Integer caseRecordId) {
        caseRecordService.deleteByRecordId(caseRecordId);
        return R.ok().message("案件记录删除成功");
    }
}

