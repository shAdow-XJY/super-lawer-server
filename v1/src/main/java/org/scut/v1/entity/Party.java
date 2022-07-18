package org.scut.v1.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author ${author}
 * @since 2021-08-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="Party对象", description="")
public class Party implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "当事人名称")
    private String partyName;

    @ApiModelProperty(value = "0委托方、1对方当事人")
    private String partyType;

    @ApiModelProperty(value = "单位或者个人")
    private String organizeType;

    @ApiModelProperty(value = "查看party_attribute表格")
    private String partyAttribute;

    private Integer caseId;

    private Date createTime;

    private Date modifyTime;


}
