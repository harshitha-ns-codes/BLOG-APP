package com.blog.controller;

import com.blog.entity.Like;
import com.blog.entity.User;
import com.blog.service.LikeService;
import com.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/likes")
@CrossOrigin(origins = "*")
public class LikeController {
    @Autowired
    private LikeService likeService;

    @Autowired
    private UserService userService;

    @PostMapping("/blog/{blogId}")
    public ResponseEntity<Map<String, Object>> toggleLike(@PathVariable Long blogId, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        Like like = likeService.toggleLike(blogId, user);
        
        Map<String, Object> response = new HashMap<>();
        response.put("liked", like != null);
        response.put("likeCount", likeService.getLikeCount(blogId));
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/blog/{blogId}")
    public ResponseEntity<Map<String, Object>> getLikeInfo(@PathVariable Long blogId, Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        response.put("likeCount", likeService.getLikeCount(blogId));
        
        if (authentication != null) {
            Long userId = userService.findByUsername(authentication.getName()).getId();
            response.put("isLiked", likeService.isLiked(blogId, userId));
        } else {
            response.put("isLiked", false);
        }
        
        return ResponseEntity.ok(response);
    }
}

