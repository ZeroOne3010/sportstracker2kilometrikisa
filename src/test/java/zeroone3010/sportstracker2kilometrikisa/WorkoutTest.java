package zeroone3010.sportstracker2kilometrikisa;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WorkoutTest {
  @Test
  void toStringWithZeroMinutes() {
    final Workout workout = new Workout(LocalDate.of(2018, 12, 31),
        Duration.ofSeconds(18L), BigDecimal.valueOf(0L), SportType.CYCLING);
    assertEquals("[2018-12-31: 0.0 km, 0:00:18; 0.00 km/h]", workout.toString());
  }

  @Test
  void toStringWithZeroHours() {
    final Workout workout = new Workout(LocalDate.of(2018, 12, 31),
        Duration.ofMinutes(42L), BigDecimal.valueOf(0L), SportType.CYCLING);
    assertEquals("[2018-12-31: 0.0 km, 0:42:00; 0.00 km/h]", workout.toString());
  }

  @Test
  void toStringWithMoreThan24HoursAndSomeMinutesAndSeconds() {
    final Workout workout = new Workout(LocalDate.of(2018, 12, 31),
        Duration.ofSeconds(129723L), BigDecimal.valueOf(0L), SportType.CYCLING);
    assertEquals("[2018-12-31: 0.0 km, 36:02:03; 0.00 km/h]", workout.toString());
  }

  @Test
  void toStringWithSingleDigitMonthAndDay() {
    final Workout workout = new Workout(LocalDate.of(2018, 1, 2),
        Duration.ofMinutes(42L), BigDecimal.valueOf(0L), SportType.CYCLING);
    assertEquals("[2018-01-02: 0.0 km, 0:42:00; 0.00 km/h]", workout.toString());
  }

  @Test
  void toStringWithOneMeter() {
    final Workout workout = new Workout(LocalDate.of(2018, 1, 2),
        Duration.ofMinutes(42L), BigDecimal.valueOf(1L), SportType.CYCLING);
    assertEquals("[2018-01-02: 0.0 km, 0:42:00; 0.00 km/h]", workout.toString());
  }

  @Test
  void toStringWithTenMeters() {
    final Workout workout = new Workout(LocalDate.of(2018, 1, 2),
        Duration.ofMinutes(42L), BigDecimal.valueOf(10L), SportType.CYCLING);
    assertEquals("[2018-01-02: 0.0 km, 0:42:00; 0.01 km/h]", workout.toString());
  }

  @Test
  void toStringWithFiftyMeters() {
    final Workout workout = new Workout(LocalDate.of(2018, 1, 2),
        Duration.ofMinutes(42L), BigDecimal.valueOf(50L), SportType.CYCLING);
    assertEquals("[2018-01-02: 0.1 km, 0:42:00; 0.07 km/h]", workout.toString());
  }

  @Test
  void toStringWith149Meters() {
    final Workout workout = new Workout(LocalDate.of(2018, 1, 2),
        Duration.ofMinutes(42L), BigDecimal.valueOf(149L), SportType.CYCLING);
    assertEquals("[2018-01-02: 0.1 km, 0:42:00; 0.21 km/h]", workout.toString());
  }

  @Test
  void toStringWith150Meters() {
    final Workout workout = new Workout(LocalDate.of(2018, 1, 2),
        Duration.ofMinutes(42L), BigDecimal.valueOf(150L), SportType.CYCLING);
    assertEquals("[2018-01-02: 0.2 km, 0:42:00; 0.21 km/h]", workout.toString());
  }

  @Test
  void toStringWith12345Meters() {
    final Workout workout = new Workout(LocalDate.of(2018, 1, 2),
        Duration.ofMinutes(42L), BigDecimal.valueOf(12345L), SportType.CYCLING);
    assertEquals("[2018-01-02: 12.3 km, 0:42:00; 17.64 km/h]", workout.toString());
  }
}