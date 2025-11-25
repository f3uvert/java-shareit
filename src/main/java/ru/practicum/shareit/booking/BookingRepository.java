package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Для получения бронирований пользователя
    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId, Pageable pageable);

    // Для получения бронирований владельца вещей
    List<Booking> findByItemOwnerIdOrderByStartDesc(Long ownerId, Pageable pageable);

    // Для проверки существующих бронирований на период
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END " +
            "FROM Booking b " +
            "WHERE b.item.id = :itemId " +
            "AND b.status = 'APPROVED' " +
            "AND (:start < b.end AND :end > b.start)")
    boolean existsApprovedBookingForItemInPeriod(@Param("itemId") Long itemId,
                                                 @Param("start") LocalDateTime start,
                                                 @Param("end") LocalDateTime end);

    // Для получения текущих бронирований
    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.start <= :now AND b.end >= :now " +
            "ORDER BY b.start DESC")
    List<Booking> findCurrentByBookerId(@Param("bookerId") Long bookerId,
                                        @Param("now") LocalDateTime now,
                                        Pageable pageable);

    // Для получения прошлых бронирований
    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime now, Pageable pageable);

    // Для получения будущих бронирований
    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime now, Pageable pageable);

    // Для получения бронирований по статусу
    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status, Pageable pageable);

    // Для владельца - текущие бронирования
    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND b.start <= :now AND b.end >= :now " +
            "ORDER BY b.start DESC")
    List<Booking> findCurrentByOwnerId(@Param("ownerId") Long ownerId,
                                       @Param("now") LocalDateTime now,
                                       Pageable pageable);

    // Для владельца - прошлые бронирования
    List<Booking> findByItemOwnerIdAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime now, Pageable pageable);

    // Для владельца - будущие бронирования
    List<Booking> findByItemOwnerIdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime now, Pageable pageable);

    // Для владельца - бронирования по статусу
    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status, Pageable pageable);

    // Для получения последнего бронирования вещи
    Optional<Booking> findFirstByItemIdAndEndBeforeAndStatusOrderByEndDesc(
            Long itemId, LocalDateTime now, BookingStatus status);

    // Для получения следующего бронирования вещи
    Optional<Booking> findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(
            Long itemId, LocalDateTime now, BookingStatus status);

    // Для проверки, брал ли пользователь вещь в аренду
    @Query("SELECT COUNT(b) > 0 FROM Booking b " +
            "WHERE b.booker.id = :userId " +
            "AND b.item.id = :itemId " +
            "AND b.status = 'APPROVED' " +
            "AND b.end < :now")
    boolean hasUserBookedItem(@Param("userId") Long userId,
                              @Param("itemId") Long itemId,
                              @Param("now") LocalDateTime now);
}