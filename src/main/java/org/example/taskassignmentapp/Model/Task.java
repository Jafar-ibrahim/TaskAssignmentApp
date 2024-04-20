package org.example.taskassignmentapp.Model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.example.taskassignmentapp.Enum.TaskStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Task {
    private String id;
    @JsonProperty(required = true)
    private String title;
    @JsonProperty(required = true)
    private String description;
    @JsonProperty(required = true)
    private String assigner;
    @JsonProperty(required = true)
    private String assignee;
    @JsonProperty(required = true)
    private String startDate;
    private String endDate;
    @JsonProperty
    private TaskStatus status;



    public Task(@NonNull String title, @NonNull String description, @NonNull String assigner, @NonNull String assignee, @NonNull String startDate, String endDate) {
        this.title = title;
        this.description = description;
        this.assigner = assigner;
        this.assignee = assignee;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Task(String title, String description, String assigner, String assignee, String startDate, String endDate, TaskStatus status) {
        this.title = title;
        this.description = description;
        this.assigner = assigner;
        this.assignee = assignee;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public Task (JsonNode taskJson){
        this.id = taskJson.get("_id").asText();
        this.title = taskJson.get("title").asText();
        this.description = taskJson.get("description").asText();
        this.assigner = taskJson.get("assigner").asText();
        this.assignee = taskJson.get("assignee").asText();
        this.startDate = taskJson.get("startDate").asText();
        this.endDate = taskJson.get("endDate").asText();
        this.status = TaskStatus.valueOf(taskJson.get("status").asText());
    }

    public ObjectNode toJson() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode taskJson = mapper.createObjectNode();
        if (id != null){
            taskJson.put("id", id);
        }
        taskJson.put("title", title);
        taskJson.put("description", description);
        taskJson.put("assigner", assigner);
        taskJson.put("assignee", assignee);
        taskJson.put("startDate", startDate);
        taskJson.put("endDate", endDate);
        taskJson.put("status", status.toString());
        return taskJson;
    }
}