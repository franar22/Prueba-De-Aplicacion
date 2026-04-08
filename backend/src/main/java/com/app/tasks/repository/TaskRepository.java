package com.app.tasks.repository;

import com.app.tasks.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // Spring Data genera el SQL automáticamente a partir del nombre del método
    List<Task> findByStatusOrderByCreatedAtDesc(Task.TaskStatus status);

    List<Task> findAllByOrderByCreatedAtDesc();
}
