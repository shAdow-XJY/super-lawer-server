package org.scut.v1.controller;


import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.scut.v1.entity.Project;
import org.scut.v1.entity.Response.R;
import org.scut.v1.entity.Response.ResponseEnum;
import org.scut.v1.entity.VO.ProjDetailVO;
import org.scut.v1.entity.VO.ProjServiceVO;
import org.scut.v1.entity.VO.ProjectCreateVO;
import org.scut.v1.entity.VO.ProjectVO;
import org.scut.v1.service.IProjectService;
import org.scut.v1.service.IUserService;
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
 * @since 2021-08-15
 */
@RestController
@RequestMapping("/v1/projects")
public class ProjectController {
    @Resource
    private IProjectService iProjectService;

    @Resource
    private UserUtil userUtil;

    @Resource
    private IUserService userService;


    @ApiOperation("获取项目列表")
    @GetMapping("")
    public R listProjects(@RequestParam(required = false) @Nullable Integer pageSize,
                          @RequestParam(required = false) @Nullable Integer pageNo,
                          @ApiParam("项目过滤列表，空代表全部，new代表新分配项目，runnning end类推") @RequestParam(required = false) String filter,
                          HttpServletRequest request) {
        if (pageSize == null || pageSize <= 0) pageSize = 100;
        if (pageNo == null || pageNo <= 0) pageNo = 1;
        if (filter == null) filter = "";
        Assert.isTrue(userService.getUserType(userUtil.getUid(request))!=0,ResponseEnum.NOT_ADMIN_ERROR);
        Integer uid = userUtil.getUid(request);
        List<ProjectVO> projectVOList = iProjectService.listProjects(uid, filter, pageNo, pageSize);
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("page_no", pageNo);
        responseData.put("page_size", pageSize);
        responseData.put("projects", projectVOList);
        return R.ok().data(responseData);
    }


    @ApiOperation("获取项目具体内容")
    @GetMapping("/{projectId}")
    public R getProjDetails(
            @ApiParam("项目的唯一标识") @PathVariable Integer projectId) {
        ProjDetailVO projContentVO = iProjectService.getDetail(projectId);
        return R.ok().data("proj_detail", projContentVO);
    }


    @ApiOperation("企业上传支付证明")
    @PostMapping("/{projectId}/fee")
    public R uploadFeeAuth(
            @ApiParam("项目的唯一标识") @PathVariable Integer projectId, @ApiParam("支付证明url") @RequestParam String feeUrl) {
        System.out.println(feeUrl);
        Project project = iProjectService.getById(projectId);
        if (project != null) {
            project.setPayPictureUrl(feeUrl);
            iProjectService.updateById(project);
            return R.ok();
        }
        return R.error().message("项目不存在");
    }

    @ApiOperation("查看项目服务方案")
    @GetMapping("/service/{projectId}")
    public R getProjService(@ApiParam("项目的唯一标识") @PathVariable Integer projectId, HttpServletRequest request) {
        ProjServiceVO projServiceVO = iProjectService.getProjService(projectId);
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("proj_service", projServiceVO);
        return R.ok().data(responseData);
    }

    @ApiOperation("查看未分配项目")
    @GetMapping("/unassigned")
    public R listUnassignedProjects(@RequestParam(required = false) @Nullable Integer pageSize,
                                    @RequestParam(required = false) @Nullable Integer pageNo,
                                    HttpServletRequest request) {
        if (pageSize == null || pageSize <= 0) pageSize = 10;
        if (pageNo == null || pageNo <= 0) pageNo = 1;
        Integer uid = userUtil.getUid(request);
        if (uid != 1) return R.error().message("非管理员禁止访问");
        List<ProjectVO> projectVOList = iProjectService.listUnassignedProjects(pageNo, pageSize);
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("page_no", pageNo);
        responseData.put("page_size", pageSize);
        responseData.put("projects", projectVOList);
        return R.ok().data(responseData);

    }

    @ApiOperation("查看支付申请项目")
    @GetMapping("/fee-list")
    public R listProjectFeeList(@RequestParam(required = false) @Nullable Integer pageSize,
                                @RequestParam(required = false) @Nullable Integer pageNo,
                                HttpServletRequest request) {
        if (pageSize == null || pageSize <= 0) pageSize = 10;
        if (pageNo == null || pageNo <= 0) pageNo = 1;
        Integer uid = userUtil.getUid(request);
        if (uid != 1) return R.error().message("非管理员禁止访问");
        List<ProjectVO> projectVOList = iProjectService.listFeeList(pageNo, pageSize);
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("page_no", pageNo);
        responseData.put("page_size", pageSize);
        responseData.put("projects", projectVOList);
        return R.ok().data(responseData);

    }

