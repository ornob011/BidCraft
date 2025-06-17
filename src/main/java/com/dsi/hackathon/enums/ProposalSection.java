package com.dsi.hackathon.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProposalSection {
    RESOURCE_PLAN("Resource plan"),
    PROJECT_TIMELINE("Project timeline"),
    STAFFING_SCHEDULE("Staffing schedule"),
    TRAINING_PLAN("Training plan"),
    BUDGET_DOCUMENTATION("Budget documentation"),
    ;

    private final String label;
}
