#!/usr/bin/env python3
"""
convert_to_unsloth_format.py

Converts a combined summarization `.jsonl` file into Unsloth-compatible chat format.

Supports:
- Chat-style (messages): { "messages": [ { "role": "user", "content": "..." }, ... ] }
- Input/Output: { "input": "...", "output": "..." }
- Raw text: { "text": "..." }

Usage:
    python convert_to_unsloth_format.py --input_path ./combined.jsonl --output_path ./unsloth_chat_format.jsonl
"""

import argparse
import json
from typing import List, Dict


def convert_line_to_conversation(record: Dict) -> List[Dict[str, str]]:
    """Convert a single JSON record to Unsloth-style conversation format."""
    if "messages" in record:
        return [
            {"role": m["role"], "content": m["content"]}
            for m in record["messages"]
            if m.get("role") in {"user", "assistant", "system"}
        ]
    elif "input" in record and "output" in record:
        return [
            {"role": "user", "content": record["input"]},
            {"role": "assistant", "content": record["output"]},
        ]
    elif "text" in record:
        return [
            {"role": "user", "content": "Summarise the following document."},
            {"role": "assistant", "content": record["text"]},
        ]
    return []


def convert_jsonl(input_path: str, output_path: str) -> int:
    """Convert a .jsonl file to Unsloth chat format and save it."""
    converted_count = 0

    with open(input_path, "r", encoding="utf-8") as infile, open(output_path, "w", encoding="utf-8") as outfile:
        for line_number, line in enumerate(infile, start=1):
            try:
                record = json.loads(line.strip())
                conversation = convert_line_to_conversation(record)

                if conversation:
                    json.dump({"conversations": conversation}, outfile, ensure_ascii=False)
                    outfile.write("\n")
                    converted_count += 1
            except json.JSONDecodeError as e:
                print(f"[!] Skipping line {line_number}: JSON decode error - {e}")
            except Exception as e:
                print(f"[!] Skipping line {line_number}: Unexpected error - {e}")

    return converted_count


def main():
    parser = argparse.ArgumentParser(description="Convert JSONL summarization dataset to Unsloth chat format.")
    parser.add_argument("--input_path", type=str, required=True, help="Path to input .jsonl file")
    parser.add_argument("--output_path", type=str, required=True, help="Path to save converted .jsonl file")
    args = parser.parse_args()

    print(f"[*] Converting from: {args.input_path}")
    print(f"[*] Saving to: {args.output_path}")

    count = convert_jsonl(args.input_path, args.output_path)

    print(f"Conversion complete. Total conversations converted: {count}")


if __name__ == "__main__":
    main()
