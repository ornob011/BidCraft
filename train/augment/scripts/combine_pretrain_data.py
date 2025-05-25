#!/usr/bin/env python3
"""
combine_pretrain_data.py

A utility to combine .txt, .json, and .yaml output files from Augmentoolkit or similar
into a single `.jsonl` file for summarisation pretraining (e.g., with LoRA + Unsloth).

Supports:
- Raw text:     { "text": ... }
- Input/Output: { "input": ..., "output": ... }
- Chat-style:   { "messages": [...] }

Usage:
    python combine_pretrain_data.py --txt_dir ./raw_txt_input --output_dir ./output --save_path ./combined.jsonl
"""

import os
import json
import yaml
import argparse
import pandas as pd
from typing import List, Dict, Union


def find_files_recursive(directory: str, extensions: List[str]) -> List[str]:
    """Recursively finds all files in directory matching given extensions."""
    matches = []
    for root, _, files in os.walk(directory):
        for fname in files:
            if any(fname.endswith(ext) for ext in extensions):
                matches.append(os.path.join(root, fname))
    return matches


def read_txt_files(directory: str) -> List[Dict[str, str]]:
    """Reads all .txt files and returns them in `{"text": content}` format."""
    entries = []
    for file_path in find_files_recursive(directory, [".txt"]):
        with open(file_path, "r", encoding="utf-8", errors="ignore") as f:
            content = f.read().strip()
            if content:
                entries.append({"text": content})
    return entries


def read_json_files(directory: str) -> List[Dict[str, str]]:
    """Reads all .json files and returns them in `{"input": ..., "output": ...}` format if valid."""
    entries = []
    for file_path in find_files_recursive(directory, [".json"]):
        with open(file_path, "r", encoding="utf-8", errors="ignore") as f:
            try:
                obj = json.load(f)
                if "chunk" in obj and "test" in obj:
                    entries.append({
                        "input": obj["chunk"],
                        "output": obj["test"]
                    })
            except Exception:
                continue
    return entries


def read_yaml_files(directory: str) -> List[Dict[str, Union[List[Dict[str, str]], str]]]:
    """Reads all .yaml files and extracts `messages` if present."""
    entries = []
    for file_path in find_files_recursive(directory, [".yaml"]):
        with open(file_path, "r", encoding="utf-8", errors="ignore") as f:
            try:
                data = yaml.safe_load(f)
                messages = data if isinstance(data, list) else data.get("messages")
                if messages:
                    entries.append({"messages": messages})
            except Exception:
                continue
    return entries


def save_to_jsonl(data: List[Dict], output_path: str):
    """Saves the combined list of dictionaries to a .jsonl file."""
    with open(output_path, "w", encoding="utf-8") as f:
        for entry in data:
            json.dump(entry, f, ensure_ascii=False)
            f.write("\n")


def parse_args():
    parser = argparse.ArgumentParser(description="Combine .txt, .json, and .yaml files into a JSONL pretraining dataset.")
    parser.add_argument("--txt_dir", type=str, required=True, help="Path to folder containing .txt input files")
    parser.add_argument("--output_dir", type=str, required=True, help="Path to folder containing .json/.yaml output files")
    parser.add_argument("--save_path", type=str, required=True, help="Path to save combined .jsonl output")
    return parser.parse_args()


def preview_dataset(data: List[Dict], num_samples: int = 10):
    """Prints a preview of the combined dataset using pandas."""
    if not data:
        print("[!] No data to preview.")
        return
    df = pd.DataFrame(data[:num_samples])
    pd.set_option("display.max_colwidth", 200)
    print("\n[*] Preview of Combined Dataset:\n")
    print(df.to_string(index=False))


def main():
    args = parse_args()

    print("[*] Reading input files (recursive)...")
    all_entries = []
    all_entries.extend(read_txt_files(args.txt_dir))
    all_entries.extend(read_json_files(args.output_dir))
    all_entries.extend(read_yaml_files(args.output_dir))

    print(f"[*] Total combined entries: {len(all_entries)}")
    print(f"[*] Saving to: {args.save_path}")
    save_to_jsonl(all_entries, args.save_path)

    preview_dataset(all_entries)


if __name__ == "__main__":
    main()
