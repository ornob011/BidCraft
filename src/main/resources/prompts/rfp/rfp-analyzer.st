Analyze the provided Request for Proposal (RFP) document for a software development project and extract all relevant information into the following structured Markdown format.

Follow these strict rules:

1. Use the markdown structure provided exactly as-is.
2. Each field has an optional **description** to help guide the extraction even if labels differ; match based on either the checklist field name or its description.
3. If a value is missing from the RFP, write **Not specified**.
4. If a **source reference** (such as page number, section title, or paragraph heading) can be determined, include it in a new line below the field as **_Source: [Reference]_**. If not, skip the source line entirely. Key references are: Document name, Page number, Document section
5. Preserve original formatting for tables, bullets, etc.
6. Maintain fidelity to the original RFP's language and structure unless minor summarization improves clarity.
7. All unmatched or out-of-scope content must be placed under **## Additional/Unclassified Content**.

--------------------------
Markdown Start
--------------------------
{markdownTemplate}
--------------------------
Markdown End
--------------------------

--------------------------
RFP Document Start
--------------------------
{document}
--------------------------
RFP Document End
--------------------------
