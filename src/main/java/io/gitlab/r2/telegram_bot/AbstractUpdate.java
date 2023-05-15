package io.gitlab.r2.telegram_bot;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractUpdate implements Update {

  protected final Logger logger = LoggerFactory.getLogger(getClass());

  protected static MessageBuilder messageBuilder() {
    return new MessageBuilder();
  }

  protected static class MessageBuilder {

    private static final String MESSAGE_FIELD_IS_NULL = "Message field cannot be null";

    private final Map<TelegramField, Object> fieldValues;

    MessageBuilder() {
      fieldValues = new HashMap<>();
    }

    public MessageBuilder add(TelegramField telegramField, Object value) {
      Objects.requireNonNull(telegramField, MESSAGE_FIELD_IS_NULL);

      fieldValues.put(telegramField, value);

      return this;
    }

    public String build() {
      return new JSONObject(fieldValues).toString();
    }

  }

}
