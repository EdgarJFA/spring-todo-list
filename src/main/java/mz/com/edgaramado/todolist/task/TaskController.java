package mz.com.edgaramado.todolist.task;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import mz.com.edgaramado.todolist.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    @Autowired
    private TaskRepository taskRepository;

    @PostMapping()
    public ResponseEntity create(@RequestBody TaskModel task, ServletRequest servletRequest) {
        task.setUserId((UUID) servletRequest.getAttribute("userId"));
        var currentDate = LocalDateTime.now();
        if (currentDate.isAfter(task.getStartAt()) || currentDate.isAfter(task.getEndAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You've inserted an invalid date!");
        }

        if (task.getStartAt().isAfter(task.getEndAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The start date would be inferior than end date!");
        }
        var taskModel = this.taskRepository.save(task);
        return ResponseEntity.status(HttpStatus.OK).body(taskModel);
    }

    @GetMapping()
    public List<TaskModel> list(HttpServletRequest request) {
        return this.taskRepository.findByUserId((UUID) request.getAttribute("userId"));
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@RequestBody TaskModel taskModel, HttpServletRequest request, @PathVariable UUID id) {
        var task = this.taskRepository.findById(id).orElse(null);
        if (task == null ) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Task not founded!");
        }
        if(!task.getUserId().equals(request.getAttribute("userId"))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User do not have permission to update task!");
        }
        Utils.copyNonNullProperties(taskModel, task);
        var taskUpdated = this.taskRepository.save(task);
        return ResponseEntity.ok().body(taskUpdated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(HttpServletRequest request, @PathVariable UUID id) {
        var task = this.taskRepository.findById(id).orElse(null);
        if(task == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Task not founded!");
        }
        if(!task.getUserId().equals(request.getAttribute("userId"))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User do not have permission to delete task!");
        }
        this.taskRepository.delete(task);
        return ResponseEntity.ok().body("Task deleted!");
    }
}
