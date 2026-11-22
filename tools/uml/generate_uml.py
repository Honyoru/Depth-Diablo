#!/usr/bin/env python3
"""Generates the UML class diagrams of the project as draw.io files.

Reads the compiled classes with `javap -p` (exact members, types and inheritance) and the package
dependencies with `jdeps`, then writes one .drawio file per diagram into docs/uml/.

Usage, from the repository root:
    ./gradlew classes
    python3 tools/uml/generate_uml.py

Each diagram lists its classes below (DIAGRAMS). A parent class or interface drawn in another
diagram shows as a small grey box with its name only, so every arrow stays visible. Re-run it after
each pull request; then open each file in draw.io and export its PNG preview by hand.
"""

import html
import re
import subprocess
import sys
from collections import defaultdict
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
CLASS_DIRS = [
    ROOT / "core" / "build" / "classes" / "java" / "main",
    ROOT / "desktop" / "build" / "classes" / "java" / "main",
]
OUT_DIR = ROOT / "docs" / "uml"
BASE = "com.epitech.depthdiablo"

# Each diagram: file name, title, classes drawn in full (relative to BASE; "$" for nested types).
DIAGRAMS = [
    ("01-engine", "Engine: world, fixed step, events, rewind photo", [
        "engine.World", "engine.EventBus", "engine.EventListener", "engine.GameEvent",
        "engine.AttackStarted", "engine.DamageDealt", "engine.EntityDied", "engine.ItemDropped",
        "engine.ItemPickedUp", "engine.ShopOpened", "engine.FixedTimestep", "engine.StateBuffer",
        "engine.Rewindable", "GameConstants",
    ]),
    ("02-entities", "Entities: what lives in the world", [
        "entity.Entity", "entity.Updatable", "entity.Collidable", "entity.Interactable",
        "entity.Aabb", "entity.Wall", "entity.ItemDrop", "entity.ItemDropper", "entity.LootDrops",
        "entity.Projectile", "entity.Arrow", "entity.Fireball",
    ]),
    ("03-characters", "Characters: players, enemies, stats and builds", [
        "entity.Character", "entity.Player", "entity.Knight", "entity.Assassin", "entity.Enemy",
        "entity.MeleeEnemy", "entity.Summoner", "entity.Rat", "entity.Zombie", "entity.RatNest",
        "entity.EnemyState", "entity.Evade", "entity.Stat", "entity.StatSource",
        "entity.BaseStats", "entity.ReloadableStats", "entity.CharacterBuild",
        "entity.BuildModifier",
    ]),
    ("04-character-data", "Character data: characters.json to characters", [
        "entity.CharacterFactory", "entity.CharacterKind", "entity.PlayerKind",
        "entity.EnemyKind", "entity.CharacterSpec", "entity.CharacterSpecReader",
        "entity.PlayerFactory", "entity.EnemyFactory", "entity.EnemySetup", "data.DataRegistry",
        "data.Reloadable",
    ]),
    ("05-combat", "Combat: attacks, hitboxes, damage and status effects", [
        "combat.Attack", "combat.AttackDecorator", "combat.FireTouch", "combat.LifeSteal",
        "combat.Execute", "combat.ContactAttack", "combat.FrontContactAttack",
        "combat.AroundContactAttack", "combat.Damageable", "combat.DamageFormula",
        "combat.HitResult", "combat.Hitbox", "combat.Obb", "combat.Swing", "combat.Faction",
        "combat.StatusEffect", "combat.DamageOverTime", "combat.Burn", "combat.Poison",
        "combat.DotSpec",
    ]),
    ("06-items", "Items: weapons, armor, consumables and passive items", [
        "item.Item", "item.Equipment", "item.Weapon", "item.MeleeWeapon", "item.Sword",
        "item.Axe", "item.RangedWeapon", "item.Bow", "item.ShortBow", "item.Staff",
        "item.FireStaff", "item.Armor", "item.LeatherArmor", "item.Shield", "item.Consumable",
        "item.Potion", "item.PassiveItem", "item.FireRing", "item.StatRing",
        "item.LifeStealRing", "item.ExecutionerRing", "item.Rarity", "item.ItemEffect",
    ]),
    ("07-item-data", "Item data: inventory, items.json and loot", [
        "item.Inventory", "item.ItemFactory", "item.ItemSpecReader", "item.LootTable",
        "item.ItemSpec", "item.ItemSpec$PotionSpec", "item.ItemSpec$SwordSpec",
        "item.ItemSpec$AxeSpec", "item.ItemSpec$LeatherArmorSpec", "item.ItemSpec$ShieldSpec",
        "item.ItemSpec$FireRingSpec", "item.ItemSpec$StatRingSpec",
        "item.ItemSpec$LifeStealRingSpec", "item.ItemSpec$ExecutionerRingSpec",
        "item.ItemSpec$ShortBowSpec", "item.ItemSpec$FireStaffSpec",
    ]),
    ("08-arena-shop-input", "Arena, shop, input and randomness", [
        "arena.ArenaCycle", "arena.WaveDirector", "arena.WavePlan", "arena.EnemySpawner",
        "shop.Shopkeeper", "input.PlayerInput", "input.PlayerOrders", "random.RandomSource",
        "random.SeededRandomSource",
    ]),
    ("09-debug", "Debug features: F1 views, F2 event laser, F5 hot reload, R rewind", [
        "debug.DebugFeature", "debug.ViewSwitcher", "debug.ViewMode", "debug.EventLaser",
        "debug.EventLaser$Line", "debug.HotReload", "debug.Rewind",
        "desktop.ui.XRayRenderer", "desktop.ui.HighContrastRenderer",
        "desktop.ui.EventLaserOverlay",
    ]),
    ("10-desktop-screens", "Desktop: application, screens and drawing", [
        "desktop.DesktopLauncher", "desktop.StartupHelper", "desktop.ui.GameApplication",
        "desktop.ui.MenuScreen", "desktop.ui.MenuFlowState", "desktop.ui.GameScreen",
        "desktop.ui.Renderer", "desktop.ui.SpriteRenderer", "desktop.ui.Frame",
        "desktop.ui.PlayerView", "desktop.ui.EnemyView", "desktop.ui.ItemView",
        "desktop.ui.ProjectileView", "desktop.ui.HitPreview", "desktop.ui.HitPreview$Fan",
        "desktop.ui.HitPreview$Ray", "desktop.ui.HitPreviewView", "desktop.ui.SpriteTextures",
        "desktop.ui.KeyboardPlayerInput",
    ]),
    ("11-desktop-hud", "Desktop: HUD, inventory page and shop page", [
        "desktop.ui.Hud", "desktop.ui.HudState", "desktop.ui.InventoryPage",
        "desktop.ui.InventoryPageState", "desktop.ui.PageSlot", "desktop.ui.PassiveSwap",
        "desktop.ui.ItemEffectText", "desktop.ui.ShopPage", "desktop.ui.ShopPageState",
        "desktop.ui.UiSkinFactory",
    ]),
    ("12-desktop-audio", "Desktop: sound effects and music", [
        "desktop.audio.GameAudio", "desktop.audio.SoundEffect", "desktop.audio.SoundEvents",
        "desktop.audio.SoundOverlapGuard", "desktop.audio.MusicTrack", "desktop.audio.MusicMix",
        "desktop.audio.MusicPlayer",
    ]),
]

