<h1 align="center">Soccer League </h1> <br>
<p align="center">
    <img alt="SoccerLeague" title="SoccerLeague" src="https://i.imgur.com/rrvAtKW.png" width="450">
  </a>
</p>

<a name="APK"></a>
<div align="center">
  <h4>
    <a href="https://github.com/akkayameva/SoccerLeague/blob/main/soccerLeague.apk">
      APK
    </a>
    <span> | </span>
    <a href="https://github.com/akkayameva/SoccerLeague/tree/main/SoccerLeague">
      Source Code
    </a>
     </h4>
</div>



## Table of Contents

- [Introduction](#introduction)
- [Features](#features)
- [Technical](#technical)
- [Dependency](#dependency)
- [Conclusion](#conclusion)
- [Developer](#developer)
- [Resources](#resources)



## Introduction

 ![task-project](https://img.shields.io/badge/Task%20Project-2021-1f425f.svg?color=yellow)
  ![made-with-android](https://img.shields.io/badge/Made%20with-Android-1f425f.svg?color=green)
  ![made-with-java](https://img.shields.io/badge/Made%20with-Kotlin-1f425f.svg?color=orange)
  ![GitHub visitors](https://img.shields.io/badge/dynamic/json?color=red&label=visitors%20&query=value&url=https%3A%2F%2Fapi.countapi.xyz%2Fhit%2Fakkayameva.SoccerLeague%2Freadme)


  ![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/akkayameva/SoccerLeague?color=red&label=code%20size)
  ![GitHub repo size](https://img.shields.io/github/repo-size/akkayameva/SoccerLeague?color=orange&label=repo%20size)
  ![GitHub last commit](https://img.shields.io/github/last-commit/akkayameva/SoccerLeague?color=yellowgreen&label=last%20commit)

To manage league matches developed an sample android application.This application will be used to determine match fixture
* Fixture is a sporting event arranged to take place on a particular date.
* [Fixture Calculation](https://sports.icalculator.info/fixtures-calculator.html) All teams play each other twice -home and away



<p align="center">
  <img src = "https://i.imgur.com/Y4EZnDu.png" width=600>
</p>

## Features

A few features of app

* Fixtures can be created for the created team list.

* It consists of two rounds of matches, home and away.

* The team that became Home in the first round is placed away in the second round.

* There is animation in transitions between screens.

* Has a Dark Mode compatible theme


<p align="center">
  <img src = "https://i.imgur.com/IPAIyKJ.png" width=500>
</p>

<p align="center">
  <img src = "https://i.imgur.com/VEKHpHH.png" width=500>
</p>

## Technical

|                            | Soccer League      | ◾ Note          |
| -------------------------- | :----------------: | :-------------: |
| OOP Principles             |         ✔️         |                  |
| MVVM Architecture          |         ✔️         |                  |
| MOCK Rest API (POSTMAN)    |         ✔️         | Postman used        |
| Room DB                    |         ✔️         |                     |
| Jetpack Libraries          |         ✔️         | for compose        |
| Compose UI                 |         ✔️         | I try my best      |
| Support Dark Mode          |         ✔️         | for system settings|
| Lottie Animation           |         ✔️         | 2 animation        |
| Live and Mutable Data      |         ✔️         |                     |
| Support different size     |         ✔️         |  team list can be different        |




## Dependency

Kotlin and Compose version:

```xml
buildscript {
    ext.kotlin_version = '1.4.32'
    ext {
        compose_version = '1.0.0-beta06'
    }
```

Compose for UI:
```xml
    implementation "androidx.compose.ui:ui:$compose_version"
    implementation "androidx.compose.material:material:$compose_version"
    implementation "androidx.compose.ui:ui-tooling:$compose_version"
    implementation "androidx.compose.runtime:runtime-livedata:$compose_version"

 //provides paging layouts for Jetpack Compose
    implementation 'com.google.accompanist:accompanist-pager:0.9.1'
    implementation "com.google.accompanist:accompanist-pager-indicators:0.9.1"
```

Navigation for navigate:
```xml
    implementation 'androidx.navigation:navigation-fragment-ktx:2.3.5'
    implementation 'androidx.navigation:navigation-ui-ktx:2.3.5'
    implementation "androidx.fragment:fragment-ktx:1.3.3"
```

Moshi for JSON serialization:
```xml
    implementation "com.squareup.moshi:moshi:1.9.2"
    kapt "com.squareup.moshi:moshi-kotlin-codegen:1.9.2"
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation "com.squareup.retrofit2:converter-moshi:2.7.1"
    implementation 'com.squareup.okhttp3:logging-interceptor:4.5.0'
```

Room DB for data:
```xml
   implementation "androidx.room:room-runtime:2.3.0"
   implementation "androidx.room:room-ktx:2.3.0"
   kapt "androidx.room:room-compiler:2.3.0"
```

Timber for debug:
```xml
   implementation 'com.jakewharton.timber:timber:4.7.1'
```
Koin for data injection:
```xml
   // Koin main features for Android (Scope,ViewModel ...)
    implementation "io.insert-koin:koin-android:$koin_version"
    // Koin Android - experimental builder extensions
    implementation "io.insert-koin:koin-android-ext:$koin_version"
```
Lottie for animations:
```xml
  implementation 'com.airbnb.android:lottie-compose:1.0.0-beta03-1'
```



## Conclusion
I started developing the application primarily with java. However, I chose to use kotlin because I get null error very often in java and because more solutions can be offered with kotlin.

I tried the use of Compose for the first time by learning from the source, I mentioned in the resources.

As a result, I had the opportunity to compare Kotlin and Java.
I also learned that using Compose is easier to integrate than xml files.

## Developer
| <a href="https://github.com/akkayameva" target="_blank">**Meva Akkaya**</a> |
| :---: |
![MevaAkkaya](https://avatars1.githubusercontent.com/u/30075835?s=460&v=4&s=100)
| Mobile App Developer |
|  <a href="https://github.com/akkayameva" target="_blank">`github.com/akkayameva`</a>  |
| <a href="https://www.linkedin.com/in/akkayameva/" target="_blank">`linkedin.com/akkayameva`</a> | 
| <a href="mailto:akkayameva@gmail.com" target="_blank">`akkayameva@gmail.com`</a> | |

## Resources
* [Jetpack Compose Basics Lab](https://developer.android.com/codelabs/jetpack-compose-basics#0)
* [Modern JSON seril. - Moshi](https://gaikwadchetan93.medium.com/moshi-modern-json-serialization-library-for-java-and-kotlin-138040bc3d5b)

