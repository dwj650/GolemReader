import React, { useState, useEffect, useRef } from "react";
import {
  Play, Pause, ChevronLeft, ChevronRight, ChevronDown, SkipBack, SkipForward,
  ChevronsLeft, ChevronsRight, Search, Timer, Image as ImageIcon,
  Library as LibraryIcon, Settings as SettingsIcon, Headphones, BookOpen,
  Loader2, X, Sun, Moon, Monitor, Palette, Contrast, Type, Zap, Gauge,
  Pause as PauseIcon, Volume2, Mic2,
} from "lucide-react";

/* ============================================================
   Golem Reader — Visual Prototype v0.3.0 · FROZEN VISUAL CONTRACT (D103)
   Dummy data, no backend.

   STATUS: APPROVED by operator 2026-07-12 (D103). Supersedes
   v0.2.0 as the frozen visual contract; v0.2.0 is retained in this
   directory for rollback.

   Changelog v0.2.0 -> v0.3.0 (everything else is unchanged):
   - Settings tab is now LIVE (the S13 deliverable). Opens the new
     Settings screen: F-064 shell, "Appearance" section hosting the
     REAL theme picker (F-065, built in S12) as its first entry.
   - The theme picker WORKS in this prototype: System / Light / Dark
     re-paint the whole app using the exact palette values from the
     built GolemThemeTokens.kt — these are S12's real themes, not
     mockup colors. "System" follows this device's light/dark mode.
   - Dimmed "map preview" rows show where future settings will land
     per the Settings Map (S14-S16 accessibility, F-005/6/7 playback,
     voice manager). NOTE: in the shipping app these are HIDDEN
     entirely (D68 — absent, never a dead control). They are drawn
     dimmed here only so the map itself can be seen and approved.
   - Bottom nav: Library remains roadmap (F-019 unbuilt). Proposal
     A-i: the real app HIDES this tab until F-019 exists (D68);
     shown dimmed here for the same map-preview reason.
   - Now Playing / Reading screens: unchanged from v0.2.0 except
     they now re-theme. The preview-strip tap and swipe-left entry
     to Reading were ALREADY the v0.2.0 contract; S13 ships a
     simplified version of that strip (real sentence text, tap to
     open Reading) — full sync-preview richness remains F-073.
   ============================================================ */

const PALETTES = {
  /* Values copied verbatim from app/src/main/java/com/golemreader/theme/GolemThemeTokens.kt */
  dark: {
    name: "dark",
    bg: "#0D0F0C", surface: "#181C15", surfaceRaised: "#202519",
    line: "#2A3024", text: "#D8DDCC", text2: "#7A826C",
    accent: "#A8CC3A", onAccent: "#0D0F0C",
    lamp: "#E8A040", lampSoft: "rgba(232,160,64,0.16)",
    navBg: "rgba(13,15,12,0.94)", dead: "#4A5040",
  },
  light: {
    name: "light",
    bg: "#F8FAF0", surface: "#FFFFFF", surfaceRaised: "#EFF3E2",
    line: "#D2D9C2", text: "#1E261A", text2: "#5D6652",
    accent: "#4F6F12", onAccent: "#FFFFFF",
    lamp: "#B86716", lampSoft: "rgba(184,103,22,0.15)",
    navBg: "rgba(248,250,240,0.94)", dead: "#B9C1A9",
  },
};

const FONT_DISPLAY = "'Fraunces', Georgia, serif";
const FONT_BODY = "'Inter', system-ui, sans-serif";
const FONT_READ = "'Newsreader', Georgia, serif";

const CHAPTERS = [
  "A Quiet Arrival", "The Binder's Ledger", "Ink and Iron", "The Harbor Bell",
  "What the Candle Saw", "Declensions of Fire",
];

const SENTENCES = [
  { display: "The lamp had burned low, but Idris kept reading." },
  { display: "Outside, the harbor bell counted out the hour, twice." },
  { display: "\u201cYou were expected an hour ago,\u201d the old binder said, not looking up." },
  { display: "Idris set the satchel down slowly," },
  { display: "and only then did he notice the ink still drying on the ledger." },
  { display: "Neither of them spoke again until the candle guttered out." },
];

function RoadmapTag({ C }) {
  return (
    <span style={{
      fontSize: 8.5, letterSpacing: 0.4, textTransform: "uppercase",
      color: C.text2, border: `1px solid ${C.line}`, borderRadius: 99,
      padding: "1px 6px", marginLeft: 6,
    }}>
      Roadmap
    </span>
  );
}

