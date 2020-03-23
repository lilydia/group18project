package com.uwaterloo.watodo;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Calendar;

@Entity(tableName = "task_table")
public class Task {
    @PrimaryKey(autoGenerate =  true)
    private int id;
    private String title;
    private String description;
    private int priority;
    private int ddlYear;
    private int ddlMonth;
    private int ddlDate;

    public Task(String title, String description, int priority, int ddlYear, int ddlMonth, int ddlDate) {
        this.title = title;
        this.description = description;
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

    public int getPriority() {
        return priority;
    }

    public String getDeadline() {
        String deadline = ddlYear + "." + ddlMonth + "." + ddlDate;
        return deadline;
    }

    public int getDdlYear() { return ddlYear; }
    public int getDdlMonth() { return ddlMonth; }
    public int getDdlDate() { return ddlDate; }
}
