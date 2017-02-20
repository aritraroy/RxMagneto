# RxMagneto

A fast, simple and powerful Play Store information fetcher for Android.

![RxMagneto](https://github.com/aritraroy/RxMagneto/blob/master/raw/logo_250.png)

This library allows you to **fetch various live information from Play Store** of your app or any other app of your choice. With just a few lines of code, you can get access to lots of app data fetched live from Google Play Store.

It has been named after the famous anti-villian from X-Men. This library has been **completely written using RxJava** giving you powrful controls on how you want to use it.

![RxMagneto](https://github.com/aritraroy/RxMagneto/blob/master/raw/github_promo.png)

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

## Usecases

There are several usecases of this library. Here are some common examples for you to get started, but be creative and make the most use of it.

### App Updates

This is a common problem for many developers wanting to check if their users are using the latest version of the app available on Play Store. With RxMagneto, you can get it done with just a single line of code.

### Recent Changelog

You might want to show your users the latest changelog when they update the app. RxMagneto can help you fetch the most recent changelog directly from Play Store with just a single line of code.

### User Ratings

Many developers ask their users to rate the app after a certain period of time. You can also show these users your current rating and ratings count directly from Play Store to encourage users more to rate your app.
