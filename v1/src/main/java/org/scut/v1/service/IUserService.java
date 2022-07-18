package org.scut.v1.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.scut.v1.entity.User;
import org.scut.v1.entity.VO.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author ${author}
 * @since 2021-08-15
 */
public interface IUserService extends IService<User> {

    void userRegister(RegisterInfoVO registerInfoVO);
    String getNameById(Integer id);
    Boolean existUserByPassport(String passport);
    Integer getUserIdByPassport(String passport);
    Map<String, Object> login(String passport, String pwd);

    void authLawer(Integer uid, AuthLawerVO authLawerVO);
    void authEnterprise(Integer uid, AuthEnterpriseVO enterpriseVO);
    BasicUserInfoVO preBasicUserInfo(Integer uid);
    LawerUserInfoVO preLawerUserInfo(Integer uid);
    EnterpriseUserInfoVO preEnterpriseUserInfo(Integer uid);
    int getUserType(Integer uid);


    List<LawerVO> listLawers(Integer pageNo, Integer pageSize);

    List<AuthListInfo> listAuthers();

    User getUserInfoByPassport(String passport);

    Map<String,Object> transferUserInfo(User user);
}
