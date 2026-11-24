import hashlib, json, os, re, glob, datetime

ROOT = r"C:\Users\sodeshpa\.copilot\repos\copilot-worktrees\crh-logon\sodeshpa-improved-telegram"
OUT = os.path.join(ROOT, ".solutiondocs", ".docgen", "work-config-infra")
APP = "application:sodeshpa-improved-telegram"
NOW = datetime.datetime.now(datetime.timezone.utc).strftime("%Y-%m-%dT%H:%M:%SZ")
SECRET = re.compile(r"(?i)pass|pwd|secret|token|credential|keystore\.pass")

ops1, ops2 = [], []


def slug(s):
    return re.sub(r"[^a-z0-9]+", "-", s.lower()).strip("-")


def h(*parts):
    return hashlib.sha256("|".join(str(p) for p in parts).encode()).hexdigest()


def sha(path):
    return hashlib.sha256(open(os.path.join(ROOT, path), "rb").read()).hexdigest()


def put(ops, rec):
    ops.append({"record": rec, "expected_revision": 0})


def read_props(path):
    d = {}
    for line in open(os.path.join(ROOT, path), encoding="utf8", errors="replace"):
        m = re.match(r"^\s*([A-Za-z0-9_.\-]+)\s*[=:]\s*(.*?)\s*$", line)
        if m and not line.lstrip().startswith("#"):
            d[m.group(1)] = m.group(2)
    return d


SRC, EV = {}, {}


def source(path, ops):
    if path in SRC:
        return SRC[path]
    sid = "source_artifact:cfg-" + slug(path)
    rev = sha(path)
    put(ops, {"id": sid, "record_kind": "node", "type": "source_artifact", "name": path.replace("\\", "/"),
              "application_id": APP, "status": "active",
              "properties": {"source_type": "config_file", "location": path.replace("\\", "/"), "revision": rev}})
    eid = "evidence:cfg-" + h(path)
    put(ops, {"id": eid, "record_kind": "evidence", "application_id": APP, "status": "active", "source_id": sid,
              "source_revision": rev, "method": "config-file",
              "locator": {"file": path.replace("\\", "/"), "section": "file"}, "observed_at": NOW})
    SRC[path] = (sid, rev)
    EV[path] = eid
    return SRC[path]


def claim(ops, path, subject, predicate, value=None, target=None, conf=0.95, deriv="observation"):
    sid, rev = source(path, ops)
    cid = "claim:" + h(subject, predicate, json.dumps(value, sort_keys=True), target)
    rec = {"id": cid, "record_kind": "claim", "application_id": APP, "status": "candidate", "subject": subject,
           "predicate": predicate, "derivation": deriv, "confidence": conf, "evidence_ids": [EV[path]],
           "applicability": {"source_revision": rev}}
    if target:
        rec["target"] = target
    else:
        rec["value"] = value
    put(ops, rec)
    return cid


def node(ops, ntype, nid, name, props, path, predicate, value, conf=0.95):
    put(ops, {"id": nid, "record_kind": "node", "type": ntype, "name": name, "application_id": APP,
              "status": "active", "properties": props})
    claim(ops, path, nid, predicate, value, conf=conf)


def rel(ops, rtype, frm, to, path, conf=0.95):
    cid = claim(ops, path, frm, rtype, target=to, conf=conf)
    put(ops, {"id": "relationship:" + rtype + "-" + h(frm, to)[:40], "record_kind": "relationship",
              "application_id": APP, "status": "active", "type": rtype, "from": frm, "to": to, "properties": {},
              "claim_ids": [cid]})


def owner(path):
    top = path.replace("\\", "/").split("/")[0]
    return {"etc": "component:etc", "ansible": "component:ansible", "logon-ansible": "component:logon-ansible"}.get(top, APP)


