package com.hyomyang.oauth2login.oauth2.domain;


import jakarta.persistence.*;

@Entity
public class Post {
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    private String title;
    private String content;
}
