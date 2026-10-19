package com.codezee.taskmanager.service;

import com.codezee.taskmanager.model.Task;
import com.codezee.taskmanager.model.TaskStatus;
import com.codezee.taskmanager.repository.TaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class TaskService {

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    public Page<Task> findFiltered(
            TaskStatus status,
            LocalDate dueBefore,
            LocalDate dueAfter,
            int page,
            int size) {
        if (page < 0) {
            page = 0;
        }
        if (size <= 0) {
            size = DEFAULT_PAGE_SIZE;
        }
        if (size > MAX_PAGE_SIZE) {
            size = MAX_PAGE_SIZE;
        }
        if (dueBefore != null && dueAfter != null && dueAfter.isAfter(dueBefore)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "dueAfter cannot be after dueBefore");
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by("dueDate").ascending().and(Sort.by("id")));
        return taskRepository.findFiltered(status, dueBefore, dueAfter, pageable);
    }

    public Task findById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Task not found with id: " + id));
    }

    public Task create(Task task) {
        if (task.getStatus() == null) {
            task.setStatus(TaskStatus.TODO);
        }
        return taskRepository.save(task);
    }

    public Task update(Long id, Task updates) {
        Task existing = findById(id);
        if (updates.getTitle() != null) {
            existing.setTitle(updates.getTitle());
        }
        if (updates.getDescription() != null) {
            existing.setDescription(updates.getDescription());
        }
        if (updates.getStatus() != null) {
            existing.setStatus(updates.getStatus());
        }
        if (updates.getDueDate() != null) {
            existing.setDueDate(updates.getDueDate());
        }
        return taskRepository.save(existing);
    }

    public void delete(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Task not found with id: " + id);
        }
        taskRepository.deleteById(id);
    }
}
