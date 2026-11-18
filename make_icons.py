"""Generate Force Racing launcher icons from the site's own logo."""
from pathlib import Path

from PIL import Image

SRC = Path(r"C:\Users\ijk11\force-racing-app-src\logo\favicon.png")
RES = Path(r"C:\Users\ijk11\force-racing-app-src\res")
BG = (11, 11, 13, 255)
RED = (251, 1, 2)


def keyed_logo() -> Image.Image:
    """Turn the white-backed favicon into a clean red mark on transparency."""
    img = Image.open(SRC).convert("RGBA")
    out = Image.new("RGBA", img.size)
    src = img.load()
    dst = out.load()
    for y in range(img.height):
        for x in range(img.width):
            r, g, b, _ = src[x, y]
            # distance from white decides coverage; colour is forced to brand red
            alpha = 255 - min(r, g, b)
            dst[x, y] = (RED[0], RED[1], RED[2], alpha)
    return out.crop(out.getbbox())


def paste_center(canvas: Image.Image, art: Image.Image, fraction: float) -> None:
    target = int(canvas.width * fraction)
    w, h = art.size
    scale = min(target / w, target / h)
    resized = art.resize((max(1, int(w * scale)), max(1, int(h * scale))), Image.LANCZOS)
    canvas.alpha_composite(
        resized,
        ((canvas.width - resized.width) // 2, (canvas.height - resized.height) // 2),
    )


def rounded_square(size: int, radius_fraction: float = 0.22) -> Image.Image:
    from PIL import ImageDraw

    mask = Image.new("L", (size, size), 0)
    draw = ImageDraw.Draw(mask)
    radius = int(size * radius_fraction)
    draw.rounded_rectangle((0, 0, size - 1, size - 1), radius=radius, fill=255)
    tile = Image.new("RGBA", (size, size), BG)
    tile.putalpha(mask)
    return tile


def main() -> None:
    art = keyed_logo()
    print("logo box", art.size)

    legacy = {"mdpi": 48, "hdpi": 72, "xhdpi": 96, "xxhdpi": 144, "xxxhdpi": 192}
    for density, size in legacy.items():
        folder = RES / f"mipmap-{density}"
        folder.mkdir(parents=True, exist_ok=True)
        canvas = rounded_square(size)
        paste_center(canvas, art, 0.52)
        canvas.save(folder / "ic_launcher.png")
        canvas.save(folder / "ic_launcher_round.png")

    # adaptive foreground: 108dp canvas, art inside the 66dp safe zone
    foreground = {"mdpi": 108, "hdpi": 162, "xhdpi": 216, "xxhdpi": 324, "xxxhdpi": 432}
    for density, size in foreground.items():
        folder = RES / f"mipmap-{density}"
        folder.mkdir(parents=True, exist_ok=True)
        canvas = Image.new("RGBA", (size, size), (0, 0, 0, 0))
        paste_center(canvas, art, 0.52)
        canvas.save(folder / "ic_launcher_foreground.png")

    anydpi = RES / "mipmap-anydpi-v26"
    anydpi.mkdir(parents=True, exist_ok=True)
    (anydpi / "ic_launcher.xml").write_text(
        '<?xml version="1.0" encoding="utf-8"?>\n'
        '<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">\n'
        '    <background android:drawable="@color/ic_launcher_background" />\n'
        '    <foreground android:drawable="@mipmap/ic_launcher_foreground" />\n'
        "</adaptive-icon>\n",
        encoding="utf-8",
    )
    (anydpi / "ic_launcher_round.xml").write_text(
        (anydpi / "ic_launcher.xml").read_text(encoding="utf-8"), encoding="utf-8"
    )

    values = RES / "values"
    values.mkdir(parents=True, exist_ok=True)
    (values / "colors.xml").write_text(
        '<?xml version="1.0" encoding="utf-8"?>\n'
        "<resources>\n"
        '    <color name="ic_launcher_background">#0B0B0D</color>\n'
        '    <color name="brand_red">#FB0102</color>\n'
        "</resources>\n",
        encoding="utf-8",
    )

    # splash logo (drawable), 2x for crispness
    drawable = RES / "drawable"
    drawable.mkdir(parents=True, exist_ok=True)
    splash = Image.new("RGBA", (512, 512), (0, 0, 0, 0))
    paste_center(splash, art, 0.86)
    splash.save(drawable / "logo.png")
    print("icons written")


if __name__ == "__main__":
    main()
