package com.codesoom.sejongdeveloper.controllers;

import com.codesoom.sejongdeveloper.application.ObtainOrderQueryService;
import com.codesoom.sejongdeveloper.application.ObtainOrderService;
import com.codesoom.sejongdeveloper.application.UserService;
import com.codesoom.sejongdeveloper.domain.Item;
import com.codesoom.sejongdeveloper.domain.ObtainOrder;
import com.codesoom.sejongdeveloper.dto.ObtainOrderDetailRequest;
import com.codesoom.sejongdeveloper.dto.ObtainOrderRequest;
import com.codesoom.sejongdeveloper.dto.ObtainOrderResponse;
import com.codesoom.sejongdeveloper.dto.ObtainOrderSearchCondition;
import com.codesoom.sejongdeveloper.errors.ObtainOrderNotFoundException;
import com.codesoom.sejongdeveloper.repository.ItemRepository;
import com.codesoom.sejongdeveloper.repository.ObtainOrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.Cookie;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ObtainOrderController.class)
class ObtainOrderControllerTest {

    private static final String OBTAIN_ORDER_NAME = "수주명 테스트";
    private static final Long OBTAIN_ORDER_ID = 1L;
    private static final Long ITEM_ID = 1L;
    private static final Long INVALID_ITEM_ID = 2L;
    private static final String UPDATE_OBTAIN_ORDER_NAME = "수정한 수주명 테스트";
    private static final Long INVALID_OBTAIN_ORDER_ID = 2L;
    private static final Long OBTAIN_ORDER_DETAIL_ID = 1L;
    private static final String TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjF9.ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaDk";
    private static final Cookie COOKIE = new Cookie("Authentication", TOKEN);

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ObtainOrderService obtainOrderService;

    @MockBean
    private ObtainOrderQueryService obtainOrderQueryService;

    @MockBean
    private ItemRepository itemRepository;

    @MockBean
    private ObtainOrderRepository obtainOrderRepository;

    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        ObtainOrderResponse obtainOrderResponse = ObtainOrderResponse.builder()
                .id(OBTAIN_ORDER_ID)
                .build();

        given(obtainOrderService.findObtainOrder(OBTAIN_ORDER_ID)).willReturn(obtainOrderResponse);

        given(obtainOrderService.findObtainOrder(INVALID_OBTAIN_ORDER_ID))
                .willThrow(new ObtainOrderNotFoundException(INVALID_OBTAIN_ORDER_ID));

        given(obtainOrderService.createObtainOrder(
                any(ObtainOrder.class),
                anyList())
        ).willReturn(OBTAIN_ORDER_ID);

        given(obtainOrderService.updateObtainOrder(
                eq(INVALID_OBTAIN_ORDER_ID),
                any(ObtainOrder.class),
                anyList(), anyList())
        ).willThrow(new ObtainOrderNotFoundException(INVALID_OBTAIN_ORDER_ID));

        Item item = Item.builder()
                .id(ITEM_ID)
                .build();

        given(itemRepository.findById(ITEM_ID)).willReturn(Optional.of(item));

        List<ObtainOrderResponse> list = new ArrayList<>();
        list.add(obtainOrderResponse);

        given(obtainOrderQueryService.findObtainOrders(any(ObtainOrderSearchCondition.class), any(Pageable.class)))
                .willReturn(new PageImpl<>(list, PageRequest.of(0, 10), 1L));

        ObtainOrder obtainOrder = ObtainOrder.builder()
                .id(OBTAIN_ORDER_ID)
                .name(OBTAIN_ORDER_NAME)
                .build();

