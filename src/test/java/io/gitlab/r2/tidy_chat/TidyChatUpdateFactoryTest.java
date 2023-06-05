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
import org.junit.jupiter.params.provider.CsvSource;
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

  @DisplayName("The message should be deleted")
  @ParameterizedTest
  @CsvSource({"new_chat_members,group", "left_chat_member,group", "new_chat_photo,group",
      "delete_chat_photo,group", "pinned_message,group", "new_chat_members,supergroup",
      "left_chat_member,supergroup", "new_chat_title,supergroup", "new_chat_photo,supergroup",
      "delete_chat_photo,supergroup", "pinned_message,supergroup", "new_chat_members,channel",
      "left_chat_member,channel", "new_chat_title,channel", "new_chat_photo,channel",
      "delete_chat_photo,channel", "pinned_message,channel"})
  void shouldBeDeleted(String fieldName, String chatType) {
    // given
    when(message.has("via_bot")).thenReturn(false);
    when(message.getJSONObject("chat")).thenReturn(message);
    when(message.getLong("id")).thenReturn(12345L);
    when(message.getLong("message_id")).thenReturn(67890L);
    when(message.keySet()).thenReturn(Set.of(fieldName));
    if ("new_chat_title".equals(fieldName)) {
      when(message.getString("new_chat_title")).thenReturn("test title");
      when(message.getString("type")).thenReturn(chatType);
    }
    when(message.getString("title")).thenReturn("test title");

    // when
    var update = updateFactory.processMessage(message);

    // then
    assertNotNull(update);
  }

  @DisplayName("The new title message in group should not be deleted")
  @Test
  void newTitleInGroupShouldNotBeDeleted() {
    // given
    when(message.has("via_bot")).thenReturn(false);
    when(message.getJSONObject("chat")).thenReturn(message);
    when(message.getLong("id")).thenReturn(12345L);
    when(message.getLong("message_id")).thenReturn(67890L);
    when(message.keySet()).thenReturn(Set.of("new_chat_title"));
    when(message.getString("title")).thenReturn("test title");
    when(message.getString("type")).thenReturn("group");

    // when
    var update = updateFactory.processMessage(message);

    // then
    assertNull(update);
  }

}