package org.scut.v1.entity.VO;

import lombok.Data;
import org.scut.v1.entity.CaseRecord;
import org.scut.v1.entity.Cases;
import org.scut.v1.entity.Party;

import java.util.List;

@Data
public class CaseDetailVO {
    private Cases cases;
    private List<Party> parties;
    private List<CaseRecord> caseRecords;
}
