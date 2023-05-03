package dev.example.test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.micrometer.core.instrument.MockClock;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.http.HttpRequestTags;
import io.micrometer.core.instrument.binder.jetty.TimedHandler;
import io.micrometer.core.instrument.simple.SimpleConfig;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
   * > Task :compileTestJava FAILED
   * /micrometer-bug/src/test/java/dev/example/test/TimedHandlerTest.java:66: error: cannot access HttpServletRequest
   *             HttpRequestTags.method(request),
   *                            ^
   *   class file for jakarta.servlet.http.HttpServletRequest not found
   * /micrometer-bug/src/test/java/dev/example/test/TimedHandlerTest.java:67: error: cannot access HttpServletResponse
   *             HttpRequestTags.status(response),
   *                            ^
   *   class file for jakarta.servlet.http.HttpServletResponse not found
   * /micrometer-bug/src/test/java/dev/example/test/TimedHandlerTest.java:68: error: cannot access HttpServletResponse
   *             HttpRequestTags.outcome(response)));
   *                            ^
   *   class file for jakarta.servlet.http.HttpServletResponse not found
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
   * /micrometer-bug/src/main/java/dev/example/test/CustomHttpServletRequestTagsProvider.java:14: error: cannot access HttpServletRequest
   *     return Tags.of(Tag.of("path", request.getPathInfo()), HttpRequestTags.method(request),
   *                                                                          ^
   *   class file for jakarta.servlet.http.HttpServletRequest not found
   * /micrometer-bug/src/main/java/dev/example/test/CustomHttpServletRequestTagsProvider.java:15: error: cannot access HttpServletResponse
   *         HttpRequestTags.status(response), HttpRequestTags.outcome(response));
   *                        ^
   *   class file for jakarta.servlet.http.HttpServletResponse not found
   * /micrometer-bug/src/main/java/dev/example/test/CustomHttpServletRequestTagsProvider.java:15: error: cannot access HttpServletResponse
   *         HttpRequestTags.status(response), HttpRequestTags.outcome(response));
   *                                                          ^
   *   class file for jakarta.servlet.http.HttpServletResponse not found
   */
  @Test
  void customProviderClass() throws Exception {
    var registry = new SimpleMeterRegistry(SimpleConfig.DEFAULT, new MockClock());
    var handler = new TimedHandler(registry, Tags.empty(),
        new CustomHttpServletRequestTagsProvider());

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
