package org.example.taskassignmentapp;

import org.example.taskassignmentapp.Model.Task;
import org.example.taskassignmentapp.Service.DatabaseService;
import org.example.taskassignmentapp.Service.DocumentService;
import org.example.taskassignmentapp.Service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

public class Main {

    private static final TaskService taskService = new TaskService(new DocumentService(new DatabaseService()));
    public static void main(String[] args) {
        Task task = new Task("Task 1", "Task 1 description", "Task 1 assignee", "Task 1 assigner","2024-01-01", "2024-01-01");
        Task returnedTask = taskService.assignTask(task, "admin", "admin");
        System.out.println(returnedTask);
    }
}
