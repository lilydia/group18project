package com.uwaterloo.watodo;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;


@Entity(tableName = "task_table")
public class Task {
    @PrimaryKey(autoGenerate =  true)
    private int id;
    private String title;
    private String description; // data type might be changed later
    private String location; // data type might be changed later
    private String groupTag;
    private int completeness;
    private int priority;
    private int ddlYear;
    private int ddlMonth;
    private int ddlDate;

    public Task(String title, String description, String location, String groupTag, int completeness, int priority, int ddlYear, int ddlMonth, int ddlDate) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.groupTag = groupTag;
        this.completeness = completeness;
        this.priority = priority;
        this.ddlYear = ddlYear;
        this.ddlMonth = ddlMonth;
        this.ddlDate = ddlDate;
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

    public String getLocation() {
        return location;
    }

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

    public int getDdlDate() {
        return ddlDate;
    }


}
