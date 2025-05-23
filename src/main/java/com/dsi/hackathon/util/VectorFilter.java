package com.dsi.hackathon.util;

import com.dsi.hackathon.enums.MetaDataLabel;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionTextParser;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class VectorFilter {

    public static Filter.Expression andEq(Map<MetaDataLabel, Object> kvm) {

        if (ObjectUtils.isEmpty(kvm)) {
            return null;
        }

        String expressionStr = kvm.entrySet()
                                         .stream()
                                         .map(entry -> "'%s' == '%s'".formatted(entry.getKey(), entry.getValue()))
                                         .collect(Collectors.joining(" && "));

        return new FilterExpressionTextParser().parse(expressionStr);
    }
}
