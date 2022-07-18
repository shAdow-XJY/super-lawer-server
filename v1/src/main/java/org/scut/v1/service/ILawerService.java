package org.scut.v1.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.scut.v1.entity.Lawer;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ${author}
 * @since 2021-08-15
 */
public interface ILawerService extends IService<Lawer> {

    Boolean isLawer(Integer id);

    Lawer getLawerByUid(Integer uid);

    void authLawer(Integer id, Integer result);
}
