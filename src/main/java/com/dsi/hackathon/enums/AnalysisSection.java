package com.dsi.hackathon.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AnalysisSection {
    GENERAL_DETAILS("General Details"),
    CONTACT_INFORMATION("Contact Information"),
    PROJECT_OVERVIEW("Project Overview"),
    TECHNICAL_REQUIREMENTS("Technical Requirements"),
    BUDGET_AND_FINANCIALS("Budget and Financials"),
    PROPOSAL_SUBMISSION_GUIDELINES("Proposal Submission Guidelines"),
    PROPOSAL_EVALUATION_AND_SELECTION("Proposal Evaluation and Selection"),
    LEGAL_AND_COMPLIANCE("Legal and Compliance"),
    TRAINING_SUPPORT_AND_STAFFING("Training, Support and Staffing"),
    ADDITIONAL_DETAILS("Additional Details"),
    ;

    private final String label;
}
