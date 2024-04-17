package org.example.taskassignmentapp.Model;

import lombok.Data;
import lombok.NonNull;

@Data
public class Task {
    @NonNull
    private String id;
    @NonNull
    private String name;
    @NonNull
    private String description;
    @NonNull
    private String assigner;
    @NonNull
    private String assignee;
    @NonNull
    private String startDate;
    private String endDate;
    @NonNull
    private String status;
}
