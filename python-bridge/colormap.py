"""
colormap.py — Colormap service for Sionna Visualizer.
Provides research-quality colour palettes for chart exports and publications.
"""
from typing import List


# ─── Palette definitions ──────────────────────────────────────────────────────

DEFAULT_COLORS = ["#64ffda", "#ff9800", "#e91e63", "#2196f3", "#9c27b0",
                  "#00bcd4", "#8bc34a", "#ff5722"]

PUBLICATION_COLORS = ["#0077BB", "#EE7733", "#009988", "#CC3311", "#AA3377"]

# Each continuous palette: list of (R,G,B) stops to interpolate
CONTINUOUS_PALETTES = {
    "viridis":   [(68,1,84), (59,82,139), (33,145,140), (94,201,98), (253,231,37)],
    "plasma":    [(13,8,135), (126,3,168), (204,71,120), (248,149,64), (240,249,33)],
    "grayscale": [(255,255,255), (180,180,180), (120,120,120), (60,60,60), (0,0,0)],
    "cool":      [(0,255,255), (64,191,255), (128,128,255), (191,64,255), (255,0,255)],
    "warm":      [(255,255,0), (255,200,0), (255,150,0), (255,100,0), (220,40,0)],
}


def _hex(r: int, g: int, b: int) -> str:
    return f"#{r:02X}{g:02X}{b:02X}"


def _interpolate_stops(stops: List[tuple], t: float) -> str:
    """Given palette stops, interpolate at position t ∈ [0,1]."""
    if t <= 0.0:
        return _hex(*stops[0])
    if t >= 1.0:
        return _hex(*stops[-1])
    n = len(stops) - 1
    scaled = t * n
    lo = int(scaled)
    hi = min(lo + 1, n)
    frac = scaled - lo
    r = int(stops[lo][0] + frac * (stops[hi][0] - stops[lo][0]))
    g = int(stops[lo][1] + frac * (stops[hi][1] - stops[lo][1]))
    b = int(stops[lo][2] + frac * (stops[hi][2] - stops[lo][2]))
    return _hex(r, g, b)


class ColormapService:
    """Generate hex colour lists for any registered colormap."""

    KNOWN = {"default", "viridis", "plasma", "grayscale", "cool", "warm", "publication"}

    def get_colors(self, colormap: str, num_colors: int) -> List[str]:
        colormap = colormap.lower() if colormap else "default"
        if colormap not in self.KNOWN:
            colormap = "default"

        if colormap == "default":
            return [DEFAULT_COLORS[i % len(DEFAULT_COLORS)] for i in range(num_colors)]

        if colormap == "publication":
            return [PUBLICATION_COLORS[i % len(PUBLICATION_COLORS)] for i in range(num_colors)]

        stops = CONTINUOUS_PALETTES[colormap]
        if num_colors == 1:
            return [_interpolate_stops(stops, 0.5)]
        return [_interpolate_stops(stops, i / (num_colors - 1)) for i in range(num_colors)]

    def get_colormap_preview(self, colormap: str) -> List[str]:
        """Always returns 5 sample hex colors for the frontend preview strip."""
        return self.get_colors(colormap, 5)

    def list_all(self) -> List[dict]:
        definitions = [
            ("default",     "Default"),
            ("viridis",     "Viridis"),
            ("plasma",      "Plasma"),
            ("grayscale",   "Grayscale"),
            ("cool",        "Cool"),
            ("warm",        "Warm"),
            ("publication", "Publication"),
        ]
        return [
            {
                "id": cid,
                "label": label,
                "preview_colors": self.get_colormap_preview(cid),
            }
            for cid, label in definitions
        ]


# Module-level singleton
colormap_service = ColormapService()
