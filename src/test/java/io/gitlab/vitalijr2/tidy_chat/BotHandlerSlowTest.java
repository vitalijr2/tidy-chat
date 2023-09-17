package io.gitlab.vitalijr2.tidy_chat;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
@Tag("slow")
class BotHandlerSlowTest {

  @Mock
  private Context context;
  @Mock
  private APIGatewayProxyRequestEvent requestEvent;
  private BotHandler handler;

  @BeforeEach
  void setUp() {
    handler = new BotHandler();
  }

  @DisplayName("The message should not be deleted")
  @ParameterizedTest
  @CsvFileSource(resources = "should-not-be-deleted.csv", delimiter = '|', numLinesToSkip = 1)
  void shouldNotBeDeleted(String caseName, String body) {
    // given
    when(context.getAwsRequestId()).thenReturn("123-456-789");
    when(requestEvent.getBody()).thenReturn(body);

    // when
    var responseEvent = handler.handleRequest(requestEvent, context);

    // then
    assertAll(caseName, () -> assertNotNull(responseEvent, "Response event no null"),
        () -> assertThat("Status code", responseEvent.getStatusCode(), equalTo(200)),
        () -> assertThat("Response body", responseEvent.getBody(), equalTo("OK")));
  }

  @DisplayName("The message should be deleted")
  @ParameterizedTest
  @CsvFileSource(resources = "should-be-deleted.csv", delimiter = '|', numLinesToSkip = 1)
  void shouldBeDeleted(String caseName, String body) {
    // given
    when(context.getAwsRequestId()).thenReturn("123-456-789");
    when(requestEvent.getBody()).thenReturn(body);

    // when
    var responseEvent = handler.handleRequest(requestEvent, context);

    // then
    assertAll(caseName, () -> assertNotNull(responseEvent, "Response event not null"),
        () -> assertThat("Status code", responseEvent.getStatusCode(), equalTo(200)),
        () -> assertEquals("Response callback",
            "{\"method\":\"deleteMessage\",\"message_id\":67890,\"chat_id\":12345}",
            responseEvent.getBody(), true));
  }

}
