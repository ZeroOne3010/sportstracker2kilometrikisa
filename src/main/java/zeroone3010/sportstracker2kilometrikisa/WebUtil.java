package zeroone3010.sportstracker2kilometrikisa;

import jdk.incubator.http.HttpClient;
import jdk.incubator.http.HttpClient.Redirect;
import jdk.incubator.http.HttpRequest;

import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.util.Optional;

final class WebUtil {
  private static final String REFERER_HEADER = "Referer";
  private static final String CONTENT_TYPE_HEADER = "Content-Type";
  public static final String COOKIE_HEADER = "Cookie";
  public static final String CONNECTION_HEADER = "Connection";
  public static final String CONNECTION_KEEP_ALIVE = "keep-alive";
  public static final String X_REQUESTED_WITH_HEADER = "X-Requested-With";

  private WebUtil() {
    // Not instantiable
  }

  static HttpRequestBuilderWrapper buildPostRequest(final String uri, final String content) {
    return new HttpRequestBuilderWrapper(baseBuilder(uri)
        .version(HttpClient.Version.HTTP_1_1)
        .POST(HttpRequest.BodyPublisher.fromString(content))
    );
  }

  static HttpRequestBuilderWrapper buildGetRequest(final String uri) {
    return new HttpRequestBuilderWrapper(baseBuilder(uri).GET());
  }

  private static HttpRequest.Builder baseBuilder(String uri) {
    return HttpRequest.newBuilder(URI.create(uri));
  }

  static HttpClient client() {
    final String proxyHost = System.getenv("proxyHost");
    final ProxySelector proxy = Optional.ofNullable(proxyHost)
        .map(host -> ProxySelector.of(new InetSocketAddress(host, (int) Integer.valueOf(System.getenv("proxyPort")))))
        .orElse(ProxySelector.getDefault());
    return HttpClient.newBuilder()
        .proxy(proxy)
        .followRedirects(Redirect.NEVER)
        .build();
  }

  static class HttpRequestBuilderWrapper {
    private HttpRequest.Builder builder;

    HttpRequestBuilderWrapper(final HttpRequest.Builder builder) {
      this.builder = builder;
    }

    HttpRequestBuilderWrapper referer(final String referer) {
      this.builder.header(REFERER_HEADER, referer);
      return this;
    }

    HttpRequestBuilderWrapper contentType(final String contentType) {
      this.builder.header(CONTENT_TYPE_HEADER, contentType);
      return this;
    }

    HttpRequestBuilderWrapper header(final String key, final String value) {
      this.builder.header(key, value);
      return this;
    }

    HttpRequest build() {
      return builder.build();
    }
  }
}
