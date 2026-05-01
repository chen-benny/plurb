package org.plurb.panorama.repository;

import org.plurb.panorama.model.Post;
import org.plurb.panorama.model.PostSeries;
import org.plurb.panorama.model.Series;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostSeriesRepository extends JpaRepository<PostSeries, PostSeries.PostSeriesId> {

    void deleteByPost(Post post);

    @Query("SELECT COALESCE(MAX(ps.position), 0) FROM PostSeries ps WHERE ps.series = :series")
    int findMaxPositionBySeries(@Param("series") Series series);
}
