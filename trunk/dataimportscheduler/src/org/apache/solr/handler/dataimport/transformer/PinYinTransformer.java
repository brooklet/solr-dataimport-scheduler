package org.apache.solr.handler.dataimport.transformer;

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
import org.apache.solr.handler.dataimport.Transformer;

public class PinYinTransformer extends Transformer {

	private String getPingYin(String src) {
		char[] t1 = null;
		t1 = src.toCharArray();
		String[] t2 = new String[t1.length];
		HanyuPinyinOutputFormat t3 = new HanyuPinyinOutputFormat();
		t3.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		t3.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		t3.setVCharType(HanyuPinyinVCharType.WITH_V);
		String t4 = "";
		int t0 = t1.length;
		try {
			for (int i = 0; i < t0; i++) {
				// 判断是否为汉字字符
				if (java.lang.Character.toString(t1[i]).matches(
						"[\\u4E00-\\u9FA5]+")) {
					t2 = PinyinHelper.toHanyuPinyinStringArray(t1[i], t3);
					t4 += t2[0];
					t4 += " ";
				} else
					t4 += java.lang.Character.toString(t1[i]);
			}
			return t4.trim();
		} catch (BadHanyuPinyinOutputFormatCombination e1) {
			e1.printStackTrace();
		}
		return t4.trim();
	}

	// 返回中文的首字母
	private String getPinYinHeadChar(String str) {
		String convert = "";
		for (int j = 0; j < str.length(); j++) {
			char word = str.charAt(j);
			String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
			if (pinyinArray != null) {
				convert += pinyinArray[0].charAt(0);
			} else {
				convert += word;
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
					row.put(columnName, namePinyin.trim());
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
					String namePinYinHeadChar = getPinYinHeadChar(value);
					row.put(columnName, namePinYinHeadChar.trim());
				}
			}
		}
		return row;
	}
	
/*
	public Map<String, Object> transformRow(Map<String, Object> row) {
		String gameName = (String) row.get("gameName");
		if (gameName != null) {
			String namePinyin = getPingYin(gameName);
			String namePinYinHeadChar = getPinYinHeadChar(gameName);
			row.put("namePinyin", namePinyin.trim());
			row.put("namePinYinHeadChar", namePinYinHeadChar.trim());
		}
		return row;
	}

	public static void main(String[] args) {
		String cnStr = "中华人民共和国sdlfksldfsdf嘅囧誰說壞學生來勼髮視頻裆児";
		Map<String, Object> row = new HashMap<String, Object>();
		row.put("gameName", cnStr);

		GamePinYinTransformer gamePinYinTransformer = new GamePinYinTransformer();
		row = gamePinYinTransformer.transformRow(row);
		System.out.println(row);
	}
	*/
}
