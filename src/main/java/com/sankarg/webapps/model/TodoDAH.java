package com.sankarg.webapps.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TodoDAH {
    private final static Logger logger = LoggerFactory.getLogger(TodoDAH.class);

    private static List<Todo> todoList = new ArrayList<>();

    public static List<Todo> findAll() {
        return todoList;
    }

    public static Todo find(String id) throws Exception {
        //return todoList.stream().filter(t -> t.getId().equals(id)).findFirst().orElseThrow(Exception::new);
        Todo todo = todoList.stream().filter(
            t -> t.getTodoId().equals(id)
                || t.getItems().stream().anyMatch(t1 -> t1.getTodoId().equals(id))).findFirst().orElseThrow(Exception::new);
        if (todo.getTodoId().equals(id)) {
            return todo;
        } else {
            return todo.getItems().stream().filter(t -> t.getTodoId().equals(id)).findFirst().orElseThrow(Exception::new);
        }
    }

    public static List<Todo> findByStatus(String statusStr) {
        Status status = Status.ACTIVE;
        if (statusStr.equalsIgnoreCase("completed")) {
            status = Status.COMPLETED;
        }
        return findByStatus(status);
    }

    public static void create(Todo newTodo) {
        todoList.add(newTodo);
    }

    public static void update(String id, String newTitle) throws Exception {
        Todo todo = find(id);
        if (todo != null) {
            todo.setTitle(newTitle);
        } else {
            throw new Exception("not found");
        }
    }

    public static void remove(String id) throws Exception {
        todoList.remove(find(id));
    }

    public static void removeAll() {
        todoList.clear();
    }

    public static void removeAll(String statusStr) throws Exception {
        Status status = Status.COMPLETED;
        if (statusStr.equalsIgnoreCase("active")) {
            status = Status.ACTIVE;
        } else if (statusStr.equalsIgnoreCase("completed")) {
            status = Status.COMPLETED;
        }
        remove(status);
    }

    public static void changeStatus(String id) throws Exception {
        Todo todo = find(id);
        if (todo != null) {
            todo.changeStatus();
        } else {
            throw new Exception("not found");
        }
    }

    public static void addItem(Todo todo, Todo todoItem) {
        todo.addItem(todoItem);
    }

    private static void remove(Status status) {
        findByStatus(status).forEach(t -> {
            try {
                TodoDAH.remove(t.getTodoId());
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        });
    }

    private static List<Todo> findByStatus(Status status) {
        return todoList.stream().filter(t -> t.getStatus().equals(status)).collect(Collectors.toList());
    }
}