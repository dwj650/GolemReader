import React, { useState, useEffect, useRef, useMemo } from "react";
import {
  Play, Pause, ChevronLeft, ChevronRight, ChevronDown, SkipBack, SkipForward,
  ChevronsLeft, ChevronsRight, Search, Timer, Image as ImageIcon,
  Library as LibraryIcon, Settings as SettingsIcon, Headphones, BookOpen,
  Loader2, X, Sun, Moon, Monitor, Palette, Contrast, Type, Zap, Gauge,
  Pause as PauseIcon, Volume2, Mic2, Plus, Heart, CheckCircle2, CircleDashed,
  AlertTriangle, Info, Bookmark, FileText, FolderSearch, Trash2, ArrowUpDown,
} from "lucide-react";

/* ============================================================
   Golem Reader — Visual Prototype v0.4.0 — THE FROZEN VISUAL CONTRACT
   Dummy data, no backend.

   STATUS: APPROVED at P3's G1 (operator, 2026-07-18, at draft 3)
   and frozen by D123, superseding v0.3.0 (D103). v0.3.0 and the
   G1 drafts are retained for rollback. P3's G4 compares the app
   against THIS artifact.

   Changelog draft2 -> draft3 (operator direction, G1 session):
   - SHELF SEARCH IS DAY 1. Operator context recorded: the real
     library is ~10,000 books. A search field on the Library screen
     filters by TITLE or AUTHOR (case-insensitive substring) over
     the catalog's already-stored fields; live-filters the shelf;
     clean "no matches" state. Simple by design — richer search
     stays F-062/F-076.
   - The 10k scale also re-prices (flagged for the step design
     sessions, not solved here): T-019-C1's scroll budget moves
     from "hundreds" to a 10k-row fixture, and S20's first folder
     import of a full library is a LONG-RUNNING job needing a
     survive-the-screen-off design.

   Changelog draft1 -> draft2 (operator direction, G1 session):
   - IMPORT GAINS TWO DOORS (S20), both Android's own pickers:
     "Choose files" (multi-select, ACTION_OPEN_DOCUMENT) and
     "Import a folder" (ACTION_OPEN_DOCUMENT_TREE) — a ONE-TIME
     scan that imports every EPUB in the picked folder. Batch rules
     demonstrated: progress indicator; a file that fails to parse
     is SKIPPED AND REPORTED, never kills the run; a duplicate
     ATTACHES as a source per F-021. Not an ongoing sync folder.
   - The by-folder shelf view (covers grouped by source location)
     is deferred to the Wishlist by operator direction — nothing in
     P3 forecloses it (source locations are already stored, F-021).

   Changelog v0.3.0 -> v0.4.0-draft1 (everything else unchanged):
   - LIBRARY TAB IS LIVE (F-019 / S21) and is the app's home tab —
     F-019 is the home screen. The shelf lists books with cover,
     title, author, and a state badge (F-024); sort by recent /
     title / author / state (R6); "+" opens the import flow (S20 —
     in this prototype, a mock of the system file picker); the
     empty state (R8) can be previewed with the demo toggle in the
     top ribbon. One book demonstrates the UNAVAILABLE state
     (F-022): still listed, badged, never removed (D3e).
   - BOOK DETAILS (thin shell, S23/S24) added, reached from the
     info button on a shelf row. Hosts: finished/favorite toggles
     + bookmarks (F-024), resume position (F-058), the source list
     with active-source selector (F-021), and the relink affordance
     for an unavailable source (F-022). Reparse (F-023) and
     metadata editing (F-026) are drawn dimmed as roadmap.
   - Settings map preview: the three P2 accessibility rows are now
     tagged BUILT (they shipped in S14–S16); drawn undimmed. The
     controls themselves are illustrative here — the real ones are
     in the app.
   - Entry-point note: tap-a-row OPENS the book (F-019 R4);
     the ⓘ button opens Book Details. Whether ⓘ or long-press is
     the final Details entry is an open boundary for the S23
     design session.
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

/* ---- Dummy library (F-019 shelf data). States per F-024:
   finished/favorite STORED; new/in-progress DERIVED. ---- */
