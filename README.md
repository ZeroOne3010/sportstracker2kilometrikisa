Sports Tracker 2 Kilometrikisa
==============================

This is a Java 16 application for automatically transferring your cycling statistics from
[Sports Tracker](https://www.sports-tracker.com/) to [Kilometrikisa](https://www.kilometrikisa.fi/).

The application has been updated to support Kilometrikisa 2021.

By default this application finds all of your Sports Tracker cycling entries from the current day,
sums their kilometers and minutes together, and sends the result to Kilometrikisa.

Builds with Gradle. Uses no external libraries (besides JUnit 5).

Run with the following parameters:

    -Dkkuser=KILOMETRIKISA_USERNAME
    -Dkkpass=KILOMETRIKISA_PASSWORD
    -Dstuser=SPORTSTRACKER_USERNAME
    -Dstpass=SPORTSTRACKER_PASSWORD

And optionally, if you wish to customize the output format:

    -Djava.util.logging.SimpleFormatter.format="[%1$tF %1$tT] [%4$-5s] %3$-14s: %5$s %n"
