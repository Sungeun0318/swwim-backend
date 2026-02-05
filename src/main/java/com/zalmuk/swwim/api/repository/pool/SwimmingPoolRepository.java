package com.zalmuk.swwim.api.repository.pool;

import com.zalmuk.swwim.api.entity.pool.SwimmingPool;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface SwimmingPoolRepository extends JpaRepository<SwimmingPool, Integer> {

    Optional<SwimmingPool> findByPlaceId(String placeId);

    boolean existsByPlaceId(String placeId);

    @Query("SELECT p FROM SwimmingPool p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<SwimmingPool> searchByName(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM SwimmingPool p WHERE " +
            "p.latitude BETWEEN :minLat AND :maxLat AND " +
            "p.longitude BETWEEN :minLng AND :maxLng")
    List<SwimmingPool> findByLocationBounds(
            @Param("minLat") BigDecimal minLat,
            @Param("maxLat") BigDecimal maxLat,
            @Param("minLng") BigDecimal minLng,
            @Param("maxLng") BigDecimal maxLng);

    @Query(value = "SELECT *, " +
            "(6371 * acos(cos(radians(:lat)) * cos(radians(latitude)) * " +
            "cos(radians(longitude) - radians(:lng)) + sin(radians(:lat)) * " +
            "sin(radians(latitude)))) AS distance " +
            "FROM swimming_pools " +
            "HAVING distance < :radius " +
            "ORDER BY distance", nativeQuery = true)
    List<SwimmingPool> findNearbyPools(
            @Param("lat") BigDecimal latitude,
            @Param("lng") BigDecimal longitude,
            @Param("radius") double radiusKm);

    List<SwimmingPool> findAllByOrderByDisplayOrderAsc();
}
