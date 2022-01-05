package org.adinor.db;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import org.adinor.api.ImmutablePostRequest;
import org.adinor.api.PostRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class InMemoryStorageTest {

  private static final String ID = "ID";

  @Mock private Clock clock;
  private PostRequest postRequest;

  private InMemoryStorage underTest;

  @Before
  public void beforeEach() {
    underTest = new InMemoryStorage(clock);
  }

  @Test
  public void canAddRule_Long() {
    long expectedValue = 12345L;
    postRequest =
        ImmutablePostRequest.builder().id("123").initialValue(""+expectedValue).build();
    assertThat(underTest.addRule(ID, postRequest)).isTrue();
    assertThat((underTest.getValue(ID).get().get())).contains(String.valueOf(expectedValue+1));
  }

  @Test
  public void canAddRule_String() {
    String expectedValue = "abcdefghijklmnopqrst";
    postRequest =
        ImmutablePostRequest.builder().id("123").initialValue(expectedValue).build();
    assertThat(underTest.addRule(ID, postRequest)).isTrue();
    assertThat(underTest.getValue(ID).get().get()).contains(expectedValue);
  }

  @Test
  public void failsToSecondAddRuleWithTheSameId() {
    postRequest = ImmutablePostRequest.builder().id("123").build();
    assertThat(underTest.addRule(ID, postRequest)).isTrue();
    assertThat(underTest.addRule(ID, postRequest)).isFalse();
  }

  @Test
  public void removesExpiredAllowingReAdd() {
    postRequest = ImmutablePostRequest.builder().id("123").ttlSeconds(1).build();

    when(clock.instant())
        .thenReturn(Instant.parse("2020-01-15T10:20:30Z"))
        .thenReturn(Instant.parse("2020-01-15T10:20:30Z"))
        .thenReturn(Instant.parse("2020-01-15T10:20:32Z"));

    assertThat(underTest.addRule(ID, postRequest)).isTrue();
    assertThat(underTest.getValue(ID)).isNotEmpty();
    assertThat(underTest.getValue(ID).get().get()).isNotEmpty();
    assertThat(underTest.getValue(ID).get().get()).isEmpty();

    assertThat(underTest.addRule(ID, postRequest)).isTrue();
  }

  @Test
  public void removesWhenCounterReachedAllowingReAdd() {
    postRequest = ImmutablePostRequest.builder().id("123").maxRequests(1).build();

    when(clock.instant()).thenReturn(Instant.parse("2020-01-15T10:20:30Z"));

    assertThat(underTest.addRule(ID, postRequest)).isTrue();
    assertThat(underTest.getValue(ID)).isNotEmpty();
    assertThat(underTest.getValue(ID).get()).isNotNull();
    assertThat(underTest.getValue(ID).get().get()).isNotEmpty();
    assertThat(underTest.getValue(ID).get().get()).isEmpty();

    assertThat(underTest.addRule(ID, postRequest)).isTrue();
  }
}
