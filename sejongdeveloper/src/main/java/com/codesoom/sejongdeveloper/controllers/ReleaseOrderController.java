package com.codesoom.sejongdeveloper.controllers;

import com.codesoom.sejongdeveloper.application.ReleaseOrderService;
import com.codesoom.sejongdeveloper.domain.ReleaseOrder;
import com.codesoom.sejongdeveloper.dto.ReleaseOrderSaveRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 출고에 대한 요청을 관리한다.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/release-orders")
public class ReleaseOrderController {

    private final ReleaseOrderService releaseOrderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long save(@RequestBody @Valid ReleaseOrderSaveRequest request) {
        return releaseOrderService.saveReleaseOrder(request);
    }

}