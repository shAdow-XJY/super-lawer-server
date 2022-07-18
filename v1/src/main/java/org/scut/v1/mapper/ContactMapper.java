package org.scut.v1.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.mapstruct.Mapper;
import org.scut.v1.entity.CaseType;
import org.scut.v1.entity.Contact;

@Mapper
public interface ContactMapper extends BaseMapper<Contact> {
}
