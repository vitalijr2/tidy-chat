package uk.bot_by.tidy_chat.command;

import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresent;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.InvocationTargetException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@Tag("slow")
class KnownCommandTest {

  @DisplayName("Known commands")
  @ParameterizedTest
  @CsvSource(value = {"about,uk.bot_by.tidy_chat.command.AboutCommand",
      "start,uk.bot_by.tidy_chat.command.StartCommand"})
  void knownCommands(String name, Class<Command> clazz)
      throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
    // when
    Command command = clazz.getDeclaredConstructor().newInstance();

    // then
    assertAll("Known command: " + name,
        () -> assertEquals(name, command.getName(), "name"),
        () -> assertThat(command.call(), isPresent()));
  }

}
