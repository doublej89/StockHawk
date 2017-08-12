# StockHawk

StockHawk is a simple stock tracking app. Upon launch it displays a list of a limited number of 
stock quotes. The quotes are updated every hour.

If any of the list items are tapped, a line chart appears showing the hour by hour evolution of 
price of the particular stock.

Tapping on the floating action button on the main page prompts the user to enter a stock symbol
which, if not already present in the database, will be added to the database and displayed in the
list along with its price, percentage change, etc.

A widget is also available for this app, which displays a list of the same stock quotes as in the
main app. The list items also display the same on click behavior as in th main app.

# Installation

This app uses the Gradle build system.

First download the code by cloning this repository or downloading an archived snapshot. (See the options at the top of the page.)

In Android Studio, use the "Import non-Android Studio project" or "Import Project" option. Next select the directory in which you donwloaded this repository. If prompted for a gradle configuration accept the default settings.

Make sure the Android version in your device/emulator is at least 4.0.3 (API level 15).
