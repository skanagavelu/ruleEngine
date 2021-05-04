package com.nepu.metro.common.controller.utils;

import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class HttpResponseUtils {

    private static final MultiValueMap<String, String> COMMON_HTTP_HEADERS_MAP = new LinkedMultiValueMap<String, String>();

    static {
        COMMON_HTTP_HEADERS_MAP.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
    }

    public static final MultiValueMap<String, String> getCommonHttpHeaders() {
        return COMMON_HTTP_HEADERS_MAP;
    }
}
