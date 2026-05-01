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

    public List<Series> getAllSeriesAcrossUsers() {
        return seriesRepository.findAllByOrderByCreatedAtDesc();
    }

    public Optional<Series> getSeries(User author, String slug) {
        return seriesRepository.findByAuthorAndSlug(author, slug);
    }

    @Transactional
    public Series findOrCreate(User author, String title, String description) {
        return seriesRepository.findByAuthorAndTitleIgnoreCase(author, title)
                .orElseGet(() -> createSeries(author, title, generateSlug(title),
                        description == null || description.isBlank() ? null : description.trim()));
    }

    private String generateSlug(String title) {
        return title.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .trim()
                .replaceAll("[\\s-]+", "-");
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
