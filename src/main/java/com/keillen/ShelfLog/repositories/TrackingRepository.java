package com.keillen.ShelfLog.repositories;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.keillen.ShelfLog.entities.Format;
import com.keillen.ShelfLog.entities.Tracking;
import com.keillen.ShelfLog.entities.TrackingId;

/**
 * Database access for tracking rows
 * The ID type is TrackingId because the primary key is the (user, media) pair
 */

public interface TrackingRepository extends JpaRepository<Tracking, TrackingId>{
    
    //Pageable is what is request to be shown
    //page hold the items
    @Query("""
        SELECT t FROM Tracking t
        WHERE (t.id.userId = :userId)
            AND (:format IS NULL OR t.media.format = :format)
            AND (:status IS NULL OR t.status.name = :status)
        """)
        Page<Tracking> search(@Param("userId") int userId, @Param("status") String status, 
            @Param("format") Format format, Pageable pageable);

}
