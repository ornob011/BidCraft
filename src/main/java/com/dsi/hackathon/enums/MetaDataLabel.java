package com.dsi.hackathon.enums;

import org.springframework.ai.vectorstore.filter.Filter;

public enum MetaDataLabel {
    FILENAME,
    DOC_NAME,
    PROJECT_ID,
    UPLOADED_DOC_ID,
    ;

    public Filter.Expression eq(Object val) {
        return new Filter.Expression(
            Filter.ExpressionType.EQ,
            new Filter.Key(this.name()),
            new Filter.Value(val.toString())
        );
    }
}
