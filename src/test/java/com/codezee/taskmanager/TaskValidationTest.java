package com.codezee.taskmanager;

import com.codezee.taskmanager.dto.TaskRequest;
import com.codezee.taskmanager.model.TaskStatus;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskValidationTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        factory.close();
    }

    @Test
    void rejectsBlankTitle() {
        TaskRequest request = new TaskRequest("  ", "desc", TaskStatus.TODO, LocalDate.now().plusDays(1));
        Set<ConstraintViolation<TaskRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> "title".equals(v.getPropertyPath().toString())));
    }

    @Test
    void rejectsTitleLongerThan200() {
        String longTitle = "x".repeat(201);
        TaskRequest request = new TaskRequest(longTitle, "desc", TaskStatus.TODO, LocalDate.now().plusDays(1));
        Set<ConstraintViolation<TaskRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("200")));
    }

    @Test
    void rejectsPastDueDate() {
        TaskRequest request = new TaskRequest(
                "Valid title",
                "ok",
                TaskStatus.TODO,
                LocalDate.now().minusDays(1));
        Set<ConstraintViolation<TaskRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> "dueDate".equals(v.getPropertyPath().toString())));
    }

    @Test
    void acceptsValidRequest() {
        TaskRequest request = new TaskRequest(
                "Ship validation",
                "bean validation on DTOs",
                TaskStatus.IN_PROGRESS,
                LocalDate.now().plusWeeks(1));
        Set<ConstraintViolation<TaskRequest>> violations = validator.validate(request);
        assertEquals(0, violations.size());
    }

    @Test
    void rejectsDescriptionOver2000Chars() {
        TaskRequest request = new TaskRequest(
                "Title",
                "d".repeat(2001),
                TaskStatus.TODO,
                null);
        Set<ConstraintViolation<TaskRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> "description".equals(v.getPropertyPath().toString())));
    }
}
