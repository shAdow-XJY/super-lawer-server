package org.scut.v1.schedule;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.scut.v1.entity.Project;
import org.scut.v1.service.IContactService;
import org.scut.v1.service.IMsgService;
import org.scut.v1.service.IProjectService;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务
public class ProjectMsgTsk {
    @Resource
    private IProjectService projectService;
    @Resource
    private IMsgService msgService;
    @Resource
    private IContactService contactService;

    //3.添加定时任务
    @Scheduled(cron = "0 0 1 * * ?")
    private void configureTasks() {
        QueryWrapper<Project> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("status", 0, 1, 2);
        int pageSize = 100;
        int totalPage = projectService.count(queryWrapper) / pageSize + 1;
        for (int i = 1; i <= totalPage; i++) {
            Page<Project> page = new Page<>(i, pageSize);
            List<Project> projectList = projectService.page(page, queryWrapper).getRecords();
            for (Project project : projectList) {
                extracted(project);
            }
        }
    }

    @Transactional
    void extracted(Project project) {
        Date date = new Date();
        if (date.after(project.getEndTime())) {
            if (project.getStatus() == 2) {
                project.setStatus(3);
                projectService.updateById(project);
                msgService.saveProjectMsg(1, project.getFromId(), "温馨提醒,您的项目编号为:" + project.getId() + " 的项目已于" + project.getEndTime() + "结束。");
                msgService.saveProjectMsg(1, project.getToId(), "温馨提醒,您的项目编号为:" + project.getId() + " 的项目已于" + project.getEndTime() + "结束。");
                contactService.deleteContact(project.getFromId(), project.getToId());
            }
            project.setStatus(-1);

        }
    }
}