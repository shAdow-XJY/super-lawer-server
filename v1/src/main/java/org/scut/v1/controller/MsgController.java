package org.scut.v1.controller;


import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.scut.v1.entity.Response.R;
import org.scut.v1.entity.VO.ContactVO;
import org.scut.v1.entity.VO.MsgVO;
import org.scut.v1.service.IMsgService;
import org.scut.v1.util.RedisUtils;
import org.scut.v1.util.UserUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
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
 * @since 2021-08-15
 */
@RestController
@RequestMapping("/v1/msg")
public class MsgController {

    @Resource
    private UserUtil userUtil;

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private IMsgService iMsgService;

    @ApiOperation("获取用户联系人列表及最近的一条消息记录")
    @GetMapping("/contacts")
    public R getContacts(@RequestParam(required = false) @Nullable Integer pageSize,
                         @RequestParam(required = false) @Nullable Integer pageNo, HttpServletRequest request) {
        if (pageSize == null || pageSize <= 0) pageSize = 100;
        if (pageNo == null || pageNo <= 0) pageNo = 1;
        //判断是否登陆
        Integer uid = userUtil.getUid(request);

        List<ContactVO> contactVOList = iMsgService.getContacts(uid, pageNo, pageSize);
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("page_no", pageNo);
        responseData.put("page_size", pageSize);
        responseData.put("contacts", contactVOList);
        return R.ok().data(responseData);
    }

    @ApiOperation("查看用户与某个具体的人的聊天记录")
    @GetMapping("/contacts/{contactId}")
    public R getMessageContent(@ApiParam("对方的唯一标识") @PathVariable Integer contactId,
                               @RequestParam(required = false) @Nullable Integer pageSize,
                               @RequestParam(required = false) @Nullable Integer pageNo, HttpServletRequest request) {
        if (pageSize == null || pageSize <= 0) pageSize = 1000;
        if (pageNo == null || pageNo <= 0) pageNo = 1;
        //判断是否登陆
        Integer uid = userUtil.getUid(request);
        List<MsgVO> list = iMsgService.getMsgWithUid(uid, contactId, pageNo, pageSize);
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("page_no", pageNo);
        responseData.put("page_size", pageSize);
        responseData.put("msg", list);
        return R.ok().data(responseData);
    }


    @ApiOperation("用户给某个人发送消息")
    @PostMapping("/contacts/{contactId}")
    public R sendMessage(@ApiParam("对方的唯一标识") @PathVariable Integer contactId, HttpServletRequest request,
                         @ApiParam("0代表文本消息，1代表文件消息") @RequestParam Integer msgType,
                         @RequestParam(required = false) @Nullable String content,
                         @RequestBody(required = false) @Nullable MultipartFile file) {
        //判断是否登陆
        Integer uid = userUtil.getUid(request);
        redisUtils.del(uid+"contactsmsg"+contactId);
        if (msgType == 0) {
            iMsgService.savePlainMsg(uid, contactId, content);
        } else {
            iMsgService.saveFileMsg(uid, contactId, file);
        }
        return R.ok().message("消息发送成功");
    }


}