const BOOKS = [
  {
    id: "b1", title: "The Binder's Ledger", author: "Maren Vasquez",
    coverA: "#2A3E2C", coverB: "#10160E", coverAL: "#C9D8B0", coverBL: "#EFF3E2",
    derived: "in-progress", favorite: true, finished: false,
    lastOpened: "Today", added: "Jun 12", resume: "Ch. 6 · sentence 4",
    available: true,
    sources: [
      { id: "s1", uri: "Downloads/binders-ledger.epub", active: true, ok: true },
      { id: "s2", uri: "Backups/ledger-copy.epub", active: false, ok: true },
    ],
    bookmarks: [
      { at: "Ch. 3 · sentence 14", note: "the ledger's first lie" },
      { at: "Ch. 5 · sentence 2", note: "" },
    ],
  },
  {
    id: "b2", title: "The Harbor Almanac", author: "T. Okonkwo",
    coverA: "#233447", coverB: "#0E141C", coverAL: "#B9CBDD", coverBL: "#E8EFF6",
    derived: "new", favorite: false, finished: false,
    lastOpened: "—", added: "Jul 02", resume: null,
    available: true,
    sources: [{ id: "s1", uri: "Books/harbor-almanac.epub", active: true, ok: true }],
    bookmarks: [],
  },
  {
    id: "b3", title: "Declensions of Fire", author: "Sable Quist",
    coverA: "#4A2B1E", coverB: "#1A0F0A", coverAL: "#E3C2AE", coverBL: "#F6EBE2",
    derived: "finished", favorite: false, finished: true,
    lastOpened: "Jun 28", added: "May 30", resume: null,
    available: true,
    sources: [{ id: "s1", uri: "Books/declensions.epub", active: true, ok: true }],
    bookmarks: [{ at: "Ch. 11 · sentence 31", note: "ending" }],
  },
  {
    id: "b4", title: "The Candle's Census", author: "R. Halloran",
    coverA: "#3C3A22", coverB: "#14130B", coverAL: "#DAD6AE", coverBL: "#F2F0DD",
    derived: "in-progress", favorite: false, finished: false,
    lastOpened: "Jun 20", added: "Jun 01", resume: "Ch. 2 · sentence 9",
    available: false, /* F-022: source missing — listed, badged, never removed */
    sources: [{ id: "s1", uri: "SD card/candles-census.epub", active: true, ok: false }],
    bookmarks: [],
  },
];

function RoadmapTag({ C, label = "Roadmap" }) {
  return (
    <span style={{
      fontSize: 8.5, letterSpacing: 0.4, textTransform: "uppercase",
      color: C.text2, border: `1px solid ${C.line}`, borderRadius: 99,
      padding: "1px 6px", marginLeft: 6,
    }}>
      {label}
    </span>
  );
}

function BuiltTag({ C }) {
  return (
    <span style={{
      fontSize: 8.5, letterSpacing: 0.4, textTransform: "uppercase",
      color: C.accent, border: `1px solid ${C.accent}55`, borderRadius: 99,
      padding: "1px 6px", marginLeft: 6,
    }}>
      Built
    </span>
  );
}

/* ================= LIBRARY — NEW IN v0.4.0 (F-019 / S21) ================= */

function StateBadge({ book, C }) {
  /* F-024 badge summary on the shelf (D15j). Derived states computed,
     favorite/finished stored. Unavailable (F-022) overrides visually. */
  if (!book.available) {
    return (
      <span className="flex items-center gap-1" style={{
        fontSize: 9.5, letterSpacing: 0.3, textTransform: "uppercase",
        color: C.lamp, border: `1px solid ${C.lamp}66`, background: C.lampSoft,
        borderRadius: 99, padding: "2px 7px",
      }}>
        <AlertTriangle size={10} /> Unavailable
      </span>
    );
  }
  const map = {
    "new": { label: "New", icon: <CircleDashed size={10} />, color: C.text2, border: C.line, bg: "transparent" },
    "in-progress": { label: "In progress", icon: <BookOpen size={10} />, color: C.accent, border: `${C.accent}55`, bg: "transparent" },
    "finished": { label: "Finished", icon: <CheckCircle2 size={10} />, color: C.text2, border: C.line, bg: C.surfaceRaised },
  };
  const s = map[book.derived];
  return (
    <span className="flex items-center gap-1" style={{
      fontSize: 9.5, letterSpacing: 0.3, textTransform: "uppercase",
      color: s.color, border: `1px solid ${s.border}`, background: s.bg,
      borderRadius: 99, padding: "2px 7px",
    }}>
      {s.icon} {s.label}
    </span>
  );
}

function Cover({ book, C, size = 52 }) {
  const dark = C.name === "dark";
  return (
    <div style={{
      width: size, height: Math.round(size * 1.35), borderRadius: 8, flexShrink: 0,
      background: `linear-gradient(160deg, ${dark ? book.coverA : book.coverAL}, ${dark ? book.coverB : book.coverBL})`,
      border: `1px solid ${C.line}`, position: "relative",
      opacity: book.available ? 1 : 0.45,
      display: "flex", alignItems: "flex-end", padding: 5,
    }}>
      <span style={{
        fontFamily: FONT_DISPLAY, fontSize: 7.5, lineHeight: 1.2,
        color: dark ? "rgba(216,221,204,0.75)" : "rgba(30,38,26,0.65)",
      }}>
        {book.title}
      </span>
    </div>
  );
}