CHAR_WIDTH = 6.7  # Courier New 11 px
LINE_HEIGHT = 15
HEADER_HEIGHT = 36
MAX_COLUMNS = 6  # a row wraps after this many boxes
MARGIN = 40  # around the whole grid
CORRIDOR = 36  # smallest corridor between two rows or two columns, in pixels
LANE = 10  # space between two lanes of a corridor
LABEL_ROOM = 16  # extra room in a row corridor for the field names written on arrows


# ---------- reading the classes ----------

def javap(names):
    classpath = ":".join(str(d) for d in CLASS_DIRS)
    result = subprocess.run(
        ["javap", "-p", "-cp", classpath, *names], capture_output=True, text=True, check=True)
    return result.stdout


def short(type_name):
    """com.epitech.depthdiablo.item.ItemSpec$SwordSpec -> ItemSpec.SwordSpec; java.util.List -> List."""
    return re.sub(r"\b(?:[a-z_][\w]*\.)+([A-Z][\w$]*)", r"\1", type_name).replace("$", ".")


def split_params(params):
    depth, current, out = 0, "", []
    for ch in params:
        if ch == "<":
            depth += 1
        elif ch == ">":
            depth -= 1
        if ch == "," and depth == 0:
            out.append(current.strip())
            current = ""
        else:
            current += ch
    if current.strip():
        out.append(current.strip())
    return out


