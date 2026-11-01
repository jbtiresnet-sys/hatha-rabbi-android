# هذا ربي — Android App V2

Status: planning specification; **not a finished APK or implemented features**.

## Goal
Native Android experience for https://www.itsmygod.org/ rather than a WebView of the website.

## Agreed experience
- Light cream/white, soft blue and gold visual style, new logo and splash screen; automatic transition to home.
- Home: category grid followed by latest content across all WordPress categories.
- Native category, subcategory, article, news, audio and video screens.
- Arabic (RTL) and English (LTR) interface language switch; content retains original language unless translations exist.
- Arabic voice recognition **and command execution**, e.g. open category or play latest lecture; explicit command routing and permission/error handling.
- Audio playback in background, favorites, local download management and offline playback for permitted files.
- FCM notifications: automatic on publish, manual campaigns, urgent channel with distinct sound (subject to Android notification permission, user channel preferences and device settings); deep links to native content.
- WordPress companion plugin for category visibility/order/images, home settings, notifications, branding and supported remote configuration.

## Integration plan
1. Audit current Android code and WordPress REST API endpoints, taxonomy and media fields.
2. Build native screens and paginated API data layer with caching and fallback images.
3. Add media service, offline downloads and speech-to-command dispatch.
4. Add WordPress plugin with authenticated admin settings, protected notification endpoints, FCM integration and duplicate-send protection.
5. Add Firebase configuration through secure local/CI secrets, not committed service credentials.
6. Test on Android devices and build signed release APK via CI.

## Security and delivery
- Do not commit Firebase server credentials, signing keys or access tokens.
- Require authenticated WordPress capabilities for notification sending.
- Changes to content/config can sync without a new APK; native feature changes require a new build.

This document records agreed requirements only. Implementation and validation remain outstanding.
