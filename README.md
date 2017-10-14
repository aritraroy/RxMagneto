# RxMagneto

A fast, light-weight and powerful Play Store information fetcher for Android.

### Specs
[ ![Download](https://api.bintray.com/packages/aritraroy/maven/rxmagneto/images/download.svg) ](https://bintray.com/aritraroy/maven/rxmagneto/_latestVersion) [![API](https://img.shields.io/badge/API-15%2B-orange.svg?style=flat)](https://android-arsenal.com/api?level=14) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) [![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-RxMagneto-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/5362)

![RxMagneto](https://github.com/aritraroy/RxMagneto/blob/master/raw/logo_250.png)

This library allows you to **fetch various live information from Play Store** of your app or any other app of your choice. With just a few lines of code, you can get access to a lot of useful app data fetched fresh from the Play Store.

It has been named after the famous anti-villain from X-Men. This library has been **completely written using RxJava** giving you powerful controls to make the most use of it.

![RxMagneto](https://github.com/aritraroy/RxMagneto/blob/master/raw/github_screenshot.png)

# Download

This library is available in **jCenter** which is the default Maven repository used in Android Studio.

## Gradle 
```gradle
dependencies {
    // other dependencies here
    
    compile 'com.andrognito.rxmagneto:rxmagneto:1.0.0'
}
```

## Maven

```xml
<dependency>
  <groupId>com.andrognito.rxmagneto</groupId>
  <artifactId>rxmagneto</artifactId>
  <version>1.0.0</version>
  <type>pom</type>
</dependency>
```

### Spread Some :heart:
[![GitHub stars](https://img.shields.io/github/stars/aritraroy/RxMagneto.svg?style=social&label=Star)](https://github.com/aritraroy) [![GitHub followers](https://img.shields.io/github/followers/aritraroy.svg?style=social&label=Follow)](https://github.com/aritraroy)  
[![Twitter Follow](https://img.shields.io/twitter/follow/aritraroy93.svg?style=social)](https://twitter.com/aritraroy93) 

## Use Cases

There are several use cases of this library. Here are some common examples for you to get started, but you can always be creative and make the most use of it.

### App Updates

This is a common problem for many developers wanting to check if their users are using the latest version of the app available on Play Store or not. With RxMagneto, you can get it done with just a single line of code and zero hassle.

### Recent Changelog

You might want to show your users the latest changelog when they update the app. RxMagneto can help you fetch the most recent changelog directly from Play Store with just one line of code.

### User Ratings

Many developers prompt their users to rate the app after a certain period of time. You can also show your current rating and ratings count directly from Play Store to encourage these users to rate your app even more.

## Setup

Once you have downloaded the library in your project, setting up RxMagneto is super-easy.

```java
RxMagneto rxMagneto = RxMagneto.getInstance();
rxMagneto.initialize(this);
```

The initialize method takes a Context object. It can either be an Application context or Activity context.


## Quick Example

Here is a quick example for you to get started right away in less than a minute.

```java
RxMagneto rxMagneto = RxMagneto.getInstance();
rxMagneto.initialize(this);

Observable<String> observable = rxMagneto.grabVersion(packageName);
observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> { 
                    Log.d("RxMagneto", "Version: " + s);
                }, throwable -> {
                    Log.d("RxMagneto", "Error");
                });
```

## All Features

Here is a list of all the featues offered by RxMagneto. I will be actively taking more feature requests and expand the library over time.


### Get Play Store URL

Gets the Play Store URL of the specified package. You can make this call in the main thread of the application.

```java
Observable<String> urlObservable = rxMagneto.grabUrl(packageName);
```

### Get Verified Play Store URL

Gets the verified Play Store URL of the specified package. You can NOT make this call in the main thread of the application.

```java
Observable<String> urlObservable = rxMagneto.grabVerifiedUrl(packageName);
```

### Get Version

Gets the latest version of the specified package from Play Store. You can NOT make this call in the main thread of the application.

```java
Observable<String> versionObservable = rxMagneto.grabVersion(packageName);
```

### Check If Update Is Available

Check if an update is available for the specified package from Play Store. You can NOT make this call in the main thread of the application.

```java
Observable<Boolean> updateAvailableObservable = rxMagneto.isUpgradeAvailable(packageName);
```

### Get Downloads

Gets the no. of downloads of the specified package from Play Store. You can NOT make this call in the main thread of the application.

```java
Observable<String> downloadsObservable = rxMagneto.grabDownloads(packageName);
```

### Get Last Published Date

Gets the last published date of the specified package from Play Store. You can NOT make this call in the main thread of the application.

```java
Observable<String> publishedDateObservable = rxMagneto.grabPublishedDate(packageName);
```

### Get OS Requirements

Gets the OS requirements of the specified package from Play Store. You can NOT make this call in the main thread of the application.

```java
Observable<String> osRequirementsObservable = rxMagneto.grabOsRequirements(packageName);
```

### Get Content Rating

Gets the content rating of the specified package from Play Store. You can NOT make this call in the main thread of the application.

```java
Observable<String> contentRatingObservable = rxMagneto.grabContentRating(packageName);
```

### Get App Rating

Gets the app rating of the specified package from Play Store. You can NOT make this call in the main thread of the application.

```java
Observable<String> appRatingObservable = rxMagneto.grabAppRating(packageName);
```

### Get App Ratings Count

Gets the no. of app ratings of the specified package from Play Store. You can NOT make this call in the main thread of the application.

```java
Observable<String> appRatingsCount = rxMagneto.grabAppRatingsCount(packageName);
```

### Get Recent Changelog (as Array)

Gets the recent changelog (as array) of the specified package from Play Store. You can NOT make this call in the main thread of the application.

```java
Observable<ArrayList<String>> changelogObservable = rxMagneto.grabPlayStoreRecentChangelogArray(packageName);
```

### Get Recent Changelog (as String)

Gets the recent changelog (as string) of the specified package from Play Store. You can NOT make this call in the main thread of the application.

```java
Observable<String> changelogObservable = rxMagneto.grabPlayStoreRecentChangelog(packageName);
```

# Contribution

This library is quite powerful and offers a lot of features. But I will love to have more feature requests from you to expand it further. If you find a bug or would like to improve any aspect of it, feel free to contribute with pull requests.

# About The Author

### Aritra Roy

Android & Backend Developer. Blogger. Designer. Fitness Enthusiast.

<a href="https://play.google.com/store/apps/details?id=com.codexapps.andrognito&hl=en" target="_blank"><img src="https://github.com/aritraroy/social-icons/blob/master/play-store-icon.png?raw=true" width="60"></a> <a href="https://blog.aritraroy.in/" target="_blank"><img src="https://github.com/aritraroy/social-icons/blob/master/medium-icon.png?raw=true" width="60"></a>
<a href="http://stackoverflow.com/users/2858654/aritra-roy" target="_blank"><img src="https://github.com/aritraroy/social-icons/blob/master/stackoverflow-icon.png?raw=true" width="60"></a>
<a href="https://twitter.com/aritraroy" target="_blank"><img src="https://github.com/aritraroy/social-icons/blob/master/twitter-icon.png?raw=true" width="60"></a>
<a href="http://linkedin.com/in/aritra-roy"><img src="https://github.com/aritraroy/social-icons/blob/master/linkedin-icon.png?raw=true" width="60"></a>


# License

```
Copyright 2017 aritraroy

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
