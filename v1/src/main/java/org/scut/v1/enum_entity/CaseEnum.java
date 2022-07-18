package org.scut.v1.enum_entity;


import lombok.AllArgsConstructor;

/**
 * 关于案件类型和对应案件程序之间的映射关系
 */
@AllArgsConstructor
public enum CaseEnum {

    CIVIL_CASES("民事案件", "仲裁,初审一审，初审二审，重审一审，重审二审,申请再审,再审一审,再审二审,执行,其他"),
    COMMERCIAL_CASES("商事案件","仲裁,初审一审，初审二审，重审一审，重审二审,申请再审,再审一审,再审二审,执行,其他"),
    ARBITRATION_CASES("仲裁案件","仲裁,初审一审，初审二审，重审一审，重审二审,申请再审,再审一审,再审二审,执行,其他" ),
    CRIMINAL_CASES("刑事案件","立案,侦查,审查起诉,初审一审，初审二审，重审一审，重审二审,申请再审,再审一审,再审二审,执行,其他"),
    ADMINISTRATIVE_CASES("行政案件","初审一审，初审二审，重审一审，重审二审,申请再审,再审一审,再审二审,执行,其他");

    private final String caseType;
    private final String caseApplication;
}