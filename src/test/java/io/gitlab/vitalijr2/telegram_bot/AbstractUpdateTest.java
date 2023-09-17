package io.gitlab.vitalijr2.telegram_bot;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("fast")
class AbstractUpdateTest {

  @DisplayName("Returns an instance of")
  @Test
  void messageBuilder() {
    // when and then
    assertNotNull(assertDoesNotThrow(AbstractUpdate::messageBuilder, "create new one"), "not null");
  }

  @DisplayName("Empty message builder builds an empty object")
  @Test
  void emptyObject() {
    // given
    var messageBuilder = AbstractUpdate.messageBuilder();

    // when and then
    assertEquals("{}", messageBuilder.build(), true);
  }

  @DisplayName("Adding fields")
  @Test
  void addingFields() {
    // given
    var messageBuilder = AbstractUpdate.messageBuilder();

    // when
    messageBuilder.add(TelegramField.ChatID, "123");
    messageBuilder.add(TelegramField.OneTimeKeyboard, true);
    var message = messageBuilder.build();

    // then
    assertEquals("{\"one_time_keyboard\": true, \"chat_id\": \"123\" }", message, true);
  }

}