CFG = [
    ("config/build.properties", "properties", "build", False, ["pkg"], "Base Ant build/deploy properties"),
    ("config/build-dev.properties", "properties", "build", True, ["dev"], "Developer environment build overrides"),
    ("config/build-nightly.properties", "properties", "build", True, ["nightly"], "Nightly environment overrides"),
    ("config/build-local_tmpl.properties", "properties", "build", True, ["local"], "Template for per-developer build-local.properties"),
    ("config/logon-db.properties", "properties", "database", True, ["ch01", "ch02", "ch03", "hu00", "hu01", "hu02", "vn01", "rhu01", "rhu02", "int2", "int3", "int4", "test1", "test2", "test3"], "Per-environment Oracle schema targets"),
    ("config/settings_tmpl.xml", "xml", "build", False, [], "Maven settings template"),
    ("etc/logon.properties", "properties", "application", False, ["pkg"], "Runtime application properties"),
    ("etc/logon-dev.properties", "properties", "application", True, ["dev"], "Dev runtime properties incl. cron expressions"),
    ("etc/logon-nightly.properties", "properties", "application", True, ["nightly"], "Nightly runtime properties incl. cron expressions"),
    ("etc/log4j.xml", "xml", "logging", False, ["pkg"], "log4j 1.2 configuration"),
    ("etc/log4j_dev.xml", "xml", "logging", True, ["dev"], "log4j configuration for dev"),
    ("etc/log4j_nightly.xml", "xml", "logging", True, ["nightly"], "log4j configuration for nightly"),
    ("etc/login-config.xml", "xml", "security", False, [], "JBoss JAAS application policies"),
    ("etc/roles.properties", "properties", "security", False, ["dev"], "Role mapping for dev users"),
    ("etc/users.properties", "properties", "security", False, ["dev"], "User definitions for dev"),
    ("container/jboss7/standalone-dev.xml", "xml", "deployment", True, ["dev"], "JBoss EAP standalone config (dev)"),
    ("container/jboss7/standalone-nightly.xml", "xml", "deployment", True, ["nightly"], "JBoss EAP standalone config (nightly)"),
    ("container/properties.xml", "xml", "deployment", False, [], "Container-specific Ant properties"),
    ("logon-business/src/main/resources/META-INF/persistence.xml", "xml", "database", False, [], "JPA persistence unit logon"),
    ("logon-business/etc/cfg/log4j.properties", "properties", "logging", False, [], "log4j properties for logon-business"),
    ("ansible/ansible.cfg", "ini", "deployment", False, [], "Ansible defaults for build-node provisioning"),
    ("ansible/inventories/integration", "ini", "deployment", False, ["integration"], "Ansible inventory: build host"),
    ("logon-ansible/ansible.cfg", "ini", "deployment", False, [], "Ansible config for Windows INT deployments"),
    ("logon-ansible/inventory", "ini", "deployment", False, ["int"], "Ansible inventory of INT hosts"),
    ("logon-ansible/group_vars_tmpl/logonebm1", "yaml", "deployment", False, ["int"], "Ansible group vars template"),
]
FMT = {"ini": "custom", "yaml": "yaml"}
cfg_ids = {}
for path, fmt, purpose, envs, envl, desc in CFG:
    if not os.path.exists(os.path.join(ROOT, path)):
        continue
    cid = "config_item:" + slug(path)
    cfg_ids[path] = cid
    contains_secret = False
    if fmt == "properties":
        contains_secret = any(SECRET.search(k) and v for k, v in read_props(path).items())
    props = {"file_path": path.replace("\\", "/"), "format": FMT.get(fmt, fmt), "purpose": purpose,
             "environment_specific": envs, "version_controlled": True, "contains_secrets": contains_secret}
    if envl:
        props["environments"] = envl
    node(ops1, "config_item", cid, path.replace("\\", "/"), props, path, "purpose", desc)
    rel(ops1, "contains", owner(path), cid, path)

CONFIG_ID = "configuration:logon-build-and-runtime"
secret_count = 0
for p in ("config/build.properties", "config/build-dev.properties", "config/build-nightly.properties", "config/logon-db.properties"):
    secret_count += sum(1 for k, v in read_props(p).items() if SECRET.search(k) and v)
