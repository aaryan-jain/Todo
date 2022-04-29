package org.incubyte.todo;

import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@MicronautTest
public class TodoControllerTest {

    @Inject
    @Client("/")
    HttpClient httpClient;


    @Test
    public void create_a_todo_object_with_default_state_not_done_and_save_it_in_db() {
        Todo todo = new Todo();
        todo.setDescription("Remember to hydrate");


        Todo savedTodo = this.httpClient.toBlocking().retrieve(HttpRequest.POST("/todos", todo), Argument.of(Todo.class));

        assertThat(savedTodo.getDescription()).isEqualTo("Remember to hydrate");
        assertThat(savedTodo.isDone()).isFalse();
        assertThat(savedTodo.getId()).isPositive();

        Todo retrievedTodo = this.httpClient.toBlocking().retrieve(HttpRequest.GET("/todos/" + savedTodo.getId()), Argument.of(Todo.class));

        assertThat(retrievedTodo.getId()).isEqualTo(savedTodo.getId());
        assertThat(retrievedTodo.getDescription()).isEqualTo(savedTodo.getDescription());
        assertThat(retrievedTodo.isDone()).isEqualTo(savedTodo.isDone());
    }


    @Test
    public void test_for_todo_CRUD_and_filtering()
    {
        Todo todo1 = new Todo();

        todo1.setDescription("I need to do homework");
        todo1.setDone(false);


        Todo todo2 = new Todo();

        todo2.setDescription("I need to have a bath");
        todo2.setDone(true);

        Todo savedTodo1 = this.httpClient.toBlocking().retrieve(HttpRequest.POST("/todos", todo1), Argument.of(Todo.class));
        assertThat(savedTodo1.getDescription()).isEqualTo("I need to do homework");
        assertThat(savedTodo1.isDone()).isFalse();
        assertThat(savedTodo1.getId()).isPositive();

        Todo retrievedTodo = this.httpClient.toBlocking().retrieve(HttpRequest.GET("/todos/" + savedTodo1.getId()), Argument.of(Todo.class));

        assertThat(retrievedTodo.getId()).isEqualTo(savedTodo1.getId());
        assertThat(retrievedTodo.getDescription()).isEqualTo(savedTodo1.getDescription());
        assertThat(retrievedTodo.isDone()).isEqualTo(savedTodo1.isDone());

        Todo savedTodo2 = this.httpClient.toBlocking().retrieve(HttpRequest.POST("/todos", todo2), Argument.of(Todo.class));
        assertThat(savedTodo2.getDescription()).isEqualTo("I need to have a bath");
        assertThat(savedTodo2.isDone()).isTrue();
        assertThat(savedTodo2.getId()).isPositive();


        List<Todo> retrivedTodoList = httpClient.toBlocking().retrieve(
                HttpRequest.GET("todos/"), Argument.listOf(Todo.class));

        assertThat(retrivedTodoList).containsExactly(savedTodo1, savedTodo2);
        List<Todo> retrivedOpenTodoList = httpClient.toBlocking().retrieve(
                HttpRequest.GET("todos/open"), Argument.listOf(Todo.class));

        assertThat(retrivedOpenTodoList).containsExactly(savedTodo1);

        List<Todo> retrivedCloseTodoList = httpClient.toBlocking().retrieve(
                HttpRequest.GET("todos/close"), Argument.listOf(Todo.class));

        assertThat(retrivedCloseTodoList).containsExactly(savedTodo2);

    }



}
