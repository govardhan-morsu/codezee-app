package com.codezee.taskmanager.service;

import com.codezee.taskmanager.dto.TaskRequest;
import com.codezee.taskmanager.dto.TaskResponse;
import com.codezee.taskmanager.mapper.TaskMapper;
import com.codezee.taskmanager.model.Task;
import com.codezee.taskmanager.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private static final Logger log = LoggerFactory.getLogger(TaskServiceImpl.class);

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    public TaskServiceImpl(TaskRepository taskRepository, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> findAll() {
        log.debug("Listing all tasks");
        List<TaskResponse> tasks = taskRepository.findAll().stream()
                .map(taskMapper::toResponse)
                .toList();
        log.info("Found {} tasks", tasks.size());
        return tasks;
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponse findById(Long id) {
        log.debug("Looking up task id={}", id);
        return taskMapper.toResponse(requireTask(id));
    }

    @Override
    public TaskResponse create(TaskRequest request) {
        log.info("Creating task title='{}'", request.getTitle());
        Task saved = taskRepository.save(taskMapper.toEntity(request));
        log.info("Created task id={}", saved.getId());
        return taskMapper.toResponse(saved);
    }

    @Override
    public TaskResponse update(Long id, TaskRequest request) {
        log.info("Updating task id={}", id);
        Task existing = requireTask(id);
        taskMapper.applyUpdates(existing, request);
        Task saved = taskRepository.save(existing);
        log.debug("Updated task id={} status={}", saved.getId(), saved.getStatus());
        return taskMapper.toResponse(saved);
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting task id={}", id);
        if (!taskRepository.existsById(id)) {
            log.warn("Delete failed; task id={} not found", id);
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Task not found with id: " + id);
        }
        taskRepository.deleteById(id);
        log.info("Deleted task id={}", id);
    }

    private Task requireTask(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Task id={} not found", id);
                    return new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Task not found with id: " + id);
                });
    }
}