class JavaType:
    def __init__(self, fqcn):
        self.fqcn = fqcn
        self.name = short(fqcn)
        self.kind = "class"
        self.abstract = False
        self.parent = None
        self.interfaces = []
        self.constants = []
        self.fields = []   # (visibility, static, name, type)
        self.methods = []  # (visibility, static, abstract, name, params, return)


MODIFIERS = {"public", "protected", "private", "static", "final", "abstract", "default",
             "synchronized", "native", "transient", "volatile", "strictfp", "sealed", "non-sealed"}


def parse(text):
    types = {}
    current = None
    for line in text.splitlines():
        line = line.rstrip()
        header = re.match(r"^(.*?)\b(class|interface) ([\w.$]+)(?: extends ([^{]+?))?(?: implements ([^{]+?))?(?: permits [^{]+?)? ?\{$", line)
        if header and not line.startswith("  "):
            mods, kind, fqcn, extends, implements = header.groups()
            current = JavaType(fqcn)
            current.abstract = "abstract" in mods.split() and kind == "class"
            current.kind = kind
            if kind == "interface":
                current.interfaces = [i.strip() for i in split_params(extends or "")]
            else:
                parent = (extends or "").strip()
                if parent.startswith("java.lang.Enum"):
                    current.kind = "enum"
                elif parent == "java.lang.Record":
                    current.kind = "record"
                elif parent and parent != "java.lang.Object":
                    current.parent = re.sub(r"<.*", "", parent)
                current.interfaces = [re.sub(r"<.*", "", i.strip())
                                      for i in split_params(implements or "")]
            types[fqcn] = current
            continue
        if current is None or not line.startswith("  ") or line.strip() in ("static {};", "}"):
            continue
        member = line.strip().rstrip(";")
        if "$" in member.split("(")[0].split()[-1] if "(" in member else "$" in member.split()[-1]:
            continue  # synthetic: $VALUES, lambda$..., access$...
        words = member.split("(")[0].split()
        mods = [w for w in words if w in MODIFIERS]
        visibility = next((v for v in ("public", "protected", "private") if v in mods), "package")
        static = "static" in mods
        if "(" in member:
            before, params = member.split("(", 1)
            params = params.rsplit(")", 1)[0]
            parts = [w for w in before.split() if w not in MODIFIERS]
            name = parts[-1]
            ret = " ".join(parts[:-1]) if len(parts) > 1 else ""
            if name == current.fqcn:  # constructor
                if current.kind in ("enum", "record"):
                    continue
                name, ret = current.name.split(".")[-1], None
            if current.kind == "enum" and name in ("values", "valueOf"):
                continue
            if current.kind == "record" and name in ("toString", "hashCode", "equals"):
                continue
            if any(m[3] == name and m[4] == params for m in current.methods):
                continue  # bridge method: same name and parameters, wider return type
            current.methods.append((visibility, static, "abstract" in mods, name,
                                    [short(p) for p in split_params(params)],
                                    short(ret) if ret is not None else None))
        else:
            parts = [w for w in member.split() if w not in MODIFIERS]
            name, ftype = parts[-1], " ".join(parts[:-1])
            if current.kind == "enum" and static and ftype == current.fqcn:
                current.constants.append(name)
            else:
                current.fields.append((visibility, static, name, short(ftype)))
    for t in types.values():
        if t.kind == "record":  # accessors repeat the components
            components = {f[2] for f in t.fields}
            t.methods = [m for m in t.methods if not (m[3] in components and not m[4])]
    return types


