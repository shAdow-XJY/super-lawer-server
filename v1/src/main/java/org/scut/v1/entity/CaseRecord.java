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
@ApiModel(value="CaseRecord对象", description="")
public class CaseRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "案件摘要")
    private String digest;

    @ApiModelProperty(value = "工作详情")
    private String detail;

    @ApiModelProperty(value = "案件id")
    private Integer caseId;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    private Date modifyTime;


}
