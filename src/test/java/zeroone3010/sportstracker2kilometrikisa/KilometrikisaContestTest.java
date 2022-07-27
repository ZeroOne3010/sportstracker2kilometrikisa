package zeroone3010.sportstracker2kilometrikisa;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class KilometrikisaContestTest {
  @Test
  void summer2021() {
    KilometrikisaContest contest = new KilometrikisaContest(LocalDate.of(2021, 5, 1));
    assertEquals(45, contest.getId());

    contest = new KilometrikisaContest(LocalDate.of(2021, 6, 13));
    assertEquals(45, contest.getId());

    contest = new KilometrikisaContest(LocalDate.of(2021, 7, 27));
    assertEquals(45, contest.getId());

    contest = new KilometrikisaContest(LocalDate.of(2021, 8, 30));
    assertEquals(45, contest.getId());

    contest = new KilometrikisaContest(LocalDate.of(2021, 9, 30));
    assertEquals(45, contest.getId());
  }

  @Test
  void summer2022() {
    KilometrikisaContest contest = new KilometrikisaContest(LocalDate.of(2022, 5, 1));
    assertEquals(47, contest.getId());

    contest = new KilometrikisaContest(LocalDate.of(2022, 6, 13));
    assertEquals(47, contest.getId());

    contest = new KilometrikisaContest(LocalDate.of(2022, 7, 27));
    assertEquals(47, contest.getId());

    contest = new KilometrikisaContest(LocalDate.of(2022, 8, 30));
    assertEquals(47, contest.getId());

    contest = new KilometrikisaContest(LocalDate.of(2022, 9, 30));
    assertEquals(47, contest.getId());
  }

  @Test
  void winter2023() {
    KilometrikisaContest contest = new KilometrikisaContest(LocalDate.of(2023, 1, 1));
    assertEquals(48, contest.getId());

    contest = new KilometrikisaContest(LocalDate.of(2023, 2, 13));
    assertEquals(48, contest.getId());

    contest = new KilometrikisaContest(LocalDate.of(2023, 3, 8));
    assertEquals(48, contest.getId());
  }

  @Test
  void summer2023() {
    KilometrikisaContest contest = new KilometrikisaContest(LocalDate.of(2023, 5, 1));
    assertEquals(49, contest.getId());

    contest = new KilometrikisaContest(LocalDate.of(2023, 6, 13));
    assertEquals(49, contest.getId());

    contest = new KilometrikisaContest(LocalDate.of(2023, 7, 27));
    assertEquals(49, contest.getId());

    contest = new KilometrikisaContest(LocalDate.of(2023, 8, 30));
    assertEquals(49, contest.getId());

    contest = new KilometrikisaContest(LocalDate.of(2023, 9, 30));
    assertEquals(49, contest.getId());
  }

  @Test
  void winter2024() {
    KilometrikisaContest contest = new KilometrikisaContest(LocalDate.of(2024, 1, 1));
    assertEquals(50, contest.getId());

    contest = new KilometrikisaContest(LocalDate.of(2024, 2, 13));
    assertEquals(50, contest.getId());

    contest = new KilometrikisaContest(LocalDate.of(2024, 3, 8));
    assertEquals(50, contest.getId());
  }
}
