# GlyphType

> Type • Animate • Glow — on your Nothing Phone's Glyph Matrix.

A minimalist Android app that converts text into scrolling dot-matrix animations for the Nothing Phone (4a) Pro's 13×13 Glyph Matrix. Built with Jetpack Compose and the Nothing Glyph SDK.

---

## What It Does

```
Input:  "HELLO"
Output: Scrolling LED animation on the Glyph Matrix ← ← ←
```

1. **Type any text** — letters, numbers, symbols
2. **Choose animation** — smooth scroll or typewriter effect
3. **Adjust speed** — from blazing fast (30ms) to slow reveal (500ms)
4. **Preview live** — built-in 13×13 circular dot-matrix simulator
5. **Set as Glyph Toy** — runs on your Always-On Glyph Display

---

## Features

| Feature | Status |
|---------|--------|
| Custom 5×5 pixel font (A-Z, 0-9, 16 symbols) | ✅ |
| Scroll Left animation | ✅ |
| Typing effect animation | ✅ |
| 13×13 on-screen simulator with LED glow effects | ✅ |
| Adjustable speed (30ms–500ms per frame) | ✅ |
| Glyph Toy integration (AOD support) | ✅ |
| Android 12+ Splash Screen API | ✅ |
| Adaptive icon with dark/light mode (monochrome layer) | ✅ |
| Precomputed frames (zero-allocation playback) | ✅ |
| Coroutine-based animation (non-blocking UI) | ✅ |
| Nothing Glyph Matrix SDK integration | ✅ |
| Graceful fallback on non-Nothing devices | ✅ |
| Monochrome Nothing OS-inspired design | ✅ |

---

## Architecture

```
┌─────────────────────────────────────────────┐
│  MainActivity                               │
│  Splash Screen → Compose Entry Point        │
├─────────────────────────────────────────────┤
│  MainScreen              │  GlyphSimulator  │
│  - GlyphType header      │  - 13×13 grid    │
│  - Text input (pill)     │  - Circular dots  │
│  - Animation type        │  - LED glow FX   │
│  - Speed slider          │  - Real-time     │
│  - Play/Stop (pill)      │    preview       │
│  - Set as Glyph Toy      │                  │
├─────────────────────────────────────────────┤
│  MainViewModel (Coroutine Animation Loop)   │
├──────────────┬──────────────────────────────┤
│ TextRenderer │ FrameGenerator               │
│ text → bitmap│ bitmap → IntArray frames     │
├──────────────┴──────────────────────────────┤
│ PixelFont (5×5 pixel font: A-Z, 0-9, etc.) │
├─────────────────────────────────────────────┤
│ GlyphController (Nothing SDK Wrapper)       │
│ → setAppMatrixFrame(int[169])               │
├─────────────────────────────────────────────┤
│ TextGlyphToyService (AOD Glyph Toy)         │
│ → Runs animation on Always-On Display       │
└─────────────────────────────────────────────┘
```

---

## Tech Stack

| Component | Technology |
|-----------|-----------|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM (ViewModel + StateFlow) |
| Concurrency | Kotlin Coroutines |
| Splash Screen | AndroidX Core SplashScreen API |
| SDK | Nothing Glyph Matrix Developer Kit v2.0 |
| Target Device | Nothing Phone (4a) Pro |
| Matrix | 13×13 (169 LEDs), brightness 0–255 |
| Min SDK | 34 (Android 14) |

---

## Project Structure

