import json, os, re, hashlib, subprocess, sys, collections, datetime

ROOT = r"C:\Users\sodeshpa\.copilot\repos\copilot-worktrees\crh-logon\sodeshpa-improved-telegram"
APP = "application:sodeshpa-improved-telegram"
WORK = os.path.join(ROOT, ".solutiondocs", ".docgen", "fs-work")
REV = subprocess.check_output(["git", "-C", ROOT, "rev-parse", "HEAD"], text=True).strip()
NOW = datetime.datetime.now(datetime.timezone.utc).strftime("%Y-%m-%dT%H:%M:%SZ")
CODE_LANGS = {"Java", "JavaScript/TypeScript", "Python", "Ruby", "Script", "Web/Template", "SQL"}
LAYERS = {"controllers", "controller", "services", "service", "repositories", "repository", "dao", "models", "model",
          "views", "view", "utils", "util", "middleware", "handlers", "handler", "routes", "domain", "infrastructure",
          "application", "entities", "entity", "dto", "config", "filters", "listeners", "interceptors", "validators"}


def h(s):
    return hashlib.sha256(s.encode()).hexdigest()[:24]


def slug(s):
    return re.sub(r"[^a-z0-9._-]+", "-", s.lower()).strip("-")


man = json.load(open(os.path.join(ROOT, ".solutiondocs", ".docgen", "file-manifest.json"), encoding="utf-8"))["files"]
reg = json.load(open(os.path.join(ROOT, ".solutiondocs", ".docgen", "component-registry.json"), encoding="utf-8"))["components"]
roots = sorted([(c["source_root"].replace("/", "\\"), c["id"]) for c in reg], key=lambda x: -len(x[0]))


def owner(path):
    p = path.replace("/", "\\")
    for r, cid in roots:
        if p == r or p.startswith(r + "\\"):
            return cid
    return APP


def loc_of(path):
    with open(os.path.join(ROOT, path), "rb") as f:
        b = f.read()
    return b.count(b"\n") + (1 if b and not b.endswith(b"\n") else 0)


stats = {}
units = []
naming = collections.defaultdict(lambda: collections.Counter())
layers = collections.defaultdict(collections.Counter)
large = []
for f in man:
    if f["status"] == "skipped":
        continue
    o = owner(f["path"])
    f["owner"] = o
    try:
        f["loc"] = loc_of(f["path"])
    except OSError:
        f["loc"] = 0
    s = stats.setdefault(o, {"files": 0, "loc": 0, "lang": collections.Counter(), "langfiles": collections.Counter()})
    s["files"] += 1
    s["loc"] += f["loc"]
    s["lang"][f["language"]] += f["loc"]
    s["langfiles"][f["language"]] += 1
    if f["estimated_tokens"] > 50000:
        large.append((f["path"], f["estimated_tokens"]))
    name = os.path.basename(f["path"])
    if re.search(r"(Test|Tests|IT)\.java$|\.spec\.(ts|js)$|^test_.*\.py$|_test\.(py|go)$", name):
        naming[o]["test_files"] += 1
    if re.match(r"(Main|Application|App)\.(java|py|ts|js)$|^(main|index)\.(py|ts|js|go)$|^Program\.cs$", name):
        naming[o]["entry_points"] += 1
    if re.match(r"[A-Z][A-Za-z0-9]*\.", name):
        naming[o]["PascalCase"] += 1
    elif re.match(r"[a-z]+[A-Z][A-Za-z0-9]*\.", name):
        naming[o]["camelCase"] += 1
    elif "_" in name.split(".")[0]:
        naming[o]["snake_case"] += 1
    elif "-" in name.split(".")[0]:
        naming[o]["kebab-case"] += 1
    for d in f["path"].replace("/", "\\").split("\\")[:-1]:
        if d.lower() in LAYERS:
            layers[o][d.lower()] += 1
    if f["language"] in CODE_LANGS:
        units.append(f)

used = {}


def unit_id(path):
    base = "code_unit:" + slug(path)
    if base in used and used[base] != path:
        base += "-" + h(path)[:8]
    used[base] = path
    return base


