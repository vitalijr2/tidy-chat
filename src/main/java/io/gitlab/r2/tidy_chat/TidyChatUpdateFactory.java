package io.gitlab.r2.tidy_chat;

import static io.gitlab.r2.telegram_bot.TelegramUtils.getChatId;
import static io.gitlab.r2.telegram_bot.TelegramUtils.getChatTitle;
import static io.gitlab.r2.telegram_bot.TelegramUtils.getMessageId;
import static io.gitlab.r2.telegram_bot.TelegramUtils.getNewChatTitle;
import static io.gitlab.r2.telegram_bot.TelegramUtils.isBotMessage;

import io.gitlab.r2.telegram_bot.AbstractUpdateFactory;
import io.gitlab.r2.telegram_bot.Update;
import io.gitlab.r2.tidy_chat.updates.DeleteMessage;
import java.util.Collection;
import java.util.Set;
import org.json.JSONObject;

public class TidyChatUpdateFactory extends AbstractUpdateFactory {

  private static final String NEW_CHAT_TITLE = "new_chat_title";
  private static final Collection<String> REMOVE_MESSAGES_WITH_KEYS = Set.of("new_chat_members",
      "left_chat_member", NEW_CHAT_TITLE, "new_chat_photo", "delete_chat_photo", "pinned_message");
  private static final String RIGHT_ARROW = " -> ";

  @Override
  protected Update processInlineQuery(JSONObject message) {
    throw new UnsupportedOperationException("Inline query is not supported");
  }

  @Override
  protected Update processMessage(JSONObject message) {
    logger.trace("Process message");

    Update update = null;

    if (isBotMessage(message)) {
      logger.debug("Ignore message via another bot");
    } else {
      var chatId = getChatId(message);
      var messageId = getMessageId(message);
      var title = getChatTitle(message);

      var operationBeingDeleted = message.keySet().stream()
          .filter(REMOVE_MESSAGES_WITH_KEYS::contains).findAny();

      if (operationBeingDeleted.isPresent()) {
        if (operationBeingDeleted.get().equals(NEW_CHAT_TITLE)) {
          title = title + RIGHT_ARROW + getNewChatTitle(message);
        }
        update = new DeleteMessage(chatId, messageId, operationBeingDeleted.get(), title);
      }
    }

    return update;
  }

}
