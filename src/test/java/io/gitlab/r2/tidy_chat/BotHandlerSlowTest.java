package io.gitlab.r2.tidy_chat;

import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresent;
import static org.hamcrest.MatcherAssert.assertThat;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@Tag("slow")
class BotHandlerSlowTest {

  private BotHandler handler;

  @DisplayName("Text message return an empty response")
  @Test
  void text() {
    // given
    JSONObject text = new JSONObject();

    text.put("text", "test");
    handler = new BotHandler();

    // when and then
    assertThat(handler.processMessage(text), isEmpty());
  }

  @DisplayName("Remove join or left messages")
  @ParameterizedTest
  @CsvSource(value = {"new_chat_members,test title", "left_chat_member,test title",
      "new_chat_title,new title", "new_chat_photo,test title", "delete_chat_photo,test title",
      "pinned_message,test title"})
  void removeJoinOrLeftMessages(String action, String title) {
    // given
    JSONObject chat = new JSONObject();
    JSONObject member_action = new JSONObject();

    chat.put("id", 9876543210L);
    chat.put("title", "test title");
    member_action.put("chat", chat);
    member_action.put("message_id", 12345L);
    if ("new_chat_title".equals(action)) {
      member_action.put(action, "new title");
    } else {
      member_action.put(action, "test");
    }

    handler = new BotHandler();

    // when and then
    assertThat(handler.processMessage(member_action), isPresent());
  }

  @DisplayName("Ignore other bots' messages")
  @Test
  void viaBot() {
    // given
    JSONObject bot = new JSONObject();
    JSONObject botMessage = new JSONObject();

    bot.put("first_name", "Test Firstname");
    bot.put("id", 12345);
    bot.put("last_name", "Test Lastname");
    bot.put("username", "iamabot");
    botMessage.put("via_bot", bot);
    handler = new BotHandler();

    // when
    var responseEvent = handler.processMessage(botMessage);

    // then
    assertThat(responseEvent, isEmpty());
  }

}
