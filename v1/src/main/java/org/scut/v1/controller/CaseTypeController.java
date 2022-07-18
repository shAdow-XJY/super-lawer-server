package org.scut.v1.controller;


import io.swagger.annotations.ApiOperation;
import org.scut.v1.entity.Response.R;
import org.scut.v1.service.ICaseTypeService;
import org.scut.v1.util.UserUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2021-08-21
 */
@RestController
@RequestMapping("/v1/case-type")
public class CaseTypeController {
    @Resource
    private ICaseTypeService caseTypeService;

    @ApiOperation("获取所有案件类型和对应的案件程序")
    @GetMapping("/")
    public R listCases(HttpServletRequest request) {
        return R.ok().data("case_types", caseTypeService.listAllCaseType());
    }
}