function BufferingIndicator({ buffering, C }) {
  if (!buffering) return null;
  return (
    <div className="flex items-center gap-2 mx-6 mb-3" style={{
      background: C.surfaceRaised, border: `1px solid ${C.line}`,
      borderRadius: 10, padding: "8px 12px", color: C.text2, fontSize: 12.5,
    }}>
      <Loader2 size={14} style={{ animation: "spin 1.1s linear infinite" }} />
      Catching up…
    </div>
  );
}

function ChapterModal({ chapter, onSelect, onClose, C }) {
  return (
    <div onClick={onClose} style={{
      position: "absolute", inset: 0, background: "rgba(0,0,0,0.55)",
      display: "flex", alignItems: "center", justifyContent: "center",
      padding: 16, zIndex: 20,
    }}>
      <div onClick={(e) => e.stopPropagation()} style={{
        background: C.surfaceRaised, borderRadius: 18, border: `1px solid ${C.line}`,
        width: "100%", maxHeight: "70%", display: "flex", flexDirection: "column", overflow: "hidden",
      }}>
        <div className="flex items-center justify-between" style={{ padding: "14px 16px", borderBottom: `1px solid ${C.line}` }}>
          <span style={{ fontFamily: FONT_DISPLAY, fontWeight: 600, fontSize: 17, color: C.text }}>
            Select chapter <RoadmapTag C={C} />
          </span>
          <button onClick={onClose} style={{ color: C.text2, background: "transparent" }}><X size={18} /></button>
        </div>
        <div className="overflow-y-auto" style={{ padding: "4px 0" }}>
          {CHAPTERS.map((c, i) => (
            <button key={i} onClick={() => { onSelect(i); onClose(); }} className="w-full text-left"
              style={{ padding: "12px 16px", color: i === chapter ? C.accent : C.text, fontSize: 15, background: "transparent" }}>
              Chapter {i + 1}: {c}
            </button>
          ))}
        </div>
      </div>
    </div>
  );
}

