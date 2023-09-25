package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.util.Constants.USER_ID_HEADER;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class RequestController {
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> save(@RequestBody @Valid CreateRequestRequest createRequestRequest,
                                       @RequestHeader(USER_ID_HEADER) Long requestorId) {
        return requestClient.save(createRequestRequest, requestorId);
    }

    @GetMapping
    public ResponseEntity<Object> findByRequestorId(@RequestHeader(USER_ID_HEADER) Long requestorId) {
        return requestClient.findByRequestorId(requestorId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findRequestsForAnotherRequestors(@RequestHeader(USER_ID_HEADER) Long requestorId,
                                                                   @RequestParam(defaultValue = "0") @PositiveOrZero long from,
                                                                   @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        return requestClient.findRequestsForAnotherRequestors(requestorId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findById(@RequestHeader(USER_ID_HEADER) Long userId,
                                           @PathVariable Long requestId) {
        return requestClient.findDtoById(requestId, userId);
    }
}