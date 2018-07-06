package com.sankarg.webapps.model;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class TodoCodec implements Codec<Todo> {
    private Codec<Document> documentCodec;

    public TodoCodec(Codec<Document> docCodec) {
        this.documentCodec = docCodec;
    }

    @Override
    public void encode(BsonWriter writer, Todo todo, EncoderContext eContext) {
        Document document = new Document();
        document.append(Todo.FIELD_ID, todo.getTodoId());
        document.append(Todo.FIELD_TITLE, todo.getTitle());
        document.append(Todo.FIELD_STATUS, todo.getStatus());
        //writer.writeString(document.toJson());
        documentCodec.encode(writer, document, eContext);
    }

    @Override
    public Class<Todo> getEncoderClass() {
        return Todo.class;
    }

    @Override
    public Todo decode(BsonReader reader, DecoderContext dContext) {
        Document document = documentCodec.decode(reader, dContext);
        Status status = Status.COMPLETED;
        if (document.getString(Todo.FIELD_STATUS).equalsIgnoreCase(Todo.FIELD_STATUS_ACTIVE)) {
            status = Status.ACTIVE;
        } else if (document.getString(Todo.FIELD_STATUS).equalsIgnoreCase(Todo.FIELD_STATUS_COMPLETED)) {
            status = Status.COMPLETED;
        }
        return new Todo(document.getString(Todo.FIELD_ID), document.getString(Todo.FIELD_TITLE), status);
    }
}