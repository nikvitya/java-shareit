package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.entity.Status;

import java.time.LocalDateTime;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findByBookerIdOrderByStartDesc(Long bookerId, Pageable page);

    Page<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStart(Long bookerId,
                                                                          LocalDateTime nowForStart, LocalDateTime nowForEnd, Pageable page);

    Page<Booking> findByBookerIdAndEndIsBeforeOrderByStartDesc(Long bookerId, LocalDateTime now, Pageable page);

    Page<Booking> findByBookerIdAndStartIsAfterOrderByStartDesc(Long bookerId, LocalDateTime now, Pageable page);

    Page<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, Status status, Pageable page);

    Page<Booking> findByItemOwnerId(Long ownerId, Pageable page);

    Page<Booking> findByItemOwnerIdAndStartIsBeforeAndEndIsAfter(Long ownerId,
                                                                 LocalDateTime now, LocalDateTime now1, Pageable page);

    Page<Booking> findByItemOwnerIdAndEndIsBefore(Long ownerId, LocalDateTime now, Pageable page);

    Page<Booking> findByItemOwnerIdAndStartIsAfter(Long ownerId, LocalDateTime now, Pageable page);

    Page<Booking> findByItemOwnerIdAndStatus(Long ownerId, Status waiting, Pageable page);

    @Query("select bo " +
            "from Booking as bo " +
            "join bo.item as it " +
            "where it.id = :itemId and bo.status = :status and bo.start < :now " +
            "order by bo.start desc")
    Page<Booking> findLastBooking(@Param("itemId") long itemId,
                                  @Param("status") Status status,
                                  @Param("now") LocalDateTime now, Pageable page);

    @Query("select bo " +
            "from Booking as bo " +
            "join bo.item as it " +
            "where it.id = :itemId and bo.status = :status and bo.start > :now " +
            "order by bo.start asc")
    Page<Booking> findNextBooking(@Param("itemId") long itemId,
                                  @Param("status") Status status,
                                  @Param("now") LocalDateTime now, Pageable page);

    Booking findFirstByBooker_IdAndItem_IdAndStatusAndEndIsBefore(Long authorId, Long itemId, Status approved,
                                                                  LocalDateTime now);
}