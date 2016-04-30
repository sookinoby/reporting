package converter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;

import org.omg.CORBA.portable.InputStream;

import pojo.reporting.com.r2m.Response;

public class Main {
	
	
	public static void main(String[] args) {
		System.out.println("test");
		converter.FileReader fn = new converter.FileReader();
		Response res = fn.readFileAndReturnJSON("gameData.json");
		
	}

}
