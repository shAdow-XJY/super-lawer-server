package org.scut.v1.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.scut.v1.entity.Enterprise;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ${author}
 * @since 2021-08-15
 */
public interface IEnterpriseService extends IService<Enterprise> {

    Enterprise getEnterpriseByUid(Integer uid);

    void authEnterprise(Integer id, Integer result);
}
