package org.tomjenkinson.pullviewer;

public class Pull {

    private final String author;
    private String project;
    private String pullUrl;
    private String jiraUrl;
    private String description;

    private boolean hold;

    public Pull(String project, String pullUrl, String jiraUrl, String author, String description, boolean hold) {
        this.project = project;
        this.pullUrl = pullUrl;
        this.jiraUrl = jiraUrl;
        this.author = author;
        this.description = description;
        this.hold = hold;
    }

    public String getProject() {
        return project;
    }

    public String getPullUrl() {
        return pullUrl;
    }

    public String getJiraUrl() {
        return jiraUrl;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription (String description) {
        this.description = description;
    }

    public boolean isHold() {
        return hold;
    }
}
