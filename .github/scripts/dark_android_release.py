"""Validate DARK alpha metadata and the signed APK before publication."""

import argparse
import hashlib
import json
import re
import shutil
import subprocess
from pathlib import Path


PACKAGE = "io.github.xenbw.dark.debug"
CERT_SHA256 = "cf35916b8688602f910bf579cc1170389d8e463832cc994523300fd313196469"


def metadata(tag, run_number):
    if not re.fullmatch(r"v(?:0|[1-9]\d*)\.(?:0|[1-9]\d*)\.(?:0|[1-9]\d*)-[0-9A-Za-z]+(?:[.-][0-9A-Za-z]+)*", tag):
        raise ValueError("Use a prerelease tag such as v0.1.0-alpha")
    code = 154 + int(run_number)
    if not 155 <= code <= 2100000000:
        raise ValueError("Workflow run number is outside Android versionCode limits")
    return {"tag": tag, "version": tag[1:], "version_code": code}


def check_certificate(certificates):
    if certificates != [CERT_SHA256]:
        raise ValueError(f"Signing certificate differs from the original DARK APK: expected {CERT_SHA256}, got {certificates}")


def check_badging(text, version, code):
    first = text.splitlines()[0]
    fields = dict(re.findall(r"(\w+)='([^']*)'", first))
    if fields.get("name") != PACKAGE or fields.get("versionName") != version or fields.get("versionCode") != str(code):
        raise ValueError("APK package or version differs from the requested DARK build")
    if "application-label:'DARK'" not in text.splitlines():
        raise ValueError("APK application label must be DARK")


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    commands = parser.add_subparsers(dest="command", required=True)
    meta = commands.add_parser("metadata")
    meta.add_argument("--tag", required=True)
    meta.add_argument("--run-number", type=int, required=True)
    key = commands.add_parser("verify-keystore")
    key.add_argument("path", type=Path)
    package = commands.add_parser("package")
    package.add_argument("--apk", type=Path, required=True)
    package.add_argument("--sdk", type=Path, required=True)
    package.add_argument("--tag", required=True)
    package.add_argument("--run-number", type=int, required=True)
    package.add_argument("--commit", required=True)
    package.add_argument("--run-url", required=True)
    package.add_argument("--output", type=Path, default=Path("dist"))
    args = parser.parse_args()
    if args.command == "metadata":
        for name, value in metadata(args.tag, args.run_number).items():
            print(f"{name}={value}")
    elif args.command == "verify-keystore":
        certificate = subprocess.check_output([
            "keytool", "-exportcert", "-keystore", str(args.path),
            "-storepass", "android", "-alias", "androiddebugkey",
        ])
        check_certificate([hashlib.sha256(certificate).hexdigest()])
        print("Original DARK signing certificate verified")
    else:
        values = metadata(args.tag, args.run_number)
        tools = args.sdk / "build-tools" / "36.0.0"
        signature = subprocess.check_output([
            str(tools / "apksigner"), "verify", "--verbose", "--print-certs", str(args.apk),
        ], text=True)
        check_certificate(re.findall(r"^Signer #\d+ certificate SHA-256 digest: ([0-9a-f]+)$", signature, re.MULTILINE))
        badging = subprocess.check_output([str(tools / "aapt2"), "dump", "badging", str(args.apk)], text=True)
        check_badging(badging, values["version"], values["version_code"])
        args.output.mkdir(parents=True, exist_ok=True)
        target = args.output / f"DARK-{args.tag}.apk"
        shutil.copy2(args.apk, target)
        digest = hashlib.sha256(target.read_bytes()).hexdigest()
        target.with_suffix(".apk.sha256").write_text(f"{digest}  {target.name}\n")
        record = {
            **values, "commit": args.commit, "workflow_run": args.run_url,
            "package": PACKAGE, "certificate_sha256": CERT_SHA256, "apk_sha256": digest,
        }
        (args.output / f"DARK-{args.tag}-build.json").write_text(json.dumps(record, indent=2) + "\n")
        print(json.dumps(record, indent=2))


if __name__ == "__main__":
    main()
