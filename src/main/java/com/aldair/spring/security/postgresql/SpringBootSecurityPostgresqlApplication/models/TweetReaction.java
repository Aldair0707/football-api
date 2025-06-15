package com.aldair.spring.security.postgresql.SpringBootSecurityPostgresqlApplication.models;

import jakarta.persistence.*;

@Entity
@Table(name = "tweet_reactions", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "tweet_id"})
})
public class TweetReaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reaction_id")
    private String reactionId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "tweet_id")
    private Long tweetId;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false) // Relación con User
    private User user;

    @ManyToOne
    @JoinColumn(name = "tweet_id", insertable = false, updatable = false) // Relación con Publicacion
    private Publicacion tweet;

    @ManyToOne
    @JoinColumn(name = "reaction_id", insertable = false, updatable = false) // Relación con Reaccion
    private Reaccion reaction;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReactionId() {
        return reactionId;
    }

    public void setReactionId(String reactionId) {
        this.reactionId = reactionId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTweetId() {
        return tweetId;
    }

    public void setTweetId(Long tweetId) {
        this.tweetId = tweetId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Publicacion getTweet() {
        return tweet;
    }

    public void setTweet(Publicacion tweet) {
        this.tweet = tweet;
    }

    public Reaccion getReaction() {
        return reaction;
    }

    public void setReaction(Reaccion reaction) {
        this.reaction = reaction;
    }

    // Getters y Setters

    
}
