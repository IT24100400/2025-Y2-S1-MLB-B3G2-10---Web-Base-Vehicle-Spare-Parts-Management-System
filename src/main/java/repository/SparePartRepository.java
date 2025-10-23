package com.vehicle.spareparts.repository;

import com.vehicle.spareparts.entity.SparePart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SparePartRepository extends JpaRepository<SparePart, Long> {
    
    Optional<SparePart> findByPartNumber(String partNumber);
    
    List<SparePart> findByCategory(String category);
    
    List<SparePart> findByIsActive(Boolean isActive);
    
    @Query("SELECT s FROM SparePart s WHERE s.stockQuantity <= s.reorderLevel AND s.isActive = true")
    List<SparePart> findLowStockParts();
    
    @Query("SELECT s FROM SparePart s WHERE " +
           "(LOWER(s.partName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.partNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.category) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.brand) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND s.isActive = true")
    List<SparePart> searchParts(@Param("keyword") String keyword);
    
    @Query("SELECT DISTINCT s.category FROM SparePart s WHERE s.isActive = true ORDER BY s.category")
    List<String> findAllCategories();
}
