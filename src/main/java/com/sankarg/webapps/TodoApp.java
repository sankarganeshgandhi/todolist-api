package com.sankarg.webapps;

import com.sankarg.webapps.model.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.*;

import java.util.List;

import static spark.Spark.*;

/**
 * TodoApp
 *
 * @author Sankarganesh
 */
public class TodoApp {
    private final static Logger logger = LoggerFactory.getLogger(TodoApp.class);

    public static void main(String[] args) {
        Runtime runtime = Runtime.getRuntime();
        final Thread mainThread = Thread.currentThread();
        runtime.addShutdownHook(new Thread() {
            public void run() {
            TodoDAO.close();
            mainThread.interrupt();
            }
        });

        AppConfig.init();
        TodoDAO.init();

        init();
    }

    private static void init() {
        exception(Exception.class, (e, req, res) -> e.printStackTrace());
        staticFiles.location("/public");
        port(Integer.parseInt(AppConfig.get(AppConfig.APP_SERVICE_PORT)));

        get("/todos", (req, res) -> {
            String todos = getTodos(req);
            res.header("content-type", "application/json");
            res.status(200);
            return todos;
        });

        get("/todos/:id", (req, res) -> {
            String todo = null;
            try {
                todo = getTodo(req);
                res.header("content-type", "application/json");
                res.status(200);
            } catch (Exception ex) {
                res.status(404);
            }
            return todo;
        });

        post("/todos", (req, res) -> {
            createTodo(req);
            res.status(200);
            return "";
        });

        /*post("/todos/:id/todoitem", (req, res) -> {
            createTodoItem(req);
            res.status(200);
            return "";
        });*/

        put("/todos/:id", (req, res) -> {
            updateTodo(req);
            res.status(200);
            return "";
        });

        put("/todos/:id/toggle", (req, res) -> {
            toggleTodoStatus(req);
            res.status(200);
            return "";
        });

        delete("/todos/:id", (req, res) -> {
            deleteTodo(req);
            res.status(200);
            return "";
        });

        delete("/todos", (req, res) -> {
            try {
                deleteTodos(req);
                res.status(200);
            } catch (Exception ex) {
                res.status(404);
            }
            return "";
        });
    }

    private static String getTodo(Request req) throws Exception {
        String id = req.params("id");
        Todo todo = TodoDAO.findById(id);
        return Todo.getJSON(todo);
    }

    private static String getTodos(Request req) {
        String statusStr = req.queryParams("status");
        List<Todo> todoList;
        if (statusStr != null) {
            Status status;
            if (statusStr.equalsIgnoreCase(Todo.FIELD_STATUS_ACTIVE)) {
                status = Status.ACTIVE;
            } else {
                status = Status.COMPLETED;
            }
            todoList = TodoDAO.findByStatus(status);
        } else {
            todoList = TodoDAO.findAll();
        }
        return Todo.getJSON(todoList);
    }

    private static void createTodo(Request req) {
        String todoTitle = req.queryParams("title");
        Todo newTodo = Todo.create(todoTitle);
        TodoDAO.create(newTodo);
    }

    private static void updateTodo(Request req) throws Exception {
        String todoId = req.params("id");
        Todo todo = TodoDAO.findById(todoId);

        String updatedTitle = req.queryParams("title");
        todo.setTitle(updatedTitle);

        TodoDAO.update(todo);
    }

    private static void toggleTodoStatus(Request req) throws Exception {
        String todoId = req.params("id");
        Todo todo = TodoDAO.findById(todoId);

        todo.changeStatus();
        TodoDAO.update(todo);
    }

    private static void deleteTodo(Request req) throws Exception {
        String todoId = req.params("id");
        Todo todoToDelete = TodoDAO.findById(todoId);
        TodoDAO.delete(todoToDelete);
    }

    private static void deleteTodos(Request req) throws Exception {
        String statusStr = req.queryParams("status");
        List<Todo> todoList;
        if (statusStr != null) {
            Status status;
            if (statusStr.equalsIgnoreCase(Todo.FIELD_STATUS_ACTIVE)) {
                status = Status.ACTIVE;
            } else {
                status = Status.COMPLETED;
            }
            todoList = TodoDAO.findByStatus(status);
        } else {
            todoList = TodoDAO.findAll();
        }
        TodoDAO.delete(todoList);
    }

    private static void createTodoItem(Request req) throws Exception {
        String parentTodoId = req.params("id");
        Todo parentTodo = TodoDAH.find(parentTodoId);

        String todoTitle = req.queryParams("title");
        Todo newTodoItem = Todo.create(todoTitle);
        TodoDAH.addItem(parentTodo, newTodoItem);
    }
}
