package com.keillen.animeTracker.repositories;
import java.util.Optional;
import com.keillen.animeTracker.entities.Format;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.keillen.animeTracker.entities.Media;

/**
 * Database access for catalog titles
 * findByRefIdAndSource lets the AniList import check whether a title is already saved before inserting it.
 * search find all media that matches or is close the matching the parameters set
 */

public interface MediaRepository extends JpaRepository<Media, Integer>{
    
    //Finds a title by its ID in the source API (e.g. AniList) plus the source name
    Optional<Media> findByRefIdAndSource(String refId, String source);

    //Search media using optional filters
    //Each condition applies if not null
    @Query("""
        SELECT m FROM Media m
        WHERE (:pattern IS NULL OR LOWER(m.title) LIKE :pattern)
            AND (:format IS NULL OR m.format = :format)
            AND (:genre IS NULL OR EXISTS (SELECT 1 FROM m.genres g WHERE g.name = :genre))
            AND (:tag IS NULL OR EXISTS (SELECT 1 FROM m.tags t WHERE t.name = :tag))
        """)
    Page<Media> search(@Param("pattern") String pattern, @Param("format") Format format,
                   @Param("genre") String genre, @Param("tag") String tag, Pageable pageable);
}
