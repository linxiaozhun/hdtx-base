package com.hdtx.base.common.spring.ribbon;

import feign.Client;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * refer to feign.okhttp.OkHttpClient
 */
public class TdFeignOkHttpClient implements Client {

    private static final Logger logger = LoggerFactory.getLogger(TdFeignOkHttpClient.class);

    private final OkHttpClient delegate;

    private final RibbonTimeoutMatcher ribbonTimeoutMatcher;

    public TdFeignOkHttpClient() {
        this(new OkHttpClient(), null);
    }

    public TdFeignOkHttpClient(OkHttpClient delegate, RibbonTimeoutMatcher ribbonTimeoutMatcher) {
        this.delegate = delegate;
        this.ribbonTimeoutMatcher = ribbonTimeoutMatcher;
    }

    static Request toOkHttpRequest(feign.Request input) {
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(input.url());

        MediaType mediaType = null;
        boolean hasAcceptHeader = false;
        for (String field : input.headers().keySet()) {
            if (field.equalsIgnoreCase("Accept")) {
                hasAcceptHeader = true;
            }

            for (String value : input.headers().get(field)) {
                requestBuilder.addHeader(field, value);
                if (field.equalsIgnoreCase("Content-Type")) {
                    mediaType = MediaType.parse(value);
                    if (input.charset() != null) {
                        mediaType.charset(input.charset());
                    }
                }
            }
        }
        // Some servers choke on the default accept string.
        if (!hasAcceptHeader) {
            requestBuilder.addHeader("Accept", "*/*");
        }

        byte[] inputBody = input.body();
        boolean isMethodWithBody =
                feign.Request.HttpMethod.POST == input.httpMethod() || feign.Request.HttpMethod.PUT == input.httpMethod()
                        || feign.Request.HttpMethod.PATCH == input.httpMethod();
        if (isMethodWithBody) {
            requestBuilder.removeHeader("Content-Type");
            if (inputBody == null) {
                // write an empty BODY to conform with okhttp 2.4.0+
                // http://johnfeng.github.io/blog/2015/06/30/okhttp-updates-post-wouldnt-be-allowed-to-have-null-body/
                inputBody = new byte[0];
            }
        }

        RequestBody body = inputBody != null ? RequestBody.create(mediaType, inputBody) : null;
        requestBuilder.method(input.httpMethod().name(), body);
        return requestBuilder.build();
    }

    private static feign.Response toFeignResponse(Response response, feign.Request request)
            throws IOException {
        return feign.Response.builder()
                .status(response.code())
                .reason(response.message())
                .request(request)
                .headers(toMap(response.headers()))
                .body(toBody(response.body()))
                .build();
    }

    private static Map<String, Collection<String>> toMap(Headers headers) {
        return (Map) headers.toMultimap();
    }

    private static feign.Response.Body toBody(final ResponseBody input) throws IOException {
        if (input == null || input.contentLength() == 0) {
            if (input != null) {
                input.close();
            }
            return null;
        }
        final Integer length = input.contentLength() >= 0 && input.contentLength() <= Integer.MAX_VALUE
                ? (int) input.contentLength()
                : null;

        return new feign.Response.Body() {

            @Override
            public void close() throws IOException {
                input.close();
            }

            @Override
            public Integer length() {
                return length;
            }

            @Override
            public boolean isRepeatable() {
                return false;
            }

            @Override
            public InputStream asInputStream() throws IOException {
                return input.byteStream();
            }

            @Override
            public Reader asReader() throws IOException {
                return input.charStream();
            }

            @Override
            public Reader asReader(Charset charset) throws IOException {
                return asReader();
            }
        };
    }

    @Override
    public feign.Response execute(feign.Request input, feign.Request.Options options)
            throws IOException {
        OkHttpClient requestScoped;
        options = replaceOptionsIfUrlSpecifiedOptionsPresent(input, options);
        if (delegate.connectTimeoutMillis() != options.connectTimeoutMillis()
                || delegate.readTimeoutMillis() != options.readTimeoutMillis()) {
            requestScoped = delegate.newBuilder()
                    .connectTimeout(options.connectTimeoutMillis(), TimeUnit.MILLISECONDS)
                    .readTimeout(options.readTimeoutMillis(), TimeUnit.MILLISECONDS)
                    .followRedirects(options.isFollowRedirects())
                    .build();
        } else {
            requestScoped = delegate;
        }
        Request request = toOkHttpRequest(input);
        Response response = requestScoped.newCall(request).execute();
        return toFeignResponse(response, input).toBuilder().request(input).build();
    }

    private feign.Request.Options replaceOptionsIfUrlSpecifiedOptionsPresent(feign.Request input, feign.Request.Options options) {

        try {
            if(ribbonTimeoutMatcher != null && ribbonTimeoutMatcher.isEnabled()) {
                RibbonServiceConfig ribbonServiceConfig = ribbonTimeoutMatcher.findRibbonServiceConfig(input.url(), input.method());
                if(ribbonServiceConfig != null) {
                    return new feign.Request.Options(
                            ribbonServiceConfig.getConnectTimeout() != null ? ribbonServiceConfig.getConnectTimeout() : options.connectTimeoutMillis(),
                            ribbonServiceConfig.getReadTimeout() != null ? ribbonServiceConfig.getReadTimeout() : options.readTimeoutMillis());
                }
            }

        } catch (Exception e) {
            logger.error("replace specified url for feign method fail, " + e.getMessage());
        }

        return options;
    }


}