```
app/src/main/
├── AndroidManifest.xml
├── java/com/deepu/glyphtext/
│   ├── MainActivity.kt                 # Splash + Compose entry point
│   ├── engine/
│   │   ├── PixelFont.kt                # 5×5 pixel font definitions
│   │   ├── TextRenderer.kt             # Text → horizontal bitmap
│   │   └── FrameGenerator.kt           # Bitmap → animation frames
│   ├── glyph/
│   │   ├── GlyphController.kt          # Nothing SDK lifecycle wrapper
│   │   ├── GlyphToyPrefs.kt            # SharedPreferences for Glyph Toy
│   │   └── TextGlyphToyService.kt      # AOD Glyph Toy service
│   ├── ui/
│   │   ├── MainScreen.kt               # Full UI (header, input, buttons)
│   │   ├── GlyphSimulatorView.kt       # 13×13 circular dot simulator
│   │   └── theme/
│   │       ├── Color.kt                # Monochrome palette
│   │       ├── Theme.kt                # Dark color scheme
│   │       └── Type.kt                 # Sans-serif typography
│   └── viewmodel/
│       └── MainViewModel.kt            # Animation + state coordination
└── res/
    ├── drawable/
    │   ├── ic_launcher_foreground.xml   # Dot-matrix "G" with motion trails
    │   ├── ic_launcher_background.xml   # Pure black background
    │   └── ic_toy_preview.xml           # Glyph Toy picker icon
    ├── mipmap-anydpi-v26/
    │   ├── ic_launcher.xml              # Adaptive icon (with monochrome)
    │   └── ic_launcher_round.xml        # Round adaptive icon
    ├── values/
    │   ├── colors.xml                   # Brand colors
    │   ├── strings.xml                  # App strings
    │   └── themes.xml                   # Base dark theme
    └── values-v31/
        └── splash_theme.xml             # Android 12+ splash config
```

---

## Design Language

GlyphType follows a **monochrome Nothing OS** design philosophy:

- **Background:** Pure black (`#000000`)
- **Primary accent:** White (`#FFFFFF`)
- **Surfaces:** Near-black (`#1A1A1A`)
- **Borders:** Dark gray (`#333333`)
- **Typography:** Sans-serif with wide letter-spacing
- **Buttons:** Pill-shaped with subtle scale-down press animation
- **Simulator:** Circular LED dots with radial glow effects
- **Icon:** Dot-matrix "G" with motion trail — auto-tints for light/dark mode via monochrome layer

---

## Getting Started

### Prerequisites

- **Android Studio** (Ladybug or newer)
- **Nothing Phone (4a) Pro** for real Glyph output *(optional — simulator works on any device)*
- Android SDK 34+
- JDK 17+

### Build & Run

```bash
git clone https://github.com/deepudxd/GlyphTextAnimator.git
cd GlyphTextAnimator
```

1. Open in Android Studio → `File → Open` → select project root
2. Wait for Gradle sync to complete
3. Run on a connected device or emulator (`Shift+F10`)

### Glyph Toy Setup

1. Open the app and type your text
2. Tap **"Set as Glyph Toy"**
3. Go to **Settings → Glyph Interface → Glyph Toy**
4. Select **"GlyphType"** from the toy list
5. Your animation now runs on the Always-On Display

### SDK Note

The app includes the Nothing Glyph Matrix SDK (`glyph-matrix-sdk-2.0.aar`) in `app/libs/`. To update, download from the [official repository](https://github.com/Nothing-Developer-Programme/GlyphMatrix-Developer-Kit).

---

## Contributing

Contributions welcome!

1. Fork the repo
2. Create your feature branch (`git checkout -b feature/cool-animation`)
3. Commit your changes (`git commit -m 'Add wave animation'`)
4. Push to the branch (`git push origin feature/cool-animation`)
5. Open a Pull Request

### Ideas

- 🌊 Wave animation effect
- 🔄 Bounce/ping-pong scroll
- 📊 Custom brightness per character
- 🎨 Pattern generator (checkerboard, spiral, etc.)
- ⏱️ Clock/timer display mode
- 📱 Home screen widget

---

## License

MIT License — see [LICENSE](LICENSE) for details.

## Acknowledgments

- [Nothing Developer Programme](https://github.com/Nothing-Developer-Programme) — Glyph Matrix SDK
- [GlyphMatrix-Developer-Kit](https://github.com/Nothing-Developer-Programme/GlyphMatrix-Developer-Kit) — SDK documentation
- [GlyphMatrix-Example-Project](https://github.com/Nothing-Developer-Programme/GlyphMatrix-Example-Project) — Kotlin reference patterns

---

**Made with ❤️ for the Nothing community**
