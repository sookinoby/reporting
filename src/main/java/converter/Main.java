package converter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import pojo.reporting.com.r2m.Response;

public class Main {

	
	public static void main(String[] args) {

		converter.FileReader fn = new converter.FileReader();
		Response res = fn.readFileAndReturnJSON("gameData.json");
        fn.construct_result();
        fn.createPdf();
	}

}
