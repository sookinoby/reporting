package converter;

import com.google.common.io.Files;
import com.google.gson.Gson;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import pojo.reporting.com.r2m.Questions;
import pojo.reporting.com.r2m.Response;
import pojo.reporting.com.r2m.Wrong;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class FileReader {
private String fileName;
private ArrayList<Questions> questions;
private ArrayList<Questions> constructed_result;
private ArrayList<ArrayList<Questions>> table_result;
private ArrayList<Wrong> wrong_list;
private int no_of_correct = 0;
float average_time_per_fact = 0;
private String operatorSymbol;
private	float accuracy;
public Response readFileAndReturnJSON(String fn) 
{
	this.fileName = fn;
	Gson gson = null;
	BufferedReader br = null;
	InputStreamReader in = null;
	try {
		 gson = new Gson();


		 in = new InputStreamReader(getClass().getResourceAsStream("/" +this.fileName));

		  br = new BufferedReader(in);


		 Response res = gson.fromJson(br, Response.class);
		 this.questions = res.getGameData().getQuestionList();
		 return res;
	}
	catch(Exception e)
	{
		System.out.println(in);
		System.out.println(e.toString());
		return null;
	}
}

	public void construct_result()
	{
		table_result = new ArrayList<ArrayList<Questions>>();
		wrong_list = new ArrayList<Wrong>();
		for(int row=0; row < this.questions.size()/10 ;row++)
		{
			constructed_result = new ArrayList<Questions>();
			for(int col=0; col < this.questions.size() /10; col++)
			{
				Questions data = this.findInQuestionList( Integer.toString(row), Integer.toString(col));
				if(data == null)
					System.out.println("row" + row + "column" + col);
				//System.out.println("row" + row + "column" + col);
				constructed_result.add(data);
			//	System.out.println(data.isRight());
				if(!data.isRight())
				{

					Wrong wrong = new Wrong();
					wrong.setQ1(data.getQ()[0]);
					wrong.setQ2(data.getQ()[2]);
					wrong.setOp(data.getQ()[1]);
					wrong.setStudentAnswer(data.getStudentAnswer());
					wrong_list.add(wrong);
					this.operatorSymbol = data.getQ()[1];
				}
				else if(data.isRight()) {

					this.average_time_per_fact = (this.average_time_per_fact * this.no_of_correct + data.getTime()) / (this.average_time_per_fact+1);
					this.no_of_correct = this.no_of_correct + 1;
					this.operatorSymbol = data.getQ()[1];
				}

			}
			table_result.add(constructed_result);
		}
		this.average_time_per_fact = Math.round( (this.average_time_per_fact / 1000) * 100) / 100;
		this.accuracy = Math.round((this.no_of_correct / this.questions.size()) * 100);
	}

	public Questions findInQuestionList(String mapRow,String mapCol)
	{
		for(int i=0;i<this.questions.size();i++)
		{
			// console.log(this.questionList[i]);
			if(this.questions.get(i).getMapCol().equals( mapCol) && this.questions.get(i).getMapRow().equals(mapRow) )
				return this.questions.get(i);
		}
		return null;
	}

	public void createPdf()
	{
		try {
			// Create a document and add a page to it
			PDDocument document = new PDDocument();
			PDPage page = new PDPage();
			page.setMediaBox(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));
			document.addPage(page);
			PDFont font = PDType1Font.HELVETICA_BOLD;
			String abc = getClass().getResource("/r2m_logo2.png").toString();
			BufferedImage bckimg = ImageIO.read(new File("/Users/alexaravind/Desktop/ideaProjects/reporting/target/classes/r2m_logo2.png"));


		//	PDImageXObject pdImage = PDImageXObject.createFromFile(getClass().getResource("/r2m_logo2.png").toString(), document);
			PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true);
			float scale = 1;
		//	contentStream.drawImage(pdImage, 20, 560, pdImage.getWidth()*scale, pdImage.getHeight()*scale);
			contentStream.beginText();
			contentStream.setFont(font, 20);
			contentStream.newLineAtOffset(5,560);
		//	contentStream.showText("Hello World");
			contentStream.newLine();

			contentStream.endText();
			contentStream.drawLine(0, 530, PDRectangle.A4.getWidth(), 530);
			contentStream.close();

			/*

			//Initialize table
			float margin = 40;
			float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
			float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
			float yStart = yStartNewPage;
			float bottomMargin = 0;
// Create a new font object selecting one of the PDF base fonts

			// Start a new content stream which will "hold" the to be created content
			float cell_width = 100/11f;

			BaseTable table = new BaseTable(yStart, yStartNewPage, bottomMargin, 600, margin, document, page, true,
					true);



//Create Header row
			Row<PDPage> headerRow = table.createRow(25f);
			Cell<PDPage> cell;
			cell = headerRow.createCell(cell_width, this.operatorSymbol);
			cell.setFont(PDType1Font.HELVETICA_BOLD);
			cell.setFillColor(Color.YELLOW);
			cell.setAlign(HorizontalAlignment.CENTER);
			cell.setValign(VerticalAlignment.MIDDLE);
			for(int i=0; i<questions.size()/10;i++)
			{
				cell = headerRow.createCell(cell_width, Integer.toString(i));
				cell.setFont(PDType1Font.HELVETICA_BOLD);
				cell.setFillColor(Color.YELLOW);
				cell.setAlign(HorizontalAlignment.CENTER);
				cell.setValign(VerticalAlignment.MIDDLE);
			}
			table.addHeaderRow(headerRow);
			int m =0;
			for (ArrayList<Questions> questions_row: table_result) {
				Row<PDPage> row = table.createRow(25f);
					cell = row.createCell(cell_width,Integer.toString(m));
					cell.setValign(VerticalAlignment.MIDDLE);
					cell.setAlign(HorizontalAlignment.CENTER);
					cell.setFillColor(Color.WHITE);

				for(Questions q: questions_row) {

					cell = row.createCell(cell_width, q.getStudentAnswer());
					if(q.isRight()) {
						cell.setFillColor(Color.GREEN);
					}
					else {
						cell.setFillColor(Color.PINK);
					}
					cell.setValign(VerticalAlignment.MIDDLE);
					cell.setAlign(HorizontalAlignment.CENTER);
				}
				m++;
			}

			table.draw(); */
			File file = new File("TestSimple.pdf");
			System.out.println("Sample file saved at : " + file.getAbsolutePath());
			Files.createParentDirs(file);
			document.save(file);
			document.close();
		}
		catch(Exception e)
		{
			System.out.println(e.toString());
		}

	}

}


