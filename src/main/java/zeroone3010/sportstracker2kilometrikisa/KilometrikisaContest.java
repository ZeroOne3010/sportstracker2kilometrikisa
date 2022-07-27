package zeroone3010.sportstracker2kilometrikisa;

import java.time.LocalDate;

class KilometrikisaContest {
  private static final int CONTEXT_ID_OFFSET = 44;
  private static final int YEAR_TO_CONTEXT_ID_OFFSET = 2021;

  private final LocalDate date;

  KilometrikisaContest(final LocalDate date) {
    this.date = date;
  }

  int getId() {
    final float summer = date.getMonthValue() > 4 ? 0.5f : 0f;
    return (int) (CONTEXT_ID_OFFSET + (2 * (date.getYear() - YEAR_TO_CONTEXT_ID_OFFSET + summer)));
  }
}
