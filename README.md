# Tic Tac Toe Master

A native Android Tic Tac Toe game written in **Kotlin + Jetpack Compose**. Works fully offline, needs no
account, no internet permission, no ads and has no paid features.

> **Status:** the source code and unit tests are complete, but this project was written in an
> environment without the Android SDK or internet, so it has **not been compiled, run on a device or
> had its unit tests executed yet**. The game-logic algorithm (rules + Minimax) was checked with an
> equivalent script. Please follow "Build the APK" below; if the build prints an error, send it over and it
> can be fixed quickly. Use the checklist at the bottom to verify everything on your phone.

## Build the APK (free, pick one)

### Option A: Android Studio (easiest)
1. Install the free [Android Studio](https://developer.android.com/studio) (it bundles JDK 17 and the SDK).
2. **File > Open** and choose this `TicTacToeMaster` folder. Let Gradle sync finish (first time downloads
   dependencies; needs internet once). If Studio offers an "Android Gradle Plugin upgrade", you can accept it or ignore it.
3. **Build > Build App Bundle(s) / APK(s) > Build APK(s)**.
4. Click **locate** in the popup, or open `app/build/outputs/apk/debug/app-debug.apk`.

### Option B: GitHub Actions (no installation at all)
1. Create a free GitHub repository and upload this whole folder (keep the `.github` folder).
2. Open the **Actions** tab > **Build APK** > **Run workflow**.
3. When it finishes, download the artifact **TicTacToeMaster-debug-apk** and unzip it to get `app-debug.apk`.
   (The workflow also runs the unit tests first.)

### Option C: Command line
Needs JDK 17, the Android SDK (platform 35, build-tools) and Gradle 8.9:
```
gradle testDebugUnitTest      # run the unit tests
gradle assembleDebug          # build the APK
```
Run `gradle wrapper --gradle-version 8.9` once if you want a `./gradlew` script. The APK is written to
`app/build/outputs/apk/debug/app-debug.apk`. (`gradle assembleRelease` builds an installable release APK
signed with the debug key; create your own keystore before publishing on Google Play.)

## Install on your phone
- Copy `app-debug.apk` to the phone (USB, cloud drive, email...), open it and allow
  "Install unknown apps" for your file manager/browser when asked. Or with USB debugging on: `adb install app-debug.apk`.
- Requires **Android 8.0 (API 26) or newer**.

## Features
| Area | What is included |
|---|---|
| Design | Dark navy background with purple/blue gradients, glowing X and O, drawn-in animations, rounded large buttons, responsive portrait/landscape/tablet layouts, light theme option |
| Splash / icon | Animated splash (logo + name), Android 12+ system splash, adaptive launcher icon |
| Menu | Play with Friend, Play with Computer, Choose Difficulty, Game Statistics, Settings, How to Play |
| Two players | Same device, X then O, turn indicator, win/draw detection, Play Again |
| Vs computer | Easy = random legal moves, Medium = win/block tactics mixed with 35% random moves, Hard = Minimax with alpha-beta (never loses). You play X, computer plays O |
| Board | Large touch squares, winning three highlighted with a pulsing glow, no moves after game end, Restart/Play Again + Home buttons, leave-game confirmation, Android back support |
| Statistics | Total games, vs-computer wins/losses/draws/win %/current & best win streak, separate two-player results (P1 wins, P2 wins, draws). Saved with DataStore |
| Sound | Placement sounds, victory, draw and defeat sounds (synthesised in code, no audio files). Muted by Sound setting and when the phone is on silent/vibrate |
| Settings | Sound, vibration, dark/light theme, starting player (X / O / alternate), reset statistics (with confirmation). Saved locally |
| Extras | Tutorial on first launch (and from menu/settings), confetti celebration, offline, no accounts |
| Accessibility | Content descriptions for every square ("Row 1, column 2, empty"), live-region status text, 48dp+ touch targets, AA-contrast text colours |

## Project layout
```
app/src/main/java/com/tictactoemaster/
  game/       GameModels.kt (Board, GameState, Rules), ComputerPlayer.kt   <- pure Kotlin, no UI
  data/       Models.kt, SettingsRepository.kt (DataStore)
  platform/   SoundManager.kt, Haptics.kt
  ui/         AppViewModel.kt, TicTacToeApp.kt (navigation), theme/, components/, screens/
app/src/test/ GameLogicTest.kt  (win lines, draw, invalid moves, Hard never loses)
```

## Design notes
- All moves go through `GameState.play()`, which rejects occupied squares, bad indexes and moves after the
  game ended, so rapid taps or a misbehaving AI can never corrupt the board.
- A game is recorded in the statistics exactly once, when it ends. Abandoned games are not counted.
- A win streak = consecutive wins vs the computer; a loss or draw resets it.
- When the computer wins, a softer "defeat" sound plays instead of the victory fanfare, and there is no confetti.

## Manual test checklist
- [ ] Splash shows, then tutorial on first launch only; menu afterwards
- [ ] Two players: alternate X/O, win highlight + confetti, draw message, Play Again
- [ ] Vs computer on Easy, Medium and Hard (Hard: you can only draw or lose)
- [ ] Starting player = O makes the computer open; Alternate swaps each Play Again
- [ ] Mashing the squares quickly, restarting repeatedly: no crash, no overwritten squares
- [ ] Back during a game shows the leave confirmation; back on Stats/Settings returns to the menu
- [ ] Statistics update, survive closing the app, and Reset (with confirmation) clears them
- [ ] Sound/vibration toggles work; phone on silent = no sound
- [ ] Light/dark theme persists after restart; airplane mode changes nothing
