package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreateRequestRequest;
import ru.practicum.shareit.request.dto.CreateRequestResponse;
import ru.practicum.shareit.request.dto.GetRequestResponse;

import java.util.List;

import static ru.practicum.shareit.util.Constants.USER_ID_HEADER;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class RequestController {
    private final RequestService requestService;

    @PostMapping
    public CreateRequestResponse save(@RequestBody CreateRequestRequest createRequestRequest,
                                      @RequestHeader(USER_ID_HEADER) Long requestorId) {
        return requestService.save(createRequestRequest, requestorId);
    }

    @GetMapping
    public List<GetRequestResponse> findByRequestorId(@RequestHeader(USER_ID_HEADER) Long requestorId) {
        return requestService.findByRequestorId(requestorId);
    }

    @GetMapping("/all")
    public List<GetRequestResponse> findRequestsForAnotherRequestors(@RequestHeader(USER_ID_HEADER) Long requestorId,
                                                                     @RequestParam(defaultValue = "0") long from,
                                                                     @RequestParam(defaultValue = "10") int size) {
        return requestService.findRequestsForAnotherRequestors(requestorId, from, size);
    }

    @GetMapping("/{requestId}")
    public GetRequestResponse findById(@RequestHeader(USER_ID_HEADER) Long userId,
                                       @PathVariable Long requestId) {
        return requestService.findDtoById(requestId, userId);
    }
}