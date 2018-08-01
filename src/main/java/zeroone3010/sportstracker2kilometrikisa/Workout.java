package zeroone3010.sportstracker2kilometrikisa;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Locale;

public final class Workout {
  private static final float MPS_TO_KMPH = 3.6f;

  private final LocalDate date;
  private final Duration duration;
  private final BigDecimal totalDistanceInMeters;
  private final SportType type;

  public Workout(final LocalDate date, final Duration duration, final BigDecimal totalDistanceInMeters, final SportType type) {
    this.date = date;
    this.duration = duration;
    this.totalDistanceInMeters = totalDistanceInMeters;
    this.type = type;
  }

  public LocalDate getDate() {
    return date;
  }

  public Duration getDuration() {
    return duration;
  }

  public BigDecimal getTotalDistanceInMeters() {
    return totalDistanceInMeters;
  }

  public BigDecimal getTotalDistanceInKilometers() {
    return totalDistanceInMeters.divide(BigDecimal.valueOf(1000L), 1, RoundingMode.HALF_UP);
  }

  public SportType getType() {
    return type;
  }

  @Override
  public String toString() {
    final float kmph = totalDistanceInMeters.floatValue() / duration.toSeconds() * MPS_TO_KMPH;
    return String.format(Locale.ROOT, "[%s: %s km, %d:%02d:%02d; %.02f km/h]", date,
        getTotalDistanceInKilometers(),
        duration.toHours(), duration.toMinutesPart(), duration.toSecondsPart(), kmph);
  }
}
