# Calculator

A clean, modern Android calculator app built with Java and Android Jetpack. Designed with a sleek dark aesthetic, responsive layout, and exact arithmetic precision.

## Features

- **Core Operations**: Addition, subtraction, multiplication, division, and percentage calculations.
- **Exact Precision**: High-precision calculation engine backed by `BigDecimal` to eliminate floating-point rounding errors.
- **Expression History**: Displays real-time operation history above the main display.
- **Dynamic Auto-Sizing Text**: Display dynamically scales text down as numbers grow, preventing clipping.
- **Tactile Feedback**: Integrated haptic touch feedback on button interactions.
- **Smart Shortcuts**:
  - Long-press **DEL** to trigger quick full clear (AC).
  - Long-press **Display** to instantly copy calculation result to the clipboard.
- **Safe Evaluation**: Handles chained operations seamlessly and guards against divide-by-zero errors.
- **Modern Architecture**: Clean separation between pure arithmetic logic and UI layer with type-safe **View Binding**.

## Requirements

| Tool | Version |
|---|---|
| Android Studio | Ladybug 2024.2 or later |
| Android SDK | 24+ (Android 7.0 Nougat) |
| JDK | 11 |

## Getting Started

### Clone and Open
Open the repository root directory in Android Studio. Gradle will configure and sync automatically.

### Command Line Builds

```shell
# Run unit tests on the JVM
./gradlew test

# Assemble debug APK
./gradlew assembleDebug

# Run instrumented tests on connected device/emulator
./gradlew connectedAndroidTest
```

## Project Structure

```
├── app/
│   └── src/
│       ├── main/
│       │   ├── java/com/tamim/calculator/
│       │   │   ├── Calculator.java      # High-precision arithmetic engine
│       │   │   └── MainActivity.java    # View Binding, haptic touch & UI handling
│       │   └── res/
│       │       ├── layout/activity_main.xml
│       │       ├── drawable/            # Custom pill ripple drawables
│       │       └── values/              # Color system, themes, strings
│       └── test/java/com/tamim/calculator/
│           └── CalculatorTest.java      # Comprehensive JVM unit tests
├── build.gradle.kts
├── settings.gradle.kts
└── gradlew
```

## License

MIT
