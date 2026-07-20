import React, { useState, useEffect } from "react";
import {
  Mic2, Play, Pause, Check, X, Plus, ChevronRight, ChevronLeft, Volume2,
  Gauge, Pause as PauseIcon, Sun, Moon, Sparkles, ListChecks, SlidersHorizontal,
  BookOpen, Ear, CircleAlert, Loader2, Download, HardDrive, Wand2, Ban,
  ToggleLeft, Rocket, ShieldCheck, Info, CheckCircle2, Pencil, Trash2, Package,
  Image as ImageGlyph, Lock, Unlock, Type, AlignJustify, Bold as BoldIcon,
} from "lucide-react";

/* ============================================================
   Golem Reader — V1 HORIZON · CONCEPT SKETCHES v2 — NOT A CONTRACT
   Dummy data, no backend.

   Changelog v1 -> v2 (operator direction, same G1 session):
   - IMAGES sketch added (F-074 V1 minimal: cover + current
     chapter's images in a simple list, reached by tapping the
     cover per the frozen D52 gesture grammar; pan/zoom gallery
     is the V2 body, seam only). Includes the HONEST F-075 action
     row: its one real V1 control is a ROTATION LOCK; the search
     and sleep slots are reserved but NOT drawn until F-076/F-077
     exist — no dead buttons. (The dimmed pills in the v0.x
     contract prototypes are a map-preview convention; the
     shipping rule is absence.)
   - READING DISPLAY sketch added (F-078 V1: dyslexia-friendly
     font, line spacing, bold — with the locked rule drawn live:
     accessibility WINS over the theme on any shared property.
     Dyslexia font rendered with a stand-in face in this sketch.)
   ============================================================ */

/* ============================================================
   Golem Reader — V1 HORIZON · CONCEPT SKETCHES — NOT A CONTRACT
   Dummy data, no backend.

   STATUS: Design-direction artifact drawn at the P3 G1 session
   (2026-07-19) for the remaining V1 tiers (P4+): the voice
   manager, the pronunciation & normalization suite, playback
   settings, and first-run. NOTHING HERE IS FROZEN. Each future
   phase mints its own v0.x contract at its own G1, grounded in
   that phase's scoping — these sketches are INPUT those sessions
   are free to overrule, never precedent. The frozen contract
   line remains: v0.4.0 (D123).

   Grounding (spec summaries read 2026-07-19):
   - F-048/F-049: composite voice identity; ONE active voice;
     preview is a transient overlay, ducks-or-pauses the book,
     NEVER changes the active voice.
   - F-036/F-037: respelling authoring (replace/suppress) with a
     "hear it now" loop on the active voice; respellings only,
     never raw phonemes (IPA is capability-gated, not V1).
   - F-030/F-031: class-toggle switchboard (global vs per-book;
     user choice always wins) + batched review list with
     why-flagged labels and accept/edit/reject.
   - F-005/F-006/F-007: speed 0.5–3.0x in 0.1 steps with presets,
     >2.5x allowed-not-promised; pause by FEEL (Tight/Natural/
     Relaxed), not milliseconds; NO in-app volume knob in V1 —
     the absence is deliberate (system volume only), drawn here
     as a note, not a control.
   - F-070: permissions → first VOICE (hard gate: "add a voice
     to begin") → first book; every step degrades gracefully;
     accessible from the start (D74).
   - F-077 sleep timer: V2 stub — its Now Playing pill stays a
     reserved slot; NO screen sketched.
   ============================================================ */

