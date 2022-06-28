package uk.bot_by.tidy_chat.command;

import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresentAndIs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("fast")
class StaticTextCommandTest {

  private StaticTextCommand command;

  @BeforeEach
  void setUp() {
    command = new StaticTextCommand("test");
  }

  @Test
  void name() {
    // when and then
    assertEquals("test", command.getName());
  }

  @Test
  void call() {
    // when and then
    assertThat("call", command.call(), isPresentAndIs("_Test passed_\n"));
  }

  @Test
  void readStaticResponse() {
    // when and then
    assertEquals("_Test passed_\n", command.readStaticResponse("test"));
  }

  @Test
  void resourceNotFound() {
    // when
    Exception exception = assertThrows(RuntimeException.class,
        () -> command.readStaticResponse("test-not-found"));

    // then
    assertAll("Resource not found",
        () -> assertEquals("Cannot read response text for test-not-found", exception.getMessage()),
        () -> assertTrue(exception.getCause() instanceof NullPointerException));
  }

}
