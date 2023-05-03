# micrometer bug reproduction project

When `jakarta.*` dependency isn't included in the project, the class `io.micrometer.core.instrument.binder.http.HttpRequestTags` does not compile when `TimeHandler`.

## using `HttpServletRequestTagsProvider` in a lambda

```java
var handler = new TimedHandler(registry, Tags.empty(),
        (request, response) -> Tags.of(
            Tag.of("path", request.getPathInfo()),
            HttpRequestTags.method(request),
            HttpRequestTags.status(response),
            HttpRequestTags.outcome(response)));
```

```shell 
❯ ./gradlew test

> Task :compileTestJava FAILED
/micrometer-bug/src/test/java/dev/example/test/TimedHandlerTest.java:66: error: cannot access HttpServletRequest
            HttpRequestTags.method(request),
                           ^
  class file for jakarta.servlet.http.HttpServletRequest not found
/micrometer-bug/src/test/java/dev/example/test/TimedHandlerTest.java:67: error: cannot access HttpServletResponse
            HttpRequestTags.status(response),
                           ^
  class file for jakarta.servlet.http.HttpServletResponse not found
/micrometer-bug/src/test/java/dev/example/test/TimedHandlerTest.java:68: error: cannot access HttpServletResponse
            HttpRequestTags.outcome(response)));
                           ^
  class file for jakarta.servlet.http.HttpServletResponse not found
3 errors

FAILURE: Build failed with an exception.

* What went wrong:
Execution failed for task ':compileTestJava'.
> Compilation failed; see the compiler error output for details.

* Try:
> Run with --stacktrace option to get the stack trace.
> Run with --info or --debug option to get more log output.
> Run with --scan to get full insights.

* Get more help at https://help.gradle.org

BUILD FAILED in 751ms
3 actionable tasks: 3 executed
```

## using `HttpServletRequestTagsProvider` in a custom class

```java
public class CustomHttpServletRequestTagsProvider implements HttpServletRequestTagsProvider {

  @Override
  public Iterable<Tag> getTags(HttpServletRequest request, HttpServletResponse response) {
    return Tags.of(Tag.of("path", request.getPathInfo()), HttpRequestTags.method(request),
        HttpRequestTags.status(response), HttpRequestTags.outcome(response));
  }
}
```

```shell
❯ ./gradlew test

> Task :compileJava FAILED
/micrometer-bug/src/main/java/dev/example/test/CustomHttpServletRequestTagsProvider.java:14: error: cannot access HttpServletRequest
    return Tags.of(Tag.of("path", request.getPathInfo()), HttpRequestTags.method(request),
                                                                         ^
  class file for jakarta.servlet.http.HttpServletRequest not found
/micrometer-bug/src/main/java/dev/example/test/CustomHttpServletRequestTagsProvider.java:15: error: cannot access HttpServletResponse
        HttpRequestTags.status(response), HttpRequestTags.outcome(response));
                       ^
  class file for jakarta.servlet.http.HttpServletResponse not found
/micrometer-bug/src/main/java/dev/example/test/CustomHttpServletRequestTagsProvider.java:15: error: cannot access HttpServletResponse
        HttpRequestTags.status(response), HttpRequestTags.outcome(response));
                                                         ^
  class file for jakarta.servlet.http.HttpServletResponse not found
3 errors

FAILURE: Build failed with an exception.

* What went wrong:
Execution failed for task ':compileJava'.
> Compilation failed; see the compiler error output for details.

* Try:
> Run with --stacktrace option to get the stack trace.
> Run with --info or --debug option to get more log output.
> Run with --scan to get full insights.

* Get more help at https://help.gradle.org

BUILD FAILED in 417ms
2 actionable tasks: 2 executed
```

## gradle dependencies

