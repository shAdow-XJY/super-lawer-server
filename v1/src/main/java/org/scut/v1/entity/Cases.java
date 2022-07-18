package org.scut.v1.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

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
@ApiModel(value = "Case对象", description = "")
public class Cases implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "案件类型")
    private String caseType;

    @ApiModelProperty(value = "案件程序")
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

    @ApiModelProperty(value = "办案律师id,唯一标识")
    private Integer lawerId;

    @ApiModelProperty(value = "案件文档路径")
    private String caseFilesPath;

    @ApiModelProperty(value = "收费方式")
    private String chargeMethod;

    @ApiModelProperty(value = "案件标的,单位:元")
    private Double caseBiaodiAmount;

    @ApiModelProperty(value = "案件合同金额")
    private Double caseWinAmount;

    @ApiModelProperty(value = "案件实际收费金额:单位元")
    private Double caseProfitAmount;

    @ApiModelProperty(value = "案件受理案号")
    private String caseAcceptNo;

    @ApiModelProperty(value = "案件裁决日期")
    private Date caseArbitramentDate;

    @ApiModelProperty(value = "案件裁决结果,查看case_result")
    private String caseResult;

    @ApiModelProperty(value = "结案日期")
    private Date settlementDate;

    @ApiModelProperty(value = "结案状态,参见settlement_status")
    private String settlementStatus;

    @ApiModelProperty(value = "归档日期")
    private Date archiveDatetime;

    @ApiModelProperty(value = "档案保管地")
    private String archiveSavePlace;

    @ApiModelProperty(value = "归档人")
    private String archivePerson;

    @ApiModelProperty(value = "关联项目的id")
    private String relativeCaseIds;

    @ApiModelProperty(value = "案件备注")
    private String remarks;

    private String caseName;

    private Date createTime;

    private Date modifyTime;

    @ApiModelProperty(value = "在办,结案,归档")
    private String caseStatus;

}
