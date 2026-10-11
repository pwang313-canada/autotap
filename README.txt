AutoTap — auto-tap keywords inside YouTube (personal use, not for Play Store)
// MARKER: autoTapV1

BUILD
  Open this folder in Android Studio (Koala or newer) and press Run.
  Package: org.cakk.autotap, minSdk 26.

SETUP (on the phone)
  1. Install the APK.
  2. Open AutoTap -> "Open accessibility settings" -> enable AutoTap.
  3. Back in the app, add a rule (e.g. keyword "Skip Ad") and turn Monitoring ON.

HOW IT WORKS
  TapService (AccessibilityService) listens to YouTube window changes,
  walks the screen's node tree, and taps the first node whose text or
  content-description matches an enabled rule keyword. Per-rule cooldown
  prevents rapid re-taps.

DEBUG MODE
  Turn on "Debug mode" on the home screen, open YouTube, then run:
    adb logcat -s AutoTap:D
  Every visible text on screen is logged as [text] {content-desc} —
  copy the exact wording into your rule keyword.

NOTES
  - The service only receives events from packages listed in
    res/xml/accessibility_service_config.xml (currently YouTube only).
    If you change a rule's target package, add it there too.
  - If a tap never fires, the text is probably not exposed in YouTube's
    accessibility tree — use debug mode to check what IS visible.
