package com.example.demo.util;

import graphql.execution.directives.QueryAppliedDirective;
import graphql.execution.directives.QueryAppliedDirectiveArgument;
import graphql.execution.directives.QueryDirectives;
import graphql.schema.DataFetchingEnvironment;

import java.util.List;

public class DirectiveUtil {

    public static <E> E getDirective(DataFetchingEnvironment env, String directiveName, String argument) {
        QueryDirectives queryDirectives= env.getQueryDirectives();
        List<QueryAppliedDirective> directives = queryDirectives.getImmediateAppliedDirective(directiveName);
        if (directives.size() > 0) {
            QueryAppliedDirective cache = directives.get(0);
            QueryAppliedDirectiveArgument currentArgument = cache.getArgument(argument);
            return currentArgument.getValue();
        }
        return null;
    }
}
