package dev.example.test;

import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.http.HttpRequestTags;
import io.micrometer.core.instrument.binder.http.HttpServletRequestTagsProvider;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CustomHttpServletRequestTagsProvider implements HttpServletRequestTagsProvider {

  @Override
  public Iterable<Tag> getTags(HttpServletRequest request, HttpServletResponse response) {
    return Tags.of(Tag.of("path", request.getPathInfo()), HttpRequestTags.method(request),
        HttpRequestTags.status(response), HttpRequestTags.outcome(response));
  }
}
