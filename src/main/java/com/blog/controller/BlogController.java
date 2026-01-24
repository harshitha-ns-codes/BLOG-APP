package com.blog.controller;

import com.blog.dto.BlogDTO;
import com.blog.entity.Blog;
import com.blog.entity.User;
import com.blog.service.BlogService;
import com.blog.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/blogs")
@CrossOrigin(origins = "*")
public class BlogController {
    @Autowired
    private BlogService blogService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllBlogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sort) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Blog> blogs;
        
        if (search != null && !search.isEmpty()) {
            blogs = blogService.searchBlogs(search, pageable);
        } else if ("popular".equals(sort)) {
            blogs = blogService.getTopBlogs(pageable);
        } else {
            blogs = blogService.getAllBlogs(pageable);
        }
        
        Page<BlogDTO> blogDTOs = blogs.map(blog -> blogService.convertToDTO(blog, null));
        
        Map<String, Object> response = new HashMap<>();
        response.put("blogs", blogDTOs.getContent());
        response.put("currentPage", blogDTOs.getNumber());
        response.put("totalItems", blogDTOs.getTotalElements());
        response.put("totalPages", blogDTOs.getTotalPages());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BlogDTO> getBlogById(@PathVariable Long id, Authentication authentication) {
        Blog blog = blogService.getBlogById(id);
        Long userId = authentication != null ? 
            userService.findByUsername(authentication.getName()).getId() : null;
        return ResponseEntity.ok(blogService.convertToDTO(blog, userId));
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<Map<String, Object>> getBlogsByAuthor(
            @PathVariable Long authorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Blog> blogs = blogService.getBlogsByAuthor(authorId, pageable);
        Page<BlogDTO> blogDTOs = blogs.map(blog -> blogService.convertToDTO(blog, null));
        
        Map<String, Object> response = new HashMap<>();
        response.put("blogs", blogDTOs.getContent());
        response.put("currentPage", blogDTOs.getNumber());
        response.put("totalItems", blogDTOs.getTotalElements());
        response.put("totalPages", blogDTOs.getTotalPages());
        
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<BlogDTO> createBlog(
            @Valid @RequestBody Blog blog,
            @RequestParam(required = false) String[] categories,
            Authentication authentication) {
        User author = userService.findByUsername(authentication.getName());
        Set<String> categorySet = categories != null ? 
            java.util.Arrays.stream(categories).collect(java.util.stream.Collectors.toSet()) : null;
        Blog createdBlog = blogService.createBlog(blog, author, categorySet);
        return ResponseEntity.ok(blogService.convertToDTO(createdBlog, author.getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BlogDTO> updateBlog(
            @PathVariable Long id,
            @Valid @RequestBody Blog blog,
            Authentication authentication) {
        User author = userService.findByUsername(authentication.getName());
        Blog updatedBlog = blogService.updateBlog(id, blog, author);
        return ResponseEntity.ok(blogService.convertToDTO(updatedBlog, author.getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBlog(@PathVariable Long id, Authentication authentication) {
        User author = userService.findByUsername(authentication.getName());
        blogService.deleteBlog(id, author);
        return ResponseEntity.ok().build();
    }
}