# ---------- drawing ----------

VISIBILITY = {"public": "+", "protected": "#", "private": "-", "package": "~"}


def esc(text):
    return html.escape(text, quote=False)


def box_lines(t):
    lines = []
    if t.constants:
        lines.append([esc(c) for c in t.constants])
    fields = []
    for vis, static, name, ftype in t.fields:
        text = esc(f"{VISIBILITY[vis]} {name}: {ftype}")
        fields.append(f"<u>{text}</u>" if static else text)
    methods = []
    for vis, static, abstract, name, params, ret in t.methods:
        signature = f"{VISIBILITY[vis]} {name}({', '.join(params)})" + (f": {ret}" if ret else "")
        text = esc(signature)
        if static:
            text = f"<u>{text}</u>"
        if abstract:
            text = f"<i>{text}</i>"
        methods.append(text)
    if t.kind != "enum" or fields:
        lines.append(fields)
    lines.append(methods)
    return lines


def stereotype(t):
    return {"interface": "«interface»", "enum": "«enum»", "record": "«record»"}.get(
        t.kind, "«abstract»" if t.abstract else "")


def box(t):
    sections = box_lines(t)
    name = esc(t.name)
    title = f"<i>{name}</i>" if t.abstract or t.kind == "interface" else name
    head = f"<div style='text-align:center'>{stereotype(t)}{'<br>' if stereotype(t) else ''}<b>{title}</b></div>"
    body = "<hr size='1'>".join("<br>".join(s) if s else "&nbsp;" for s in sections)
    plain = [re.sub(r"<[^>]+>", "", html.unescape(x)) for s in sections for x in s]
    width = max([len(t.name) + 4, len(stereotype(t))] + [len(p) for p in plain]) * CHAR_WIDTH + 24
    count = sum(max(1, len(s)) for s in sections)
    height = HEADER_HEIGHT + count * LINE_HEIGHT + len(sections) * 8
    return f"{head}<hr size='1'>{body}", max(140, width), height


def depth_of(node, parents_of, cache):
    if node not in cache:
        cache[node] = 0  # guards against a cycle
        cache[node] = 1 + max((depth_of(p, parents_of, cache) for p in parents_of[node]),
                              default=-1)
    return cache[node]


def xml_attr(text):
    return (text.replace("&", "&amp;").replace('"', "&quot;").replace("<", "&lt;")
            .replace(">", "&gt;"))


def grid_rows(levels, parents_of):
    """Turns depth levels into rows of at most MAX_COLUMNS boxes, each child near its parents."""
    rows, column_of = [], {}
    for level in sorted(levels):
        def key(node):
            placed = [column_of[p] for p in parents_of[node] if p in column_of]
            return (sum(placed) / len(placed) if placed else 1e9, node)
        ordered = sorted(levels[level], key=key)
        for i in range(0, len(ordered), MAX_COLUMNS):
            row = ordered[i:i + MAX_COLUMNS]
            for c, node in enumerate(row):
                column_of[node] = c
            rows.append(row)
    return rows


