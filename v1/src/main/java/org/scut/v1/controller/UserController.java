package org.scut.v1.controller;


import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.scut.v1.entity.Enterprise;
import org.scut.v1.entity.Lawer;
import org.scut.v1.entity.Response.R;
import org.scut.v1.entity.Response.ResponseEnum;
import org.scut.v1.entity.User;
import org.scut.v1.entity.VO.*;
import org.scut.v1.service.IEnterpriseService;
import org.scut.v1.service.ILawerService;
import org.scut.v1.service.IUserService;
import org.scut.v1.service.MailService;
import org.scut.v1.util.*;
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
@RequestMapping("/v1/user")
public class UserController {

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private MailService mailService;

    @Resource
    private IUserService userService;

    @Resource
    private UserUtil userUtil;

    @Resource
    private ILawerService lawerService;

    @Resource
    private IEnterpriseService enterpriseService;

    @ApiOperation("用户注册接口")
    @PostMapping("/register")
    public R userRegister(@RequestBody RegisterInfoVO registerInfoVO) {
        Assert.notNull(registerInfoVO.getCheckCode(), ResponseEnum.CHECK_CODE_ERROR);
        Assert.isTrue(CheckUtil.isPhoneLegal(registerInfoVO.getPhone()), ResponseEnum.PHONE_FORMAT_ERROR);
        String checkCode = (String) redisUtils.get(registerInfoVO.getEmail());
        Assert.equals(registerInfoVO.getCheckCode(), checkCode, ResponseEnum.CHECK_CODE_ERROR);
        Assert.notEmpty(checkCode, ResponseEnum.CHECK_CODE_EXPIRED);
        Assert.notEmpty(registerInfoVO.getPassport(), ResponseEnum.PASSPORT_NOT_EXIST);
        Assert.notEmpty(registerInfoVO.getPassword(), ResponseEnum.PASSWORD_IS_NULL);
        userService.userRegister(registerInfoVO);
        return R.ok().message("用户注册成功");
    }

    @ApiOperation("发送邮箱验证码")
    @PostMapping("/check-code/send")
    public R sendCheckCode(@RequestParam String mail) {
        Assert.notEmpty(mail, ResponseEnum.MAIL_IS_NULL);
        String code = RandomUtils.getFourBitRandom();
        boolean sendResult = mailService.sendPlainMail("超级律师验证码", "验证码" + code
                + " ,有效期为5分钟。您的超级律师帐号正在登录应用，如非本人操作，请立即更改密码。", mail);
        if (!sendResult) return R.error().message("验证码发送失败");
        redisUtils.set(mail, code, 60 * 5);
        return R.ok().message("验证码发送成功");
    }

    @ApiOperation("发送邮箱验证码，通过账号")
    @PostMapping("/check-code/send/passport")
    public R sendCheckCodeByPassport(@RequestParam String passport) {
        Assert.notEmpty(passport, ResponseEnum.PASSPORT_IS_NULL);
        String code = RandomUtils.getFourBitRandom();
        User user = userService.getUserInfoByPassport(passport);
        if (user != null) {
            boolean sendResult = mailService.sendPlainMail("超级律师验证码", "验证码" + code
                    + " ,有效期为5分钟。您的超级律师帐号正在登录应用，如非本人操作，请立即更改密码。", user.getEmail());
            if (!sendResult) return R.error().message("验证码发送失败");
            redisUtils.set(passport, code, 60 * 5);
            return R.ok().message("验证码发送成功");
        }
        return R.error().message("不存在此通行证");
    }

    @ApiOperation("邮箱验证,参数是账号，用作忘记密码")
    @PostMapping("/check-code/check")
    public R checkCheckCode(@RequestParam String passport, @RequestParam String checkCode) {
        Assert.isTrue(userService.existUserByPassport(passport), ResponseEnum.PASSPORT_NOT_EXIST);
        Assert.isTrue(redisUtils.get(passport).equals(checkCode), ResponseEnum.CHECK_CODE_ERROR);
        return R.ok().message("验证码验证完成");
    }

    @ApiOperation("修改账号密码")
    @PutMapping("/pwd")
    public R changePwd(@RequestParam String passport, @RequestParam String pwd, HttpServletRequest request) {
        User user = userService.getUserInfoByPassport(passport);
        user.setPassword(pwd);
        userService.updateById(user);
        return R.ok().message("密码修改成功");
    }

    @ApiOperation("用户登录")
    @PostMapping("/login")
    public R userLogin(@RequestParam String passport, @RequestParam String pwd) {
        Assert.notEmpty(passport, ResponseEnum.PASSPORT_IS_NULL);
        Map<String, Object> response = userService.login(passport, pwd);
        Integer uid = userService.getUserIdByPassport(passport);
        redisUtils.set((String) response.get("token"), uid, 60 * 60 * 3);
        return R.ok().message("用户登录成功").data(response);
    }

