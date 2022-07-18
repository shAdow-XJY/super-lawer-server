package org.scut.v1.entity.VO;

import com.sun.xml.ws.developer.Serialization;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
@Serialization
@Data
public class MatterVO {
    public Integer matterId;
    public Integer fromName;
    public String matterContent;
    public String matterReason;
    public Boolean isHandle;
    @ApiModelProperty(value = "0代表拒绝,1代表同意")
    public Integer handleResult;
    public Date commitTime;
    public Date handleTime;
}
