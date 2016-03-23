package org.apache.solr.handler.dataimport.functions;

import java.util.List;

import org.apache.solr.handler.dataimport.Context;
import org.apache.solr.handler.dataimport.Evaluator;

/**
 * 
 * @author zhangliang
 * 
 */
public class ShardTableNameFunctionEvaluator extends Evaluator {

	/**
	 * expression
	 * ${dataimporter.functions.shardTableName('tb_user_dl_js_#index#',
	 * dataimporter. request.userId,begin,count)}
	 */
	public String evaluate(String expression, Context context) {
		System.out.println("expression: " + expression);

		List<Object> l = parseParams(expression, context.getVariableResolver());
		if (l.size() != 4) {
			throw new RuntimeException("'shardTableName' must have 4 parameter ");
		}
		String tableBaseName = l.get(0).toString();
		System.out.println("tableBaseName: " + tableBaseName);
		Long id = Long.parseLong(l.get(1).toString());
		Integer begin = ((Double)l.get(2)).intValue();
		Integer count = ((Double)l.get(3)).intValue();
		long tableNameIndex = begin + (id % count);
		System.out.println("tableNameIndex: " + tableNameIndex);
		String realName = tableBaseName.replaceAll("#index#", Long.toString(tableNameIndex));
		System.out.println("realName: " + realName);
		return realName;
	}

}
