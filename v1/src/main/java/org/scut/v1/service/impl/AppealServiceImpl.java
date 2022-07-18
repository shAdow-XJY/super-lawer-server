package org.scut.v1.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.scut.v1.entity.Appeal;
import org.scut.v1.entity.DTO.AppealVO;
import org.scut.v1.entity.User;
import org.scut.v1.mapper.AppealMapper;
import org.scut.v1.mapper.UserMapper;
import org.scut.v1.service.IAppealService;
import org.scut.v1.service.IMsgService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2021-08-15
 */
@Service
@Slf4j
public class AppealServiceImpl extends ServiceImpl<AppealMapper, Appeal> implements IAppealService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private IMsgService msgService;

    @Value("${admin.id}")
    private Integer adminId;

    /**
     * 列出所有申诉列表
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Override
    public List<AppealVO> listAppeals(Integer pageNo, Integer pageSize) {

        QueryWrapper<Appeal> queryWrapper = new QueryWrapper();
        queryWrapper.orderBy(true, false, "commit_time");
        Page<Appeal> page = new Page<>(pageNo, pageSize);

        List<Appeal> appeals = baseMapper.selectPage(page, queryWrapper).getRecords();
        List<AppealVO> appealVOList=new ArrayList<>();
        for (Appeal appeal : appeals) {
            AppealVO appealVO = new AppealVO();
            BeanUtils.copyProperties(appeal, appealVO);
            User user =userMapper.selectById(appeal.getUid());
            appealVO.setFromWhoName(user.getNickname());
            appealVOList.add(appealVO);
        }
        return appealVOList;
    }

    /**
     * 管理员同意申诉内容
     * @param appealId
     */
    @Override
    @Transactional
    public void approveAppeal(Integer appealId) {
        Appeal appeal=new Appeal();
        appeal.setId(appealId);
        appeal.setIsHandle(true);
        appeal.setHandleResult(1);
        appeal.setHandleTime(new Date());
        baseMapper.updateById(appeal);
        appeal=baseMapper.selectById(appeal);
        msgService.savePlainMsg(adminId,appeal.getUid(),"您的申诉编号为:"+appeal.getId()+" 的申诉已经审批完成，审批结果为同意");
    }

    /**
     * 管理员拒绝申诉内容
     * @param appealId
     */
    @Override
    public void rejectAppeal(Integer appealId) {
        Appeal appeal=new Appeal();
        appeal.setId(appealId);
        appeal.setIsHandle(true);
        appeal.setHandleResult(0);
        appeal.setHandleTime(new Date());
        baseMapper.updateById(appeal);
        appeal=baseMapper.selectById(appeal);
        msgService.savePlainMsg(adminId,appeal.getUid(),"您的申诉编号为:"+appeal.getId()+" 的申诉已经审批完成，审批结果为拒绝");

    }
}
