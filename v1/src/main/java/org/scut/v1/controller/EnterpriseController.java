package org.scut.v1.controller;


import io.swagger.annotations.ApiOperation;
import org.scut.v1.entity.Response.R;
import org.scut.v1.service.IEnterpriseService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2021-08-15
 */
@RestController
@RequestMapping("/v1/enterprise")
public class EnterpriseController {
    @Resource
    private IEnterpriseService enterpriseService;

    @ApiOperation("根据id获取企业认证信息")
    @GetMapping("/info/{id}")
    public R getEnterpriseInfo(@PathVariable Integer id, HttpServletRequest request){
        return R.ok().data("info",enterpriseService.getById(id));
    }
}

