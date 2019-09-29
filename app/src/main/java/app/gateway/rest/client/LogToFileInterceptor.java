package app.gateway.rest.client;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import app.qamode.log.LogToFileHandler;
import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

public class LogToFileInterceptor implements Interceptor {
    private static final Charset UTF8 = Charset.forName("UTF-8");


    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        RequestBody requestBody = request.body();

        Connection connection = chain.connection();
        Protocol protocol = connection != null ? connection.protocol() : Protocol.HTTP_1_1;
        String requestStartMessage = "--> " + request.method() + ' ' + request.url() + ' ' + protocol;
        if (requestBody != null) {
            requestStartMessage += " (" + requestBody.contentLength() + "-byte body)";
        }
        LogToFileHandler.addLog(requestStartMessage);

        Headers headers = request.headers();
        for (int i = 0, count = headers.size(); i < count; i++) {
            String name = headers.name(i);
            // Skip headers from the request body as they are explicitly logged above.
            if (!"Content-Type".equalsIgnoreCase(name) && !"Content-Length".equalsIgnoreCase(name)) {
                LogToFileHandler.addLog(name + ": " + headers.value(i));
            }
        }


        Buffer buffer1 = new Buffer();
        if (requestBody != null) {
            requestBody.writeTo(buffer1);
            Charset charset1 = UTF8;
            MediaType contentType1 = requestBody.contentType();
            if (contentType1 != null) {
                charset1 = contentType1.charset(UTF8);
            }

            LogToFileHandler.addLog("");
            if (isPlaintext(buffer1)) {
                LogToFileHandler.addLog(buffer1.readString(charset1));
                LogToFileHandler.addLog("--> END " + request.method()
                        + " (" + requestBody.contentLength() + "-byte body)");
            } else {
                LogToFileHandler.addLog("--> END " + request.method() + " (binary "
                        + requestBody.contentLength() + "-byte body omitted)");
            }
        }

        long startNs = System.nanoTime();
        Response response;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            LogToFileHandler.addLog("<-- HTTP FAILED: " + e);
            throw e;
        }
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

        ResponseBody responseBody = response.body();
        long contentLength = 0;
        if (responseBody != null) {
            contentLength = responseBody.contentLength();
            LogToFileHandler.addLog("<-- " + response.code() + ' ' + response.message() + ' '
                    + response.request().url() + " (" + tookMs + "ms" + ("") + ')');

            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE); // Buffer the entire body.
            Buffer buffer2 = source.buffer();
            Charset charset2 = UTF8;
            MediaType contentType2 = responseBody.contentType();
            if (contentType2 != null) {
                charset2 = contentType2.charset(UTF8);
            }
            if (!isPlaintext(buffer2)) {
                LogToFileHandler.addLog("");
                LogToFileHandler.addLog("<-- END HTTP (binary " + buffer2.size() + "-byte body omitted)");
                return response;
            }
            if (contentLength != 0) {
                LogToFileHandler.addLog("");
                LogToFileHandler.addLog(buffer2.clone().readString(charset2));
            }
            LogToFileHandler.addLog("<-- END HTTP (" + buffer2.size() + "-byte body)");
        }
        return response;
    }

    static boolean isPlaintext(Buffer buffer) {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false; // Truncated UTF-8 sequence.
        }
    }
}
