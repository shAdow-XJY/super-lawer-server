package org.scut.v1.enum_entity;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum UserTypeEnum {
    UN_AUTH_USER(0),
    ADMIN(1),
    LAWER(2),
    ENTERPRISE(3);

    private int code;

}