function NowPlayingScreen({
  playing, onTogglePlay, buffering, activeIndex, chapter, setChapter, onOpenReading, C,
}) {
  const [chapModal, setChapModal] = useState(false);
  const touchStartX = useRef(null);

  const handleTouchStart = (e) => { touchStartX.current = e.touches[0].clientX; };
  const handleTouchEnd = (e) => {
    if (touchStartX.current == null) return;
    const dx = e.changedTouches[0].clientX - touchStartX.current;
    if (dx < -60) onOpenReading(); // swipe left -> Reading View, per D51/D52
    touchStartX.current = null;
  };

  return (
    <div className="flex-1 overflow-y-auto"
      style={{ background: `radial-gradient(120% 60% at 50% 0%, ${C.surface}, ${C.bg} 65%)`, paddingBottom: 12 }}
      onTouchStart={handleTouchStart} onTouchEnd={handleTouchEnd}>
      <div className="px-5 pt-5 pb-1" style={{ textAlign: "center" }}>
        <span style={{ color: C.text2, fontSize: 11, letterSpacing: 0.6, textTransform: "uppercase" }}>Now Playing</span>
      </div>

      <div className="flex flex-col items-center px-8 pt-3">
        <button style={{ position: "relative", background: "transparent" }} title="Tap for book images (F-074 · roadmap)">
          <div style={{
            position: "absolute", inset: -20, borderRadius: 24,
            background: `radial-gradient(circle, ${C.lampSoft}, transparent 70%)`,
            filter: "blur(6px)", opacity: playing ? 1 : 0.3,
          }} />
          <div style={{
            width: 152, height: 152, borderRadius: 16,
            background: C.name === "dark"
              ? "linear-gradient(160deg, #2A3E2C, #10160E)"
              : "linear-gradient(160deg, #C9D8B0, #EFF3E2)",
            position: "relative", border: `1px solid ${C.line}`,
          }} />
          <div style={{ color: C.text2, fontSize: 10, marginTop: 6 }}>
            Tap cover for images <RoadmapTag C={C} />
          </div>
        </button>
        <div style={{ fontFamily: FONT_DISPLAY, fontWeight: 700, fontSize: 19, marginTop: 14, color: C.text, textAlign: "center" }}>
          The Binder's Ledger
        </div>
      </div>

      <BufferingIndicator buffering={buffering} C={C} />

      {/* Chapter nav — F-003, not built yet. Roadmap. */}
      <div className="flex items-center justify-center gap-2 px-6 mt-3">
        <button onClick={() => setChapter((c) => Math.max(0, c - 1))} style={{ color: C.text2, background: "transparent" }}>
          <ChevronLeft size={20} />
        </button>
        <button onClick={() => setChapModal(true)} className="flex items-center gap-2"
          style={{ flex: 1, justifyContent: "center", background: C.surface, border: `1px solid ${C.line}`, borderRadius: 12, padding: "8px 10px", opacity: 0.85 }}>
          <span style={{ fontSize: 13, fontWeight: 500, color: C.text, whiteSpace: "nowrap" }}>
            Ch. {chapter + 1} — {CHAPTERS[chapter]}
          </span>
          <ChevronDown size={14} style={{ color: C.text2 }} />
          <RoadmapTag C={C} />
        </button>
        <button onClick={() => setChapter((c) => Math.min(CHAPTERS.length - 1, c + 1))} style={{ color: C.text2, background: "transparent" }}>
          <ChevronRight size={20} />
        </button>
      </div>

      {/* Embedded reading preview + entry to Reading View.
          S13 CHANGE (proposal): this strip becomes REAL in simplified
          form — live sentence text, tap opens Reading. It replaces the
          old top screen-switch (S9/D93 placeholder), which is REMOVED.
          Full sync-preview richness stays F-073; swipe gesture stays
          D51/D52 target vision. */}
      <div className="px-6 mt-4">
        <button onClick={onOpenReading} className="w-full text-left"
          style={{ background: C.surface, border: `1px solid ${C.line}`, borderRadius: 14, padding: "13px 15px" }}>
          <div style={{ fontFamily: FONT_READ, fontSize: 15, lineHeight: 1.5 }}>
            <span style={{ color: C.text, background: C.lampSoft, borderRadius: 5, padding: "2px 4px" }}>
              {SENTENCES[activeIndex].display}
            </span>
            <span style={{ color: C.text2 }}> {SENTENCES[activeIndex + 1]?.display || ""}</span>
          </div>
          <div style={{ color: C.text2, fontSize: 10, marginTop: 6, display: "flex", alignItems: "center", gap: 4 }}>
            <BookOpen size={11} /> Tap to open reader — real in S13 (simplified) · full preview is F-073
          </div>
        </button>
      </div>

      {/* Progress + sentence skip — F-003, not built. Roadmap. */}
      <div className="px-6 mt-4" style={{ opacity: 0.6 }}>
        <div style={{ height: 4, background: C.surfaceRaised, borderRadius: 99 }}>
          <div style={{ height: 4, width: "42%", background: C.accent, borderRadius: 99, position: "relative" }}>
            <div style={{ position: "absolute", right: -5, top: -3, width: 10, height: 10, borderRadius: 99, background: C.text }} />
          </div>
        </div>
        <div className="flex justify-between" style={{ color: C.text2, fontSize: 11, marginTop: 5 }}>
          <span>6:02</span><span>Skip by sentence <RoadmapTag C={C} /></span><span>-7:48</span>
        </div>
      </div>

      {/* Transport — play/pause built (F-002); sentence skip is F-003 roadmap. */}
      <div className="flex items-center justify-center gap-5 mt-4">
        <div className="flex flex-col items-center" style={{ color: C.text2, opacity: 0.55 }}>
          <ChevronsLeft size={22} /><span style={{ fontSize: 9 }}>5</span>
        </div>
        <div className="flex flex-col items-center" style={{ color: C.text2, opacity: 0.55 }}>
          <SkipBack size={20} /><span style={{ fontSize: 9 }}>1</span>
        </div>
        <button onClick={onTogglePlay}
          style={{ width: 66, height: 66, borderRadius: 99, background: C.accent, color: C.onAccent, display: "flex", alignItems: "center", justifyContent: "center" }}
          aria-label={playing ? "Pause" : "Play"}>
          {playing ? <Pause size={26} /> : <Play size={26} style={{ marginLeft: 3 }} />}
        </button>
        <div className="flex flex-col items-center" style={{ color: C.text2, opacity: 0.55 }}>
          <SkipForward size={20} /><span style={{ fontSize: 9 }}>1</span>
        </div>
        <div className="flex flex-col items-center" style={{ color: C.text2, opacity: 0.55 }}>
          <ChevronsRight size={22} /><span style={{ fontSize: 9 }}>5</span>
        </div>
      </div>

      {/* Search / Sleep / Images pills — F-076 / sleep timer / F-074. Roadmap. */}
      <div className="flex items-center justify-center gap-2 mt-4 px-6" style={{ opacity: 0.6 }}>
        {[
          { icon: <Search size={13} />, label: "Search" },
          { icon: <Timer size={13} />, label: "Sleep" },
          { icon: <ImageIcon size={13} />, label: "Images" },
        ].map((p) => (
          <div key={p.label} className="flex items-center gap-1.5"
            style={{ background: C.surface, border: `1px solid ${C.line}`, borderRadius: 99, padding: "6px 11px", color: C.text2, fontSize: 11.5 }}>
            {p.icon}{p.label}
          </div>
        ))}
      </div>
      <div style={{ textAlign: "center", color: C.text2, fontSize: 10, marginTop: 10 }}>
        ← swipe for reading
      </div>

      {chapModal && (
        <ChapterModal chapter={chapter} onSelect={setChapter} onClose={() => setChapModal(false)} C={C} />
      )}
    </div>
  );
}

