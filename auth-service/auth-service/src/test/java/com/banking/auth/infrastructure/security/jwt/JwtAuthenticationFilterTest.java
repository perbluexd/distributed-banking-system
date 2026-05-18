package com.banking.auth.infrastructure.security.jwt;

import com.banking.auth.application.port.out.JwtTokenVerifierPort;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    private JwtTokenVerifierPort verifier;
    private JwtAuthenticationFilter filter;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        verifier = mock(JwtTokenVerifierPort.class);
        filter = new JwtAuthenticationFilter(verifier);
        filterChain = mock(FilterChain.class);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldSetAuthenticationWhenAuthorizationHeaderContainsBearerToken() throws Exception {
        UUID userId = UUID.randomUUID();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid-token");

        MockHttpServletResponse response = new MockHttpServletResponse();

        when(verifier.verifyAccessTokenAndGetUserId("valid-token")).thenReturn(userId);

        filter.doFilter(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        assertNotNull(authentication);
        assertEquals(userId.toString(), authentication.getName());
        assertTrue(authentication.isAuthenticated());

        verify(verifier).verifyAccessTokenAndGetUserId("valid-token");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldNotSetAuthenticationWhenAuthorizationHeaderIsMissing() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        assertNull(authentication);

        verifyNoInteractions(verifier);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldNotSetAuthenticationWhenAuthorizationHeaderIsNotBearer() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Basic abc123");

        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        assertNull(authentication);

        verifyNoInteractions(verifier);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldTrimBearerTokenBeforeVerifying() throws Exception {
        UUID userId = UUID.randomUUID();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer   valid-token   ");

        MockHttpServletResponse response = new MockHttpServletResponse();

        when(verifier.verifyAccessTokenAndGetUserId("valid-token")).thenReturn(userId);

        filter.doFilter(request, response, filterChain);

        verify(verifier).verifyAccessTokenAndGetUserId("valid-token");
        verify(filterChain).doFilter(request, response);
    }
}