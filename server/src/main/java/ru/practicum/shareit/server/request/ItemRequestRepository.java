package ru.practicum.shareit.server.request;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findByRequestorIdOrderByCreatedDesc(Long requestorId);

    @Query("SELECT r FROM ItemRequest r WHERE r.requestor.id <> :requestorId ORDER BY r.created DESC")
    List<ItemRequest> findByRequestorIdNotOrderByCreatedDesc(@Param("requestorId") Long requestorId, Pageable pageable);

    List<ItemRequest> findByRequestorIdNotOrderByCreatedDesc(Long requestorId);
}