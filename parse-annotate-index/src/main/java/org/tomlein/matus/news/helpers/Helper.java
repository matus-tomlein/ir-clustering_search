package org.tomlein.matus.news.helpers;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;


public class Helper {
	public static Date parseDate(final String str) {
		Calendar c = DatatypeConverter.parseDateTime(str);
		return c.getTime();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map sortMapByValues(Map unsortMap, boolean ascending) {

		List list = new LinkedList(unsortMap.entrySet());

		Comparator comparator = null;
		if (ascending) {
			comparator = new Comparator() {
				public int compare(Object o1, Object o2) {
					return ((Comparable) ((Map.Entry) (o1)).getValue())
							.compareTo(((Map.Entry) (o2)).getValue());
				}
			};
		} else {
			comparator = new Comparator() {
				public int compare(Object o1, Object o2) {
					return ((Comparable) ((Map.Entry) (o2)).getValue())
							.compareTo(((Map.Entry) (o1)).getValue());
				}
			};
		}

		// sort list based on comparator
		Collections.sort(list, comparator);

		// put sorted list into map again
		//LinkedHashMap make sure order in which keys were inserted
		Map sortedMap = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
}
