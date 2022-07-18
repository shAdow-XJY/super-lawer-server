package org.scut.v1.service;

import org.scut.v1.entity.Party;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ${author}
 * @since 2021-08-21
 */
public interface IPartyService extends IService<Party> {
   String insertParties(Party[] parties);

    List<Party> getPartyList(Integer caseId, Integer pageNo, Integer pageSize);
}
