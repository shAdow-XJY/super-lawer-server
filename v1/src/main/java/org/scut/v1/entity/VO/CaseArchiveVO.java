package org.scut.v1.entity.VO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class CaseArchiveVO {
    @ApiModelProperty(value = "归档日期")
    private Date archiveDatetime;
    @ApiModelProperty(value = "档案保管地")
    private String archiveSavePlace;
    @ApiModelProperty(value = "归档人")
    private String archivePerson;
    @ApiModelProperty(value = "案件备注")
    private String remarks;
}
