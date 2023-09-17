package io.gitlab.vitalijr2.tidy_chat.updates;

import io.gitlab.vitalijr2.telegram_bot.AbstractUpdate;
import io.gitlab.vitalijr2.telegram_bot.TelegramField;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class DeleteMessage extends AbstractUpdate {

  private static final String DELETE_MESSAGE = "deleteMessage";

  private static final Marker REMOVE = MarkerFactory.getMarker("remove");

  private final long chatId;
  private final long messageId;
  private final String cause;
  private final String title;

  public DeleteMessage(long chatId, long messageId, String cause, String title) {
    this.chatId = chatId;
    this.messageId = messageId;
    this.cause = cause;
    this.title = title;
  }

  @Override
  public String call() {
    logger.info(REMOVE, "remove message in the chat {}/{}: {}", chatId, title, cause);

    return messageBuilder().add(TelegramField.Method, DELETE_MESSAGE)
        .add(TelegramField.ChatID, chatId).add(TelegramField.MessageId, messageId).build();
  }

}
