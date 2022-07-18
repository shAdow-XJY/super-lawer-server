package org.scut.v1.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.scut.v1.entity.Project;
import org.scut.v1.entity.VO.ProjDetailVO;
import org.scut.v1.entity.VO.ProjServiceVO;
import org.scut.v1.entity.VO.ProjectCreateVO;
import org.scut.v1.entity.VO.ProjectVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ${author}
 * @since 2021-08-15
 */
public interface IProjectService extends IService<Project> {
    void commitProject(Integer uid,ProjectCreateVO projectCreateVO);

    List<ProjectVO> listProjects(Integer uid, String filter, Integer pageNo, Integer pageSize);

    ProjDetailVO getDetail(Integer projectId);

    ProjServiceVO getProjService(Integer projectId);

    void approveProject(Integer projectId);

    void rejectProject(Integer projectId);

    void assignProject(Integer projectId, Integer lawyerId);

    List<ProjectVO> listUnassignedProjects(Integer pageNo, Integer pageSize);

    void rejectAssignProject(Integer projectId);

    List<ProjectVO> listFeeList(Integer pageNo, Integer pageSize);
}
