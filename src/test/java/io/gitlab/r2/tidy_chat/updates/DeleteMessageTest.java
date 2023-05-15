package io.gitlab.r2.tidy_chat.updates;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

@ExtendWith(MockitoExtension.class)
@Tag("fast")
class DeleteMessageTest {

  @Captor
  private ArgumentCaptor<Marker> markerCaptor;
  private Logger logger;

  @BeforeEach
  void setUp() {
    logger = LoggerFactory.getLogger(DeleteMessage.class);
  }

  @DisplayName("Delete message")
  @Test
  void call() {
    // given
    var update = new DeleteMessage(12345L, 67890L, "test operation", "test title");

    // when
    var response = update.call();

    // then
    verify(logger).info(markerCaptor.capture(), eq("remove message in the chat {}/{}: {}"),
        eq(12345L), eq("test title"), eq("test operation"));

    assertAll("Delete message",
        () -> assertThat("Marker", markerCaptor.getValue(), hasProperty("name", equalTo("remove"))),
        () -> assertEquals("Response callback",
            "{\"message_id\":67890,\"method\":\"deleteMessage\",\"chat_id\":12345}", response,
            true));
  }

}