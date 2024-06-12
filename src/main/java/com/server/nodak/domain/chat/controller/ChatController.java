package com.server.nodak.domain.chat.controller;

import com.server.nodak.domain.chat.dto.request.ChatRoomCreateRequest;
import com.server.nodak.domain.chat.dto.response.ChatRoomListResponse;
import com.server.nodak.domain.chat.service.ChatService;
import com.server.nodak.domain.user.domain.UserRole;
import com.server.nodak.global.common.response.ApiResponse;
import com.server.nodak.security.aop.AuthorizationRequired;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ApiResponse<ChatRoomListResponse>> getChatRoomList() {
        return ResponseEntity.ok().build();
    }

    // 채팅방 삭제
    @DeleteMapping("/chatrooms/{chatRoomId}")
    public ResponseEntity<ApiResponse<Void>> deleteChatRoom(@PathVariable long chatRoomId) {
        return ResponseEntity.ok().build();
    }


}
