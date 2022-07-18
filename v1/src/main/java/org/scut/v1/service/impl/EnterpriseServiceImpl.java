package org.scut.v1.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.scut.v1.entity.Enterprise;
import org.scut.v1.entity.User;
import org.scut.v1.exception.exceptionEntity.BusinessException;
import org.scut.v1.mapper.EnterpriseMapper;
import org.scut.v1.mapper.UserMapper;
import org.scut.v1.service.IEnterpriseService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2021-08-15
 */
@Service
public class EnterpriseServiceImpl extends ServiceImpl<EnterpriseMapper, Enterprise> implements IEnterpriseService {

    @Resource
    private UserMapper userMapper;

    @Override
    public Enterprise getEnterpriseByUid(Integer uid) {
        QueryWrapper<Enterprise> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        return baseMapper.selectOne(queryWrapper);
    }

    @Override
    public void authEnterprise(Integer id, Integer result) {
        Enterprise enterprise = new Enterprise();
        enterprise.setId(id);
        enterprise=baseMapper.selectById(id);
        switch (result) {
            case 0:
                enterprise.setStatus(2);
                break;
            case 1:
                enterprise.setStatus(1);
                break;
            default:
                throw new BusinessException("无法识别的处理结果");
        }
        baseMapper.updateById(enterprise);
        User user = new User();
        user.setUserType(3);
        user.setId(enterprise.getUid());
        userMapper.updateById(user);
    }
}
