package com.server.nodak.domain.chat.controller;

import com.server.nodak.domain.chat.dto.request.ChatRoomCreateRequest;
import com.server.nodak.domain.chat.dto.request.MessageRequest;
import com.server.nodak.domain.chat.dto.response.ChatRoomListResponse;
import com.server.nodak.domain.chat.service.ChatService;
import com.server.nodak.domain.user.domain.UserRole;
import com.server.nodak.global.common.response.ApiResponse;
import com.server.nodak.security.aop.AuthorizationRequired;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    // 채팅방 생성
    @PostMapping("/chatrooms")
    @AuthorizationRequired(UserRole.GENERAL)
    public ResponseEntity<ApiResponse<Void>> saveChatRoom(@RequestBody ChatRoomCreateRequest request,
                                                          Principal principal) {
        long requesterId = Long.parseLong(principal.getName());
        chatService.saveChatRoom(requesterId, request);

        return ResponseEntity.ok().build();
    }

    // 채팅방 리스트 조회
    @GetMapping("/chatrooms")
    @AuthorizationRequired(UserRole.GENERAL)
    public ResponseEntity<ApiResponse<Page<ChatRoomListResponse>>> getChatRoomList(@PageableDefault Pageable pageable,
                                                                                   Principal principal) {
        long requesterId = Long.parseLong(principal.getName());
        Page<ChatRoomListResponse> chatRoomList = chatService.findChatRoomList(requesterId, pageable);
        return ResponseEntity.ok(ApiResponse.success(chatRoomList));
    }

    // 채팅방 삭제
    @DeleteMapping("/chatrooms/{chatRoomId}")
    @AuthorizationRequired(UserRole.GENERAL)
    public ResponseEntity<ApiResponse<Void>> deleteChatRoom(@PathVariable long chatRoomId,
                                                            Principal principal) {
        chatService.deleteChatRoom(chatRoomId);
        return ResponseEntity.ok().build();
    }

    @MessageMapping("/{chatRoomId}")
    @AuthorizationRequired(UserRole.GENERAL)
    public void sendMessage(@DestinationVariable String chatRoomId,
                            MessageRequest message,
                            Principal principal) {
        long requesterId = Long.parseLong(principal.getName());
        chatService.saveMessage(requesterId, Long.parseLong(chatRoomId), message);

        messagingTemplate.convertAndSend("/sub/" + chatRoomId, message);
    }

}
