package org.scut.v1.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.scut.v1.entity.Matter;
import org.scut.v1.entity.VO.MatterVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ${author}
 * @since 2021-08-15
 */
public interface IMatterService extends IService<Matter> {

    List<MatterVO> listMatters(Integer uid, Integer pageNo, Integer pageSize);

    void handleMatter(Integer matterId);

    void handleMatter(Integer uid,Integer matterId, String appealContent);

    void commitMatter(Integer uid, Integer matterToWho, String matterContent, String matterReason);
}
