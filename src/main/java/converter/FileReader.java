package converter;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.google.gson.Gson;

import pojo.reporting.com.r2m.Response;

public class FileReader {
private String fileName;

public Response readFileAndReturnJSON(String fn) 
{
	this.fileName = fn;
	try {
		Gson gson = new Gson();
		 BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/"+this.fileName)));
		 Response res = gson.fromJson(br, Response.class);
		 return res;
	}
	catch(Exception e)
	{
		System.out.println(e);
		return null;
	}
}

}


