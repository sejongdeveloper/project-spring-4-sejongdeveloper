package com.codesoom.sejongdeveloper.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JwtUtil 클래스")
class JwtUtilTest {
    private static final Long USER_ID = 1L;
    private static final String KEY = "12345678901234567890123456789012";
    private static final String VALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjF9.ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaDk";

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(KEY);
    }

    @Nested
    @DisplayName("encode 메소드는")
    class a1 {
        @Nested
        @DisplayName("주어진 유저의 아이디를 받은 경우")
        class a2 {
            @Test
            @DisplayName("토큰을 리턴한다")
            void a3() {
                String token = jwtUtil.encode("userId", USER_ID);

                assertThat(token).isEqualTo(VALID_TOKEN);
            }
        }
    }

}
