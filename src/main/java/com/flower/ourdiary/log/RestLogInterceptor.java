package com.flower.ourdiary.log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flower.ourdiary.common.Constant;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.internal.http.HttpHeaders;
import okio.Buffer;
import okio.BufferedSource;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class RestLogInterceptor implements Interceptor {

    private final ObjectMapper objectMapper;

    private static final Charset UTF8 = StandardCharsets.UTF_8;

    private volatile Set<String> headersToRedact = Collections.emptySet();

    public void redactHeader(String name) {
        Set<String> newHeadersToRedact = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        newHeadersToRedact.addAll(headersToRedact);
        newHeadersToRedact.add(name);
        headersToRedact = newHeadersToRedact;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();

        RestLog.RestLogBuilder logBuilder = RestLog.builder()
                .threadId(MDC.get(Constant.MDC_KEY_THREAD_ID))
                .method(request.method())
                .url(request.url().toString());

//        sb.append(",req_headers:{");
//        boolean prevHeaderExists = false;
//        Headers headers = request.headers();
//        for (int i = 0, count = headers.size(); i < count; i++) {
//            String name = headers.name(i);
//            if ("Content-Type".equalsIgnoreCase(name) || "Content-Length".equalsIgnoreCase(name))
//                continue;
//
//            String value = headersToRedact.contains(name) ? "██" : headers.value(i);
//            sb.append(prevHeaderExists ? "," : "");
//            sb.append(name);
//            sb.append(":\"");
//            sb.append(value);
//            sb.append("\"");
//            prevHeaderExists = true;
//        }
//        sb.append("}");

        if(request.body() != null) {
            RequestBody requestBody = request.body();

            logBuilder
                    .reqContentType(requestBody.contentType().toString())
                    .reqContentLength(requestBody.contentLength());

            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);

            Charset charset = UTF8;
            MediaType contentType = requestBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(UTF8);
            }

            if (isPlaintext(buffer)) {
                logBuilder.reqBody(buffer.readString(charset));
            }
        }

        long startMs = System.currentTimeMillis();
        logBuilder.time(new Date(startMs));

        Response response;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            logBuilder.resError("[FAILED] "+e.toString().replace("\n",""));
            try {
                log.info(objectMapper.writeValueAsString(logBuilder.build()));
            } catch (Exception e2) { }
            throw e;
        }

        logBuilder.duration(System.currentTimeMillis() - startMs);

        logBuilder.statusCode(response.code());

//        (response.message().isEmpty() ? "" : ' ' + response.message())

//        sb.append(",req_headers:{");
//        prevHeaderExists = false;
//        headers = response.headers();
//        for (int i = 0, count = headers.size(); i < count; i++) {
//            String name = headers.name(i);
//            String value = headersToRedact.contains(name) ? "██" : headers.value(i);
//            sb.append(prevHeaderExists ? "," : "");
//            sb.append(name);
//            sb.append(":\"");
//            sb.append(value);
//            sb.append("\"");
//            prevHeaderExists = true;
//        }
//        sb.append("}");

        ResponseBody responseBody = response.body();

        logBuilder
                .resContentType(Optional.ofNullable(responseBody.contentType()).map(Object::toString).orElse(null))
                .resContentLength(Optional.ofNullable(responseBody.contentLength()).orElse(0L));

        if(response.code() >= 400 && HttpHeaders.hasBody(response)) {
            if(!bodyHasUnknownEncoding(response.headers())) {
                BufferedSource source = responseBody.source();
                source.request(Long.MAX_VALUE); // Buffer the entire body.
                Buffer buffer = source.getBuffer();

//                Long gzippedLength = null;
//                if ("gzip".equalsIgnoreCase(headers.get("Content-Encoding"))) {
//                    gzippedLength = buffer.size();
//                    try (GzipSource gzippedResponseBody = new GzipSource(buffer.clone())) {
//                        buffer = new Buffer();
//                        buffer.writeAll(gzippedResponseBody);
//                    }
//                }

                Charset charset = UTF8;
                MediaType contentType = responseBody.contentType();
                if (contentType != null) {
                    charset = contentType.charset(UTF8);
                }

                if (isPlaintext(buffer)) { // if not BINARY
                    if(responseBody.contentLength() > 0) {
                        logBuilder.resBody(buffer.clone().readString(charset));
//                        sb.append(",res_body:\"");
//                        sb.append(String.format("%s", buffer.clone().readString(charset)));
//                        sb.append("\"");
                    }
//                    if (gzippedLength != null) {
//                        logger.log("<-- END HTTP (" + buffer.size() + "-byte, "
//                                + gzippedLength + "-gzipped-byte body)");
//                    } else {
//                        logger.log("<-- END HTTP (" + buffer.size() + "-byte body)");
//                    }
                }
            }
        }

        try {
            log.info(objectMapper.writeValueAsString(logBuilder.build()));
        } catch (Exception e2) { }

        return response;
    }

    static boolean isPlaintext(Buffer buffer) {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted())
                    break;
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint))
                    return false;
            }
            return true;
        } catch (EOFException e) {
            return false;
        }
    }

    private static boolean bodyHasUnknownEncoding(Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        return contentEncoding != null
                && !contentEncoding.equalsIgnoreCase("identity")
                && !contentEncoding.equalsIgnoreCase("gzip");
    }


    @Builder
    @Getter
    static class RestLog {
        private final String logType = "REST";
        private final Date time;
        private final String threadId;
        private final String method;
        private final String url;
        private final String reqContentType;
        private final Long reqContentLength;
        private final String reqBody;
        private final String resError;
        private final Long duration;
        private final Integer statusCode;
        private final String resContentType;
        private final Long resContentLength;
        private final String resBody;
    }
}