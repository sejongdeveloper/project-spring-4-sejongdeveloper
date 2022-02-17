package com.codesoom.sejongdeveloper.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class PlaceOrderUpdateRequest {

    private Long id;    //발주 일련번호

    private String name;    //발주명

    private LocalDate date; //발주일

    List<PlaceOrderDetailUpdateRequest> placeOrderDetails;  //발주상세 목록

    @Builder
    public PlaceOrderUpdateRequest(Long id,
                                   String name,
                                   LocalDate date,
                                   List<PlaceOrderDetailUpdateRequest> placeOrderDetails) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.placeOrderDetails = placeOrderDetails;
    }
}