function ImportSheet({ onClose, C }) {
  /* S20: TWO import doors, both Android's own pickers — no in-app
     file browser is ever built. "Choose files" = the system document
     picker, multi-select. "Import a folder" = the system TREE picker;
     we scan the picked folder ONCE and import every EPUB in it.
     Not a sync folder — a one-time populate. */
  const [mode, setMode] = useState("choose"); /* choose | scanning | done */
  const [count, setCount] = useState(0);
  const TOTAL = 23;

  useEffect(() => {
    if (mode !== "scanning") return;
    const id = setInterval(() => {
      setCount((c) => {
        if (c + 1 >= TOTAL) { clearInterval(id); setTimeout(() => setMode("done"), 350); }
        return Math.min(c + 1, TOTAL);
      });
    }, 90);
    return () => clearInterval(id);
  }, [mode]);

  return (
    <div onClick={onClose} style={{
      position: "absolute", inset: 0, background: "rgba(0,0,0,0.55)",
      display: "flex", alignItems: "flex-end", zIndex: 20,
    }}>
      <div onClick={(e) => e.stopPropagation()} style={{
        background: C.surfaceRaised, borderTop: `1px solid ${C.line}`,
        borderRadius: "18px 18px 0 0", width: "100%", padding: "16px 18px 22px",
      }}>
        <div className="flex items-center justify-between" style={{ marginBottom: 12 }}>
          <span style={{ fontFamily: FONT_DISPLAY, fontWeight: 600, fontSize: 16, color: C.text }}>
            Add books
          </span>
          <button onClick={onClose} style={{ color: C.text2, background: "transparent" }}><X size={18} /></button>
        </div>

        {mode === "choose" && (
          <>
            <button className="flex items-center gap-3 w-full text-left"
              style={{ background: C.surface, border: `1px solid ${C.line}`, borderRadius: 12, padding: "13px 14px" }}>
              <FileText size={19} style={{ color: C.lamp, flexShrink: 0 }} />
              <div className="flex-1">
                <div style={{ fontSize: 14.5, color: C.text, fontWeight: 500 }}>Choose files</div>
                <div style={{ color: C.text2, fontSize: 11.5, lineHeight: 1.45 }}>
                  The system picker opens — select one or several EPUBs anywhere on the phone.
                </div>
              </div>
            </button>
            <button onClick={() => { setCount(0); setMode("scanning"); }}
              className="flex items-center gap-3 w-full text-left"
              style={{ background: C.surface, border: `1px solid ${C.line}`, borderRadius: 12, padding: "13px 14px", marginTop: 8 }}>
              <FolderSearch size={19} style={{ color: C.lamp, flexShrink: 0 }} />
              <div className="flex-1">
                <div style={{ fontSize: 14.5, color: C.text, fontWeight: 500 }}>Import a folder</div>
                <div style={{ color: C.text2, fontSize: 11.5, lineHeight: 1.45 }}>
                  Pick a folder; every EPUB in it is imported in one pass. A one-time scan, not a sync.
                </div>
              </div>
            </button>
            <div style={{ color: C.text2, fontSize: 10.5, lineHeight: 1.5, marginTop: 12 }}>
              Either way: a book you already have attaches as another source —
              never a duplicate shelf entry (F-021, D64).
            </div>
          </>
        )}

        {mode === "scanning" && (
          <div style={{ padding: "6px 2px" }}>
            <div className="flex items-center gap-2" style={{ color: C.text, fontSize: 13.5 }}>
              <Loader2 size={15} style={{ animation: "spin 1.1s linear infinite", color: C.lamp }} />
              Importing {count} of {TOTAL}…
            </div>
            <div style={{ height: 4, background: C.surface, borderRadius: 99, marginTop: 10 }}>
              <div style={{ height: 4, width: `${(count / TOTAL) * 100}%`, background: C.accent, borderRadius: 99, transition: "width 90ms linear" }} />
            </div>
            <div style={{ color: C.text2, fontSize: 10.5, lineHeight: 1.5, marginTop: 10 }}>
              A file that can't be parsed is skipped and reported — it never stops the run.
            </div>
          </div>
        )}

        {mode === "done" && (
          <div style={{ padding: "6px 2px" }}>
            <div className="flex items-center gap-2" style={{ color: C.text, fontSize: 14, fontWeight: 500 }}>
              <CheckCircle2 size={16} style={{ color: C.accent }} /> Folder imported
            </div>
            <div style={{ color: C.text2, fontSize: 12.5, lineHeight: 1.7, marginTop: 8 }}>
              <b style={{ color: C.text }}>21</b> books added<br />
              <b style={{ color: C.text }}>1</b> attached to a book you already had (duplicate file)<br />
              <b style={{ color: C.text }}>1</b> skipped — couldn't be parsed (shown by name in the real app)
            </div>
            <button onClick={onClose} style={{
              marginTop: 14, background: C.accent, color: C.onAccent, borderRadius: 99,
              padding: "9px 18px", fontSize: 13, fontWeight: 600,
            }}>
              Done
            </button>
          </div>
        )}
      </div>
    </div>
  );
}

