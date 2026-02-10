package com.zalmuk.swwim.api.service.chat;

import com.zalmuk.swwim.api.entity.chat.ChatMessage;
import com.zalmuk.swwim.api.entity.chat.ChatRoom;
import com.zalmuk.swwim.api.entity.user.User;
import com.zalmuk.swwim.api.repository.chat.ChatMessageRepository;
import com.zalmuk.swwim.api.repository.chat.ChatRoomRepository;
import com.zalmuk.swwim.api.repository.user.BlockedUserRepository;
import com.zalmuk.swwim.api.repository.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final BlockedUserRepository blockedUserRepository;

    public ChatService(ChatRoomRepository chatRoomRepository,
                       ChatMessageRepository chatMessageRepository,
                       UserRepository userRepository,
                       BlockedUserRepository blockedUserRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository = userRepository;
        this.blockedUserRepository = blockedUserRepository;
    }

    public Optional<ChatRoom> findRoomById(UUID id) {
        return chatRoomRepository.findById(id);
    }

    public Page<ChatRoom> getUserChatRooms(String userId, Pageable pageable) {
        return chatRoomRepository.findActiveRoomsByUserId(userId, pageable);
    }

    public Optional<ChatRoom> findRoomByParticipants(String userId1, String userId2) {
        User user1 = userRepository.findById(userId1).orElse(null);
        User user2 = userRepository.findById(userId2).orElse(null);
        if (user1 == null || user2 == null) {
            return Optional.empty();
        }
        return chatRoomRepository.findByParticipants(user1, user2);
    }

    @Transactional
    public ChatRoom getOrCreateRoom(String userId1, String userId2) {
        User user1 = userRepository.findById(userId1)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        User user2 = userRepository.findById(userId2)
                .orElseThrow(() -> new IllegalArgumentException("대화 상대를 찾을 수 없습니다."));

        // 차단 여부 확인
        if (blockedUserRepository.existsByUserAndBlockedUser(user1, user2) ||
            blockedUserRepository.existsByUserAndBlockedUser(user2, user1)) {
            throw new IllegalStateException("차단된 사용자와는 대화할 수 없습니다.");
        }

        return chatRoomRepository.findByParticipants(user1, user2)
                .orElseGet(() -> {
                    ChatRoom room = new ChatRoom(user1, user2);
                    return chatRoomRepository.save(room);
                });
    }

    public Page<ChatMessage> getMessages(UUID roomId, Pageable pageable) {
        return chatRoomRepository.findById(roomId)
                .map(room -> chatMessageRepository.findByRoomOrderByCreatedAtDesc(room, pageable))
                .orElse(Page.empty());
    }

    @Transactional
    public ChatMessage sendMessage(UUID roomId, String senderId, String message) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (!room.isParticipant(sender)) {
            throw new IllegalArgumentException("해당 채팅방의 참여자가 아닙니다.");
        }

        ChatMessage chatMessage = new ChatMessage(room, sender, message);
        chatMessage = chatMessageRepository.save(chatMessage);

        // 채팅방 마지막 메시지 업데이트
        room.setLastMessage(message);
        room.setLastMessageAt(LocalDateTime.now());
        chatRoomRepository.save(room);

        return chatMessage;
    }

    @Transactional
    public void markMessagesAsRead(UUID roomId, String userId) {
        chatMessageRepository.markAllAsRead(roomId, userId);
    }

    public long getUnreadCount(UUID roomId, String userId) {
        return chatMessageRepository.countUnreadMessages(roomId, userId);
    }

    public long getTotalUnreadCount(String userId) {
        return chatMessageRepository.countTotalUnreadMessages(userId);
    }

    @Transactional
    public void deleteRoom(UUID roomId, String userId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (!room.isParticipant(user)) {
            throw new IllegalArgumentException("해당 채팅방의 참여자가 아닙니다.");
        }

        chatRoomRepository.delete(room);
    }
}
