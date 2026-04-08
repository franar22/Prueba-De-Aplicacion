package com.app.tasks.service;

import com.app.tasks.model.Task;
import com.app.tasks.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    // Inyección por constructor (recomendada sobre @Autowired en campo)
    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // ---------- LISTAR ----------

    public List<Task> getAllTasks() {
        return taskRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Task> getTasksByStatus(Task.TaskStatus status) {
        return taskRepository.findByStatusOrderByCreatedAtDesc(status);
    }

    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    // ---------- CREAR ----------

    public Task createTask(Task task) {
        // Si no viene status lo ponemos en PENDIENTE
        if (task.getStatus() == null) {
            task.setStatus(Task.TaskStatus.PENDIENTE);
        }
        return taskRepository.save(task);
    }

    // ---------- ACTUALIZAR ----------

    public Optional<Task> updateTask(Long id, Task updatedTask) {
        return taskRepository.findById(id).map(existingTask -> {
            if (updatedTask.getTitle() != null) {
                existingTask.setTitle(updatedTask.getTitle());
            }
            if (updatedTask.getDescription() != null) {
                existingTask.setDescription(updatedTask.getDescription());
            }
            if (updatedTask.getStatus() != null) {
                existingTask.setStatus(updatedTask.getStatus());
            }
            return taskRepository.save(existingTask);
        });
    }

    // ---------- ELIMINAR ----------

    public boolean deleteTask(Long id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // ---------- ESTADÍSTICAS ----------

    public long countByStatus(Task.TaskStatus status) {
        return taskRepository.findByStatusOrderByCreatedAtDesc(status).size();
    }
}
