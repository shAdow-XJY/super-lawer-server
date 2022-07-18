package org.scut.v1.controller;


import io.swagger.annotations.ApiOperation;
import org.scut.v1.entity.Response.R;
import org.scut.v1.service.IServiceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2021-08-15
 */
@RestController
@RequestMapping("/v1/service")
public class ServiceController {
    @Resource
    private IServiceService service;

    @ApiOperation("获取所有服务内容列表")
    @GetMapping("/")
    public R getServiceList() {
        return R.ok().data("services", service.list());
    }
}

