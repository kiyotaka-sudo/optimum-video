package com.optimum.optimum.controller;

import com.optimum.optimum.model.ContentType;
import com.optimum.optimum.model.Episode;
import com.optimum.optimum.model.MaturityRating;
import com.optimum.optimum.model.VideoTitle;
import com.optimum.optimum.repository.EpisodeRepository;
import com.optimum.optimum.repository.VideoTitleRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final VideoTitleRepository videoTitleRepository;
    private final EpisodeRepository episodeRepository;
    private final Path uploadDir = Paths.get("uploads");

    public AdminController(VideoTitleRepository videoTitleRepository, EpisodeRepository episodeRepository) {
        this.videoTitleRepository = videoTitleRepository;
        this.episodeRepository = episodeRepository;
        try {
            Files.createDirectories(uploadDir.resolve("images"));
            Files.createDirectories(uploadDir.resolve("videos"));
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload folders", e);
        }
    }

    private String saveFile(MultipartFile file, String subFolder) throws IOException {
        if (file == null || file.isEmpty()) return null;
        String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename().replaceAll("[^a-zA-Z0-9.-]", "_");
        Path targetPath = uploadDir.resolve(subFolder).resolve(filename);
        file.transferTo(targetPath);
        return "/uploads/" + subFolder + "/" + filename;
    }

    @GetMapping("/titles/new")
    public String newTitleForm() {
        return "admin-add-title";
    }

    @PostMapping("/titles/new")
    public String saveTitle(@RequestParam("title") String title,
                            @RequestParam("synopsis") String synopsis,
                            @RequestParam("releaseYear") int releaseYear,
                            @RequestParam("type") String typeStr,
                            @RequestParam(value = "isAfricanContent", required = false) boolean isAfricanContent,
                            @RequestParam(value = "poster", required = false) MultipartFile poster,
                            @RequestParam(value = "backdrop", required = false) MultipartFile backdrop,
                            @RequestParam(value = "video", required = false) MultipartFile video) throws IOException {

        ContentType type = ContentType.valueOf(typeStr);
        String posterUrl = saveFile(poster, "images");
        String backdropUrl = saveFile(backdrop, "images");
        String videoUrl = saveFile(video, "videos");

        VideoTitle videoTitle = new VideoTitle(type, title, releaseYear, 120, posterUrl, backdropUrl, 0.0, MaturityRating.PG13, synopsis);
        videoTitle.setAfricanContent(isAfricanContent);
        videoTitle.setVideoUrl(videoUrl);

        videoTitleRepository.save(videoTitle);
        return "redirect:/admin?success";
    }

    @GetMapping("/titles/{id}/episodes")
    public String listEpisodes(@PathVariable("id") UUID titleId, Model model) {
        VideoTitle series = videoTitleRepository.findById(titleId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid series Id:" + titleId));
        model.addAttribute("series", series);
        model.addAttribute("episodes", episodeRepository.findBySeriesIdOrderBySeasonNumberAscEpisodeNumberAsc(titleId));
        return "admin-episodes";
    }

    @PostMapping("/titles/{id}/episodes/new")
    public String saveEpisode(@PathVariable("id") UUID titleId,
                              @RequestParam("title") String title,
                              @RequestParam("synopsis") String synopsis,
                              @RequestParam("seasonNumber") int seasonNumber,
                              @RequestParam("episodeNumber") int episodeNumber,
                              @RequestParam("durationMinutes") int durationMinutes,
                              @RequestParam(value = "thumbnail", required = false) MultipartFile thumbnail,
                              @RequestParam(value = "video", required = false) MultipartFile video) throws IOException {

        VideoTitle series = videoTitleRepository.findById(titleId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid series Id:" + titleId));

        String thumbnailUrl = saveFile(thumbnail, "images");
        String videoUrl = saveFile(video, "videos");

        Episode episode = new Episode(series, seasonNumber, episodeNumber, title, synopsis, durationMinutes, thumbnailUrl);
        episode.setVideoUrl(videoUrl);

        episodeRepository.save(episode);
        return "redirect:/admin/titles/" + titleId + "/episodes?success";
    }
}
