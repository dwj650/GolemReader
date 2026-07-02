import React, { useState, useEffect, useRef } from "react";
import {
  Play, Pause, ChevronLeft, ChevronRight, ChevronDown, SkipBack, SkipForward,
  ChevronsLeft, ChevronsRight, Search, Timer, Image as ImageIcon,
  Library as LibraryIcon, Settings as SettingsIcon, Headphones, BookOpen,
  Loader2, X,
} from "lucide-react";

/* ============================================================
   Golem Reader — Visual Prototype v0.2.0
   Dummy data, no backend.

   Changelog v0.1.0 -> v0.2.0:
   - FIXED a real error against already-locked decisions D51/D52:
     Reading View had its own bottom-nav tab. It shouldn't — Now
     Playing is the hub of a three-surface topology (Now Playing /
     Reading / Image Viewer); Reading is reached by swipe-left or
     tapping the embedded text preview, never a nav destination.
   - Bottom nav is now Library / Now Playing / Settings, matching
     the operator's actual vision.
   - Now Playing rebuilt much closer to the operator's own mockup:
     large glowing cover, chapter nav row, embedded reading-preview
     strip, progress bar, sentence skip +-1/+-5, search/sleep/images
     pills.
   - Every control not actually built in the P1 walking skeleton is
     tagged "roadmap" and dimmed, not implied as live. The gesture
     nav itself (swipe-to-Reading) is the TARGET vision from D51/D52
     — the real current build (S9/D93) uses a simpler placeholder
     switch instead, noted where relevant.
   ============================================================ */

