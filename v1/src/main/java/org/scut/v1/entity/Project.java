package org.scut.v1.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
@ApiModel(value = "Project对象", description = "")
public class Project implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    // 企业id
    private Integer fromId;
    @TableField(value = "to_id", updateStrategy = FieldStrategy.IGNORED)
    private Integer toId;

    private String projectName;

    private String projectContent;

    private String serviceId;

    private Date commitTime;

    private Date endTime;

    @ApiModelProperty(value = "法律咨询" + "案件申报")
    private String projectType;

    private Double totalMoney;

    @ApiModelProperty(value = "是否支付")
    private Boolean isPayment;
    
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String payPictureUrl;

    private Date createTime;

    private Date modifyTime;

    @ApiModelProperty(value = "-1代表项目被管理员拒绝,0代表未分配律师，1代表已分配但律师未进行处理，2代表律师已经同意-服务开始生效,3代表服务已结束")
    private Integer status;

}
