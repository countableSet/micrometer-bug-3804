package dev.example.test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.micrometer.core.instrument.MockClock;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.http.HttpRequestTags;
import io.micrometer.core.instrument.simple.SimpleConfig;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micrometer.jetty11.TimedHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.HttpChannelState;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.junit.jupiter.api.Test;

class TimedHandlerTest {

  @Test
  void defaultTagsProvider() throws Exception {
    var registry = new SimpleMeterRegistry(SimpleConfig.DEFAULT, new MockClock());
    var handler = new TimedHandler(registry, Tags.empty());

    Request baseRequest = mock(Request.class);
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    HttpChannelState state = mock(HttpChannelState.class);
    Response baseResponse = mock(Response.class);

    when(baseRequest.getHttpChannelState()).thenReturn(state);
    when(state.isInitial()).thenReturn(true);
    when(baseRequest.getResponse()).thenReturn(baseResponse);
    when(baseResponse.isCommitted()).thenReturn(true);

    when(request.getMethod()).thenReturn("get");
    when(response.getStatus()).thenReturn(200);

    handler.handle("/path", baseRequest, request, response);
  }

  /**
   * Fails to compile with error:
   * TimedHandlerTest.java:55: error: cannot access HttpServletRequest
   *             HttpRequestTags.method(request),
   *                            ^
   *   class file for javax.servlet.http.HttpServletRequest not found
   * TimedHandlerTest.java:56: error: cannot access HttpServletResponse
   *             HttpRequestTags.status(response),
   *                            ^
   *   class file for javax.servlet.http.HttpServletResponse not found
   * TimedHandlerTest.java:57: error: cannot access HttpServletResponse
   *             HttpRequestTags.outcome(response)));
   *                            ^
   *   class file for javax.servlet.http.HttpServletResponse not found
   */
  @Test
  void customProviderLambda() throws Exception {
    var registry = new SimpleMeterRegistry(SimpleConfig.DEFAULT, new MockClock());
    var handler = new TimedHandler(registry, Tags.empty(),
        (request, response) -> Tags.of(
            Tag.of("path", request.getPathInfo()),
            HttpRequestTags.method(request),
            HttpRequestTags.status(response),
            HttpRequestTags.outcome(response)));

    Request baseRequest = mock(Request.class);
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    HttpChannelState state = mock(HttpChannelState.class);
    Response baseResponse = mock(Response.class);

    when(baseRequest.getHttpChannelState()).thenReturn(state);
    when(state.isInitial()).thenReturn(true);
    when(baseRequest.getResponse()).thenReturn(baseResponse);
    when(baseResponse.isCommitted()).thenReturn(true);

    when(request.getMethod()).thenReturn("get");
    when(response.getStatus()).thenReturn(200);

    handler.handle("/path", baseRequest, request, response);
  }

  /**
   * Fails to compile with error:
   * CustomHttpJakartaServletRequestTagsProvider.java:17: error: cannot access HttpServletRequest
   *         HttpRequestTags.method(request),
   *                        ^
   *   class file for javax.servlet.http.HttpServletRequest not found
   * CustomHttpJakartaServletRequestTagsProvider.java:18: error: cannot access HttpServletResponse
   *         HttpRequestTags.status(response),
   *                        ^
   *   class file for javax.servlet.http.HttpServletResponse not found
   * CustomHttpJakartaServletRequestTagsProvider.java:19: error: cannot access HttpServletResponse
   *         HttpRequestTags.outcome(response));
   *                        ^
   *   class file for javax.servlet.http.HttpServletResponse not found
   */
  @Test
  void customProviderClass() throws Exception {
    var registry = new SimpleMeterRegistry(SimpleConfig.DEFAULT, new MockClock());
    var handler = new TimedHandler(registry, Tags.empty(),
        new CustomHttpJakartaServletRequestTagsProvider());

    Request baseRequest = mock(Request.class);
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    HttpChannelState state = mock(HttpChannelState.class);
    Response baseResponse = mock(Response.class);

    when(baseRequest.getHttpChannelState()).thenReturn(state);
    when(state.isInitial()).thenReturn(true);
    when(baseRequest.getResponse()).thenReturn(baseResponse);
    when(baseResponse.isCommitted()).thenReturn(true);

    when(request.getMethod()).thenReturn("get");
    when(response.getStatus()).thenReturn(200);

    handler.handle("/path", baseRequest, request, response);
  }
}
