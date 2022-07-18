package org.scut.v1.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.scut.v1.entity.Enterprise;
import org.scut.v1.entity.Lawer;
import org.scut.v1.entity.Response.ResponseEnum;
import org.scut.v1.entity.User;
import org.scut.v1.entity.VO.*;
import org.scut.v1.exception.exceptionEntity.BusinessException;
import org.scut.v1.mapper.UserMapper;
import org.scut.v1.service.IEnterpriseService;
import org.scut.v1.service.ILawerService;
import org.scut.v1.service.IUserService;
import org.scut.v1.util.TokenUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2021-08-15
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {


    @Resource
    private UserMapper userMapper;

    @Resource
    private ILawerService lawerService;

    @Resource
    private IEnterpriseService enterpriseService;


    @Override
    public void userRegister(RegisterInfoVO registerInfoVO) {

        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("passport", registerInfoVO.getPassport());
        if (userMapper.selectCount(userQueryWrapper) > 0) throw new BusinessException(ResponseEnum.USER_HAS_REGISTER);
        User user = new User();
        {
            user.setCover(registerInfoVO.getCover());
            user.setNickname(registerInfoVO.getUsername());
            user.setEmail(registerInfoVO.getEmail());
            user.setPassport(registerInfoVO.getPassport());
            user.setPassword(registerInfoVO.getPassword());
            user.setPhone(registerInfoVO.getPhone());
        }
        //对密码进行md5加密保存
        user.setPassword(user.getPassword());
        System.out.println(user);
        userMapper.insert(user);
    }

    @Override
    public String getNameById(Integer id) {
        User user = userMapper.selectById(id);
        return user.getNickname();
    }

    @Override
    public Boolean existUserByPassport(String passport) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("passport", passport);
        return userMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public Integer getUserIdByPassport(String passport) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("passport", passport).select("id");
        return userMapper.selectOne(queryWrapper).getId();
    }

    @Override
    public Map<String, Object> login(String passport, String pwd) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("passport", passport).eq("password", pwd);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) throw new BusinessException(ResponseEnum.PASSWORD_ERROR);
        Map<String, Object> map = new HashMap<>();
        String token = TokenUtil.getToken(passport);
        map=transferUserInfo(user);
        map.put("token", token);
        return map;
    }

    @Override
    @Transactional
    public void authLawer(Integer uid, AuthLawerVO authLawerVO) {
        Lawer lawer = new Lawer();
        lawer.setUid(uid);
        BeanUtils.copyProperties(authLawerVO, lawer);
        lawer.setIdcardFront(authLawerVO.getIdcardFrontUrl());
        lawer.setIdcardBack(authLawerVO.getIdcardBackUrl());
        lawer.setBusinessLicense(authLawerVO.getBusinessLicenseUrl());
        lawer.setAuthTime(new Date());
        lawerService.save(lawer);

    }

    @Override
    @Transactional
    public void authEnterprise(Integer uid, AuthEnterpriseVO enterpriseVO) {
        Enterprise enterprise = new Enterprise();
        enterprise.setUid(uid);
        BeanUtils.copyProperties(enterpriseVO, enterprise);
        enterprise.setBusinessLicense(enterpriseVO.getBusiness_licenseUrl());
        enterprise.setAuthTime(new Date());
        enterpriseService.save(enterprise);

    }

    @Override
    public BasicUserInfoVO preBasicUserInfo(Integer uid) {
        User user = userMapper.selectById(uid);
        BasicUserInfoVO basicUserInfoVO = new BasicUserInfoVO();
        BeanUtils.copyProperties(user, basicUserInfoVO);
        basicUserInfoVO.setRegisterTime(user.getCreateTime());
        return basicUserInfoVO;
    }

    @Override
    public LawerUserInfoVO preLawerUserInfo(Integer uid) {
        LawerUserInfoVO lawerUserInfoVO = new LawerUserInfoVO();
        QueryWrapper<Lawer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        Lawer lawer = lawerService.getOne(queryWrapper);
        BeanUtils.copyProperties(lawer, lawerUserInfoVO);
        return lawerUserInfoVO;
    }

    @Override
    public EnterpriseUserInfoVO preEnterpriseUserInfo(Integer uid) {
        EnterpriseUserInfoVO enterpriseUserInfoVO = new EnterpriseUserInfoVO();
        QueryWrapper<Enterprise> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        Enterprise enterprise = enterpriseService.getOne(queryWrapper);
        BeanUtils.copyProperties(enterprise, enterpriseUserInfoVO);
        return enterpriseUserInfoVO;
    }

    @Override
    public int getUserType(Integer uid) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.select("user_type").eq("id", uid);
        return userMapper.selectOne(userQueryWrapper).getUserType();
    }

    @Override
    public List<LawerVO> listLawers(Integer pageNo, Integer pageSize) {
        Page<Lawer> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<Lawer>(pageNo, pageSize);
        QueryWrapper<Lawer> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("status",1);
        List<Lawer> list = lawerService.page(page,queryWrapper).getRecords();
        List<LawerVO> lawerVOS = new ArrayList<>();
        User user = null;
        for (Lawer lawer : list) {
            LawerVO lawerVO = new LawerVO();
            queryWrapper.clear();
            queryWrapper.select("nickname", "passport");
            user = userMapper.selectById(lawer.getUid());
            lawerVO.setId(lawer.getUid());
            lawerVO.setNickname(user.getNickname());
            lawerVOS.add(lawerVO);
        }
        return lawerVOS;
    }

    @Override
    public List<AuthListInfo> listAuthers() {
        List<AuthListInfo> authListInfos=new ArrayList<>();

        QueryWrapper<Lawer> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("status",0);
        List<Lawer> list=lawerService.list(queryWrapper);
        for (Lawer lawer : list) {
            AuthListInfo authListInfo=new AuthListInfo();
            authListInfo.setId(lawer.getId());
            authListInfo.setAuthType("lawer");
            authListInfo.setAuthTime(lawer.getCreateTime());
            authListInfo.setNickName(userMapper.selectById(lawer.getUid()).getNickname());
            authListInfos.add(authListInfo);
        }

        QueryWrapper<Enterprise> queryWrapper2=new QueryWrapper<>();
        queryWrapper2.eq("status",0);
        List<Enterprise> list2=enterpriseService.list(queryWrapper2);
        for (Enterprise enterprise : list2) {
            AuthListInfo authListInfo=new AuthListInfo();
            authListInfo.setId(enterprise.getId());
            authListInfo.setAuthType("enterprise");
            authListInfo.setAuthTime(enterprise.getCreateTime());
            authListInfo.setNickName(userMapper.selectById(enterprise.getUid()).getNickname());
            authListInfos.add(authListInfo);
        }

        return authListInfos;


    }

    @Override
    public User getUserInfoByPassport(String passport) {
        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("passport",passport);
        return baseMapper.selectOne(queryWrapper);
    }

    @Override
    public Map<String,Object> transferUserInfo(User user) {
        if (user == null) throw new BusinessException(ResponseEnum.PASSWORD_ERROR);
        Map<String, Object> map = new HashMap<>();
        map.put("cover", user.getCover());
        map.put("email", user.getEmail());
        map.put("nickname", user.getNickname());
        switch (user.getUserType()) {
            case 0:
                map.put("user_type", "未认证用户");
                break;
            case 1:
                map.put("user_type", "管理员");
                break;
            case 2:
                map.put("user_type", "律师用户");
                break;
            case 3:
                map.put("user_type", "企业用户");
                break;
        }
        return map;
    }


}
