package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;


public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long bookerId,
                                                                              LocalDateTime now, LocalDateTime now1);

    List<Booking> findByBookerIdAndEndIsBeforeOrderByStartDesc(Long bookerId, LocalDateTime now);

    List<Booking> findByBookerIdAndStartIsAfterOrderByStartDesc(Long bookerId, LocalDateTime now);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, Status status);

    List<Booking> findByItemOwnerIdOrderByStartDesc(Long ownerId);

    List<Booking> findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long ownerId,
                                                                                 LocalDateTime now, LocalDateTime now1);

    List<Booking> findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(Long ownerId, LocalDateTime now);

    List<Booking> findByItemOwnerIdAndStartIsAfterOrderByStartDesc(Long ownerId, LocalDateTime now);

    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, Status waiting);

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
