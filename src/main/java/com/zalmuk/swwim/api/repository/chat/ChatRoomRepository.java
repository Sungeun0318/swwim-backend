package com.zalmuk.swwim.api.repository.chat;

import com.zalmuk.swwim.api.entity.chat.ChatRoom;
import com.zalmuk.swwim.api.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, UUID> {

    @Query("SELECT cr FROM ChatRoom cr WHERE (cr.participant1 = :user1 AND cr.participant2 = :user2) OR (cr.participant1 = :user2 AND cr.participant2 = :user1)")
    Optional<ChatRoom> findByParticipants(@Param("user1") User user1, @Param("user2") User user2);

    @Query("SELECT cr FROM ChatRoom cr WHERE cr.participant1.id = :userId OR cr.participant2.id = :userId ORDER BY cr.lastMessageAt DESC")
    Page<ChatRoom> findByUserId(@Param("userId") String userId, Pageable pageable);

    @Query("SELECT cr FROM ChatRoom cr WHERE (cr.participant1.id = :userId OR cr.participant2.id = :userId) AND cr.lastMessage IS NOT NULL ORDER BY cr.lastMessageAt DESC")
    Page<ChatRoom> findActiveRoomsByUserId(@Param("userId") String userId, Pageable pageable);

    @Query("SELECT COUNT(cr) FROM ChatRoom cr WHERE cr.participant1.id = :userId OR cr.participant2.id = :userId")
    long countByUserId(@Param("userId") String userId);
}
