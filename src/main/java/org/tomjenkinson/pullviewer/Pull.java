package org.tomjenkinson.pullviewer;

public class Pull {

	private String pullUrl;
	private String jiraUrl;
	private String description;

	public Pull(String pullUrl, String jiraUrl, String description) {
		this.pullUrl = pullUrl;
		this.jiraUrl = jiraUrl;
		this.description = description;
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
