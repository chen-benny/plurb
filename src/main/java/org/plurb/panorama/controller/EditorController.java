package org.plurb.panorama.controller;

import org.plurb.panorama.model.Post;
import org.plurb.panorama.model.User;
import org.plurb.panorama.repository.UserRepository;
import org.plurb.panorama.service.PostService;
import org.plurb.panorama.service.SeriesService;
import org.plurb.panorama.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/panorama/editor")
public class EditorController {

    private final UserRepository userRepository;
    private final PostService postService;
    private final SeriesService seriesService;
    private final UserService userService;

    public EditorController(UserRepository userRepository,
                            PostService postService,
                            SeriesService seriesService,
                            UserService userService) {
        this.userRepository = userRepository;
        this.postService = postService;
        this.seriesService = seriesService;
        this.userService = userService;
    }

    private User resolveUser(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found: " +
                        userDetails.getUsername()));
    }

    @GetMapping
    public String dashBoard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User author = resolveUser(userDetails);
        model.addAttribute("posts", postService.getAllPosts(author));
        model.addAttribute("seriesList", seriesService.getAllSeries(author));
        return "editor/dashboard";
    }

    @GetMapping("/new")
    public String newPostForm() {
        return "editor/edit";
    }

    @PostMapping("/new")
    public String createPost(@AuthenticationPrincipal UserDetails userDetails,
                             @RequestParam String title,
                             @RequestParam String slug,
                             @RequestParam(required = false, defaultValue = "") String description,
                             @RequestParam(required = false, defaultValue = "") String coverImageUrl,
                             @RequestParam String bodyMd,
                             @RequestParam(required = false, defaultValue = "") String tags,
                             RedirectAttributes redirectAttributes) {
        User author = resolveUser(userDetails);
        List<String> tagList = Arrays.stream(tags.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
        try {
            postService.createPost(author, title, slug, description, coverImageUrl, bodyMd, tagList);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/panorama/editor/new";
        }
        return "redirect:/panorama/editor";
    }

    @GetMapping("/{id}")
    public String editPostForm(@PathVariable Long id,
                               @AuthenticationPrincipal UserDetails userDetails,
                               Model model) {
        User author = resolveUser(userDetails);
        Post post = postService.getPostById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found: " + id));
        if (!post.getAuthor().getId().equals(author.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        model.addAttribute("post", post);
        return "editor/edit";
    }

    @PostMapping("/{id}")
    public String updatePost(@PathVariable Long id,
                             @AuthenticationPrincipal UserDetails userDetails,
                             @RequestParam String title,
                             @RequestParam String slug,
                             @RequestParam(required = false, defaultValue = "") String description,
                             @RequestParam(required = false, defaultValue = "") String coverImageUrl,
                             @RequestParam String bodyMd,
                             @RequestParam(required = false, defaultValue = "") String tags,
                             RedirectAttributes redirectAttributes) {
        User author = resolveUser(userDetails);
        Post post = postService.getPostById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found: " + id));
        if (!post.getAuthor().getId().equals(author.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        List<String> tagList = Arrays.stream(tags.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
        try {
            postService.updatePost(post, title, slug, description, coverImageUrl, bodyMd, tagList);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/panorama/editor/" + id;
        }
        return "redirect:/panorama/editor";
    }

    @PostMapping("/{id}/publish")
    public String togglePublish(@PathVariable Long id,
                                @AuthenticationPrincipal UserDetails userDetails) {
        User author = resolveUser(userDetails);
        Post post = postService.getPostById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found: " + id));
        if (!post.getAuthor().getId().equals(author.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        postService.togglePublish(post);
        return "redirect:/panorama/editor";
    }

    @PostMapping("/{id}/delete")
    public String deletePost(@PathVariable Long id,
                             @AuthenticationPrincipal UserDetails userDetails) {
        User author = resolveUser(userDetails);
        Post post = postService.getPostById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found: " + id));
        if (!post.getAuthor().getId().equals(author.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        postService.deletePost(post);
        return "redirect:/panorama/editor";
    }

    @GetMapping("/about")
    public String aboutForm(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User author = resolveUser(userDetails);
        model.addAttribute("aboutMd", author.getAboutMd() != null ? author.getAboutMd() : "");
        return "editor/about";
    }

    @PostMapping("/about")
    public String updateAbout(@AuthenticationPrincipal UserDetails userDetails,
                              @RequestParam String aboutMd) {
        User author = resolveUser(userDetails);
        userService.updateAbout(author, aboutMd);
        return "redirect:/panorama/" + author.getUsername() + "/about";
    }

    @GetMapping("/password")
    public String passwordForm() {
        return "editor/password";
    }

    @PostMapping("/password")
    public String updatePassword(@AuthenticationPrincipal UserDetails userDetails,
                                 @RequestParam String oldPassword,
                                 @RequestParam String newPassword,
                                 RedirectAttributes redirectAttributes) {
        User author = resolveUser(userDetails);
        boolean success = userService.changePassword(author, oldPassword, newPassword);
        if (!success) {
            redirectAttributes.addFlashAttribute("error", "Current password is incorrect.");
            return "redirect:/panorama/editor/password";
        }
        redirectAttributes.addFlashAttribute("success", "Password changed successfully.");
        return "redirect:/panorama/editor/password";
    }
}
