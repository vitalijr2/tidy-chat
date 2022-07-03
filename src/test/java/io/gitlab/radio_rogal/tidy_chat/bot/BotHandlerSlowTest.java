package io.gitlab.radio_rogal.tidy_chat.bot;

import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresent;
import static org.hamcrest.MatcherAssert.assertThat;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
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
  @ValueSource(strings = {"new_chat_members", "left_chat_member", "pinned_message",
      "new_chat_title", "new_chat_photo", "delete_chat_photo"})
  void removeJoinOrLeftMessages(String action) {
    // given
    JSONObject chat = new JSONObject();
    JSONObject member_action = new JSONObject();

    chat.put("id", 9876543210L);
    member_action.put("chat", chat);
    member_action.put("message_id", 12345L);
    member_action.put(action, "test");
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