function ReadingScreen({ activeIndex, onClose, C }) {
  const activeRef = useRef(null);
  const touchStartX = useRef(null);

  useEffect(() => {
    if (activeRef.current) {
      activeRef.current.scrollIntoView({ behavior: "smooth", block: "center" });
    }
  }, [activeIndex]);

  const handleTouchStart = (e) => { touchStartX.current = e.touches[0].clientX; };
  const handleTouchEnd = (e) => {
    if (touchStartX.current == null) return;
    const dx = e.changedTouches[0].clientX - touchStartX.current;
    if (dx > 60) onClose(); // swipe right -> back to Now Playing
    touchStartX.current = null;
  };

  return (
    <div className="flex-1 overflow-y-auto" onTouchStart={handleTouchStart} onTouchEnd={handleTouchEnd}>
      <div className="flex items-center justify-between px-5 pt-5 pb-2">
        <button onClick={onClose} style={{ color: C.text, background: "transparent" }}>
          <ChevronLeft size={24} />
        </button>
        <span style={{ color: C.text2, fontSize: 11, letterSpacing: 0.6, textTransform: "uppercase" }}>Reading</span>
        <div style={{ width: 24 }} />
      </div>
      <div className="px-5 pb-2">
        <div style={{ fontFamily: FONT_DISPLAY, fontWeight: 700, fontSize: 21, color: C.text }}>
          The Binder's Ledger
        </div>
      </div>
      <div className="px-5" style={{ fontFamily: FONT_READ, fontSize: 19, lineHeight: 1.75, color: C.text }}>
        {SENTENCES.map((s, i) => {
          const isActive = i === activeIndex;
          return (
            <span key={i} ref={isActive ? activeRef : undefined} style={{
              background: isActive ? C.lampSoft : "transparent",
              borderRadius: 6, padding: isActive ? "1px 3px" : 0,
              boxShadow: isActive ? `0 0 0 1px ${C.lamp}55` : "none",
              transition: "background 220ms ease, box-shadow 220ms ease",
            }}>
              {s.display}{" "}
            </span>
          );
        })}
      </div>
      <div className="px-5 mt-8" style={{ color: C.text2, fontSize: 10.5, lineHeight: 1.5 }}>
        Swipe right, or tap the back arrow, to return to Now Playing. This screen and
        the highlight are real and built (F-014/F-016). In S13, Reading is reached by
        tapping the preview strip on Now Playing — the swipe gesture remains the
        target vision (D51/D52).
      </div>
    </div>
  );
}

/* ============ NEW IN v0.3.0: the S13 Settings screen ============ */

function SectionLabel({ children, C }) {
  return (
    <div style={{
      color: C.text2, fontSize: 11, letterSpacing: 0.6, textTransform: "uppercase",
      marginTop: 22, marginBottom: 2,
    }}>
      {children}
    </div>
  );
}

