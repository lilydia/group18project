package com.uwaterloo.watodo;

import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "task_table")
public class Task {
    @PrimaryKey(autoGenerate =  true)
    private int id;
    private String title;
    private String description; // data type might be changed later
    private double latitude;
    private double longitude;
    private String groupTag;
    private int completeness;
    private int priority;
    private int ddlYear;
    private int ddlMonth;
    private int ddlDay;

    public Task(String title, String description, double latitude, double longitude, String groupTag, int completeness, int priority, int ddlYear, int ddlMonth, int ddlDay) {
        this.title = title;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.groupTag = groupTag;
        this.completeness = completeness;
        this.priority = priority;
        this.ddlYear = ddlYear;
        this.ddlMonth = ddlMonth;
        this.ddlDay = ddlDay;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() { return longitude; }

    public String getGroupTag() {
        return groupTag;
    }

    public int getCompleteness() {
        return completeness;
    }

    public int getPriority() {
        return priority;
    }

    public int getDdlYear() {
        return ddlYear;
    }

    public int getDdlMonth() {
        return ddlMonth;
    }

    public int getDdlDay() {
        return ddlDay;
    }


}
