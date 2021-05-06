package zeroone3010.sportstracker2kilometrikisa;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Locale;

public record Workout(LocalDate date,
                      Duration duration,
                      BigDecimal totalDistanceInMeters,
                      SportType type) {

  private static final float MPS_TO_KMPH = 3.6f;

  public BigDecimal getTotalDistanceInKilometers() {
    return totalDistanceInMeters.divide(BigDecimal.valueOf(1000L), 1, RoundingMode.HALF_UP);
  }

  @Override
  public String toString() {
    final float kmph = totalDistanceInMeters.floatValue() / duration.toSeconds() * MPS_TO_KMPH;
    return String.format(Locale.ROOT, "[%s: %s km, %d:%02d:%02d; %.02f km/h]", date,
        getTotalDistanceInKilometers(),
        duration.toHours(), duration.toMinutesPart(), duration.toSecondsPart(), kmph);
  }
}
