package org.example.authservice.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

@Slf4j
public class RequestLogFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper((HttpServletRequest) request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper((HttpServletResponse) response);

        try {
            log.info("Request: {} {}",
                    ((HttpServletRequest) request).getMethod(),
                    ((HttpServletRequest) request).getRequestURI());
            chain.doFilter(wrappedRequest, wrappedResponse);

        } finally {
            byte[] responseBody = wrappedResponse.getContentAsByteArray();
            String body = responseBody.length > 0 ?
                    new String(responseBody, wrappedResponse.getCharacterEncoding()) : "[empty body]";

            log.info("Response {} {}: {} | Body: {}",
                    wrappedResponse.getStatus(),
                    ((HttpServletRequest) request).getMethod(),
                    ((HttpServletRequest) request).getRequestURI(),
                    body);

            wrappedResponse.copyBodyToResponse();
        }
    }
}