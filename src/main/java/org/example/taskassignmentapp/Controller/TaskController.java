package org.example.taskassignmentapp.Controller;

import jakarta.servlet.http.HttpSession;
import org.example.taskassignmentapp.Enum.TaskStatus;
import org.example.taskassignmentapp.Model.Task;
import org.example.taskassignmentapp.Model.UserDTO;
import org.example.taskassignmentapp.Service.TaskService;
import org.example.taskassignmentapp.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/task-app/api/tasks")
public class TaskController {

    private final TaskService taskService;
    private final UserService userService;


    @Autowired
    public TaskController(TaskService taskService, UserService userService) {
        this.taskService = taskService;
        this.userService = userService;
    }

    @GetMapping("/add-task")
    public String addTaskForm(@RequestParam("username") String assignee, Model model , HttpSession httpSession) {
        UserDTO user = (UserDTO) httpSession.getAttribute("user");

        Task task = new Task();
        task.setAssignee(assignee);
        task.setAssigner(user.getUsername());
        task.setStatus(TaskStatus.ASSIGNED);
        model.addAttribute("task", task);
        model.addAttribute("username", assignee);
        return "add_task";
    }

    @PostMapping("/add-task")
    public String assignNewTask(@ModelAttribute Task task,
                                @RequestParam("username") String assignee,
                                RedirectAttributes redirectAttributes) {
        if (task != null) {
            Task createdTask = taskService.assignTask(task, "admin", "admin");
            if (createdTask != null) {
                redirectAttributes.addFlashAttribute("success", "Task assigned successfully");
                return "redirect:/task-app/api/tasks/add-task?username=" + createdTask.getAssignee();
            }
            redirectAttributes.addFlashAttribute("error", "Task assignment failed");
            return "redirect:/task-app/api/tasks/add-task?username=" + task.getAssignee();
        }
        redirectAttributes.addFlashAttribute("error", "Invalid task details");
        return "redirect:/task-app/api/tasks/add-task?username=" + assignee;
    }
    @GetMapping("/users-list")
    public String getUsersList(Model model) {
        model.addAttribute("users", userService.getAllUsers("admin", "admin"));
        return "admin_users_list";
    }


    @GetMapping("/{username}")
    public String getUserTasks(@PathVariable String username , Model model) {
        List<Task> tasks =  taskService.getTasksByUsername(username, "admin", "admin");
        model.addAttribute("tasks", tasks);
        model.addAttribute("username", username);
        return "user_tasks";
    }

    @PostMapping("/status")
    public String updateTaskStatus(@RequestParam("taskId") String taskId,
                                   @RequestParam("newStatus") TaskStatus status,
                                   @RequestParam("username") String username , Model model){

        if(taskService.updateTaskStatus(taskId, status, "admin", "admin")) {
            model.addAttribute("success", "Task status updated successfully");
        }else{
            model.addAttribute("error", "Task status update failed");
        }
        return getUserTasks(username, model);
    }

}