function MapPreviewRow({ icon, label, note, C }) {
  /* D68: in the SHIPPING app these rows are HIDDEN (absent, never a
     dead control). Drawn dimmed here ONLY to visualize the Settings
     Map for approval. */
  return (
    <div className="flex items-center gap-3" style={{ padding: "13px 0", borderBottom: `1px solid ${C.line}`, opacity: 0.45 }}>
      <div style={{ color: C.text2 }}>{icon}</div>
      <div className="flex-1">
        <div style={{ fontSize: 14.5, color: C.text }}>{label}</div>
        {note && <div style={{ color: C.text2, fontSize: 12 }}>{note}</div>}
      </div>
      <RoadmapTag C={C} />
    </div>
  );
}

function SettingsScreen({ themeChoice, setThemeChoice, C }) {
  const modes = [
    ["system", "System", <Monitor size={15} key="m" />],
    ["light", "Light", <Sun size={15} key="s" />],
    ["dark", "Dark", <Moon size={15} key="d" />],
  ];
  return (
    <div className="flex-1 overflow-y-auto" style={{ paddingBottom: 16 }}>
      <div className="px-5 pt-5 pb-1">
        <div style={{ fontFamily: FONT_DISPLAY, fontWeight: 700, fontSize: 22, color: C.text }}>Settings</div>
        <div style={{ color: C.text2, fontSize: 11, marginTop: 2 }}>
          F-064 shell · S13 — hosts controls owned by their features
        </div>
      </div>

      <div className="px-5">
        {/* ---- Appearance: the ONE real section S13 ships ---- */}
        <SectionLabel C={C}>Appearance</SectionLabel>
        <div style={{ padding: "13px 0", borderBottom: `1px solid ${C.line}` }}>
          <div className="flex items-center gap-3" style={{ marginBottom: 10 }}>
            <Palette size={17} style={{ color: C.lamp }} />
            <div className="flex-1">
              <div style={{ fontSize: 14.5, color: C.text }}>Theme</div>
              <div style={{ color: C.text2, fontSize: 12 }}>F-065 · built in S12 · lands here in S13</div>
            </div>
          </div>
          {/* The real picker: System · Light · Dark (built order).
              LIVE in this prototype — switches the real S12 palettes. */}
          <div className="flex" style={{ background: C.surfaceRaised, borderRadius: 12, padding: 3 }}>
            {modes.map(([id, label, icon]) => {
              const active = themeChoice === id;
              return (
                <button key={id} onClick={() => setThemeChoice(id)}
                  className="flex items-center justify-center gap-2"
                  style={{
                    flex: 1, padding: "10px 0", borderRadius: 9, fontSize: 13, fontWeight: 500,
                    background: active ? C.surface : "transparent",
                    color: active ? C.text : C.text2,
                    boxShadow: active ? `0 0 0 1px ${C.accent}` : "none",
                  }}>
                  {icon}{label}
                </button>
              );
            })}
          </div>
        </div>

        {/* ---- Map preview: everything below is HIDDEN in the real
                app until its parent feature exists (D68). Shown dimmed
                here only to make the Settings Map visible. ---- */}
        <SectionLabel C={C}>Accessibility — arrives during P2</SectionLabel>
        <MapPreviewRow icon={<Contrast size={17} />} label="High contrast" note="F-066 · S14" C={C} />
        <MapPreviewRow icon={<Type size={17} />} label="Text size" note="F-068 · S15" C={C} />
        <MapPreviewRow icon={<Zap size={17} />} label="Reduce motion" note="F-067 · S16" C={C} />

        <SectionLabel C={C}>Playback — later phases</SectionLabel>
        <MapPreviewRow icon={<Gauge size={17} />} label="Speed" note="F-005" C={C} />
        <MapPreviewRow icon={<PauseIcon size={17} />} label="Pauses between sentences" note="F-006" C={C} />
        <MapPreviewRow icon={<Volume2 size={17} />} label="Volume" note="F-007" C={C} />

        <SectionLabel C={C}>Voices — later phases</SectionLabel>
        <MapPreviewRow icon={<Mic2 size={17} />} label="Voice manager" note="F-048 family" C={C} />

        <div style={{ color: C.text2, fontSize: 10.5, lineHeight: 1.5, marginTop: 18 }}>
          Dimmed rows are a map preview only. In the shipping app, a setting whose
          feature isn't built yet is absent entirely — never a dead control (D68).
          On day one, Settings shows exactly one section: Appearance.
        </div>
      </div>
    </div>
  );
}

