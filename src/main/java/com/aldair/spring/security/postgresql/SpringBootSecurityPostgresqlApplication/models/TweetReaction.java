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
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("tweetId")
    @JoinColumn(name = "tweet_id")
    private Publicacion tweet;

    @ManyToOne
    @MapsId("reactionId")
    @JoinColumn(name = "reaction_id")
    private Reaccion reaction;

    // Getters y Setters

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
        this.userId = user.getId();
        this.user = user;
    }

    public Publicacion getTweet() {
        return tweet;
    }

    public void setTweet(Publicacion tweet) {
        this.tweetId = tweet.getId();
        this.tweet = tweet;
    }

    public Reaccion getReaction() {
        return reaction;
    }

    public void setReaction(Reaccion reaction) {
        this.reactionId = reaction.getDescription().name();  // Asignamos el nombre de la reacción
        this.reaction = reaction;
    }
}
