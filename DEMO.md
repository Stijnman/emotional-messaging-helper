# EMH Demo Flows

Record these flows on a real device with Ollama + llava running for high-quality demos.

## Flow 1: Basic Emotional Reply (No Vision)

1. Open WhatsApp, receive a message from a contact (e.g. "I'm feeling really down today after the meeting").
2. EMH floating bubble appears (or tap to open panel).
3. Adjust Figurative Level slider (try 4–7).
4. Select a tone preset (e.g. "Supportive & Warm").
5. Tap "Generate".
6. Review the rich, psychologically attuned reply.
7. Tap "Copy" or "Paste Reply" (auto-paste into WhatsApp input).
8. Send in WhatsApp.

**Expected:** Reply feels human, empathetic, uses relationship context if memory exists. Not generic corporate speak.

## Flow 2: With Vision Context (Screenshot)

1. In WhatsApp, take a screenshot of the chat (or use the "Add Vision" button in panel).
2. EMH captures via MediaProjection.
3. "Vision attached" card appears with thumbnail + "Clear" / "Recapture".
4. Generate reply (the prompt engine now has visual context).
5. Paste/Send.

**Expected:** Reply references visual elements intelligently (e.g. "I see the photo you sent of the kids...").

## Flow 3: Using Relationship Memory + History

1. Previously save a note for the contact via Settings or in-panel memory editor ("Prefers direct communication, hates small talk, recently lost their job").
2. Receive new message.
3. Generate – note how memory is injected into the prompt.
4. Save the generation to History.
5. Later, open History, tap a past entry → restores tone slider + text into live panel.
6. Edit slightly and re-generate or paste.

## Flow 4: Skill Invocation (Phase 2+)

(Once skills are implemented)

1. Message contains potential manipulation ("You always do this...").
2. Agent detects via DeceptionFlagSkill or ToneAnalyzerSkill.
3. Panel shows gentle flag + alternative strategy.
4. User can accept/override.

## Recording Tips

- Use Android Studio screen recorder or `adb shell screenrecord`.
- Show the full panel + WhatsApp side-by-side when possible.
- Capture haptics (sound + vibration) on generate/paste.
- Show Ollama logs in terminal for transparency ("local only").
- Keep videos short (15–45s) per flow.

Update this file with actual links/GIFs once recorded.
