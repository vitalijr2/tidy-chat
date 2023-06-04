package io.gitlab.r2.tidy_chat;

import static java.util.Objects.nonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Set;
import org.json.JSONObject;
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

@ExtendWith(MockitoExtension.class)
@Tag("slow")
class TidyChatUpdateFactoryTest {

  @Mock
  private JSONObject message;

  private TidyChatUpdateFactory updateFactory;

  @BeforeEach
  void setUp() {
    updateFactory = new TidyChatUpdateFactory();
  }

  @DisplayName("Inline query is not supported")
  @Test
  void inlineQuery() {
    // when
    var exception = assertThrows(UnsupportedOperationException.class,
        () -> updateFactory.processInlineQuery(message));

    // then
    verifyNoInteractions(message);

    assertEquals("Inline query is not supported", exception.getMessage());
  }

  @DisplayName("Ignore a bot message")
  @Test
  void botMessage() {
    // given
    when(message.has("via_bot")).thenReturn(true);

    // when
    var update = updateFactory.processMessage(message);

    // then
    assertNull(update);
  }

  @DisplayName("The message should not be deleted")
  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = "qwerty")
  void shouldNotBeDeleted(String fieldName) {
    // given
    when(message.has("via_bot")).thenReturn(false);
    when(message.keySet()).thenReturn((nonNull(fieldName)) ? Set.of(fieldName) : Set.of());

    // when
    var update = updateFactory.processMessage(message);

    // then
    assertNull(update);
  }

  @DisplayName("The message should not be deleted")
  @ParameterizedTest
  @ValueSource(strings = {"new_chat_members", "left_chat_member", "new_chat_title",
      "new_chat_photo", "delete_chat_photo", "pinned_message"})
  void shouldBeDeleted(String fieldName) {
    // given
    when(message.has("via_bot")).thenReturn(false);
    when(message.getJSONObject("chat")).thenReturn(message);
    when(message.getLong("id")).thenReturn(12345L);
    when(message.getLong("message_id")).thenReturn(67890L);
    when(message.keySet()).thenReturn(Set.of(fieldName));
    if ("new_chat_title".equals(fieldName)) {
      when(message.getString("new_chat_title")).thenReturn("test title");
    }
    when(message.getString("title")).thenReturn("test title");

    // when
    var update = updateFactory.processMessage(message);

    // then
    assertNotNull(update);
  }

}