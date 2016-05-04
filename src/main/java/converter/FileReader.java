package converter;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import com.google.common.io.Files;
import com.google.gson.Gson;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.HorizontalAlignment;
import be.quodlibet.boxable.Row;
import be.quodlibet.boxable.VerticalAlignment;
import pojo.reporting.com.r2m.Questions;
import pojo.reporting.com.r2m.Response;
import pojo.reporting.com.r2m.Wrong;

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
			String path = getClass().getResource("/r2m_logo1.png").getPath();
		//	BufferedImage bckimg = ImageIO.read(new File("/Users/alexaravind/Desktop/ideaProjects/reporting/target/classes/r2m_logo2.png"));

		
			PDImageXObject pdImage = PDImageXObject.createFromFile(path, document);
			PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true);
			float scale = 0.75f;
			int begin_x = 570;
			int begin_y = 570;
			contentStream.drawImage(pdImage, 30, 530, pdImage.getWidth()*scale, pdImage.getHeight()*scale);
			contentStream.beginText();
			contentStream.setFont(font, 12.0f);
			contentStream.newLineAtOffset(begin_x,begin_y);
			contentStream.showText("Student Name:");
			contentStream.endText();
			
			contentStream.beginText();
			contentStream.newLineAtOffset(begin_x + 100,begin_y);
			contentStream.showText("Suresh");
			contentStream.endText();
			
			contentStream.beginText();
			contentStream.newLineAtOffset(begin_x,begin_y-20);
			contentStream.showText("Student Grade:");
			contentStream.endText();
			
			contentStream.beginText();
			contentStream.newLineAtOffset(begin_x+100,begin_y-20);
			contentStream.showText("4");
			contentStream.endText();
			
			contentStream.drawLine(0, 540, page.getBleedBox().getWidth(), 540);
			contentStream.close();
			
			float margin = 200;
			float tableWidth_summary = page.getMediaBox().getWidth() - (2 * margin);
			float yStartNewPage_summary = page.getMediaBox().getHeight() - (2 * 40);
			float yStart_summary = yStartNewPage_summary;
			float bottomMargin = 10;
			float cell_font = 11.0f;
// Create a new font object selecting one of the PDF base fonts

			// Start a new content stream which will "hold" the to be created content
			float cell_width_summary = 100/2f;

			BaseTable table_summary = new BaseTable(yStart_summary, yStartNewPage_summary, bottomMargin, tableWidth_summary, margin, document, page, true,
					true);
			Row<PDPage> headerRow_summary; 
			headerRow_summary = table_summary.createRow(25f);
			
			Cell<PDPage> cell_summary;
			cell_summary = headerRow_summary.createCell(cell_width_summary, "Accuracy");
			cell_summary.setFontSize(cell_font);
			cell_summary.setFont(PDType1Font.HELVETICA_BOLD);
			
			cell_summary.setFillColor(Color.WHITE);
			cell_summary.setAlign(HorizontalAlignment.CENTER);
			cell_summary.setValign(VerticalAlignment.MIDDLE);
			//
			cell_summary = headerRow_summary.createCell(cell_width_summary, "12.9");
			cell_summary.setFontSize(cell_font);
			cell_summary.setFont(PDType1Font.HELVETICA_BOLD);
			
			cell_summary.setFillColor(Color.WHITE);
			cell_summary.setAlign(HorizontalAlignment.CENTER);
			cell_summary.setValign(VerticalAlignment.MIDDLE);
			
			headerRow_summary = table_summary.createRow(25f);
			cell_summary = headerRow_summary.createCell(cell_width_summary, "Fact per time");
			cell_summary.setFontSize(cell_font);
			cell_summary.setFont(PDType1Font.HELVETICA_BOLD);
			
			cell_summary.setFillColor(Color.WHITE);
			cell_summary.setAlign(HorizontalAlignment.CENTER);
			cell_summary.setValign(VerticalAlignment.MIDDLE);
			
			cell_summary = headerRow_summary.createCell(cell_width_summary, "12.9");
			cell_summary.setFontSize(cell_font);
			cell_summary.setFont(PDType1Font.HELVETICA_BOLD);
			
			cell_summary.setFillColor(Color.WHITE);
			cell_summary.setAlign(HorizontalAlignment.CENTER);
			cell_summary.setValign(VerticalAlignment.MIDDLE);
			table_summary.draw();
		
