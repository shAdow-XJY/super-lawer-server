package org.scut.v1.entity.VO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ProjServiceVO {

    @ApiModelProperty(value = "1代表1级服务")
    public Integer rank;

    @ApiModelProperty(value = "每月金额")
    public Integer price;

    public String serviceName;

    public String serviceContent;
}
