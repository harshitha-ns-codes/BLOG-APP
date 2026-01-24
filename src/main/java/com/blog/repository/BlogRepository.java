package com.blog.repository;

import com.blog.entity.Blog;
import com.blog.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {
    Page<Blog> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<Blog> findByAuthorIdOrderByCreatedAtDesc(Long authorId, Pageable pageable);
    Page<Blog> findByCategoriesIn(List<Category> categories, Pageable pageable);
    
    @Query("SELECT b FROM Blog b WHERE b.title LIKE %:keyword% OR b.content LIKE %:keyword% OR b.excerpt LIKE %:keyword%")
    Page<Blog> searchBlogs(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT b FROM Blog b ORDER BY b.viewCount DESC")
    Page<Blog> findTopBlogsByViews(Pageable pageable);
}

