package PFC.Util;

import java.time.LocalTime;

import javafx.collections.ObservableList;

public class DataOperations {
	
	public static String getValideName(String data)
	{
		String toRet = "";
		String[] tmp;
		
		if(data.indexOf(" ") == 0)
		{
			tmp = data.replaceFirst(" +", "").split(" +");
		}
		else 
		{
			tmp = data.split(" +");
		}
		
		for(String s : tmp)
		{
			toRet = toRet + " " + s;
		}
		
		toRet = toRet.substring(1);
		
		return toRet;
	}
	
	public static String capitalize(String data)
	{
		String toRet = "";
		String[] tmp;
		
		if(data.indexOf(" ") == 0)
		{
			tmp = data.replaceFirst(" +", "").split(" +");
		}
		else 
		{
			tmp = data.split(" +");
		}
		
		for(String s : tmp)
		{
			s = s.replaceFirst(".",s.substring(0,1).toUpperCase());
			toRet = toRet + " " + s;
		}
		
		toRet = toRet.substring(1);
		
		return toRet;
	}
	
	public static LocalTime StringToLocalTime(String data)
	{
		String s = DataOperations.getValideName(data);
		int hour = Integer.valueOf(s.split(":")[0]);
		int minute = Integer.valueOf(s.split(":")[1]);
		return LocalTime.of(hour,minute);
	}
	
	public static String groupesOf(ObservableList<String> listGroupe)
	{
		if(listGroupe.size() > 0)
		{
			listGroupe = listGroupe.sorted();
			String ret = " (";
			ret = ret + listGroupe.get(0);
			for(int i=1;i<listGroupe.size();i++)
			{
				ret = ret + "+" + listGroupe.get(i);
			}
			ret = ret + ")";
			return ret;
		}
		else
		{
			return null;
		}
	}
	
	public static char getLatinAlphabetLettreOf(int number)
	{
		switch(number)
		{
			case 1:		return 'A';
			case 2:		return 'B';
			case 3:		return 'C';
			case 4:		return 'D';
			case 5:		return 'E';
			case 6:		return 'F';
			case 7:		return 'G';
			case 8:		return 'H';
			case 9:		return 'I';
			case 10:	return 'J';
			case 11:	return 'K';
			default:	return ' ';
		}
	}
}
