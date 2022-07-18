package org.scut.v1.service;

import org.scut.v1.entity.Contact;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ${author}
 * @since 2021-08-27
 */
public interface IContactService extends IService<Contact> {

    List<Contact> getContactsByUid(Integer uid, Integer pageNo, Integer pageSize);

    void deleteContact(Integer fromId, Integer toId);
}
