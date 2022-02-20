package com.codesoom.sejongdeveloper.controllers;

import com.codesoom.sejongdeveloper.application.PlaceOrderService;
import com.codesoom.sejongdeveloper.domain.PlaceOrder;
import com.codesoom.sejongdeveloper.dto.PlaceOrderDetailUpdateRequest;
import com.codesoom.sejongdeveloper.dto.PlaceOrderSaveRequest;
import com.codesoom.sejongdeveloper.dto.PlaceOrderSearchCondition;
import com.codesoom.sejongdeveloper.dto.PlaceOrderUpdateRequest;
import com.codesoom.sejongdeveloper.repository.PlaceOrderRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PlaceOrderController.class)
@SuppressWarnings({"InnerClassMayBeStatic", "NonAsciiCharacters"})
@DisplayName("PlaceOrderController 클래스")
class PlaceOrderControllerTest {

    private static final String PLACE_ORDER_NAME = "testName";
    private static final Long VALID_PLACE_ORDER_ID = 1L;
    private static final Long INVALID_PLACE_ORDER_ID = 2L;
    private static final Long PLACE_ORDER_DETAIL_ID = 1L;
    private static final Long ITEM_ID = 1L;
    private static final LocalDate PLACE_ORDER_DATE = LocalDate.now();

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @MockBean
    private PlaceOrderService placeOrderService;

    @MockBean
    private PlaceOrderRepository placeOrderRepository;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        given(placeOrderService.savePlaceOrder(any(PlaceOrderSaveRequest.class))).willReturn(VALID_PLACE_ORDER_ID);

        PlaceOrder placeOrder = PlaceOrder.builder()
                .id(VALID_PLACE_ORDER_ID)
                .build();

