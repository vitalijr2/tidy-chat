package io.gitlab.r2.telegram_bot;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("fast")
class TelegramUtilsTest {

  JSONObject message;

  @BeforeEach
  void setUp() {
    message = new JSONObject();
    message.put("message_id", 1365);
    message.put("text", "Hello, world!");
  }

  @DisplayName("Chat ID")
  @Test
  void chatId() {
    // given
    JSONObject chat = new JSONObject();

    chat.put("id", 9876543210L);
    message.put("chat", chat);

    // when and then
    assertEquals(9876543210L, TelegramUtils.getChatId(message));
  }

  @DisplayName("ID")
  @Test
  void id() {
    // given
    message.put("id", 9876543210L);

    // when and then
    assertEquals(9876543210L, TelegramUtils.getId(message));
  }

  @DisplayName("Message sends via another bot")
  @Test
  void botMessage() {
    // given
    JSONObject bot = new JSONObject("{\"id\":\"12345\"}");

    message.put("via_bot", bot);

    // when and then
    assertTrue(TelegramUtils.isBotMessage(message));
  }

  @DisplayName("Message does not send via another bot")
  @Test
  void nonBotMessage() {
    // when and then
    assertFalse(TelegramUtils.isBotMessage(message));
  }


  @DisplayName("Remove message")
  @Test
  void removeMessage() {
    // when
    var jsonText = TelegramUtils.deleteMessage(9876543210L, 12345L);

    // then
    JSONObject responseMessage = new JSONObject(jsonText);

    assertAll("Response message",
        () -> assertThat(responseMessage.keySet(),
            containsInAnyOrder("chat_id", "message_id", "method")),
        () -> assertEquals(9876543210L, responseMessage.getLong("chat_id")),
        () -> assertEquals(12345L, responseMessage.getLong("message_id")),
        () -> assertEquals("deleteMessage", responseMessage.getString("method"), "method"));
  }

}
