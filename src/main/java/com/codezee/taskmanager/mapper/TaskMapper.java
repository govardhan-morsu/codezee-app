package com.codezee.taskmanager.mapper;

import com.codezee.taskmanager.dto.TaskRequest;
import com.codezee.taskmanager.dto.TaskResponse;
import com.codezee.taskmanager.model.Task;
import com.codezee.taskmanager.model.TaskStatus;
import org.springframework.stereotype.Component;

/**
 * Converts between task entities and request/response DTOs.
 */
@Component
public class TaskMapper {

    public Task toEntity(TaskRequest request) {
        if (request == null) {
            return null;
        }
        TaskStatus status = request.getStatus() != null ? request.getStatus() : TaskStatus.TODO;
        return new Task(request.getTitle(), request.getDescription(), status, request.getDueDate());
    }

    public void applyUpdates(Task existing, TaskRequest request) {
        if (request.getTitle() != null) {
            existing.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            existing.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            existing.setStatus(request.getStatus());
        }
        if (request.getDueDate() != null) {
            existing.setDueDate(request.getDueDate());
        }
    }

    public TaskResponse toResponse(Task task) {
        if (task == null) {
            return null;
        }
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getDueDate());
    }
}
