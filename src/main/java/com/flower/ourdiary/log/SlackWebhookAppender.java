package com.flower.ourdiary.log;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.flower.ourdiary.util.ObjectMapperFactory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.*;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SlackWebhookAppender extends AppenderBase<ILoggingEvent> {

    private final ConcurrentMap<String, ILoggingEvent> eventMap = new ConcurrentHashMap<>();

    private SlackWebhookRestClient slackWebhookRestClient;
    private ObjectMapper objectMapper;

    @Getter @Setter
    private String webhookUrl = null;

    @Getter @Setter
    private String channel = "#random";

    @Getter @Setter
    private String iconEmoji = ":ghost:";

    @Getter @Setter
    private String username = "spring-boot-server-bot";

    private boolean isSendableLog(JsonNode jsonNode) {
        return jsonNode.get("path").asText().startsWith("/api/")
                && jsonNode.get("res_body") != NullNode.getInstance();
    }

    @Override
    public void start() {

        objectMapper = ObjectMapperFactory.createWithDateFormat();

        OkHttpClient client = new OkHttpClient();

        // for test
//        OkHttpClient client = new OkHttpClient.Builder()
//                .addInterceptor(new RestLogInterceptor(objectMapper))
//                .build();

        slackWebhookRestClient = new Retrofit.Builder()
                .client(client)
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .baseUrl("https://hooks.slack.com/")
                .build()
                .create(SlackWebhookRestClient.class);

        super.start();
    }

    @SuppressWarnings("unused")
    public ConcurrentMap<String, ILoggingEvent> getEventMap() {
        return eventMap;
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        try {
            JsonNode jsonNode = objectMapper.readTree(eventObject.getMessage());
            if(!isSendableLog(jsonNode)) {
                return;
            }
            slackWebhookRestClient.dispatchMessage(
                    webhookUrl, new ReqSlackWebhookBody(
                            channel,
                            iconEmoji,
                            username,
                            jsonNode.toPrettyString()
                    )
            ).execute();
        } catch (Throwable t) {
            addError(t.getLocalizedMessage(), t);
        }
    }

    @RequiredArgsConstructor
    @Getter
    public static class ReqSlackWebhookBody {
        private final String channel;
        private final String iconEmoji;
        private final String username;
        private final String text;
    }

    public interface SlackWebhookRestClient {
        @POST
        @Headers({"Content-Type: application/json"})
        Call<ResponseBody> dispatchMessage(
                @Url String url,
                @Body ReqSlackWebhookBody body
        );
    }
}