    @ApiOperation("查看认证状态")
    @GetMapping("/auth/info")
    public R getAuthInfo(HttpServletRequest request) {
        Integer uid = userUtil.getUid(request);
        Map<String, Object> result = new HashMap<>();
        switch (userService.getUserType(uid)) {
            case 0: {
                Lawer lawer = lawerService.getLawerByUid(uid);
                if (lawer != null) {
                    result.put("auth_type", "lawer");
                    result.put("auth_status", "认证中");
                    result.put("auth_info", lawer);
                    break;
                }
                Enterprise enterprise = enterpriseService.getEnterpriseByUid(uid);
                if (enterprise != null) {
                    result.put("auth_type", "enterprise");
                    result.put("auth_status", "认证中");
                    result.put("auth_info", enterprise);
                    break;
                }
                result.put("auth_type", null);
                result.put("auth_status", "未认证");
                result.put("auth_info", null);
                break;
            }
            case 2: {
                Lawer lawer = lawerService.getLawerByUid(uid);
                if (lawer != null) {
                    result.put("auth_type", "lawer");
                    result.put("auth_status", "认证完成");
                    result.put("auth_info", lawer);
                    break;
                }
                break;
            }
            case 3: {
                Enterprise enterprise = enterpriseService.getEnterpriseByUid(uid);
                if (enterprise != null) {
                    result.put("auth_type", "enterprise");
                    result.put("auth_status", "认证完成");
                    result.put("auth_info", enterprise);
                    break;
                }
                break;
            }
            default:
                result.put("auth_type", "未认证用户");
                result.put("auth_status", "未认证");
                result.put("auth_info", null);

        }
        return R.ok().data("auth_info", result);
    }

    @ApiOperation("查看认证状态详情")
    @GetMapping("/auth/info/detail")
    public R getAuthInfoById(@RequestParam Integer id, @RequestParam String authType, HttpServletRequest request) {
        Integer uid = userUtil.getUid(request);
        Assert.isTrue(uid == 1, ResponseEnum.NOT_ADMIN_ERROR);
        Map<String, Object> result = new HashMap<>();
        switch (authType) {
            case "lawer": {
                Lawer lawer = lawerService.getById(id);
                if (lawer != null) {
                    result.put("auth_type", "lawer");
                    result.put("auth_info", lawer);
                    break;
                }
                break;
            }
            case "enterprise": {
                Enterprise enterprise = enterpriseService.getById(id);
                if (enterprise != null) {
                    result.put("auth_type", "enterprise");
                    result.put("auth_info", enterprise);
                    break;
                }
                break;
            }
            default:
                result.put("auth_info", null);

        }
        return R.ok().data("auth_info", result);
    }


    @ApiOperation("律师认证申请提交")
    @PostMapping("/auth/lawer")
    public R userAuth(@RequestBody AuthLawerVO authLawerVO, HttpServletRequest request) {
        Integer uid = userUtil.getUid(request);
        Assert.notNull(uid, ResponseEnum.USER_LOGIN_EXPIRE);
        Assert.isTrue(userService.getUserType(uid) == 0, ResponseEnum.USER_HAD_REGISTER);
        Assert.isTrue(CheckUtil.IDCardNoCheck(authLawerVO.getIdNumber()), ResponseEnum.ID_NUMBER_ERROR);
        userService.authLawer(uid, authLawerVO);
        return R.ok().message("律师注册成功");
    }

    @ApiOperation("企业认证申请提交")
    @PostMapping("/auth/enterprise")
    public R enterpriseAuth(@RequestBody AuthEnterpriseVO authEnterpriseVO, HttpServletRequest request) {
        Integer uid = userUtil.getUid(request);
        Assert.notNull(uid, ResponseEnum.USER_LOGIN_EXPIRE);
        Assert.isTrue(userService.getUserType(uid) == 0, ResponseEnum.USER_HAD_REGISTER);
        userService.authEnterprise(uid, authEnterpriseVO);
        return R.ok().message("企业注册成功");
    }

    @ApiOperation("管理员查看认证申请列表")
    @GetMapping("/auth/list")
    public R getAuthList(HttpServletRequest request) {
        Integer uid = userUtil.getUid(request);
        Assert.isTrue(uid == 1, ResponseEnum.NOT_ADMIN_ERROR);
        List<AuthListInfo> list = userService.listAuthers();
        return R.ok().data("authers", list);
    }

    @ApiOperation("查看个人身份信息状态")
    @GetMapping("/info/status")
    public R getInfoStatus(HttpServletRequest request) {
        Integer uid = userUtil.getUid(request);
        User user = userService.getById(uid);
        return R.ok().data( userService.transferUserInfo(user));
    }

    @ApiOperation("管理员处理认证申请")
    @PostMapping("/auth")
    public R handleAuth(@RequestParam @ApiParam("认证类型 lawer或者enterprise ") String authType, @RequestParam @ApiParam("要处理的认证者id") Integer id,
                        @RequestParam @ApiParam("处理结果，0代表拒绝认证，1代表同意认证") Integer result, HttpServletRequest request) {
        Integer uid = userUtil.getUid(request);
        Assert.isTrue(uid == 1, ResponseEnum.NOT_ADMIN_ERROR);
        switch (authType) {
            case "lawer":
                lawerService.authLawer(id, result);
                break;
            case "enterprise":
                enterpriseService.authEnterprise(id, result);
                break;
            default:
                return R.error().message("参数错误，请检查");
        }
        return R.ok().message("认证处理成功");
    }

    @ApiOperation("获取所有律师列表")
    @GetMapping("/lawers")
    public R getLawers(
            @RequestParam(required = false, defaultValue = "10") @Nullable Integer pageSize,
            @RequestParam(required = false, defaultValue = "1") @Nullable Integer pageNo, HttpServletRequest request) {
        Integer uid = userUtil.getUid(request);
        Assert.isTrue(uid == 1, ResponseEnum.NOT_ADMIN_ERROR);
        List<LawerVO> list = userService.listLawers(pageNo, pageSize);
        return R.ok().data("lawers", list);
    }


    @ApiOperation("查看用户个人信息")
    @GetMapping("/info")
    public R userInfo(HttpServletRequest request) {
        Integer uid = userUtil.getUid(request);
        Assert.notNull(uid, ResponseEnum.USER_LOGIN_EXPIRE);
        return R.ok().data("basic_info", userService.preBasicUserInfo(uid));

    }

}
