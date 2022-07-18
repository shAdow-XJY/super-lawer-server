package org.scut.v1.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.scut.v1.entity.Lawer;
import org.scut.v1.entity.User;
import org.scut.v1.exception.exceptionEntity.BusinessException;
import org.scut.v1.mapper.LawerMapper;
import org.scut.v1.mapper.UserMapper;
import org.scut.v1.service.ILawerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class LawerServiceImpl extends ServiceImpl<LawerMapper, Lawer> implements ILawerService {

    @Resource
    private LawerMapper lawerMapper;

    @Resource
    private UserMapper userMapper;

    @Override
    public Boolean isLawer(Integer id) {
        QueryWrapper<Lawer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", id).eq("status", 1);
        return lawerMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public Lawer getLawerByUid(Integer uid) {
        QueryWrapper<Lawer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        return lawerMapper.selectOne(queryWrapper);
    }

    @Override
    @Transactional
    public void authLawer(Integer id, Integer result) {
        Lawer lawer = new Lawer();
        lawer.setId(id);
        lawer=lawerMapper.selectById(lawer);
        switch (result) {
            case 0:
                lawer.setStatus(2);
                break;
            case 1:
                lawer.setStatus(1);
                break;
            default:
                throw new BusinessException("无法识别的处理结果");
        }
        lawerMapper.updateById(lawer);
        User user = new User();
        user.setUserType(2);
        user.setId(lawer.getUid());
        userMapper.updateById(user);
    }
}
