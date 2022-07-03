/*
 * Copyright 2022 Witalij Berdinskich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *	   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gitlab.radio_rogal.tidy_chat.bot;

import static io.gitlab.radio_rogal.tidy_chat.TelegramUtils.deleteMessage;
import static io.gitlab.radio_rogal.tidy_chat.TelegramUtils.getChatId;
import static io.gitlab.radio_rogal.tidy_chat.TelegramUtils.getMessageId;
import static io.gitlab.radio_rogal.tidy_chat.TelegramUtils.isBotMessage;
import static java.util.Objects.isNull;
import static java.util.Optional.empty;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import io.gitlab.radio_rogal.tidy_chat.LambdaUtils;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import org.jetbrains.annotations.VisibleForTesting;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class BotHandler implements
    RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

  private static final String AWS_REQUEST_ID = "request#";
  private static final String FORWARDED_FOR = "X-Forwarded-For";
  private static final Collection<String> REMOVE_MESSAGES_WITH_KEYS = Set.of("new_chat_members",
      "left_chat_member", "new_chat_title", "new_chat_photo", "delete_chat_photo",
      "pinned_message");
  private static final int MAX_SUBSTRING_LENGTH = 1024;
  private static final String MESSAGE = "message";

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Override
  public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent,
      Context context) {
    MDC.put(AWS_REQUEST_ID, context.getAwsRequestId());

    Optional<APIGatewayProxyResponseEvent> responseEvent = empty();
    var requestEventBody = requestEvent.getBody();

    if (isNull(requestEventBody) || requestEventBody.isBlank()) {
      logger.info("Empty request from {}", requestEvent.getHeaders().get(FORWARDED_FOR));
    } else {
      try {
        var update = new JSONObject(requestEventBody);

        if (logger.isTraceEnabled()) {
          logger.trace(update.toString());
        }
        if (update.has(MESSAGE)) {
          responseEvent = processMessage(update.getJSONObject(MESSAGE));
        } else {
          logger.info("Unprocessed update: {}", update.keySet());
        }
      } catch (JSONException exception) {
        logger.warn("Wrong request from {}: {}\n{}", requestEvent.getHeaders().get(FORWARDED_FOR),
            exception.getMessage(), requestEventBody.substring(0,
                Math.min(requestEventBody.length(), MAX_SUBSTRING_LENGTH)));
      }
    }
    responseEvent.ifPresent(event -> logger.trace("Response: {}", event.getBody()));

    return responseEvent.orElseGet(LambdaUtils::responseOK);
  }

  @VisibleForTesting
  Optional<APIGatewayProxyResponseEvent> processMessage(JSONObject message) {
    logger.trace("Process message");

    Optional<APIGatewayProxyResponseEvent> responseEvent = empty();

    if (isBotMessage(message)) {
      logger.debug("Ignore message via another bot");
    } else if (message.keySet().stream().anyMatch(REMOVE_MESSAGES_WITH_KEYS::contains)) {
      var chatId = getChatId(message);
      var messageId = getMessageId(message);
      var responseBody = deleteMessage(chatId, messageId);

      logger.info("chat {}", chatId);

      responseEvent = Optional.of(LambdaUtils.getResponseEvent(responseBody));
    }

    return responseEvent;
  }

}
