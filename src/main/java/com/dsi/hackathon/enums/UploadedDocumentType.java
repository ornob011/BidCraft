package com.dsi.hackathon.enums;

import lombok.Getter;

@Getter
public enum UploadedDocumentType {
    // Documents from Organizer
    REQUEST_FOR_PROPOSAL("Request for Proposal (RFP)"),
    TERMS_OF_REFERENCE("Terms of Reference (TOR)"),
//    REQUEST_FOR_TENDER("Request for Tender (RFT)"),
//    SCOPE_OF_WORK("Scope of Work (SOW)"),
//    INSTRUCTIONS_TO_BIDDERS("Instructions to Bidders"),
//    EVALUATION_CRITERIA("Evaluation Criteria Document"),
//    TENDER_ADVERTISEMENT("Tender Advertisement"),
//    PUBLIC_NOTICE("Public Notice"),
//
//    // Documents from Vendor
//    TECHNICAL_PROPOSAL("Technical Proposal"),
//    FINANCIAL_PROPOSAL("Financial Proposal"),
//    COMPLIANCE_DOCUMENTS("Compliance Documents"),
//    BID_SECURITY("Bid Security"),
//    EARNEST_MONEY_DEPOSIT("Earnest Money Deposit (EMD)"),

    // Other documents
    OTHER_ATTACHMENTS("Other Attachments");

    private final String displayName;

    UploadedDocumentType(String displayName) {
        this.displayName = displayName;
    }
}

