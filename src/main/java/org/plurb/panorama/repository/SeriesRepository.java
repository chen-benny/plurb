package org.plurb.panorama.repository;

import org.plurb.panorama.model.Series;
import org.plurb.panorama.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SeriesRepository extends JpaRepository<Series, Long> {

    Optional<Series> findByAuthorAndSlug(User author, String slug);

    List<Series> findByAuthorOrderByCreatedAtDesc(User author);
}
