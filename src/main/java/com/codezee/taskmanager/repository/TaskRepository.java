package com.codezee.taskmanager.repository;

import com.codezee.taskmanager.model.Task;
import com.codezee.taskmanager.model.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("""
            SELECT t FROM Task t
            WHERE (:status IS NULL OR t.status = :status)
              AND (:dueBefore IS NULL OR t.dueDate <= :dueBefore)
              AND (:dueAfter IS NULL OR t.dueDate >= :dueAfter)
            """)
    Page<Task> findFiltered(
            @Param("status") TaskStatus status,
            @Param("dueBefore") LocalDate dueBefore,
            @Param("dueAfter") LocalDate dueAfter,
            Pageable pageable);
}
