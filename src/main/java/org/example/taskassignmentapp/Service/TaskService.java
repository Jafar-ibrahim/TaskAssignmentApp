package org.example.taskassignmentapp.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.log4j.Log4j2;
import org.example.taskassignmentapp.Enum.TaskStatus;
import org.example.taskassignmentapp.Model.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
public class TaskService {

    private final DocumentService documentService;

    @Autowired
    public TaskService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public Task assignTask(Task task,
                           String adminUsername, String adminPassword){

        task.setStatus(TaskStatus.ASSIGNED);
        String documentId = documentService.createDocument(task.toJson(), adminUsername, adminPassword);
        task.setId(documentId);
        return task;
    }

    public boolean updateTaskStatus(String taskId, TaskStatus status, String adminUsername, String adminPassword){
        ObjectMapper objectMapper = new ObjectMapper();
        Optional<ObjectNode> task = documentService.getDocumentById(taskId, adminUsername, adminPassword);
        Long taskVersion = task.map(t -> t.get("version").asLong()).orElse(null);
        if(task.isPresent()){
            ObjectNode statusNode = objectMapper.createObjectNode();
            statusNode.put("status", status.toString());
            statusNode.put("_version", taskVersion);
            documentService.updateDocument(taskId, statusNode, adminUsername, adminPassword);
            log.info("Task status updated successfully to {} for task Id {}", status, taskId);
            return true;
        }else {
            log.error("Task not found with Id {}", taskId);
            return false;
        }
    }

    public List<Task> getTasksByUsername(String username, String adminUsername, String adminPassword){
        List<Task> tasks = new ArrayList<>();
        List<JsonNode> taskNodes = documentService.fetchAllDocumentsByPropertyValue("assignee", username, adminUsername, adminPassword);
        for(JsonNode taskNode: taskNodes){
            tasks.add(new Task(taskNode));
        }
        return tasks;
    }


}
