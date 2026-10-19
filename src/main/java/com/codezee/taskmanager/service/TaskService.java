package com.codezee.taskmanager.service;

import com.codezee.taskmanager.dto.TaskRequest;
import com.codezee.taskmanager.dto.TaskResponse;

import java.util.List;

public interface TaskService {

    List<TaskResponse> findAll();

    TaskResponse findById(Long id);

    TaskResponse create(TaskRequest request);

    TaskResponse update(Long id, TaskRequest request);

    void delete(Long id);
}
