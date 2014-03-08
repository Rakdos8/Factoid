package me.tabinol.factoid.utilities;

import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Dates {
	static Locale locale = Locale.getDefault();
	static Date actuelle = new Date();
	static DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

	public static String date()
	{
            String dat = dateFormat.format(actuelle);
            return dat;
	}
	
	public static String time()
	{
            Calendar cal = Calendar.getInstance();
            cal.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            String dat = sdf.format(cal.getTime());
            return dat;
	}
}
