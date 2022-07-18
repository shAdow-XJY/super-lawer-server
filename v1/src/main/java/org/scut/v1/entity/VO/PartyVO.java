package org.scut.v1.entity.VO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PartyVO {


    @ApiModelProperty(value = "当事人名称")
    private String partyName;

    @ApiModelProperty(value = "委托方、对方当事人")
    private String partyType;

    @ApiModelProperty(value = "单位或者个人")
    private String organizeType;

    @ApiModelProperty(value = "查看party_attribute表格")
    private String partyAttribute;

}
