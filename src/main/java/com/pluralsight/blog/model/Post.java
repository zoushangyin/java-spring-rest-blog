package com.pluralsight.blog.model;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Version
    private Long version;
    @NotNull
    @Size(min=4, max=100)
    private String title;
    @Column(length=1000000)
    @Lob
    private String body;
    @Temporal(TemporalType.DATE)
    @CreationTimestamp
    private Date date;
    @ManyToOne
    private Author author;

    public Post() {
        super();
    }

    public Post(String title, String body){//, Author author) {
        this();
        this.title = title;
        this.body = body;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Date getDate() {
        return date;
    }

    public String getDateStr() {
        DateFormat outputFormatter = new SimpleDateFormat("MM/dd/yyyy");
        return outputFormatter.format(this.date);
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object obj) {
        Post inputPost = (Post)obj;
        if (!this.title.equals(inputPost.getTitle())){
            System.out.println("titles not equal");
            return false;
        }
        if (!this.body.equals(inputPost.getBody())){
            System.out.println("body not equal");
            return false;}
        if (!this.author.equals(inputPost.getAuthor())){
            System.out.println("author not equal");
            return false;}
//        if (this.date.getDay()==inputPost.getDate().getDay() &&
//                this.date.getMonth()==inputPost.getDate().getMonth() &&
//                this.date.getYear()==inputPost.getDate().getYear()){
//            System.out.println("date not equal");
//            return false;}

        return true;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }
}
