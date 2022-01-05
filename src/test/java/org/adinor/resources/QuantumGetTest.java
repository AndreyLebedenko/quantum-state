package org.adinor.resources;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.adinor.api.GetResponse;
import org.adinor.db.Storage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

@RunWith(MockitoJUnitRunner.class)
public class QuantumGetTest {

  private static final String ID = "ID";

  @Mock private Storage storage;
  @Mock private AsyncResponse asyncResponse;

  private QuantumGet underTest;

  @Before
  public void before() {
    underTest = new QuantumGet(storage);
  }

  @Test
  public void canGet() {
    GetResponse expectedValue = GetResponse.builder().data("1").build();
    when(storage.getValue(ID)).thenReturn(Optional.of(() -> Optional.of(expectedValue.getData())));
    when(asyncResponse.resume(any(Response.class)))
        .then(
            (Answer<Boolean>)
                invocationOnMock -> {
                  Response response = invocationOnMock.getArgument(0);
                  assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
                  assertThat(response.getEntity()).isEqualTo(expectedValue);
                  return true;
                });

    underTest.get(asyncResponse, ID);

    verify(asyncResponse, times(1)).resume(any(Response.class));
  }

  @Test
  public void failsWhenNotFound() {
    when(storage.getValue(ID)).thenReturn(Optional.empty());
    when(asyncResponse.resume(any(Response.class)))
        .then(
            (Answer<Boolean>)
                invocationOnMock -> {
                  Response response = invocationOnMock.getArgument(0);
                  assertThat(response.getStatus()).isEqualTo(Status.NOT_FOUND.getStatusCode());
                  assertThat(response.getEntity()).isNull();
                  return true;
                });

    underTest.get(asyncResponse, ID);

    verify(asyncResponse, times(1)).resume(any(Response.class));
  }
}
