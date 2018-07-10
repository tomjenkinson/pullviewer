package org.tomjenkinson.pullviewer;

public class Pull {

    private String pullLinkName;
    private String pullUrl;
    private String jiraUrl;
    private String description;

    public Pull(String pullLinkName, String pullUrl, String jiraUrl, String description) {
        this.pullLinkName = pullLinkName;
        this.pullUrl = pullUrl;
        this.jiraUrl = jiraUrl;
        this.description = description;
    }

    public String getPullLinkName() {
        return pullLinkName;
    }

    public String getPullUrl() {
        return pullUrl;
    }

    public String getJiraUrl() {
        return jiraUrl;
    }

    public String getDescription() {
        return description;
    }
}
