# ✨ Glyph Text Animator

> Turn text into scrolling LED animations on the Nothing Phone (4a) Pro's Glyph Matrix.

An open-source Android app made for fun that converts text input into pixel art and animates it across the 13×13 Glyph Matrix on the back of the Nothing Phone (4a) Pro.

---

## 🎬 What It Does

```
Input:  "HELLO"
Output: Scrolling LED animation on the Glyph Matrix ← ← ←
```

1. **Type any text** — letters, numbers, symbols
2. **Choose animation** — smooth scroll or typewriter effect
3. **Adjust speed** — from blazing fast (30ms) to slow reveal (500ms)
4. **Watch it animate** — both on-screen simulator and real Glyph LEDs

## 📱 Screenshots

The app includes a built-in **13×13 LED simulator** so you can preview animations on any Android device — no Nothing Phone required!

## 🏗️ Architecture

```
┌─────────────────────────────────────────┐
│  MainActivity (Compose Entry Point)     │
├─────────────────────────────────────────┤
│  MainScreen          │  GlyphSimulator  │
│  - Text input        │  - 13×13 grid    │
│  - Animation type    │  - LED glow FX   │
│  - Speed slider      │  - Real-time     │
│  - Play/Stop         │    preview       │
├─────────────────────────────────────────┤
│  MainViewModel (Coroutine Animation)    │
├──────────────┬──────────────────────────┤
│ TextRenderer │ FrameGenerator           │
│ text → bitmap│ bitmap → IntArray frames │
├──────────────┴──────────────────────────┤
│ PixelFont (5×5 pixel font: A-Z, 0-9)   │
├─────────────────────────────────────────┤
│ GlyphController (Nothing SDK Wrapper)   │
│ → setAppMatrixFrame(int[169])           │
└─────────────────────────────────────────┘
```

## ✅ Features

| Feature | Status |
|---------|--------|
| Custom 5×5 pixel font (A-Z, 0-9, 16 symbols) | ✅ |
| Scroll Left animation | ✅ |
| Typing effect animation | ✅ |
| 13×13 on-screen simulator with glow effects | ✅ |
| Adjustable speed (30ms–500ms per frame) | ✅ |
| Precomputed frames (zero-allocation playback) | ✅ |
| Coroutine-based animation (non-blocking UI) | ✅ |
| Nothing Glyph Matrix SDK integration | ✅ |
| Graceful fallback on non-Nothing devices | ✅ |
| Nothing-inspired dark theme | ✅ |

## 🚀 Getting Started

### Prerequisites

- **Android Studio** (Ladybug or newer)
- **Nothing Phone (4a) Pro** for real Glyph output *(optional — simulator works on any device)*
- Android SDK 34+

### Build & Run

1. **Clone the repo**
   ```bash
   git clone https://github.com/YOUR_USERNAME/GlyphTextAnimator.git
   cd GlyphTextAnimator
   ```

2. **Open in Android Studio** → `File → Open` → select project root

3. **Wait for Gradle sync** to complete

4. **Run** on a connected device or emulator (`Shift+F10`)

### SDK Note

The app includes the Nothing Glyph Matrix SDK (`glyph-matrix-sdk-2.0.aar`) in `app/libs/`. If you need to update it, download from the [official repository](https://github.com/Nothing-Developer-Programme/GlyphMatrix-Developer-Kit).

## 🛠️ Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose + Material3
- **Architecture:** ViewModel + StateFlow
- **Concurrency:** Kotlin Coroutines
- **SDK:** Nothing Glyph Matrix Developer Kit v2.0
- **Target:** Nothing Phone (4a) Pro (`Glyph.DEVICE_25111p`)
- **Matrix:** 13×13 (169 LEDs), brightness 0–255

## 📂 Project Structure

```
app/src/main/java/com/deepu/glyphtext/
├── MainActivity.kt              # Compose entry point
├── engine/
│   ├── PixelFont.kt             # 5×5 pixel font definitions
│   ├── TextRenderer.kt          # Text → horizontal bitmap
│   └── FrameGenerator.kt        # Bitmap → animation frames
├── glyph/
│   └── GlyphController.kt       # Nothing SDK lifecycle wrapper
├── ui/
│   ├── MainScreen.kt            # Full UI screen
│   ├── GlyphSimulatorView.kt    # 13×13 LED grid simulator
│   └── theme/                   # Nothing-inspired dark theme
└── viewmodel/
    └── MainViewModel.kt         # Coordinates everything
```

## 🤝 Contributing

This is a fun project — contributions welcome!

1. Fork the repo
2. Create your feature branch (`git checkout -b feature/cool-animation`)
3. Commit your changes (`git commit -m 'Add wave animation'`)
4. Push to the branch (`git push origin feature/cool-animation`)
5. Open a Pull Request

### Ideas for Contributions

- 🌊 Wave animation effect
- 🔄 Bounce/ping-pong scroll
- 📊 Custom brightness per character
- 🎨 Pattern generator (checkerboard, spiral, etc.)
- ⏱️ Clock/timer display mode
- 📱 Widget support

## 📄 License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- [Nothing Developer Programme](https://github.com/Nothing-Developer-Programme) for the Glyph Matrix SDK
- [GlyphMatrix-Developer-Kit](https://github.com/Nothing-Developer-Programme/GlyphMatrix-Developer-Kit) documentation
- [GlyphMatrix-Example-Project](https://github.com/Nothing-Developer-Programme/GlyphMatrix-Example-Project) for the Kotlin patterns

---

**Made with ❤️ for the Nothing community**
