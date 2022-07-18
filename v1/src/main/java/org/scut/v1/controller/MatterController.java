package org.scut.v1.controller;


import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.scut.v1.entity.VO.MatterVO;
import org.scut.v1.entity.Response.R;
import org.scut.v1.service.IMatterService;
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
@RequestMapping("/v1/matter")
public class MatterController {

    @Resource
    private IMatterService matterService;

    @Resource
    private UserUtil userUtil;

    @ApiOperation("查看待办事项")
    @GetMapping("")
    public R listMatters(@RequestParam(required = false) @Nullable Integer pageSize,
                         @RequestParam(required = false) @Nullable Integer pageNo, HttpServletRequest request) {
        if (pageSize == null || pageSize <= 0) pageSize = 10;
        if (pageNo == null || pageNo <= 0) pageNo = 1;
        //判断是否登陆
        Integer uid = userUtil.getUid(request);
        List<MatterVO> matterVoList = matterService.listMatters(uid, pageNo, pageSize);
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("page_no", pageNo);
        responseData.put("page_size", pageSize);
        responseData.put("matters", matterVoList);
        return R.ok().data(responseData);
    }

    @ApiOperation("处理具体事项")
    @GetMapping("/{matterId}")
    public R handleMatter(@RequestParam Integer handleResult, @PathVariable Integer matterId, @RequestParam(required = false) String appealContent, HttpServletRequest request) {
        //判断是否登陆
        Integer uid = userUtil.getUid(request);
        if (handleResult.equals(1)) {
            matterService.handleMatter(matterId);
        } else {
            matterService.handleMatter(uid, matterId, appealContent);
        }
        return R.ok().message("事项处理成功");
    }


    @ApiOperation("增加事项")
    @PostMapping("")
    public R commitMatter(
            @RequestParam String matterContent, @RequestParam String matterReason, @RequestParam()@ApiParam("给谁提事项，为用户唯一标识id") Integer matterToWho, HttpServletRequest request) {
        //判断是否登陆
        Integer uid = userUtil.getUid(request);
        matterService.commitMatter(uid, matterToWho, matterContent, matterReason);
        return R.ok().message("事项增加成功");
    }

}

