package org.scut.v1.entity.VO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class CaseVO {

    private String caseType;

    private String caseApplication;

    @ApiModelProperty(value = "案由")
    private String caseReason;

    @ApiModelProperty(value = "案委时间")
    private Date caseCommitTime;

    @ApiModelProperty(value = "立案时间")
    private Date caseFilingTime;

    @ApiModelProperty(value = "受理单位名称")
    private String acceptUnitName;

    @ApiModelProperty(value = "受理单位类型:法院,检察院,公安机关,仲裁机构")
    private String acceptUnitType;

    @ApiModelProperty(value = "受理单位地址")
    private String acceptAddr;

    @ApiModelProperty(value = "案件标的,单位:元")
    private Double caseBiaodiAmount;

    private List<PartyVO> partyVOS;

}
