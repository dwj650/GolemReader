---
id: F-073-DELTA-CANDIDATE
tier: reference
status: candidate — NOT locked
updated: 2026-07-15
against: F-073-Embedded-Sync-Preview-Requirements v1.0.0
cross-refs: [F-016, F-067, D56, D103, D117, OB-015-2]
resolves-at: F-073 design session (P3+)
---
# F-073 spec delta candidate — batched preview window vs. centered-scroll frame

**Status: candidate. No D-number. Nothing here is locked.** This records an operator
design challenge raised at the G4 gate (2026-07-15) against F-073 R2 as written, so the
finding survives at full fidelity until F-073 is designed. It changes no built code and
gates nothing.

## Origin

Raised by the operator at the G4 gate while the S18 remediation was being scoped. Not a
gate finding against the D103 contract — the preview strip as built is the deliberately
simplified S13 form (one sentence, tap-to-open), with F-073 named as its full form. This
is a **forward design input**, not a defect.

## The two asks

### Ask 1 — fixed-height box. **Already committed; no conflict.**
> "the bottom of the text box moves based on the length of the text. if we could make it
> static i would appreciate it"

This is **F-073 R1** verbatim: *"A frame of fixed height; the user cannot resize it in
V1."* Backed by **D56 (frame)**. The current box grows and shrinks because the built
strip is a content-wrapping `Column` holding a single sentence — the simplified S13 form,
which predates F-073. **Nothing to decide.** R1 already promises this; it is unbuilt, not
unspecified. Recorded here only so the operator's ask is visibly answered.

### Ask 2 — the highlight should move, not the text. **Conflicts with R2.**

Operator's description of current behavior:

```
reading line 1              reading line 2
        line 2      →               line 3
        line 3                      line 4
```
The highlight holds its position; the text advances beneath it.

Operator's preferred behavior:

```
Reading line 1                      line 1
        line 2      →       reading line 2
        line 3                      line 3
```
A batch of ~3 lines loads and holds still; the highlight **walks down** through the
batch; when the batch is exhausted, the next batch loads.

**The conflict.** F-073 **R2** specifies: *"The current sentence is **centered** with
roughly 2–3 sentences of surrounding display-branch text."* And §1 elaborates: *"As the
voice advances, the text **scrolls inside the little frame** to keep the current sentence
centered."* Centered-and-kept-centered means **the highlight is fixed and the text
moves** — precisely the model the operator reports as feeling jerky. The spec and the
operator's preference genuinely disagree about the core interaction. This is not a
misreading of the spec; it is a challenge to it.

## Why the challenge has merit (argument for the delta)

1. **Reduced-motion fit (F-067).** F-073 **depends-on F-067**. Under reduced motion, a
   continuous-scroll model must degrade to an instant jump anyway — which is the S16
   pattern (instant-jump highlight scroll, D110/D111). A batched window is **natively
   motion-free**: nothing scrolls, ever; the highlight simply lights a different line.
   The batched model may need *no* reduced-motion variant at all, whereas the
   centered-scroll model needs two paths and a proven-still flip.
2. **Perceived stability.** Text that holds still while an indicator moves is the
   read-along model most readers already know. Continuous re-centering means the eye
   re-acquires its place on every sentence.
3. **Cost.** A fixed batch with a moving highlight has no scroll animation, no scroll
   position to hold transient, and no starvation-vs-scroll interaction. T-001-C1 (the
   deferred cost check) has one fewer contributor.

## Why R2 may still be right (argument against the delta — kept for honesty)

1. **Batch boundaries are a new seam.** When the highlight reaches the last line of a
   batch, *something* must advance — and that transition is a jump of up to three lines
   at once, which may read as **more** jarring than a one-sentence scroll, not less.
   The centered model has no boundary because it never batches.
2. **Context asymmetry.** In a batch, the current sentence can sit at the top (no
   backward context) or the bottom (no forward context). Centering guarantees context on
   both sides always — which is R2's actual purpose.
3. **D56 backing.** R2 traces to a locked decision. Overturning it needs a decision, not
   a preference.

## Open question this leaves for the F-073 design session

Does the batch advance model (**a**) jump a full batch at the boundary, (**b**) slide by
one line as the highlight reaches the last line — a hybrid, effectively R2 with a lazier
trigger — or (**c**) something else? The operator's diagram does not specify the boundary
behavior, and **the boundary is where the two models actually differ**. Resolve with
operator evidence on real hardware, not in the abstract.

## Disposition

Carried to the **F-073 design session** (P3+). At that session: present R2 and this
challenger as competing components, resolve by operator decision, and mint a D-number
for whichever wins. If the delta wins, F-073 gets a v1.0.1 delta amending R2 and §1, and
D56's frame clause is revisited for its centering language. **Until then F-073 R2 stands
as written** — this candidate does not amend the spec.

## What is NOT affected

The S18 inserted step (D117/D118) is fenced to the status-bar inset and transport glyphs
and touches none of this. The built simplified strip remains correct as the D103-era S13
form. No known-issue entry is opened: the strip is behaving as designed for its current
simplified scope.
