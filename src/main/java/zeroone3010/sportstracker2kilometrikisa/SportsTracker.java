package zeroone3010.sportstracker2kilometrikisa;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;
import java.util.regex.Pattern;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

public final class SportsTracker {
  private static final Logger logger = System.getLogger("Sports Tracker");

  private static final String BASE_URL = "https://api.sports-tracker.com";
  private static final String LOGIN_URL = BASE_URL + "/apiserver/v1/login?source=javascript";
  private static final String WORKOUTS_URL = BASE_URL + "/apiserver/v1/workouts?limited=true&limit=20";
  private static final String LOGIN_REFERER_URL = BASE_URL + "/login";
  private static final String WORKOUTS_REFERER_URL = BASE_URL + "/dashboard";
  private static final String LOGIN_CONTENT_TYPE = "application/x-www-form-urlencoded; charset=UTF-8";
  private static final String AUTHORIZATION_HEADER = "STTAuthorization";

  private final String user;
  private final String password;

  public SportsTracker(final String user, final String password) {
    this.user = user;
    this.password = password;
  }

  public List<Workout> getWorkouts() throws IOException, InterruptedException {
    Objects.requireNonNull(user, "Sports Tracker username must not be null.");
    Objects.requireNonNull(password, "Sports Tracker password must not be null.");

    logger.log(Logger.Level.INFO, "Logging in with Sports Tracker user " + user);

    final HttpClient client = HttpClient.newHttpClient();
    final HttpRequest loginRequest =
        WebUtil.buildPostRequest(LOGIN_URL, String.format("l=%s&p=%s", user, password))
            .referer(LOGIN_REFERER_URL)
            .contentType(LOGIN_CONTENT_TYPE)
            .build();
    final HttpResponse<String> loginResponse = client.send(loginRequest, BodyHandlers.ofString());
    logger.log(Level.DEBUG, loginResponse.body());
    if (loginResponse.statusCode() < 200 || loginResponse.statusCode() > 299) {
      throw new RuntimeException("Error logging in to Sports Tracker, received status code " + loginResponse.statusCode());
    }

    final Pattern sessionKeyPattern = Pattern.compile("\"sessionkey\"\\s*:\\s*\"([^\"]*)\"");
    final String sessionKey = sessionKeyPattern.matcher(loginResponse.body()).results().findFirst().get().group(1);
    logger.log(Level.DEBUG, sessionKey);

    final HttpRequest workoutsRequest = WebUtil.buildGetRequest(WORKOUTS_URL)
        .referer(WORKOUTS_REFERER_URL)
        .header(AUTHORIZATION_HEADER, sessionKey)
        .build();
    final HttpResponse<String> workoutsResponse = client.send(workoutsRequest, BodyHandlers.ofString());
    logger.log(Level.TRACE, workoutsResponse.body());

    final JsonElement resultJson = JsonParser.parseString(workoutsResponse.body());
    final JsonArray rawWorkouts = resultJson.getAsJsonObject().getAsJsonArray("payload");
    logger.log(Level.TRACE, rawWorkouts);

    final List<Workout> workoutList = StreamSupport.stream(rawWorkouts.spliterator(), false)
        .map(JsonElement::getAsJsonObject)
        .filter(w -> Objects.equals(SportType.CYCLING.getSportsTrackerId(), w.get("activityId").getAsInt()))
        .map(w -> new Workout(
            Instant.ofEpochMilli(w.get("startTime").getAsLong())
                .atZone(TimeZone.getDefault().toZoneId()).toLocalDate(),
            Duration.of(w.get("totalTime").getAsLong(), ChronoUnit.SECONDS),
            w.get("totalDistance").getAsBigDecimal(),
            SportType.fromSportsTrackerId(w.get("activityId").getAsInt())))
        .collect(toList());
    logger.log(Level.TRACE, workoutList);
    return workoutList;
  }
}
