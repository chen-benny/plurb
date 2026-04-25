package org.plurb.panorama.repository;

import org.plurb.panorama.model.User;
import org.plurb.panorama.model.Post;
import org.plurb.panorama.model.PostStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    Optional<Post> findByAuthorAndSlug(User author, String slug);

    List<Post> findByAuthorAndStatusOrderByPublishedAtDesc(User author, PostStatus status);

    List<Post> findByAuthorOrderByCreatedAtDesc(User author);

    List<Post> findByAuthorAndTagsSlugAndStatusOrderByCreatedAtDesc(User author, String slug, PostStatus status);

    List<Post> findByStatusOrderByPublishedAtDesc(PostStatus status);
}
