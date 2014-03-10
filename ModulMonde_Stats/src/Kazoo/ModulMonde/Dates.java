package Kazoo.ModulMonde;

import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Dates
{
//	* Choix de la langue francaise
	static Locale locale = Locale.getDefault();
	static Date actuelle = new Date();
	
//	* Definition du format utilise pour les dates
	static DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

//	* Donne la date au format "aaaa-mm-jj"
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