package com.example.greenaura;

public class Goals {
    String goalTitle;
    String goalDescription;
    int goalAuraPoints;
    String image;

    public Goals(String goalTitle, String goalDescription, int goalAuraPoints, String image) {
        this.goalTitle = goalTitle;
        this.goalDescription = goalDescription;
        this.goalAuraPoints = goalAuraPoints;
        this.image = image;
    }

    public Goals(String goalTitle) {
        this.goalTitle = goalTitle;
    }

    public String getGoalTitle() {
        return goalTitle;
    }

    public void setGoalTitle(String goalTitle) {
        this.goalTitle = goalTitle;
    }

    public String getGoalDescription() {
        return goalDescription;
    }

    public void setGoalDescription(String goalDescription) {
        this.goalDescription = goalDescription;
    }

    public int getGoalAuraPoints() {
        return goalAuraPoints;
    }

    public void setGoalAuraPoints(int goalAuraPoints) {
        this.goalAuraPoints = goalAuraPoints;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
