import json, os, subprocess, time, re, datetime
ROOT = r"C:\Users\sodeshpa\.copilot\repos\copilot-worktrees\crh-logon\sodeshpa-improved-telegram"
PLUGIN = r"C:\Users\sodeshpa\.copilot\installed-plugins\cca-appmod-gtm\swm-understanding"
KW = PLUGIN + r"\skills\knowledge-writer\scripts\kw.py"
KQ = PLUGIN + r"\skills\knowledge-query\scripts\kq.py"
KB = os.path.join(ROOT, ".solutiondocs", "knowledge")
WORK = os.path.join(ROOT, ".solutiondocs", ".docgen", "fs-work")
APP = "application:sodeshpa-improved-telegram"
S = json.load(open(os.path.join(WORK, "summary.json")))
REV = S["_rev"]
NOW = datetime.datetime.now(datetime.timezone.utc).strftime("%Y-%m-%dT%H:%M:%SZ")
CODE = {"Java", "JavaScript/TypeScript", "Python", "Ruby", "Script", "Web/Template", "SQL"}


def run(script, *a):
    p = subprocess.run(["python", script, "--kb", KB, *a], capture_output=True, text=True, encoding="utf-8")
    return p.returncode, p.stdout, p.stderr


def src_id(o):
    if o == APP:
        return "source_artifact:fs-application-tree"
    return "source_artifact:fs-" + re.sub(r"[^a-z0-9._-]+", "-", o.split(":", 1)[1].lower()).strip("-") + "-tree"


def call_locked(script, *a):
    for _ in range(40):
        rc, out, err = run(script, *a)
        if "lock" in err + out and rc != 0:
            time.sleep(5)
            continue
        return rc, out, err
    return 1, "", "lock timeout"


def enrich(subj, props_update, claims):
    tag = re.sub(r"[^a-z0-9]+", "-", subj.lower())
    e1 = "evidence:fs-summary-" + tag
    for attempt in range(5):
        rc, out, err = run(KQ, "get", subj)
        cur = json.loads(out)
        cur["properties"] = {**cur.get("properties", {}), **props_update(cur.get("properties", {}))}
        ops = []
        rc2, _, _ = call_locked(KQ, "get", e1)
        if rc2 != 0:
            ops.append({"record": {"id": e1, "record_kind": "evidence", "application_id": APP, "source_id": src_id(subj),
                                   "source_revision": REV, "method": "file-system",
                                   "locator": {"file": ".solutiondocs\\.docgen\\file-manifest.json", "section": subj},
                                   "observed_at": NOW, "status": "active"}, "expected_revision": 0})
        for pred, val, conf, d in claims:
            cid = "claim:fs-" + tag + "-" + re.sub(r"[^a-z0-9]+", "-", pred)
            if call_locked(KQ, "get", cid)[0] == 0:
                continue
            c = {"id": cid, "record_kind": "claim", "application_id": APP, "subject": subj, "predicate": pred, "value": val,
                 "derivation": d, "status": "candidate", "confidence": conf, "evidence_ids": [e1],
                 "applicability": {"source_revision": REV}}
            if subj != APP:
                c["scope_id"] = subj
            ops.append({"record": c, "expected_revision": 0})
        ops.append({"record": cur, "expected_revision": cur["revision"]})
        p = os.path.join(WORK, "enrich.json")
        json.dump(ops, open(p, "w"))
        rc, out, err = call_locked(KW, "batch", "--operations-file", p)
        if rc == 0 and '"committed"' in out:
            return "ok"
        if "revision" in err.lower():
            continue
        return "FAIL " + err[:500]
    return "FAIL revision conflicts"


