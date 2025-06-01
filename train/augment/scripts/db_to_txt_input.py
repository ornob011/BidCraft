"""
Augment Pipeline - Vector Store Export Script

This script connects to a PostgreSQL vector store, groups text chunks by document metadata (e.g., UPLOADED_DOC_ID and FILENAME), and exports each document as a consolidated `.txt` file in a target folder (default: `raw_txt_input`).

Key Features:
- Production-ready, modular Python structure
- Environment-based configuration
- Handles mixed-type metadata (dict or JSON string)
- Sanitizes output filenames safely
- Logs progress and skips invalid rows

Usage:
$ python augment_pipeline.py

Optional environment variables:
- PG_HOST, PG_PORT, PG_DATABASE, PG_USER, PG_PASSWORD, PG_SSLMODE
- OUTPUT_DIR
"""

import psycopg2
import os
import json
from collections import defaultdict
import re
from typing import Dict, List, Tuple, Union

# === Configuration ===
DB_CONFIG = {
    "host": os.getenv("PG_HOST", "hackathon-ornob011-70d9.l.aivencloud.com"),
    "port": int(os.getenv("PG_PORT", 19839)),
    "database": os.getenv("PG_DATABASE", "defaultdb"),
    "user": os.getenv("PG_USER", "avnadmin"),
    "password": os.getenv("PG_PASSWORD", "AVNS_rDz2KsR9Y0AiEos14-K"),
    "sslmode": os.getenv("PG_SSLMODE", "require")
}

OUTPUT_DIR = os.getenv("OUTPUT_DIR", "raw_txt_input")
os.makedirs(OUTPUT_DIR, exist_ok=True)

def sanitize_filename(name: str) -> str:
    """Sanitize file name for file system."""
    base = os.path.splitext(name)[0]
    return re.sub(r"[\\/*?\"<>| :]", "_", base)

def load_metadata(raw: Union[str, Dict]) -> Dict:
    """Safely convert raw metadata into a Python dictionary."""
    if isinstance(raw, dict):
        return raw
    try:
        return json.loads(raw)
    except Exception:
        return {}

def fetch_vectorstore_rows() -> List[Tuple[str, Union[str, Dict]]]:
    """Connect to PostgreSQL and fetch content + metadata."""
    with psycopg2.connect(**DB_CONFIG) as conn:
        with conn.cursor() as cur:
            cur.execute("SELECT content, metadata FROM vector_store WHERE content IS NOT NULL;")
            return cur.fetchall()

def group_documents(rows: List[Tuple[str, Union[str, Dict]]]) -> Dict[Tuple[int, str], List[Tuple[int, str]]]:
    """Group rows by (UPLOADED_DOC_ID, sanitized filename) into page-sorted chunks."""
    documents = defaultdict(list)
    for idx, (content, raw_metadata) in enumerate(rows):
        metadata = load_metadata(raw_metadata)
        upload_id = metadata.get("UPLOADED_DOC_ID")
        if upload_id is None:
            print(f"[SKIP] Row {idx}: Missing UPLOADED_DOC_ID")
            continue

        filename = metadata.get("FILENAME") or metadata.get("file_name") or f"unknown_{upload_id}.txt"
        page_number = metadata.get("page_number", 0)
        safe_filename = sanitize_filename(filename)
        key = (upload_id, safe_filename)
        documents[key].append((page_number, content.strip()))
    return documents

def write_documents(documents: Dict[Tuple[int, str], List[Tuple[int, str]]]) -> int:
    """Write grouped documents to output directory."""
    count = 0
    for (upload_id, filename), chunks in documents.items():
        chunks.sort(key=lambda x: x[0])
        combined = "\n\n".join(chunk for _, chunk in chunks)
        output_path = os.path.join(OUTPUT_DIR, f"{upload_id}_{filename}.txt")
        with open(output_path, "w", encoding="utf-8") as f:
            f.write(combined.strip())
        print(f"[Written]: {output_path}")
        count += 1
    return count

def main():
    print("[INFO] Fetching rows from database...")
    rows = fetch_vectorstore_rows()
    print(f"[INFO] Total rows fetched: {len(rows)}")

    print("[INFO] Grouping documents by UPLOADED_DOC_ID and filename...")
    documents = group_documents(rows)
    print(f"[INFO] Grouped into {len(documents)} document(s).")

    print("[INFO] Writing combined .txt files...")
    total_written = write_documents(documents)
    print(f"\n[FINAL] {total_written} document files saved in '{OUTPUT_DIR}/'")

if __name__ == "__main__":
    main()
