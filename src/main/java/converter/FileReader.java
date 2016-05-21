package converter;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import com.google.common.io.Files;
import com.google.gson.Gson;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.HorizontalAlignment;
import be.quodlibet.boxable.ImageCell;
import be.quodlibet.boxable.Row;
import be.quodlibet.boxable.VerticalAlignment;
import be.quodlibet.boxable.utils.ImageUtils;
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
private String child_name;
private String child_grade;
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
		 this.child_grade = res.getStudentGrade();
		 this.child_name = res.getStudentName();
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
				{
					Questions fake = new Questions();
					String[] q = {row+"", "+", col+""};
					fake.setQ(q);
					fake.setStudentAnswer("-");
					fake.setRight(false);
					System.out.println("row" + row + "column" + col);
				}
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
				//	System.out.println("par" + data.getTime());
					this.average_time_per_fact = (this.average_time_per_fact * this.no_of_correct + data.getTime()) / (this.no_of_correct+1);
				//	System.out.println("avg" + this.average_time_per_fact );
					this.no_of_correct = this.no_of_correct + 1;
					this.operatorSymbol = data.getQ()[1];
				}

			}
			table_result.add(constructed_result);
		}
		System.out.println(this.average_time_per_fact);
		this.average_time_per_fact = this.average_time_per_fact  / 1000;
		
		this.accuracy = this.no_of_correct / (float)this.questions.size();
		this.accuracy = this.accuracy * 100;
		System.out.println(this.average_time_per_fact);
		System.out.println(this.accuracy);
		
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
	
	public Cell<PDPage> createCell(Row<PDPage> headerRow_summary,float cell_width_summary,String text,float cell_font_summary_size
			, HorizontalAlignment halgin, VerticalAlignment valgin,PDFont font,Color color)
	{
		Cell<PDPage> cell_summary;
		cell_summary = headerRow_summary.createCell(cell_width_summary,text);
		cell_summary.setFontSize(cell_font_summary_size);
		cell_summary.setFont(font);
		cell_summary.setFillColor(color);
		cell_summary.setAlign(halgin);
		cell_summary.setValign(valgin);
		return cell_summary;
		
	}
	

	public void createPdf()
	{
		try {
			// Create a document and add a page to it
			
			PDDocument document = new PDDocument();
			PDPage page = new PDPage();
			page.setMediaBox(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));
			document.addPage(page);
		
			PDFont font = PDType1Font.TIMES_ROMAN;
			
			String path = getClass().getResource("/r2m_logo1.png").getPath();
			File equal_sign = new File(getClass().getResource("/not-equal.png").toURI());
			
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
			contentStream.showText(this.child_name);
			contentStream.endText();
			
			contentStream.beginText();
			contentStream.newLineAtOffset(begin_x,begin_y-20);
			contentStream.showText("Student Grade:");
			contentStream.endText();
			
			contentStream.beginText();
			contentStream.newLineAtOffset(begin_x+100,begin_y-20);
			contentStream.showText(this.child_grade);
			contentStream.endText();
			
			contentStream.drawLine(0, 540, page.getBleedBox().getWidth(), 540);
			contentStream.close();
			
			float margin_summary = 200;
			float tableWidth_summary = page.getMediaBox().getWidth() - (2 * margin_summary);
			float yStartNewPage_summary = page.getMediaBox().getHeight() - (2 * 40);
			float yStart_summary = yStartNewPage_summary;
			float bottomMargin = 10;
			float cell_font_summary = 11.0f;
// Create a new font object selecting one of the PDF base fonts

			// Start a new content stream which will "hold" the to be created content
			float cell_width_summary = 100/2f;

			BaseTable table_summary = new BaseTable(yStart_summary, yStartNewPage_summary, bottomMargin, tableWidth_summary, margin_summary, document, page, true,
					true);
			Cell<PDPage> cell_summary;
			Row<PDPage> headerRow_summary; 
			
			
		
			headerRow_summary = table_summary.createRow(25f);
			cell_summary = createCell(headerRow_summary,cell_width_summary,"Accuracy",cell_font_summary,HorizontalAlignment.CENTER,VerticalAlignment.MIDDLE,PDType1Font.TIMES_BOLD,Color.WHITE);
			cell_summary = createCell(headerRow_summary,cell_width_summary,String.format("%.2f", this.accuracy),cell_font_summary,HorizontalAlignment.CENTER,VerticalAlignment.MIDDLE,PDType1Font.TIMES_BOLD,Color.WHITE);
			
			headerRow_summary = table_summary.createRow(25f);
			cell_summary = createCell(headerRow_summary,cell_width_summary,"Fact per time",cell_font_summary,HorizontalAlignment.CENTER,VerticalAlignment.MIDDLE,PDType1Font.TIMES_BOLD,Color.WHITE);
			cell_summary = createCell(headerRow_summary,cell_width_summary,String.format("%.2f", this.average_time_per_fact),cell_font_summary,HorizontalAlignment.CENTER,VerticalAlignment.MIDDLE,PDType1Font.TIMES_BOLD,Color.WHITE);
	
			table_summary.draw();
		

			//Initialize table
			float margin = 40;
			float top_margin_matrix = 80;
			float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
			float yStartNewPage = page.getMediaBox().getHeight() - (2 * 80);
			float yStart = yStartNewPage;
			float bottomMargin_matrix = 0;
			float cell_font = 11.0f;
// Create a new font object selecting one of the PDF base fonts

			// Start a new content stream which will "hold" the to be created content
			float cell_width = 100/11f;

			BaseTable table = new BaseTable(yStart, yStartNewPage,top_margin_matrix, bottomMargin_matrix, tableWidth, margin, document, page, true,
					true);
			
//Create Header row
			Row<PDPage> headerRow = table.createRow(25f);
			Cell<PDPage> cell;
			cell = createCell(headerRow,cell_width,this.operatorSymbol,cell_font_summary,HorizontalAlignment.CENTER,VerticalAlignment.MIDDLE,PDType1Font.TIMES_BOLD,Color.YELLOW);
		
			for(int i=0; i<questions.size()/10;i++)
			{	
				cell = createCell(headerRow,cell_width,Integer.toString(i),cell_font_summary,HorizontalAlignment.CENTER,VerticalAlignment.MIDDLE,PDType1Font.TIMES_BOLD,Color.YELLOW);
			}
			table.addHeaderRow(headerRow);
			int m =0;
			for (ArrayList<Questions> questions_row: table_result) {
					Row<PDPage> row = table.createRow(25f);
					cell = createCell(row,cell_width,Integer.toString(m),cell_font,HorizontalAlignment.CENTER,VerticalAlignment.MIDDLE,PDType1Font.TIMES_BOLD,Color.YELLOW);
					
				for(Questions q: questions_row) {
					cell = createCell(row,cell_width,q.getStudentAnswer(),cell_font,HorizontalAlignment.CENTER,VerticalAlignment.MIDDLE,PDType1Font.TIMES_BOLD,Color.YELLOW);
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
		
			table.draw(); 
			float margin_2 = 100;
			float margin_top_error = 200;
			float tableWidth_2 = page.getMediaBox().getWidth() - (2 * margin_2);
			float cell_width_2 = 100/5f; // the cell width is calculated in percentage . So its 100 / 5 , not the table width / 5;
			float cell_height_2 = 25f;
			float yStart_second = 100;
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
			//	int q =0;
				headerRow_error = table2.createRow(cell_height_2);
				cell2 = createCell(headerRow_error,100,"List Of Wrong Facts",cell_font_2,HorizontalAlignment.CENTER,VerticalAlignment.MIDDLE,PDType1Font.TIMES_BOLD,Color.WHITE);
			//	table2.addHeaderRow(headerRow_error);
			for(Wrong  w:this.wrong_list)
			{
				Row<PDPage> row = table2.createRow(cell_height_2);
				
				cell2 = createCell(row,cell_width_2,w.getQ1(),cell_font_2,HorizontalAlignment.CENTER,VerticalAlignment.MIDDLE,PDType1Font.TIMES_BOLD,Color.WHITE);
			//	System.out.println(cell2.getHeight());
			//	q++;
				
				cell2 = createCell(row,cell_width_2,w.getOp(),cell_font_2,HorizontalAlignment.CENTER,VerticalAlignment.MIDDLE,PDType1Font.TIMES_BOLD,Color.WHITE);
				cell2 = createCell(row,cell_width_2,w.getQ2(),cell_font_2,HorizontalAlignment.CENTER,VerticalAlignment.MIDDLE,PDType1Font.TIMES_BOLD,Color.WHITE);
				ImageCell  cell_image;
				cell_image = row.createImageCell(cell_width_2, ImageUtils.readImage(equal_sign));
				cell_image.setAlign(HorizontalAlignment.CENTER);
				cell_image.setValign(VerticalAlignment.MIDDLE);
				//cell2 = createCell(row,cell_width_2,"\u2260",cell_font_2,HorizontalAlignment.CENTER,VerticalAlignment.MIDDLE,font2,Color.WHITE);
				cell2 = createCell(row,cell_width_2,w.getStudentAnswer(),cell_font_2,HorizontalAlignment.CENTER,VerticalAlignment.MIDDLE,PDType1Font.TIMES_BOLD,Color.WHITE);

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


