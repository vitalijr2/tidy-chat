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
package uk.bot_by.tidy_chat.bot;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class BotCommand {

  private final String command;
  private final List<String> arguments;

  private BotCommand(String command, List<String> arguments) {
    this.command = command;
    this.arguments = List.copyOf(arguments);
  }

  public static BotCommandBuilder builder() {
    return new BotCommandBuilder();
  }

  public List<String> getArguments() {
    return arguments;
  }

  public String getCommand() {
    return command;
  }

  public static class BotCommandBuilder {

    private final List<String> arguments = new ArrayList<>();
    private String command;

    private BotCommandBuilder() {
    }

    public BotCommand build() {
      return new BotCommand(requireNonNull(command, "Command cannot be null"), arguments);
    }

    public void argument(@NotNull String argument) {
      arguments.add(argument);
    }

    public void command(@NotNull String command) {
      this.command = command;
    }

  }

}
