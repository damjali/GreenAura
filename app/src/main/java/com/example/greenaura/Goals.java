package com.example.greenaura;

public class Goals {
    String goalTitle;
    String goalID;
    String goalDescription;
    int goalAuraPoints;
    String image;

    public String getGoalDifficulty() {
        return goalDifficulty;
    }

    public void setGoalDifficulty(String goalDifficulty) {
        this.goalDifficulty = goalDifficulty;
    }

    String goalDifficulty;

    public Goals(String goalID, String goalTitle, String goalDescription, int goalAuraPoints) {
        this.goalTitle = goalTitle;
        this.goalDescription = goalDescription;
        this.goalAuraPoints = goalAuraPoints;
        this.goalID = goalID;
    }

    public Goals(String goalID, String goalTitle, String goalDescription, int goalAuraPoints, String image, String GoalDifficulty) {
        this.goalTitle = goalTitle;
        this.goalDescription = goalDescription;
        this.goalAuraPoints = goalAuraPoints;
        this.goalID = goalID;
        this.image = image;
        this.goalDifficulty = GoalDifficulty;
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

    public String getGoalID() {
        return goalID;
    }

    public void setGoalID(String goalID) {
        this.goalID = goalID;
    }
}
