package uk.bot_by.tidy_chat.bot;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Objects;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import uk.bot_by.tidy_chat.bot.BotCommand.BotCommandBuilder;

@Tag("fast")
class BotCommandTest {

  @BeforeEach
  void setUp() {
  }

  @DisplayName("Build a bot command with arguments")
  @Test
  void buildWithArguments() {
    // given
    BotCommandBuilder builder = BotCommand.builder();

    builder.command("qwerty");
    builder.argument("first");
    builder.argument("second");
    builder.argument("third");

    // when
    BotCommand command = assertDoesNotThrow(builder::build);

    // then
    assertAll("Bot command",
        () -> assertNotNull(command, "command"),
        () -> assertEquals("qwerty", command.getCommand()),
        () -> assertThat(command.getArguments(), contains("first", "second", "third")));
  }

  @DisplayName("Built a bot command")
  @Test
  void build() {
    // given
    BotCommandBuilder builder = BotCommand.builder();

    builder.command("qwerty");

    // when
    BotCommand command = assertDoesNotThrow(builder::build);

    // then
    assertAll("Bot command",
        () -> assertNotNull(command, "command"),
        () -> assertEquals("qwerty", command.getCommand()),
        () -> assertThat(command.getArguments(), empty()));
  }

  @DisplayName("Command is required")
  @ParameterizedTest
  @CsvSource(value = {"N/A,N/A,N/A", "first,second,third", "first,second,N/A",
      "first,N/A,N/A"}, nullValues = "N/A")
  void getCommand(String first, String second, String third) {
    // given
    BotCommandBuilder builder = BotCommand.builder();

    Stream.of(first, second, third).filter(Objects::nonNull).forEach(builder::argument);

    // when
    Exception exception = assertThrows(NullPointerException.class, builder::build);

    // then
    assertEquals("Command cannot be null", exception.getMessage());
  }

}
