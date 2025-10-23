package com.vehicle.spareparts.repository;

import com.vehicle.spareparts.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    
    List<Feedback> findByCustomerId(Long customerId);
    
    List<Feedback> findByStatus(String status);
    
    List<Feedback> findByFeedbackType(String feedbackType);
    
    @Query("SELECT f FROM Feedback f WHERE f.sparePart.id = :partId")
    List<Feedback> findFeedbackByPart(@Param("partId") Long partId);
    
    @Query("SELECT AVG(f.rating) FROM Feedback f WHERE f.sparePart.id = :partId AND f.rating IS NOT NULL")
    Double getAverageRatingByPart(@Param("partId") Long partId);
}
