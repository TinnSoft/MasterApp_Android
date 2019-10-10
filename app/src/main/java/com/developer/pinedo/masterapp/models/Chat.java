package com.developer.pinedo.masterapp.models;

public class Chat {

    public int id;
    public String name;
    public String description;
    public String created_at;
    public int node_id;
    public int type_stakeholder;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getNode_id() {
        return node_id;
    }

    public void setNode_id(int node_id) {
        this.node_id = node_id;
    }

    public int getType_stakeholder() {
        return type_stakeholder;
    }

    public void setType_stakeholder(int type_stakeholder) {
        this.type_stakeholder = type_stakeholder;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", created_at='" + created_at + '\'' +
                ", node_id=" + node_id +
                ", type_stakeholder=" + type_stakeholder +
                '}';
    }
}