function LibraryScreen({ demoEmpty, onOpenBook, onOpenDetails, C }) {
  const [sort, setSort] = useState("recent");
  const [query, setQuery] = useState("");
  const [importSheet, setImportSheet] = useState(false);

  const sorts = [
    ["recent", "Recent"], ["title", "Title"], ["author", "Author"], ["state", "State"],
  ];

  const ordered = useMemo(() => {
    /* Day-1 shelf search: case-insensitive substring over title OR
       author — both already catalog fields. At 10k books this is a
       filter on an indexed table, not new machinery. */
    const q = query.trim().toLowerCase();
    let arr = BOOKS.filter(
      (b) => !q || b.title.toLowerCase().includes(q) || b.author.toLowerCase().includes(q)
    );
    if (sort === "title") arr.sort((a, b) => a.title.localeCompare(b.title));
    else if (sort === "author") arr.sort((a, b) => a.author.localeCompare(b.author));
    else if (sort === "state") {
      const rank = { "in-progress": 0, "new": 1, "finished": 2 };
      arr.sort((a, b) => rank[a.derived] - rank[b.derived]);
    }
    /* "recent" keeps the dummy order (b1 opened today first). */
    return arr;
  }, [sort, query]);

  return (
    <div className="flex-1 overflow-y-auto" style={{ paddingBottom: 12, position: "relative" }}>
      <div className="flex items-center justify-between px-5 pt-5 pb-1">
        <div>
          <div style={{ fontFamily: FONT_DISPLAY, fontWeight: 700, fontSize: 22, color: C.text }}>Library</div>
          <div style={{ color: C.text2, fontSize: 11, marginTop: 2 }}>
            F-019 · home screen · live in S21
          </div>
        </div>
        {/* R5 — the add-a-book affordance lives here */}
        <button onClick={() => setImportSheet(true)} aria-label="Add a book"
          style={{
            width: 42, height: 42, borderRadius: 99, background: C.accent, color: C.onAccent,
            display: "flex", alignItems: "center", justifyContent: "center",
          }}>
          <Plus size={20} />
        </button>
      </div>

      {demoEmpty ? (
        /* ---- R8: the empty / first-run state ---- */
        <div className="flex flex-col items-center justify-center px-10" style={{ marginTop: 110, textAlign: "center" }}>
          <div style={{
            width: 74, height: 96, borderRadius: 10, border: `1.5px dashed ${C.line}`,
            display: "flex", alignItems: "center", justifyContent: "center", marginBottom: 16,
          }}>
            <BookOpen size={26} style={{ color: C.text2 }} />
          </div>
          <div style={{ fontFamily: FONT_DISPLAY, fontWeight: 600, fontSize: 17, color: C.text }}>
            Your shelf is empty
          </div>
          <div style={{ color: C.text2, fontSize: 12.5, lineHeight: 1.55, marginTop: 6 }}>
            Add an EPUB from your phone and Golem Reader will read it to you.
          </div>
          <button onClick={() => setImportSheet(true)} className="flex items-center gap-2"
            style={{
              marginTop: 16, background: C.accent, color: C.onAccent, borderRadius: 99,
              padding: "10px 18px", fontSize: 13.5, fontWeight: 600,
            }}>
            <Plus size={15} /> Add a book
          </button>
          <div style={{ color: C.text2, fontSize: 10, marginTop: 14 }}>
            First-run guidance beyond this invite is F-070 <RoadmapTag C={C} />
          </div>
        </div>
      ) : (
        <>
          {/* ---- Day-1 shelf search: title or author ---- */}
          <div className="px-5 mt-1">
            <div className="flex items-center gap-2" style={{
              background: C.surface, border: `1px solid ${C.line}`, borderRadius: 12,
              padding: "9px 12px",
            }}>
              <Search size={15} style={{ color: C.text2, flexShrink: 0 }} />
              <input
                value={query}
                onChange={(e) => setQuery(e.target.value)}
                placeholder="Search title or author"
                aria-label="Search title or author"
                style={{
                  flex: 1, background: "transparent", border: "none", outline: "none",
                  color: C.text, fontSize: 13.5, fontFamily: FONT_BODY, minWidth: 0,
                }}
              />
              {query && (
                <button onClick={() => setQuery("")} aria-label="Clear search"
                  style={{ color: C.text2, background: "transparent", flexShrink: 0 }}>
                  <X size={14} />
                </button>
              )}
            </div>
          </div>

          {/* ---- R6: sort / filter (MVP set: recent · title · author · state) ---- */}
          <div className="flex items-center gap-1.5 px-5 mt-2" style={{ flexWrap: "wrap" }}>
            <ArrowUpDown size={12} style={{ color: C.text2 }} />
            {sorts.map(([id, label]) => {
              const active = sort === id;
              return (
                <button key={id} onClick={() => setSort(id)}
                  style={{
                    fontSize: 11.5, borderRadius: 99, padding: "5px 11px",
                    background: active ? C.surfaceRaised : "transparent",
                    color: active ? C.text : C.text2,
                    border: `1px solid ${active ? C.accent + "66" : C.line}`,
                  }}>
                  {label}
                </button>
              );
            })}
          </div>

          {/* ---- R3: the shelf ---- */}
          <div className="px-5 mt-3">
            {ordered.length === 0 && (
              <div style={{ textAlign: "center", color: C.text2, fontSize: 12.5, lineHeight: 1.6, padding: "36px 10px" }}>
                No books match "{query}".<br />
                <button onClick={() => setQuery("")} style={{ color: C.accent, background: "transparent", fontSize: 12.5, marginTop: 4 }}>
                  Clear search
                </button>
              </div>
            )}
            {ordered.map((b) => (
              <div key={b.id} className="flex items-center gap-3"
                style={{ padding: "11px 0", borderBottom: `1px solid ${C.line}` }}>
                {/* R4: tapping the row opens the book */}
                <button onClick={() => onOpenBook(b)} className="flex items-center gap-3 flex-1 text-left"
                  style={{ background: "transparent", minWidth: 0 }}>
                  <Cover book={b} C={C} />
                  <div className="flex-1" style={{ minWidth: 0 }}>
                    <div className="flex items-center gap-1" style={{ minWidth: 0 }}>
                      <span style={{
                        fontFamily: FONT_DISPLAY, fontWeight: 600, fontSize: 15.5, color: C.text,
                        whiteSpace: "nowrap", overflow: "hidden", textOverflow: "ellipsis",
                      }}>
                        {b.title}
                      </span>
                      {b.favorite && <Heart size={12} style={{ color: C.lamp, flexShrink: 0 }} fill={C.lamp} />}
                    </div>
                    <div style={{ color: C.text2, fontSize: 12, marginTop: 1 }}>{b.author}</div>
                    <div className="flex items-center gap-2" style={{ marginTop: 6, flexWrap: "wrap" }}>
                      <StateBadge book={b} C={C} />
                      {b.resume && b.available && (
                        <span style={{ color: C.text2, fontSize: 10.5 }}>Resume · {b.resume}</span>
                      )}
                    </div>
                  </div>
                </button>
                {/* Book Details entry — final affordance (ⓘ vs long-press) is an
                    open boundary for the S23 design session */}
                <button onClick={() => onOpenDetails(b)} aria-label={`Details for ${b.title}`}
                  style={{ color: C.text2, background: "transparent", padding: 6 }}>
                  <Info size={17} />
                </button>
              </div>
            ))}
            <div style={{ color: C.text2, fontSize: 10.5, lineHeight: 1.5, marginTop: 12 }}>
              "The Candle's Census" shows the F-022 rule: its file is missing, so it's
              badged unavailable — but it stays on the shelf with all its data. Books
              are never auto-removed (D3e). Tap a row to open the book; tap ⓘ for
              Book Details.
            </div>
          </div>
        </>
      )}

      {importSheet && <ImportSheet onClose={() => setImportSheet(false)} C={C} />}
    </div>
  );
}

