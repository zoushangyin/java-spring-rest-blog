package com.pluralsight.blog;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pluralsight.blog.data.AuthorRepository;
import com.pluralsight.blog.data.DatabaseLoader;
import com.pluralsight.blog.model.Author;
import com.pluralsight.blog.model.Post;
import com.pluralsight.blog.data.PostRepository;
import org.apache.commons.io.IOUtils;
import org.hibernate.Hibernate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.cdi.Eager;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.*;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
//@AutoConfigureMockMvc
//@PrepareForTest(DatabaseLoader.class)
public class Module2_Tests {

    //@Autowired
    //private DatabaseLoader databaseLoader;

    @Autowired
    private AuthorRepository authorRepository;

    //private AuthorRepository spyRepository;

    @Autowired
    private PostRepository postRepository;

    @Before
    public void setup() {
        Constructor<DatabaseLoader> constructor = null;
        try {
            constructor = DatabaseLoader.class.getDeclaredConstructor(PostRepository.class, AuthorRepository.class);
        } catch (NoSuchMethodException e) {
            //e.printStackTrace();
        }

        //spyRepository = Mockito.spy(authorRepository);
//        try {
//            databaseLoader = constructor.newInstance(postRepository, spyRepository);
//        } catch (Exception e) {
//            //e.printStackTrace();
//        }
    }


    @Test
    public void task_1() {
        Class c = AuthorRepository.class;
        Class[] interfaces = c.getInterfaces();

        assertEquals("Task 1: AuthorRepository should extend 1 interface - JpaRepository.",
                1, interfaces.length);

        assertEquals("Task 1: AuthorRepository should be an interface that extends JpaRepository<Author, Long>.",
                JpaRepository.class, interfaces[0]);
    }

    @Test
    public void task_2() {
        // Verify Author property called author exists in the Post class
        Field field = null;
        try {
            field =  Post.class.getDeclaredField("author");
        } catch (NoSuchFieldException e) {
            //e.printStackTrace();
        }

        String message = "Task 2: The Post class does not have a Author field named author.";
        assertNotNull(message, field);
        assertTrue(message, field.getType() == Author.class);

        // Verify that the Author field has the @ManyToOne annotation
        Annotation[] annotations = field.getDeclaredAnnotations();

        message = "Task 2: The field author should have 1 annotation - the @ManyToOne annotation.";
        assertEquals(message, 1, annotations.length);
        assertEquals(message, ManyToOne.class, annotations[0].annotationType());
        assertTrue("Task 2: The author's `@ManyToOne` annotation does not have `(fetch = FetchType.EAGER)`.", annotations[0].toString().contains("EAGER"));

        // Check for getter
        Method getAuthorMethod = null;
        try {
            getAuthorMethod = Post.class.getMethod("getAuthor");
        } catch (NoSuchMethodException e) { }
        assertNotNull("Task 3: The getAuthor() method does not exist in the Post class.", getAuthorMethod);

        // Check for setter
        Method setAuthorMethod = null;
        try {
            setAuthorMethod = Post.class.getMethod("setAuthor", Author.class);
        } catch (NoSuchMethodException e) { }
        assertNotNull("Task 3: The setAuthor() method does not exist in the Post class.", setAuthorMethod);
    }

