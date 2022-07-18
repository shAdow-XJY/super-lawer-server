package org.scut.v1.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.netty.util.internal.StringUtil;
import org.scut.v1.entity.Contact;
import org.scut.v1.entity.Project;
import org.scut.v1.entity.User;
import org.scut.v1.entity.VO.ProjDetailVO;
import org.scut.v1.entity.VO.ProjServiceVO;
import org.scut.v1.entity.VO.ProjectCreateVO;
import org.scut.v1.entity.VO.ProjectVO;
import org.scut.v1.mapper.ProjectMapper;
import org.scut.v1.mapper.ServiceMapper;
import org.scut.v1.mapper.UserMapper;
import org.scut.v1.service.*;
import org.scut.v1.util.DateUtil;
import org.scut.v1.util.RedisUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2021-08-15
 */
@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements IProjectService {
    @Resource
    private ProjectMapper projectMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private ServiceMapper serviceMapper;
    @Resource
    private IMsgService msgService;
    @Resource
    private IUserService userService;
    @Resource
    private IContactService contactService;
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private IEnterpriseService enterpriseService;
    @Resource
    private ILawerService lawerService;
    @Override
    public List<ProjectVO> listProjects(Integer uid, String filter, Integer pageNo, Integer pageSize) {
        if (redisUtils.hashKey(uid + "ProjectVOList" + filter + pageNo)) {
            return (List<ProjectVO>) redisUtils.get(uid + "ProjectVOList" + filter + pageNo);
        }
        QueryWrapper<User> uQueryWrapper = new QueryWrapper<>();
        uQueryWrapper.eq("id", uid).select("user_type");
        int userType = userMapper.selectOne(uQueryWrapper).getUserType();
        String idType = "";
        if (userType == 3) {
            idType = "from_id";
        } else if (userType == 2) {
            idType = "to_id";
        }
        Integer state = null;
        switch (filter) {
            case "new":
                state = 0;
                break;
            case "running":
                state = 2;
                break;
            case "end":
                state = 3;
                break;
            case "reject":
                state = -1;
                break;
        }
        QueryWrapper<Project> queryWrapper = new QueryWrapper<>();
        if (!StringUtil.isNullOrEmpty(idType)) queryWrapper.eq(idType, uid);
        if (state != null) {
            if (state == 0 && idType.equals("from_id")) {
                queryWrapper.in("status", 0, 1);
            } else if (state == 0 && idType.equals("to_id")) {
                queryWrapper.eq("status", 1);
            } else
                queryWrapper.eq("status", state);
        }
        queryWrapper.orderBy(true, false, "create_time");
        Page<Project> page = new Page<>(pageNo, pageSize);
        List<Project> projects = projectMapper.selectPage(page, queryWrapper).getRecords();
        List<ProjectVO> projectVOList = new ArrayList<>();
        for (Project project : projects) {
            ProjectVO projectVO = new ProjectVO();
            projectVO.setProjectId(project.getId());
            projectVO.setCommitTime(project.getCommitTime());
            projectVO.setProjectName(project.getProjectName());
            projectVO.setEndTime(project.getEndTime());
            int fromId = project.getFromId();
            User fromUser = userMapper.selectById(fromId);
            projectVO.setFromName(fromUser.getNickname());
            projectVOList.add(projectVO);
        }
        redisUtils.set(uid + "ProjectVOList" + filter + pageNo, projectVOList, 10);
        return projectVOList;
    }

    @Override
    public ProjDetailVO getDetail(Integer projectId) {
        String key="projectDetail"+projectId;
        if(redisUtils.hashKey(key)){
            return (ProjDetailVO) redisUtils.get(key);
        }
        Project project = projectMapper.selectById(projectId);
        ProjDetailVO projDetailVO = new ProjDetailVO();
        BeanUtils.copyProperties(project, projDetailVO);
        projDetailVO.setService(serviceMapper.selectById(project.getServiceId()));
        projDetailVO.setFromName(userService.getNameById(project.getFromId()));
        projDetailVO.setEnterprise(enterpriseService.getEnterpriseByUid(project.getFromId()));
        if(project.getStatus()==2||project.getStatus()==3&&project.getToId() != null){
            projDetailVO.setToName(userService.getNameById(project.getToId()));
            projDetailVO.setLawer(lawerService.getLawerByUid(project.getToId()));
        }
        redisUtils.set(key,projDetailVO,30);
        return projDetailVO;
    }

    @Override
    public ProjServiceVO getProjService(Integer projectId) {
        QueryWrapper<Project> projQueryWrapper = new QueryWrapper<>();
        projQueryWrapper.select("service_id").eq("id", projectId);
        String serviceId = projectMapper.selectOne(projQueryWrapper).getServiceId();
        org.scut.v1.entity.Service service = serviceMapper.selectById(serviceId);
        ProjServiceVO projServiceVO = new ProjServiceVO();
        BeanUtils.copyProperties(service, projServiceVO);
        return projServiceVO;
    }

    @Override
    @Transactional
    public void approveProject(Integer projectId) {
        Project project = baseMapper.selectById(projectId);
        project.setId(projectId);
        project.setStatus(2);
        int pricePerMonth = serviceMapper.selectById(project.getServiceId()).getPrice();

        //TODO 价格的计算有点问题，先这样，看下怎么计算合理
        project.setTotalMoney(pricePerMonth / 30.0 * DateUtil.differentDays(new Date(), project.getEndTime()));
        project.setIsPayment(false);

        baseMapper.updateById(project);
        msgService.saveProjectMsg(1, project.getFromId(), "您的项目编号为:" + project.getId() + " 的律师处理已经审批完成，审批结果为同意。持续时间为" + new Date() + "~" + project.getEndTime() + ",总费用为:" + project.getTotalMoney() + "。项目已经启动。");

        QueryWrapper<Contact> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", project.getFromId());
        queryWrapper.eq("contact_id", project.getToId());
        if (contactService.count(queryWrapper) > 0) return;
        Contact contact = new Contact();
        contact.setUid(project.getFromId());
        contact.setContactId(project.getToId());
        contactService.save(contact);
        contact.setContactId(project.getFromId());
        contact.setUid(project.getToId());
        contactService.save(contact);
    }


    @Override
    @Transactional
    public void rejectProject(Integer projectId) {
        Project project = new Project();
        project.setId(projectId);
        project.setToId(null);
        project.setStatus(0);
        baseMapper.updateById(project);
        project = baseMapper.selectById(project);
        msgService.saveProjectMsg(1, project.getFromId(), "您的项目编号为:" + project.getId() + " 的律师处理已经审批完成，审批结果为拒绝；请联系管理员重新进行分配");
    }

    @Override
    public void commitProject(Integer uid, ProjectCreateVO projectCreateVO) {
        Project project = new Project();
        project.setCommitTime(new Date());
        project.setStatus(0);
        BeanUtils.copyProperties(projectCreateVO, project);
        project.setFromId(uid);
        baseMapper.insert(project);
    }

    @Override
    public List<ProjectVO> listUnassignedProjects(Integer pageNo, Integer pageSize) {
        QueryWrapper<Project> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 0);
        Page<Project> page = new Page<>(pageNo, pageSize);
        List<Project> projects = projectMapper.selectPage(page, queryWrapper).getRecords();
        List<ProjectVO> projectVOList = new ArrayList<>();
        for (Project project : projects) {
            ProjectVO projectVO = new ProjectVO();
            BeanUtils.copyProperties(project, projectVO);
            projectVO.setProjectId(project.getId());
            int fromId = project.getFromId();
            User fromUser = userMapper.selectById(fromId);
            projectVO.setFromName(fromUser.getNickname());
            projectVOList.add(projectVO);
        }
        return projectVOList;
    }

    @Override
    public void rejectAssignProject(Integer projectId) {
        Project project = new Project();
        project.setId(projectId);
        project.setStatus(-1);
        projectMapper.updateById(project);
    }

    @Override
    public List<ProjectVO> listFeeList(Integer pageNo, Integer pageSize) {
        QueryWrapper<Project> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("status", 2, 3).eq("is_payment", false).isNotNull("pay_picture_url");
        Page<Project> page = new Page<>(pageNo, pageSize);
        List<Project> projects = projectMapper.selectPage(page, queryWrapper).getRecords();
        List<ProjectVO> projectVOList = new ArrayList<>();
        for (Project project : projects) {
            ProjectVO projectVO = new ProjectVO();
            BeanUtils.copyProperties(project, projectVO);
            projectVO.setProjectId(project.getId());
            int fromId = project.getFromId();
            User fromUser = userMapper.selectById(fromId);
            projectVO.setFromName(fromUser.getNickname());
            projectVOList.add(projectVO);
        }
        return projectVOList;
    }

    @Override
    @Transactional
    public void assignProject(Integer projectId, Integer lawyerId) {
        Project project = new Project();
        project.setId(projectId);
        project.setStatus(1);
        project.setToId(lawyerId);
        projectMapper.updateById(project);
        msgService.saveAssignMsg(projectId, lawyerId);
    }
}
