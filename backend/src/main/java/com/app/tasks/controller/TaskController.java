package com.app.tasks.controller;

import com.app.tasks.model.Task;
import com.app.tasks.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // -------------------------------------------------------
    // GET /api/tasks
    // GET /api/tasks?status=PENDIENTE
    // -------------------------------------------------------
    @GetMapping
    public ResponseEntity<List<Task>> getTasks(
            @RequestParam(required = false) Task.TaskStatus status) {

        List<Task> tasks = (status != null)
                ? taskService.getTasksByStatus(status)
                : taskService.getAllTasks();

        return ResponseEntity.ok(tasks);
    }

    // -------------------------------------------------------
    // GET /api/tasks/{id}
    // -------------------------------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // -------------------------------------------------------
    // POST /api/tasks
    // Body: { "title": "...", "description": "...", "status": "PENDIENTE" }
    // -------------------------------------------------------
    @PostMapping
    public ResponseEntity<Task> createTask(@Valid @RequestBody Task task) {
        Task created = taskService.createTask(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // -------------------------------------------------------
    // PUT /api/tasks/{id}
    // Body: campos que quieras actualizar
    // -------------------------------------------------------
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(
            @PathVariable Long id,
            @RequestBody Task updatedTask) {

        return taskService.updateTask(id, updatedTask)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // -------------------------------------------------------
    // DELETE /api/tasks/{id}
    // -------------------------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        boolean deleted = taskService.deleteTask(id);
        if (deleted) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Tarea eliminada correctamente");
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.notFound().build();
    }

    // -------------------------------------------------------
    // GET /api/tasks/health  — usado por Docker healthcheck
    // -------------------------------------------------------
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "tasks-api");
        health.put("pendientes", taskService.countByStatus(Task.TaskStatus.PENDIENTE));
        health.put("en_progreso", taskService.countByStatus(Task.TaskStatus.EN_PROGRESO));
        health.put("completadas", taskService.countByStatus(Task.TaskStatus.COMPLETADA));
        return ResponseEntity.ok(health);
    }
}
