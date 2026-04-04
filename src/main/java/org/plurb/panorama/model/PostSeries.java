package org.plurb.panorama.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "post_series")
@IdClass(PostSeries.PostSeriesId.class)
public class PostSeries {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "series_id", nullable = false)
    private Series series;

    @Column(nullable = false)
    private int position;

    public Post getPost() { return post; }
    public void setPost(Post post) { this.post = post; }

    public Series getSeries() { return series; }
    public void setSeries(Series series) { this.series = series; }

    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }

    public static class PostSeriesId implements Serializable {
        private Long post;
        private Long series;

        @Override
        public boolean equals(Object o) {
            if (this == o) { return true; }
            if (!(o instanceof PostSeriesId other)) { return false; }
            return Objects.equals(post, other.post) && Objects.equals(series, other.series);
        }

        @Override
        public int hashCode() {
            return Objects.hash(post, series);
        }
    }
}