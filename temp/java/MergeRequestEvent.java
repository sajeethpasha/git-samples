package com.example.webhook;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class MergeRequestEvent {

    private String object_kind;
    private String event_type;
    private User user;
    private Project project;
    private ObjectAttributes object_attributes;

    @Data
    @NoArgsConstructor
    public static class User {
        private int id;
        private String name;
        private String username;
        private String avatar_url;
        private String email;
    }

    @Data
    @NoArgsConstructor
    public static class Project {
        private int id;
        private String name;
        private String description;
        private String web_url;
    }

    @Data
    @NoArgsConstructor
    public static class ObjectAttributes {
        private int assignee_id;
        private int author_id;
        private String created_at;
        private String description;
        private MergeParams merge_params;
        private String merge_status;
        private String source_branch;
        private String target_branch;
        private List<Label> labels;
        private List<Assignee> assignees;
    }

    @Data
    @NoArgsConstructor
    public static class MergeParams {
        private String force_remove_source_branch;
    }

    @Data
    @NoArgsConstructor
    public static class Label {
        private int id;
        private String title;
        private String color;
    }

    @Data
    @NoArgsConstructor
    public static class Assignee {
        private int id;
        private String name;
        private String username;
    }
}
