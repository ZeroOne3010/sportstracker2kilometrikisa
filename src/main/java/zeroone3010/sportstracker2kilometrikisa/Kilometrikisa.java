package zeroone3010.sportstracker2kilometrikisa;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static zeroone3010.sportstracker2kilometrikisa.WebUtil.COOKIE_HEADER;
import static zeroone3010.sportstracker2kilometrikisa.WebUtil.X_REQUESTED_WITH_HEADER;

public class Kilometrikisa {
  private static final Logger logger = System.getLogger("Kilometrikisa");

  private static final String PROTOCOL = "https";
  private static final String HOST = "www.kilometrikisa.fi";
  private static final String BASE_URL = PROTOCOL + "://" + HOST;
  private static final String LOGIN_PAGE_URL = BASE_URL + "/accounts/login/";
  private static final String LOGIN_URL = LOGIN_PAGE_URL;
  private static final String LOGIN_CONTENT_TYPE = "application/x-www-form-urlencoded";
  private static final String KILOMETER_CALENDAR_URL = BASE_URL + "/contest/log/";
  private static final String POST_KILOMETERS_URL = BASE_URL + "/contest/log-save/";
  private static final String POST_MINUTES_URL = BASE_URL + "/contest/minute-log-save/";
  private static final String POST_ENTRY_CONTENT_TYPE = "application/x-www-form-urlencoded; charset=UTF-8";

  private final String user;
  private final String password;
  private final HttpClient client;

  public Kilometrikisa(final String user, final String password) {
    this.user = user;
    this.password = password;
    this.client = WebUtil.client();
  }

  public void post(final Workout workout) throws IOException, InterruptedException {
    Objects.requireNonNull(user, "Kilometrikisa username must not be null.");
    Objects.requireNonNull(password, "Kilometrikisa password must not be null.");

    logger.log(Logger.Level.INFO, "Logging in with Kilometrikisa user " + user);
    if (workout == null) {
      logger.log(Level.INFO, "Workout is null, doing nothing.");
      return;
    }

    final HttpRequest loginPageRequest = WebUtil.buildGetRequest(LOGIN_PAGE_URL)
        .build();
    final HttpResponse<String> loginPageResponse = client.send(loginPageRequest, BodyHandlers.ofString());
    logger.log(Level.DEBUG, loginPageResponse.headers());
    logger.log(Level.DEBUG, loginPageResponse.body());
    String token = extractCsrfToken(loginPageResponse);
    logger.log(Level.TRACE, token);

    final String loginData = "username=" + user + "&password=" + password + "&csrfmiddlewaretoken=" + token + "&next=";
    final HttpRequest loginRequest = WebUtil.buildPostRequest(LOGIN_URL, loginData)
        .referer(LOGIN_PAGE_URL)
        .contentType(LOGIN_CONTENT_TYPE)
        .header(COOKIE_HEADER, "csrftoken=" + token)
        .build();
    final HttpResponse<String> loginResponse = client.send(loginRequest, BodyHandlers.ofString());
    logger.log(Level.DEBUG, "Login response headers: " + loginResponse.headers().map());
    logger.log(Level.DEBUG, "Login response body: " + loginResponse.body());
    final String sessionId = extractCookie(loginResponse, "sessionid");
    token = extractCookie(loginResponse, "csrftoken");

    postKilometers(workout, token, sessionId);
    postMinutes(workout, token, sessionId);
  }

  private void postMinutes(final Workout workout, final String token, final String sessionId) throws IOException, InterruptedException {
    final String minuteMessage = convertToPostableMinutes(workout, token);
    final HttpRequest minuteLogPostRequest = WebUtil.buildPostRequest(POST_MINUTES_URL, minuteMessage)
        .referer(KILOMETER_CALENDAR_URL)
        .contentType(POST_ENTRY_CONTENT_TYPE)
        .header(COOKIE_HEADER, "csrftoken=" + token + "; sessionid=" + sessionId)
        .header(X_REQUESTED_WITH_HEADER, "XMLHttpRequest")
        .build();
    logger.log(Level.DEBUG, minuteMessage);
    final HttpResponse<String> minutePostResponse = client.send(minuteLogPostRequest, BodyHandlers.ofString());
    logger.log(Level.DEBUG, "Post response body: " + minutePostResponse.body());

    if (minutePostResponse.statusCode() != 200) {
      throw new RuntimeException("Storing minutes failed! '" + minuteMessage + "' resulted in '" + minutePostResponse.body() + "'");
    }
    logger.log(Logger.Level.INFO, "Minutes ("
        + workout.getDuration().toHours() + ":"
        + workout.getDuration().toMinutesPart() + ") posted successfully");
  }

  private void postKilometers(final Workout workout, final String token, final String sessionId) throws IOException, InterruptedException {
    final String kilometerMessage = convertToPostableKilometers(workout, token);
    final HttpRequest trainingLogPostRequest = WebUtil.buildPostRequest(POST_KILOMETERS_URL, kilometerMessage)
        .referer(KILOMETER_CALENDAR_URL)
        .contentType(POST_ENTRY_CONTENT_TYPE)
        .header(COOKIE_HEADER, "csrftoken=" + token + "; sessionid=" + sessionId)
        .header(X_REQUESTED_WITH_HEADER, "XMLHttpRequest")
        .build();
    logger.log(Level.DEBUG, kilometerMessage);
    final HttpResponse<String> postResponse = client.send(trainingLogPostRequest, BodyHandlers.ofString());
    logger.log(Level.DEBUG, "Post response body: " + postResponse.body());

    if (postResponse.statusCode() != 200) {
      throw new RuntimeException("Storing kilometers failed! '" + kilometerMessage + "' resulted in '" + postResponse.body() + "'");
    }
    logger.log(Logger.Level.INFO, "Kilometers (" + workout.getTotalDistanceInKilometers() + ") posted successfully");
  }

  private String convertToPostableKilometers(final Workout workout, final String csrfToken) {
    return "contest_id=38&" +
        "km_amount=" + workout.getTotalDistanceInKilometers() + "&" +
        "km_date=" + workout.getDate() + "&" +
        "csrfmiddlewaretoken=" + csrfToken;
  }

  private String convertToPostableMinutes(final Workout workout, final String csrfToken) {
    return "contest_id=38&" +
        "hours=" + workout.getDuration().toHours() + "&" +
        "minutes=" + workout.getDuration().toMinutesPart() + "&" +
        "date=" + workout.getDate() + "&" +
        "csrfmiddlewaretoken=" + csrfToken;
  }

  private String extractCsrfToken(final HttpResponse<String> response) {
    final Pattern tokenPattern = Pattern.compile(".*<input type='hidden' name='csrfmiddlewaretoken' value='([^']+)' />.*", Pattern.DOTALL);
    final String body = response.body();
    final Matcher matcher = tokenPattern.matcher(body);
    assert matcher.matches();
    final String token = Optional.ofNullable(matcher.group(1))
        .orElseThrow(() -> new IllegalStateException("Could not extract the token"));
    logger.log(Level.TRACE, String.format("Extracted token: '%s'.", token));
    return token;
  }

  private String extractCookie(final HttpResponse<String> response, final String cookieName) {
    final Pattern cookiePattern = Pattern.compile(cookieName + "=([^;]+);.*");
    final String cookie = response.headers()
        .allValues("Set-Cookie")
        .stream()
        .map(cookiePattern::matcher)
        .filter(Matcher::matches)
        .map(c -> c.group(1))
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("Could not extract the cookie"));
    logger.log(Level.TRACE, String.format("Extracted cookie: '%s'.", cookie));
    return cookie;
  }
}