    @Test
    public void task_3() {
        // Add the corresponding property to the Author Entity, which will be a List of Posts called posts with
        // the @OneToMany annotation.
        Field field = null;
        try {
            field =  Author.class.getDeclaredField("posts");
        } catch (NoSuchFieldException e) {
            //e.printStackTrace();
        }

        String message = "Task 3: The Author class does not have a List of Posts field named posts.";
        assertNotNull(message, field);
        assertTrue(message, field.getType() == List.class);
        // We also need to do a few additional things in the Author class:
        //  ** Add a default constructor that calls super() and also initializes posts to an empty ArrayList.

        boolean arrayListInit = false;
        try {
            Constructor<Author> constructor = Author.class.getConstructor();
            Author author = constructor.newInstance();
            if (author.getPosts().size() == 0)
                arrayListInit = true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue("Task 3: The List of posts was not initialized in the default constructor.", arrayListInit);

        //  ** Also add a getter for the List of Posts property.
        Method getPostsMethod = null;
        try {
            getPostsMethod = Author.class.getMethod("getPosts");
        } catch (NoSuchMethodException e) {
            //e.printStackTrace();
        }
        assertNotNull("Task 3: The getPosts() method does not exist in the Author class.", getPostsMethod);

        //  ** Instead of a setter, add a method called public void addPost(Post post) which adds a post to the list.
        Method addPostMethod = null;
        try {
            addPostMethod = Author.class.getMethod("addPost", Post.class);
        } catch (NoSuchMethodException e) {
            //e.printStackTrace();
        }
        assertNotNull("Task 3: The addPost(Post post) method does not exist in the Author class.", getPostsMethod);
    }

    @Test
    public void task_4() {
        // Task 1 - Add field AuthorRepository authorRepository to DatabaseLoader
        Field[] fields = DatabaseLoader.class.getDeclaredFields();

        boolean authorRepositoryExists = false;
        for (Field field : fields) {
            if (field.getName().equals("authorRepository") && field.getType().equals(AuthorRepository.class)) {
                authorRepositoryExists = true;
            }
        }

        String message = "Task 4: A field called authorRepository of type AuthorRepository does not exist in DatabaseLoader.";
        assertTrue(message, authorRepositoryExists);

        // Check for DatabaseLoader constructor with PostRepository parameter
        Constructor<DatabaseLoader> constructor = null;
        try {
            constructor = DatabaseLoader.class.getDeclaredConstructor(PostRepository.class, AuthorRepository.class);
        } catch (NoSuchMethodException e) {
            //e.printStackTrace();
        }

        message = "Task 4: A DatabaseLoader constructor with PostRepository and AuthorRepository parameters does not exist.";
        assertNotNull(message, constructor);
    }

    @Test
    public void task_5() {
        List<Author> authorsSaved = Arrays.asList(
                new Author("sholderness", "Sarah",  "Holderness", "password"),
                new Author("tbell", "Tom",  "Bell", "password"),
                new Author("efisher", "Eric",  "Fisher", "password"),
                new Author("csouza", "Carlos",  "Souza", "password")
        );

        List<Author> authorsFromRepo = authorRepository.findAll();

        if (authorsFromRepo.size() != authorsSaved.size())
            assertTrue("Task 5: The authors were not saved to the authorRepository correctly.", false);

        boolean listsEqual = true;
        for (int i = 0; i< authorsFromRepo.size(); i++) {
            if (!authorsFromRepo.get(i).equals(authorsSaved.get(i)))
                listsEqual = false;
        }
        assertTrue("Task 5: The authors were not saved to the authorRepository correctly.", listsEqual);
    }

    @Test
    public void task_6() {
        // Needed in Author.java:  (PERSIST not needed)
        // @OneToMany(/*cascade = CascadeType.PERSIST, */fetch = FetchType.EAGER)
        // private List<Post> posts;
        String[] templates = {
                "Smart Home %s", "Mobile %s - For When You're On he Go", "The %s - Your New Favorite Accessory"};
        String[] gadgets = {
                "Earbuds", "Speakers", "Tripod", "Instant Pot", "Coffee Cup", "Keyboard", "Sunglasses"};

        List<Author> authorsSaved = Arrays.asList(
                new Author("sholderness", "Sarah",  "Holderness", "password"),
                new Author("tbell", "Tom",  "Bell", "password"),
                new Author("efisher", "Eric",  "Fisher", "password"),
                new Author("csouza", "Carlos",  "Souza", "password")
        );

        List<Post> postsSaved = new ArrayList<>();

        IntStream.range(0,40).forEach(i->{
            String template = templates[i % templates.length];
            String gadget = gadgets[i % gadgets.length];
            Author author = authorsSaved.get(i % authorsSaved.size());

            String title = String.format(template, gadget);
            Post post = new Post(title, "Lorem ipsum dolor sit amet, consectetur adipiscing elit… ");
            // post.setAuthor(author);
            try {
                Method method = Post.class.getMethod("setAuthor", Author.class);
                method.invoke(post, author);
            } catch (Exception e) {
                assertTrue("Task 6: The Post class does not have a setAuthor() method.", false);
            }

            author.addPost(post);
            postsSaved.add(post);
        });

        List<Post> posts = postRepository.findAll();
        boolean postsEqual = false;
        for (int i = 0; i< posts.size(); i++) {
            try {
                if (checkPostsEqual(posts.get(i), postsSaved.get(i))) //posts.get(i).equals(postsSaved.get(i)))
                    postsEqual = true;
                else
                    postsEqual = false;
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }

        assertTrue("Task 6: The posts saved to PostRepository are not correct", postsEqual);

        List<Author> authors = authorRepository.findAll();
        boolean authorsEqual = false;
        for (int i = 0; i< authors.size(); i++) {
            try {
                if (authors.get(i).equals(authorsSaved.get(i))){// && haveSamePosts(authors.get(i),authorsSaved.get(i))) {
                    authorsEqual = true;
                }
                else
                    authorsEqual = false;
            } catch (org.hibernate.LazyInitializationException e) {
                e.printStackTrace();
                assertTrue("Task 2: The author's `@ManyToOne` annotation does not have `(fetch = FetchType.EAGER)`.", false);
            } catch (IndexOutOfBoundsException e){
                authorsEqual = false;
            }
        }

        assertTrue("Task 6: The authors saved to AuthorRepository are not correct", authorsEqual);
    }

    private boolean haveSamePosts(Author author1, Author author2) {
        Hibernate.initialize(author1.getPosts());
        Hibernate.initialize(author2.getPosts());
        try {
            if (author1.getPosts().size() != author2.getPosts().size())
                return false;

            for (int i = 0; i < author1.getPosts().size(); i++) {
                //if (!author1.getPosts().contains(author2.getPosts().get(i)) ||
                //        !author2.getPosts().contains(author1.getPosts().get(i)))
                if (!author1.getPosts().get(i).equals(author2.getPosts().get(i)))
                    return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private boolean checkPostsEqual(Post post1, Post post2) {
        if (!post1.getTitle().equals(post2.getTitle()))
            return false;
        if (!post1.getBody().equals(post2.getBody()))
            return false;
        try {
            Method method = Post.class.getMethod("getAuthor");
            Author author1 = (Author)method.invoke(post1);
            Author author2 = (Author)method.invoke(post2);
            if (!author1.equals(author2))
                return false;
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Test
    public void task_7() {
        Method method = null;
        try {
            method = PostRepository.class.getMethod("findByAuthor_Lastname", String.class);
        } catch (Exception e) {
            ////e.printStackTrace();
        }

        assertNotNull("Task 5: The method findByAuthor_Lastname(String lastname) doesn't exist in the PostRepository class.", method );
    }

    @Test
    public void task_8() {
        // Verify String property called username exists in the Author class
        Field field = null;
        try {
            field =  Author.class.getDeclaredField("username");
        } catch (NoSuchFieldException e) {
            //e.printStackTrace();
        }

        String message = "Task 8: The `Author` class does not have a `String` field named `username`.";
        assertNotNull(message, field);
        assertTrue(message, field.getType() == String.class);

        // Verify that the Author field has the @ManyToOne annotation
        Annotation[] annotations = field.getDeclaredAnnotations();

        message = "Task 8: The field `username` should have 1 annotation - the `@JsonIgnore` annotation.";
        assertEquals(message, 1, annotations.length);
        assertEquals(message, JsonIgnore.class, annotations[0].annotationType());
        //System.out.println(annotations[0]);

        // Verify String property called password exists in the Author class
        field = null;
        try {
            field =  Author.class.getDeclaredField("password");
        } catch (NoSuchFieldException e) {
            //e.printStackTrace();
        }

        message = "Task 8: The `Author` class does not have a `String` field named `password`.";
        assertNotNull(message, field);
        assertTrue(message, field.getType() == String.class);

        // Verify that the Author field has the @ManyToOne annotation
        annotations = field.getDeclaredAnnotations();

        message = "Task 8: The field `password` should have 1 annotation - the `@JsonIgnore` annotation.";
        assertEquals(message, 1, annotations.length);
        assertEquals(message, JsonIgnore.class, annotations[0].annotationType());
        //System.out.println(annotations[0]);
    }

    @Test
    public void task_9() {
        String message = "Task 9: The class `AuthorRepository` should have 1 annotation - `@RepositoryRestResource(exported = false)`.";
        Annotation[] annotations = AuthorRepository.class.getDeclaredAnnotations();
        assertEquals(message, 1, annotations.length);
        assertEquals(message, RepositoryRestResource.class, annotations[0].annotationType());
        assertTrue(message, annotations[0].toString().contains("exported=false"));
    }
}