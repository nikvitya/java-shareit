package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDtoForGetItemResponse;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.IncompatibleUserIdException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotBookedBeforeException;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.dto.CreateCommentRequest;
import ru.practicum.shareit.item.dto.GetItemResponse;
import ru.practicum.shareit.item.dto.SearchItemResponse;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional(propagation = Propagation.REQUIRED)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final CommentRepository commentRepository;

    @Override
    public Item saveItem(Item item) {
        return itemRepository.save(item);
    }

    @Override
    public Item update(Item item) {
        Item oldItem = findById(item.getId());
        if (!Objects.equals(item.getOwner().getId(), oldItem.getOwner().getId()))
            throw new IncompatibleUserIdException("id пользователей не совпадают.");

        return itemRepository.save(oldItem
                .setName(item.getName() == null ? oldItem.getName() : item.getName())
                .setDescription(item.getDescription() == null ? oldItem.getDescription() : item.getDescription())
                .setAvailable(item.getAvailable() == null ? oldItem.getAvailable() : item.getAvailable()));
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Item findById(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(String.format("Предмет с id %s не найден.", itemId)));
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public GetItemResponse findDtoById(long itemId, long ownerId) {
        Item item = findById(itemId);
        List<CommentResponse> comments = commentRepository.findByItem_IdOrderByCreatedAsc(itemId);
        if (!Objects.equals(item.getOwner().getId(), ownerId))
            return ItemMapper.toGetItemResponse(item, null, null, comments);

        BookingDtoForGetItemResponse lastBooking = bookingRepository
                .findLastBooking(itemId, Status.APPROVED, LocalDateTime.now(), PageRequest.of(0, 1))
                .stream().map(BookingMapper::toBookingDtoForGetItemResponse).findFirst().orElse(null);
        BookingDtoForGetItemResponse nextBooking = bookingRepository
                .findNextBooking(itemId, Status.APPROVED, LocalDateTime.now(), PageRequest.of(0, 1))
                .stream().map(BookingMapper::toBookingDtoForGetItemResponse).findFirst().orElse(null);
        return ItemMapper.toGetItemResponse(item, lastBooking, nextBooking, comments);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public List<GetItemResponse> findByOwnerId(long userId) {
        List<Item> items = itemRepository.findByOwnerId(userId);
        return items.stream().map(ItemMapper::toGetItemResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public List<SearchItemResponse> searchAvailableItemsByText(String text) {
        if (text.isBlank())
            return new ArrayList<>();
        return itemRepository.searchAvailableItemsByText(text);
    }

    @Override
    public CommentResponse saveComment(Long authorId, Long itemId, CreateCommentRequest createCommentRequest) {
        if (bookingRepository.findFirstByBooker_IdAndItem_IdAndStatusAndEndIsBefore(authorId, itemId, Status.APPROVED,
                LocalDateTime.now()) == null)
            throw new UserNotBookedBeforeException("Пользователь не может оставить отзыв, " +
                    "поскольку не пользовался вещью в прошлом.");
        return CommentMapper.toCommentResponse(commentRepository.save(new Comment()
                .setText(createCommentRequest.getText())
                .setItem(findById(itemId))
                .setAuthor(userService.findById(authorId))));
    }
}