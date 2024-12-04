// GitLabEvent.java
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.EXISTING_PROPERTY,
  property = "object_kind",
  visible = true
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = PushEvent.class, name = "push"),
    @JsonSubTypes.Type(value = TagPushEvent.class, name = "tag_push"),
    @JsonSubTypes.Type(value = IssueEvent.class, name = "issue"),
    @JsonSubTypes.Type(value = MergeRequestEvent.class, name = "merge_request"),
    @JsonSubTypes.Type(value = NoteEvent.class, name = "note"),
    @JsonSubTypes.Type(value = PipelineEvent.class, name = "pipeline")
    // Add other event types here
})
public abstract class GitLabEvent {
    private String object_kind;

    // Getters and Setters
    public String getObject_kind() {
        return object_kind;
    }

    public void setObject_kind(String object_kind) {
        this.object_kind = object_kind;
    }
}
-----------
 
// PushEvent.java
import java.util.List;

public class PushEvent extends GitLabEvent {
    private String event_name;
    private String before;
    private String after;
    private String ref;
    private String checkout_sha;
    private User user;
    private Project project;
    private List<Commit> commits;
    private int total_commits_count;

    // Getters and Setters
    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public String getBefore() {
        return before;
    }

    public void setBefore(String before) {
        this.before = before;
    }

    public String getAfter() {
        return after;
    }

