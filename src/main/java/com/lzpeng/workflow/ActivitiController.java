package com.lzpeng.workflow;

import org.activiti.engine.*;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: Henry Yan
 */
@RestController
@RequestMapping("/activiti")
public class ActivitiController {

    @Autowired
    private ManagementService managementService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ProcessEngine processEngine;

    @Autowired
    private HttpServletResponse response;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @RequestMapping("/engine/info")
    public Map<String, String> engineProperties() {
        return managementService.getProperties();
    }

    @GetMapping("/processes")
    public List processes() {
        List ret = new ArrayList();
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().list();
        for (ProcessDefinition definition : list) {
            Map<String, Object> map = new HashMap();
            map.put("id", definition.getId());
            map.put("key", definition.getKey());
            map.put("name", definition.getName());
            map.put("deploymentId", definition.getDeploymentId());
            ret.add(map);
        }
        return ret;
    }

    @PostMapping("/process/start/{processDefinitionId}")
    public void startProcess(@PathVariable("processDefinitionId") String processDefinitionId) throws IOException {

        Map<String, Object> map = new HashMap<>();
        map.put("name", "lzpeng");
        map.put("random", RandomUtils.nextInt(1000,2000));
        String businessKey = "businessKey" + 1;
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinitionId, businessKey, map);
        System.out.println("成功启动了流程：" + processInstance.getId());
        response.sendRedirect("/activiti/tasks");
    }

    @GetMapping("/tasks")
    public List tasks() {
        List ret = new ArrayList();
        List<Task> tasks = taskService.createTaskQuery().orderByTaskCreateTime().desc().list();
        for (Task task : tasks) {
            taskService.setOwner(task.getId(), "user");
            taskService.setAssignee(task.getId(), "admin");
            task.getProcessVariables().forEach((k, v) -> {
                System.out.println(k + " : " + v);
            });
            Map<String, Object> map = new HashMap();
            map.put("id", task.getId());
            map.put("name", task.getName());
            map.put("createTime", task.getCreateTime());
            map.put("owner", task.getOwner());
            map.put("assignee", task.getAssignee());
            map.put("processInstanceId", task.getProcessInstanceId());
            map.put("executionId", task.getExecutionId());
            map.put("processDefinitionId", task.getProcessDefinitionId());
            ret.add(map);
        }
        return ret;
    }

    @PostMapping("/task/complete/{taskId}")
    public void completeTask(@PathVariable("taskId") String taskId) throws IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("submitType", "Y");
        map.put("tlApprove", "Y");
        map.put("hrApprove", "Y");
        taskService.complete(taskId, map);
        response.sendRedirect("/activiti/tasks");
    }

}