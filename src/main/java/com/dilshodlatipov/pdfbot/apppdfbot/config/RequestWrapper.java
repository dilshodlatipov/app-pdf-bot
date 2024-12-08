package com.dilshodlatipov.pdfbot.apppdfbot.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;

@Component
public class RequestWrapper implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        ContentCachingRequestWrapper wrapper = new ContentCachingRequestWrapper(req);
        chain.doFilter(wrapper, response);
    }
}
