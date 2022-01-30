package com.codesoom.sejongdeveloper.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ObtainOrderSearchCondition {
    private String name;    //수주명

    private LocalDate startDate;    //시작수주날짜

    private LocalDate endDate;  //마지막수주날짜

    private Pageable pageable;  //페이징
}