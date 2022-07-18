package org.scut.v1.entity.DTO;

import lombok.Data;

import java.util.Date;

@Data
public class AppealVO {
    public Integer id;
    public String fromWhoName;
    public Integer matterId;
    public String appealReason;
    public Boolean isHandle;
    public Integer handleResult;
    public Date handleTime;
    public Date commitTime;
}
