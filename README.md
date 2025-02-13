# Android Self Update Library

This is an Android library that allows you to update your application through an external website. The library includes both the server-side test part and the Android library itself.

## Description

The library provides an easy way to dynamically update the version of an Android app using an external server. It allows checking for available updates, downloading new versions, and performing updates without the need to publish a new APK on Google Play.

## Features

- Check for available updates on an external server.
- Download and install updates.
- Integration with any backend server to manage versions.
- Simple interface for integration with Android apps.

## Installation

To add the library to your app, add the following dependency to your build.gradle file:

```gradle
dependencies {
    implementation 'com.github.earlzdev:AppUpdater:1.0.0-RC'
}
```

Also, include jitpack in your list of repositories:

```gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
      mavenCentral()
      maven { url 'https://jitpack.io' }
    }
}
```

[![](https://jitpack.io/v/earlzdev/AppUpdater.svg)](https://jitpack.io/#earlzdev/AppUpdater)


## Usage

Before using the library, initialize it in your MainActivity

```kotlin
AppUpdateInitializer.initialize(
    activity = activity,
    config = AppUpdaterInitConfig(
        actualVersionCode = BuildConfig.VERSION_CODE,
        actualVersionName = BuildConfig.VERSION_NAME,
        downloadApkUrl = "http://your_host:port/download",
        updateAvailabilityCheckUrl = "http://your_host:port/check_version",
        bearerToken = "e.jwt", // JWT token if you use JWT authentication
        logEnabled = BuildConfig.DEBUG // If you want enable logging
    )
)
```

Check for available updates 
```kotlin
UpdaterApiRegistry.updateAvailabilityUseCase.updateAvailable().onSuccess { updateAvailable: Boolean ->
    // ...           
}.onFailure {
    // ...
}
```

Start the update process
```kotlin
UpdaterApiRegistry.updateManager.startUpdate()
```

To track the current update process state, you can subscribe to the stream in UpdateStateHolder:
```kotlin
val updateStepFlow: StateFlow<UpdateStep?> = UpdateStateHolder.currentStateFlow()
    .onEach(::handleUpdateStep)
    .stateIn(viewModelScope, SharingStarted.Eagerly, null)

private suspend fun handleUpdateStep(updateStep: UpdateStep?) {
    when (updateStep?.failure) {
        is NoInstallPermissionException -> // Показ алерта для запроса разрешения
        is CancelledInstallationException -> // Выбрасывается если пользователь нажал кнопку "Отмена" в системном диалоге подтверждения установки
        is UnauthorizedException -> {
            // Обновление JWT токена
            // ...
            // Далее вызов метода для продолжения процесса обновления
            UpdaterApiRegistry.updateManager.updateTokenAndRetry("new_token")
        }
        else -> // Обработка остальных исключений
    }
}
```

If the user agrees to give permission for installation
```kotlin
UpdaterApiRegistry.updateManager.onAgreedToGivePermission()
```

Otherwise
```kotlin
UpdaterApiRegistry.updateManager.onDeclinedToGivePermission()
```

A full example can be found in the example folder.

## Example

Demo app

https://github.com/user-attachments/assets/960da344-d609-47ec-b4b3-1903f130fc90

https://github.com/user-attachments/assets/b7a6cfe2-1e2c-429b-afc0-054496487b80

## Serverside

An example of the server-side implementation can be found in the example_backend folder. You can integrate the current implementation into your own infrastructure.

## Feedback

Feel free to contribute or write an issue.

## License

```
MIT License

Copyright (c) 2025 Saushin Ilya (@earlzdev)

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
