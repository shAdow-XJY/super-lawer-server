package org.scut.v1.entity.VO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CaseRecordVO {
    private Integer caseId;
    @ApiModelProperty(value = "案件摘要")
    private String digest;
    @ApiModelProperty(value = "工作详情")
    private String detail;

}
