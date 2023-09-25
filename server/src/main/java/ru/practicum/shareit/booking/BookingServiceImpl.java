package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.entity.Status;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.util.OffsetBasedPageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.shareit.util.Constants.SORT_BY_START_DESC;

@Service
@Transactional(propagation = Propagation.REQUIRED)
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    public BookingResponse save(Long bookerId, CreateBookingRequest createBookingRequest) {

        Item item = itemService.findById(createBookingRequest.getItemId());
        Booking booking = new Booking()
                .setStart(createBookingRequest.getStart())
                .setEnd(createBookingRequest.getEnd())
                .setStatus(Status.WAITING)
                .setBooker(userService.findById(bookerId))
                .setItem(item);

        if (Objects.equals(item.getOwner().getId(), bookerId))
            throw new WrongUserIdException("Создание брони не доступно для владельца предмета.");
        if (!item.getAvailable())
            throw new ItemNotAvailableException("Предмет не доступен для бронирования.");
        if (booking.getStart().isAfter(booking.getEnd()) || booking.getStart().isEqual(booking.getEnd()))
            throw new StartNotBeforeEndException("Время начала использования вещи должно быть строго раньше " +
                    "времени окончания.");

        return BookingMapper.toBookingResponse(bookingRepository.save(booking));
    }

    @Override
    public BookingResponse update(Long bookingId, Long ownerId, boolean approved) {
        Booking oldBooking = findById(bookingId);
        if (!Objects.equals(oldBooking.getItem().getOwner().getId(), ownerId))
            throw new WrongUserIdException("Обновление статуса брони доступно только для владельцев предметов.");
        if (!Objects.equals(oldBooking.getStatus(), Status.WAITING))
            throw new StatusAlreadyChangedException(String.format("Статус был изменён владельцем предмета ранее на %s",
                    oldBooking.getStatus()));

        return BookingMapper.toBookingResponse(bookingRepository.save(oldBooking.setStatus(approved ? Status.APPROVED : Status.REJECTED)));
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse findByIdAndUserId(Long bookingId, Long ownerOrBookerId) {
        Booking booking = findById(bookingId);

        if (!Objects.equals(booking.getItem().getOwner().getId(), ownerOrBookerId)
                && !Objects.equals(booking.getBooker().getId(), ownerOrBookerId))
            throw new WrongUserIdException(
                    String.format("Пользователь с id %d не является владельцем " +
                            "предмета или бронирующим, поэтому информация о брони недоступна.", ownerOrBookerId));

        return BookingMapper.toBookingResponse(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> findByBookerIdAndState(Long bookerId, String state, Long from, int size) {
        Pageable page = new OffsetBasedPageRequest(from, size);
        userService.findById(bookerId);
        Page<Booking> bookingPage;

        switch (state.toUpperCase()) {
            case "ALL":
                bookingPage = bookingRepository.findByBookerIdOrderByStartDesc(bookerId, page);
                break;
            case "CURRENT":
                bookingPage = bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStart(bookerId,
                        LocalDateTime.now(), LocalDateTime.now(), page);
                break;
            case "PAST":
                bookingPage = bookingRepository.findByBookerIdAndEndIsBeforeOrderByStartDesc(bookerId, LocalDateTime.now(), page);
                break;
            case "FUTURE":
                bookingPage = bookingRepository.findByBookerIdAndStartIsAfterOrderByStartDesc(bookerId, LocalDateTime.now(), page);
                break;
            case "WAITING":
                bookingPage = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(bookerId, Status.WAITING, page);
                break;
            case "REJECTED":
                bookingPage = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(bookerId, Status.REJECTED, page);
                break;
            default:
                throw new UnknownStateException(String.format("Передано неподдерживаемое состояние бронирования %s",
                        state));
        }

        return bookingPage.stream().map(BookingMapper::toBookingResponse).collect(Collectors.toList());


    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> findByItemOwnerIdAndState(Long ownerId, String state, Long from, int size) {
        Pageable page = new OffsetBasedPageRequest(from, size, SORT_BY_START_DESC);

        userService.findById(ownerId);
        Page<Booking> bookingPage;


        switch (state.toUpperCase()) {
            case "ALL":
                bookingPage = bookingRepository.findByItemOwnerId(ownerId, page);
                break;
            case "CURRENT":
                bookingPage = bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfter(ownerId,
                        LocalDateTime.now(), LocalDateTime.now(), page);
                break;
            case "PAST":
                bookingPage = bookingRepository.findByItemOwnerIdAndEndIsBefore(ownerId, LocalDateTime.now(),
                        page);
                break;
            case "FUTURE":
                bookingPage = bookingRepository.findByItemOwnerIdAndStartIsAfter(ownerId, LocalDateTime.now(),
                        page);
                break;
            case "WAITING":
                bookingPage = bookingRepository.findByItemOwnerIdAndStatus(ownerId, Status.WAITING, page);
                break;
            case "REJECTED":
                bookingPage = bookingRepository.findByItemOwnerIdAndStatus(ownerId, Status.REJECTED, page);
                break;
            default:
                throw new UnknownStateException(String.format("Передано неподдерживаемое состояние бронирования %s",
                        state));
        }

        return bookingPage.stream().map(BookingMapper::toBookingResponse).collect(Collectors.toList());


    }

    public Booking findById(Long id) {
        return bookingRepository.findById(id).orElseThrow(
                () -> new BookingNotFoundException(String.format("Бронь с id %d не найдена.", id)));
    }
}