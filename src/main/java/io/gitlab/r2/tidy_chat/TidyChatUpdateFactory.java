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
      var operationBeingDeleted = message.keySet().stream()
          .filter(REMOVE_MESSAGES_WITH_KEYS::contains).findAny();

      if (operationBeingDeleted.isPresent()) {
        var chatId = getChatId(message);
        var messageId = getMessageId(message);
        var title = (NEW_CHAT_TITLE.equals(operationBeingDeleted.get())) ? getNewChatTitle(message)
            : getChatTitle(message);

        update = new DeleteMessage(chatId, messageId, operationBeingDeleted.get(), title);
      }
    }
    return update;
  }

}
