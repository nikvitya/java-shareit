package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingShort;
import ru.practicum.shareit.booking.entity.Status;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.exception.IncompatibleUserIdException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotBookedBeforeException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.entity.Comment;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.RequestService;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.util.OffsetBasedPageRequest;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.REQUIRED)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final CommentRepository commentRepository;
    private final RequestService requestService;

    @Override
    public ItemResponse saveItem(long ownerId, CreateItemRequest createItemRequest) {

        Item item = ItemMapper.toItem(userService.findById(ownerId), createItemRequest);

        Long requestId = createItemRequest.getRequestId();
        if (requestId != null) {
            item.setRequest(requestService.findById(requestId));
        }

        return ItemMapper.toItemResponse(itemRepository.save(item));
    }

    @Override
    public ItemResponse update(long ownerId, long itemId, UpdateItemRequest updateItemRequest) {

        Item item = ItemMapper.toItem(userService.findById(ownerId), updateItemRequest).setId(itemId);
        Item oldItem = findById(itemId);

        if (!Objects.equals(ownerId, oldItem.getOwner().getId()))
            throw new IncompatibleUserIdException("id пользователей не совпадают.");

        return ItemMapper.toItemResponse(
                itemRepository.save(oldItem.setName(item.getName() == null || item.getName().isBlank() ?
                                oldItem.getName() : item.getName())
                        .setDescription(item.getDescription() == null || item.getDescription().isBlank() ?
                                oldItem.getDescription() : item.getDescription())
                        .setAvailable(item.getAvailable() == null ? oldItem.getAvailable() : item.getAvailable())));
    }

    @Override
    @Transactional(readOnly = true)
    public Item findById(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(String.format("Предмет с id %s не найден.", itemId)));
    }

    @Override
    @Transactional(readOnly = true)
    public GetItemResponse findDtoById(long itemId, long ownerId) {
        Item item = findById(itemId);

        List<CommentResponse> comments = commentRepository.findByItem_IdOrderByCreatedAsc(itemId);
        if (!Objects.equals(item.getOwner().getId(), ownerId))
            return ItemMapper.toGetItemResponse(item, null, null, comments);

        BookingShort lastBooking = bookingRepository
                .findLastBooking(itemId, Status.APPROVED, LocalDateTime.now(), PageRequest.of(0, 1))
                .stream().map(BookingMapper::toBookingDtoShort).findFirst().orElse(null);
        BookingShort nextBooking = bookingRepository
                .findNextBooking(itemId, Status.APPROVED, LocalDateTime.now(), PageRequest.of(0, 1))
                .stream().map(BookingMapper::toBookingDtoShort).findFirst().orElse(null);
        return ItemMapper.toGetItemResponse(item, lastBooking, nextBooking, comments);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GetItemResponse> findByOwnerId(long ownerId, Long from, int size) {
        Pageable page = new OffsetBasedPageRequest(from, size);

        return itemRepository.findByOwnerId(ownerId, page)
                .stream().map(ItemMapper::toGetItemResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemResponse> searchAvailableItemsByText(String text, Long from, int size) {
        Pageable page = new OffsetBasedPageRequest(from, size);
        if (text.isBlank())
            return Collections.emptyList();
        return itemRepository.searchAvailableItemsByText(text, page).toList();
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