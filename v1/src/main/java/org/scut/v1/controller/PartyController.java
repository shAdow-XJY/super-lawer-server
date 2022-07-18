package org.scut.v1.controller;


import io.swagger.annotations.ApiOperation;
import org.scut.v1.entity.Party;
import org.scut.v1.entity.Response.R;
import org.scut.v1.entity.Response.ResponseEnum;
import org.scut.v1.entity.VO.PartyVO;
import org.scut.v1.service.ICaseService;
import org.scut.v1.service.IPartyService;
import org.scut.v1.util.Assert;
import org.scut.v1.util.UserUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import reactor.util.annotation.Nullable;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2021-08-21
 */
@RestController
@RequestMapping("/v1/party")
public class PartyController {

    @Resource
    private ICaseService caseService;

@Resource
private UserUtil userUtil;
    @Resource
    private IPartyService partyService;

    @ApiOperation("获取案件当事人")
    @GetMapping("/")
    public R getPartyList(@RequestParam(required = false) @Nullable Integer pageSize,
                          @RequestParam(required = false) @Nullable Integer pageNo, @RequestParam Integer caseId, HttpServletRequest request) {
        if (pageSize == null || pageSize <= 0) pageSize = 10;
        if (pageNo == null || pageNo <= 0) pageNo = 1;
        Integer uid = userUtil.getUid(request);
        Assert.isTrue(caseService.existCase(caseId, uid), ResponseEnum.CASE_NOT_EXISt);
        List<Party> parties = partyService.getPartyList(caseId, pageNo, pageSize);
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("page_no", pageNo);
        responseData.put("page_size", pageSize);
        responseData.put("parties", parties);
        return R.ok().data(responseData);
    }

    @ApiOperation("案件添加当事人")
    @PostMapping("/")
    public R addParty(@RequestBody PartyVO partyVO, @RequestParam Integer caseId, HttpServletRequest request) {
        Integer uid = userUtil.getUid(request);
        Assert.isTrue(caseService.existCase(caseId, uid), ResponseEnum.CASE_NOT_EXISt);
        Assert.isTrue(!caseService.existParty(caseId, partyVO.getPartyName()), ResponseEnum.CASE_PARTY_EXIT);
        Party party = new Party();
        BeanUtils.copyProperties(partyVO, party);
        partyService.insertParties(new Party[]{party});
        return R.ok().message("当事人添加成功");
    }

    @ApiOperation("案件删除当事人")
    @DeleteMapping("/{partyId}")
    public R addParty(@PathVariable Integer partyId) {
        partyService.removeById(partyId);
        return R.ok().message("当事人删除成功");
    }
}

