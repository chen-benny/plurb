package org.plurb.panorama.service;

import org.plurb.panorama.model.Series;
import org.plurb.panorama.model.User;
import org.plurb.panorama.repository.SeriesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class SeriesService {

    private final SeriesRepository seriesRepository;

    public SeriesService(SeriesRepository seriesRepository) {
        this.seriesRepository = seriesRepository;
    }

    public List<Series> getAllSeries(User author) {
        return seriesRepository.findByAuthorOrderByCreatedAtDesc(author);
    }

    public Optional<Series> getSeries(User author, String slug) {
        return seriesRepository.findByAuthorAndSlug(author, slug);
    }

    @Transactional
    public Series createSeries(User author, String title, String slug, String description) {
        Series series = new Series();
        series.setAuthor(author);
        series.setTitle(title);
        series.setSlug(slug);
        series.setDescription(description);
        return seriesRepository.save(series);
    }

    @Transactional
    public void deleteSeries(Series series) {
        seriesRepository.delete(series);
    }
}