const C = {
  bgDeep: "#0D0F0C",
  surface: "#181C15",
  surface2: "#202519",
  line: "#2A3024",
  mist: "#D8DDCC",
  bark: "#7A826C",
  lime: "#A8CC3A",
  lamp: "#E8A040",
  lampSoft: "rgba(232,160,64,0.16)",
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

function RoadmapTag() {
  return (
    <span
      style={{
        fontSize: 8.5,
        letterSpacing: 0.4,
        textTransform: "uppercase",
        color: C.bark,
        border: `1px solid ${C.line}`,
        borderRadius: 99,
        padding: "1px 6px",
        marginLeft: 6,
      }}
    >
      Roadmap
    </span>
  );
}

function BufferingIndicator({ buffering }) {
  if (!buffering) return null;
  return (
    <div
      className="flex items-center gap-2 mx-6 mb-3"
      style={{
        background: C.surface2,
        border: `1px solid ${C.line}`,
        borderRadius: 10,
        padding: "8px 12px",
        color: C.bark,
        fontSize: 12.5,
      }}
    >
      <Loader2 size={14} style={{ animation: "spin 1.1s linear infinite" }} />
      Catching up…
    </div>
  );
}

function ChapterModal({ chapter, onSelect, onClose }) {
  return (
    <div
      onClick={onClose}
      style={{
        position: "absolute", inset: 0, background: "rgba(0,0,0,0.55)",
        display: "flex", alignItems: "center", justifyContent: "center",
        padding: 16, zIndex: 20,
      }}
    >
      <div
        onClick={(e) => e.stopPropagation()}
        style={{
          background: "#20251B", borderRadius: 18, border: `1px solid ${C.line}`,
          width: "100%", maxHeight: "70%", display: "flex", flexDirection: "column",
          overflow: "hidden",
        }}
      >
        <div className="flex items-center justify-between" style={{ padding: "14px 16px", borderBottom: `1px solid ${C.line}` }}>
          <span style={{ fontFamily: FONT_DISPLAY, fontWeight: 600, fontSize: 17, color: C.mist }}>
            Select chapter <RoadmapTag />
          </span>
          <button onClick={onClose} style={{ color: C.bark, background: "transparent" }}>
            <X size={18} />
          </button>
        </div>
        <div className="overflow-y-auto" style={{ padding: "4px 0" }}>
          {CHAPTERS.map((c, i) => (
            <button
              key={i}
              onClick={() => { onSelect(i); onClose(); }}
              className="w-full text-left"
              style={{ padding: "12px 16px", color: i === chapter ? C.lime : C.mist, fontSize: 15, background: "transparent" }}
            >
              Chapter {i + 1}: {c}
            </button>
          ))}
        </div>
      </div>
    </div>
  );
}

function NowPlayingScreen({
  playing, onTogglePlay, buffering, activeIndex, setActiveIndex,
  chapter, setChapter, onOpenReading,
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
    <div
      className="flex-1 overflow-y-auto"
      style={{
        background: `radial-gradient(120% 60% at 50% 0%, #1A1F16, ${C.bgDeep} 65%)`,
        paddingBottom: 12,
      }}
      onTouchStart={handleTouchStart}
      onTouchEnd={handleTouchEnd}
    >
      <div className="px-5 pt-5 pb-1" style={{ textAlign: "center" }}>
        <span style={{ color: C.bark, fontSize: 11, letterSpacing: 0.6, textTransform: "uppercase" }}>
          Now Playing
        </span>
      </div>

      <div className="flex flex-col items-center px-8 pt-3">
        <button
          style={{ position: "relative", background: "transparent" }}
          title="Tap for book images (F-074 · roadmap)"
        >
          <div
            style={{
              position: "absolute", inset: -20, borderRadius: 24,
              background: `radial-gradient(circle, ${C.lampSoft}, transparent 70%)`,
              filter: "blur(6px)", opacity: playing ? 1 : 0.3,
            }}
          />
          <div
            style={{
              width: 152, height: 152, borderRadius: 16,
              background: "linear-gradient(160deg, #2A3E2C, #10160E)",
              position: "relative",
            }}
          />
          <div style={{ color: C.bark, fontSize: 10, marginTop: 6 }}>
            Tap cover for images <RoadmapTag />
          </div>
        </button>
        <div style={{ fontFamily: FONT_DISPLAY, fontWeight: 700, fontSize: 19, marginTop: 14, color: C.mist, textAlign: "center" }}>
          The Binder's Ledger
        </div>
      </div>

      <BufferingIndicator buffering={buffering} />

      {/* Chapter nav — F-003, not built yet. Shown as roadmap. */}
      <div className="flex items-center justify-center gap-2 px-6 mt-3">
        <button onClick={() => setChapter((c) => Math.max(0, c - 1))} style={{ color: C.bark, background: "transparent" }}>
          <ChevronLeft size={20} />
        </button>
        <button
          onClick={() => setChapModal(true)}
          className="flex items-center gap-2"
          style={{ flex: 1, justifyContent: "center", background: C.surface, border: `1px solid ${C.line}`, borderRadius: 12, padding: "8px 10px", opacity: 0.85 }}
        >
          <span style={{ fontSize: 13, fontWeight: 500, color: C.mist, whiteSpace: "nowrap" }}>
            Ch. {chapter + 1} — {CHAPTERS[chapter]}
          </span>
          <ChevronDown size={14} style={{ color: C.bark }} />
          <RoadmapTag />
        </button>
        <button onClick={() => setChapter((c) => Math.min(CHAPTERS.length - 1, c + 1))} style={{ color: C.bark, background: "transparent" }}>
          <ChevronRight size={20} />
        </button>
      </div>

      {/* Embedded reading preview (F-073) + gesture entry to Reading View.
          The DESTINATION (Reading View) is real and built (F-014/F-016).
          This preview-strip content and the swipe gesture itself are the
          target vision (D51/D52); the current P1 build uses a simpler
          screen-switch button instead (D93). */}
      <div className="px-6 mt-4">
        <button
          onClick={onOpenReading}
          className="w-full text-left"
          style={{ background: C.surface, border: `1px solid ${C.line}`, borderRadius: 14, padding: "13px 15px" }}
        >
          <div style={{ fontFamily: FONT_READ, fontSize: 15, lineHeight: 1.5 }}>
            <span style={{ color: C.mist, background: C.lampSoft, borderRadius: 5, padding: "2px 4px" }}>
              {SENTENCES[activeIndex].display}
            </span>
            <span style={{ color: C.bark }}> {SENTENCES[activeIndex + 1]?.display || ""}</span>
          </div>
          <div style={{ color: C.bark, fontSize: 10, marginTop: 6, display: "flex", alignItems: "center", gap: 4 }}>
            <BookOpen size={11} /> Tap to open reader · swipe left to read
            <RoadmapTag />
          </div>
        </button>
      </div>

      {/* Progress + sentence skip — F-003, not built. Roadmap. */}
      <div className="px-6 mt-4" style={{ opacity: 0.6 }}>
        <div style={{ height: 4, background: C.surface2, borderRadius: 99 }}>
          <div style={{ height: 4, width: "42%", background: C.lime, borderRadius: 99, position: "relative" }}>
            <div style={{ position: "absolute", right: -5, top: -3, width: 10, height: 10, borderRadius: 99, background: C.mist }} />
          </div>
        </div>
        <div className="flex justify-between" style={{ color: C.bark, fontSize: 11, marginTop: 5 }}>
          <span>6:02</span><span>Skip by sentence <RoadmapTag /></span><span>-7:48</span>
        </div>
      </div>

      {/* Transport — play/pause is the only part actually built (F-002).
          Sentence skip +-1/+-5 is F-003, roadmap. */}
      <div className="flex items-center justify-center gap-5 mt-4">
        <div className="flex flex-col items-center" style={{ color: C.bark, opacity: 0.55 }}>
          <ChevronsLeft size={22} /><span style={{ fontSize: 9 }}>5</span>
        </div>
        <div className="flex flex-col items-center" style={{ color: C.bark, opacity: 0.55 }}>
          <SkipBack size={20} /><span style={{ fontSize: 9 }}>1</span>
        </div>
        <button
          onClick={onTogglePlay}
          style={{ width: 66, height: 66, borderRadius: 99, background: C.lime, color: C.bgDeep, display: "flex", alignItems: "center", justifyContent: "center" }}
          aria-label={playing ? "Pause" : "Play"}
        >
          {playing ? <Pause size={26} /> : <Play size={26} style={{ marginLeft: 3 }} />}
        </button>
        <div className="flex flex-col items-center" style={{ color: C.bark, opacity: 0.55 }}>
          <SkipForward size={20} /><span style={{ fontSize: 9 }}>1</span>
        </div>
        <div className="flex flex-col items-center" style={{ color: C.bark, opacity: 0.55 }}>
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
          <div
            key={p.label}
            className="flex items-center gap-1.5"
            style={{ background: C.surface, border: `1px solid ${C.line}`, borderRadius: 99, padding: "6px 11px", color: C.bark, fontSize: 11.5 }}
          >
            {p.icon}{p.label}
          </div>
        ))}
      </div>
      <div style={{ textAlign: "center", color: C.bark, fontSize: 10, marginTop: 10 }}>
        ← swipe for reading
      </div>

      {chapModal && (
        <ChapterModal chapter={chapter} onSelect={setChapter} onClose={() => setChapModal(false)} />
      )}
    </div>
  );
}

