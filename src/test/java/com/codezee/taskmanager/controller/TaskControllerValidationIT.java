package com.codezee.taskmanager.controller;

import com.codezee.taskmanager.exception.GlobalExceptionHandler;
import com.codezee.taskmanager.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TaskController.class)
@Import(GlobalExceptionHandler.class)
class TaskControllerValidationIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Test
    void createTask_returnsJsonErrorWhenTitleMissing() throws Exception {
        String body = """
                {
                  "description": "no title here",
                  "status": "TODO",
                  "dueDate": "%s"
                }
                """.formatted(LocalDate.now().plusDays(3));

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.violations[*].field", hasItem("title")));
    }

    @Test
    void createTask_returnsJsonErrorWhenDueDateInPast() throws Exception {
        String body = """
                {
                  "title": "Late task",
                  "dueDate": "%s"
                }
                """.formatted(LocalDate.now().minusDays(2));

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.violations[*].field", hasItem("dueDate")))
                .andExpect(jsonPath("$.violations[*].message", hasItem(containsString("past"))));
    }

    @Test
    void createTask_returnsJsonErrorForMalformedJson() throws Exception {
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{not-json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Malformed JSON request"));
    }
}
