package com.zalmuk.swwim.api.controller;

import com.zalmuk.swwim.api.dto.chat.ChatMessageRequest;
import com.zalmuk.swwim.api.dto.chat.ChatMessageResponse;
import com.zalmuk.swwim.api.dto.chat.ChatRoomResponse;
import com.zalmuk.swwim.api.dto.chat.CreateChatRoomRequest;
import com.zalmuk.swwim.api.dto.common.ApiResponse;
import com.zalmuk.swwim.api.dto.common.PageResponse;
import com.zalmuk.swwim.api.entity.chat.ChatMessage;
import com.zalmuk.swwim.api.entity.chat.ChatRoom;
import com.zalmuk.swwim.api.entity.user.User;
import com.zalmuk.swwim.api.service.chat.ChatService;
import com.zalmuk.swwim.api.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * 채팅 컨트롤러
 */
@Tag(name = "Chat", description = "채팅 API")
@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {

    private final ChatService chatService;
    private final UserService userService;

    public ChatController(ChatService chatService, UserService userService) {
        this.chatService = chatService;
        this.userService = userService;
    }

    @Operation(summary = "채팅방 목록", description = "로그인한 사용자의 채팅방 목록을 조회합니다.")
    @GetMapping("/rooms")
    public ResponseEntity<ApiResponse<PageResponse<ChatRoomResponse>>> getRooms(
            @AuthenticationPrincipal String userId,
            @PageableDefault(size = 20) Pageable pageable) {
        User currentUser = userService.findById(userId).orElseThrow(() ->
                new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Page<ChatRoom> rooms = chatService.getUserChatRooms(userId, pageable);
        Page<ChatRoomResponse> responses = rooms.map(room -> {
            ChatRoomResponse response = ChatRoomResponse.from(room, currentUser);
            response.setUnreadCount((int) chatService.getUnreadCount(room.getId(), userId));
            return response;
        });
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(responses)));
    }

    @Operation(summary = "채팅방 생성", description = "새로운 채팅방을 생성합니다.")
    @PostMapping("/rooms")
    public ResponseEntity<ApiResponse<ChatRoomResponse>> createRoom(
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody CreateChatRoomRequest request) {
        User currentUser = userService.findById(userId).orElseThrow(() ->
                new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        ChatRoom room = chatService.getOrCreateRoom(userId, request.getParticipantId());
        return ResponseEntity.ok(ApiResponse.success(ChatRoomResponse.from(room, currentUser), "채팅방이 생성되었습니다."));
    }

    @Operation(summary = "메시지 목록", description = "채팅방의 메시지 목록을 조회합니다.")
    @GetMapping("/rooms/{id}/messages")
    public ResponseEntity<ApiResponse<PageResponse<ChatMessageResponse>>> getMessages(
            @AuthenticationPrincipal String userId,
            @PathVariable UUID id,
            @PageableDefault(size = 50) Pageable pageable) {
        Page<ChatMessage> messages = chatService.getMessages(id, pageable);
        Page<ChatMessageResponse> responses = messages.map(ChatMessageResponse::from);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(responses)));
    }

    @Operation(summary = "메시지 전송", description = "채팅방에 메시지를 전송합니다.")
    @PostMapping("/rooms/{id}/messages")
    public ResponseEntity<ApiResponse<ChatMessageResponse>> sendMessage(
            @AuthenticationPrincipal String userId,
            @PathVariable UUID id,
            @Valid @RequestBody ChatMessageRequest request) {
        ChatMessage message = chatService.sendMessage(id, userId, request.getMessage());
        return ResponseEntity.ok(ApiResponse.success(ChatMessageResponse.from(message), "메시지가 전송되었습니다."));
    }

    @Operation(summary = "읽음 처리", description = "채팅방의 메시지를 읽음 처리합니다.")
    @PostMapping("/rooms/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @AuthenticationPrincipal String userId,
            @PathVariable UUID id) {
        chatService.markMessagesAsRead(id, userId);
        return ResponseEntity.ok(ApiResponse.success(null, "읽음 처리되었습니다."));
    }

    @Operation(summary = "읽지 않은 총 메시지 수", description = "전체 채팅방의 읽지 않은 메시지 수를 조회합니다.")
    @GetMapping("/unread/count")
    public ResponseEntity<ApiResponse<Integer>> getTotalUnreadCount(
            @AuthenticationPrincipal String userId) {
        long count = chatService.getTotalUnreadCount(userId);
        return ResponseEntity.ok(ApiResponse.success((int) count));
    }
}
