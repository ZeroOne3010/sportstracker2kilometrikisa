package zeroone3010.sportstracker2kilometrikisa;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

class WorkoutCollector implements Collector<Workout, LinkedHashMap<LocalDate, Workout>, LinkedHashMap<LocalDate, Workout>> {
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
}
