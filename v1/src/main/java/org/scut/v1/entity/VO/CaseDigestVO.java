package org.scut.v1.entity.VO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class CaseDigestVO {

    public Integer id;
    @ApiModelProperty(value = "原告方")
    public List<String> plaintiffs;
    @ApiModelProperty(value ="被告方")
    public List<String> defendants;
    public String caseType;
    public String caseApplication;
    @ApiModelProperty(value = "案由")
    public String caseReason;
    @ApiModelProperty(value = "案件标的,单位:元")
    public Double caseBiaodiAmount;
    public String caseName;


}
