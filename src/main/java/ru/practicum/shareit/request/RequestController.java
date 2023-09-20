package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreateRequestRequest;
import ru.practicum.shareit.request.dto.CreateRequestResponse;
import ru.practicum.shareit.request.dto.GetRequestResponse;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

import static ru.practicum.shareit.util.Constants.USER_ID_HEADER;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class RequestController {
    private final RequestService requestService;

    @PostMapping
    public CreateRequestResponse save(@RequestBody @Valid CreateRequestRequest createRequestRequest,
                                      @RequestHeader(USER_ID_HEADER) Long requestorId) {
        return requestService.save(createRequestRequest, requestorId);
    }

    @GetMapping
    public List<GetRequestResponse> findByRequestorId(@RequestHeader(USER_ID_HEADER) Long requestorId) {
        return requestService.findByRequestorId(requestorId);
    }

    @GetMapping("/all")
    public List<GetRequestResponse> findRequestsForAnotherRequestors(@RequestHeader(USER_ID_HEADER) Long requestorId,
                                                                     @RequestParam(defaultValue = "0") @Min(0) @Max(Long.MAX_VALUE) long from,
                                                                     @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        return requestService.findRequestsForAnotherRequestors(requestorId, from, size);
    }

    @GetMapping("/{requestId}")
    public GetRequestResponse findById(@RequestHeader(USER_ID_HEADER) Long userId,
                                       @PathVariable Long requestId) {
        return requestService.findDtoById(requestId, userId);
    }
}