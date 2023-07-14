# The Länd of Adventure

## About

"**The Länd of Adventure**" is a native Android application developed as an application/software project for the summer semester 2023 at the [Hochschule der Medien](https://www.hdm-stuttgart.de). 

It offers an exciting GPS-based exploration game in the city of Stuttgart, motivating users to explore the city on foot while enjoying an entertaining gaming experience. Designed as a single-player experience, "The Länd of Adventure" immerses players in a medieval-themed dialog and interface, encouraging them to embark on an adventurous journey through Stuttgart's urban landscape at their own pace. 

Our project is designed to support Android devices running version 8 (Android Oreo) and above.

### Technologies

* [Kotlin](https://kotlinlang.org/): The primary programming language, known for its conciseness and interoperability with Java 
* [Mapbox](https://www.mapbox.com/): Integration of Mapbox provides advanced mapping and location services 
* [Room](https://developer.android.com/training/data-storage/room/): Room library simplifies working with SQLite databases, ensuring efficient data management


## How to build and use

### Prerequisites
* You will need to have [Android Studio](https://developer.android.com/studio) installed to build this project. 
* Make sure you have the Android Studio default JDK installed and selected 
(*Settings> Build, Execution, Deployment> Build tools> Gradle*)

The steps may vary depending on your operating system.

### Project Setup

Clone the repository to your local machine using Git or download the source code as a ZIP file and extract it. Open the project in Android Studio.

A [MapBox](https://www.mapbox.com) account is required to use the application.
After you have created an account, you will need to create a Mapbox API token on your profile and copy it into the mapbox.env file. The Token has to have the Downloads:Read Flag.

To run the project, first [install an emulator](https://developer.android.com/studio/run/managing-avds). Once the emulator is installed, run the Länd of Adventure app to launch the application on the emulator and start the adventure. 

For instructions on navigating the emulator, click here: [Emulator Navigation](https://developer.android.com/studio/run/emulator)



## How to generate documentation

This program is fully documented using [Dokka](https://github.com/Kotlin/dokka). To generate the javadoc files, you can navigate to the project
folder and run the following command

On Windows:

`gradlew dokkaHtml`

On Mac and Linux:

`./gradlew dokkaHtml`

The HTML file will be in the build directory and then in the dokka directory.

For this command to work, you must have [gradle](https://gradle.org/install/) installed.

## Dependencies

#### Support Libraries

|Dependency|Version|License|
|:--:|:--:|:--:|
|Androidx Legacy: Legacy Support|v4:1.0.0|Apache-2.0 License|
|Androidx Lifecycle: LiveData|2.6.1|Apache-2.0 License|
|Androidx Preference|1.2.0|Apache-2.0 License|
|Androidx ConstraintLayout|2.1.4|Apache-2.0 License|
|Android Google GMS Play-Services-Location|21.0.1| [Android Software Development Kit License ](https://developer.android.com/studio/terms.html)|

#### MapBox Libraries

|Dependency|Version|License|
|:--:|:--:|:--:|
|Mapbox Maps SDK For Android|10.12.1|Apache-2.0 License|

#### In App Navigation
|Dependency|Version|License|
|:--:|:--:|:--:|
|Androidx Navigation Fragment|2.5.3|Apache-2.0 License|
|Androidx Navigation UI Kotlin Extensions|2.5.3|Apache-2.0 License|
|Androidx Navigation Fragment|2.4.1|Apache-2.0 License|

#### Room Database

|Dependency|Version|License|
|:--:|:--:|:--:|
|Room Runtime|2.5.1|Apache-2.0 License|
|Room Compiler|2.5.1|Apache-2.0 License|
|Room Kotlin Extensions|2.5.1|Apache-2.0 License|


#### Other dependencies

|Dependency|Version|License|
|:--:|:--:|:--:|
|Android Activity Kotlin Extensions| 1.7.1 |Apache-2.0 License|
|Android Fragment Kotlin Extensions|1.5.7|Apache-2.0 License
|Android Lifecycle ViewModel Kotlin Extensions|2.6.1|Apache-2.0 License|
|Core Kotlin Extensions | 1.10.1| Apache-2.0 License|
|Glide |4.14.2|[License](https://github.com/bumptech/glide/blob/master/LICENSE)|
|Glide Compiler | 4.14.2| [License](https://github.com/bumptech/glide/blob/master/LICENSE) |
|Material Components For Android| 1.9.0| Apache-2.0 License|

### Testing dependencies

|Dependency|Version|License|
|:--:|:--:|:--:|
|JUnit|4.13.2|Apache-2.0 License|
|JUnit Jupiter|5.8.1|EPL 2.0|
|Androix Test JUnit Extension|1.1.5|Apache-2.0 License|
|Androidx Espresso |3.5.1|Apache-2.0 License|

### Maven Plugins

|Dependency|Version|License|
|:--:|:--:|:--:|
|detekt Plugins|1.22.0|Apache-2.0 License|
|Android Application Gradle Plugin||Apache-2.0 License|
|Google Kotlin Symbol Processing|1.8.0-1.0.8| Apache-2.0 License |
|Dokka|1.8.20| Apache-2.0 License |


## Copyright

The-Länd-of-Adventure (c) 2023 
[Johannes Hausch](https://github.com/JohannesHausch),
[Sebastian Maier](https://github.com/SebastianMaier03), [Konstantinos Gimoussiakakis](https://github.com/Kostanix), 
[Aylin Alagöz](https://github.com/ayal104), 
[Dorina Sobiecki](https://github.com/DorinaSobiecki)

SPDX-License-Identifier: GPL-3.0
