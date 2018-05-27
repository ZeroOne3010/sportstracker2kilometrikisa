package zeroone3010.sportstracker2kilometrikisa;

import javax.script.ScriptException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static java.lang.System.Logger;
import static java.lang.System.getLogger;
import static java.lang.System.getProperty;

public class Main {
  private static final Logger logger = getLogger("Main");

  public static void main(final String... args) throws IOException, InterruptedException, ScriptException {
    final String stUser = getProperty("stuser");
    final String stPass = getProperty("stpass");
    final String kkUser = getProperty("kkuser");
    final String kkPass = getProperty("kkpass");

    final SportsTracker sportsTracker = new SportsTracker(stUser, stPass);
    final LinkedHashMap<LocalDate, Workout> workouts = sportsTracker.getWorkouts().stream()
        .filter(w -> w.getType() == SportType.CYCLING)
        .collect(Collectors.groupingBy(Workout::getDate,
            Collectors.reducing((a, b) -> new Workout(a.getDate(),
                a.getDuration().plus(b.getDuration()),
                a.getTotalDistanceInMeters().add(b.getTotalDistanceInMeters()),
                a.getType())
            ))).values().stream()
        .map(Optional::get)
        .sorted(Comparator.comparing(Workout::getDate).reversed())
        .collect(new Collector<Workout, LinkedHashMap<LocalDate, Workout>, LinkedHashMap<LocalDate, Workout>>() {
          @Override
          public Supplier<LinkedHashMap<LocalDate, Workout>> supplier() {
            return LinkedHashMap::new;
          }

          @Override
          public BiConsumer<LinkedHashMap<LocalDate, Workout>, Workout> accumulator() {
            return (map, workout) -> map.put(workout.getDate(), workout);
          }

          @Override
          public BinaryOperator<LinkedHashMap<LocalDate, Workout>> combiner() {
            return (a, b) -> {
              a.putAll(b);
              return a;
            };
          }

          @Override
          public Function<LinkedHashMap<LocalDate, Workout>, LinkedHashMap<LocalDate, Workout>> finisher() {
            return Function.identity();
          }

          @Override
          public Set<Characteristics> characteristics() {
            return Set.of(Characteristics.IDENTITY_FINISH);
          }
        });

    logger.log(Logger.Level.INFO, "Loaded the following workouts from Sports Tracker: " + workouts);

    final Kilometrikisa kilometrikisa = new Kilometrikisa(kkUser, kkPass);
    final Workout todaysWorkout = workouts.get(LocalDate.now().minusDays(0));

    logger.log(Logger.Level.INFO, "Of those, this one is today's and will be posted: " + todaysWorkout);
    kilometrikisa.post(todaysWorkout);
  }
}
