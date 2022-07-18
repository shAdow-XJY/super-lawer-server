package org.scut.v1.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.scut.v1.entity.Appeal;
import org.scut.v1.entity.DTO.AppealVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ${author}
 * @since 2021-08-15
 */
public interface IAppealService extends IService<Appeal> {

    List<AppealVO> listAppeals(Integer pageNo, Integer pageSize);

    void approveAppeal(Integer appealId);

    void rejectAppeal(Integer appealId);
}
