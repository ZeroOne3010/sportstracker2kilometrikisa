package zeroone3010.sportstracker2kilometrikisa;

import java.util.Arrays;

public enum SportType {
  CYCLING(2),
  OTHER(null);

  private final Integer sportsTrackerId;

  SportType(final Integer sportsTrackerId) {
    this.sportsTrackerId = sportsTrackerId;
  }

  public int getSportsTrackerId() {
    return sportsTrackerId;
  }

  public static SportType fromSportsTrackerId(final int id) {
    return Arrays.stream(values())
        .filter(t -> t.getSportsTrackerId() == id)
        .findAny()
        .orElse(SportType.OTHER);
  }
}
