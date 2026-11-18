import json
import re
from pathlib import Path

from bs4 import BeautifulSoup

html = Path(r"C:\Users\ijk11\force-racing-app-src\site.html").read_text("utf-8", errors="ignore")
soup = BeautifulSoup(html, "html.parser")
for tag in soup(["script", "style", "noscript"]):
    tag.decompose()

out = {}
out["nav"] = [a.get_text(" ", strip=True) for a in soup.select("#main-nav a")]
out["title"] = soup.title.get_text(strip=True) if soup.title else ""
out["headings"] = [h.get_text(" ", strip=True)[:80] for h in soup.find_all(["h1", "h2", "h3"])]
out["paras"] = [p.get_text(" ", strip=True)[:140] for p in soup.find_all("p") if len(p.get_text(strip=True)) > 40][:25]
out["links"] = sorted({a["href"] for a in soup.find_all("a", href=True) if a["href"].startswith("http")})
out["li_text"] = [li.get_text(" ", strip=True)[:60] for li in soup.find_all("li")][:60]
Path(r"C:\Users\ijk11\force-racing-app-src\site.json").write_text(
    json.dumps(out, indent=1, ensure_ascii=False), "utf-8"
)
print("nav:", out["nav"])
print("headings:", out["headings"][:25])
print("links:", out["links"][:10])