node(ops1, "configuration", CONFIG_ID, "Logon build and runtime configuration",
     {"summary": {"total_config_files": len(cfg_ids), "hardcoded_secrets_count": secret_count},
      "secrets_management": {"provider": "plain-text properties files in version control", "rotation_configured": False,
                             "secrets_in_config": secret_count}},
     "config/build.properties", "secrets_in_config_files", secret_count, conf=0.85)
rel(ops1, "contains", APP, CONFIG_ID, "config/build.properties")
for p, cid in cfg_ids.items():
    rel(ops1, "references", CONFIG_ID, cid, p)

subbuilds = sorted(os.path.relpath(x, ROOT).replace("\\", "/") for x in glob.glob(os.path.join(ROOT, "*", "build.xml")))
PIPES = {
    "build_pipeline:logon-ant-master-build": ("Logon Ant master build", "build.xml", {
        "build_system": {"tool": "Apache Ant", "config_files": ["build.xml", "common.xml", "properties.xml", "build-test.xml"] + subbuilds,
                         "build_profiles": ["pkg", "dev", "nightly", "local"],
                         "build_steps": [{"name": "init"}, {"name": "dist"}, {"name": "do-all"}, {"name": "deploy"}, {"name": "package"}, {"name": "relnotes"}]},
        "logging": {"framework": "log4j", "framework_version": "1.2.16", "config_file": "etc/log4j.xml",
                    "log_destinations": [{"type": "file", "config_location": "etc/log4j.xml"}, {"type": "console", "config_location": "etc/log4j.xml"}]}},
        "build_tool", "Apache Ant"),
    "build_pipeline:logon-container-deploy": ("Logon container (JBoss EAP) build and deploy", "container/build.xml", {
        "build_system": {"tool": "Apache Ant", "config_files": ["container/build.xml", "container/properties.xml"],
                         "build_steps": [{"name": "earint/earext/earterminal"}, {"name": "int/ext/terminal deploy"}, {"name": "container-coldstart"}, {"name": "hot-deploy"}]}},
        "ear_targets", "earint, earext, earterminal"),
    "build_pipeline:logon-test-build": ("Logon Ant test build", "build-test.xml", {
        "build_system": {"tool": "Apache Ant", "config_files": ["build-test.xml"],
                         "build_steps": [{"name": "test-unit"}, {"name": "test-integration"}, {"name": "test-pmd"}, {"name": "test-load"}, {"name": "test-pda-workflow"}]}},
        "test_targets", "unit, integration, PMD, coverage, load, PDA workflow"),
    "build_pipeline:logon-ansible-int-deploy": ("Logon Ansible INT deployment", "logon-ansible/build-logon.sh", {
        "ci_cd_pipeline": {"platform": "Ansible (WinRM playbooks) with shell wrappers", "config_file": "logon-ansible/install-INT1.sh",
                           "trigger": "manual",
                           "stages": [{"name": "init-variables.sh"}, {"name": "logonebmdb2.play"}, {"name": "logonebm1/2.play"}]}},
        "deployment_tool", "Ansible playbooks over WinRM"),
    "build_pipeline:build-node-provisioning": ("Build node provisioning (Ansible)", "ansible/buildnode.yml", {
        "developer_environment": {"setup_automated": True, "setup_automation_tool": "Ansible role buildnode"}},
        "provisioning_tool", "Ansible"),
}
for pid, (name, path, props, pred, val) in PIPES.items():
    node(ops1, "build_pipeline", pid, name, props, path, pred, val)
    rel(ops1, "contains", owner(path), pid, path)
rel(ops1, "uses", "build_pipeline:logon-ant-master-build", "build_pipeline:logon-container-deploy", "container/build.xml")
rel(ops1, "uses", "build_pipeline:logon-ant-master-build", "build_pipeline:logon-test-build", "build-test.xml")
for p in ("config/build.properties", "config/build-dev.properties", "config/build-nightly.properties", "config/build-local_tmpl.properties"):
    rel(ops1, "uses", "build_pipeline:logon-ant-master-build", cfg_ids[p], "properties.xml")