/* ============ BOOK DETAILS — NEW IN v0.4.0 (thin shell, S23/S24) ============ */

function DetailsToggle({ label, icon, on, onFlip, note, C }) {
  return (
    <button onClick={onFlip} className="flex items-center gap-3 w-full text-left"
      style={{ padding: "12px 0", borderBottom: `1px solid ${C.line}`, background: "transparent" }}>
      <div style={{ color: on ? C.lamp : C.text2 }}>{icon}</div>
      <div className="flex-1">
        <div style={{ fontSize: 14.5, color: C.text }}>{label}</div>
        {note && <div style={{ color: C.text2, fontSize: 11.5 }}>{note}</div>}
      </div>
      <div style={{
        width: 40, height: 22, borderRadius: 99, position: "relative",
        background: on ? C.accent : C.surfaceRaised, border: `1px solid ${on ? C.accent : C.line}`,
        transition: "background 150ms ease",
      }}>
        <div style={{
          position: "absolute", top: 2, left: on ? 20 : 2, width: 16, height: 16,
          borderRadius: 99, background: on ? C.onAccent : C.text2, transition: "left 150ms ease",
        }} />
      </div>
    </button>
  );
}

function BookDetailsScreen({ book, onBack, C }) {
  const [finished, setFinished] = useState(book.finished);
  const [favorite, setFavorite] = useState(book.favorite);
  const [activeSource, setActiveSource] = useState(book.sources.find((s) => s.active)?.id);

  return (
    <div className="flex-1 overflow-y-auto" style={{ paddingBottom: 16 }}>
      <div className="flex items-center justify-between px-5 pt-5 pb-2">
        <button onClick={onBack} style={{ color: C.text, background: "transparent" }} aria-label="Back to Library">
          <ChevronLeft size={24} />
        </button>
        <span style={{ color: C.text2, fontSize: 11, letterSpacing: 0.6, textTransform: "uppercase" }}>Book Details</span>
        <div style={{ width: 24 }} />
      </div>

      <div className="flex items-center gap-4 px-5 pb-3">
        <Cover book={book} C={C} size={64} />
        <div className="flex-1" style={{ minWidth: 0 }}>
          <div style={{ fontFamily: FONT_DISPLAY, fontWeight: 700, fontSize: 19, color: C.text }}>
            {book.title}
          </div>
          <div style={{ color: C.text2, fontSize: 12.5 }}>{book.author}</div>
          <div className="flex items-center gap-2" style={{ marginTop: 7 }}>
            <StateBadge book={{ ...book, finished, favorite, derived: finished ? "finished" : book.derived }} C={C} />
          </div>
        </div>
      </div>

      <div className="px-5">
        {/* ---- F-058: resume position (book-level, source-independent) ---- */}
        <div style={{
          background: C.surface, border: `1px solid ${C.line}`, borderRadius: 12,
          padding: "11px 13px", fontSize: 12.5, color: C.text2, display: "flex", gap: 8, alignItems: "center",
        }}>
          <BookOpen size={14} style={{ color: C.lamp, flexShrink: 0 }} />
          {book.resume
            ? <span><b style={{ color: C.text }}>Resume at {book.resume}</b> — saved automatically (F-058 · S22)</span>
            : <span>Not started yet — position will be saved as you listen (F-058 · S22)</span>}
        </div>

        {/* ---- F-024: stored states ---- */}
        <SectionLabel C={C}>Book state — F-024 · S23</SectionLabel>
        <DetailsToggle label="Finished" icon={<CheckCircle2 size={17} />} on={finished}
          onFlip={() => setFinished((v) => !v)} note="New / in-progress are computed, never stored" C={C} />
        <DetailsToggle label="Favorite" icon={<Heart size={17} />} on={favorite}
          onFlip={() => setFavorite((v) => !v)} C={C} />

        {/* ---- F-024: bookmarks, anchored to the composite sentence index ---- */}
        <SectionLabel C={C}>Bookmarks — F-024 · S23</SectionLabel>
        {book.bookmarks.length === 0 ? (
          <div style={{ color: C.text2, fontSize: 12.5, padding: "11px 0", borderBottom: `1px solid ${C.line}` }}>
            No bookmarks yet — add them while listening.
          </div>
        ) : book.bookmarks.map((bm, i) => (
          <div key={i} className="flex items-center gap-3" style={{ padding: "11px 0", borderBottom: `1px solid ${C.line}` }}>
            <Bookmark size={15} style={{ color: C.lamp, flexShrink: 0 }} />
            <div className="flex-1">
              <div style={{ fontSize: 13.5, color: C.text }}>{bm.at}</div>
              {bm.note && <div style={{ color: C.text2, fontSize: 11.5, fontFamily: FONT_READ, fontStyle: "italic" }}>"{bm.note}"</div>}
            </div>
            <button style={{ color: C.text2, background: "transparent" }} aria-label="Remove bookmark"><Trash2 size={14} /></button>
          </div>
        ))}
        <div style={{ color: C.text2, fontSize: 10.5, marginTop: 6 }}>
          Anchored to the sentence, not a byte offset — they survive a reparse (D36).
        </div>

        {/* ---- F-021: sources + active pointer · F-022: availability/relink ---- */}
        <SectionLabel C={C}>Sources — F-021 · one book, many files</SectionLabel>
        {book.sources.map((s) => {
          const active = activeSource === s.id;
          return (
            <button key={s.id} onClick={() => s.ok && setActiveSource(s.id)}
              className="flex items-center gap-3 w-full text-left"
              style={{ padding: "12px 0", borderBottom: `1px solid ${C.line}`, background: "transparent" }}>
              <div style={{
                width: 16, height: 16, borderRadius: 99, flexShrink: 0,
                border: `2px solid ${active ? C.accent : C.line}`,
                background: active ? C.accent : "transparent",
                display: "flex", alignItems: "center", justifyContent: "center",
              }}>
                {active && <div style={{ width: 6, height: 6, borderRadius: 99, background: C.onAccent }} />}
              </div>
              <FileText size={15} style={{ color: s.ok ? C.text2 : C.lamp, flexShrink: 0 }} />
              <div className="flex-1" style={{ minWidth: 0 }}>
                <div style={{
                  fontSize: 13, color: s.ok ? C.text : C.text2,
                  whiteSpace: "nowrap", overflow: "hidden", textOverflow: "ellipsis",
                }}>
                  {s.uri}
                </div>
                <div style={{ fontSize: 10.5, color: s.ok ? C.text2 : C.lamp }}>
                  {active ? "Active source" : "Alternate"}{!s.ok && " · file can't be reached"}
                </div>
              </div>
            </button>
          );
        })}
        {!book.available && (
          /* F-022 R4/R5: re-prompt; relink only accepts a hash-matching file */
          <button className="flex items-center gap-2 w-full justify-center"
            style={{
              marginTop: 10, border: `1px solid ${C.lamp}66`, background: C.lampSoft,
              color: C.text, borderRadius: 12, padding: "10px 0", fontSize: 13, fontWeight: 500,
            }}>
            <FolderSearch size={15} style={{ color: C.lamp }} />
            Locate this book's file… (F-022 · S24)
          </button>
        )}
        <div style={{ color: C.text2, fontSize: 10.5, lineHeight: 1.5, marginTop: 8 }}>
          Relink only accepts a file with the matching identity hash — the wrong book
          is rejected with a message, never silently attached (F-022 R5). If the same
          file reappears on its own, it auto-relinks with no action needed (R3).
        </div>

        {/* ---- Roadmap on this surface ---- */}
        <SectionLabel C={C}>Later</SectionLabel>
        <MapPreviewRow icon={<Loader2 size={17} />} label="Reparse this book" note="F-023 — safe parser upgrades" C={C} />
        <MapPreviewRow icon={<Type size={17} />} label="Edit title / author" note="F-026 · V2" C={C} />
      </div>
    </div>
  );
}

