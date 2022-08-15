package io.gitlab.radio_rogal.tidy_chat.bot;

import static java.util.Collections.singleton;
import static java.util.Collections.singletonMap;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import java.util.Optional;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

@ExtendWith(MockitoExtension.class)
@Tag("fast")
class BotHandlerFastTest {

  @Mock
  private Context context;
  @Mock
  private Logger logger;
  @Mock
  private APIGatewayProxyRequestEvent requestEvent;

  @InjectMocks
  @Spy
  private BotHandler handler = new BotHandler();

  @DisplayName("A request event with an empty body")
  @ParameterizedTest(name = "[{index}] body <{0}>")
  @NullAndEmptySource
  @ValueSource(strings = " ")
  void emptyRequestEventBody(String body) {
    // given
    when(context.getAwsRequestId()).thenReturn("test-id");
    when(requestEvent.getHeaders()).thenReturn(singletonMap("X-Forwarded-For", "1.2.3.4.5"));
    when(requestEvent.getBody()).thenReturn(body);

    // when
    var responseEvent = handler.handleRequest(requestEvent, context);

    // then
    verify(context).getAwsRequestId();
    verify(logger).warn("Empty request from {}", "1.2.3.4.5");

    assertResponseEvent(responseEvent, "OK", "text/plain", 200);
  }

  @DisplayName("A request body contains invalid JSON")
  @Test
  void wrongJSON() {
    // given
    when(context.getAwsRequestId()).thenReturn("test-id");
    when(requestEvent.getHeaders()).thenReturn(singletonMap("X-Forwarded-For", "1.2.3.4.5"));
    when(requestEvent.getBody()).thenReturn("{\"json\":\"bad\"");

    // when
    var responseEvent = handler.handleRequest(requestEvent, context);

    // then
    verify(context).getAwsRequestId();
    verify(logger).warn(eq("Wrong request from {}: {}\n{}"), eq("1.2.3.4.5"), anyString(),
        eq("{\"json\":\"bad\""));

    assertResponseEvent(responseEvent, "OK", "text/plain", 200);
  }

  @DisplayName("To handle an unknown update")
  @Test
  void handleUnknownRequest() {
    // given
    when(context.getAwsRequestId()).thenReturn("test-id");
    when(logger.isTraceEnabled()).thenReturn(true);
    when(requestEvent.getBody()).thenReturn("{\"test\":\"passed\"}");

    // when
    var responseEvent = handler.handleRequest(requestEvent, context);

    // then
    verify(context).getAwsRequestId();
    verify(logger).isTraceEnabled();
    verify(logger).trace("{\"test\":\"passed\"}");
    verify(logger).info(eq("Unprocessed update: {}"), eq(singleton("test")));
    verifyNoMoreInteractions(logger);

    assertResponseEvent(responseEvent, "OK", "text/plain", 200);
  }

  @DisplayName("To handle a message")
  @Test
  void handleMessageRequest() {
    // given
    when(context.getAwsRequestId()).thenReturn("test-id");
    when(logger.isTraceEnabled()).thenReturn(false);
    when(requestEvent.getBody()).thenReturn("{\"message\":{\"text\":\"Hello, world!\"}}");
    doAnswer(invocation -> {
      var responseEvent = mock(APIGatewayProxyResponseEvent.class);

      when(responseEvent.getBody()).thenReturn("O.K.");
      when(responseEvent.getHeaders()).thenReturn(singletonMap("Content-Type", "text/plain"));
      when(responseEvent.getStatusCode()).thenReturn(202);

      return Optional.of(responseEvent);
    }).when(handler).processMessage(isA(JSONObject.class));

    // when
    var responseEvent = handler.handleRequest(requestEvent, context);

    // then
    verify(context).getAwsRequestId();
    verify(logger).isTraceEnabled();
    verify(handler).processMessage(isA(JSONObject.class));

    assertResponseEvent(responseEvent, "O.K.", "text/plain", 202);
  }

  private void assertResponseEvent(APIGatewayProxyResponseEvent responseEvent, String expectedBody,
      String expectedContentType, int expectedStatusCode) {
    assertAll("Response", () -> assertEquals(expectedBody, responseEvent.getBody()),
        () -> assertEquals(expectedContentType, responseEvent.getHeaders().get("Content-Type")),
        () -> assertEquals(expectedStatusCode, responseEvent.getStatusCode()));
  }

}