def route(nodes, rows, edges):
    """Places the boxes on a grid and routes every edge through the empty corridors.

    nodes: id -> (value, width, height, style); rows: lists of ids; edges: (source, target,
    kind, label), where kind picks the arrow style and the port. Row corridor h lies above row h
    (h = len(rows) is below the last row); column corridor v lies left of column v. An edge only
    runs inside corridors, plus the straight piece between a box side and its corridor, so it
    never crosses a box. Each corridor has one lane per target: edges to the same box share it.
    Returns the mxCell strings of the boxes and of the edges.
    """
    cell = {n: (r, c) for r, row in enumerate(rows) for c, n in enumerate(row)}
    columns = max(len(row) for row in rows)
    column_width = [max((nodes[row[c]][1] for row in rows if c < len(row)), default=0)
                    for c in range(columns)]
    row_height = [max(nodes[n][2] for n in row) for row in rows]

    plans = []
    h_lanes = defaultdict(dict)  # row corridor -> {lane key: lane number}
    v_lanes = defaultdict(dict)  # column corridor -> {lane key: lane number}
    for source, target, kind, label in edges:
        (ra, ca), (rb, cb) = cell[source], cell[target]
        if rb < ra:
            ha, hb, exit_y, entry_y = ra, rb + 1, 0, 1
        elif rb > ra:
            ha, hb, exit_y, entry_y = ra + 1, rb, 1, 0
        else:
            ha, hb, exit_y, entry_y = ra, ra, 0, 0
        lane_key = (target, kind)
        h_lanes[ha].setdefault(lane_key, len(h_lanes[ha]))
        v = None
        if ha != hb:
            h_lanes[hb].setdefault(lane_key, len(h_lanes[hb]))
            # the column corridor that keeps the way across shortest
            v = min(range(columns + 1), key=lambda j: abs(j - ca - 0.5) + abs(j - cb - 0.5))
            v_lanes[v].setdefault(lane_key, len(v_lanes[v]))
        plans.append((source, target, kind, label, ha, hb, v, exit_y, entry_y, lane_key))

    def corridor_size(lanes, extra=0):
        return max(CORRIDOR, 2 * LANE + len(lanes) * LANE + extra)

    labelled = {plan[4] for plan in plans if plan[3]}
    h_size = [corridor_size(h_lanes[h], LABEL_ROOM if h in labelled else 0)
              for h in range(len(rows) + 1)]
    v_size = [corridor_size(v_lanes[v]) for v in range(columns + 1)]
    v_left, column_left = [], []
    x = MARGIN
    for c in range(columns):
        v_left.append(x)
        x += v_size[c]
        column_left.append(x)
        x += column_width[c]
    v_left.append(x)
    h_top, row_top = [], []
    y = MARGIN + 40  # room for the title and the legend
    for r in range(len(rows)):
        h_top.append(y)
        y += h_size[r]
        row_top.append(y)
        y += row_height[r]
    h_top.append(y)

    box_at, cells = {}, []
    for n, (r, c) in cell.items():
        value, width, height, style = nodes[n]
        bx = column_left[c] + (column_width[c] - width) / 2
        by = row_top[r]
        box_at[n] = (bx, by, width, height)
        cells.append(f'<mxCell id="{n}" value="{xml_attr(value)}" style="{style}" vertex="1" '
                     f'parent="1"><mxGeometry x="{bx:.0f}" y="{by:.0f}" width="{width:.0f}" '
                     f'height="{height:.0f}" as="geometry"/></mxCell>')

    def h_lane_y(h, key):
        return h_top[h] + LANE + h_lanes[h][key] * LANE

    def v_lane_x(v, key):
        return v_left[v] + LANE + v_lanes[v][key] * LANE

    styles = {
        "extends": "endArrow=block;endFill=0;endSize=12;",
        "implements": "endArrow=block;endFill=0;endSize=12;dashed=1;",
        "field": "endArrow=open;endFill=0;strokeColor=#6c8ebf;fontColor=#6c8ebf;fontSize=10;"
                 "labelBackgroundColor=#ffffff;",
        "uses": "endArrow=open;endFill=0;dashed=1;strokeColor=#999999;",
    }
    for i, (source, target, kind, label, ha, hb, v, exit_y, entry_y, key) in enumerate(plans):
        exit_x = 0.75 if kind == "field" else 0.5
        entry_x = 0.25 if kind == "field" else 0.5
        sx, sy, sw, sh = box_at[source]
        tx, ty, tw, th = box_at[target]
        start_x, end_x = sx + sw * exit_x, tx + tw * entry_x
        ya = h_lane_y(ha, key)
        points = [(start_x, ya)]
        if v is not None:
            vx = v_lane_x(v, key)
            yb = h_lane_y(hb, key)
            points += [(vx, ya), (vx, yb), (end_x, yb)]
        else:
            points.append((end_x, ya))
        array = "".join(f'<mxPoint x="{px:.0f}" y="{py:.0f}"/>' for px, py in points)
        style = (styles[kind] + "html=1;rounded=0;edgeStyle=none;"
                 f"exitX={exit_x};exitY={exit_y};exitDx=0;exitDy=0;exitPerimeter=0;"
                 f"entryX={entry_x};entryY={entry_y};entryDx=0;entryDy=0;entryPerimeter=0;")
        label_attr = f' value="{xml_attr(esc(label))}"' if label else ""
        label_geometry = ' x="-0.7" y="0"' if label else ""
        cells.append(f'<mxCell id="e{i}"{label_attr} style="{style}" edge="1" parent="1" '
                     f'source="{source}" target="{target}"><mxGeometry relative="1"'
                     f'{label_geometry} as="geometry"><Array as="points">{array}</Array>'
                     '</mxGeometry></mxCell>')
    return cells


