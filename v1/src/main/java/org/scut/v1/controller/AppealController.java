package org.scut.v1.controller;


import io.swagger.annotations.ApiOperation;
import org.scut.v1.entity.DTO.AppealVO;
import org.scut.v1.entity.Response.R;
import org.scut.v1.service.IAppealService;
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
@RequestMapping("/v1/appeal")
public class AppealController {
    @Resource
    private IAppealService appealService;

    @Resource
    private UserUtil userUtil;

    @ApiOperation("管理员查看申诉")
    @GetMapping("")
    public R listAppeals(@RequestParam(required = false) @Nullable Integer pageSize,
                         @RequestParam(required = false) @Nullable Integer pageNo, HttpServletRequest request) {
        if (pageSize == null || pageSize <= 0) pageSize = 10;
        if (pageNo == null || pageNo <= 0) pageNo = 1;
        //判断是否登陆
        if (userUtil.getUid(request) != 1) return R.error().message("非管理员禁止访问");
        List<AppealVO> appealVOList = appealService.listAppeals(pageNo, pageSize);
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("page_no", pageNo);
        responseData.put("page_size", pageSize);
        responseData.put("matters", appealVOList);
        return R.ok().data(responseData);
    }

    @ApiOperation("管理员处理申诉")
    @PostMapping("/{appealId}")
    public R handleAppeal(@RequestParam("0代表拒绝，1代表同意") Integer handleResult,
                          @PathVariable(value = "appealId") Integer appealId
            , HttpServletRequest request) {
//        判断是否登陆
        Integer uid = userUtil.getUid(request);
        if(uid!=0) return  R.error().message("非管理员禁止访问");
        if (handleResult == 1) {
            appealService.approveAppeal(appealId);
        } else {
            appealService.rejectAppeal(appealId);
        }

        return R.ok().message("申诉处理成功");
    }
}

