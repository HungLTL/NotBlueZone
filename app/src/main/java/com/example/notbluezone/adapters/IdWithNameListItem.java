package com.example.notbluezone.adapters;

import org.jetbrains.annotations.NotNull;

public class IdWithNameListItem {
    private String id;
    private String name;

    public IdWithNameListItem(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @NotNull
    @Override
    public String toString() {
        return id + " - " + name;
    }
}
