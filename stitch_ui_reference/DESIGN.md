# Design System: The Editorial Lexicon

## 1. Overview & Creative North Star
**Creative North Star: "The Obsidian Curator"**

This design system moves away from the utilitarian "database" feel of traditional dictionaries and towards a high-end, editorial experience. The goal is to treat language with the same reverence as a luxury gallery treats art. 

We break the "standard app" template by utilizing **intentional asymmetry** and **tonal depth**. Rather than rigid grids, we use expansive negative space (breathing room) and overlapping glass layers to create a sense of physical immersion. This is not just a tool; it is a sophisticated digital environment where "Dark Mode" isn't an afterthought—it is the foundation of the brand’s soul.

---

## 2. Colors
Our palette is rooted in the absence of light, using deep charcols and pure blacks to create a canvas where information glows.

### Color Tokens (Material Convention)
*   **Background:** `#131313` (Deep Charcoal)
*   **Surface Tiers:**
    *   `surface-container-lowest`: `#0e0e0e` (Deepest Black for background recession)
    *   `surface-container`: `#1f1f1f` (Standard card base)
    *   `surface-container-highest`: `#353535` (Elevated glass layers)
*   **Accents:**
    *   `primary`: `#adc6ff` (Electric Blue highlight)
    *   `tertiary`: `#e9c400` (Refined Gold for "Word of the Day" or Premium features)

### The "No-Line" Rule
**Explicit Instruction:** Prohibit 1px solid borders for sectioning. Boundaries must be defined solely through background color shifts or tonal transitions. For example, a search bar (`surface-container-high`) should sit within a header (`surface-dim`) without a stroke.

### The Glass & Gradient Rule
To achieve a "SaaS Dashboard" premium feel, floating elements (like the definition modal) must use Glassmorphism:
*   **Fill:** `surface-variant` at 40-60% opacity.
*   **Effect:** Backdrop blur of `12px` to `20px`.
*   **Signature Texture:** Use a linear gradient for main CTAs (e.g., `primary` to `primary-container`) to provide a "lit from within" glow.

---

## 3. Typography
We utilize a pairing of **Manrope** (Display) and **Inter** (Body) to balance editorial flair with high-performance readability.

*   **Display & Headlines (Manrope):** These are the "Art Directors" of the page. Use `display-lg` (3.5rem) for the main searched word. The tight tracking and bold weight of Manrope convey authority and modernism.
*   **Body & Labels (Inter):** Inter is chosen for its exceptional legibility at small sizes. 
    *   `body-md` (0.875rem) is our workhorse for definitions.
    *   `label-sm` (0.6875rem) is used for phonetic transcriptions and metadata, always in `on-surface-variant` to maintain hierarchy.

**Hierarchy Strategy:** Information density is managed by scale, not just color. A "Headline-sm" term title should feel significantly more "heavy" than its "Body-md" definition.

---

## 4. Elevation & Depth
Depth in this system is achieved through **Tonal Layering** rather than traditional drop shadows.

### The Layering Principle
Think of the UI as stacked sheets of obsidian glass. 
*   **Base:** `surface-dim`
*   **Secondary Content:** `surface-container-low`
*   **Active Interaction:** `surface-container-highest`

### Ambient Shadows
If a floating element (like a context menu) requires a shadow, it must be "Ambient":
*   **Blur:** `30px` to `50px`.
*   **Opacity:** 6% of the `primary` color or `on-surface` color.
*   **Avoid:** Harsh, black `#000000` drop shadows.

### The "Ghost Border" Fallback
If contrast is required for accessibility, use a **Ghost Border**:
*   `outline-variant` at **15% opacity**. This creates a microscopic "glint" on the edge of the glass card without looking like a rigid box.

---

## 5. Components

### Search Bar (The Hero)
*   **Style:** `surface-container-high` background, `xl` (1.5rem) roundedness. 
*   **Interaction:** On focus, the background transitions to a subtle gradient of `surface-container-highest` with a `primary` Ghost Border.

### Definition Cards
*   **Style:** Glassmorphic. No dividers between definitions. Use `md` (0.75rem) spacing to separate "Senses" (meanings).
*   **Layout:** Intentional asymmetry. Place the word origin or etymology in a `surface-container-lowest` inset box to create visual interest.

### Action Chips (Audio/Bookmark)
*   **Style:** `full` roundedness. 
*   **Iconography:** Minimalist line icons. Use `primary` for the icon color and `on-secondary-container` for the chip background to keep it subtle.

### Lists (History/Favorites)
*   **Constraint:** **Forbid dividers.** Use `surface-container-low` for the list item and `surface` for the background. Vertical whitespace is your separator.

### Audio Player / Speaker Icon
*   **Visual:** When active, the speaker icon should pulse with a `primary-fixed-dim` outer glow (8px blur), suggesting sound waves.

---

## 6. Do's and Don'ts

### Do
*   **Do** use asymmetrical layouts (e.g., left-aligned word titles with right-aligned metadata).
*   **Do** embrace pure black (`#000000`) for the "lowest" surfaces to make the Glassmorphism pop.
*   **Do** use `primary` sparingly—only for highlights, active states, and critical CTAs.

### Don't
*   **Don't** use 1px solid borders to separate list items. It breaks the "premium glass" illusion.
*   **Don't** use standard "Blue" links. Use `tertiary` (Gold) or `primary` (Electric Blue) with a subtle underline.
*   **Don't** clutter the screen. If the definition is long, use the `surface-container` tiers to create a "reading mode" focus area.