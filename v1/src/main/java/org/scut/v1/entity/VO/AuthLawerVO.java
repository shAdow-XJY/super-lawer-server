package org.scut.v1.entity.VO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AuthLawerVO {

    private String realName;
    private String idNumber;
    @ApiModelProperty(value = "“小学“,“初中”,“高中”,“硕士”,“博士”,“本科")
    private String degree;
    @ApiModelProperty(value = "年")
    private Integer workingTime;
    private Integer sex;
    private String idcardFrontUrl;
    private String idcardBackUrl;
    private String businessLicenseUrl;
}
