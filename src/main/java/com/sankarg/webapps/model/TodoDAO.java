package com.sankarg.webapps.model;

import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class TodoDAO {
    private final static Logger logger = LoggerFactory.getLogger(TodoDAO.class);

    private final static String APP_DS_URI = "";

    private final static String APP_DS_USER = "";
    private final static String APP_DS_PWD = "";
    private final static String APP_DS_URL = "mongodb://" + APP_DS_USER + ":" + APP_DS_PWD + "@" + APP_DS_URI;
    private final static String APP_DS_DB = "webapps";
    private final static String APP_DS_DB_COLLECTION = "todolist";

    private static MongoClient mongoClient = null;
    private static MongoDatabase  database = null;

    public static void init() {
        Codec<Document> defaultDocumentCodec = MongoClient.getDefaultCodecRegistry().get(Document.class);
        CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
            CodecRegistries.fromCodecs(new TodoCodec(defaultDocumentCodec)),
            MongoClient.getDefaultCodecRegistry()
        );
        MongoClientOptions options = MongoClientOptions.builder().codecRegistry(codecRegistry).build();
        MongoClientURI uri = new MongoClientURI(APP_DS_URL, MongoClientOptions.builder(options));
        mongoClient = new MongoClient(uri);

        database = mongoClient.getDatabase(APP_DS_DB);
        logger.info("connected with database: " + database);
    }

    public static void close() {
        mongoClient.close();
        logger.info("database connection has been closed");
    }

    public static List<Todo> findAll() {
        BasicDBObject whereQuery = new BasicDBObject();

        MongoCollection<Document> docCollection = database.getCollection(APP_DS_DB_COLLECTION);
        List<Todo> todoList = new ArrayList<>();
        for(Todo todo : docCollection.find(whereQuery, Todo.class)) {
            todoList.add(todo);
        }
        return todoList;
    }

    public static Todo findById(String id) {
        BasicDBObject whereQuery = new BasicDBObject();
        whereQuery.put(Todo.FIELD_ID, id);
        MongoCollection<Document> docCollection = database.getCollection(APP_DS_DB_COLLECTION);
        FindIterable<Todo> todoCollection = docCollection.find(whereQuery, Todo.class);
        Todo todo = null;
        if (todoCollection.iterator().hasNext()) {
            todo = todoCollection.iterator().next();
        }
        return todo;
    }

    public static List<Todo> findByStatus(Status status) {
        BasicDBObject whereQuery = new BasicDBObject();
        String statusStr;
        if (status == Status.COMPLETED) {
            statusStr = Todo.FIELD_STATUS_COMPLETED;
        } else {
            statusStr = Todo.FIELD_STATUS_ACTIVE;
        }
        whereQuery.put(Todo.FIELD_STATUS, statusStr);

        MongoCollection<Document> docCollection = database.getCollection(APP_DS_DB_COLLECTION);
        FindIterable<Todo> todoCollection = docCollection.find(whereQuery, Todo.class);
        List<Todo> todoList = new ArrayList<>();
        for (Todo todo : todoCollection) {
            todoList.add(todo);
        }
        return todoList;
    }

    public static void create(Todo newTodo) {
        MongoCollection<Document> docCollection = database.getCollection(APP_DS_DB_COLLECTION);
        Document document = Document.parse(Todo.getJSON(newTodo));
        docCollection.insertOne(document);
    }

    public static void update(Todo updatedTodo) {
        BasicDBObject whereQuery = new BasicDBObject();
        whereQuery.put(Todo.FIELD_ID, updatedTodo.getTodoId());

        MongoCollection<Document> docCollection = database.getCollection(APP_DS_DB_COLLECTION);
        FindIterable<Todo> todoCollection = docCollection.find(whereQuery, Todo.class);
        Todo existingTodo = todoCollection.first();
        docCollection.replaceOne(BsonDocument.parse(Todo.getJSON(existingTodo)),
            Document.parse(Todo.getJSON(updatedTodo)));
    }

    public static void delete(Todo todo) {
        MongoCollection<Document> docCollection = database.getCollection(APP_DS_DB_COLLECTION);
        docCollection.deleteOne(Document.parse(Todo.getJSON(todo)));
    }

    public static void delete(List<Todo> todoList) {
        MongoCollection<Document> docCollection = database.getCollection(APP_DS_DB_COLLECTION);
        for (Todo todo : todoList) {
            docCollection.deleteMany(Document.parse(Todo.getJSON(todo)));
        }
    }
}