    @ApiOperation("管理员分配项目给律师")
    @PostMapping("/assign")
    public R assignProject(@ApiParam("项目的唯一标识") @RequestParam Integer projectId,
                           @RequestParam Integer lawyerId,
                           HttpServletRequest request) {
        Integer uid = userUtil.getUid(request);
        Assert.isTrue(uid == 1, ResponseEnum.NOT_ADMIN_ERROR);
        Assert.isTrue(userService.getUserType(lawyerId) == 2, ResponseEnum.NOT_LAWER_ERROR);
        Assert.isTrue(iProjectService.getById(projectId).getStatus() == 0, ResponseEnum.CASE_STATE_ERROR);
        iProjectService.assignProject(projectId, lawyerId);
        return R.ok().message("项目已经分配，等待律师处理");
    }

    @ApiOperation("管理员拒绝项目分配")
    @PostMapping("/assign/reject")
    public R rejectAssignProject(@ApiParam("项目的唯一标识") @RequestParam Integer projectId,
                                 HttpServletRequest request) {
        Integer uid = userUtil.getUid(request);
        Assert.isTrue(uid == 1, ResponseEnum.NOT_ADMIN_ERROR);
        iProjectService.rejectAssignProject(projectId);
        return R.ok().message("项目拒绝成功");
    }

    @ApiOperation("律师处理新项目")
    @PostMapping("/{projectId}")
    public R handleProject(@RequestParam() @ApiParam("0代表拒绝，1代表同意") Integer handleResult,
                           @PathVariable(value = "projectId") Integer projectId,
                           HttpServletRequest request) {
        Integer uid = userUtil.getUid(request);
        Assert.isTrue(userService.getUserType(uid) == 2, ResponseEnum.NOT_LAWER_ERROR);
        Project project = iProjectService.getById(projectId);
        Assert.isTrue(project.getToId().equals(uid), ResponseEnum.NOT_ASSIGN_LAWER);
        Assert.isTrue(project.getStatus().equals(1), ResponseEnum.CASE_STATE_ERROR);
        if (handleResult == 1) {
            iProjectService.approveProject(projectId);
        } else {
            iProjectService.rejectProject(projectId);
        }
        return R.ok().message("项目处理成功");
    }

    @ApiOperation("管理员审核支付证明处理结果")
    @PostMapping("/{projectId}/fee-handle")
    public R handleProjectFee(@RequestParam() @ApiParam("0代表拒绝，1代表同意") Integer handleResult,
                              @PathVariable(value = "projectId") Integer projectId,
                              HttpServletRequest request) {
        Integer uid = userUtil.getUid(request);
        Assert.isTrue(uid == 1, ResponseEnum.NOT_ADMIN_ERROR);
        Project project = iProjectService.getById(projectId);
        if (project == null) return R.error().message("项目不存在");
        if (handleResult == 1) {
            project.setIsPayment(true);
        } else {
            project.setPayPictureUrl(null);
        }
        iProjectService.updateById(project);
        return R.ok().message("项目处理成功");
    }

    @ApiOperation("管理员收取项目费用，设置项目为已支付")
    @PostMapping("/set-pay")
    public R payForProject(@PathVariable(value = "projectId") Integer projectId,
                           HttpServletRequest request) {
        Integer uid = userUtil.getUid(request);
        Assert.isTrue(uid == 1, ResponseEnum.NOT_ADMIN_ERROR);
        Project project = iProjectService.getById(projectId);
        project.setIsPayment(true);
        return R.ok().message("项目已经支付");
    }

    @ApiOperation("发起项目")
    @PostMapping("/commit")
    public R commitProject(
            @RequestBody ProjectCreateVO projectCreateVO, HttpServletRequest request) {
        //判断是否登陆
        Integer uid = userUtil.getUid(request);
        Assert.isTrue(userService.getUserType(uid) == 3, ResponseEnum.NOT_ENTERPRISE_ERROR);
        iProjectService.commitProject(uid, projectCreateVO);
        return R.ok().message("项目发起成功");
    }
}

