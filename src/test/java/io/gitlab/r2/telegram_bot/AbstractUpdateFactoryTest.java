package io.gitlab.r2.telegram_bot;

import static java.util.Collections.singleton;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtendWith(MockitoExtension.class)
@Tag("fast")
class AbstractUpdateFactoryTest {

  @Captor
  private ArgumentCaptor<JSONObject> jsonCaptor;
  @Spy
  private AbstractUpdateFactory factory = new TestUpdateFactory();
  private Logger logger;

  @AfterEach
  void tearDown() {
    clearInvocations(logger);
  }

  @BeforeEach
  void setUp() {
    logger = LoggerFactory.getLogger(TestUpdateFactory.class);
  }

  @DisplayName("Wrong update")
  @ParameterizedTest(name = "[{index}] body <{0}>")
  @EmptySource
  @ValueSource(strings = {" ", "{\"json\":\"bad\""})
  void wrongUpdate(String body) {
    // when
    var update = factory.parseUpdate(body);

    // then
    verify(logger).warn(startsWith("Wrong update:"), anyString(), eq(body));

    assertNull(update);
  }

  @DisplayName("To handle an unknown update")
  @Test
  void handleUnknownRequest() {
    // given
    when(logger.isTraceEnabled()).thenReturn(false);

    // when
    var update = factory.parseUpdate("{\"test\":\"passed\"}");

    // then
    verify(logger).isTraceEnabled();
    verify(logger).info(eq("Unprocessed update: {}"), eq(singleton("test")));

    assertNull(update);
  }

  @DisplayName("To handle and trace an unknown update")
  @Test
  void handleAndTraceUnknownRequest() {
    // given
    when(logger.isTraceEnabled()).thenReturn(true);

    // when
    var update = factory.parseUpdate("{\"test\":\"passed\"}");

    // then
    verify(logger).isTraceEnabled();
    verify(logger).trace("{\"test\":\"passed\"}");
    verify(logger).info(eq("Unprocessed update: {}"), eq(singleton("test")));

    assertNull(update);
  }

  @DisplayName("To handle an inline query")
  @Test
  void handleInlineQuery() {
    doAnswer(invocationOnMock -> Mockito.mock(Update.class)).when(factory)
        .processInlineQuery(isA(JSONObject.class));

    // when
    var update = factory.parseUpdate("{\"inline_query\":{\"test\":\"passed\"}}");

    // then
    verify(factory).parseUpdate(anyString());
    verify(factory).processInlineQuery(jsonCaptor.capture());
    verifyNoMoreInteractions(factory);

    assertAll("Inline query",
        () -> assertEquals("JSON message", "{\"test\":\"passed\"}", jsonCaptor.getValue(), false),
        () -> assertNotNull(update, "Update not null"));
  }

  @DisplayName("To handle a message")
  @Test
  void handleMessage() {
    doAnswer(invocationOnMock -> Mockito.mock(Update.class)).when(factory)
        .processMessage(isA(JSONObject.class));

    // when
    var update = factory.parseUpdate("{\"message\":{\"test\":\"passed\"}}");

    // then
    verify(factory).parseUpdate(anyString());
    verify(factory).processMessage(jsonCaptor.capture());
    verifyNoMoreInteractions(factory);

    assertAll("Message",
        () -> assertEquals("JSON message", "{\"test\":\"passed\"}", jsonCaptor.getValue(), false),
        () -> assertNotNull(update, "Update not null"));
  }

  @DisplayName("To handle the own char member")
  @Test
  void handleMyChatMember() {
    doAnswer(invocationOnMock -> Mockito.mock(Update.class)).when(factory)
        .processMyChatMember(isA(JSONObject.class));

    // when
    var update = factory.parseUpdate("{\"my_chat_member\":{\"test\":\"passed\"}}");

    // then
    verify(factory).parseUpdate(anyString());
    verify(factory).processMyChatMember(jsonCaptor.capture());
    verifyNoMoreInteractions(factory);

    assertAll("My chat member",
        () -> assertEquals("JSON message", "{\"test\":\"passed\"}", jsonCaptor.getValue(), false),
        () -> assertNotNull(update, "Update not null"));
  }

  static class TestUpdateFactory extends AbstractUpdateFactory {

    @Override
    protected Update processInlineQuery(JSONObject message) {
      return null;
    }

    @Override
    protected Update processMessage(JSONObject message) {
      return null;
    }

    @Override
    protected Update processMyChatMember(JSONObject message) {
      return null;
    }

  }

}
