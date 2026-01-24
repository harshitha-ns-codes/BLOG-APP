package com.blog.controller;

import com.blog.dto.CommentDTO;
import com.blog.entity.Comment;
import com.blog.entity.User;
import com.blog.service.CommentService;
import com.blog.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "*")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @GetMapping("/blog/{blogId}")
    public ResponseEntity<List<CommentDTO>> getCommentsByBlogId(@PathVariable Long blogId) {
        List<Comment> comments = commentService.getCommentsByBlogId(blogId);
        List<CommentDTO> commentDTOs = comments.stream()
                .map(commentService::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(commentDTOs);
    }

    @PostMapping
    public ResponseEntity<CommentDTO> createComment(
            @Valid @RequestBody Map<String, String> request,
            Authentication authentication) {
        Long blogId = Long.parseLong(request.get("blogId"));
        String content = request.get("content");
        User user = userService.findByUsername(authentication.getName());
        Comment comment = commentService.createComment(blogId, content, user);
        return ResponseEntity.ok(commentService.convertToDTO(comment));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentDTO> updateComment(
            @PathVariable Long id,
            @Valid @RequestBody Map<String, String> request,
            Authentication authentication) {
        String content = request.get("content");
        User user = userService.findByUsername(authentication.getName());
        Comment comment = commentService.updateComment(id, content, user);
        return ResponseEntity.ok(commentService.convertToDTO(comment));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        commentService.deleteComment(id, user);
        return ResponseEntity.ok().build();
    }
}

