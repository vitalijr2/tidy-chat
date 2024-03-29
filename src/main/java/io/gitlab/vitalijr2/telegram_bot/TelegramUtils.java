/*
 * Copyright 2022-2023 Vitalij Berdinskih
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
package io.gitlab.vitalijr2.telegram_bot;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class TelegramUtils {

  // Telegram field names
  private static final String CHAT = "chat";
  private static final String ID = "id";
  private static final String MESSAGE_ID = "message_id";
  private static final String NEW_CHAT_TITLE = "new_chat_title";
  private static final String TITLE = "title";
  private static final String VIA_BOT = "via_bot";
  // Telegram field values

  private TelegramUtils() {
  }

  /**
   * Get a chat
   */

  /**
   * Get a chat ID
   */
  public static long getChatId(@NotNull JSONObject message) {
    return getId(message.getJSONObject(CHAT));
  }

  public static String getChatTitle(@NotNull JSONObject message) {
    return getTitle(message.getJSONObject(CHAT));
  }

  /**
   * Get an ID
   */
  public static long getId(@NotNull JSONObject message) {
    return message.getLong(ID);
  }

  public static long getMessageId(@NotNull JSONObject message) {
    return message.getLong(MESSAGE_ID);
  }

  public static String getNewChatTitle(@NotNull JSONObject message) {
    return message.getString(NEW_CHAT_TITLE);
  }

  public static String getTitle(@NotNull JSONObject message) {
    return message.getString(TITLE);
  }

  public static boolean isBotMessage(JSONObject message) {
    return message.has(VIA_BOT);
  }

}
