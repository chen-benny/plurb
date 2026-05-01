package org.plurb.panorama.repository;

import org.plurb.panorama.model.PostStatus;
import org.plurb.panorama.model.Tag;
import org.plurb.panorama.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findBySlug(String slug);

    Optional<Tag> findByName(String name);

    List<Tag> findByPostsStatusOrderByNameAsc(PostStatus status);

    List<Tag> findByPostsAuthorOrderByNameAsc(User author);
}
