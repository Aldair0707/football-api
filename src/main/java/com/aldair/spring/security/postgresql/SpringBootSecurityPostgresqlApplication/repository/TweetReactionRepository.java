package com.aldair.spring.security.postgresql.SpringBootSecurityPostgresqlApplication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aldair.spring.security.postgresql.SpringBootSecurityPostgresqlApplication.models.EReaction;
import com.aldair.spring.security.postgresql.SpringBootSecurityPostgresqlApplication.models.TweetReaction;
import com.aldair.spring.security.postgresql.SpringBootSecurityPostgresqlApplication.models.Publicacion;
import com.aldair.spring.security.postgresql.SpringBootSecurityPostgresqlApplication.models.User;
import com.aldair.spring.security.postgresql.SpringBootSecurityPostgresqlApplication.models.Reaccion;

import java.util.List;
import java.util.Optional;

@Repository
public interface TweetReactionRepository extends JpaRepository<TweetReaction, Long> {

    // Método para encontrar una reacción por tweet y usuario
    Optional<TweetReaction> findByTweetAndUser(Publicacion tweet, User user);

    // Contar las reacciones por tweet y tipo de reacción
    Long countByTweetAndReaction(Publicacion tweet, Reaccion reaction);

    // Obtener todas las reacciones para un tweet
    List<TweetReaction> findByTweet(Publicacion tweet);
}