function ReadingScreen({ activeIndex, onClose }) {
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
    <div
      className="flex-1 overflow-y-auto"
      onTouchStart={handleTouchStart}
      onTouchEnd={handleTouchEnd}
    >
      <div className="flex items-center justify-between px-5 pt-5 pb-2">
        <button onClick={onClose} style={{ color: C.mist, background: "transparent" }}>
          <ChevronLeft size={24} />
        </button>
        <span style={{ color: C.bark, fontSize: 11, letterSpacing: 0.6, textTransform: "uppercase" }}>
          Reading
        </span>
        <div style={{ width: 24 }} />
      </div>
      <div className="px-5 pb-2">
        <div style={{ fontFamily: FONT_DISPLAY, fontWeight: 700, fontSize: 21, color: C.mist }}>
          The Binder's Ledger
        </div>
      </div>
      <div className="px-5" style={{ fontFamily: FONT_READ, fontSize: 19, lineHeight: 1.75, color: C.mist }}>
        {SENTENCES.map((s, i) => {
          const isActive = i === activeIndex;
          return (
            <span
              key={i}
              ref={isActive ? activeRef : undefined}
              style={{
                background: isActive ? C.lampSoft : "transparent",
                borderRadius: 6,
                padding: isActive ? "1px 3px" : 0,
                boxShadow: isActive ? `0 0 0 1px ${C.lamp}55` : "none",
                transition: "background 220ms ease, box-shadow 220ms ease",
              }}
            >
              {s.display}{" "}
            </span>
          );
        })}
      </div>
      <div className="px-5 mt-8" style={{ color: C.bark, fontSize: 10.5, lineHeight: 1.5 }}>
        Swipe right, or tap the back arrow, to return to Now Playing. This screen and
        the highlight are real and built (F-014/F-016) — the swipe gesture to reach it
        is the target vision (D51/D52); the current build uses a simpler switch (D93).
      </div>
    </div>
  );
}

