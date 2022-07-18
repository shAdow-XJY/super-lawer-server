package org.scut.v1.entity.VO;


import lombok.Data;

import java.util.Date;

@Data
public class ProjectVO {
    public int projectId;
    public String fromName;
    public String projectName;
    public Date commitTime;
    public Date endTime;
}