        given(obtainOrderRepository.findById(OBTAIN_ORDER_ID)).willReturn(Optional.of(obtainOrder));
    }

    @DisplayName("유효한 파라미터에 대한 수주저장 요청은 수주를 저장한다.")
    @Test
    void createValidRequest() throws Exception {
        String json = objectMapper.writeValueAsString(getObtainOrderRequest(OBTAIN_ORDER_NAME));

        mockMvc.perform(post("/obtain-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .cookie(COOKIE))
                .andExpect(status().isCreated());

        verify(obtainOrderService).createObtainOrder(any(ObtainOrder.class), anyList());
    }

    @DisplayName("유효하지 않는 파라미터에 대한 수주저장 요청은 Bad Request로 응답한다.")
    @Test
    void createInValidRequest() throws Exception {
        String json = objectMapper.writeValueAsString(getInvalidName());

        mockMvc.perform(post("/obtain-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .cookie(COOKIE))
                .andExpect(status().isBadRequest());

        json = objectMapper.writeValueAsString(getIsNullObtainOrderDetails());

        mockMvc.perform(post("/obtain-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .cookie(COOKIE))
                .andExpect(status().isBadRequest());

        json = objectMapper.writeValueAsString(getInvalidItem());

        mockMvc.perform(post("/obtain-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .cookie(COOKIE))
                .andExpect(status().isBadRequest());

    }

    @DisplayName("존재하지 않는 아이디의 품목에 대한 수주저장 요청은 Bad Request로 응답한다.")
    @Test
    void createWrongItem() throws Exception {
        String json = objectMapper.writeValueAsString(getObtainOrderWithInvalidItemId());

        mockMvc.perform(post("/obtain-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .cookie(COOKIE))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("유효한 파라미터에 대한 수주수정에 대한 요청은 수주를 저장한다.")
    @Test
    void updateValidRequest() throws Exception {
        String json = objectMapper.writeValueAsString(getObtainOrderRequest(UPDATE_OBTAIN_ORDER_NAME));

        mockMvc.perform(patch("/obtain-orders/"+OBTAIN_ORDER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .cookie(COOKIE))
                .andExpect(status().isOk());

        verify(obtainOrderService)
                .updateObtainOrder(eq(OBTAIN_ORDER_ID), any(ObtainOrder.class), anyList(), anyList());
    }

    @DisplayName("존재하지 않는 아이디의 수주에 대한 수주수정 요청은 Bad Request로 응답한다.")
    @Test
    void updateInvalidObtainOrder() throws Exception {
        String json = objectMapper.writeValueAsString(getObtainOrderRequest(UPDATE_OBTAIN_ORDER_NAME));

        mockMvc.perform(patch("/obtain-orders/"+INVALID_OBTAIN_ORDER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .cookie(COOKIE))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("주어진 아이디의 수주를 리턴한다.")
    @Test
    void getObtainOrder() throws Exception {
        mockMvc.perform(get("/obtain-orders/" + OBTAIN_ORDER_ID)
                        .cookie(COOKIE))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"id\":" + OBTAIN_ORDER_ID)));

        verify(obtainOrderService).findObtainOrder(OBTAIN_ORDER_ID);
    }

    @DisplayName("존재하지 않는 아이디의 수주 대한 수주조회 요청은 Bad Request로 응답한다.")
    @Test
    void getObtainOrderInvalidId() throws Exception {
        mockMvc.perform(get("/obtain-orders/" + INVALID_OBTAIN_ORDER_ID)
                        .cookie(COOKIE))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("주어진 검색조건의 수주 목록을 리턴한다.")
    @Test
    void list() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);

        ObtainOrderSearchCondition condition = ObtainOrderSearchCondition.builder().build();

        Map<String, Object> map = new HashMap<>();
        map.put("condition", condition);
        map.put("pageable", pageable);

        String json = objectMapper.writeValueAsString(map);

        mockMvc.perform(get("/obtain-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .cookie(COOKIE))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"id\":" + OBTAIN_ORDER_DETAIL_ID)));
    }

    private ObtainOrderRequest getObtainOrderWithInvalidItemId() {
        ObtainOrderDetailRequest obtainOrderDetailRequest = ObtainOrderDetailRequest.builder()
                .itemId(INVALID_ITEM_ID)
                .quantity(1_000.0)
                .build();

        return ObtainOrderRequest.builder()
                .name(OBTAIN_ORDER_NAME)
                .obtainOrderDetails(List.of(obtainOrderDetailRequest))
                .build();
    }

    private ObtainOrderRequest getInvalidItem() {
        ObtainOrderDetailRequest obtainOrderDetailRequest = ObtainOrderDetailRequest.builder()
                .quantity(1_000.0)
                .build();

        return ObtainOrderRequest.builder()
                .name(OBTAIN_ORDER_NAME)
                .obtainOrderDetails(List.of(obtainOrderDetailRequest))
                .build();
    }

    private ObtainOrderRequest getIsNullObtainOrderDetails() {
        return ObtainOrderRequest.builder()
                .name(OBTAIN_ORDER_NAME)
                .build();
    }

    private ObtainOrderRequest getInvalidName() {
        return ObtainOrderRequest.builder()
                    .name("")
                    .build();
    }

    private ObtainOrderRequest getObtainOrderRequest(String name) {
        ObtainOrderDetailRequest obtainOrderDetailRequest = getObtainOrderDetailRequest();

        return ObtainOrderRequest.builder()
                .name(name)
                .obtainOrderDetails(List.of(obtainOrderDetailRequest))
                .build();
    }

    private ObtainOrderDetailRequest getObtainOrderDetailRequest() {
        return ObtainOrderDetailRequest.builder()
                .itemId(1L)
                .quantity(1_000.0)
                .build();
    }
}
