package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.CreateRequestRequest;
import ru.practicum.shareit.request.dto.CreateRequestResponse;
import ru.practicum.shareit.request.dto.GetRequestResponse;
import ru.practicum.shareit.request.entity.Request;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.util.OffsetBasedPageRequest;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.util.Constants.SORT_BY_CREATED_DESC;

@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.REQUIRED)
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserService userService;

    @Override
    public CreateRequestResponse save(CreateRequestRequest createRequestRequest, Long requestorId) {
        Request request = new Request()
                .setDescription(createRequestRequest.getDescription())
                .setRequestor(userService.findById(requestorId));
        return RequestMapper.toCreateRequestResponse(requestRepository.save(request));
    }

    @Transactional(readOnly = true)
    @Override
    public Request findById(Long requestId) {
        return requestRepository.findById(requestId).orElseThrow(() -> new NotFoundException(
                String.format("Запрос с id %d не найден.", requestId)));
    }

    @Transactional(readOnly = true)
    @Override
    public GetRequestResponse findDtoById(Long requestId, Long userId) {
        userService.findById(userId);
        return RequestMapper.toGetRequestResponse(findById(requestId));
    }

    @Transactional(readOnly = true)
    @Override
    public List<GetRequestResponse> findByRequestorId(Long requestorId) {
        userService.findById(requestorId);
        return requestRepository.findByRequestorId(requestorId)
                .stream().map(RequestMapper::toGetRequestResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<GetRequestResponse> findRequestsForAnotherRequestors(Long requestorId, Long from, int size) {
        Pageable page = new OffsetBasedPageRequest(from, size, SORT_BY_CREATED_DESC);
        return requestRepository.findByRequestorIdNot(requestorId, page)
                .stream().map(RequestMapper::toGetRequestResponse).collect(Collectors.toList());
    }


}