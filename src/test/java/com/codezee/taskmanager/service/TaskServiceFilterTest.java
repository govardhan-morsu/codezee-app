package com.codezee.taskmanager.service;

import com.codezee.taskmanager.model.Task;
import com.codezee.taskmanager.model.TaskStatus;
import com.codezee.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceFilterTest {

    @Mock
    private TaskRepository taskRepository;

    private TaskService taskService;

    @BeforeEach
    void setUp() {
        taskService = new TaskService(taskRepository);
    }

    @Test
    void findFiltered_passesStatusAndDueDatesToRepository() {
        Task task = new Task("Ship filters", "status + dueDate", TaskStatus.TODO, LocalDate.of(2026, 11, 1));
        Page<Task> page = new PageImpl<>(List.of(task));
        when(taskRepository.findFiltered(
                eq(TaskStatus.TODO),
                eq(LocalDate.of(2026, 12, 1)),
                eq(LocalDate.of(2026, 10, 1)),
                any(Pageable.class)))
                .thenReturn(page);

        Page<Task> result = taskService.findFiltered(
                TaskStatus.TODO,
                LocalDate.of(2026, 12, 1),
                LocalDate.of(2026, 10, 1),
                0,
                10);

        assertEquals(1, result.getTotalElements());
        assertEquals("Ship filters", result.getContent().get(0).getTitle());
    }

    @Test
    void findFiltered_clampsPageSizeAndUsesZeroBasedPage() {
        when(taskRepository.findFiltered(isNull(), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(Page.empty());

        taskService.findFiltered(null, null, null, -3, 500);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(taskRepository).findFiltered(isNull(), isNull(), isNull(), pageableCaptor.capture());
        Pageable pageable = pageableCaptor.getValue();
        assertEquals(0, pageable.getPageNumber());
        assertEquals(100, pageable.getPageSize());
    }

    @Test
    void findFiltered_rejectsInvertedDueDateRange() {
        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> taskService.findFiltered(
                        null,
                        LocalDate.of(2026, 1, 1),
                        LocalDate.of(2026, 6, 1),
                        0,
                        20));

        assertEquals(400, ex.getStatusCode().value());
        assertTrue(ex.getReason().contains("dueAfter"));
    }

    @Test
    void findFiltered_defaultsSizeWhenNonPositive() {
        when(taskRepository.findFiltered(isNull(), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(Page.empty());

        taskService.findFiltered(null, null, null, 1, 0);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(taskRepository).findFiltered(isNull(), isNull(), isNull(), pageableCaptor.capture());
        assertEquals(20, pageableCaptor.getValue().getPageSize());
        assertEquals(1, pageableCaptor.getValue().getPageNumber());
    }

    @Test
    void findFiltered_allowsEqualDueBeforeAndDueAfter() {
        LocalDate day = LocalDate.of(2026, 11, 15);
        when(taskRepository.findFiltered(isNull(), eq(day), eq(day), any(Pageable.class)))
                .thenReturn(Page.empty());

        taskService.findFiltered(null, day, day, 0, 20);

        verify(taskRepository).findFiltered(isNull(), eq(day), eq(day), any(Pageable.class));
    }
}
