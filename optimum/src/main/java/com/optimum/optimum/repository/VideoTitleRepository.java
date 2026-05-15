package com.optimum.optimum.repository;

import com.optimum.optimum.model.ContentType;
import com.optimum.optimum.model.VideoTitle;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VideoTitleRepository extends JpaRepository<VideoTitle, UUID> {
    List<VideoTitle> findByType(ContentType type);

    @Query("""
            select distinct t from VideoTitle t
            left join t.genres g
            where (:query is null or lower(t.title) like lower(concat('%', :query, '%')) or lower(t.synopsis) like lower(concat('%', :query, '%')))
              and (:genre is null or g.slug = :genre)
            order by t.rating desc
            """)
    List<VideoTitle> search(@Param("query") String query, @Param("genre") String genre);

    List<VideoTitle> findTop8ByOrderByRatingDesc();

    List<VideoTitle> findTop8ByNewReleaseTrueOrderByReleaseYearDesc();

    List<VideoTitle> findTop8ByOptimumOriginalTrueOrderByRatingDesc();
}
