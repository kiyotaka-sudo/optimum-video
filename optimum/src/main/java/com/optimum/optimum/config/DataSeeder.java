package com.optimum.optimum.config;

import com.optimum.optimum.model.ContentType;
import com.optimum.optimum.model.Episode;
import com.optimum.optimum.model.Genre;
import com.optimum.optimum.model.LiveChannel;
import com.optimum.optimum.model.MaturityRating;
import com.optimum.optimum.model.PlaybackProgress;
import com.optimum.optimum.model.SubscriptionPlan;
import com.optimum.optimum.model.UserAccount;
import com.optimum.optimum.model.UserProfile;
import com.optimum.optimum.model.UserRole;
import com.optimum.optimum.model.VideoTitle;
import com.optimum.optimum.repository.EpisodeRepository;
import com.optimum.optimum.repository.GenreRepository;
import com.optimum.optimum.repository.LiveChannelRepository;
import com.optimum.optimum.repository.PlaybackProgressRepository;
import com.optimum.optimum.repository.SubscriptionPlanRepository;
import com.optimum.optimum.repository.UserAccountRepository;
import com.optimum.optimum.repository.UserProfileRepository;
import com.optimum.optimum.repository.VideoTitleRepository;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataSeeder implements CommandLineRunner {
    private final GenreRepository genres;
    private final VideoTitleRepository titles;
    private final EpisodeRepository episodes;
    private final UserAccountRepository accounts;
    private final UserProfileRepository profiles;
    private final PlaybackProgressRepository progress;
    private final SubscriptionPlanRepository plans;
    private final PasswordEncoder passwordEncoder;
    private final LiveChannelRepository channels;

    public DataSeeder(GenreRepository genres, VideoTitleRepository titles, EpisodeRepository episodes,
                      UserAccountRepository accounts, UserProfileRepository profiles,
                      PlaybackProgressRepository progress, SubscriptionPlanRepository plans,
                      PasswordEncoder passwordEncoder, LiveChannelRepository channels) {
        this.genres = genres;
        this.titles = titles;
        this.episodes = episodes;
        this.accounts = accounts;
        this.profiles = profiles;
        this.progress = progress;
        this.plans = plans;
        this.passwordEncoder = passwordEncoder;
        this.channels = channels;
    }

    @Override
    @Transactional
    public void run(String... args) {
        UserAccount admin = accounts.findByEmail("mbargaernest80@gmail.com")
                .orElseGet(() -> accounts.save(new UserAccount(
                        "mbargaernest80@gmail.com",
                        "MBARGA ERNEST",
                        passwordEncoder.encode("Nash@2006"),
                        "MBARGA",
                        "ERNEST",
                        UserRole.ADMIN)));
        if (profiles.findByAccount(admin).isEmpty()) {
            profiles.save(new UserProfile(admin, "MBARGA ERNEST", false, MaturityRating.R, true));
        }

        if (titles.count() > 0) {
            return;
        }

        Genre action = genres.save(new Genre("action", "Action"));
        Genre drama = genres.save(new Genre("drama", "Drame"));
        Genre scifi = genres.save(new Genre("sci-fi", "Science-fiction"));
        Genre doc = genres.save(new Genre("documentary", "Documentaire"));
        Genre thriller = genres.save(new Genre("thriller", "Thriller"));
        Genre cameroon = genres.save(new Genre("cinema-camerounais", "Cinema camerounais"));
        Genre africanTv = genres.save(new Genre("chaines-africaines", "Chaines TV africaines"));
        Genre sorcery = genres.save(new Genre("sorcellerie", "Sorcellerie"));
        Genre necromancy = genres.save(new Genre("necromancie", "Necromancie"));
        Genre legends = genres.save(new Genre("mythes-africains", "Mythes africains"));

        VideoTitle hero = movie("Nuit Rouge", 2026, 128, 9.1,
                "https://images.unsplash.com/photo-1524985069026-dd778a71c7b4?auto=format&fit=crop&w=900&q=80",
                "https://images.unsplash.com/photo-1517602302552-471fe67acf66?auto=format&fit=crop&w=1800&q=80",
                "Une enquetrice camerounaise infiltre un reseau de diffusion pirate qui menace les premieres africaines.").markOriginal().markNew();
        hero.addGenre(action);
        hero.addGenre(thriller);
        hero.addGenre(cameroon);
        hero.addCast("Amina Diallo");
        hero.addCast("Noah Martins");
        titles.save(hero);

        VideoTitle series = movie("Terminal 9", 2025, null, 8.8,
                "https://images.unsplash.com/photo-1505686994434-e3cc5abf1330?auto=format&fit=crop&w=900&q=80",
                "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?auto=format&fit=crop&w=1800&q=80",
                "Une serie afro-futuriste ou chaque profil cache une memoire interdite du continent.").markOriginal().withSeriesInfo(2);
        series.addGenre(scifi);
        series.addGenre(drama);
        series.addGenre(legends);
        series.addCast("Sofia Benali");
        titles.save(series);

        List<VideoTitle> seedTitles = List.of(
                hero,
                series,
                movie("Code Minuit", 2024, 112, 8.3, "https://images.unsplash.com/photo-1485846234645-a62644f84728?auto=format&fit=crop&w=900&q=80", "https://images.unsplash.com/photo-1440404653325-ab127d49abc1?auto=format&fit=crop&w=1800&q=80", "Un cryptographe traque une cle capable de liberer tous les films d'un studio.").markNew(),
                movie("Les Archives du Vent", 2023, 96, 7.9, "https://images.unsplash.com/photo-1497032205916-ac775f0649ae?auto=format&fit=crop&w=900&q=80", "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=1800&q=80", "Un documentaire sensoriel sur les images perdues du cinema africain."),
                movie("Quartier Libre", 2022, 105, 8.0, "https://images.unsplash.com/photo-1460881680858-30d872d5b530?auto=format&fit=crop&w=900&q=80", "https://images.unsplash.com/photo-1519501025264-65ba15a82390?auto=format&fit=crop&w=1800&q=80", "Une bande d'amis transforme un toit d'immeuble en cinema clandestin."),
                movie("Signal Faible", 2026, 118, 8.6, "https://images.unsplash.com/photo-1518005020951-eccb494ad742?auto=format&fit=crop&w=900&q=80", "https://images.unsplash.com/photo-1493246507139-91e8fad9978e?auto=format&fit=crop&w=1800&q=80", "Un thriller technologique ou chaque recommandation devient une menace.").markNew(),
                movie("Studio 404", 2021, 91, 7.5, "https://images.unsplash.com/photo-1536440136628-849c177e76a1?auto=format&fit=crop&w=900&q=80", "https://images.unsplash.com/photo-1478720568477-152d9b164e26?auto=format&fit=crop&w=1800&q=80", "Comedie noire dans un studio ou rien ne doit sortir avant minuit."),
                movie("Plan Large", 2020, 84, 7.7, "https://images.unsplash.com/photo-1522202176988-66273c2fd55f?auto=format&fit=crop&w=900&q=80", "https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=1800&q=80", "Un making-of intime sur la creation d'une plateforme video independante."),
                movie("Le Pacte des Masques", 2026, 122, 8.9, "https://images.unsplash.com/photo-1540979388789-6cee28a1cdc9?auto=format&fit=crop&w=900&q=80", "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?auto=format&fit=crop&w=1800&q=80", "Entre sorcellerie, heritage familial et justice, un village affronte ses pactes secrets.").markNew().markOriginal(),
                movie("Necromancie a Douala", 2025, 108, 8.4, "https://images.unsplash.com/photo-1509347528160-9a9e33742cdb?auto=format&fit=crop&w=900&q=80", "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=1800&q=80", "Un thriller mystique ou les morts guident une enquete impossible."),
                movie("Afrique TV Live", 2026, null, 7.8, "https://images.unsplash.com/photo-1485846234645-a62644f84728?auto=format&fit=crop&w=900&q=80", "https://images.unsplash.com/photo-1516321318423-f06f85e504b3?auto=format&fit=crop&w=1800&q=80", "Acces centralise aux chaines de television africaines et magazines culturels.")
        );

        for (VideoTitle title : seedTitles) {
            if (title.getGenres().isEmpty()) {
                title.addGenre(title.getType() == ContentType.DOCUMENTARY ? doc : drama);
            }
            if (title.getTitle().contains("Pacte")) {
                title.addGenre(sorcery);
                title.addGenre(legends);
                title.addGenre(cameroon);
            }
            if (title.getTitle().contains("Necromancie")) {
                title.addGenre(necromancy);
                title.addGenre(thriller);
                title.addGenre(cameroon);
            }
            if (title.getTitle().contains("Afrique TV")) {
                title.addGenre(africanTv);
                title.addGenre(doc);
            }
            titles.save(title);
        }

        episodes.save(new Episode(series, 1, 1, "Depart differe", "Une panne revele un passager absent des registres.", 48, series.getBackdropUrl()));
        episodes.save(new Episode(series, 1, 2, "Zone tampon", "Le controle parental devient la seule cle d'acces.", 51, series.getBackdropUrl()));
        episodes.save(new Episode(series, 1, 3, "Hors piste", "La station diffuse un souvenir qui n'appartient a personne.", 53, series.getBackdropUrl()));

        UserAccount demo = accounts.save(new UserAccount("demo@optimum.video", "Demo Optimum", passwordEncoder.encode("Password@123"), "Demo", "Optimum", UserRole.USER));
        UserProfile adult = profiles.save(new UserProfile(demo, "Demo", false, MaturityRating.R, true));
        profiles.save(new UserProfile(demo, "Kids", true, MaturityRating.PG, false));
        progress.save(new PlaybackProgress(adult, series, 1850, 3120));
        progress.save(new PlaybackProgress(adult, hero, 2400, 7680));

        plans.save(new SubscriptionPlan("Essentiel", 6.99, 69.99, 1, 3, false)
                .addFeature("HD").addFeature("1 ecran").addFeature("Catalogue standard"));
        plans.save(new SubscriptionPlan("Premium", 12.99, 129.99, 4, 6, true)
                .addFeature("4K").addFeature("Telechargements offline").addFeature("Profils enfants"));
        plans.save(new SubscriptionPlan("Famille partagee", 19.99, 199.99, 8, 12, true)
                .addFeature("Abonnement partage").addFeature("Acces collectif").addFeature("Chaines TV africaines").addFeature("4K + mobile"));

        channels.save(new LiveChannel("CRTV", "Cameroun", "Generaliste", "https://dummyimage.com/240x120/e50914/ffffff&text=CRTV", "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8", false));
        channels.save(new LiveChannel("Canal 2 International", "Cameroun", "Culture", "https://dummyimage.com/240x120/111111/ffffff&text=Canal+2", "https://test-streams.mux.dev/test_001/stream.m3u8", true));
        channels.save(new LiveChannel("Africa News", "Afrique", "Information", "https://dummyimage.com/240x120/00a8e1/ffffff&text=Africa+News", "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8", false));
    }

    private VideoTitle movie(String title, int year, Integer duration, double rating, String poster, String backdrop, String synopsis) {
        ContentType type = title.equals("Les Archives du Vent") || title.equals("Plan Large") ? ContentType.DOCUMENTARY : ContentType.MOVIE;
        return new VideoTitle(type, title, year, duration, poster, backdrop, rating, MaturityRating.PG13, synopsis);
    }
}
