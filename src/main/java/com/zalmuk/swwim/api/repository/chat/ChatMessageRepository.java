package com.zalmuk.swwim.api.repository.chat;

import com.zalmuk.swwim.api.entity.chat.ChatMessage;
import com.zalmuk.swwim.api.entity.chat.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {

    Page<ChatMessage> findByRoomOrderByCreatedAtDesc(ChatRoom room, Pageable pageable);

    @Query("SELECT cm FROM ChatMessage cm WHERE cm.room.id = :roomId AND cm.sender.id != :userId AND cm.isRead = false")
    Page<ChatMessage> findUnreadMessages(@Param("roomId") UUID roomId, @Param("userId") String userId, Pageable pageable);

    @Query("SELECT COUNT(cm) FROM ChatMessage cm WHERE cm.room.id = :roomId AND cm.sender.id != :userId AND cm.isRead = false")
    long countUnreadMessages(@Param("roomId") UUID roomId, @Param("userId") String userId);

    @Query("SELECT COUNT(cm) FROM ChatMessage cm WHERE cm.room.id IN (SELECT cr.id FROM ChatRoom cr WHERE cr.participant1.id = :userId OR cr.participant2.id = :userId) AND cm.sender.id != :userId AND cm.isRead = false")
    long countTotalUnreadMessages(@Param("userId") String userId);

    @Modifying
    @Query("UPDATE ChatMessage cm SET cm.isRead = true, cm.readAt = CURRENT_TIMESTAMP WHERE cm.room.id = :roomId AND cm.sender.id != :userId AND cm.isRead = false")
    void markAllAsRead(@Param("roomId") UUID roomId, @Param("userId") String userId);

    void deleteByRoom(ChatRoom room);
}
