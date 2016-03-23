package org.apache.solr.handler.dataimport.functions;

import java.util.List;
import java.util.Locale;

import org.apache.solr.handler.dataimport.Context;
import org.apache.solr.handler.dataimport.Evaluator;

public class LowerCaseFunctionEvaluator extends Evaluator {
	public String evaluate(String expression, Context context) {
		List<Object> l = parseParams(expression, context.getVariableResolver());
		if (l.size() != 1) {
			throw new RuntimeException("'toLowerCase' must have only one parameter ");
		}
		return l.get(0).toString().toLowerCase(Locale.ROOT);
	}
}
