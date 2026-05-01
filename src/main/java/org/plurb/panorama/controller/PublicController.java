package org.plurb.panorama.controller;

import org.plurb.panorama.model.Post;
import org.plurb.panorama.model.Series;
import org.plurb.panorama.model.User;
import org.plurb.panorama.repository.UserRepository;
import org.plurb.panorama.service.MarkdownService;
import org.plurb.panorama.service.PostService;
import org.plurb.panorama.service.SeriesService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

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

    @GetMapping("/")
    public String landing() {
        return "landing";
    }

    @GetMapping("/panorama")
    public String index(@RequestParam(defaultValue = "0") int page,
                        @RequestParam(required = false) String tag,
                        Model model) {
        int size = 10;
        Page<Post> postsPage;
        if (tag != null && !tag.isBlank()) {
            postsPage = postService.getPublishedPostsByTagPaged(tag, page, size);
            model.addAttribute("activeTag", tag);
        } else {
            postsPage = postService.getAllPublishedPostsPaged(page, size);
        }
        model.addAttribute("postsPage", postsPage);
        model.addAttribute("allTags", postService.getAllUsedTags());
        model.addAttribute("allSeries", seriesService.getAllSeriesAcrossUsers());
        return "public/index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/panorama/{username}")
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

    @GetMapping("/panorama/{username}/about")
    public String about(@PathVariable String username, Model model) {
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + username));
        model.addAttribute("author", author);
        model.addAttribute("renderedBody", author.getAboutMd() != null
                ? markdownService.render(author.getAboutMd()) : "");
        return "public/about";
    }

    @GetMapping("/panorama/{username}/tag/{tagSlug}")
    public String tag(@PathVariable String username, @PathVariable String tagSlug, Model model) {
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + username));
        model.addAttribute("author", author);
        model.addAttribute("tagSlug", tagSlug);
        model.addAttribute("posts", postService.getPublishedPostsByTag(author, tagSlug));
        return "public/tag";
    }

    @GetMapping("/panorama/{username}/series/{seriesSlug}")
    public String series(@PathVariable String username, @PathVariable String seriesSlug, Model model) {
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + username));
        Series series = seriesService.getSeries(author, seriesSlug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Series not found: " + seriesSlug));
        model.addAttribute("author", author);
        model.addAttribute("series", series);
        return "public/series";
    }

    @GetMapping("/panorama/{username}/{slug}")
    public String post(@PathVariable String username, @PathVariable String slug, Model model) {
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + username));
        Post post = postService.getPublishedPost(author, slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found: " + slug));
        model.addAttribute("author", author);
        model.addAttribute("post", post);
        model.addAttribute("renderedBody", markdownService.render(post.getBodyMd()));
        model.addAttribute("prevPost", postService.getPreviousPost(post).orElse(null));
        model.addAttribute("nextPost", postService.getNextPost(post).orElse(null));
        return "public/post";
    }
}
