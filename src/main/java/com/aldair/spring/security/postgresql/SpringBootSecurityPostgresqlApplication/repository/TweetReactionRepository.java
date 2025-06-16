package com.aldair.spring.security.postgresql.SpringBootSecurityPostgresqlApplication.repository;
/*
import com.aldair.spring.security.postgresql.SpringBootSecurityPostgresqlApplication.models.TweetReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TweetReactionRepository extends JpaRepository<TweetReaction, Long> {
    
    // Consulta para obtener el conteo de reacciones por tipo de emoji para un tweet específico
    @Query("SELECT tr.reaction.description, COUNT(tr) as total " +
            "FROM TweetReaction tr " +
            "WHERE tr.tweet.id = :tweetId " +
            "GROUP BY tr.reaction.description " +
            "ORDER BY total DESC")
    List<Object[]> findMostVotedReactionByTweetId(@Param("tweetId") Long tweetId);

    // Método para eliminar todas las reacciones asociadas a un tweet
    void deleteByTweetId(Long tweetId);

    // Buscar por tweetId y userId (verifica si un usuario ya reaccionó a un tweet)
    Optional<TweetReaction> findByTweetIdAndUserId(Long tweetId, Long userId);

    // Buscar todas las reacciones asociadas a un tweet
    List<TweetReaction> findByTweetId(Long tweetId);
    
}
*/