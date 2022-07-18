package org.scut.v1.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
@ApiModel(value="Service对象", description="")
public class Service implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "1代表1级服务")
    private Integer rank;

    @ApiModelProperty(value = "每月金额")
    private Integer price;

    private String serviceName;

    @ApiModelProperty(value = "模版文件夹地址")
    private String templatePath;

    private String serviceContent;

    private Date createTime;

    private Date modifyTime;


}
