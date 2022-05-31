Sports Tracker 2 Kilometrikisa
==============================

This is a Java 16 application for automatically transferring your cycling statistics from
[Sports Tracker](https://www.sports-tracker.com/) to [Kilometrikisa](https://www.kilometrikisa.fi/).

The application has been updated to support Kilometrikisa 2022.

By default this application finds all of your Sports Tracker cycling entries from the current day,
sums their kilometers and minutes together, and sends the result to Kilometrikisa. You can also customize
the number of days to look back with the `daysInPast` command line argument.

Builds with Gradle. Uses external GSON and JUnit 5 libraries.

Run with the following parameters:

    -Dkkuser=KILOMETRIKISA_USERNAME
    -Dkkpass=KILOMETRIKISA_PASSWORD
    -Dstuser=SPORTSTRACKER_USERNAME
    -Dstpass=SPORTSTRACKER_PASSWORD

Also add the `daysInPast` parameter if you want to include older entries than just the current day too.
The following would include your entries from today, yesterday, and the day before yesterday:

    -DdaysInPast=2

And optionally, if you wish to customize the output format:

    -Djava.util.logging.SimpleFormatter.format="[%1$tF %1$tT] [%4$-5s] %3$-14s: %5$s %n"

Note that this application will overwrite the entries in Kilometrikisa, so if you have added kilometers there
manually, outside of copying them from Sports Tracker, be careful.
