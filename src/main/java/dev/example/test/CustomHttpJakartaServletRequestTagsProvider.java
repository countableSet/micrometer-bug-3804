package dev.example.test;

import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.http.HttpJakartaServletRequestTagsProvider;
import io.micrometer.core.instrument.binder.http.HttpRequestTags;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomHttpJakartaServletRequestTagsProvider implements
    HttpJakartaServletRequestTagsProvider {

  @Override
  public Iterable<Tag> getTags(HttpServletRequest request, HttpServletResponse response) {
    return Tags.of(
        Tag.of("path", request.getPathInfo()),
        HttpRequestTags.method(request),
        HttpRequestTags.status(response),
        HttpRequestTags.outcome(response));
  }
}
