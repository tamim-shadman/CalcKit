# CalcKit

A versatile, modern Android calculator and unit converter suite built with Java, Material Design Components, and Android Jetpack. Designed with a sleek AMOLED dark & light aesthetic, responsive portrait/landscape layouts, full calculation history, memory registers, advanced scientific capabilities, and exact arithmetic precision.

---

## ✨ Features

### 🧮 Pure Exact-Precision Arithmetic
- **`BigDecimal` Pipeline**: High-precision calculation engine eliminating floating-point rounding errors across all operations.
- **Contextual Percentage (`%`)**: Real-world percentage evaluation (e.g. `100 + 10% = 110`, `50 − 20% = 40`, `200 × 15% = 30`).
- **Repeated Equals**: Consecutively pressing `=` automatically repeats the last operator and operand (constant calculation).
- **Active Operator Feedback**: The pending operator button (`+`, `−`, `×`, `÷`) highlights visually until the next operand is entered.

### 🔬 Advanced Scientific Suite
- **Trigonometry**: `sin`, `cos`, and `tan` with clean degree normalization.
- **Angle Modes**: Real-time **DEG** / **RAD** toggle with on-screen mode badge.
- **Logarithms**: Common logarithm (`log` base 10) and Natural logarithm (`ln` base e).
- **Advanced Operations**: Factorial (`x!`), General Power (`xʸ`), Square Root (`√`), Square (`x²`), Reciprocal (`1/x`), Negate (`±`), and Constants (`π`, `e`).

### 💾 Memory Registers
- Standard physical calculator memory operations:
  - **MC**: Clear stored memory.
  - **MR**: Recall stored memory into display.
  - **M+**: Add current display value to memory.
  - **M−**: Subtract current display value from memory.
  - **MS**: Store current display value into memory.
- **Visual Memory Indicator**: An **"M"** badge illuminates in the header whenever memory contains a non-zero value.

### 📐 Multi-Category Offline Unit Converter
Integrated converter bottom sheet with live conversion as you type:
- **Length**: Meters, Kilometers, Centimeters, Millimeters, Miles, Yards, Feet, Inches.
- **Weight / Mass**: Kilograms, Grams, Milligrams, Metric Tons, Pounds, Ounces.
- **Temperature**: Celsius (°C), Fahrenheit (°F), Kelvin (K).
- **Digital Data**: Bytes, Kilobytes, Megabytes, Gigabytes, Terabytes.
- **Speed**: m/s, km/h, mph, Knots.
- **Area**: Square Meters, Square Kilometers, Square Feet, Acres, Hectares.
- **Features**: Live typing calculation, unit swap button (`⇄`), and one-tap copy result.

### 📜 Calculation History
- Persistent storage across app launches using `SharedPreferences`.
- Slide-up Material bottom sheet displaying previous operations with timestamps.
- One-tap to load any past result back into the current calculation.
- Long-press to copy calculation result or expression to clipboard.
- "Clear All" with confirmation dialog.

### ⚙️ Themes & Customization
- **Theme Selection**:
  - AMOLED Dark (deep blacks for battery savings and contrast)
  - Clean Light
  - Follow System Default
- **Feedback Controls**:
  - Haptic touch vibration toggle.
  - Subtle audio click sound toggle.

### 📱 Adaptive Layouts
- **Portrait**: Ergonomic 4×5 symmetric keypad with expandable scientific drawer and memory bar.
- **Landscape**: Dual-pane layout featuring dedicated scientific function keypad alongside the standard keypad.
- **State Preservation**: Survives device rotation and configuration changes without losing in-progress calculations or history.

---

## 🛠 Tech Stack & Requirements

| Component | Specification |
|---|---|
| **Architecture** | MVVM (`ViewModel`, `LiveData`, View Binding) |
| **Arithmetic** | Java `BigDecimal` with 16-digit `MathContext` |
| **UI System** | Android Jetpack & Material Components (Day/Night) |
| **Android Studio** | Ladybug 2024.2 or later |
| **Android SDK** | Min SDK 24 (Android 7.0 Nougat) / Target SDK 34 |
| **JDK** | Java 11+ |

---

## 🚀 Getting Started

### Clone and Open
Open the `Android-Calculator` directory in Android Studio. Gradle will configure and sync automatically.

### Command Line Builds

```shell
cd Android-Calculator

# Run JVM unit tests
./gradlew test

# Assemble debug APK
./gradlew assembleDebug

# Run instrumented tests on connected device/emulator
./gradlew connectedAndroidTest
```

---

## 📂 Project Structure

```
Android-Calculator/
├── app/
│   └── src/
│       ├── main/
│       │   ├── java/com/tamim/calculator/
│       │   │   ├── MainActivity.java                # UI controller, audio/haptic & actions
│       │   │   ├── Calculator.java                  # Backwards-compatible facade
│       │   │   ├── engine/
│       │   │   │   └── CalculatorEngine.java        # Arithmetic, trig, logs, powers & memory
│       │   │   ├── viewmodel/
│       │   │   │   └── CalculatorViewModel.java     # Lifecycle-aware state & calculations
│       │   │   ├── converter/
│       │   │   │   ├── UnitCategory.java            # Units, symbols & conversion factors
│       │   │   │   └── UnitConverter.java           # High-precision multi-unit conversion
│       │   │   ├── model/
│       │   │   │   └── HistoryItem.java             # Calculation entry model
│       │   │   ├── data/
│       │   │   │   ├── HistoryRepository.java       # SharedPreferences history storage
│       │   │   │   └── PreferencesManager.java      # Theme, sound, and haptic preferences
│       │   │   ├── ui/
│       │   │   │   ├── HistoryAdapter.java          # RecyclerView adapter for history
│       │   │   │   ├── HistoryBottomSheetDialogFragment.java # History sheet
│       │   │   │   ├── ConverterBottomSheetDialogFragment.java # Unit converter modal
│       │   │   │   └── SettingsDialogFragment.java  # Theme and feedback settings
│       │   │   └── util/
│       │   │       └── NumberFormatter.java         # Grouping, scientific notation & typing
│       │   └── res/
│       │       ├── layout/activity_main.xml         # Modern portrait layout
│       │       ├── layout-land/activity_main.xml    # Dual-pane widescreen landscape layout
│       │       ├── layout/bottom_sheet_history.xml  # History modal layout
│       │       ├── layout/bottom_sheet_converter.xml# Unit converter modal layout
│       │       ├── layout/dialog_settings.xml       # Settings dialog layout
│       │       ├── layout/item_history.xml          # History row item
│       │       ├── drawable/                        # Vector icons, active states & ripples
│       │       ├── values/                          # Light theme tokens, strings, dimensions
│       │       └── values-night/                    # AMOLED Dark theme tokens
│       └── test/java/com/tamim/calculator/
│           └── CalculatorTest.java                  # Comprehensive unit tests
├── build.gradle.kts
├── settings.gradle.kts
└── gradlew
```

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).
