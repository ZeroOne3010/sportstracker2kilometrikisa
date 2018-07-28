package zeroone3010.sportstracker2kilometrikisa;

import jdk.incubator.http.HttpClient;
import jdk.incubator.http.HttpRequest;
import jdk.incubator.http.HttpResponse;
import jdk.incubator.http.HttpResponse.BodyHandler;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;

public final class SportsTracker {
  private static final Logger logger = System.getLogger("Sports Tracker");

  private static final String BASE_URL = "https://www.sports-tracker.com";
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

  public List<Workout> getWorkouts() throws IOException, InterruptedException, ScriptException {
    Objects.requireNonNull(user, "Sports Tracker username must not be null.");
    Objects.requireNonNull(password, "Sports Tracker password must not be null.");

    logger.log(Logger.Level.INFO, "Logging in with Sports Tracker user " + user);

    final HttpClient client = HttpClient.newHttpClient();
    final HttpRequest loginRequest =
        WebUtil.buildPostRequest(LOGIN_URL, String.format("l=%s&p=%s", user, password))
            .referer(LOGIN_REFERER_URL)
            .contentType(LOGIN_CONTENT_TYPE)
            .build();
    final HttpResponse<String> loginResponse = client.send(loginRequest, BodyHandler.asString());
    logger.log(Level.DEBUG, loginResponse.body());

    final Pattern sessionKeyPattern = Pattern.compile("\"sessionkey\"\\s*:\\s*\"([^\"]*)\"");
    final String sessionKey = sessionKeyPattern.matcher(loginResponse.body()).results().findFirst().get().group(1);
    logger.log(Level.DEBUG, sessionKey);

    final HttpRequest workoutsRequest = WebUtil.buildGetRequest(WORKOUTS_URL)
        .referer(WORKOUTS_REFERER_URL)
        .header(AUTHORIZATION_HEADER, sessionKey)
        .build();
    final HttpResponse<String> workoutsResponse = client.send(workoutsRequest, BodyHandler.asString());
    logger.log(Level.TRACE, workoutsResponse.body());

    final ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
    final Map<String, Object> resultJson = (Map<String, Object>) engine.eval(String.format("JSON.parse('%s')", workoutsResponse.body()));

    final Collection<Map<String, Object>> rawWorkouts = ((Map<String, Map<String, Object>>) resultJson.get("payload")).values();
    logger.log(Level.TRACE, rawWorkouts);

    final List<Workout> workoutList = rawWorkouts.stream()
        .filter(w -> Objects.equals(2, w.get("activityId")))
        .map(w -> new Workout(
            Instant.ofEpochMilli(Number.class.cast(w.get("startTime")).longValue())
                .atZone(TimeZone.getDefault().toZoneId()).toLocalDate(),
            Duration.of(Number.class.cast(w.get("totalTime")).longValue(), ChronoUnit.SECONDS),
            new BigDecimal(String.valueOf(w.get("totalDistance"))),
            SportType.fromSportsTrackerId((int) w.get("activityId"))))
        .collect(toList());
    logger.log(Level.TRACE, workoutList);
    return workoutList;
  }
}
