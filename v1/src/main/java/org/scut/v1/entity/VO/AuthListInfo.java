package org.scut.v1.entity.VO;

import lombok.Data;

import java.util.Date;

@Data
public class AuthListInfo {
    private String nickName;
    private Date authTime;
    private String authType;
    private int id;
}
