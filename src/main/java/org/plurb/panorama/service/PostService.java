package org.plurb.panorama.service;

import org.plurb.panorama.model.*;
import org.plurb.panorama.repository.PostRepository;
import org.plurb.panorama.repository.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final TagRepository tagRepository;

    public PostService(PostRepository postRepository, TagRepository tagRepository) {
        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
    }

    public List<Post> getPublishedPosts(User author) {
        return postRepository.findByAuthorAndStatusOrderByPublishedAtDesc(
                author, PostStatus.PUBLISHED);
    }

    public List<Post> getAllPosts(User author) {
        return postRepository.findByAuthorOrderByCreatedAtDesc(author);
    }

    public Optional<Post> getPublishedPost(User author, String slug) {
        return postRepository.findByAuthorAndSlug(author, slug)
                .filter(post -> post.getStatus().equals(PostStatus.PUBLISHED));
    }

    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id);
    }

    public List<Post> getPublishedPostsByTags(User author, String tagSlug) {
        return postRepository.findByAuthorAndTagsSlugAndStatusOrderByCreatedAtDesc(
                author, tagSlug, PostStatus.PUBLISHED);
    }

    @Transactional
    public Post createPost(User author, String title, String slug,
                           String bodyMd, List<String> tagNames) {
        Post post = new Post();
        post.setAuthor(author);
        post.setTitle(title);
        post.setSlug(slug);
        post.setBodyMd(bodyMd);
        post.setTags(resolveTags(tagNames));
        return postRepository.save(post);
    }

    @Transactional
    public Post updatePost(Post post, String title, String slug,
                           String bodyMd, List<String> tagNames) {
        post.setTitle(title);
        post.setSlug(slug);
        post.setBodyMd(bodyMd);
        post.setTags(resolveTags(tagNames));
        post.setUpdatedAt(OffsetDateTime.now());
        return postRepository.save(post);
    }

    @Transactional
    public Post togglePublish(Post post) {
        if (post.getStatus().equals(PostStatus.DRAFT)) {
            post.setStatus(PostStatus.PUBLISHED);
            post.setUpdatedAt(OffsetDateTime.now());
        } else {
            post.setStatus(PostStatus.DRAFT);
            post.setPublishedAt(null);
        }
        post.setUpdatedAt(OffsetDateTime.now());
        return postRepository.save(post);
    }

    @Transactional
    public void deletePost(Post post) {
        postRepository.delete(post);
    }

    // find existing or create new tags
    private List<Tag> resolveTags(List<String> tagNames) {
        List<Tag> tags = new ArrayList<>();
        for (String tagName : tagNames) {
            String trimmed = tagName.trim().toLowerCase();
            if (trimmed.isEmpty()) { continue; }
            Tag tag = tagRepository.findByName(trimmed).orElseGet(() -> {
                Tag newTag = new Tag();
                newTag.setName(trimmed);
                newTag.setSlug(trimmed.replaceAll("[^a-z0-9]+", "-"));
                return tagRepository.save(newTag);
            });
            tags.add(tag);
        }
        return tags;
    }


}