function BottomNav({ tab, setTab }) {
  const tabs = [
    { id: "library", icon: <LibraryIcon size={20} />, label: "Library", live: false },
    { id: "nowplaying", icon: <Headphones size={20} />, label: "Now Playing", live: true },
    { id: "settings", icon: <SettingsIcon size={20} />, label: "Settings", live: false },
  ];
  return (
    <div
      style={{
        background: "rgba(13,15,12,0.94)", borderTop: `1px solid ${C.line}`,
        backdropFilter: "blur(12px)", display: "flex", padding: "10px 0 14px", flexShrink: 0,
      }}
    >
      {tabs.map((t) => {
        const isActive = tab === t.id;
        return (
          <button
            key={t.id}
            onClick={() => t.live && setTab(t.id)}
            className="flex-1 flex flex-col items-center gap-1"
            style={{ color: !t.live ? "#4A5040" : isActive ? C.lime : C.bark, cursor: t.live ? "pointer" : "default", background: "transparent" }}
          >
            {t.icon}
            <span style={{ fontSize: 10 }}>{t.label}</span>
            {!t.live && <span style={{ fontSize: 8, letterSpacing: 0.4, textTransform: "uppercase" }}>Roadmap</span>}
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
  const [buffering, setBuffering] = useState(false);
  const [activeIndex, setActiveIndex] = useState(3);
  const [chapter, setChapter] = useState(5);

  useEffect(() => {
    if (!playing || buffering) return;
    const id = setInterval(() => {
      setActiveIndex((i) => (i + 1) % (SENTENCES.length - 1));
    }, 2200);
    return () => clearInterval(id);
  }, [playing, buffering]);

  return (
    <div
      className="flex flex-col"
      style={{
        width: 390, height: 780, margin: "0 auto", background: C.bgDeep,
        fontFamily: FONT_BODY, borderRadius: 32, overflow: "hidden",
        border: `1px solid ${C.line}`, position: "relative",
      }}
    >
      <style>{`@keyframes spin { to { transform: rotate(360deg); } }`}</style>

      <div
        style={{
          background: C.surface2, color: C.bark, fontSize: 9.5, letterSpacing: 0.4,
          textTransform: "uppercase", textAlign: "center", padding: "5px 0", flexShrink: 0,
        }}
      >
        Prototype v0.2.0 · dummy data, no backend
      </div>

      {showReading ? (
        <ReadingScreen activeIndex={activeIndex} onClose={() => setShowReading(false)} />
      ) : (
        <NowPlayingScreen
          playing={playing}
          onTogglePlay={() => setPlaying((p) => !p)}
          buffering={buffering}
          activeIndex={activeIndex}
          setActiveIndex={setActiveIndex}
          chapter={chapter}
          setChapter={setChapter}
          onOpenReading={() => setShowReading(true)}
        />
      )}

      <BottomNav tab={tab} setTab={setTab} />
    </div>
  );
}
