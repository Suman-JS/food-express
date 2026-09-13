package com.suman.foodexpress.restaurants.repository;

import com.suman.foodexpress.restaurants.entity.Restaurant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RestaurantsRepository extends JpaRepository<Restaurant, UUID> {
  @Query(
      """
      SELECT r
      FROM Restaurant r
      WHERE :search IS NULL
         OR :search = ''
         OR LOWER(r.name) LIKE LOWER(CONCAT('%', :search, '%'))
      """)
  Page<Restaurant> search(@Param("search") String search, Pageable pageable);

  Optional<Restaurant> findByOwner_Id(UUID ownerId);

  boolean existsByOwner_Id(UUID ownerId);
}
