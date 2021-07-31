package org.adinor.db;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
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
  @Mock private PostRequest postRequest;

  private InMemoryStorage underTest;

  @Before
  public void beforeEach() {
    underTest = new InMemoryStorage(clock);
  }

  @Test
  public void canAddRule() {
    assertThat(underTest.addRule(ID, postRequest)).isTrue();
  }

  @Test
  public void failsToSecondAddRuleWithTheSameId() {
    assertThat(underTest.addRule(ID, postRequest)).isTrue();
    assertThat(underTest.addRule(ID, postRequest)).isFalse();
  }

  @Test
  public void removesExpiredAllowingReAdd() {
    when(postRequest.getTtlSeconds()).thenReturn(Optional.of(1));
    when(postRequest.getMaxRequests()).thenReturn(-1L);

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
    when(postRequest.getTtlSeconds()).thenReturn(Optional.empty());
    when(postRequest.getMaxRequests()).thenReturn(1L);

    when(clock.instant()).thenReturn(Instant.parse("2020-01-15T10:20:30Z"));

    assertThat(underTest.addRule(ID, postRequest)).isTrue();
    assertThat(underTest.getValue(ID)).isNotEmpty();
    assertThat(underTest.getValue(ID).get().get()).isNotEmpty();
    assertThat(underTest.getValue(ID).get().get()).isEmpty();

    assertThat(underTest.addRule(ID, postRequest)).isTrue();
  }
}
