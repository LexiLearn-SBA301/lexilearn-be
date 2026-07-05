package com.sba.lexilearnbe.shared.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sba.lexilearnbe.modules.auth.entity.Account;
import com.sba.lexilearnbe.modules.auth.entity.Role;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class JwtAuthenticationFilterTest {

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternalStoresAccountIdUuidAsAuthenticationPrincipal() throws Exception {
        UUID accountId = UUID.fromString("00000000-0000-0000-0000-000000000101");
        JwtService jwtService = jwtService();
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(
                jwtService,
                new NonBlacklistedTokenService(),
                new SecurityErrorResponseWriter(new ObjectMapper())
        );

        Role userRole = Role.builder()
                .name("USER")
                .build();
        Account account = Account.builder()
                .id(accountId)
                .email("test.reader@lexilearn.local")
                .roles(Set.of(userRole))
                .build();
        String token = jwtService.generateAccessToken(account);

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        filter.doFilterInternal(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNotNull();
        assertThat(authentication.getPrincipal()).isEqualTo(accountId);
        assertThat(authentication.getPrincipal()).isInstanceOf(UUID.class);
        assertThat(authentication.getAuthorities())
                .extracting(Object::toString)
                .containsExactly("ROLE_USER");
    }

    @Test
    void reviewEndpointSkipsJwtOnlyForPublicGetRequest() {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(
                jwtService(),
                new NonBlacklistedTokenService(),
                new SecurityErrorResponseWriter(new ObjectMapper())
        );
        String path = "/api/v1/works/20000000-0000-0000-0000-000000000008/reviews";

        MockHttpServletRequest getRequest = new MockHttpServletRequest("GET", path);
        MockHttpServletRequest postRequest = new MockHttpServletRequest("POST", path);

        assertThat(filter.shouldNotFilter(getRequest)).isTrue();
        assertThat(filter.shouldNotFilter(postRequest)).isFalse();
    }

    private JwtService jwtService() {
        JwtService jwtService = new JwtService();
        ReflectionTestUtils.setField(
                jwtService,
                "secret",
                "0123456789abcdef0123456789abcdef0123456789abcdef"
        );
        ReflectionTestUtils.setField(jwtService, "accessTokenTtl", 1800L);
        jwtService.init();
        return jwtService;
    }

    private static class NonBlacklistedTokenService extends TokenBlacklistService {

        NonBlacklistedTokenService() {
            super(null, null);
        }

        @Override
        public boolean isBlacklisted(String token) {
            return false;
        }
    }
}
