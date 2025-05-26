import json
import requests
from tqdm import tqdm

# Config
INPUT_PATH = "output/augmented_data.jsonl"
OUTPUT_PATH = "output/augmented_summarised.jsonl"
OLLAMA_API_URL = "http://localhost:11434/api/chat"
OLLAMA_MODEL = "llama3.2"

# Combined document analysis instruction
INSTRUCTION = """
You are an expert document analyst, capable of summarising, extracting structure, and interpreting complex documents such as Terms of Reference (ToRs), Requests for Proposals (RFPs), and project outlines.

For any given input document, you must intelligently perform the following tasks:

1. **Document Type Classification**:
   - Briefly identify if the input appears to be a ToR, RFP, project summary, or a general document.

2. **Table of Contents (if applicable)**:
   - Identify and list all major sections and subsections.
   - Provide descriptive and structured headings.
   - Organize content hierarchically, even if not explicitly marked.

3. **Summary**:
   - Summarise the content comprehensively, condensing the core ideas, objectives, scope, and key themes.
   - Adapt your summarisation depth depending on the complexity of the source.

4. **Structured Extraction** (only if RFP or ToR detected):
   - For RFPs, extract and structure the following:
     - Project Title
     - Issuing Organization
     - Submission Deadline
     - Project Objectives
     - Scope of Work
     - Eligibility Criteria
     - Deliverables
     - Timeline and Milestones
     - Evaluation Criteria
     - Budget Information (if present)
     - Key Contacts
     - Ambiguities or Missing Information
   - For ToRs, extract:
     - Section structure (ToC)
     - Key requirements, goals, roles, and any specific deliverables or milestones
     - Highlight data, statistics, or obligations clearly.

Present all findings in a clean, reader-friendly format using sections, bullet points, and headings.
"""

def generate_summary(text):
    payload = {
        "model": OLLAMA_MODEL,
        "messages": [
            {"role": "system", "content": INSTRUCTION.strip()},
            {"role": "user", "content": f"Document:\n---\n{text.strip()}"}
        ],
        "stream": False
    }
    try:
        response = requests.post(OLLAMA_API_URL, json=payload)
        response.raise_for_status()
        return response.json()["message"]["content"].strip()
    except Exception as e:
        print("[!] Error:", e)
        return "Summary unavailable due to error."


# Process and write summaries
with open(INPUT_PATH, "r", encoding="utf-8") as infile, open(OUTPUT_PATH, "w", encoding="utf-8") as outfile:
    for line in tqdm(infile, desc="Summarising"):
        record = json.loads(line)
        text = record.get("output") or record.get("input")
        summary = generate_summary(text)

        summarised_record = {
            "instruction": INSTRUCTION.strip(),
            "input": text,
            "output": summary
        }
        outfile.write(json.dumps(summarised_record, ensure_ascii=False) + "\n")

print(f"Saved summarised data to {OUTPUT_PATH}")