def node(id_, type_, name, props, qn=None):
    r = {"id": id_, "record_kind": "node", "type": type_, "name": name, "application_id": APP, "properties": props, "status": "active"}
    if qn:
        r["qualified_name"] = qn
    return r


def claim(id_, subj, pred, value, ev, conf=0.95, deriv="observation", scope=None):
    r = {"id": id_, "record_kind": "claim", "application_id": APP, "subject": subj, "predicate": pred, "value": value,
         "derivation": deriv, "status": "candidate", "confidence": conf, "evidence_ids": [ev],
         "applicability": {"source_revision": REV}}
    if scope:
        r["scope_id"] = scope
    return r


def evidence(id_, src, locator, method="file-system"):
    return {"id": id_, "record_kind": "evidence", "application_id": APP, "source_id": src, "source_revision": REV,
            "method": method, "locator": locator, "observed_at": NOW, "status": "active"}


def src_id(o):
    return "source_artifact:fs-" + slug(o.split(":", 1)[1]) + "-tree" if o != APP else "source_artifact:fs-application-tree"


ops_sources = []
for o in list(stats):
    nm = o.split(":", 1)[1] if o != APP else "application root"
    loc = ROOT if o == APP else os.path.join(ROOT, next(r for r, c in roots if c == o))
    ops_sources.append({"record": node(src_id(o), "source_artifact", "File tree: " + nm,
                                       {"source_type": "directory_tree", "location": loc, "revision": REV}), "expected_revision": 0})
json.dump(ops_sources, open(os.path.join(WORK, "batch-000-sources.json"), "w"))

CH = 120
for i in range(0, len(units), CH):
    ops = []
    for f in units[i:i + CH]:
        uid = unit_id(f["path"])
        o = f["owner"]
        base = h(f["path"])
        ev = "evidence:fs-" + base
        cid = "claim:fs-" + base
        rcl = "claim:fs-rel-" + base
        rid = "relationship:fs-contains-" + base
        props = {"unit_type": "file", "language": f["language"], "file_path": f["path"], "loc": f["loc"],
                 "metrics": {"loc": f["loc"]}}
        ops.append({"record": evidence(ev, src_id(o), {"file": f["path"], "line_start": 1, "line_end": max(f["loc"], 1)}), "expected_revision": 0})
        ops.append({"record": node(uid, "code_unit", os.path.basename(f["path"]), props, qn=f["path"].replace("\\", "/")), "expected_revision": 0})
        ops.append({"record": claim(cid, uid, "file_inventory", {"file_path": f["path"], "language": f["language"], "loc": f["loc"],
                                                                 "size_bytes": f["size_bytes"], "estimated_tokens": f["estimated_tokens"]}, ev, scope=o), "expected_revision": 0})
        ops.append({"record": claim(rcl, rid, "contains", {"container": o, "file_path": f["path"]}, ev, scope=o), "expected_revision": 0})
        ops.append({"record": {"id": rid, "record_kind": "relationship", "type": "contains", "application_id": APP, "from": o, "to": uid,
                               "properties": {}, "claim_ids": [rcl], "status": "active"}, "expected_revision": 0})
    json.dump(ops, open(os.path.join(WORK, "batch-%03d.json" % (i // CH + 1)), "w"))

summary = {}
for o, s in stats.items():
    tot = max(s["loc"], 1)
    langs = [{"name": l, "percentage": round(100.0 * n / tot, 2)} for l, n in s["lang"].most_common()]
    summary[o] = {"files": s["files"], "loc": s["loc"], "languages": langs, "langfiles": dict(s["langfiles"]),
                  "layers": dict(layers[o]), "naming": dict(naming[o])}
summary["_large"] = sorted(large, key=lambda x: -x[1])
summary["_units"] = len(units)
summary["_rev"] = REV
json.dump(summary, open(os.path.join(WORK, "summary.json"), "w"), indent=1)
print(len(units), "units;", (len(units) + CH - 1) // CH, "batches;", len(stats), "owners")

