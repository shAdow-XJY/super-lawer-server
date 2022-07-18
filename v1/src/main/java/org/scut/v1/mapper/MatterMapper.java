package org.scut.v1.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.mapstruct.Mapper;
import org.scut.v1.entity.Matter;
import org.scut.v1.entity.VO.MatterVO;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author ${author}
 * @since 2021-08-15
 */
@Mapper
public interface MatterMapper extends BaseMapper<Matter> {

    @Select("select " +
            "m.id as matter_id,m.matter_content,m.matter_reason,m.is_handle,m.handle_result,m.commit_time,m.handle_time,u.nickname " +
            "from matter as m join user u on m.from_id=u.id " +
            "where m.to_id=#{uid}")
     List<MatterVO> listMattersByPage(@Param("uid") Integer uid, Page<MatterVO> page);
}
