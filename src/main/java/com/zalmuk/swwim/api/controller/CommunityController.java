package com.zalmuk.swwim.api.controller;

import com.zalmuk.swwim.api.dto.common.ApiResponse;
import com.zalmuk.swwim.api.dto.common.PageResponse;
import com.zalmuk.swwim.api.dto.community.CommentRequest;
import com.zalmuk.swwim.api.dto.community.CommentResponse;
import com.zalmuk.swwim.api.dto.community.PostRequest;
import com.zalmuk.swwim.api.dto.community.PostResponse;
import com.zalmuk.swwim.api.entity.community.CommunityPost;
import com.zalmuk.swwim.api.entity.community.PostComment;
import com.zalmuk.swwim.api.service.community.CommunityService;
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
 * 커뮤니티 컨트롤러
 */
@Tag(name = "Community", description = "커뮤니티 API")
@RestController
@RequestMapping("/api/v1/posts")
public class CommunityController {

    private final CommunityService communityService;

    public CommunityController(CommunityService communityService) {
        this.communityService = communityService;
    }

    @Operation(summary = "게시글 목록", description = "게시글 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<PostResponse>>> getPosts(
            @AuthenticationPrincipal String userId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<CommunityPost> posts;
        if (userId != null && !userId.isEmpty()) {
            posts = communityService.getPostsExcludingBlocked(userId, pageable);
        } else {
            posts = communityService.getAllPosts(pageable);
        }
        Page<PostResponse> responses = posts.map(PostResponse::from);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(responses)));
    }

    @Operation(summary = "내 게시글 목록", description = "내 게시글 목록을 조회합니다.")
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<PageResponse<PostResponse>>> getMyPosts(
            @AuthenticationPrincipal String userId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<CommunityPost> posts = communityService.getUserPosts(userId, pageable);
        Page<PostResponse> responses = posts.map(PostResponse::from);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(responses)));
    }

    @Operation(summary = "게시글 작성", description = "새 게시글을 작성합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<PostResponse>> createPost(
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody PostRequest request) {
        CommunityPost post = communityService.createPost(userId, request.getTitle(), request.getContent(), request.getImageUrl());
        return ResponseEntity.ok(ApiResponse.success(PostResponse.from(post), "게시글이 등록되었습니다."));
    }

    @Operation(summary = "게시글 상세", description = "게시글의 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PostResponse>> getPost(
            @AuthenticationPrincipal String userId,
            @PathVariable UUID id) {
        return communityService.findPostById(id)
                .map(post -> {
                    communityService.incrementViewCount(id);
                    PostResponse response = PostResponse.from(post);
                    if (userId != null && !userId.isEmpty()) {
                        response.setIsLiked(communityService.isPostLiked(id, userId));
                    }
                    return ResponseEntity.ok(ApiResponse.success(response));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "게시글 수정", description = "게시글을 수정합니다.")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PostResponse>> updatePost(
            @AuthenticationPrincipal String userId,
            @PathVariable UUID id,
            @Valid @RequestBody PostRequest request) {
        CommunityPost post = communityService.updatePost(id, userId, request.getTitle(), request.getContent(), request.getImageUrl());
        return ResponseEntity.ok(ApiResponse.success(PostResponse.from(post), "게시글이 수정되었습니다."));
    }

    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @AuthenticationPrincipal String userId,
            @PathVariable UUID id) {
        communityService.deletePost(id, userId);
        return ResponseEntity.ok(ApiResponse.success(null, "게시글이 삭제되었습니다."));
    }

    @Operation(summary = "좋아요", description = "게시글에 좋아요를 누릅니다.")
    @PostMapping("/{id}/like")
    public ResponseEntity<ApiResponse<Void>> likePost(
            @AuthenticationPrincipal String userId,
            @PathVariable UUID id) {
        communityService.likePost(id, userId);
        return ResponseEntity.ok(ApiResponse.success(null, "좋아요를 눌렀습니다."));
    }

    @Operation(summary = "좋아요 취소", description = "게시글 좋아요를 취소합니다.")
    @DeleteMapping("/{id}/like")
    public ResponseEntity<ApiResponse<Void>> unlikePost(
            @AuthenticationPrincipal String userId,
            @PathVariable UUID id) {
        communityService.unlikePost(id, userId);
        return ResponseEntity.ok(ApiResponse.success(null, "좋아요를 취소했습니다."));
    }

    @Operation(summary = "댓글 목록", description = "게시글의 댓글 목록을 조회합니다.")
    @GetMapping("/{id}/comments")
    public ResponseEntity<ApiResponse<PageResponse<CommentResponse>>> getComments(
            @PathVariable UUID id,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<PostComment> comments = communityService.getComments(id, pageable);
        Page<CommentResponse> responses = comments.map(CommentResponse::from);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(responses)));
    }

    @Operation(summary = "댓글 작성", description = "게시글에 댓글을 작성합니다.")
    @PostMapping("/{id}/comments")
    public ResponseEntity<ApiResponse<CommentResponse>> createComment(
            @AuthenticationPrincipal String userId,
            @PathVariable UUID id,
            @Valid @RequestBody CommentRequest request) {
        PostComment comment = communityService.createComment(id, userId, request.getText());
        return ResponseEntity.ok(ApiResponse.success(CommentResponse.from(comment), "댓글이 등록되었습니다."));
    }

    @Operation(summary = "댓글 삭제", description = "댓글을 삭제합니다.")
    @DeleteMapping("/{id}/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @AuthenticationPrincipal String userId,
            @PathVariable UUID id,
            @PathVariable UUID commentId) {
        communityService.deleteComment(commentId, userId);
        return ResponseEntity.ok(ApiResponse.success(null, "댓글이 삭제되었습니다."));
    }

    @Operation(summary = "댓글 좋아요")
    @PostMapping("/{id}/comments/{commentId}/like")
    public ResponseEntity<ApiResponse<Void>> likeComment(
            @AuthenticationPrincipal String userId,
            @PathVariable UUID id,
            @PathVariable UUID commentId) {
        communityService.likeComment(commentId, userId);
        return ResponseEntity.ok(ApiResponse.success(null, "댓글 좋아요를 눌렀습니다."));
    }

    @Operation(summary = "댓글 좋아요 취소")
    @DeleteMapping("/{id}/comments/{commentId}/like")
    public ResponseEntity<ApiResponse<Void>> unlikeComment(
            @AuthenticationPrincipal String userId,
            @PathVariable UUID id,
            @PathVariable UUID commentId) {
        communityService.unlikeComment(commentId, userId);
        return ResponseEntity.ok(ApiResponse.success(null, "댓글 좋아요를 취소했습니다."));
    }

    @Operation(summary = "인기 게시글", description = "인기 게시글 목록을 조회합니다.")
    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<PageResponse<PostResponse>>> getPopularPosts(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<CommunityPost> posts = communityService.getPopularPosts(pageable);
        Page<PostResponse> responses = posts.map(PostResponse::summary);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(responses)));
    }

    @Operation(summary = "게시글 검색", description = "제목 또는 내용으로 게시글을 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<PostResponse>>> searchPosts(
            @Parameter(description = "검색 키워드") @RequestParam String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<CommunityPost> posts = communityService.searchPosts(keyword, pageable);
        Page<PostResponse> responses = posts.map(PostResponse::summary);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(responses)));
    }
}