        given(placeOrderRepository.findById(VALID_PLACE_ORDER_ID)).willReturn(Optional.of(placeOrder));
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 발주등록_요청을_처리하는_핸들러는 {
        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 주어진_파라미터가_유효성_검사를_통과한_경우 {
            private String json;

            @BeforeEach
            void setUp() throws JsonProcessingException {
                PlaceOrderSaveRequest request = new PlaceOrderSaveRequest();
                request.setName(PLACE_ORDER_NAME);

                json = objectMapper.writeValueAsString(request);
            }

            @Test
            @DisplayName("발주를_저장한다")
            void 발주를_저장한다() throws Exception {
                mockMvc.perform(post("/place-orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                        .andExpect(status().isOk())
                        .andExpect(content().string(containsString("" + VALID_PLACE_ORDER_ID)));
            }
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 발주조회_요청을_처리하는_핸들러는 {
        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 주어진_아이디의_발주를_찾은_경우 {
            @Test
            @DisplayName("발주를 리턴한다")
            void 발주를_리턴한다() throws Exception {
                mockMvc.perform(get("/place-orders/" + VALID_PLACE_ORDER_ID))
                        .andExpect(status().isOk())
                        .andExpect(content().string(containsString("\"id\":" + VALID_PLACE_ORDER_ID)));
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 주어진_아이디의_발주를_찾지_못한_경우 {
            @Test
            @DisplayName("Bad Request로 응답한다")
            void Bad_Request로_응답한다() throws Exception {
                mockMvc.perform(get("/place-orders/" + INVALID_PLACE_ORDER_ID))
                        .andExpect(status().isBadRequest());
            }
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 발주수정_요청을_처리하는_핸들러는 {
        private static final String UPDATE_PLACE_ORDER_NAME = "수정된 발주명";

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 주어진_파라미터가_유효성검사를_통과한_경우 {
            private String json;

            @BeforeEach
            void setUp() throws JsonProcessingException {
                PlaceOrderDetailUpdateRequest detailRequest = PlaceOrderDetailUpdateRequest.builder()
                        .id(PLACE_ORDER_DETAIL_ID)
                        .itemId(ITEM_ID)
                        .build();

                List<PlaceOrderDetailUpdateRequest> detailRequests = List.of(detailRequest);

                PlaceOrderUpdateRequest request = PlaceOrderUpdateRequest.builder()
                        .name(UPDATE_PLACE_ORDER_NAME)
                        .placeOrderDetails(detailRequests)
                        .build();

                json = objectMapper.writeValueAsString(request);
            }

            @Test
            @DisplayName("발주를 수정한다")
            void 발주를_수정한다() throws Exception {
                mockMvc.perform(patch("/place-orders/" + VALID_PLACE_ORDER_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                        .andExpect(status().isOk());
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 발주명이_없는_경우 {
            private String json;

            @BeforeEach
            void setUp() throws JsonProcessingException {
                PlaceOrderUpdateRequest request = PlaceOrderUpdateRequest.builder().build();

                json = objectMapper.writeValueAsString(request);
            }

            @Test
            @DisplayName("Bad Request로 응답한다")
            void Bad_Request로_응답한다() throws Exception {
                mockMvc.perform(patch("/place-orders/" + VALID_PLACE_ORDER_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                        .andExpect(status().isBadRequest());
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 상품의_아이디가_없는_경우 {
            private String json;

            @BeforeEach
            void setUp() throws JsonProcessingException {
                PlaceOrderDetailUpdateRequest detailRequest = PlaceOrderDetailUpdateRequest.builder()
                        .id(PLACE_ORDER_DETAIL_ID)
                        .build();

                List<PlaceOrderDetailUpdateRequest> detailRequests = List.of(detailRequest);

                PlaceOrderUpdateRequest request = PlaceOrderUpdateRequest.builder()
                        .name(UPDATE_PLACE_ORDER_NAME)
                        .placeOrderDetails(detailRequests)
                        .build();

                json = objectMapper.writeValueAsString(request);
            }

            @Test
            @DisplayName("Bad Request로 응답한다")
            void Bad_Request로_응답한다() throws Exception {
                mockMvc.perform(patch("/place-orders/" + VALID_PLACE_ORDER_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                        .andExpect(status().isBadRequest());
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 발주상세가_없는_경우 {
            private String json;

            @BeforeEach
            void setUp() throws JsonProcessingException {
                PlaceOrderUpdateRequest request = PlaceOrderUpdateRequest.builder()
                        .name(UPDATE_PLACE_ORDER_NAME)
                        .build();

                json = objectMapper.writeValueAsString(request);
            }

            @Test
            @DisplayName("Bad Request로 응답한다")
            void Bad_Request로_응답한다() throws Exception {
                mockMvc.perform(patch("/place-orders/" + VALID_PLACE_ORDER_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                        .andExpect(status().isBadRequest());
            }
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class 발주목록_조회를_요청하는_핸들러는 {
        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 검색조건을_만족하는_발주목록을_찾은_경우 {
            PlaceOrderSearchCondition nameCondition;
            PlaceOrderSearchCondition dateCondition;

            @BeforeEach
            void setUp() {
                Pageable pageable = PageRequest.of(0, 10);

                nameCondition = PlaceOrderSearchCondition.builder()
                        .name(PLACE_ORDER_NAME)
                        .pageable(pageable)
                        .build();

                dateCondition = PlaceOrderSearchCondition.builder()
                        .date(PLACE_ORDER_DATE)
                        .pageable(pageable)
                        .build();

            }

            @Test
            @DisplayName("발주목록을 리턴한다")
            void 발주목록을_리턴한다() throws Exception {
                String json = objectMapper.writeValueAsString(nameCondition);

                mockMvc.perform(get("/place-orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                        .andExpect(status().isOk())
                        .andExpect(content().string(containsString(PLACE_ORDER_NAME)));

                json = objectMapper.writeValueAsString(dateCondition);

                mockMvc.perform(get("/place-orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                        .andExpect(status().isOk())
                        .andExpect(content().string(containsString(PLACE_ORDER_DATE.toString())));
            }
        }
    }
}