    public void setAfter(String after) {
        this.after = after;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getCheckout_sha() {
        return checkout_sha;
    }

    public void setCheckout_sha(String checkout_sha) {
        this.checkout_sha = checkout_sha;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public List<Commit> getCommits() {
        return commits;
    }

    public void setCommits(List<Commit> commits) {
        this.commits = commits;
    }

    public int getTotal_commits_count() {
        return total_commits_count;
    }

    public void setTotal_commits_count(int total_commits_count) {
        this.total_commits_count = total_commits_count;
    }

    // Nested Classes
    public static class Commit {
        private String id;
        private String message;
        private String timestamp;
        private String url;
        private Author author;
        private List<String> added;
        private List<String> modified;
        private List<String> removed;

        // Getters and Setters
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public Author getAuthor() {
            return author;
        }

        public void setAuthor(Author author) {
            this.author = author;
        }

        public List<String> getAdded() {
            return added;
        }

        public void setAdded(List<String> added) {
            this.added = added;
        }

        public List<String> getModified() {
            return modified;
        }

        public void setModified(List<String> modified) {
            this.modified = modified;
        }

        public List<String> getRemoved() {
            return removed;
        }

        public void setRemoved(List<String> removed) {
            this.removed = removed;
        }
    }

    public static class Author {
        private String name;
        private String email;

        // Getters and Setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}
-----------
 
// TagPushEvent.java
public class TagPushEvent extends GitLabEvent {
    private String event_name;
    private String before;
    private String after;
    private String ref;
    private String checkout_sha;
    private User user;
    private Project project;
    private Repository repository;
    private String message;
    private boolean created;
    private String tag;

    // Getters and Setters
    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public String getBefore() {
        return before;
    }

    public void setBefore(String before) {
        this.before = before;
    }

    public String getAfter() {
        return after;
    }

    public void setAfter(String after) {
        this.after = after;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getCheckout_sha() {
        return checkout_sha;
    }

    public void setCheckout_sha(String checkout_sha) {
        this.checkout_sha = checkout_sha;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isCreated() {
        return created;
    }

    public void setCreated(boolean created) {
        this.created = created;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    // Nested Classes
    public static class Repository {
        private String name;
        private String url;
        private String description;
        private String homepage;

        // Getters and Setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getHomepage() {
            return homepage;
        }

        public void setHomepage(String homepage) {
            this.homepage = homepage;
        }
    }
}
-----------
 
// IssueEvent.java
import java.util.List;
import java.util.Map;

public class IssueEvent extends GitLabEvent {
    private String event_type;
    private User user;
    private Project project;
    private Repository repository;
    private ObjectAttributes object_attributes;
    private List<String> labels;
    private Map<String, Object> changes;

    // Getters and Setters
    public String getEvent_type() {
        return event_type;
    }

    public void setEvent_type(String event_type) {
        this.event_type = event_type;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public ObjectAttributes getObject_attributes() {
        return object_attributes;
    }

    public void setObject_attributes(ObjectAttributes object_attributes) {
        this.object_attributes = object_attributes;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public Map<String, Object> getChanges() {
        return changes;
    }

    public void setChanges(Map<String, Object> changes) {
        this.changes = changes;
    }

    // Nested Classes
    public static class ObjectAttributes {
        private int id;
        private String title;
        private int assignee_id;
        private int author_id;
        private String description;
        private String created_at;
        private String updated_at;
        private Integer milestone_id;
        private String state;
        private int iid;
        private int project_id;
        private List<String> labels;
        private boolean confidential;
        private String closed_at;
        private String closed_by;
        private String due_date;

        // Getters and Setters
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getAssignee_id() {
            return assignee_id;
        }

        public void setAssignee_id(int assignee_id) {
            this.assignee_id = assignee_id;
        }

        public int getAuthor_id() {
            return author_id;
        }

        public void setAuthor_id(int author_id) {
            this.author_id = author_id;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }

        public Integer getMilestone_id() {
            return milestone_id;
        }

        public void setMilestone_id(Integer milestone_id) {
            this.milestone_id = milestone_id;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public int getIid() {
            return iid;
        }

        public void setIid(int iid) {
            this.iid = iid;
        }

        public int getProject_id() {
            return project_id;
        }

        public void setProject_id(int project_id) {
            this.project_id = project_id;
        }

        public List<String> getLabels() {
            return labels;
        }

        public void setLabels(List<String> labels) {
            this.labels = labels;
        }

        public boolean isConfidential() {
            return confidential;
        }

        public void setConfidential(boolean confidential) {
            this.confidential = confidential;
        }

        public String getClosed_at() {
            return closed_at;
        }

        public void setClosed_at(String closed_at) {
            this.closed_at = closed_at;
        }

        public String getClosed_by() {
            return closed_by;
        }

        public void setClosed_by(String closed_by) {
            this.closed_by = closed_by;
        }

        public String getDue_date() {
            return due_date;
        }

        public void setDue_date(String due_date) {
            this.due_date = due_date;
        }
    }
}
-----------
 
// MergeRequestEvent.java
import java.util.List;
import java.util.Map;

public class MergeRequestEvent extends GitLabEvent {
    private String event_type;
    private User user;
    private Project project;
    private Repository repository;
    private ObjectAttributes object_attributes;
    private List<String> labels;
    private Map<String, Object> changes;

    // Getters and Setters
    public String getEvent_type() {
        return event_type;
    }

    public void setEvent_type(String event_type) {
        this.event_type = event_type;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public ObjectAttributes getObject_attributes() {
        return object_attributes;
    }

    public void setObject_attributes(ObjectAttributes object_attributes) {
        this.object_attributes = object_attributes;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public Map<String, Object> getChanges() {
        return changes;
    }

    public void setChanges(Map<String, Object> changes) {
        this.changes = changes;
    }

    // Nested Classes
    public static class ObjectAttributes {
        private int id;
        private String target_branch;
        private String source_branch;
        private int source_project_id;
        private int author_id;
        private int assignee_id;
        private String title;
        private String created_at;
        private String updated_at;
        private String state;
        private String merge_status;
        private int target_project_id;
        private int iid;
        private String description;
        private int position;
        private String locked_at;
        private int updated_by_id;
        private boolean merge_when_pipeline_succeeds;
        private Integer merge_user_id;
        private String merge_commit_sha;
        private String deleted_at;
        private String merge_error;
        private Map<String, Object> merge_params;
        private String merge_when_pipeline_succeeds_at;
        private boolean force_remove_source_branch;

        // Getters and Setters
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTarget_branch() {
            return target_branch;
        }

        public void setTarget_branch(String target_branch) {
            this.target_branch = target_branch;
        }

        public String getSource_branch() {
            return source_branch;
        }

        public void setSource_branch(String source_branch) {
            this.source_branch = source_branch;
        }

        public int getSource_project_id() {
            return source_project_id;
        }

        public void setSource_project_id(int source_project_id) {
            this.source_project_id = source_project_id;
        }

        public int getAuthor_id() {
            return author_id;
        }

        public void setAuthor_id(int author_id) {
            this.author_id = author_id;
        }

        public int getAssignee_id() {
            return assignee_id;
        }

        public void setAssignee_id(int assignee_id) {
            this.assignee_id = assignee_id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getMerge_status() {
            return merge_status;
        }

        public void setMerge_status(String merge_status) {
            this.merge_status = merge_status;
        }

        public int getTarget_project_id() {
            return target_project_id;
        }

        public void setTarget_project_id(int target_project_id) {
            this.target_project_id = target_project_id;
        }

        public int getIid() {
            return iid;
        }

        public void setIid(int iid) {
            this.iid = iid;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public String getLocked_at() {
            return locked_at;
        }

        public void setLocked_at(String locked_at) {
            this.locked_at = locked_at;
        }

        public int getUpdated_by_id() {
            return updated_by_id;
        }

        public void setUpdated_by_id(int updated_by_id) {
            this.updated_by_id = updated_by_id;
        }

        public boolean isMerge_when_pipeline_succeeds() {
            return merge_when_pipeline_succeeds;
        }

        public void setMerge_when_pipeline_succeeds(boolean merge_when_pipeline_succeeds) {
            this.merge_when_pipeline_succeeds = merge_when_pipeline_succeeds;
        }

        public Integer getMerge_user_id() {
            return merge_user_id;
        }

        public void setMerge_user_id(Integer merge_user_id) {
            this.merge_user_id = merge_user_id;
        }

        public String getMerge_commit_sha() {
            return merge_commit_sha;
        }

        public void setMerge_commit_sha(String merge_commit_sha) {
            this.merge_commit_sha = merge_commit_sha;
        }

        public String getDeleted_at() {
            return deleted_at;
        }

        public void setDeleted_at(String deleted_at) {
            this.deleted_at = deleted_at;
        }

        public String getMerge_error() {
            return merge_error;
        }

        public void setMerge_error(String merge_error) {
            this.merge_error = merge_error;
        }

        public Map<String, Object> getMerge_params() {
            return merge_params;
        }

        public void setMerge_params(Map<String, Object> merge_params) {
            this.merge_params = merge_params;
        }

        public String getMerge_when_pipeline_succeeds_at() {
            return merge_when_pipeline_succeeds_at;
        }

        public void setMerge_when_pipeline_succeeds_at(String merge_when_pipeline_succeeds_at) {
            this.merge_when_pipeline_succeeds_at = merge_when_pipeline_succeeds_at;
        }

        public boolean isForce_remove_source_branch() {
            return force_remove_source_branch;
        }

        public void setForce_remove_source_branch(boolean force_remove_source_branch) {
            this.force_remove_source_branch = force_remove_source_branch;
        }
    }
}
-----------
 
// NoteEvent.java
public class NoteEvent extends GitLabEvent {
    private String event_type;
    private User user;
    private Project project;
    private Repository repository;
    private ObjectAttributes object_attributes;
    private MergeRequest merge_request;
    private Issue issue;
    private Snippet snippet;

    // Getters and Setters
    public String getEvent_type() {
        return event_type;
    }

    public void setEvent_type(String event_type) {
        this.event_type = event_type;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public ObjectAttributes getObject_attributes() {
        return object_attributes;
    }

    public void setObject_attributes(ObjectAttributes object_attributes) {
        this.object_attributes = object_attributes;
    }

    public MergeRequest getMerge_request() {
        return merge_request;
    }

    public void setMerge_request(MergeRequest merge_request) {
        this.merge_request = merge_request;
    }

    public Issue getIssue() {
        return issue;
    }

    public void setIssue(Issue issue) {
        this.issue = issue;
    }

    public Snippet getSnippet() {
        return snippet;
    }

    public void setSnippet(Snippet snippet) {
        this.snippet = snippet;
    }

    // Nested Classes
    public static class ObjectAttributes {
        private int id;
        private String note;
        private String noteable_type;
        private int author_id;
        private String created_at;
        private String updated_at;
        private int project_id;
        private String attachment;
        private String commit_id;
        private String line_code;
        private int noteable_id;
        private boolean system;
        private String st_diff;
        private String position;
        private String original_position;
        private String url;
        private String type;
        private String attachment_type;
        private String commit_title;

        // Getters and Setters
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }

        public String getNoteable_type() {
            return noteable_type;
        }

        public void setNoteable_type(String noteable_type) {
            this.noteable_type = noteable_type;
        }

        public int getAuthor_id() {
            return author_id;
        }

        public void setAuthor_id(int author_id) {
            this.author_id = author_id;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }

        public int getProject_id() {
            return project_id;
        }

        public void setProject_id(int project_id) {
            this.project_id = project_id;
        }

        public String getAttachment() {
            return attachment;
        }

        public void setAttachment(String attachment) {
            this.attachment = attachment;
        }

        public String getCommit_id() {
            return commit_id;
        }

        public void setCommit_id(String commit_id) {
            this.commit_id = commit_id;
        }

        public String getLine_code() {
            return line_code;
        }

        public void setLine_code(String line_code) {
            this.line_code = line_code;
        }

        public int getNoteable_id() {
            return noteable_id;
        }

        public void setNoteable_id(int noteable_id) {
            this.noteable_id = noteable_id;
        }

        public boolean isSystem() {
            return system;
        }

        public void setSystem(boolean system) {
            this.system = system;
        }

        public String getSt_diff() {
            return st_diff;
        }

        public void setSt_diff(String st_diff) {
            this.st_diff = st_diff;
        }

        public String getPosition() {
            return position;
        }

        public void setPosition(String position) {
            this.position = position;
        }

        public String getOriginal_position() {
            return original_position;
        }

        public void setOriginal_position(String original_position) {
            this.original_position = original_position;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getAttachment_type() {
            return attachment_type;
        }

        public void setAttachment_type(String attachment_type) {
            this.attachment_type = attachment_type;
        }

        public String getCommit_title() {
            return commit_title;
        }

        public void setCommit_title(String commit_title) {
            this.commit_title = commit_title;
        }
    }

    public static class MergeRequest {
        // Define fields similar to MergeRequestEvent's ObjectAttributes if needed
        // Getters and Setters
    }

    public static class Issue {
        // Define fields similar to IssueEvent's ObjectAttributes if needed
        // Getters and Setters
    }

    public static class Snippet {
        // Define fields as per GitLab's snippet object
        // Getters and Setters
    }
}
-----------
 
// PipelineEvent.java
import java.util.List;

public class PipelineEvent extends GitLabEvent {
    private String event_type;
    private User user;
    private Project project;
    private ObjectAttributes object_attributes;
    private List<Build> builds;
    private Commit commit;
    private String ref;
    private String checkout_sha;
    private String before_sha;

    // Getters and Setters
    public String getEvent_type() {
        return event_type;
    }

    public void setEvent_type(String event_type) {
        this.event_type = event_type;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public ObjectAttributes getObject_attributes() {
        return object_attributes;
    }

    public void setObject_attributes(ObjectAttributes object_attributes) {
        this.object_attributes = object_attributes;
    }

    public List<Build> getBuilds() {
        return builds;
    }

    public void setBuilds(List<Build> builds) {
        this.builds = builds;
    }

    public Commit getCommit() {
        return commit;
    }

    public void setCommit(Commit commit) {
        this.commit = commit;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getCheckout_sha() {
        return checkout_sha;
    }

    public void setCheckout_sha(String checkout_sha) {
        this.checkout_sha = checkout_sha;
    }

    public String getBefore_sha() {
        return before_sha;
    }

    public void setBefore_sha(String before_sha) {
        this.before_sha = before_sha;
    }

    // Nested Classes
    public static class ObjectAttributes {
        private int id;
        private String sha;
        private String ref;
        private String status;
        private String created_at;
        private String updated_at;
        private String source;
        private int triggered_by;
        private String finished_at;
        private int duration;
        private List<Variable> variables;

        // Getters and Setters
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getSha() {
            return sha;
        }

        public void setSha(String sha) {
            this.sha = sha;
        }

        public String getRef() {
            return ref;
        }

        public void setRef(String ref) {
            this.ref = ref;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public int getTriggered_by() {
            return triggered_by;
        }

        public void setTriggered_by(int triggered_by) {
            this.triggered_by = triggered_by;
        }

        public String getFinished_at() {
            return finished_at;
        }

        public void setFinished_at(String finished_at) {
            this.finished_at = finished_at;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public List<Variable> getVariables() {
            return variables;
        }

        public void setVariables(List<Variable> variables) {
            this.variables = variables;
        }
    }

    public static class Build {
        private int id;
        private String stage;
        private String name;
        private String status;
        private String created_at;
        private String started_at;
        private String finished_at;
        private String when;
        private boolean manual;
        private boolean allow_failure;
        private User user;
        private Runner runner;

        // Getters and Setters
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getStage() {
            return stage;
        }

        public void setStage(String stage) {
            this.stage = stage;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getStarted_at() {
            return started_at;
        }

        public void setStarted_at(String started_at) {
            this.started_at = started_at;
        }

        public String getFinished_at() {
            return finished_at;
        }

        public void setFinished_at(String finished_at) {
            this.finished_at = finished_at;
        }

        public String getWhen() {
            return when;
        }

        public void setWhen(String when) {
            this.when = when;
        }

        public boolean isManual() {
            return manual;
        }

        public void setManual(boolean manual) {
            this.manual = manual;
        }

        public boolean isAllow_failure() {
            return allow_failure;
        }

        public void setAllow_failure(boolean allow_failure) {
            this.allow_failure = allow_failure;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public Runner getRunner() {
            return runner;
        }

        public void setRunner(Runner runner) {
            this.runner = runner;
        }
    }

    public static class Commit {
        private String id;
        private String message;
        private String timestamp;
        private String url;
        private Author author;
        private List<String> added;
        private List<String> modified;
        private List<String> removed;

        // Getters and Setters
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public Author getAuthor() {
            return author;
        }

        public void setAuthor(Author author) {
            this.author = author;
        }

        public List<String> getAdded() {
            return added;
        }

        public void setAdded(List<String> added) {
            this.added = added;
        }

        public List<String> getModified() {
            return modified;
        }

        public void setModified(List<String> modified) {
            this.modified = modified;
        }

        public List<String> getRemoved() {
            return removed;
        }

        public void setRemoved(List<String> removed) {
            this.removed = removed;
        }
    }

    public static class Variable {
        private String key;
        private String value;

        // Getters and Setters
        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class Runner {
        private int id;
        private String description;
        private String ip_address;
        private String active;
        private String is_shared;
        private String runner_type;
        private String name;
        private String online;
        private String status;
        private String contacted_at;
        private String created_at;

        // Getters and Setters
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getIp_address() {
            return ip_address;
        }

        public void setIp_address(String ip_address) {
            this.ip_address = ip_address;
        }

        public String getActive() {
            return active;
        }

        public void setActive(String active) {
            this.active = active;
        }

        public String getIs_shared() {
            return is_shared;
        }

        public void setIs_shared(String is_shared) {
            this.is_shared = is_shared;
        }

        public String getRunner_type() {
            return runner_type;
        }

        public void setRunner_type(String runner_type) {
            this.runner_type = runner_type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getOnline() {
            return online;
        }

        public void setOnline(String online) {
            this.online = online;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getContacted_at() {
            return contacted_at;
        }

        public void setContacted_at(String contacted_at) {
            this.contacted_at = contacted_at;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }
    }

    public static class Author {
        private String name;
        private String email;

        // Getters and Setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}
-----------
 
// User.java
public class User {
    private int id;
    private String name;
    private String username;
    private String email;
    private String avatar_url;

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    } 

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    } 

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    } 

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    } 

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }
}
-----------
 
// Project.java
public class Project {
    private int id;
    private String name;
    private String description;
    private String web_url;
    private String avatar_url;
    private String git_ssh_url;
    private String git_http_url;
    private String namespace;
    private int visibility_level;
    private String path_with_namespace;
    private String default_branch;
    private String homepage;
    private String url;
    private String ssh_url;
    private String http_url;

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    } 

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    } 

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    } 

    public String getWeb_url() {
        return web_url;
    }

    public void setWeb_url(String web_url) {
        this.web_url = web_url;
    } 

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    } 

    public String getGit_ssh_url() {
        return git_ssh_url;
    }

    public void setGit_ssh_url(String git_ssh_url) {
        this.git_ssh_url = git_ssh_url;
    } 

    public String getGit_http_url() {
        return git_http_url;
    }

    public void setGit_http_url(String git_http_url) {
        this.git_http_url = git_http_url;
    } 

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    } 

    public int getVisibility_level() {
        return visibility_level;
    }

    public void setVisibility_level(int visibility_level) {
        this.visibility_level = visibility_level;
    } 

    public String getPath_with_namespace() {
        return path_with_namespace;
    }

    public void setPath_with_namespace(String path_with_namespace) {
        this.path_with_namespace = path_with_namespace;
    } 

    public String getDefault_branch() {
        return default_branch;
    }

    public void setDefault_branch(String default_branch) {
        this.default_branch = default_branch;
    } 

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    } 

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    } 

    public String getSsh_url() {
        return ssh_url;
    }

    public void setSsh_url(String ssh_url) {
        this.ssh_url = ssh_url;
    } 

    public String getHttp_url() {
        return http_url;
    }

    public void setHttp_url(String http_url) {
        this.http_url = http_url;
    }
}
-----------
 
// GitLabWebhookController.java
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/gitlab-webhook")
public class GitLabWebhookController {

    private final ObjectMapper objectMapper;
    private static final String SECRET_TOKEN = "mysecret"; // Should be stored securely

    public GitLabWebhookController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostMapping
    public ResponseEntity<String> handleGitLabWebhook(@RequestBody String payload,
                                                       @RequestHeader("X-Gitlab-Event") String eventType,
                                                       @RequestHeader("X-Gitlab-Token") String token) {
        if (!SECRET_TOKEN.equals(token)) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }

        try {
            GitLabEvent event = objectMapper.readValue(payload, GitLabEvent.class);
            handleEvent(event);
            return new ResponseEntity<>("Event processed", HttpStatus.OK);
        } catch (Exception e) {
            // Log the exception
            e.printStackTrace();
            return new ResponseEntity<>("Error processing event", HttpStatus.BAD_REQUEST);
        }
    }

    private void handleEvent(GitLabEvent event) {
        switch (event.getObject_kind()) {
            case "push":
                PushEvent pushEvent = (PushEvent) event;
                processPushEvent(pushEvent);
                break;
            case "tag_push":
                TagPushEvent tagPushEvent = (TagPushEvent) event;
                processTagPushEvent(tagPushEvent);
                break;
            case "issue":
                IssueEvent issueEvent = (IssueEvent) event;
                processIssueEvent(issueEvent);
                break;
            case "merge_request":
                MergeRequestEvent mrEvent = (MergeRequestEvent) event;
                processMergeRequestEvent(mrEvent);
                break;
            case "note":
                NoteEvent noteEvent = (NoteEvent) event;
                processNoteEvent(noteEvent);
                break;
            case "pipeline":
                PipelineEvent pipelineEvent = (PipelineEvent) event;
                processPipelineEvent(pipelineEvent);
                break;
            // Add other cases for different events
            default:
                // Handle unknown event types or ignore
                System.out.println("Unhandled event type: " + event.getObject_kind());
                break;
        }
    }

    private void processPushEvent(PushEvent event) {
        System.out.println("Push event on ref: " + event.getRef());
        System.out.println("Pushed by: " + event.getUser().getName());
        System.out.println("Total commits: " + event.getTotal_commits_count());

        for (PushEvent.Commit commit : event.getCommits()) {
            System.out.println("Commit Message: " + commit.getMessage());
            // Additional processing
        }
    }

    private void processTagPushEvent(TagPushEvent event) {
        System.out.println("Tag Push event on ref: " + event.getRef());
        System.out.println("Tag: " + event.getTag());
        System.out.println("Pushed by: " + event.getUser().getName());
        System.out.println("Message: " + event.getMessage());
        // Additional processing
    }

    private void processIssueEvent(IssueEvent event) {
        System.out.println("Issue Event: " + event.getObject_attributes().getTitle());
        System.out.println("Action: " + event.getObject_attributes().getState());
        // Additional processing
    }

    private void processMergeRequestEvent(MergeRequestEvent event) {
        System.out.println("Merge Request Event: " + event.getObject_attributes().getTitle());
        System.out.println("State: " + event.getObject_attributes().getState());
        // Additional processing
    }

    private void processNoteEvent(NoteEvent event) {
        System.out.println("Note Event: " + event.getObject_attributes().getNote());
        System.out.println("Noteable Type: " + event.getObject_attributes().getNoteable_type());
        // Additional processing
    }

    private void processPipelineEvent(PipelineEvent event) {
        System.out.println("Pipeline Event Status: " + event.getObject_attributes().getStatus());
        System.out.println("Ref: " + event.getRef());
        // Additional processing
    }

    // Implement other processing methods as needed
}
-----------
 
// JacksonConfig.java
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {
    
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // Ignore unknown properties to prevent failures
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }
}
