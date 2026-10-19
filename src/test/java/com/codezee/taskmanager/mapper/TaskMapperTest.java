package com.codezee.taskmanager.mapper;

import com.codezee.taskmanager.dto.TaskRequest;
import com.codezee.taskmanager.dto.TaskResponse;
import com.codezee.taskmanager.model.Task;
import com.codezee.taskmanager.model.TaskStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TaskMapperTest {

    private final TaskMapper mapper = new TaskMapper();

    @Test
    void toEntity_defaultsStatusToTodo() {
        TaskRequest request = new TaskRequest("Mapper check", "desc", null, LocalDate.of(2026, 11, 15));
        Task entity = mapper.toEntity(request);
        assertEquals("Mapper check", entity.getTitle());
        assertEquals(TaskStatus.TODO, entity.getStatus());
        assertEquals(LocalDate.of(2026, 11, 15), entity.getDueDate());
    }

    @Test
    void applyUpdates_onlyTouchesProvidedFields() {
        Task existing = new Task("Old", "old desc", TaskStatus.TODO, LocalDate.of(2026, 10, 1));
        existing.setId(7L);
        TaskRequest request = new TaskRequest();
        request.setTitle("New title");
        request.setStatus(TaskStatus.DONE);

        mapper.applyUpdates(existing, request);

        assertEquals("New title", existing.getTitle());
        assertEquals("old desc", existing.getDescription());
        assertEquals(TaskStatus.DONE, existing.getStatus());
        assertEquals(LocalDate.of(2026, 10, 1), existing.getDueDate());
    }

    @Test
    void toResponse_mapsAllFields() {
        Task task = new Task("Respond", "body", TaskStatus.IN_PROGRESS, LocalDate.of(2026, 12, 1));
        task.setId(42L);
        TaskResponse response = mapper.toResponse(task);
        assertEquals(42L, response.getId());
        assertEquals("Respond", response.getTitle());
        assertEquals(TaskStatus.IN_PROGRESS, response.getStatus());
    }

    @Test
    void nullInputs_returnNull() {
        assertNull(mapper.toEntity(null));
        assertNull(mapper.toResponse(null));
    }
}