function BottomNav({ tab, setTab, C }) {
  const tabs = [
    { id: "library", icon: <LibraryIcon size={20} />, label: "Library", live: false, note: "hidden until F-019" },
    { id: "nowplaying", icon: <Headphones size={20} />, label: "Now Playing", live: true },
    { id: "settings", icon: <SettingsIcon size={20} />, label: "Settings", live: true, isNew: true },
  ];
  return (
    <div style={{
      background: C.navBg, borderTop: `1px solid ${C.line}`,
      backdropFilter: "blur(12px)", display: "flex", padding: "10px 0 14px", flexShrink: 0,
    }}>
      {tabs.map((t) => {
        const isActive = tab === t.id;
        return (
          <button key={t.id} onClick={() => t.live && setTab(t.id)}
            className="flex-1 flex flex-col items-center gap-1"
            style={{
              color: !t.live ? C.dead : isActive ? C.accent : C.text2,
              cursor: t.live ? "pointer" : "default", background: "transparent",
            }}>
            {t.icon}
            <span style={{ fontSize: 10 }}>{t.label}</span>
            {!t.live && (
              <span style={{ fontSize: 7.5, letterSpacing: 0.3, textTransform: "uppercase" }}>{t.note}</span>
            )}
            {t.isNew && (
              <span style={{
                fontSize: 7.5, letterSpacing: 0.4, textTransform: "uppercase",
                color: C.onAccent, background: C.accent, borderRadius: 99, padding: "1px 5px",
              }}>
                live in S13
              </span>
            )}
          </button>
        );
      })}
    </div>
  );
}

export default function GolemReaderPrototype() {
  const [tab, setTab] = useState("nowplaying");
  const [showReading, setShowReading] = useState(false);
  const [playing, setPlaying] = useState(true);
  const [buffering] = useState(false);
  const [activeIndex, setActiveIndex] = useState(3);
  const [chapter, setChapter] = useState(5);

  /* Theme choice — mirrors the built ThemeChoice enum:
     FollowSystem / Light / Dark, default FollowSystem. */
  const [themeChoice, setThemeChoice] = useState("system");
  const [systemDark, setSystemDark] = useState(true);

  useEffect(() => {
    const mq = window.matchMedia?.("(prefers-color-scheme: dark)");
    if (!mq) return;
    setSystemDark(mq.matches);
    const onChange = (e) => setSystemDark(e.matches);
    mq.addEventListener?.("change", onChange);
    return () => mq.removeEventListener?.("change", onChange);
  }, []);

  /* Mirrors resolveThemeValueSet() in GolemThemeTokens.kt */
  const C = themeChoice === "system"
    ? (systemDark ? PALETTES.dark : PALETTES.light)
    : PALETTES[themeChoice];

  useEffect(() => {
    if (!playing || buffering) return;
    const id = setInterval(() => {
      setActiveIndex((i) => (i + 1) % (SENTENCES.length - 1));
    }, 2200);
    return () => clearInterval(id);
  }, [playing, buffering]);

  return (
    <div className="flex flex-col" style={{
      width: 390, height: 780, margin: "0 auto", background: C.bg,
      fontFamily: FONT_BODY, borderRadius: 32, overflow: "hidden",
      border: `1px solid ${C.line}`, position: "relative",
      transition: "background 200ms ease",
    }}>
      <style>{`@keyframes spin { to { transform: rotate(360deg); } }`}</style>

      <div style={{
        background: C.surfaceRaised, color: C.text2, fontSize: 9.5, letterSpacing: 0.4,
        textTransform: "uppercase", textAlign: "center", padding: "5px 0", flexShrink: 0,
      }}>
        v0.3.0 · frozen visual contract (D103) · dummy data, no backend
      </div>

      {showReading ? (
        <ReadingScreen activeIndex={activeIndex} onClose={() => setShowReading(false)} C={C} />
      ) : tab === "settings" ? (
        <SettingsScreen themeChoice={themeChoice} setThemeChoice={setThemeChoice} C={C} />
      ) : (
        <NowPlayingScreen
          playing={playing}
          onTogglePlay={() => setPlaying((p) => !p)}
          buffering={buffering}
          activeIndex={activeIndex}
          chapter={chapter}
          setChapter={setChapter}
          onOpenReading={() => setShowReading(true)}
          C={C}
        />
      )}

      <BottomNav tab={tab} setTab={setTab} C={C} />
    </div>
  );
}
