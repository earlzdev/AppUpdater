# Android Self Update Library

Это библиотека для Android, которая позволяет обновлять приложение через сторонний сайт. Библиотека включает в себя тестовую часть для серверной стороны и саму библиотеку для Android-приложений.

## Описание

Библиотека предоставляет простой способ для динамического обновления версии Android-приложения с использованием внешнего сервера. Она позволяет проверять доступность обновлений, загружать новые версии и выполнять обновление без необходимости размещать новое APK в Google Play.

## Функции

- Проверка наличия обновлений на стороннем сервере.
- Загрузка и установка обновлений.
- Возможность интеграции с любым backend-сервером для управления версиями.
- Простой интерфейс для интеграции с Android-приложениями.

## Установка

Для добавления библиотеки в ваше приложение, добавьте следующую зависимость в файл `build.gradle`:

```gradle
dependencies {
    implementation 'com.github.earlzdev:AppUpdater:1.0.0-RC'
}
```

Так же укажите `jitpack` в списке репозиториев
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


## Использование

Перед использованием инициализируйте библиотеку при создании вашего `MainActivity`
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

Проверка наличия доступного обновления
```kotlin
UpdaterApiRegistry.updateAvailabilityUseCase.updateAvailable().onSuccess { updateAvailable: Boolean ->
    // ...           
}.onFailure {
    // ...
}
```

Начать процесс обновления
```kotlin
UpdaterApiRegistry.updateManager.startUpdate()
```

Для того чтобы следить за актуальным состоянием процесса обновления можно подписаться на поток в `UpdateStateHolder`
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

Если пользователь согласился дать разрешение на установку приложения
```kotlin
UpdaterApiRegistry.updateManager.onAgreedToGivePermission()
```

В противном случае
```kotlin
UpdaterApiRegistry.updateManager.onDeclinedToGivePermission()
```

Полный пример можно найти в каталоге `example`.

## Пример

Демо приложение

https://github.com/user-attachments/assets/960da344-d609-47ec-b4b3-1903f130fc90

https://github.com/user-attachments/assets/b7a6cfe2-1e2c-429b-afc0-054496487b80

## Serverside

Пример реализации сервера находится в каталоге `example_backend`. Вы можете встроить текущую реализацию в свой контур.

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
