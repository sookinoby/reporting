package converter;

import pojo.reporting.com.r2m.Response;

public class Main {

	
	public static void main(String[] args) {

		converter.FileReader fn = new converter.FileReader();
		Response res = fn.readFileAndReturnJSON("gameData.json");
        fn.construct_result();
        fn.createPdf();
	//	System.out.println(res);
   //     System.out.println("\u2260");


// Define a text content stream using the selected font, moving the cursor and drawing the text "Hello World"

		
	}

}
