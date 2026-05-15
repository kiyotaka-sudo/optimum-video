package com.optimum.optimum.controller;

import com.optimum.optimum.model.*;
import com.optimum.optimum.repository.EpisodeRepository;
import com.optimum.optimum.repository.VideoTitleRepository;
import com.optimum.optimum.service.AdminUserService;
import com.optimum.optimum.service.PaymentSimulationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    private final AdminUserService adminUserService;
    private final Path uploadDir = Paths.get("uploads");

    public AdminController(VideoTitleRepository videoTitleRepository,
                           EpisodeRepository episodeRepository,
                           AdminUserService adminUserService) {
        this.videoTitleRepository = videoTitleRepository;
        this.episodeRepository = episodeRepository;
        this.adminUserService = adminUserService;
        try {
            Files.createDirectories(uploadDir.resolve("images"));
            Files.createDirectories(uploadDir.resolve("videos"));
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload folders", e);
        }
    }

    // ─────────────────────────────────────────────────────────────
    // FICHIERS
    // ─────────────────────────────────────────────────────────────

    private String saveFile(MultipartFile file, String subFolder) throws IOException {
        if (file == null || file.isEmpty()) return null;
        String filename = UUID.randomUUID() + "_" +
                file.getOriginalFilename().replaceAll("[^a-zA-Z0-9.-]", "_");
        Path targetPath = uploadDir.resolve(subFolder).resolve(filename);
        file.transferTo(targetPath);
        return "/uploads/" + subFolder + "/" + filename;
    }

    // ─────────────────────────────────────────────────────────────
    // TITRES
    // ─────────────────────────────────────────────────────────────

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
                            @RequestParam(value = "video", required = false) MultipartFile video,
                            RedirectAttributes ra) throws IOException {

        ContentType type = ContentType.valueOf(typeStr);
        String posterUrl = saveFile(poster, "images");
        String backdropUrl = saveFile(backdrop, "images");
        String videoUrl = saveFile(video, "videos");

        VideoTitle videoTitle = new VideoTitle(type, title, releaseYear, 120,
                posterUrl, backdropUrl, 0.0, MaturityRating.PG13, synopsis);
        videoTitle.setAfricanContent(isAfricanContent);
        videoTitle.setVideoUrl(videoUrl);
        videoTitleRepository.save(videoTitle);

        ra.addFlashAttribute("successMsg", "Titre \"" + title + "\" ajouté avec succès !");
        return "redirect:/admin?success";
    }

    @PostMapping("/titles/{id}/delete")
    public String deleteTitle(@PathVariable UUID id, RedirectAttributes ra) {
        videoTitleRepository.deleteById(id);
        ra.addFlashAttribute("successMsg", "Titre supprimé.");
        return "redirect:/admin?success#catalog";
    }

    // ─────────────────────────────────────────────────────────────
    // ÉPISODES
    // ─────────────────────────────────────────────────────────────

    @GetMapping("/titles/{id}/episodes")
    public String listEpisodes(@PathVariable("id") UUID titleId, Model model) {
        VideoTitle series = videoTitleRepository.findById(titleId)
                .orElseThrow(() -> new IllegalArgumentException("Série introuvable : " + titleId));
        model.addAttribute("series", series);
        model.addAttribute("episodes",
                episodeRepository.findBySeriesIdOrderBySeasonNumberAscEpisodeNumberAsc(titleId));
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
                              @RequestParam(value = "video", required = false) MultipartFile video,
                              RedirectAttributes ra) throws IOException {

        VideoTitle series = videoTitleRepository.findById(titleId)
                .orElseThrow(() -> new IllegalArgumentException("Série introuvable : " + titleId));
        String thumbnailUrl = saveFile(thumbnail, "images");
        String videoUrl = saveFile(video, "videos");
        Episode episode = new Episode(series, seasonNumber, episodeNumber, title,
                synopsis, durationMinutes, thumbnailUrl);
        episode.setVideoUrl(videoUrl);
        episodeRepository.save(episode);

        ra.addFlashAttribute("successMsg", "Épisode ajouté.");
        return "redirect:/admin/titles/" + titleId + "/episodes?success";
    }

    // ─────────────────────────────────────────────────────────────
    // UTILISATEURS — CRUD COMPLET
    // ─────────────────────────────────────────────────────────────

    /** Créer un utilisateur (depuis la modal admin) */
    @PostMapping("/users/new")
    public String createUser(@RequestParam("email") String email,
                             @RequestParam("password") String password,
                             @RequestParam("firstName") String firstName,
                             @RequestParam("lastName") String lastName,
                             @RequestParam("role") String roleStr,
                             RedirectAttributes ra) {
        try {
            UserRole role = UserRole.valueOf(roleStr);
            adminUserService.createUser(email, password, firstName, lastName, role);
            ra.addFlashAttribute("successMsg", "Utilisateur " + firstName + " " + lastName + " créé avec succès. Email de bienvenue envoyé.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Erreur : " + e.getMessage());
        }
        return "redirect:/admin#users";
    }

    /** Modifier un utilisateur */
    @PostMapping("/users/{id}/edit")
    public String updateUser(@PathVariable UUID id,
                             @RequestParam("firstName") String firstName,
                             @RequestParam("lastName") String lastName,
                             @RequestParam("role") String roleStr,
                             RedirectAttributes ra) {
        try {
            UserRole role = UserRole.valueOf(roleStr);
            adminUserService.updateUser(id, firstName, lastName, role);
            ra.addFlashAttribute("successMsg", "Utilisateur mis à jour.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Erreur : " + e.getMessage());
        }
        return "redirect:/admin#users";
    }

    /** Suspendre / réactiver un compte */
    @PostMapping("/users/{id}/toggle-active")
    public String toggleActive(@PathVariable UUID id, RedirectAttributes ra) {
        try {
            boolean nowActive = adminUserService.toggleActive(id);
            ra.addFlashAttribute("successMsg",
                    nowActive ? "Compte réactivé." : "Compte suspendu.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Erreur : " + e.getMessage());
        }
        return "redirect:/admin#users";
    }

    /** Supprimer un utilisateur */
    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable UUID id, RedirectAttributes ra) {
        try {
            adminUserService.deleteUser(id);
            ra.addFlashAttribute("successMsg", "Utilisateur supprimé définitivement.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Erreur : " + e.getMessage());
        }
        return "redirect:/admin#users";
    }

    /** Attribuer un abonnement + simulation de paiement */
    @PostMapping("/users/{id}/subscribe")
    public String assignSubscription(@PathVariable UUID id,
                                     @RequestParam("planId") UUID planId,
                                     @RequestParam("billingCycle") String billingCycleStr,
                                     @RequestParam(value = "cardLastFour", defaultValue = "9999") String cardLastFour,
                                     @RequestParam(value = "paymentMethod", defaultValue = "CARD") String paymentMethod,
                                     RedirectAttributes ra) {
        try {
            BillingCycle billingCycle = BillingCycle.valueOf(billingCycleStr);
            PaymentSimulationService.PaymentResult result =
                    adminUserService.assignSubscription(id, planId, billingCycle, cardLastFour, paymentMethod);

            if (result.status() == PaymentSimulationService.PaymentStatus.SUCCESS) {
                ra.addFlashAttribute("successMsg",
                        "✅ Paiement accepté ! " + result.message() +
                        " — Transaction : " + result.transactionId());
            } else {
                ra.addFlashAttribute("errorMsg",
                        "❌ Paiement refusé — " + result.message());
            }
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Erreur : " + e.getMessage());
        }
        return "redirect:/admin#users";
    }

    /** Réinitialiser le mot de passe */
    @PostMapping("/users/{id}/reset-password")
    public String resetPassword(@PathVariable UUID id,
                                @RequestParam("newPassword") String newPassword,
                                RedirectAttributes ra) {
        try {
            adminUserService.resetPassword(id, newPassword);
            ra.addFlashAttribute("successMsg", "Mot de passe réinitialisé.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Erreur : " + e.getMessage());
        }
        return "redirect:/admin#users";
    }
}
