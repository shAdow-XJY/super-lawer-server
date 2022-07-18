package org.scut.v1.entity.VO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.scut.v1.entity.Enterprise;
import org.scut.v1.entity.Lawer;
import org.scut.v1.entity.Service;

import java.util.Date;

@Data
public class ProjDetailVO {

    private Integer id;
    // 企业id
    private String fromName;
    public Lawer lawer;
    public Enterprise enterprise;
    private String toName;
    private String projectName;
    private String projectContent;
    private Service service;
    private Date commitTime;
    private Date endTime;
    @ApiModelProperty(value = "法律咨询"+"案件申报")
    private String projectType;
    private Double totalMoney;
    @ApiModelProperty(value = "是否支付")
    private Boolean isPayment;
    private String payPictureUrl;
    private Date createTime;
    private Date modifyTime;
    private Integer status;
}
