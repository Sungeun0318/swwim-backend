package com.zalmuk.swwim.api.repository.notification;

import com.zalmuk.swwim.api.entity.enums.NotificationType;
import com.zalmuk.swwim.api.entity.notification.Notification;
import com.zalmuk.swwim.api.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    Page<Notification> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.isRead = false ORDER BY n.createdAt DESC")
    Page<Notification> findUnreadByUserId(@Param("userId") String userId, Pageable pageable);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.id = :userId AND n.isRead = false")
    long countUnreadByUserId(@Param("userId") String userId);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = CURRENT_TIMESTAMP WHERE n.user.id = :userId AND n.isRead = false")
    void markAllAsReadByUserId(@Param("userId") String userId);

    @Query("SELECT n FROM Notification n WHERE n.scheduledFor IS NOT NULL AND n.scheduledFor <= :now AND n.sentAt IS NULL")
    List<Notification> findPendingScheduledNotifications(@Param("now") LocalDateTime now);

    List<Notification> findByUserAndType(User user, NotificationType type);

    void deleteByUser(User user);
}
