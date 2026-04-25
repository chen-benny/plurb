package org.plurb.panorama.controller;

import org.plurb.panorama.model.Post;
import org.plurb.panorama.model.Series;
import org.plurb.panorama.model.User;
import org.plurb.panorama.model.PostStatus;
import org.plurb.panorama.repository.UserRepository;
import org.plurb.panorama.service.MarkdownService;
import org.plurb.panorama.service.PostService;
import org.plurb.panorama.service.SeriesService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Controller
public class PublicController {

    private final UserRepository userRepository;
    private final PostService postService;
    private final SeriesService seriesService;
    private final MarkdownService markdownService;

    public PublicController(UserRepository userRepository,
                            PostService postService,
                            SeriesService seriesService,
                            MarkdownService markdownService) {
        this.userRepository = userRepository;
        this.postService = postService;
        this.seriesService = seriesService;
        this.markdownService = markdownService;
    }

    @GetMapping("/{username}")
    public String profile(@PathVariable String username, Model model) {
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + username));
        model.addAttribute("author", author);
        model.addAttribute("posts", postService.getPublishedPosts(author));
        model.addAttribute("seriesList", seriesService.getAllSeries(author));
        model.addAttribute("renderedAbout", author.getAboutMd() != null
            ? markdownService.render(author.getAboutMd()) : "");
        return "public/profile";
    }

    @GetMapping("/{username}/{slug}")
    public String post(@PathVariable String username, @PathVariable String slug, Model model) {
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + username));
        Post post = postService.getPublishedPost(author, slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found: " + slug));
        model.addAttribute("author", author);
        model.addAttribute("post", post);
        model.addAttribute("renderedBody", markdownService.render(post.getBodyMd()));
        return "public/post";
    }

    @GetMapping("/{username}/tag/{tagSlug}")
    public String tag(@PathVariable String username, @PathVariable String tagSlug, Model model) {
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + username));
        model.addAttribute("author", author);
        model.addAttribute("tag", tagSlug);
        model.addAttribute("posts", postService.getPublishedPostsByTag(author, tagSlug));
        return "public/tag";
    }

    @GetMapping("/{username}/series/{seriesSlug}")
    public String series(@PathVariable String username, @PathVariable String seriesSlug, Model model) {
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + username));
        Series series = seriesService.getSeries(author, seriesSlug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Series not found: " + seriesSlug));
        model.addAttribute("author", author);
        model.addAttribute("series", series);
        return "public/series";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("posts", postService.getAllPublishedPosts());
        return "public/index";
    }

    @GetMapping("/{username}/about")
    public String about(@PathVariable String username, Model model) {
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + username));
        model.addAttribute("author", author);
        model.addAttribute("renderedBody", author.getAboutMd() != null
            ? markdownService.render(author.getAboutMd()) : "");
        return "public/about";
    }
}