rel(ops1, "uses", "build_pipeline:logon-container-deploy", cfg_ids["container/jboss7/standalone-dev.xml"], "container/build.xml")
rel(ops1, "uses", "build_pipeline:build-node-provisioning", cfg_ids["ansible/ansible.cfg"], "ansible/buildnode.yml")
rel(ops1, "uses", "build_pipeline:logon-ansible-int-deploy", cfg_ids["logon-ansible/inventory"], "logon-ansible/install-INT1.sh")

PS = [
    ("platform_service:oracle-database", "Oracle Database", {"service": "Relational database", "product": "Oracle Database (ojdbc8 driver)", "portable": False},
     "container/jboss7/standalone-dev.xml", "datasource_driver", "oracle (ojdbc8 module)"),
    ("platform_service:jboss-eap", "Red Hat JBoss EAP", {"service": "Application server", "product": "JBoss EAP 6.4.21", "portable": False},
     "config/build.properties", "JBOSS_EAP_VERSION", "6.4.21"),
    ("platform_service:hornetq-jms", "JBoss messaging (HornetQ) JMS", {"service": "Message queue", "product": "HornetQ (queue MopQueue)", "portable": False},
     "container/jboss7/standalone-dev.xml", "jms_queue", "MopQueue"),
    ("platform_service:smtp-mail-session", "JBoss mail session (SMTP)", {"service": "Outbound email", "product": "SMTP via java:jboss/mail/Default", "portable": True},
     "container/jboss7/standalone-dev.xml", "mail_session_jndi", "java:jboss/mail/Default"),
    ("platform_service:spnego-negotiation", "SPNEGO authentication (JBoss Negotiation)", {"service": "Integrated authentication", "product": "SPNEGO for int login domain", "portable": False},
     "config/build.properties", "login_domain_int", "SPNEGO"),
    ("platform_service:sonarqube-analysis", "SonarQube / ADN Sonar", {"service": "Static analysis (build-time)", "product": "adnsonar", "portable": True},
     "config/build.properties", "static_analysis_tool", "adnsonar"),
]
for pid, name, props, path, pred, val in PS:
    node(ops1, "platform_service", pid, name, props, path, pred, val, conf=0.9)
    rel(ops1, "contains", APP, pid, path, conf=0.9)
rel(ops1, "uses", "build_pipeline:logon-container-deploy", "platform_service:jboss-eap", "container/build.xml")
for t in ("oracle-database", "hornetq-jms", "smtp-mail-session", "spnego-negotiation"):
    rel(ops1, "uses", "platform_service:jboss-eap", "platform_service:" + t, "container/jboss7/standalone-dev.xml", conf=0.9)
rel(ops1, "uses", "build_pipeline:logon-ant-master-build", "platform_service:sonarqube-analysis", "config/build.properties", conf=0.85)

dev = read_props("etc/logon-dev.properties")
nightly = read_props("etc/logon-nightly.properties")
for k, v in dev.items():
    if not k.endswith(".cron_expression"):
        continue
    name = k[: -len(".cron_expression")]
    pid = "automated_process:logon-" + slug(name)
    props = {"process_type": "scheduled_task", "schedule": v, "trigger": "schedule", "source_location": "etc/logon-dev.properties"}
    node(ops1, "automated_process", pid, name, props, "etc/logon-dev.properties", "schedule", v)
    if k in nightly and nightly[k] != v:
        claim(ops1, "etc/logon-nightly.properties", pid, "schedule_nightly", nightly[k])
    rel(ops1, "contains", APP, pid, "etc/logon-dev.properties")
    rel(ops1, "references", pid, cfg_ids["etc/logon-dev.properties"], "etc/logon-dev.properties")

