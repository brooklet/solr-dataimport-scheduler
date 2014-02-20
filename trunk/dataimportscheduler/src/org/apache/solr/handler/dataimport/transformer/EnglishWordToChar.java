package org.apache.solr.handler.dataimport.transformer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.handler.dataimport.Context;
import org.apache.solr.handler.dataimport.ContextImpl;
import org.apache.solr.handler.dataimport.DataConfig;
import org.apache.solr.handler.dataimport.Transformer;
import org.apache.solr.handler.dataimport.DataConfig.Entity;

public class EnglishWordToChar extends Transformer {

	@Override
	public Map<String, Object> transformRow(Map<String, Object> row,
			Context context) {

		List<Map<String, String>> fields = context.getAllEntityFields();

		for (Map<String, String> field : fields) {
			// Check if this field has pinYin="true" specified in the
			// data-config.xml
			String toCharacter = field.get("toCharacter");
			if ("true".equals(toCharacter)) {
				// Apply PinYinHeadChar on this field
				String columnName = field.get("column");
				// Get this field's value from the current row
				String value = (String) row.get(columnName);
				// Trim and put the updated value back in the current row

				if (value != null) {
					value = value.replaceAll("(\\S{1})", "$1 ");
					row.put(columnName, value);
				}
			}

		}
		return row;
	}

	public static void main(String[] args) {
		String value = "徒步旅行 Walkabout Journeys";
		value = value.replaceAll("(\\S{1})", "$1 ");
		System.out.println(value);

		String cnStr = "徒步旅行 WalkaboutNBA2K9";
		Map<String, Object> row = new HashMap<String, Object>();
		row.put("gameName", cnStr);
		row.put("nameToCharacter", cnStr);

		DataConfig.Entity ent = new Entity();
		ArrayList<Map<String, String>> allFieldsList = new ArrayList<Map<String, String>>();

		Map<String, String> field1 = new HashMap<String, String>();
		field1.put("column", "nameToCharacter");
		field1.put("toCharacter", "true");

		allFieldsList.add(field1);

		ent.allFieldsList = allFieldsList;

		ContextImpl context = new ContextImpl(ent, null, null, null, null,
				null, null);

		EnglishWordToChar gamePinYinTransformer = new EnglishWordToChar();
		row = gamePinYinTransformer.transformRow(row, context);
		System.out.println(row);
	}

}
