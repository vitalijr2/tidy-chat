package io.gitlab.r2.tidy_chat;

import static java.util.Collections.singletonMap;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import io.gitlab.r2.telegram_bot.Update;
import io.gitlab.r2.telegram_bot.UpdateFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtendWith(MockitoExtension.class)
@Tag("fast")
class BotHandlerFastTest {

  @Mock
  private Context context;
  @Mock
  private APIGatewayProxyRequestEvent requestEvent;
  @Mock
  private Update update;
  @Mock
  private UpdateFactory updateFactory;

  private Logger logger;
  private BotHandler handler;

  @AfterEach
  void tearDown() {
    clearInvocations(logger);
  }

  @BeforeEach
  void setUp() {
    when(context.getAwsRequestId()).thenReturn("test-id");
    handler = new BotHandler(updateFactory);
    logger = LoggerFactory.getLogger(BotHandler.class);
  }

  @DisplayName("A request event with an empty body")
  @ParameterizedTest(name = "[{index}] body <{0}>")
  @NullAndEmptySource
  @ValueSource(strings = " ")
  void emptyRequestEventBody(String body) {
    // given
    when(requestEvent.getHeaders()).thenReturn(singletonMap("X-Forwarded-For", "1.2.3.4.5"));
    when(requestEvent.getBody()).thenReturn(body);

    // when
    var responseEvent = handler.handleRequest(requestEvent, context);

    // then
    verify(logger).warn("Empty request from {}", "1.2.3.4.5");

    assertAndVerify(responseEvent, "OK", "text/plain");
  }

  @DisplayName("The update factory returns null")
  @Test
  void updateFactoryReturnsNull() {
    // given
    when(requestEvent.getBody()).thenReturn("{\"test\":\"passed\"}");
    when(updateFactory.parseUpdate(anyString())).thenReturn(null);

    // when
    var responseEvent = handler.handleRequest(requestEvent, context);

    // then
    verify(logger, never()).trace(anyString());
    verifyNoMoreInteractions(logger);

    assertAndVerify(responseEvent, "OK", "text/plain");
  }

  @DisplayName("An update throws an exception")
  @Test
  void updateWithException() throws Exception {
    // given
    when(requestEvent.getHeaders()).thenReturn(singletonMap("X-Forwarded-For", "1.2.3.4.5"));
    when(requestEvent.getBody()).thenReturn("{\"json\":\"bad\"");
    when(update.call()).thenThrow(new Exception("test exception"));
    when(updateFactory.parseUpdate(anyString())).thenReturn(update);

    // when
    var responseEvent = handler.handleRequest(requestEvent, context);

    // then
    verify(logger).warn(eq("Update call from {}: {}\n{}"), eq("1.2.3.4.5"), eq("test exception"),
        eq("{\"json\":\"bad\""));

    assertAndVerify(responseEvent, "OK", "text/plain");
  }

  @DisplayName("The update returns null")
  @Test
  void updateReturnsNull() throws Exception {
    // given
    when(requestEvent.getBody()).thenReturn("{\"test\":\"passed\"}");
    when(update.call()).thenReturn(null);
    when(updateFactory.parseUpdate(anyString())).thenReturn(update);

    // when
    var responseEvent = handler.handleRequest(requestEvent, context);

    // then
    verify(logger, never()).trace(anyString());
    verifyNoMoreInteractions(logger);

    assertAndVerify(responseEvent, "OK", "text/plain");
  }

  @DisplayName("The update returns a value")
  @Test
  void updateReturnsValue() throws Exception {
    // given
    when(requestEvent.getBody()).thenReturn("{\"test\":\"passed\"}");
    when(update.call()).thenReturn("{\"number\":1}");
    when(updateFactory.parseUpdate(anyString())).thenReturn(update);

    // when
    var responseEvent = handler.handleRequest(requestEvent, context);

    // then
    verify(logger).trace("Response: {}", "{\"number\":1}");
    verifyNoMoreInteractions(logger);

    assertAndVerify(responseEvent, "{\"number\":1}", "application/json");
  }

  private void assertAndVerify(APIGatewayProxyResponseEvent responseEvent, String expectedBody,
      String expectedContentType) {
    verify(context).getAwsRequestId();
    assertAll("Response", () -> assertEquals(expectedBody, responseEvent.getBody()),
        () -> assertEquals(expectedContentType, responseEvent.getHeaders().get("Content-Type")),
        () -> assertEquals(200, responseEvent.getStatusCode()));
  }

}
