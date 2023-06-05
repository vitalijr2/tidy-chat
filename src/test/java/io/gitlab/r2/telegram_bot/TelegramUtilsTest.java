package io.gitlab.r2.telegram_bot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

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

  @DisplayName("Chat title")
  @Test
  void chatTitle() {
    // given
    JSONObject chat = new JSONObject();

    chat.put("title", "test title");
    message.put("chat", chat);

    // when and then
    assertEquals("test title", TelegramUtils.getChatTitle(message));
  }

  @DisplayName("Chat type")
  @Test
  void chatType() {
    // given
    JSONObject chat = new JSONObject();

    chat.put("type", "test type");
    message.put("chat", chat);

    // when and then
    assertEquals("test type", TelegramUtils.getChatType(message));
  }

  @DisplayName("ID")
  @Test
  void id() {
    // given
    message.put("id", 9876543210L);

    // when and then
    assertEquals(9876543210L, TelegramUtils.getId(message));
  }

  @DisplayName("Message ID")
  @Test
  void messageId() {
    // given
    message.put("message_id", 9876543210L);

    // when and then
    assertEquals(9876543210L, TelegramUtils.getMessageId(message));
  }


  @DisplayName("New chat title")
  @Test
  void newChatTitle() {
    // given
    message.put("new_chat_title", "new test title");

    // when and then
    assertEquals("new test title", TelegramUtils.getNewChatTitle(message));
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

  @DisplayName("Chat type: channel, group, supergroup")
  @ParameterizedTest
  @CsvSource({"channel,true,false,false", "group,false,true,false", "supergroup,false,false,true"})
  void chatType(String chatType, boolean isChannel, boolean isGroup, boolean isSupergroup) {
    // given
    JSONObject chat = new JSONObject();

    chat.put("type", chatType);
    message.put("chat", chat);

    // when and then
    assertEquals(isChannel, TelegramUtils.isChannel(message));
    assertEquals(isGroup, TelegramUtils.isGroup(message));
    assertEquals(isSupergroup, TelegramUtils.isSupergroup(message));
  }

}