SEL = {
    "config/build.properties": ["VERSION", "RELEASE", "environment", "compile.source", "compile.target", "container.type", "JBOSS_EAP_VERSION", "db.type",
                                 "context.root", "login.domain.int", "login.domain.ext", "login.domain.dmz", "login.domain.terminal",
                                 "redirect_to_https.int", "redirect_to_https.ext", "redirect_to_https.dmz", "redirect_to_https.terminal",
                                 "kafka.version", "jackson.version", "deployment", "configurationPath", "build-logon-int", "build-logon-ext",
                                 "build-logon-dmz", "build-logon-terminal", "db.connection.url", "application.test.url"],
    "config/build-dev.properties": ["configurationPath", "externalLog4jConfigPath", "login.domain.int", "db.connection.url"],
    "config/build-nightly.properties": ["container.host", "coverage.enabled", "configurationPath", "externalLog4jConfigPath", "login.domain.int", "db.connection.url"],
    "etc/logon.properties": ["instance", "providerurl", "devmode", "zone", "access_restrictions", "use_HttpOnly_SessionIdFilter", "use_HttpOnly_Secure_SessionIdFilter"],
    "etc/logon-dev.properties": ["instance", "providerurl", "devmode", "zone", "access_restrictions"],
    "etc/logon-nightly.properties": ["instance", "providerurl", "devmode", "zone", "access_restrictions"],
}
n_settings = 0
n_secret_settings = 0
for path in ("config/build.properties", "config/build-dev.properties", "config/build-nightly.properties", "config/logon-db.properties", "etc/logon.properties", "etc/logon-dev.properties", "etc/logon-nightly.properties"):
    props = read_props(path)
    keys = list(SEL.get(path, []))
    keys += [k for k, v in props.items() if SECRET.search(k) and k not in keys]
    for k in keys:
        if k not in props:
            continue
        v = props[k]
        sec = bool(SECRET.search(k))
        conn = bool(re.search(r"(?i)connection\.url|jdbc:", k + v)) and not sec
        sprops = {"key": k, "source_file": path, "is_secret": sec, "is_connection_string": conn, "value_type": "string"}
        if not sec and v and not conn and not re.search(r"@|://", v):
            sprops["default_value"] = v
            if re.fullmatch(r"-?\d+", v):
                sprops["value_type"] = "number"
            elif v in ("true", "false"):
                sprops["value_type"] = "boolean"
        if conn:
            sprops["value_type"] = "connection_string"
        sid = "setting:" + slug(path) + "--" + slug(k)
        node(ops2, "setting", sid, k, sprops, path, "defined_in", path.replace("\\", "/"), conf=0.9 if sec else 0.95)
        rel(ops2, "contains", cfg_ids[path], sid, path)
        n_settings += 1
        n_secret_settings += sec

for pred, val in (("containerisation_docker_exists", False), ("containerisation_compose_exists", False), ("containerisation_k8s_manifests_exist", False),
                  ("deployment_runtime", "JBoss EAP 6.4 standalone via Ant container targets; Windows MSI via Ansible"),
                  ("containerisation_blockers", "Plain-text environment property files, Ant-driven JBoss install, host-specific paths, Oracle client tooling")):
    claim(ops2, "container/build.xml", APP, pred, val, conf=0.85, deriv="static_inference")
claim(ops2, "config/build.properties", APP, "ci_cd_config_detected", False, conf=0.85, deriv="static_inference")
claim(ops2, "config/build.properties", APP, "hardcoded_credentials_in_config_files", secret_count, conf=0.85, deriv="static_inference")
claim(ops2, "logon-ansible/build-logon.sh", "build_pipeline:logon-ansible-int-deploy", "hardcoded_credential_in_script", True, conf=0.85, deriv="static_inference")

os.makedirs(OUT, exist_ok=True)
json.dump(ops1, open(os.path.join(OUT, "batch-1.json"), "w"), indent=1)
json.dump(ops2, open(os.path.join(OUT, "batch-2.json"), "w"), indent=1)
print(len(ops1), len(ops2), "settings", n_settings, "secret settings", n_secret_settings, "config items", len(cfg_ids))
