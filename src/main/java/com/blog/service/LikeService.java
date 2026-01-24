package com.blog.service;

import com.blog.entity.Blog;
import com.blog.entity.Like;
import com.blog.entity.User;
import com.blog.repository.BlogRepository;
import com.blog.repository.LikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LikeService {
    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private BlogRepository blogRepository;

    @Transactional
    public Like toggleLike(Long blogId, User user) {

        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new RuntimeException("Blog not found"));

        Like existingLike = likeRepository
                .findByUserIdAndBlogId(user.getId(), blogId)
                .orElse(null);

        if (existingLike != null) {
            likeRepository.delete(existingLike);
            return null; // unliked
        }

        Like like = new Like();
        like.setBlog(blog);
        like.setUser(user);
        return likeRepository.save(like); // liked
    }


    public long getLikeCount(Long blogId) {
        return likeRepository.countByBlogId(blogId);
    }

    public boolean isLiked(Long blogId, Long userId) {
        return likeRepository.existsByUserIdAndBlogId(userId, blogId);
    }
}