def header_cells(title, legend):
    return [f'<mxCell id="title" value="{xml_attr(esc(title))}" style="text;html=1;fontSize=18;'
            'fontStyle=1;align=left;" vertex="1" parent="1"><mxGeometry x="40" y="10" '
            'width="1400" height="30" as="geometry"/></mxCell>',
            f'<mxCell id="legend" value="{xml_attr(esc(legend))}" style="text;html=1;'
            'fontSize=11;fontColor=#666666;align=left;" vertex="1" parent="1"><mxGeometry x="40" '
            'y="42" width="1600" height="20" as="geometry"/></mxCell>']


BOX_STYLE = ("rounded=0;whiteSpace=wrap;html=1;align=left;verticalAlign=top;spacingLeft=6;"
             "spacingTop=2;fontFamily=Courier New;fontSize=11;fillColor=#ffffff;"
             "strokeColor=#333333;")
STUB_STYLE = ("rounded=0;whiteSpace=wrap;html=1;fillColor=#eeeeee;strokeColor=#999999;"
              "fontColor=#666666;fontFamily=Courier New;fontSize=11;dashed=1;")
LEGEND = ("Generated by tools/uml/generate_uml.py from the compiled classes. Hollow arrow: extends "
          "(dashed: implements). Blue open arrow: a field of that type, named on the arrow. Grey "
          "dashed boxes are drawn in another diagram.")


def diagram(file_name, title, members, types):
    full = [BASE + "." + m for m in members]
    missing = [m for m in full if m not in types]
    if missing:
        sys.exit(f"{file_name}: not compiled or unknown: {missing}")
    ids = {}

    def node_id(fqcn):
        return ids.setdefault(fqcn, f"c{len(ids)}")

    nodes, edges = {}, []
    parents_of = defaultdict(list)
    for fqcn in full:
        t = types[fqcn]
        value, width, height = box(t)
        nodes[node_id(fqcn)] = (value, width, height, BOX_STYLE)
    for fqcn in full:
        t = types[fqcn]
        for parent, kind in ([(t.parent, "extends")] if t.parent else []) + [
                (i, "implements") for i in t.interfaces]:
            if not parent.startswith(BASE):
                continue
            if node_id(parent) not in nodes:
                name = short(parent)
                nodes[node_id(parent)] = (esc(name), len(name) * CHAR_WIDTH + 30, 30, STUB_STYLE)
            parents_of[node_id(fqcn)].append(node_id(parent))
            edges.append((node_id(fqcn), node_id(parent), kind, ""))
    for fqcn in full:
        by_target = defaultdict(list)
        for _, _, name, ftype in types[fqcn].fields:
            for other in full:
                if other != fqcn and re.search(r"\b" + re.escape(short(other)) + r"\b", ftype):
                    by_target[other].append(name)
        for other, names in by_target.items():
            edges.append((node_id(fqcn), node_id(other), "field", ", ".join(names)))
    cache = {}
    levels = defaultdict(list)
    for n in nodes:
        levels[depth_of(n, parents_of, cache)].append(n)
    rows = grid_rows(levels, parents_of)
    write(file_name, title, header_cells(title, LEGEND) + route(nodes, rows, edges))
    stubs = sum(1 for n in nodes.values() if n[3] == STUB_STYLE)
    return len(full), stubs


