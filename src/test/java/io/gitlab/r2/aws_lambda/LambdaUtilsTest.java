package io.gitlab.r2.aws_lambda;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@Tag("fast")
class LambdaUtilsTest {

  @DisplayName("Returns HTTP 200, application/json")
  @ParameterizedTest
  @EmptySource
  @ValueSource(strings = "{ 'test': 'passed' }")
  public void getResponseEvent(String body) {
    // when
    var responseEvent = LambdaUtils.getResponseEvent(body);

    // then
    assertAll("Return HTTP 200 with correct content type and not empty body",
        () -> assertEquals(200, responseEvent.getStatusCode(), "HTTP status code"),
        () -> assertThat("Content type", responseEvent.getHeaders(),
            hasEntry("Content-Type", "application/json")),
        () -> assertEquals(body, responseEvent.getBody(), "Response body"));
  }

  @DisplayName("Returns HTTP 200, just OK")
  @Test
  public void responseOK() {
    // when
    var responseEvent = LambdaUtils.responseOK();

    // then
    assertAll("Return HTTP 200 with correct content type and not empty body",
        () -> assertEquals(200, responseEvent.getStatusCode(), "HTTP status code"),
        () -> assertThat("Content type", responseEvent.getHeaders(),
            hasEntry("Content-Type", "text/plain")),
        () -> assertEquals("OK", responseEvent.getBody(), "Response body"));
  }

}
