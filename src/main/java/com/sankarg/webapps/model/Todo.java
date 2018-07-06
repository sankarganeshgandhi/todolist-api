package com.sankarg.webapps.model;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Todo {
    public final static String FIELD_ID = "todoId";
    public final static String FIELD_TITLE = "title";
    public final static String FIELD_STATUS = "status";

    public final static String FIELD_STATUS_ACTIVE = "ACTIVE";
    public final static String FIELD_STATUS_COMPLETED = "COMPLETED";

    private final static Logger logger = LoggerFactory.getLogger(Todo.class);



    private String todoId;
    private String title;
    private Status status;
    private List<Todo> todoList;

    Todo(String id, String title, Status status) {
        this.todoId = id;
        this.title = title;
        this.status = status;
        todoList = new ArrayList<>();
    }

    public String getTodoId() {
        return this.todoId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }

    public Status getStatus() {
        return this.status;
    }

    public void changeStatus() {
        this.status = (this.status == Status.ACTIVE) ? Status.COMPLETED : Status.ACTIVE;
        for (Todo item : todoList) {
            item.setStatus(this.status);
        }
    }

    public List<Todo> getItems() {
        return this.todoList;
    }

    public void addItem(Todo todoItem) {
        todoList.add(todoItem);
    }

    public void removeItem(Todo todoItem) {
        todoList.remove(todoItem);
    }

    public void removeItems() {
        todoList.clear();
    }

    public void updateItem(Todo todoItem) {
        todoList.remove(todoItem);
        todoList.add(todoItem);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("id: ").append(this.todoId);
        sb.append("\ntitle: ").append(this.title);
        String statusStr = null;
        if (status == Status.ACTIVE) {
            statusStr = "active";
        } else if (status == Status.COMPLETED) {
            statusStr = "completed";
        }
        sb.append("\nstatus: ").append(statusStr);
        if (todoList != null) {
            for (Todo item : todoList) {
                sb.append(item.toString());
            }
        }
        return sb.toString();
    }

    protected void setStatus(Status status) {
        this.status = status;
    }

    public static Todo create(String title) {
        return new Todo(UUID.randomUUID().toString(), title, Status.ACTIVE);
    }

    public static String getJSON(Todo todo) {
        return getTodoJSON(todo);
    }

    public static String getJSON(List<Todo> todoList) {
        return getTodoJSON(todoList);
    }

    public static Todo getTodo(String todoJSONString) {
        Todo todo = null;
        try {
            todo = new ObjectMapper().readValue(todoJSONString, Todo.class);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return todo;
    }

    private static String getTodoJSON(Object todo) {
        String jsonString = null;
        try {
            jsonString = new ObjectMapper().writeValueAsString(todo);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return jsonString;
    }
}