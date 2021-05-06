package zeroone3010.sportstracker2kilometrikisa;

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
import java.util.stream.IntStream;

import static java.lang.System.Logger;
import static java.lang.System.getLogger;
import static java.lang.System.getProperty;

public class Main {
  private static final Logger logger = getLogger("Main");

  public static void main(final String... args) throws IOException, InterruptedException {
    final String stUser = getProperty("stuser");
    final String stPass = getProperty("stpass");
    final String kkUser = getProperty("kkuser");
    final String kkPass = getProperty("kkpass");
    final int daysInPast = Integer.parseInt(getProperty("daysInPast", "0"));
    if (daysInPast < 0) {
      throw new IllegalArgumentException("daysInPast must be zero or greater.");
    }
    final int daysAltogether = daysInPast + 1; // Include today.

    final SportsTracker sportsTracker = new SportsTracker(stUser, stPass);
    final LinkedHashMap<LocalDate, Workout> workouts = sportsTracker.getWorkouts().stream()
        .filter(w -> w.type() == SportType.CYCLING)
        .collect(Collectors.groupingBy(Workout::date,
            Collectors.reducing((a, b) -> new Workout(a.date(),
                a.duration().plus(b.duration()),
                a.totalDistanceInMeters().add(b.totalDistanceInMeters()),
                a.type())
            ))).values().stream()
        .map(Optional::get)
        .sorted(Comparator.comparing(Workout::date).reversed())
        .collect(new Collector<Workout, LinkedHashMap<LocalDate, Workout>, LinkedHashMap<LocalDate, Workout>>() {
          @Override
          public Supplier<LinkedHashMap<LocalDate, Workout>> supplier() {
            return LinkedHashMap::new;
          }

          @Override
          public BiConsumer<LinkedHashMap<LocalDate, Workout>, Workout> accumulator() {
            return (map, workout) -> map.put(workout.date(), workout);
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
    IntStream.range(0, daysAltogether).forEach(i -> {
      final LocalDate date = LocalDate.now().minusDays(i);
      final Workout workout = workouts.get(date);
      logger.log(Logger.Level.INFO, "Of those, this one is of " + date + " and will be posted: " + workout);
      try {
        kilometrikisa.post(workout);
      } catch (IOException e) {
        e.printStackTrace();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    });

  }
}