/* ======================= UNCHANGED v0.3.0 SCREENS ======================= */

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
    if (dx < -60) onOpenReading();
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
            <BookOpen size={11} /> Tap to open reader — built (S13, simplified) · full preview is F-073
          </div>
        </button>
      </div>

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
    if (dx > 60) onClose();
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
        the highlight are built (F-014/F-016). Reading is reached by tapping the
        preview strip on Now Playing — the swipe gesture remains the target vision
        (D51/D52).
      </div>
    </div>
  );
}

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

function MapPreviewRow({ icon, label, note, C, built = false }) {
  return (
    <div className="flex items-center gap-3" style={{ padding: "13px 0", borderBottom: `1px solid ${C.line}`, opacity: built ? 1 : 0.45 }}>
      <div style={{ color: built ? C.lamp : C.text2 }}>{icon}</div>
      <div className="flex-1">
        <div style={{ fontSize: 14.5, color: C.text }}>{label}</div>
        {note && <div style={{ color: C.text2, fontSize: 12 }}>{note}</div>}
      </div>
      {built ? <BuiltTag C={C} /> : <RoadmapTag C={C} />}
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
          F-064 shell · hosts controls owned by their features
        </div>
      </div>

      <div className="px-5">
        <SectionLabel C={C}>Appearance</SectionLabel>
        <div style={{ padding: "13px 0", borderBottom: `1px solid ${C.line}` }}>
          <div className="flex items-center gap-3" style={{ marginBottom: 10 }}>
            <Palette size={17} style={{ color: C.lamp }} />
            <div className="flex-1">
              <div style={{ fontSize: 14.5, color: C.text }}>Theme</div>
              <div style={{ color: C.text2, fontSize: 12 }}>F-065 · built in S12/S13</div>
            </div>
          </div>
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

        {/* v0.4.0: the P2 accessibility rows are BUILT (S14–S16); the rows
            below are representations — the real controls live in the app. */}
        <SectionLabel C={C}>Accessibility — built in P2</SectionLabel>
        <MapPreviewRow icon={<Contrast size={17} />} label="High contrast" note="F-066 · S14" C={C} built />
        <MapPreviewRow icon={<Type size={17} />} label="Text size" note="F-068 · S15" C={C} built />
        <MapPreviewRow icon={<Zap size={17} />} label="Reduce motion" note="F-067 · S16" C={C} built />

        <SectionLabel C={C}>Playback — later phases</SectionLabel>
        <MapPreviewRow icon={<Gauge size={17} />} label="Speed" note="F-005" C={C} />
        <MapPreviewRow icon={<PauseIcon size={17} />} label="Pauses between sentences" note="F-006" C={C} />
        <MapPreviewRow icon={<Volume2 size={17} />} label="Volume" note="F-007" C={C} />

        <SectionLabel C={C}>Voices — later phases</SectionLabel>
        <MapPreviewRow icon={<Mic2 size={17} />} label="Voice manager" note="F-048 family" C={C} />

        <div style={{ color: C.text2, fontSize: 10.5, lineHeight: 1.5, marginTop: 18 }}>
          Dimmed rows are a map preview only. In the shipping app, a setting whose
          feature isn't built yet is absent entirely — never a dead control (D68).
        </div>
      </div>
    </div>
  );
}