const PALETTES = {
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

function Tag({ C, children, tone = "muted" }) {
  const color = tone === "accent" ? C.accent : tone === "lamp" ? C.lamp : C.text2;
  const border = tone === "accent" ? `${C.accent}55` : tone === "lamp" ? `${C.lamp}66` : C.line;
  return (
    <span style={{
      fontSize: 8.5, letterSpacing: 0.4, textTransform: "uppercase",
      color, border: `1px solid ${border}`, borderRadius: 99, padding: "1px 6px",
    }}>{children}</span>
  );
}

function SectionLabel({ children, C }) {
  return (
    <div style={{
      color: C.text2, fontSize: 11, letterSpacing: 0.6, textTransform: "uppercase",
      marginTop: 20, marginBottom: 4,
    }}>{children}</div>
  );
}

function Note({ C, children }) {
  return (
    <div style={{ color: C.text2, fontSize: 10.5, lineHeight: 1.55, marginTop: 8 }}>
      {children}
    </div>
  );
}

/* ==================== 1 · VOICE MANAGER (F-048 / F-049 / F-057 / F-045) ==================== */

const VOICES = [
  { id: "v1", engine: "Kokoro", name: "Nora", detail: "en · warm alto", active: true, packs: 2, sizeMB: null },
  { id: "v2", engine: "Kokoro", name: "Adam", detail: "en · low, even", active: false, packs: 0, sizeMB: null },
  { id: "v3", engine: "Piper", name: "Lessac (medium)", detail: "en-US · clear, brisk", active: false, packs: 1, sizeMB: 63 },
];

function VoiceManagerSketch({ C }) {
  const [active, setActive] = useState("v1");
  const [preview, setPreview] = useState(null); /* voice id being previewed */

  useEffect(() => {
    if (!preview) return;
    const t = setTimeout(() => setPreview(null), 2600);
    return () => clearTimeout(t);
  }, [preview]);

  const grouped = ["Kokoro", "Piper"].map((eng) => ({
    eng, voices: VOICES.filter((v) => v.engine === eng),
  }));

  return (
    <div style={{ position: "relative" }}>
      <div className="flex items-center justify-between">
        <div>
          <div style={{ fontFamily: FONT_DISPLAY, fontWeight: 700, fontSize: 20, color: C.text }}>Voices</div>
          <div style={{ color: C.text2, fontSize: 11, marginTop: 2 }}>F-048 family · one active voice, always</div>
        </div>
        <button className="flex items-center gap-1.5" style={{
          background: C.accent, color: C.onAccent, borderRadius: 99, padding: "8px 14px",
          fontSize: 12.5, fontWeight: 600,
        }}>
          <Plus size={14} /> Import voice
        </button>
      </div>

      {grouped.map(({ eng, voices }) => (
        <div key={eng}>
          <SectionLabel C={C}>{eng}</SectionLabel>
          {voices.map((v) => {
            const isActive = active === v.id;
            return (
              <div key={v.id} className="flex items-center gap-3"
                style={{ padding: "12px 0", borderBottom: `1px solid ${C.line}` }}>
                <button onClick={() => setActive(v.id)} aria-label={`Make ${v.name} the active voice`}
                  style={{
                    width: 17, height: 17, borderRadius: 99, flexShrink: 0,
                    border: `2px solid ${isActive ? C.accent : C.line}`,
                    background: isActive ? C.accent : "transparent",
                    display: "flex", alignItems: "center", justifyContent: "center",
                  }}>
                  {isActive && <Check size={11} style={{ color: C.onAccent }} />}
                </button>
                <div className="flex-1" style={{ minWidth: 0 }}>
                  <div className="flex items-center gap-2">
                    <span style={{ fontSize: 14.5, color: C.text, fontWeight: isActive ? 600 : 400 }}>{v.name}</span>
                    {isActive && <Tag C={C} tone="accent">Active</Tag>}
                  </div>
                  <div style={{ color: C.text2, fontSize: 11.5 }}>{v.detail}</div>
                  <div className="flex items-center gap-2" style={{ marginTop: 3 }}>
                    {v.packs > 0 && (
                      <span className="flex items-center gap-1" style={{ color: C.text2, fontSize: 10 }}>
                        <Package size={10} /> {v.packs} rule pack{v.packs > 1 ? "s" : ""} bound (F-045)
                      </span>
                    )}
                    {v.sizeMB && (
                      <span className="flex items-center gap-1" style={{ color: C.text2, fontSize: 10 }}>
                        <HardDrive size={10} /> {v.sizeMB} MB on device (F-057)
                      </span>
                    )}
                  </div>
                </div>
                <button onClick={() => setPreview(v.id)} aria-label={`Preview ${v.name}`}
                  style={{
                    width: 36, height: 36, borderRadius: 99, background: C.surfaceRaised,
                    border: `1px solid ${C.line}`, color: C.text, display: "flex",
                    alignItems: "center", justifyContent: "center", flexShrink: 0,
                  }}>
                  <Play size={15} style={{ marginLeft: 1 }} />
                </button>
              </div>
            );
          })}
        </div>
      ))}

      <Note C={C}>
        Switching the active voice mid-book keeps your place (F-048 hot-swap). A voice's
        identity survives renaming or re-importing its model file — it's keyed on the
        file's <i>content</i>, the same trick books use (D38/D3). Voice-bound rule packs
        (F-045) follow the voice automatically.
      </Note>

      {/* F-049: the transient preview overlay — never a screen, never changes active */}
      {preview && (
        <div style={{
          position: "absolute", left: 0, right: 0, bottom: 8, zIndex: 10,
          background: C.surfaceRaised, border: `1px solid ${C.line}`, borderRadius: 14,
          padding: "11px 14px", boxShadow: "0 8px 24px rgba(0,0,0,0.35)",
        }}>
          <div className="flex items-center gap-2">
            <Loader2 size={14} style={{ color: C.lamp, animation: "spin 1.1s linear infinite" }} />
            <span style={{ color: C.text, fontSize: 13 }}>
              Previewing <b>{VOICES.find((v) => v.id === preview)?.name}</b>…
            </span>
            <button onClick={() => setPreview(null)} style={{ marginLeft: "auto", color: C.text2, background: "transparent" }}>
              <X size={15} />
            </button>
          </div>
          <div style={{ color: C.text2, fontSize: 10.5, marginTop: 4, lineHeight: 1.5 }}>
            Fixed sample, book ducked — playback restores exactly as it was. Previewing
            never changes your active voice (F-049/D39).
          </div>
        </div>
      )}
    </div>
  );
}

/* ============ 2 · SPEECH: PRONUNCIATION & NORMALIZATION (F-030/031/036/037/042) ============ */

const REVIEW_ITEMS = [
  { id: "r1", token: "5/8", guess: "five eighths", why: "bare fraction — could be a date", ch: "Ch. 4" },
  { id: "r2", token: "Dr.", guess: "Doctor", why: "abbreviation — Doctor or Drive", ch: "Ch. 4" },
  { id: "r3", token: "NOAA", guess: "N-O-A-A", why: "unknown all-caps word", ch: "Ch. 7" },
];

function SpeechSketch({ C }) {
  const [view, setView] = useState("review"); /* review | rule | toggles */
  const [items, setItems] = useState(REVIEW_ITEMS);
  const [decided, setDecided] = useState(0);
  const [respell, setRespell] = useState("NOH-ah");
  const [op, setOp] = useState("replace");
  const [hearing, setHearing] = useState(false);
  const [scope, setScope] = useState("global");
  const [toggles, setToggles] = useState({ numbers: true, dates: true, abbrev: true, acronyms: true, symbols: false });

  useEffect(() => {
    if (!hearing) return;
    const t = setTimeout(() => setHearing(false), 1500);
    return () => clearTimeout(t);
  }, [hearing]);

  const decide = (id) => { setItems((xs) => xs.filter((x) => x.id !== id)); setDecided((d) => d + 1); };

  const tabs = [
    ["review", "Review", <ListChecks size={13} key="a" />],
    ["rule", "Say it right", <Wand2 size={13} key="b" />],
    ["toggles", "Classes", <SlidersHorizontal size={13} key="c" />],
  ];

  return (
    <div>
      <div style={{ fontFamily: FONT_DISPLAY, fontWeight: 700, fontSize: 20, color: C.text }}>Speech</div>
      <div style={{ color: C.text2, fontSize: 11, marginTop: 2 }}>normalization & pronunciation suite (T2/T7)</div>

      <div className="flex gap-1.5" style={{ marginTop: 12 }}>
        {tabs.map(([id, label, icon]) => {
          const activeT = view === id;
          return (
            <button key={id} onClick={() => setView(id)} className="flex items-center gap-1.5"
              style={{
                fontSize: 11.5, borderRadius: 99, padding: "6px 12px",
                background: activeT ? C.surfaceRaised : "transparent",
                color: activeT ? C.text : C.text2,
                border: `1px solid ${activeT ? C.accent + "66" : C.line}`,
              }}>
              {icon}{label}
            </button>
          );
        })}
      </div>

      {view === "review" && (
        <div>
          <SectionLabel C={C}>Things I wasn't sure about — F-031</SectionLabel>
          {items.length === 0 ? (
            <div style={{ color: C.text2, fontSize: 12.5, padding: "16px 0" }}>
              All caught up — {decided} decided this pass. Playback never waited on any of them.
            </div>
          ) : items.map((r) => (
            <div key={r.id} style={{ padding: "11px 0", borderBottom: `1px solid ${C.line}` }}>
              <div className="flex items-center gap-2">
                <span style={{ fontFamily: FONT_READ, fontSize: 15, color: C.text }}>"{r.token}"</span>
                <span style={{ color: C.text2, fontSize: 11.5 }}>→ read as</span>
                <span style={{ color: C.text, fontSize: 13, background: C.lampSoft, borderRadius: 5, padding: "1px 6px" }}>{r.guess}</span>
              </div>
              <div className="flex items-center gap-1.5" style={{ marginTop: 4 }}>
                <CircleAlert size={11} style={{ color: C.lamp }} />
                <span style={{ color: C.text2, fontSize: 10.5 }}>{r.why} · {r.ch}</span>
              </div>
              <div className="flex gap-1.5" style={{ marginTop: 8 }}>
                <button onClick={() => decide(r.id)} className="flex items-center gap-1"
                  style={{ fontSize: 11.5, borderRadius: 99, padding: "5px 12px", background: C.accent, color: C.onAccent, fontWeight: 600 }}>
                  <Check size={12} /> Accept
                </button>
                <button onClick={() => setView("rule")} className="flex items-center gap-1"
                  style={{ fontSize: 11.5, borderRadius: 99, padding: "5px 12px", border: `1px solid ${C.line}`, color: C.text, background: "transparent" }}>
                  <Pencil size={12} /> Edit
                </button>
                <button onClick={() => decide(r.id)} className="flex items-center gap-1"
                  style={{ fontSize: 11.5, borderRadius: 99, padding: "5px 12px", border: `1px solid ${C.line}`, color: C.text2, background: "transparent" }}>
                  <X size={12} /> Reject
                </button>
              </div>
            </div>
          ))}
          <Note C={C}>
            Flags arrive batched, never one interruption per sentence. The engine already
            applied its best guess so playback never stopped (F-029); this list is where
            you settle them later. Whether "accept" also authors a reusable rule is the
            recorded boundary that resolves with F-042/F-047.
          </Note>
        </div>
      )}

      {view === "rule" && (
        <div>
          <SectionLabel C={C}>Make it say this right — F-036</SectionLabel>
          <div style={{ background: C.surface, border: `1px solid ${C.line}`, borderRadius: 14, padding: "14px 15px" }}>
            <div style={{ color: C.text2, fontSize: 11 }}>When the text says</div>
            <div style={{ fontFamily: FONT_READ, fontSize: 17, color: C.text, marginTop: 2 }}>NOAA</div>

            <div className="flex gap-1.5" style={{ marginTop: 12 }}>
              {[["replace", "Say it as…", <Ear size={12} key="e" />], ["suppress", "Don't say it", <Ban size={12} key="b" />]].map(([id, label, icon]) => {
                const on = op === id;
                return (
                  <button key={id} onClick={() => setOp(id)} className="flex items-center gap-1.5"
                    style={{
                      fontSize: 11.5, borderRadius: 99, padding: "6px 12px",
                      background: on ? C.surfaceRaised : "transparent",
                      color: on ? C.text : C.text2,
                      border: `1px solid ${on ? C.accent + "66" : C.line}`,
                    }}>
                    {icon}{label}
                  </button>
                );
              })}
            </div>

            {op === "replace" && (
              <>
                <div style={{ color: C.text2, fontSize: 11, marginTop: 14 }}>Respelling — plain letters, the way it should sound</div>
                <div className="flex items-center gap-2" style={{ marginTop: 6 }}>
                  <input value={respell} onChange={(e) => setRespell(e.target.value)}
                    aria-label="Respelling"
                    style={{
                      flex: 1, background: C.surfaceRaised, border: `1px solid ${C.line}`,
                      borderRadius: 10, padding: "9px 12px", color: C.text, fontSize: 15,
                      fontFamily: FONT_READ, outline: "none", minWidth: 0,
                    }} />
                  <button onClick={() => setHearing(true)} className="flex items-center gap-1.5"
                    aria-label="Hear it now"
                    style={{
                      background: hearing ? C.surfaceRaised : C.lampSoft, border: `1px solid ${C.lamp}66`,
                      color: C.text, borderRadius: 10, padding: "9px 13px", fontSize: 12.5, fontWeight: 500, flexShrink: 0,
                    }}>
                    {hearing ? <Loader2 size={14} style={{ color: C.lamp, animation: "spin 1s linear infinite" }} /> : <Play size={14} style={{ color: C.lamp }} />}
                    Hear it
                  </button>
                </div>
                <div style={{ color: C.text2, fontSize: 10.5, marginTop: 6 }}>
                  "Hear it" speaks just this respelling on your <i>current</i> voice (F-037) —
                  adjust by ear, then save. Respellings only; never raw phonemes (D6b).
                </div>
              </>
            )}

            <div className="flex justify-end" style={{ marginTop: 12 }}>
              <button style={{ background: C.accent, color: C.onAccent, borderRadius: 99, padding: "8px 18px", fontSize: 12.5, fontWeight: 600 }}>
                Save rule
              </button>
            </div>
          </div>
          <Note C={C}>
            A pronunciation rule is just the general rule object (F-042) wearing its
            replace-or-suppress form; resolution order and packs live in F-043/F-044.
          </Note>
        </div>
      )}

      {view === "toggles" && (
        <div>
          <SectionLabel C={C}>What gets normalized — F-030</SectionLabel>
          <div className="flex gap-1.5" style={{ marginBottom: 8 }}>
            {[["global", "All books"], ["book", "This book"]].map(([id, label]) => {
              const on = scope === id;
              return (
                <button key={id} onClick={() => setScope(id)}
                  style={{
                    fontSize: 11.5, borderRadius: 99, padding: "5px 12px",
                    background: on ? C.surfaceRaised : "transparent",
                    color: on ? C.text : C.text2,
                    border: `1px solid ${on ? C.accent + "66" : C.line}`,
                  }}>
                  {label}
                </button>
              );
            })}
          </div>
          {Object.entries({
            numbers: "Numbers — \"42\" → \"forty-two\"",
            dates: "Dates — \"3/14\" → \"March fourteenth\"",
            abbrev: "Abbreviations — \"Dr.\" → \"Doctor\"",
            acronyms: "Acronyms — spell or speak",
            symbols: "Symbols — \"&\" → \"and\"",
          }).map(([key, label]) => {
            const on = toggles[key];
            return (
              <button key={key} onClick={() => setToggles((t) => ({ ...t, [key]: !t[key] }))}
                className="flex items-center gap-3 w-full text-left"
                style={{ padding: "11px 0", borderBottom: `1px solid ${C.line}`, background: "transparent" }}>
                <div className="flex-1">
                  <div style={{ fontSize: 13.5, color: C.text }}>{label}</div>
                </div>
                <div style={{
                  width: 40, height: 22, borderRadius: 99, position: "relative",
                  background: on ? C.accent : C.surfaceRaised, border: `1px solid ${on ? C.accent : C.line}`,
                }}>
                  <div style={{
                    position: "absolute", top: 2, left: on ? 20 : 2, width: 16, height: 16,
                    borderRadius: 99, background: on ? C.onAccent : C.text2, transition: "left 150ms ease",
                  }} />
                </div>
              </button>
            );
          })}
          <Note C={C}>
            The switchboard: defaults come from D19; your choice always wins over a
            default; per-book overrides global. Sub-classes fold out in the real build —
            this sketch shows the top level only.
          </Note>
        </div>
      )}
    </div>
  );
}

/* ==================== 3 · PLAYBACK SETTINGS (F-005 / F-006 / F-007) ==================== */

function PlaybackSketch({ C }) {
  const [speed, setSpeed] = useState(1.0);
  const [feel, setFeel] = useState("natural");

  return (
    <div>
      <div style={{ fontFamily: FONT_DISPLAY, fontWeight: 700, fontSize: 20, color: C.text }}>Playback</div>
      <div style={{ color: C.text2, fontSize: 11, marginTop: 2 }}>settings rows — hosted by F-064, owned by their features</div>

      <SectionLabel C={C}>Speed — F-005</SectionLabel>
      <div style={{ background: C.surface, border: `1px solid ${C.line}`, borderRadius: 14, padding: "13px 15px" }}>
        <div className="flex items-center justify-between">
          <span className="flex items-center gap-2" style={{ color: C.text, fontSize: 14 }}>
            <Gauge size={16} style={{ color: C.lamp }} /> {speed.toFixed(1)}×
          </span>
          {speed > 2.5 && (
            <span style={{ color: C.lamp, fontSize: 10.5 }}>allowed, not promised — voice-dependent</span>
          )}
        </div>
        <input type="range" min={0.5} max={3.0} step={0.1} value={speed}
          onChange={(e) => setSpeed(parseFloat(e.target.value))}
          aria-label="Playback speed"
          style={{ width: "100%", marginTop: 10, accentColor: C.accent }} />
        <div className="flex justify-between" style={{ color: C.text2, fontSize: 10 }}>
          <span>0.5×</span><span>3.0×</span>
        </div>
        <div className="flex gap-1.5" style={{ marginTop: 10 }}>
          {[1.0, 1.25, 1.5, 2.0].map((p) => (
            <button key={p} onClick={() => setSpeed(p)}
              style={{
                fontSize: 11.5, borderRadius: 99, padding: "5px 12px",
                background: Math.abs(speed - p) < 0.01 ? C.surfaceRaised : "transparent",
                color: Math.abs(speed - p) < 0.01 ? C.text : C.text2,
                border: `1px solid ${Math.abs(speed - p) < 0.01 ? C.accent + "66" : C.line}`,
              }}>
              {p}×
            </button>
          ))}
        </div>
        <Note C={C}>Instant — playback-layer, nothing re-renders. 0.1× steps.</Note>
      </div>

      <SectionLabel C={C}>Pause between sentences — F-006</SectionLabel>
      <div style={{ background: C.surface, border: `1px solid ${C.line}`, borderRadius: 14, padding: "13px 15px" }}>
        <div className="flex" style={{ background: C.surfaceRaised, borderRadius: 12, padding: 3 }}>
          {[["tight", "Tight"], ["natural", "Natural"], ["relaxed", "Relaxed"]].map(([id, label]) => {
            const on = feel === id;
            return (
              <button key={id} onClick={() => setFeel(id)}
                style={{
                  flex: 1, padding: "9px 0", borderRadius: 9, fontSize: 13, fontWeight: 500,
                  background: on ? C.surface : "transparent",
                  color: on ? C.text : C.text2,
                  boxShadow: on ? `0 0 0 1px ${C.accent}` : "none",
                }}>
                {label}
              </button>
            );
          })}
        </div>
        <Note C={C}>
          By feel, not milliseconds — this beat is the calibrated pitch-reset that keeps a
          long listen from drifting (D26). Applies live to the next gaps. The "…" and
          em-dash pauses <i>inside</i> sentences stay the model's job.
        </Note>
      </div>

      <SectionLabel C={C}>Volume — F-007</SectionLabel>
      <div style={{
        background: "transparent", border: `1px dashed ${C.line}`, borderRadius: 14,
        padding: "13px 15px",
      }}>
        <div className="flex items-center gap-2" style={{ color: C.text2, fontSize: 13 }}>
          <Volume2 size={16} /> Deliberately no knob here.
        </div>
        <Note C={C}>
          V1 volume <b>is</b> the phone's media volume — hardware keys and the system
          slider, like every other audio app. An in-app control would duplicate and fight
          the system's. The gain/trim seam is reserved for V2 with nothing built. This
          absence is the design.
        </Note>
      </div>
    </div>
  );
}

/* ==================== 4 · FIRST-RUN (F-070) ==================== */

function OnboardingSketch({ C }) {
  const [step, setStep] = useState(0); /* 0 welcome+permissions · 1 voice gate · 2 book · 3 done */
  const [hasVoice, setHasVoice] = useState(false);

  const steps = ["Welcome", "A voice", "A book", "Listen"];

  return (
    <div>
      <div style={{ fontFamily: FONT_DISPLAY, fontWeight: 700, fontSize: 20, color: C.text }}>First run</div>
      <div style={{ color: C.text2, fontSize: 11, marginTop: 2 }}>F-070 · the critical path, never a dead end (D73)</div>

      <div className="flex items-center gap-1.5" style={{ marginTop: 14 }}>
        {steps.map((s, i) => (
          <React.Fragment key={s}>
            <div className="flex items-center gap-1.5">
              <div style={{
                width: 20, height: 20, borderRadius: 99, fontSize: 10.5, fontWeight: 600,
                display: "flex", alignItems: "center", justifyContent: "center",
                background: i < step ? C.accent : i === step ? C.lampSoft : C.surfaceRaised,
                color: i < step ? C.onAccent : i === step ? C.lamp : C.text2,
                border: `1px solid ${i === step ? C.lamp + "66" : C.line}`,
              }}>
                {i < step ? <Check size={11} /> : i + 1}
              </div>
              <span style={{ fontSize: 10, color: i === step ? C.text : C.text2 }}>{s}</span>
            </div>
            {i < steps.length - 1 && <div style={{ flex: 1, height: 1, background: C.line }} />}
          </React.Fragment>
        ))}
      </div>

      <div style={{ background: C.surface, border: `1px solid ${C.line}`, borderRadius: 16, padding: "18px 16px", marginTop: 14, minHeight: 240 }}>
        {step === 0 && (
          <div>
            <Rocket size={24} style={{ color: C.lamp }} />
            <div style={{ fontFamily: FONT_DISPLAY, fontWeight: 600, fontSize: 18, color: C.text, marginTop: 10 }}>
              Golem Reader reads your books aloud.
            </div>
            <div style={{ color: C.text2, fontSize: 12.5, lineHeight: 1.6, marginTop: 8 }}>
              Everything runs on your phone — no account, no cloud, your books stay yours.
              It needs permission to keep reading with the screen off.
            </div>
            <button onClick={() => setStep(1)} className="flex items-center gap-2"
              style={{ marginTop: 16, background: C.accent, color: C.onAccent, borderRadius: 99, padding: "10px 18px", fontSize: 13.5, fontWeight: 600 }}>
              <ShieldCheck size={15} /> Allow & continue
            </button>
          </div>
        )}

        {step === 1 && (
          <div>
            <Mic2 size={24} style={{ color: C.lamp }} />
            <div style={{ fontFamily: FONT_DISPLAY, fontWeight: 600, fontSize: 18, color: C.text, marginTop: 10 }}>
              Add a voice to begin
            </div>
            <div style={{ color: C.text2, fontSize: 12.5, lineHeight: 1.6, marginTop: 8 }}>
              Without a voice there's no speech — this is the one step that can't be
              skipped (the hard gate, D72). The app never drops you into a silent player.
            </div>
            {!hasVoice ? (
              <button onClick={() => setHasVoice(true)} className="flex items-center gap-2"
                style={{ marginTop: 16, background: C.accent, color: C.onAccent, borderRadius: 99, padding: "10px 18px", fontSize: 13.5, fontWeight: 600 }}>
                <Download size={15} /> Import a voice model
              </button>
            ) : (
              <div>
                <div className="flex items-center gap-2" style={{ marginTop: 14, color: C.text, fontSize: 13 }}>
                  <CheckCircle2 size={16} style={{ color: C.accent }} /> Nora (Kokoro) imported — now the active voice
                </div>
                <button onClick={() => setStep(2)} className="flex items-center gap-2"
                  style={{ marginTop: 12, background: C.accent, color: C.onAccent, borderRadius: 99, padding: "10px 18px", fontSize: 13.5, fontWeight: 600 }}>
                  Continue <ChevronRight size={15} />
                </button>
              </div>
            )}
          </div>
        )}

        {step === 2 && (
          <div>
            <BookOpen size={24} style={{ color: C.lamp }} />
            <div style={{ fontFamily: FONT_DISPLAY, fontWeight: 600, fontSize: 18, color: C.text, marginTop: 10 }}>
              Now, a book
            </div>
            <div style={{ color: C.text2, fontSize: 12.5, lineHeight: 1.6, marginTop: 8 }}>
              This hands off to the library's own two-door import (D121) — files or a
              whole folder. Skipping is fine: you land on the shelf's empty-state invite
              (F-019), never a dead end.
            </div>
            <div className="flex gap-2" style={{ marginTop: 16 }}>
              <button onClick={() => setStep(3)} className="flex items-center gap-2"
                style={{ background: C.accent, color: C.onAccent, borderRadius: 99, padding: "10px 18px", fontSize: 13.5, fontWeight: 600 }}>
                <Plus size={15} /> Add books
              </button>
              <button onClick={() => setStep(3)}
                style={{ border: `1px solid ${C.line}`, color: C.text2, background: "transparent", borderRadius: 99, padding: "10px 16px", fontSize: 13 }}>
                Later
              </button>
            </div>
          </div>
        )}

        {step === 3 && (
          <div>
            <Sparkles size={24} style={{ color: C.lamp }} />
            <div style={{ fontFamily: FONT_DISPLAY, fontWeight: 600, fontSize: 18, color: C.text, marginTop: 10 }}>
              That's it — press play.
            </div>
            <div style={{ color: C.text2, fontSize: 12.5, lineHeight: 1.6, marginTop: 8 }}>
              First-run itself honors all four accessibility contracts from the start —
              contrast, scaling, motion, keyboard (D74). It isn't a tour; it's the
              shortest path to a voice reading a book.
            </div>
            <button onClick={() => { setStep(0); setHasVoice(false); }}
              style={{ marginTop: 16, border: `1px solid ${C.line}`, color: C.text2, background: "transparent", borderRadius: 99, padding: "9px 16px", fontSize: 12.5 }}>
              <span className="flex items-center gap-1.5"><ChevronLeft size={14} /> Replay sketch</span>
            </button>
          </div>
        )}
      </div>

      <Note C={C}>
        D100's deferral condition holds: F-070 lands in the phase that has both the
        library (P3 ✓) and voice import (voice-manager phase) — this sketch is what it
        aims at, not a schedule.
      </Note>
    </div>
  );
}

/* ==================== 5 · IMAGES (F-074 V1 minimal) + honest F-075 row ==================== */

function ImagesSketch({ C }) {
  const [locked, setLocked] = useState(false);
  const dark = C.name === "dark";

  const chapterImages = [
    { id: "i1", caption: "The harbor map, folded twice", a: "#233447", b: "#0E141C", al: "#B9CBDD", bl: "#E8EFF6" },
    { id: "i2", caption: "The ledger's first page", a: "#3C3A22", b: "#14130B", al: "#DAD6AE", bl: "#F2F0DD" },
  ];

  return (
    <div>
      <div style={{ fontFamily: FONT_DISPLAY, fontWeight: 700, fontSize: 20, color: C.text }}>Images</div>
      <div style={{ color: C.text2, fontSize: 11, marginTop: 2 }}>
        F-074 · V1 minimal · reached by tapping the cover (D52)
      </div>

      <SectionLabel C={C}>Cover</SectionLabel>
      <div style={{
        width: "100%", height: 170, borderRadius: 14, border: `1px solid ${C.line}`,
        background: dark
          ? "linear-gradient(160deg, #2A3E2C, #10160E)"
          : "linear-gradient(160deg, #C9D8B0, #EFF3E2)",
        display: "flex", alignItems: "flex-end", padding: 12,
      }}>
        <span style={{ fontFamily: FONT_DISPLAY, fontSize: 15, color: dark ? "rgba(216,221,204,0.85)" : "rgba(30,38,26,0.75)" }}>
          The Binder's Ledger
        </span>
      </div>

      <SectionLabel C={C}>In this chapter — Ch. 5, "What the Candle Saw"</SectionLabel>
      {chapterImages.map((img) => (
        <div key={img.id} style={{ marginBottom: 12 }}>
          <div style={{
            width: "100%", height: 110, borderRadius: 12, border: `1px solid ${C.line}`,
            background: `linear-gradient(150deg, ${dark ? img.a : img.al}, ${dark ? img.b : img.bl})`,
            display: "flex", alignItems: "center", justifyContent: "center",
          }}>
            <ImageGlyph size={22} style={{ color: dark ? "rgba(216,221,204,0.4)" : "rgba(30,38,26,0.35)" }} />
          </div>
          <div style={{ color: C.text2, fontSize: 11, marginTop: 4, fontFamily: FONT_READ, fontStyle: "italic" }}>
            {img.caption}
          </div>
        </div>
      ))}
      <Note C={C}>
        A simple single/list view of the cover and the current chapter's pictures —
        that's the whole of V1. Pan, zoom, and the real gallery are the F-074 body,
        deferred to V2 with the seam left here. The images themselves come from F-018's
        extraction bump (v1.0.1, OB-D58-image) — this surface only consumes them.
      </Note>

      <SectionLabel C={C}>The action row, honestly — F-075</SectionLabel>
      <div style={{ background: C.surface, border: `1px solid ${C.line}`, borderRadius: 14, padding: "11px 13px" }}>
        <div className="flex items-center gap-2">
          <button onClick={() => setLocked((l) => !l)} className="flex items-center gap-1.5"
            aria-label={locked ? "Unlock rotation" : "Lock rotation"}
            style={{
              background: locked ? C.lampSoft : C.surfaceRaised,
              border: `1px solid ${locked ? C.lamp + "66" : C.line}`,
              color: C.text, borderRadius: 99, padding: "7px 13px", fontSize: 12,
            }}>
            {locked ? <Lock size={13} style={{ color: C.lamp }} /> : <Unlock size={13} style={{ color: C.text2 }} />}
            {locked ? "Rotation locked" : "Rotation free"}
          </button>
          <span style={{ color: C.dead, fontSize: 11, marginLeft: "auto" }}>· · ·</span>
        </div>
        <Note C={C}>
          In the shipping app this row renders <b>one</b> control in V1 — the rotation
          lock. The search (F-076) and sleep (F-077) spots are <b>reserved but not
          drawn</b> until those features exist: no dead buttons, ever. The dimmed pills
          in the v0.x contract prototypes are a map-preview convention; the shipping
          rule is absence.
        </Note>
      </div>
    </div>
  );
}

/* ==================== 6 · READING DISPLAY OVERRIDES (F-078) ==================== */

function ReadingDisplaySketch({ C }) {
  const [dysFont, setDysFont] = useState(false);
  const [spacing, setSpacing] = useState("normal"); /* compact | normal | open */
  const [bold, setBold] = useState(false);

  const lineHeights = { compact: 1.5, normal: 1.75, open: 2.1 };
  const previewFont = dysFont ? "Verdana, 'Trebuchet MS', sans-serif" : FONT_READ;

  const OverrideToggle = ({ label, icon, on, onFlip, note }) => (
    <button onClick={onFlip} className="flex items-center gap-3 w-full text-left"
      style={{ padding: "11px 0", borderBottom: `1px solid ${C.line}`, background: "transparent" }}>
      <div style={{ color: on ? C.lamp : C.text2 }}>{icon}</div>
      <div className="flex-1">
        <div style={{ fontSize: 13.5, color: C.text }}>{label}</div>
        {note && <div style={{ color: C.text2, fontSize: 10.5 }}>{note}</div>}
      </div>
      <div style={{
        width: 40, height: 22, borderRadius: 99, position: "relative",
        background: on ? C.accent : C.surfaceRaised, border: `1px solid ${on ? C.accent : C.line}`,
      }}>
        <div style={{
          position: "absolute", top: 2, left: on ? 20 : 2, width: 16, height: 16,
          borderRadius: 99, background: on ? C.onAccent : C.text2, transition: "left 150ms ease",
        }} />
      </div>
    </button>
  );

  return (
    <div>
      <div style={{ fontFamily: FONT_DISPLAY, fontWeight: 700, fontSize: 20, color: C.text }}>Reading display</div>
      <div style={{ color: C.text2, fontSize: 11, marginTop: 2 }}>
        F-078 · accessibility wins over the theme — always
      </div>

      <SectionLabel C={C}>Overrides</SectionLabel>
      <OverrideToggle label="Dyslexia-friendly font" icon={<Type size={16} />} on={dysFont}
        onFlip={() => setDysFont((v) => !v)} note="Beats the theme's font wherever they collide" />
      <OverrideToggle label="Bold text" icon={<BoldIcon size={16} />} on={bold}
        onFlip={() => setBold((v) => !v)} />

      <div style={{ padding: "11px 0", borderBottom: `1px solid ${C.line}` }}>
        <div className="flex items-center gap-3" style={{ marginBottom: 8 }}>
          <AlignJustify size={16} style={{ color: spacing !== "normal" ? C.lamp : C.text2 }} />
          <span style={{ fontSize: 13.5, color: C.text }}>Line spacing</span>
        </div>
        <div className="flex" style={{ background: C.surfaceRaised, borderRadius: 12, padding: 3 }}>
          {[["compact", "Compact"], ["normal", "Normal"], ["open", "Open"]].map(([id, label]) => {
            const on = spacing === id;
            return (
              <button key={id} onClick={() => setSpacing(id)}
                style={{
                  flex: 1, padding: "8px 0", borderRadius: 9, fontSize: 12.5, fontWeight: 500,
                  background: on ? C.surface : "transparent",
                  color: on ? C.text : C.text2,
                  boxShadow: on ? `0 0 0 1px ${C.accent}` : "none",
                }}>
                {label}
              </button>
            );
          })}
        </div>
      </div>

      <SectionLabel C={C}>Live on the reading view</SectionLabel>
      <div style={{
        background: C.surface, border: `1px solid ${C.line}`, borderRadius: 14, padding: "14px 15px",
        fontFamily: previewFont, fontSize: dysFont ? 15.5 : 17,
        lineHeight: lineHeights[spacing], color: C.text,
        fontWeight: bold ? 600 : 400,
        letterSpacing: dysFont ? 0.3 : 0,
        transition: "line-height 200ms ease",
      }}>
        The lamp had burned low, but Idris kept reading.{" "}
        <span style={{ background: C.lampSoft, borderRadius: 6, padding: "1px 3px", boxShadow: `0 0 0 1px ${C.lamp}55` }}>
          Outside, the harbor bell counted out the hour, twice.
        </span>{" "}
        "You were expected an hour ago," the old binder said.
      </div>
      <Note C={C}>
        The locked rule, drawn live: flip any override and it beats the theme (F-065) on
        that property — accessibility is not a suggestion the theme can decline. Choices
        persist; they govern the full Reading View and the preview strip where they share
        display text. The dyslexia face here is a stand-in rendering for the sketch. V2
        seam reserved, not built: line numbers and line ruling.
      </Note>
    </div>
  );
}

/* ============================ SHELL ============================ */

export default function V1HorizonConcepts() {
  const [screen, setScreen] = useState("voices");
  const [dark, setDark] = useState(true);
  const C = dark ? PALETTES.dark : PALETTES.light;

  const screens = [
    ["voices", "Voices", <Mic2 size={13} key="v" />],
    ["speech", "Speech", <Wand2 size={13} key="s" />],
    ["playback", "Playback", <Gauge size={13} key="p" />],
    ["images", "Images", <ImageGlyph size={13} key="i" />],
    ["reading", "Reading", <Type size={13} key="r" />],
    ["firstrun", "First run", <Rocket size={13} key="f" />],
  ];

  return (
    <div className="flex flex-col" style={{
      width: 390, height: 780, margin: "0 auto", background: C.bg,
      fontFamily: FONT_BODY, borderRadius: 32, overflow: "hidden",
      border: `1px solid ${C.line}`, position: "relative",
      transition: "background 200ms ease",
    }}>
      <style>{`@keyframes spin { to { transform: rotate(360deg); } }`}</style>

      <div className="flex items-center justify-center gap-2" style={{
        background: C.lampSoft, color: C.text, fontSize: 9.5, letterSpacing: 0.4,
        textTransform: "uppercase", padding: "5px 0", flexShrink: 0,
        borderBottom: `1px solid ${C.line}`,
      }}>
        V1 horizon · concept sketches v2 — NOT a contract
        <button onClick={() => setDark((d) => !d)} aria-label="Toggle theme"
          style={{ color: C.text2, background: "transparent", display: "flex" }}>
          {dark ? <Sun size={12} /> : <Moon size={12} />}
        </button>
      </div>

      <div className="flex gap-1 px-3 pt-3 pb-1" style={{ flexShrink: 0, flexWrap: "wrap" }}>
        {screens.map(([id, label, icon]) => {
          const on = screen === id;
          return (
            <button key={id} onClick={() => setScreen(id)} className="flex items-center gap-1"
              style={{
                flexBasis: "31%", flexGrow: 1, justifyContent: "center",
                fontSize: 10.5, borderRadius: 99, padding: "6px 0",
                background: on ? C.surfaceRaised : "transparent",
                color: on ? C.text : C.text2,
                border: `1px solid ${on ? C.accent + "66" : C.line}`,
              }}>
              {icon}{label}
            </button>
          );
        })}
      </div>

      <div className="flex-1 overflow-y-auto" style={{ padding: "10px 18px 20px" }}>
        {screen === "voices" && <VoiceManagerSketch C={C} />}
        {screen === "speech" && <SpeechSketch C={C} />}
        {screen === "playback" && <PlaybackSketch C={C} />}
        {screen === "images" && <ImagesSketch C={C} />}
        {screen === "reading" && <ReadingDisplaySketch C={C} />}
        {screen === "firstrun" && <OnboardingSketch C={C} />}
      </div>

      <div style={{
        background: C.navBg, borderTop: `1px solid ${C.line}`, color: C.text2,
        fontSize: 9.5, textAlign: "center", padding: "8px 14px 12px", lineHeight: 1.5, flexShrink: 0,
      }}>
        Future phases mint their own contracts at their own G1s — these sketches are
        input, never precedent. Frozen contract line: v0.4.0 (D123).
      </div>
    </div>
  );
}
