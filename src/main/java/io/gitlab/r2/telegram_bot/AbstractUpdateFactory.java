/*
 * Copyright 2023 Witalij Berdinskich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gitlab.r2.telegram_bot;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractUpdateFactory implements UpdateFactory {

  private static final String INLINE_QUERY = "inline_query";
  private static final String MESSAGE = "message";
  private static final int MAX_SUBSTRING_LENGTH = 1024;
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  protected abstract Update processInlineQuery(JSONObject message);
  protected abstract Update processMessage(JSONObject message);

  public @Nullable Update parseUpdate(@NotNull String updateText) {
    Update update = null;

    try {
      var body = new JSONObject(updateText);

      if (logger.isTraceEnabled()) {
        logger.trace(body.toString());
      }
      if (body.has(INLINE_QUERY)) {
        update = processInlineQuery(body.getJSONObject(INLINE_QUERY));
      } else if (body.has(MESSAGE)) {
        update = processMessage(body.getJSONObject(MESSAGE));
      } else {
        logger.info("Unprocessed update: {}", body.keySet());
      }
    } catch (JSONException exception) {
      logger.warn("Wrong update: {}\n{}", exception.getMessage(),
          updateText.substring(0, Math.min(updateText.length(), MAX_SUBSTRING_LENGTH)));
    }

    return update;
  }

}