function BottomNav({ tab, setTab, C }) {
  const tabs = [
    { id: "library", icon: <LibraryIcon size={20} />, label: "Library", live: true, isNew: true },
    { id: "nowplaying", icon: <Headphones size={20} />, label: "Now Playing", live: true },
    { id: "settings", icon: <SettingsIcon size={20} />, label: "Settings", live: true },
  ];
  return (
    <div style={{
      background: C.navBg, borderTop: `1px solid ${C.line}`,
      backdropFilter: "blur(12px)", display: "flex", padding: "10px 0 14px", flexShrink: 0,
    }}>
      {tabs.map((t) => {
        const isActive = tab === t.id;
        return (
          <button key={t.id} onClick={() => setTab(t.id)}
            className="flex-1 flex flex-col items-center gap-1"
            style={{ color: isActive ? C.accent : C.text2, background: "transparent" }}>
            {t.icon}
            <span style={{ fontSize: 10 }}>{t.label}</span>
            {t.isNew && (
              <span style={{
                fontSize: 7.5, letterSpacing: 0.4, textTransform: "uppercase",
                color: C.onAccent, background: C.accent, borderRadius: 99, padding: "1px 5px",
              }}>
                live in S21
              </span>
            )}
          </button>
        );
      })}
    </div>
  );
}

