package com.example.mydoc;

public class HealthTips {
    private String tips;
    private String issue;

    public HealthTips(String tips, String issue) {
        this.tips = tips;
        this.issue = issue;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    @Override
    public String toString() {
        return "\t" + tips + "\n\n\t\t\t-" + issue;
    }
}