```shell

> Task :dependencies

------------------------------------------------------------
Root project 'micrometer-bug'
------------------------------------------------------------

annotationProcessor - Annotation processors and their dependencies for source set 'main'.
No dependencies

compileClasspath - Compile classpath for source set 'main'.
+--- javax.servlet:javax.servlet-api:4.0.1
+--- javax.ws.rs:javax.ws.rs-api:2.1.1
+--- org.eclipse.jetty:jetty-server:10.0.15
|    +--- org.eclipse.jetty.toolchain:jetty-servlet-api:4.0.6
|    +--- org.eclipse.jetty:jetty-http:10.0.15
|    |    +--- org.eclipse.jetty:jetty-util:10.0.15
|    |    |    \--- org.slf4j:slf4j-api:2.0.5
|    |    +--- org.eclipse.jetty:jetty-io:10.0.15
|    |    |    +--- org.slf4j:slf4j-api:2.0.5
|    |    |    \--- org.eclipse.jetty:jetty-util:10.0.15 (*)
|    |    \--- org.slf4j:slf4j-api:2.0.5
|    +--- org.eclipse.jetty:jetty-io:10.0.15 (*)
|    \--- org.slf4j:slf4j-api:2.0.5
\--- io.micrometer:micrometer-registry-statsd:1.10.6
     \--- io.micrometer:micrometer-core:1.10.6
          +--- io.micrometer:micrometer-commons:1.10.6
          \--- io.micrometer:micrometer-observation:1.10.6
               \--- io.micrometer:micrometer-commons:1.10.6

compileOnly - Compile only dependencies for source set 'main'. (n)
No dependencies

default - Configuration for default artifacts. (n)
No dependencies

implementation - Implementation only dependencies for source set 'main'. (n)
+--- javax.servlet:javax.servlet-api:4.0.1 (n)
+--- javax.ws.rs:javax.ws.rs-api:2.1.1 (n)
+--- org.eclipse.jetty:jetty-server:10.0.15 (n)
\--- io.micrometer:micrometer-registry-statsd:1.10.6 (n)

mainSourceElements - List of source directories contained in the Main SourceSet. (n)
No dependencies

runtimeClasspath - Runtime classpath of source set 'main'.
+--- javax.servlet:javax.servlet-api:4.0.1
+--- javax.ws.rs:javax.ws.rs-api:2.1.1
+--- org.eclipse.jetty:jetty-server:10.0.15
|    +--- org.eclipse.jetty.toolchain:jetty-servlet-api:4.0.6
|    +--- org.eclipse.jetty:jetty-http:10.0.15
|    |    +--- org.eclipse.jetty:jetty-util:10.0.15
|    |    |    \--- org.slf4j:slf4j-api:2.0.5
|    |    +--- org.eclipse.jetty:jetty-io:10.0.15
|    |    |    +--- org.slf4j:slf4j-api:2.0.5
|    |    |    \--- org.eclipse.jetty:jetty-util:10.0.15 (*)
|    |    \--- org.slf4j:slf4j-api:2.0.5
|    +--- org.eclipse.jetty:jetty-io:10.0.15 (*)
|    \--- org.slf4j:slf4j-api:2.0.5
\--- io.micrometer:micrometer-registry-statsd:1.10.6
     +--- io.micrometer:micrometer-core:1.10.6
     |    +--- io.micrometer:micrometer-commons:1.10.6
     |    +--- io.micrometer:micrometer-observation:1.10.6
     |    |    \--- io.micrometer:micrometer-commons:1.10.6
     |    +--- org.hdrhistogram:HdrHistogram:2.1.12
     |    \--- org.latencyutils:LatencyUtils:2.0.3
     \--- io.netty:netty-transport-native-epoll:4.1.91.Final
          +--- io.netty:netty-common:4.1.91.Final
          +--- io.netty:netty-buffer:4.1.91.Final
          |    \--- io.netty:netty-common:4.1.91.Final
          +--- io.netty:netty-transport:4.1.91.Final
          |    +--- io.netty:netty-common:4.1.91.Final
          |    +--- io.netty:netty-buffer:4.1.91.Final (*)
          |    \--- io.netty:netty-resolver:4.1.91.Final
          |         \--- io.netty:netty-common:4.1.91.Final
          +--- io.netty:netty-transport-native-unix-common:4.1.91.Final
          |    +--- io.netty:netty-common:4.1.91.Final
          |    +--- io.netty:netty-buffer:4.1.91.Final (*)
          |    \--- io.netty:netty-transport:4.1.91.Final (*)
          \--- io.netty:netty-transport-classes-epoll:4.1.91.Final
               +--- io.netty:netty-common:4.1.91.Final
               +--- io.netty:netty-buffer:4.1.91.Final (*)
               +--- io.netty:netty-transport:4.1.91.Final (*)
               \--- io.netty:netty-transport-native-unix-common:4.1.91.Final (*)

runtimeElements - Elements of runtime for main. (n)
No dependencies

runtimeOnly - Runtime only dependencies for source set 'main'. (n)
No dependencies

testAnnotationProcessor - Annotation processors and their dependencies for source set 'test'.
No dependencies

testCompileClasspath - Compile classpath for source set 'test'.
+--- javax.servlet:javax.servlet-api:4.0.1
+--- javax.ws.rs:javax.ws.rs-api:2.1.1
+--- org.eclipse.jetty:jetty-server:10.0.15
|    +--- org.eclipse.jetty.toolchain:jetty-servlet-api:4.0.6
|    +--- org.eclipse.jetty:jetty-http:10.0.15
|    |    +--- org.eclipse.jetty:jetty-util:10.0.15
|    |    |    \--- org.slf4j:slf4j-api:2.0.5
|    |    +--- org.eclipse.jetty:jetty-io:10.0.15
|    |    |    +--- org.slf4j:slf4j-api:2.0.5
|    |    |    \--- org.eclipse.jetty:jetty-util:10.0.15 (*)
|    |    \--- org.slf4j:slf4j-api:2.0.5
|    +--- org.eclipse.jetty:jetty-io:10.0.15 (*)
|    \--- org.slf4j:slf4j-api:2.0.5
+--- io.micrometer:micrometer-registry-statsd:1.10.6
|    \--- io.micrometer:micrometer-core:1.10.6
|         +--- io.micrometer:micrometer-commons:1.10.6
|         \--- io.micrometer:micrometer-observation:1.10.6
|              \--- io.micrometer:micrometer-commons:1.10.6
+--- org.junit:junit-bom:5.9.3
|    +--- org.junit.jupiter:junit-jupiter:5.9.3 (c)
|    +--- org.junit.jupiter:junit-jupiter-api:5.9.3 (c)
|    +--- org.junit.jupiter:junit-jupiter-params:5.9.3 (c)
|    \--- org.junit.platform:junit-platform-commons:1.9.3 (c)
+--- org.junit.jupiter:junit-jupiter -> 5.9.3
|    +--- org.junit:junit-bom:5.9.3 (*)
|    +--- org.junit.jupiter:junit-jupiter-api:5.9.3
|    |    +--- org.junit:junit-bom:5.9.3 (*)
|    |    +--- org.opentest4j:opentest4j:1.2.0
|    |    +--- org.junit.platform:junit-platform-commons:1.9.3
|    |    |    +--- org.junit:junit-bom:5.9.3 (*)
|    |    |    \--- org.apiguardian:apiguardian-api:1.1.2
|    |    \--- org.apiguardian:apiguardian-api:1.1.2
|    \--- org.junit.jupiter:junit-jupiter-params:5.9.3
|         +--- org.junit:junit-bom:5.9.3 (*)
|         +--- org.junit.jupiter:junit-jupiter-api:5.9.3 (*)
|         \--- org.apiguardian:apiguardian-api:1.1.2
\--- org.mockito:mockito-junit-jupiter:5.2.0
     \--- org.mockito:mockito-core:5.2.0
          +--- net.bytebuddy:byte-buddy:1.14.1
          \--- net.bytebuddy:byte-buddy-agent:1.14.1

testCompileOnly - Compile only dependencies for source set 'test'. (n)
No dependencies

testImplementation - Implementation only dependencies for source set 'test'. (n)
+--- org.junit:junit-bom:5.9.3 (n)
+--- org.junit.jupiter:junit-jupiter (n)
\--- org.mockito:mockito-junit-jupiter:5.2.0 (n)

testRuntimeClasspath - Runtime classpath of source set 'test'.
+--- javax.servlet:javax.servlet-api:4.0.1
+--- javax.ws.rs:javax.ws.rs-api:2.1.1
+--- org.eclipse.jetty:jetty-server:10.0.15
|    +--- org.eclipse.jetty.toolchain:jetty-servlet-api:4.0.6
|    +--- org.eclipse.jetty:jetty-http:10.0.15
|    |    +--- org.eclipse.jetty:jetty-util:10.0.15
|    |    |    \--- org.slf4j:slf4j-api:2.0.5
|    |    +--- org.eclipse.jetty:jetty-io:10.0.15
|    |    |    +--- org.slf4j:slf4j-api:2.0.5
|    |    |    \--- org.eclipse.jetty:jetty-util:10.0.15 (*)
|    |    \--- org.slf4j:slf4j-api:2.0.5
|    +--- org.eclipse.jetty:jetty-io:10.0.15 (*)
|    \--- org.slf4j:slf4j-api:2.0.5
+--- io.micrometer:micrometer-registry-statsd:1.10.6
|    +--- io.micrometer:micrometer-core:1.10.6
|    |    +--- io.micrometer:micrometer-commons:1.10.6
|    |    +--- io.micrometer:micrometer-observation:1.10.6
|    |    |    \--- io.micrometer:micrometer-commons:1.10.6
|    |    +--- org.hdrhistogram:HdrHistogram:2.1.12
|    |    \--- org.latencyutils:LatencyUtils:2.0.3
|    \--- io.netty:netty-transport-native-epoll:4.1.91.Final
|         +--- io.netty:netty-common:4.1.91.Final
|         +--- io.netty:netty-buffer:4.1.91.Final
|         |    \--- io.netty:netty-common:4.1.91.Final
|         +--- io.netty:netty-transport:4.1.91.Final
|         |    +--- io.netty:netty-common:4.1.91.Final
|         |    +--- io.netty:netty-buffer:4.1.91.Final (*)
|         |    \--- io.netty:netty-resolver:4.1.91.Final
|         |         \--- io.netty:netty-common:4.1.91.Final
|         +--- io.netty:netty-transport-native-unix-common:4.1.91.Final
|         |    +--- io.netty:netty-common:4.1.91.Final
|         |    +--- io.netty:netty-buffer:4.1.91.Final (*)
|         |    \--- io.netty:netty-transport:4.1.91.Final (*)
|         \--- io.netty:netty-transport-classes-epoll:4.1.91.Final
|              +--- io.netty:netty-common:4.1.91.Final
|              +--- io.netty:netty-buffer:4.1.91.Final (*)
|              +--- io.netty:netty-transport:4.1.91.Final (*)
|              \--- io.netty:netty-transport-native-unix-common:4.1.91.Final (*)
+--- org.junit:junit-bom:5.9.3
|    +--- org.junit.jupiter:junit-jupiter:5.9.3 (c)
|    +--- org.junit.jupiter:junit-jupiter-api:5.9.3 (c)
|    +--- org.junit.jupiter:junit-jupiter-params:5.9.3 (c)
|    +--- org.junit.jupiter:junit-jupiter-engine:5.9.3 (c)
|    +--- org.junit.platform:junit-platform-commons:1.9.3 (c)
|    \--- org.junit.platform:junit-platform-engine:1.9.3 (c)
+--- org.junit.jupiter:junit-jupiter -> 5.9.3
|    +--- org.junit:junit-bom:5.9.3 (*)
|    +--- org.junit.jupiter:junit-jupiter-api:5.9.3
|    |    +--- org.junit:junit-bom:5.9.3 (*)
|    |    +--- org.opentest4j:opentest4j:1.2.0
|    |    \--- org.junit.platform:junit-platform-commons:1.9.3
|    |         \--- org.junit:junit-bom:5.9.3 (*)
|    +--- org.junit.jupiter:junit-jupiter-params:5.9.3
|    |    +--- org.junit:junit-bom:5.9.3 (*)
|    |    \--- org.junit.jupiter:junit-jupiter-api:5.9.3 (*)
|    \--- org.junit.jupiter:junit-jupiter-engine:5.9.3
|         +--- org.junit:junit-bom:5.9.3 (*)
|         +--- org.junit.platform:junit-platform-engine:1.9.3
|         |    +--- org.junit:junit-bom:5.9.3 (*)
|         |    +--- org.opentest4j:opentest4j:1.2.0
|         |    \--- org.junit.platform:junit-platform-commons:1.9.3 (*)
|         \--- org.junit.jupiter:junit-jupiter-api:5.9.3 (*)
\--- org.mockito:mockito-junit-jupiter:5.2.0
     +--- org.mockito:mockito-core:5.2.0
     |    +--- net.bytebuddy:byte-buddy:1.14.1
     |    +--- net.bytebuddy:byte-buddy-agent:1.14.1
     |    \--- org.objenesis:objenesis:3.3
     \--- org.junit.jupiter:junit-jupiter-api:5.9.2 -> 5.9.3 (*)

testRuntimeOnly - Runtime only dependencies for source set 'test'. (n)
No dependencies

(c) - A dependency constraint, not a dependency. The dependency affected by the constraint occurs elsewhere in the tree.
(*) - Indicates repeated occurrences of a transitive dependency subtree. Gradle expands transitive dependency subtrees only once per project; repeat occurrences only display the root of the subtree, followed by this annotation.

(n) - A dependency or dependency configuration that cannot be resolved.

A web-based, searchable dependency report is available by adding the --scan option.

BUILD SUCCESSFUL in 420ms
1 actionable task: 1 executed

```
