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
@ApiModel(value="Lawer对象", description="")
public class Lawer implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer uid;

    private String realName;

    private String idNumber;

    @ApiModelProperty(value = "“小学“,“初中”,“高中”,“硕士”,“博士”,“本科")
    private String degree;

    @ApiModelProperty(value = "年")
    private Integer workingTime;

    private Date authTime;

    private String idcardFront;

    private String idcardBack;

    private String businessLicense;

    private Integer sex;

    private Date createTime;

    private Date modifyTime;

    private int status;

}
