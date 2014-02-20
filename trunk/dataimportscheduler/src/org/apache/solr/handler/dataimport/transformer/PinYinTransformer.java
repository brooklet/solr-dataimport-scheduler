package org.apache.solr.handler.dataimport.transformer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import org.apache.solr.handler.dataimport.Context;
import org.apache.solr.handler.dataimport.ContextImpl;
import org.apache.solr.handler.dataimport.DataConfig;
import org.apache.solr.handler.dataimport.DataConfig.Entity;
import org.apache.solr.handler.dataimport.Transformer;

public class PinYinTransformer extends Transformer {

	private String getPingYin(String src) {
		char[] t1 = null;
		String t4 = "";
		if (src != null && src.length() > 0) {
			t1 = src.toCharArray();
			String[] t2 = new String[t1.length];
			HanyuPinyinOutputFormat t3 = new HanyuPinyinOutputFormat();
			t3.setCaseType(HanyuPinyinCaseType.LOWERCASE);
			t3.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
			t3.setVCharType(HanyuPinyinVCharType.WITH_V);

			int t0 = t1.length;
			try {
				for (int i = 0; i < t0; i++) {
					// 判断是否为汉字字符
					if (java.lang.Character.toString(t1[i]).matches(
							"[\\u4E00-\\u9FA5]+")) {
						t2 = PinyinHelper.toHanyuPinyinStringArray(t1[i], t3);
						if (t2 != null) {
							t4 += t2[0];
							t4 += " ";
						}
					} else
						t4 += java.lang.Character.toString(t1[i]);
				}
				return t4.trim();
			} catch (BadHanyuPinyinOutputFormatCombination e1) {
				e1.printStackTrace();
			}
		}
		return t4.trim();
	}

	// 返回中文的首字母
	private String getPinYinHeadChar(String str, boolean spSpace) {
		String convert = "";
		if (str != null && str.length() > 0) {
			for (int j = 0; j < str.length(); j++) {
				char word = str.charAt(j);
				String[] pinyinArray = PinyinHelper
						.toHanyuPinyinStringArray(word);
				if (pinyinArray != null) {
					if (spSpace) {
						convert += pinyinArray[0].charAt(0);
						convert += ' ';
					} else {
						convert += pinyinArray[0].charAt(0);
					}
				} else {
					convert += word;
				}
			}
		}
		return convert;
	}

	// 返回中文的第一个字的首字母
	private String getPinYinHeadCharFirst(String str) {
		String convert = "";
		if (str != null && str.length() > 0) {
			String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(str
					.charAt(0));
			if (pinyinArray != null) {
				convert = String.valueOf(pinyinArray[0].charAt(0))
						.toUpperCase();
			} else {
				convert = String.valueOf(str.charAt(0)).toUpperCase();
			}
		}
		return convert;
	}

	public Map<String, Object> transformRow(Map<String, Object> row,
			Context context) {

		List<Map<String, String>> fields = context.getAllEntityFields();

		for (Map<String, String> field : fields) {
			// Check if this field has pinYin="true" specified in the
			// data-config.xml
			String PinYin = field.get("pinYin");
			if ("true".equals(PinYin)) {
				// Apply PinYinHeadChar on this field
				String columnName = field.get("column");
				// Get this field's value from the current row
				String value = (String) row.get(columnName);
				// Trim and put the updated value back in the current row

				if (value != null) {
					String namePinyin = getPingYin(value);
					row.put(columnName, namePinyin);
				}
			}

			String PinYinHeadChar = field.get("pinYinHeadChar");
			if ("true".equals(PinYinHeadChar)) {
				// Apply PinYinHeadChar on this field
				String columnName = field.get("column");
				// Get this field's value from the current row
				String value = (String) row.get(columnName);
				// Trim and put the updated value back in the current row

				if (value != null) {
					String namePinYinHeadChar = getPinYinHeadChar(value, false);
					row.put(columnName, namePinYinHeadChar);
				}
			}

			String pinYinHeadCharSep = field.get("pinYinHeadCharSep");
			if ("true".equals(pinYinHeadCharSep)) {
				// Apply PinYinHeadChar on this field
				String columnName = field.get("column");
				// Get this field's value from the current row
				String value = (String) row.get(columnName);
				// Trim and put the updated value back in the current row

				if (value != null) {
					String namePinYinHeadCharSep = getPinYinHeadChar(value,
							true);
					row.put(columnName, namePinYinHeadCharSep);
				}
			}

			String pinYinHeadCharFirst = field.get("pinYinHeadCharFirst");
			if ("true".equals(pinYinHeadCharFirst)) {
				// Apply PinYinHeadChar on this field
				String columnName = field.get("column");
				// Get this field's value from the current row
				String value = (String) row.get(columnName);
				// Trim and put the updated value back in the current row

				if (value != null) {
					String namePinYinHeadCharFirst = getPinYinHeadCharFirst(value);
					row.put(columnName, namePinYinHeadCharFirst);
				}
			}

		}
		return row;
	}

	public static void main(String[] args) {
		// String cnStr = "获取汉语拼音并首字母sdlfksldfsdf嘅囧誰說壞學生來勼髮視頻裆児";
		String cnStr = "NBA2K9";
		Map<String, Object> row = new HashMap<String, Object>();
		row.put("gameName", cnStr);
		row.put("namePinyin", cnStr);
		row.put("namePinYinHeadChar", cnStr);
		row.put("namePinYinHeadCharSep", cnStr);
		row.put("namePinYinHeadCharFirst", cnStr);

		DataConfig.Entity ent = new Entity();
		ArrayList<Map<String, String>> allFieldsList = new ArrayList<Map<String, String>>();

		Map<String, String> field1 = new HashMap<String, String>();
		field1.put("column", "namePinyin");
		field1.put("pinYin", "true");

		Map<String, String> field2 = new HashMap<String, String>();
		field2.put("pinYinHeadChar", "true");
		field2.put("column", "namePinYinHeadChar");

		Map<String, String> field3 = new HashMap<String, String>();
		field3.put("pinYinHeadCharSep", "true");
		field3.put("column", "namePinYinHeadCharSep");

		Map<String, String> field4 = new HashMap<String, String>();
		field4.put("pinYinHeadCharFirst", "true");
		field4.put("column", "namePinYinHeadCharFirst");

		allFieldsList.add(field1);
		allFieldsList.add(field2);
		allFieldsList.add(field3);
		allFieldsList.add(field4);

		ent.allFieldsList = allFieldsList;

		ContextImpl context = new ContextImpl(ent, null, null, null, null,
				null, null);

		PinYinTransformer gamePinYinTransformer = new PinYinTransformer();
		row = gamePinYinTransformer.transformRow(row, context);
		System.out.println(row);
	}

}
