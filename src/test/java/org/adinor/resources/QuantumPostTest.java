package org.adinor.resources;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.adinor.api.ImmutablePostResponse;
import org.adinor.api.PostRequest;
import org.adinor.api.PostResponse;
import org.adinor.core.SerialFactory;
import org.adinor.db.Storage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class QuantumPostTest {

  private static final String ID = "ID";

  @Mock private Storage storage;
  @Mock private SerialFactory serialFactory;
  @Mock private AsyncResponse asyncResponse;
  @Mock private PostRequest postRequest;
  @Captor private ArgumentCaptor<Response> responseCaptor;

  private QuantumPost underTest;

  @Before
  public void before() {
    underTest = new QuantumPost(storage, serialFactory);
  }

  @Test
  public void canAdd_EmptyRequest() {
    when(postRequest.getId()).thenReturn(Optional.empty());
    when(serialFactory.produce()).thenReturn(ID);
    when(storage.addRule(ID, postRequest)).thenReturn(true);

    ImmutablePostResponse expectedResponse = PostResponse.builder().id(ID).build();
    underTest.add(asyncResponse, postRequest);

    verify(asyncResponse, times(1)).resume(responseCaptor.capture());
    verify(serialFactory, times(1)).produce();
    assertThat(responseCaptor.getValue().getStatus()).isEqualTo(200);
    assertThat(responseCaptor.getValue().getEntity()).isEqualTo(expectedResponse);
  }

  @Test
  public void canAdd_RequestWithId() {
    when(postRequest.getId()).thenReturn(Optional.of(ID));
    when(storage.addRule(ID, postRequest)).thenReturn(true);

    ImmutablePostResponse expectedResponse = PostResponse.builder().id(ID).build();
    underTest.add(asyncResponse, postRequest);

    verify(serialFactory, never()).produce();
    verify(asyncResponse, times(1)).resume(responseCaptor.capture());
    assertThat(responseCaptor.getValue().getStatus()).isEqualTo(200);
    assertThat(responseCaptor.getValue().getEntity()).isEqualTo(expectedResponse);
  }

  @Test
  public void failsWithConflictIfStorageCanNotAdd() {
    when(postRequest.getId()).thenReturn(Optional.of(ID));
    when(storage.addRule(ID, postRequest)).thenReturn(false);

    underTest.add(asyncResponse, postRequest);

    verify(serialFactory, never()).produce();
    verify(asyncResponse, times(1)).resume(responseCaptor.capture());
    assertThat(responseCaptor.getValue().getStatus()).isEqualTo(Status.CONFLICT.getStatusCode());
  }
}
