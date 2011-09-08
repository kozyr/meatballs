package com.github.kozyr.android.meatballs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class Util {
	public static String fetchData(String strUrl) throws IOException {
		BufferedReader reader = null;
		StringBuilder sb = new StringBuilder();
		try {
            URL url = new URL(strUrl);
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            
        } finally {
        	if (reader != null) {
        		try {
					reader.close();
				} catch (IOException e) {
					
				}
        	}
        }
        
        return sb.toString();
	}
	
	public static String convertToUrl(String s) {
		if (s != null) {
			return s.replace(" ", "%20");
		} else {
			return "";
		}
	}
	
	public static String removeHtml(String instructions) {
		return instructions.replaceAll("\\<[^>]*>","");
	}
}
