package com.roma42.eventifyBack.repositories;

import com.roma42.eventifyBack.models.Event;
import com.roma42.eventifyBack.models.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long> {
    Slice<Event> findSliceBy(Pageable pageable);
    Slice<Event> findSliceByPlaceContainsIgnoreCase(String place, Pageable pageable);
    Slice<Event> findSliceByTitleContainsIgnoreCase(String title, Pageable pageable);
    Slice<Event> findSliceByEventDateAfter(LocalDateTime from, Pageable pageable);
    Slice<Event> findSliceByEventDateBefore(LocalDateTime to, Pageable pageable);
    Slice<Event> findSliceByEventDateBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);
    Slice<Event> findSliceByUser(User user, Pageable pageable);
    @Query(value = "SELECT e.* FROM javaDB.event AS e " +
            "INNER JOIN " +
            "(SELECT * FROM javaDB.event_categories " +
            "LEFT JOIN javaDB.category " +
            "USING(category_id)) AS c " +
            "ON e.event_id=c.event_id " +
            "WHERE c.category_name IN :cat_set " +
            "GROUP BY e.event_id " +
            "HAVING COUNT(c.event_id) = :num " +
            "ORDER BY e.event_date ASC", nativeQuery = true)
    Slice<Event> findByCategoriesInSet(@Param("cat_set") Set<String> categories, @Param("num") Long num);
    Slice<Event> findSliceByOrderByEventDateAsc(Pageable pageable);

    void deleteByEventDateBefore(LocalDateTime now);

}
