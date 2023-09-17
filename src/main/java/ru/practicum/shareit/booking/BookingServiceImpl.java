package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.dto.GetBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@Transactional(propagation = Propagation.REQUIRED)
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    public GetBookingDto save(CreateBookingDto createBookingDto, Long bookerId) {
        Booking booking = new Booking()
                .setStart(createBookingDto.getStart())
                .setEnd(createBookingDto.getEnd())
                .setStatus(Status.WAITING)
                .setBooker(UserMapper.toUser(userService.findById(bookerId)))
                .setItem(itemService.findById(createBookingDto.getItemId()));

        if (Objects.equals(itemService.findById(booking.getItem().getId()).getOwner().getId(),
                booking.getBooker().getId()))
            throw new WrongUserIdException("Создание брони не доступно для владельца предмета.");
        if (!booking.getItem().getAvailable())
            throw new ItemNotAvailableException("Предмет не доступен для бронирования.");
        if (booking.getStart().isAfter(booking.getEnd()) || booking.getStart().isEqual(booking.getEnd()))
            throw new StartNotBeforeEndException("Время начала использования вещи должно быть строго раньше " +
                    "времени окончания.");

        return BookingMapper.toGetBookingDto(bookingRepository.save(booking));
    }

    @Override
    public GetBookingDto update(Long bookingId, Long ownerId, boolean approved) {
        Booking oldBooking = findById(bookingId);
        if (!Objects.equals(oldBooking.getItem().getOwner().getId(), ownerId))
            throw new WrongUserIdException("Обновление статуса брони доступно только для владельцев предметов.");
        if (!Objects.equals(oldBooking.getStatus(), Status.WAITING))
            throw new StatusAlreadyChangedException(String.format("Статус был изменён владельцем предмета ранее на %s",
                    oldBooking.getStatus()));

        return BookingMapper.toGetBookingDto
                (bookingRepository.save(oldBooking.setStatus(approved ? Status.APPROVED : Status.REJECTED)));
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public GetBookingDto findByIdAndOwnerOrBookerId(Long bookingId, Long ownerOrBookerId) {
        Booking booking = findById(bookingId);

        if (!Objects.equals(booking.getItem().getOwner().getId(), ownerOrBookerId)
                && !Objects.equals(booking.getBooker().getId(), ownerOrBookerId))
            throw new WrongUserIdException(
                    String.format("Пользователь с id %d не является владельцем " +
                    "предмета или бронирующим, поэтому информация о брони недоступна.", ownerOrBookerId));

        return BookingMapper.toGetBookingDto(booking);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public List<Booking> findByBookerIdAndState(Long bookerId, String state) {
        userService.findById(bookerId);

        switch (state) {
            case "ALL":
                return bookingRepository.findByBookerIdOrderByStartDesc(bookerId);
            case "CURRENT":
                return bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(bookerId,
                        LocalDateTime.now(), LocalDateTime.now());
            case "PAST":
                return bookingRepository.findByBookerIdAndEndIsBeforeOrderByStartDesc(bookerId, LocalDateTime.now());
            case "FUTURE":
                return bookingRepository.findByBookerIdAndStartIsAfterOrderByStartDesc(bookerId, LocalDateTime.now());
            case "WAITING":
                return bookingRepository.findByBookerIdAndStatusOrderByStartDesc(bookerId, Status.WAITING);
            case "REJECTED":
                return bookingRepository.findByBookerIdAndStatusOrderByStartDesc(bookerId, Status.REJECTED);
            default:
                throw new UnknownStateException(String.format("Передано неподдерживаемое состояние бронирования %s",
                        state));
        }
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public List<Booking> findByItemOwnerIdAndState(Long ownerId, String state) {
        userService.findById(ownerId);

        switch (state) {
            case "ALL":
                return bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId);
            case "CURRENT":
                return bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(ownerId,
                        LocalDateTime.now(), LocalDateTime.now());
            case "PAST":
                return bookingRepository.findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(ownerId, LocalDateTime.now());
            case "FUTURE":
                return bookingRepository.findByItemOwnerIdAndStartIsAfterOrderByStartDesc(ownerId, LocalDateTime.now());
            case "WAITING":
                return bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, Status.WAITING);
            case "REJECTED":
                return bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, Status.REJECTED);
            default:
                throw new UnknownStateException(String.format("Передано неподдерживаемое состояние бронирования %s",
                        state));
        }
    }

    private Booking findById(Long id) {
        return bookingRepository.findById(id).orElseThrow(
                () -> new BookingNotFoundException(String.format("Бронь с id %d не найдена.", id)));
    }
}