export default function GolemReaderPrototype() {
  const [tab, setTab] = useState("library"); /* v0.4.0: Library is home (F-019) */
  const [showReading, setShowReading] = useState(false);
  const [detailsBook, setDetailsBook] = useState(null);
  const [demoEmpty, setDemoEmpty] = useState(false);
  const [playing, setPlaying] = useState(true);
  const [buffering] = useState(false);
  const [activeIndex, setActiveIndex] = useState(3);
  const [chapter, setChapter] = useState(5);

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

  const openBook = () => { setDetailsBook(null); setTab("nowplaying"); };

  return (
    <div className="flex flex-col" style={{
      width: 390, height: 780, margin: "0 auto", background: C.bg,
      fontFamily: FONT_BODY, borderRadius: 32, overflow: "hidden",
      border: `1px solid ${C.line}`, position: "relative",
      transition: "background 200ms ease",
    }}>
      <style>{`@keyframes spin { to { transform: rotate(360deg); } }`}</style>

      <div className="flex items-center justify-center gap-2" style={{
        background: C.surfaceRaised, color: C.text2, fontSize: 9.5, letterSpacing: 0.4,
        textTransform: "uppercase", textAlign: "center", padding: "5px 0", flexShrink: 0,
      }}>
        v0.4.0 · FROZEN VISUAL CONTRACT (D123) · supersedes v0.3.0
        <button onClick={() => setDemoEmpty((v) => !v)}
          style={{
            fontSize: 8.5, letterSpacing: 0.4, textTransform: "uppercase",
            border: `1px solid ${C.line}`, borderRadius: 99, padding: "1px 7px",
            color: demoEmpty ? C.accent : C.text2, background: "transparent",
          }}>
          {demoEmpty ? "show shelf" : "show empty state"}
        </button>
      </div>

      {showReading ? (
        <ReadingScreen activeIndex={activeIndex} onClose={() => setShowReading(false)} C={C} />
      ) : detailsBook ? (
        <BookDetailsScreen book={detailsBook} onBack={() => setDetailsBook(null)} C={C} />
      ) : tab === "settings" ? (
        <SettingsScreen themeChoice={themeChoice} setThemeChoice={setThemeChoice} C={C} />
      ) : tab === "library" ? (
        <LibraryScreen demoEmpty={demoEmpty}
          onOpenBook={openBook}
          onOpenDetails={(b) => setDetailsBook(b)} C={C} />
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
