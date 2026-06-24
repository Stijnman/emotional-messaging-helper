Fastlane / Google Play graphics assets (Phase 3 F-Droid + store prep)

Required (replace placeholders with real assets before any store submission):

- featureGraphic.png : 1024 x 500 px (PNG, no alpha). Promotional banner. Show EMH floating panel over a WhatsApp chat screenshot with "🧠 Agent Analysis" and "📸 Vision" visible. Clean, modern, emotional tones (blues/greens).

- icon.png : 512x512 px app icon (the launcher icon; usually synced from res/mipmap).

phoneScreenshots/ (at least 2-8, 1080x1920 or 1080x2400 PNGs, portrait):
- screenshot1.png : WhatsApp chat with incoming message, EMH floating bubble visible.
- screenshot2.png : EmotionalPanel open, tone slider + figurative control, "Generate" button.
- screenshot3.png : Panel after generation, showing suggested reply + emotional insight + 🧠 Agent Analysis card (tappable).
- screenshot4.png : Vision attached ("📸 Vision screenshot attached") + multi-frame note if possible, agent skills list.
- screenshot5.png : Settings screen with "Agent Skills" toggles visible (all 5 switches).
- screenshot6.png (optional): History screen or paste success toast + haptics.

Instructions:
1. Build + run on physical device for authentic screenshots.
2. Use Android Studio Layout Inspector or scrcpy + adb to capture clean screenshots (hide status bar if possible).
3. Optimize with pngcrush or similar, keep < 1-2MB each.
4. The generated mockups (emh-*.jpg) currently in phoneScreenshots/ and docs/screenshots/ are used for the README.md. Replace with real device captures before any store submission.

Current mockups included for documentation:
- emh-hero-banner.jpg (promotional)
- emh-panel-agent.jpg (main floating panel + agent)
- emh-settings-skills.jpg (toggles)
- emh-vision-panel.jpg (vision + reply)

For F-Droid: these are also useful for fdroid metadata screenshots (symlink or copy).
