package org.scut.v1.entity.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("结案使用VO")
public class CaseSettlementVO {
    @ApiModelProperty(value = "案件实际收费金额:单位元")
    private Double caseProfitAmount;
    @ApiModelProperty(value = "收费方式")
    private String chargeMethod;
    @ApiModelProperty(value = "结案日期")
    private Date settlementDate;
    @ApiModelProperty(value = "结案状态,参见settlement_status")
    private String settlementStatus;
    @ApiModelProperty(value = "案件备注")
    private String remarks;
}
