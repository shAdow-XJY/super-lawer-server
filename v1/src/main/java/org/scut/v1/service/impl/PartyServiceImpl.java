package org.scut.v1.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.scut.v1.entity.Party;
import org.scut.v1.mapper.CaseMapper;
import org.scut.v1.mapper.PartyMapper;
import org.scut.v1.service.IPartyService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2021-08-21
 */
@Service
public class PartyServiceImpl extends ServiceImpl<PartyMapper, Party> implements IPartyService {

    @Resource
    private PartyMapper partyMapper;

    @Resource
    private CaseMapper caseMapper;

    @Override
    public String insertParties(Party[] parties) {
        int[] partiesId = new int[parties.length];
        for (int i = 0; i < partiesId.length; i++) {
            partyMapper.insert(parties[i]);
            partiesId[0] = parties[i].getId();
        }
        //2。0获取当事人列表id
        String ids = "";
        if (partiesId.length > 0) {
            if (partiesId.length == 1) ids += partiesId[0];
            else {
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < partiesId.length - 1; i++) {
                    stringBuilder.append(partiesId[i]);
                }
                ids = stringBuilder.append(partiesId[partiesId.length - 1]).toString();
            }
        }
        return ids;
    }

    @Override
    public List<Party> getPartyList(Integer caseId, Integer pageNo, Integer pageSize) {
        QueryWrapper<Party> partyQueryWrapper = new QueryWrapper<>();
        partyQueryWrapper.eq("case_id", caseId).orderBy(true, false, "create_time");
        Page<Party> page = new Page<>(pageNo, pageSize);
        return partyMapper.selectPage(page, partyQueryWrapper).getRecords();
    }


}
