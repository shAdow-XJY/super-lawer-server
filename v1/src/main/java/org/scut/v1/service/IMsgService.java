package org.scut.v1.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.scut.v1.entity.Msg;
import org.scut.v1.entity.VO.ContactVO;
import org.scut.v1.entity.VO.MsgVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ${author}
 * @since 2021-08-15
 */
public interface IMsgService extends IService<Msg> {

     List<ContactVO> getContacts(Integer uid,Integer pageNo,Integer pageSize);

     List<MsgVO> getMsgWithUid(Integer uid, Integer contactId, Integer pageNo, Integer pageSize);

     void savePlainMsg(Integer uid, Integer contactId, String content);

     void saveFileMsg(Integer uid, Integer contactId, MultipartFile file);

     void saveProjectMsg(Integer uid, Integer projectId, String content);

     void saveAssignMsg(Integer projectId, Integer lawyerId);
}
