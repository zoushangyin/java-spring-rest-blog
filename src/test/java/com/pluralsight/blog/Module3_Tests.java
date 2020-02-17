package com.pluralsight.blog;

import com.pluralsight.blog.config.RestConfig;
import com.pluralsight.blog.data.AuthorRepository;
import com.pluralsight.blog.data.DatabaseLoader;
import com.pluralsight.blog.data.PostRepository;
import com.pluralsight.blog.model.Author;
import com.pluralsight.blog.model.Post;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.event.ValidatingRepositoryEventListener;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.*;
import javax.persistence.ManyToOne;
import javax.persistence.Version;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@PrepareForTest(RestConfig.class)
public class Module3_Tests {

    @Autowired
    private PostRepository postRepository;

    @Test
    public void task_1() {
        // Verify Author property called author exists in the Post class
        Field field = null;
        try {
            field =  Post.class.getDeclaredField("version");
        } catch (NoSuchFieldException e) {
            //e.printStackTrace();
        }

        String message = "Task 1: The Post class does not have a Long field named version.";
        assertNotNull(message, field);
        assertTrue(message, field.getType() == Long.class);

        Annotation[] annotations = field.getDeclaredAnnotations();

        message = "Task 1: The field `version` should have 1 annotation - the `@Version` annotation.";
        assertEquals(message, 1, annotations.length);
        assertEquals(message, Version.class, annotations[0].annotationType());

        // **** Add Author verion
        field = null;
        try {
            field =  Author.class.getDeclaredField("version");
        } catch (NoSuchFieldException e) {
            //e.printStackTrace();
        }

        message = "Task 1: The `Author` class does not have a `Long` field named `version`.";
        assertNotNull(message, field);
        assertTrue(message, field.getType() == Long.class);

        annotations = field.getDeclaredAnnotations();

        message = "Task 1: The field `version` should have 1 annotation - the `@Version` annotation.";
        assertEquals(message, 1, annotations.length);
        assertEquals(message, Version.class, annotations[0].annotationType());
    }

    @Test
    public void task_2() {
        // Replace data-categories.sql file to add Categories
        // Open data-categories.sql file and check contents
        Path path = Paths.get("src/main/resources/application.properties");
        String result = "";
        try {
            final String output = "";
            List<String> allLines = Files.readAllLines(path);
            result = String.join("\n", allLines);
        } catch (IOException e) {
            //e.printStackTrace();
        }

        assertTrue("Task 2: The `application.properties` file does not contain `spring.data.rest.basePath = /api/v1`.", result.contains("spring.data.rest.basePath = /api/v1"));
    }

    @Test
    public void task_3() {
        // Verify Author property called author exists in the Post class
        Field field = null;
        try {
            field =  Post.class.getDeclaredField("title");
        } catch (NoSuchFieldException e) {
            //e.printStackTrace();
        }

        String message = "Task 1: The Post class does not have a String field named title.";
        assertNotNull(message, field);
        assertTrue(message, field.getType() == String.class);

        Annotation[] annotations = field.getDeclaredAnnotations();

        message = "Task 3: The field `title` should have 2 annotations - `@NotNull` and `@Size(min=4, max=100)`.";
        assertEquals(message, 2, annotations.length);
        assertTrue(message, annotations[0].annotationType().equals(NotNull.class) || annotations[1].annotationType().equals(NotNull.class));
        assertTrue(message, annotations[0].annotationType().equals(Size.class) || annotations[1].annotationType().equals(Size.class));
    }
}