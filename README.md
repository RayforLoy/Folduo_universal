**Vibe-coded with GPT-6 Astra in Codex.**

English | [日本語](README.ja.md)

# Folduo

I built this out of curiosity. I don't plan to actively develop or maintain it. I may make changes if something sparks my interest, but otherwise expect this repository to remain mostly untouched.

An experimental app for Galaxy Z Fold7, Fold8, and compatible book-style foldables. It uses hinge angle to create a frosted-glass transition between the cover and inner screens, while working with regular apps without replacing your launcher.

[Download v0.2.0](https://github.com/RayforLoy/Folduo_universal/releases/tag/v0.2.0)

## Requirements

- **A compatible book-style foldable.** Galaxy Z Fold7 families (`SM-F966*`, `SC-56F`, `SCG34`) and Galaxy Z Fold8 `SM-F9710` are recognized. Other phones are enabled when Android advertises concurrent inner-primary and cover-primary display states; devices without those capabilities receive a clear error instead of a model-based rejection.
- Tested on Android 16 / One UI 8.5, build `F966ZSCS1BZH4`. Other regional variants are enabled but still require device testing.
- [Shizuku](https://shizuku.rikka.app/guide/setup/), installed and running. Tested with `13.6.0.r1086.2650830c`.
- The supported Samsung stock interactive wallpaper, configured as described below.

No root required. Once set up, it can run without USB if Shizuku is started through wireless debugging. Shizuku must be restarted after a reboot. Long-term stability without USB has not been verified.

## Setup

Folduo supports English, Japanese, and Simplified Chinese. At the top of the app, tap **Language / 言語** and choose **English**, **日本語**, **简体中文**, or **System default**. The choice is saved and also appears in Android’s app language settings. The system-default option follows the device language.

### 1. Start Shizuku

Follow the [official setup guide](https://shizuku.rikka.app/guide/setup/) to install and start Shizuku using wireless debugging or a computer.

### 2. Configure the stock wallpaper

Folduo directly uses Android's standard hinge-angle sensor when it provides fine-grained values. On the tested Samsung devices that sensor mainly reported 0°, 90° and 180°, so Samsung's interactive wallpaper is used as the fine-angle fallback through the Shizuku helper. The app does not estimate the angle using two gyroscopes.

1. Stop Folduo and any other fold-animation or display-control helpers.
2. Set the inner home screen to the Samsung stock interactive wallpaper identified internally as `video_002.mp4`. The cover home screen must use its matching stock image, `sub_wallpaper_002`. Wallpaper names in Settings vary by OS version.
3. To get fine-grained angles on the cover screen too, use the helper below to apply the same stock interactive wallpaper there. **This changes the cover home wallpaper in One UI as well.** Keep your original wallpaper if you want to restore it later.

Download and extract `folduo-wallpaper-setup-0.2.0.zip` from the release. Install Python 3 and Android SDK platform-tools (ADB). Connect one phone with USB debugging authorized, then run these commands in the extracted folder:

```sh
python3 cover-wallpaper.py status
python3 cover-wallpaper.py apply
```

`status` checks the wallpaper without changing it. `apply` changes the cover home wallpaper only, not the lock screen. Add `--adb /path/to/adb` if ADB is not on your PATH, or `--serial DEVICE_SERIAL` if multiple devices are connected.

The helper refuses to overwrite unsupported or custom wallpapers. If it reports `other wallpaper` or `Expected inner angle-aware wallpaper unavailable`, the required wallpaper is not configured. It uses assets already installed on your phone; no Samsung wallpaper files are included here.

To build the helper yourself, prepare the [build environment](#build-from-source), then run from the repository root:

```sh
python3 tools/build-wallpaper-helper.py
python3 tools/cover-wallpaper.py status
python3 tools/cover-wallpaper.py apply
```

### 3. Install and start the app

1. Install `Folduo-0.2.0.apk` from the release. With ADB: `adb install -r Folduo-0.2.0.apk`.
2. Open **Folduo**, tap **Connect Shizuku**, and grant access.
3. Tap **Allow display over other apps**. Allow notifications too.
4. Read the screen-capture explanation, then tap **Allow temporary screen access and enable**.
5. With the phone unlocked, close it fully once to initialize. Open an app such as Calculator and slowly fold and unfold the phone.

## Controls and limitations

The cover screen uses Samsung's normal navigation. The inner screen has a small custom bar for **Recents, Home, Back and Settings**. Native navigation gestures and the notification/quick-settings shade are not fully available on the inner screen. Use the custom bar, the cover screen, or stop the app when you need the normal controls.

- Both displays are kept on while active, increasing battery use. Normal display control resumes when stopped or locked.
- The transition uses a frozen image. Video and games do not keep playing in that image.
- Home, Recents and other system screens are not handled like regular app tasks. Protected screens and apps that refuse display migration are unsupported.
- App resizing can still cause layout shifts. Samsung's private APIs and wallpaper responses may change after OS updates.
- If Shizuku stops, the animation stops. Automatic recovery is not guaranteed.

## Folduo home

To use the included launcher, tap **Use Folduo as the home app** in Folduo settings and select Folduo. Tap an icon to open an app, long-press to replace it, or use **All apps** to browse installed apps. Tap the **Folduo** button on the home screen to return to settings. English, Japanese, and Simplified Chinese are supported.

At each completed fold endpoint, Folduo makes the visible physical panel logical display 0. Normal app launches and primary-display utilities such as One Hand Operation+ therefore follow the inner screen while unfolded and the cover screen while folded. Folduo home remains available as an optional launcher.

Three Calculator/home round trips, moving the home between both displays, long-press selection and opening settings passed on the phone with both displays held on by the helper. The final check with physical folding is still pending. If an app does not open after unfolding, close the phone and launch it from the cover home screen.

## Stop and restore

- **Stop:** open the app and tap **Stop and release display control**. Do this before uninstalling.
- **Resume:** make sure Shizuku is running, then use **Resume** in the notification or **Resume animation** in the app. Unlock and close the phone fully once.
- **After a reboot:** start Shizuku again, then resume the app if needed.
- **If the display or controls get stuck:** close the phone and stop the app from the cover screen. If that is not possible, reboot and disable the app's always-on mode before restarting Shizuku.
- **Restore your wallpaper:** stop the app and choose a wallpaper in Android Settings. To restore the specific stock cover image changed by the helper, run this in the extracted helper folder:

```sh
python3 cover-wallpaper.py restore-stock
```

From a source checkout, use `python3 tools/cover-wallpaper.py restore-stock`. This restores the known stock image, not an arbitrary previous wallpaper. It refuses to overwrite a different wallpaper selected since setup.

## Build from source

Use Git, JDK 17 and the Android SDK. Set `JAVA_HOME` to your JDK and `ANDROID_HOME` to your SDK; add `platform-tools` to PATH for ADB.

```sh
sdkmanager "platforms;android-37.0" "build-tools;36.0.0" "platform-tools"
sdkmanager --licenses

git clone https://github.com/RayforLoy/Folduo_universal.git
cd Folduo_universal
git checkout v0.2.0
./gradlew :app:assembleRelease :app:testDebugUnitTest :app:lintRelease
```

Output: `app/build/outputs/apk/release/app-release.apk`. Use `gradlew.bat` on Windows. Builds were verified on macOS with Java 17; Windows and Linux have not been tested end to end.

The wrapper pins Gradle 9.5.1 and verifies its checksum. AGP is 9.2.1; compile SDK is 37, target SDK is 36, and minimum SDK is 33. Initial builds need internet access to download dependencies. The wallpaper helper also needs Python 3.

The release APK uses the existing experimental debug signing certificate. Signing keys are not published. Your own build uses your local certificate and cannot directly replace the release APK. Stop and uninstall the existing app before switching signatures; settings and permissions will need to be configured again. Uninstalling does not restore the wallpaper.

Release downloads include `SHA256SUMS`. Compare the APK with `shasum -a 256 Folduo-0.2.0.apk` on macOS or `sha256sum Folduo-0.2.0.apk` on Linux.

## Screen access

Shizuku grants ADB shell-level access. Captured images are used in memory and are not saved or uploaded by the app. Protected screens are excluded. The app has no internet permission, analytics or ads. Do not post private screenshots, device serials or unedited diagnostic logs in public issues.

## License

Original code: [MIT](LICENSE). See [third-party notices](THIRD_PARTY_NOTICES.md) for dependencies. No Apple or Samsung UI assets, wallpapers or videos are distributed. This project is not affiliated with Apple, Samsung or Shizuku.