/*
			//Initialize table
			float margin = 40;
			float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
			float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
			float yStart = yStartNewPage;
			float bottomMargin = 0;
			float cell_font = 11.0f;
// Create a new font object selecting one of the PDF base fonts

			// Start a new content stream which will "hold" the to be created content
			float cell_width = 100/11f;

			BaseTable table = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, document, page, true,
					true);
			



//Create Header row
			Row<PDPage> headerRow = table.createRow(25f);
			Cell<PDPage> cell;
			cell = headerRow.createCell(cell_width, this.operatorSymbol);
			cell.setFontSize(cell_font);
			cell.setFont(PDType1Font.HELVETICA_BOLD);
			
			cell.setFillColor(Color.YELLOW);
			cell.setAlign(HorizontalAlignment.CENTER);
			cell.setValign(VerticalAlignment.MIDDLE);
			for(int i=0; i<questions.size()/10;i++)
			{	
				
				cell = headerRow.createCell(cell_width, Integer.toString(i));
				cell.setFontSize(cell_font);
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
					cell.setFontSize(cell_font);
				for(Questions q: questions_row) {

					cell = row.createCell(cell_width, q.getStudentAnswer());
					cell.setFontSize(cell_font);
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
		*/
			//table.draw(); 
			float margin_2 = 100;
			float tableWidth_2 = page.getMediaBox().getWidth() - (2 * margin_2);
			float cell_width_2 = 100/5f; // the cell width is calculated in percentage . So its 100 / 5 , not the table width / 5;
			float cell_height_2 = 25f;
			float yStart_second = 200;
			float yStartNewPage_second = page.getMediaBox().getHeight() - (2 * 40);
			/*
			float yStartNewPage_second = page.getMediaBox().getHeight() - (2 * margin_2);;
			float yStart_second = page.getMediaBox().getHeight() - (2 * margin_2);;*/
			float cell_font_2 = 11.0f;
			BaseTable table2 = new BaseTable(yStart_second, yStartNewPage_second, bottomMargin, tableWidth_2, margin_2, document, page, true,
					true);
		
			//Row<PDPage> headerRow2 = table2.createRow(35f);
			Cell<PDPage> cell2;
			Row<PDPage> headerRow_error;
				int q =0;
				headerRow_error = table2.createRow(cell_height_2);
				cell2 = headerRow_error.createCell(100, "List Of Wrong Facts");
				cell2.setFont(PDType1Font.HELVETICA_BOLD);
				cell2.setValign(VerticalAlignment.MIDDLE);
				cell2.setAlign(HorizontalAlignment.CENTER);
				cell2.setFillColor(Color.BLACK);
				cell2.setTextColor(Color.WHITE);
				cell2.setFontSize(cell_font_2);	
				table2.addHeaderRow(headerRow_error);

			for(Wrong  w:this.wrong_list)
			{
				Row<PDPage> row = table2.createRow(cell_height_2);
				
				cell2 = row.createCell(cell_width_2,w.getQ1());
				cell2.setFillColor(Color.WHITE);
				cell2.setFontSize(cell_font_2);
				cell2.setValign(VerticalAlignment.MIDDLE);
				cell2.setAlign(HorizontalAlignment.CENTER);
				
			
				
				System.out.println(cell2.getHeight());
				q++;
				
			
				cell2 = row.createCell(cell_width_2,w.getOp());
				cell2.setValign(VerticalAlignment.MIDDLE);
				cell2.setAlign(HorizontalAlignment.CENTER);
				cell2.setFillColor(Color.WHITE);
				cell2.setFontSize(cell_font_2);
				
				cell2 = row.createCell(cell_width_2,w.getQ2());
				cell2.setValign(VerticalAlignment.MIDDLE);
				cell2.setAlign(HorizontalAlignment.CENTER);
				cell2.setFillColor(Color.WHITE);
				cell2.setFontSize(cell_font_2);
				
				cell2 = row.createCell(cell_width_2,"1");
				cell2.setValign(VerticalAlignment.MIDDLE);
				cell2.setAlign(HorizontalAlignment.CENTER);
				cell2.setFillColor(Color.WHITE);
				cell2.setFontSize(cell_font_2);
				

				cell2 = row.createCell(cell_width_2,w.getStudentAnswer());
				cell2.setValign(VerticalAlignment.MIDDLE);
				cell2.setAlign(HorizontalAlignment.CENTER);
				cell2.setFillColor(Color.WHITE);
				cell2.setFontSize(cell_font_2); 
			}

			table2.draw(); 
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


