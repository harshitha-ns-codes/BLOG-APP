package com.blog.service;

import com.blog.dto.BlogDTO;
import com.blog.entity.Blog;
import com.blog.entity.Category;
import com.blog.entity.User;
import com.blog.repository.BlogRepository;
import com.blog.repository.CategoryRepository;
import com.blog.repository.LikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BlogService {
    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    public Page<Blog> getAllBlogs(Pageable pageable) {
        return blogRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    public Blog getBlogById(Long id) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blog not found"));
        blog.setViewCount(blog.getViewCount() + 1);
        return blogRepository.save(blog);
    }

    public Page<Blog> getBlogsByAuthor(Long authorId, Pageable pageable) {
        return blogRepository.findByAuthorIdOrderByCreatedAtDesc(authorId, pageable);
    }

    public Page<Blog> searchBlogs(String keyword, Pageable pageable) {
        return blogRepository.searchBlogs(keyword, pageable);
    }

    public Page<Blog> getTopBlogs(Pageable pageable) {
        return blogRepository.findTopBlogsByViews(pageable);
    }

    @Transactional
    public Blog createBlog(Blog blog, User author, Set<String> categoryNames) {
        blog.setAuthor(author);
        
        if (categoryNames != null && !categoryNames.isEmpty()) {
            Set<Category> categories = categoryNames.stream()
                    .map(name -> categoryRepository.findByName(name)
                            .orElseGet(() -> {
                                Category cat = new Category();
                                cat.setName(name);
                                return categoryRepository.save(cat);
                            }))
                    .collect(Collectors.toSet());
            blog.setCategories(categories);
        }
        
        return blogRepository.save(blog);
    }

    @Transactional
    public Blog updateBlog(Long id, Blog updatedBlog, User author) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blog not found"));
        
        if (!blog.getAuthor().getId().equals(author.getId())) {
            throw new RuntimeException("You can only update your own blogs");
        }
        
        blog.setTitle(updatedBlog.getTitle());
        blog.setContent(updatedBlog.getContent());
        blog.setExcerpt(updatedBlog.getExcerpt());
        blog.setFeaturedImageUrl(updatedBlog.getFeaturedImageUrl());
        
        return blogRepository.save(blog);
    }

    @Transactional
    public void deleteBlog(Long id, User author) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blog not found"));
        
        if (!blog.getAuthor().getId().equals(author.getId())) {
            throw new RuntimeException("You can only delete your own blogs");
        }
        
        blogRepository.delete(blog);
    }

    public BlogDTO convertToDTO(Blog blog, Long currentUserId) {
        BlogDTO dto = new BlogDTO();
        dto.setId(blog.getId());
        dto.setTitle(blog.getTitle());
        dto.setContent(blog.getContent());
        dto.setExcerpt(blog.getExcerpt());
        dto.setFeaturedImageUrl(blog.getFeaturedImageUrl());
        dto.setCreatedAt(blog.getCreatedAt());
        dto.setUpdatedAt(blog.getUpdatedAt());
        dto.setViewCount(blog.getViewCount());
        dto.setAuthor(userService.convertToDTO(blog.getAuthor()));
        dto.setCategories(blog.getCategories().stream()
                .map(Category::getName)
                .collect(Collectors.toSet()));
        dto.setCommentCount((long) blog.getComments().size());
        dto.setLikeCount(likeRepository.countByBlogId(blog.getId()));
        if (currentUserId != null) {
            dto.setIsLiked(likeRepository.existsByUserIdAndBlogId(currentUserId, blog.getId()));
        }
        return dto;
    }
}

