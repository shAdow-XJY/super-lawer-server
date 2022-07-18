package org.scut.v1.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.scut.v1.entity.Appeal;
import org.scut.v1.entity.Matter;
import org.scut.v1.entity.VO.MatterVO;
import org.scut.v1.mapper.AppealMapper;
import org.scut.v1.mapper.MatterMapper;
import org.scut.v1.service.IMatterService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
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
public class MatterServiceImpl extends ServiceImpl<MatterMapper, Matter> implements IMatterService {


    @Resource
    private AppealMapper appealMapper;

    @Override
    public List<MatterVO> listMatters(Integer uid, Integer pageNo, Integer pageSize) {
        Page<MatterVO> page = new Page<>(pageNo, pageSize);
        return baseMapper.listMattersByPage(uid, page);
    }

    /**
     * 同意事项请求
     * @param matterId
     */
    @Override
    public void handleMatter(Integer matterId) {
        Matter matter=new Matter();
        matter.setId(matterId);
        matter.setIsHandle(true);
        matter.setHandleResult(1);
        matter.setHandleTime(new Date());
        baseMapper.updateById(matter);
    }

    /**
     * 拒绝事项请求
     * @param matterId
     * @param appealContent
     */
    @Transactional
    @Override
    public void handleMatter(Integer uid,Integer matterId, String appealContent) {
        Matter matter=new Matter();
        matter.setId(matterId);
        matter.setIsHandle(true);
        matter.setHandleResult(0);
        matter.setHandleTime(new Date());
        baseMapper.updateById(matter);
        Appeal appeal=new Appeal();
        appeal.setAppealReason(appealContent);
        appeal.setMatterId(matterId);
        appeal.setUid(uid);
        appeal.setCommitTime(new Date());
        appealMapper.insert(appeal);
    }

    /**
     * 提交新事项
     * @param uid
     * @param matterToWho
     * @param matterContent
     * @param matterReason
     */
    @Override
    public void commitMatter(Integer uid, Integer matterToWho, String matterContent, String matterReason) {
        Matter matter=new Matter();
        matter.setMatterContent(matterContent);
        matter.setMatterReason(matterReason);
        matter.setFromId(uid);
        matter.setToId(matterToWho);
        matter.setIsHandle(false);
        matter.setCommitTime(new Date());
        baseMapper.insert(matter);
    }
}
