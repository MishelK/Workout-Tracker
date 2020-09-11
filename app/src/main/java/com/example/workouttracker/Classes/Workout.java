package com.example.workouttracker.Classes;

import java.util.LinkedList;

public class Workout {

    String id, name, description;
    LinkedList<Drill> drillList;

    public Workout(String name, String description) {
        this.name = name;
        this.description = description;
        this.drillList = new LinkedList<Drill>();

    }

    public Workout( String id, String name, String description) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.drillList = new LinkedList<Drill>();

    }
}