def build(subj, pu, claims, cur, have):
    tag = re.sub(r"[^a-z0-9]+", "-", subj.lower())
    e1 = "evidence:fs-summary-" + tag
    cur["properties"] = {**cur.get("properties", {}), **pu(cur.get("properties", {}))}
    ops = []
    if e1 not in have:
        ops.append({"record": {"id": e1, "record_kind": "evidence", "application_id": APP, "source_id": src_id(subj),
                               "source_revision": REV, "method": "file-system",
                               "locator": {"file": ".solutiondocs\\.docgen\\file-manifest.json", "section": subj},
                               "observed_at": NOW, "status": "active"}, "expected_revision": 0})
    for pred, val, conf, d in claims:
        cid = "claim:fs-" + tag + "-" + re.sub(r"[^a-z0-9]+", "-", pred)
        if cid in have:
            continue
        c = {"id": cid, "record_kind": "claim", "application_id": APP, "subject": subj, "predicate": pred, "value": val,
             "derivation": d, "status": "candidate", "confidence": conf, "evidence_ids": [e1],
             "applicability": {"source_revision": REV}}
        if subj != APP:
            c["scope_id"] = subj
        ops.append({"record": c, "expected_revision": 0})
    ops.append({"record": cur, "expected_revision": cur["revision"]})
    return ops


comp = {k: v for k, v in S.items() if not k.startswith("_")}
lang_loc, lang_files, tot_loc = {}, {}, 0
for o, s in comp.items():
    tot_loc += s["loc"]
    for l in s["languages"]:
        lang_loc[l["name"]] = lang_loc.get(l["name"], 0) + round(l["percentage"] * s["loc"] / 100)
    for l, n in s["langfiles"].items():
        lang_files[l] = lang_files.get(l, 0) + n
primary = max(lang_loc, key=lang_loc.get)
rows = sorted([{"language": l, "files": lang_files.get(l, 0), "total_lines": lang_loc.get(l, 0)} for l in lang_loc], key=lambda r: -r["total_lines"])
sids = sorted({src_id(o) for o in comp})

for attempt in range(8):
    rc, out, err = call_locked(KQ, "find", "component")
    comps = {c["id"]: c for c in json.loads(out)}
    rc, out, err = call_locked(KQ, "get", APP)
    app = json.loads(out)
    rc, out, err = call_locked(KQ, "find", "evidence")
    have = {e["id"] for e in json.loads(out) if e["id"].startswith("evidence:fs-summary-")}
    rc, out, err = call_locked(KQ, "find", "claim")
    have |= {c["id"] for c in json.loads(out) if c["id"].startswith("claim:fs-") and "-component-" in c["id"] or c["id"].startswith("claim:fs-application")}
    ops = []
    for o, s in comp.items():
        if o == APP:
            continue
        claims = [("tech_stack.languages", s["languages"], 0.95, "observation"),
                  ("metrics.loc", s["loc"], 1.0, "observation"),
                  ("metrics.files", s["files"], 1.0, "observation")]
        if s["layers"]:
            claims.append(("architectural_patterns", {"layers_from_directory_names": s["layers"]}, 0.85, "static_inference"))
        if s["naming"]:
            claims.append(("naming_conventions", s["naming"], 0.85, "static_inference"))
        ops += build(o, lambda p, s=s: {"tech_stack": {**p.get("tech_stack", {}), "languages": s["languages"]},
                                        "metrics": {**p.get("metrics", {}), "loc": s["loc"]}}, claims, comps[o], have)
    aclaims = [("primary_language", primary, 0.95, "observation"), ("total_loc", tot_loc, 1.0, "observation"),
               ("codebase_composition", rows, 0.95, "observation"),
               ("files_over_50k_tokens", [{"path": p, "estimated_tokens": t} for p, t in S["_large"]], 1.0, "observation")]
    ops += build(APP, lambda p: {"primary_language": primary, "total_loc": tot_loc,
                                 "codebase_composition": {"source_ids": sids, "rows": rows}}, aclaims, app, have)
    p = os.path.join(WORK, "enrich-all.json")
    json.dump(ops, open(p, "w"))
    rc, out, err = call_locked(KW, "check-batch", "--operations-file", p)
    print("check", rc, out[:200], err[:500], flush=True)
    if rc != 0:
        break
    rc, out, err = call_locked(KW, "batch", "--operations-file", p)
    print("publish", rc, ('"committed"' in out), err[:500], flush=True)
    if rc == 0:
        break
print("primary", primary, "total_loc", tot_loc)