def write(file_name, title, cells):
    body = "".join(cells)
    xml = ('<mxfile host="app.diagrams.net" agent="tools/uml/generate_uml.py">'
           f'<diagram id="{file_name}" name="{xml_attr(title)}"><mxGraphModel dx="1600" dy="900" '
           'grid="1" gridSize="10" guides="1" tooltips="1" connect="1" arrows="1" fold="1" '
           'page="0" pageScale="1" math="0" shadow="0"><root><mxCell id="0"/>'
           f'<mxCell id="1" parent="0"/>{body}</root></mxGraphModel></diagram></mxfile>\n')
    (OUT_DIR / f"{file_name}.drawio").write_text(xml, encoding="utf-8")


def overview(types):
    """One box per package with its classes, and an arrow for each package it uses (jdeps)."""
    result = subprocess.run(["jdeps", "-verbose:package", "-filter:none",
                             *[str(d) for d in CLASS_DIRS]], capture_output=True, text=True,
                            check=True)
    deps = set()
    for line in result.stdout.splitlines():
        match = re.match(r"^\s+(com\.epitech\.depthdiablo[\w.]*)\s+->\s+"
                         r"(com\.epitech\.depthdiablo[\w.]*)", line)
        if match and match.group(1) != match.group(2):
            deps.add(match.groups())
    packages = defaultdict(list)
    for fqcn, t in types.items():
        if "$" not in fqcn:
            packages[fqcn.rsplit(".", 1)[0]].append(t.name)
    nodes, ids = {}, {}
    for package in sorted(packages):
        names = sorted(packages[package])
        lines = []
        for name in names:  # wraps the class names at about 36 characters
            if lines and len(lines[-1]) + len(name) + 2 <= 36:
                lines[-1] += ", " + name
            else:
                lines.append(name)
        label = (f"<b>{esc(package.replace(BASE, '').lstrip('.') or '(root)')}</b> "
                 f"({len(names)})<hr size='1'>" + "<br>".join(esc(x) for x in lines))
        ids[package] = f"p{len(ids)}"
        nodes[ids[package]] = (label, 290, 40 + len(lines) * 15,
                               "rounded=1;whiteSpace=wrap;html=1;align=left;verticalAlign=top;"
                               "spacingLeft=6;fontSize=11;fillColor=#dae8fc;strokeColor=#6c8ebf;")
    order = sorted(ids.values(), key=lambda n: int(n[1:]))
    rows = [order[i:i + 5] for i in range(0, len(order), 5)]
    edges = [(ids[a], ids[b], "uses", "") for a, b in sorted(deps) if a in ids and b in ids]
    title = "Overview: packages, their classes, and which package uses which"
    legend = ("Generated by tools/uml/generate_uml.py (jdeps). Grey dashed arrow: the package "
              "uses the one it points to.")
    write("00-overview", title, header_cells(title, legend) + route(nodes, rows, edges))
    return len(packages), len(edges)


def main():
    for directory in CLASS_DIRS:
        if not directory.exists():
            sys.exit(f"{directory} is missing: run ./gradlew classes first")
    names = []
    for directory in CLASS_DIRS:
        for path in directory.rglob("*.class"):
            fqcn = ".".join(path.relative_to(directory).with_suffix("").parts)
            if re.search(r"\$\d|\$\$|package-info", fqcn):
                continue  # anonymous classes, lambdas
            names.append(fqcn)
    types = parse(javap(sorted(names)))
    OUT_DIR.mkdir(parents=True, exist_ok=True)
    packages, deps = overview(types)
    print(f"00-overview: {packages} packages, {deps} dependencies")
    drawn = set()
    for file_name, title, members in DIAGRAMS:
        count, stubs = diagram(file_name, title, members, types)
        drawn.update(BASE + "." + m for m in members)
        print(f"{file_name}: {count} classes, {stubs} from other diagrams")
    left_out = sorted(short(f) for f in types if f not in drawn and "$" not in f)
    if left_out:
        print("Not in any diagram:", ", ".join(left_out))


if __name__ == "__main__":
    main()
