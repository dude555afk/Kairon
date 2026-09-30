#!/usr/bin/env python3
"""Bounded remote code-editing agent for an isolated GitHub Actions workspace.

Only typed, path-checked file tools. The model cannot run commands or modify workflows.
The workflow, not the model, owns verification, publishing and GitHub permissions.
"""
import json
import os
import pathlib
import re
import sys
import urllib.error
import urllib.request

ROOT = pathlib.Path.cwd().resolve()
MODEL = os.environ.get("KILO_MODEL", "minimax/minimax-m2.1:free")
KEY = os.environ.get("KILO_API_KEY", "")
INSTRUCTION = os.environ["AGENT_TASK"].strip()
MAX_STEPS = 12
MAX_BYTES = 100_000
DENIED = {".git", ".github", ".gradle", "build", "node_modules", ".env", "gradle", "secrets"}
ALLOWED_SUFFIXES = {".dart", ".arb", ".kt", ".kts", ".java", ".xml", ".md", ".json", ".toml", ".properties", ".yaml", ".yml", ".txt", ".sql", ".sq", ".sqm", ".css", ".html", ".js", ".ts"}
TOOLS = [
    {"type": "function", "function": {"name": "list_files", "description": "List repository source file paths. Optional relative directory.", "parameters": {"type": "object", "properties": {"directory": {"type": "string"}}, "additionalProperties": False}}},
    {"type": "function", "function": {"name": "read_file", "description": "Read an allowed source text file (max 100KB).", "parameters": {"type": "object", "properties": {"path": {"type": "string"}}, "required": ["path"], "additionalProperties": False}}},
    {"type": "function", "function": {"name": "write_file", "description": "Replace or create an allowed source text file. Does not publish changes.", "parameters": {"type": "object", "properties": {"path": {"type": "string"}, "content": {"type": "string"}}, "required": ["path", "content"], "additionalProperties": False}}},
    {"type": "function", "function": {"name": "search_text", "description": "Find a literal text in up to 100 source files.", "parameters": {"type": "object", "properties": {"needle": {"type": "string"}}, "required": ["needle"], "additionalProperties": False}}},
]


def checked(path):
    if not path or pathlib.PurePosixPath(path).is_absolute() or "\\" in path:
        raise ValueError("invalid relative path")
    pieces = pathlib.PurePosixPath(path).parts
    if any(p in ("..", ".") or p in DENIED or p.startswith(".") for p in pieces):
        raise ValueError("restricted path")
    resolved = (ROOT / path).resolve()
    if not resolved.is_relative_to(ROOT) or resolved.suffix.lower() not in ALLOWED_SUFFIXES:
        raise ValueError("restricted file type/path")
    if resolved.is_symlink():
        raise ValueError("symlinks are not allowed")
    return resolved


def paths():
    for p in ROOT.rglob("*"):
        if not p.is_file() or p.is_symlink():
            continue
        rel = p.relative_to(ROOT)
        if any(x in DENIED or x.startswith(".") for x in rel.parts):
            continue
        if p.suffix.lower() in ALLOWED_SUFFIXES:
            yield p, str(rel)


def execute(name, args):
    if name == "list_files":
        directory = args.get("directory", "")
        if directory and (directory.startswith("/") or "\\" in directory or any(x in DENIED or x.startswith(".") for x in pathlib.PurePosixPath(directory).parts)):
            raise ValueError("restricted directory")
        prefix = directory.rstrip("/") + "/" if directory else ""
        return "\n".join(rel for _, rel in paths() if rel.startswith(prefix))[:24000]
    if name == "read_file":
        p = checked(args["path"])
        if p.stat().st_size > MAX_BYTES:
            raise ValueError("file too large")
        return p.read_text(encoding="utf-8")
    if name == "write_file":
        p = checked(args["path"])
        value = args["content"]
        if len(value.encode("utf-8")) > MAX_BYTES:
            raise ValueError("file too large")
        p.parent.mkdir(parents=True, exist_ok=True)
        p.write_text(value, encoding="utf-8")
        return "Updated " + str(p.relative_to(ROOT))
    if name == "search_text":
        needle = args["needle"]
        if not needle or len(needle) > 160:
            raise ValueError("invalid search")
        hits = []
        for p, rel in paths():
            if len(hits) >= 100:
                break
            if p.stat().st_size > MAX_BYTES:
                continue
            for n, line in enumerate(p.read_text(encoding="utf-8", errors="replace").splitlines(), 1):
                if needle in line:
                    hits.append(f"{rel}:{n}: {line[:180]}")
                    if len(hits) >= 100:
                        break
        return "\n".join(hits) or "No matches"
    raise ValueError("unsupported tool")


def completion(messages):
    if not KEY and not MODEL.endswith(":free"):
        raise ValueError("Anonymous gateway is available only for :free models")
    payload = json.dumps({"model": MODEL, "messages": messages, "tools": TOOLS, "tool_choice": "auto", "temperature": 0.1}).encode()
    headers = {"Content-Type": "application/json", "User-Agent": "Kairon-Remote-Agent"}
    if KEY:
        headers["Authorization"] = "Bearer " + KEY
    request = urllib.request.Request("https://api.kilo.ai/api/gateway/chat/completions", data=payload, headers=headers)
    with urllib.request.urlopen(request, timeout=120) as response:
        return json.load(response)["choices"][0]["message"]


def main():
    if not INSTRUCTION or len(INSTRUCTION) > 4000:
        raise ValueError("Task must have 1–4000 characters")
    messages = [
        {"role": "system", "content": "You are a coding agent in a disposable checkout. Read relevant files and implement the user's requested source changes with file tools. Do not touch CI, security policies, credentials or generated build output. No shell tool exists. When done, give a concise report. You may use at most 12 tool rounds."},
        {"role": "user", "content": INSTRUCTION},
    ]
    for step in range(MAX_STEPS):
        result = completion(messages)
        calls = result.get("tool_calls") or []
        messages.append(result)
        if not calls:
            print("Agent summary:", (result.get("content") or "")[:3000])
            return
        for call in calls[:8]:
            try:
                args = json.loads(call["function"]["arguments"])
                output = execute(call["function"]["name"], args)
            except (ValueError, KeyError, OSError, UnicodeError, json.JSONDecodeError) as exc:
                output = f"Tool error: {exc}"
            messages.append({"role": "tool", "tool_call_id": call["id"], "content": output[:100000]})
        if len(calls) > 8:
            raise RuntimeError("Agent exceeded per-round tool limit")
    print("Stopped at autonomous tool-step limit.")


if __name__ == "__main__":
    try:
        main()
    except (ValueError, RuntimeError, urllib.error.URLError, KeyError) as exc:
        print(f"Agent stopped: {type(exc).__name__}: {exc}", file=sys.stderr)
        sys.exit(1)
