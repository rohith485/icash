package com.icashflow.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
import java.util.StringJoiner;

import org.apache.commons.io.FilenameUtils;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.icashflow.batch.item.excel.AbstractExcelItemReader;
import com.icashflow.batch.item.excel.RowCallbackHandler;
import com.icashflow.batch.item.excel.Sheet;
import com.icashflow.batch.item.excel.mapping.PassThroughRowMapper;
import com.icashflow.batch.item.excel.poi.PoiItemReader;
import com.icashflow.batch.item.excel.support.rowset.RowSet;
import com.icashflow.command.BuyerIcashCommand;
import com.icashflow.command.SellerIcashCommand;

@Controller
public class UploadController {

    @GetMapping("/")
    public String index(Model model) {
    	BuyerIcashCommand buyerIcashCommand = new BuyerIcashCommand();
    	model.addAttribute("buyerIcashCommand", buyerIcashCommand);
    	
    	SellerIcashCommand sellerIcashCommand = new SellerIcashCommand();
    	model.addAttribute("sellerIcashCommand", sellerIcashCommand);
    	
        return "upload";
    }

    private String dbURL = "jdbc:mysql://127.0.0.1:3306/filedb";
    private String dbUser = "root";
    private String dbPass = "admin";
    
    
    @PostMapping("/updatebuyerdata")
	public String processBuyerInput(@ModelAttribute("buyerIcashCommand") BuyerIcashCommand buyerIcashCommand, RedirectAttributes redirectAttributes) {
		MultipartFile file = buyerIcashCommand.getFile();
		if (file.isEmpty()) {
			redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
			return "redirect:uploadStatus";
		}
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPass);
			String query = " insert into UPLOADED_FILES (FILE_NAME,FILE_TYPE,FILE_DATA,"
					+ "UPLOADED_USER_ID,UPLOADED_DATE,FILE_SIZE, FILE_STATUS,"
					+ "MINIMUM_ROI,DESIRED_ROI, MINIMUM_RESERVE_AMOUNT, MAXIMUM_RESERVE_AMOUNT)"
					+ " values (?, ? ,? ,? ,? ,? ,? ,? ,? ,? ,?)";
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString(1, file.getOriginalFilename());
			preparedStmt.setString(2, FilenameUtils.getExtension(file.getOriginalFilename()));
			preparedStmt.setBytes(3, file.getBytes());
			preparedStmt.setString(4, "ROHITH");
			java.util.Date today = new java.util.Date();
			preparedStmt.setDate(5, new java.sql.Date(today.getTime()));
			preparedStmt.setLong(6, file.getSize());
			preparedStmt.setString(7, "ACTIVE");
			preparedStmt.setDouble(8, buyerIcashCommand.getMroi());
			preparedStmt.setDouble(9, buyerIcashCommand.getDroi());
			preparedStmt.setDouble(10, buyerIcashCommand.getMinReserveAmount());
			preparedStmt.setDouble(11, buyerIcashCommand.getMaxReserveAmount());
			preparedStmt.execute();
			conn.close();
			redirectAttributes.addFlashAttribute("message",
					"You successfully uploaded '" + file.getOriginalFilename() + "'");
		} catch (Exception e) {
			System.out.println("Exception occured " + e);
		}

		return "redirect:/uploadStatus";
	}
    
    @PostMapping("/updatesellerdata")
	public String processSellerInput(@ModelAttribute("sellerIcashCommand") SellerIcashCommand sellerIcashCommand, RedirectAttributes redirectAttributes) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPass);
			String query = " insert into SELLER_INPUTS (SELLER_ID,MIN_DISCOUNT,MAX_DISCOUNT,UPLOADED_DATE,DESIRED_PAYMEMT_DATE)"
					+ " values (? ,? ,? ,? ,? )";
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString(1, sellerIcashCommand.getSellerId());
			preparedStmt.setDouble(2, sellerIcashCommand.getMinDiscount());
			preparedStmt.setDouble(3, sellerIcashCommand.getMaxDiscount());
			java.util.Date today = new java.util.Date();
			preparedStmt.setDate(4, new java.sql.Date(today.getTime()));
			Date dt = new Date();
			Calendar c = Calendar.getInstance(); 
			c.setTime(dt); 
			c.add(Calendar.DATE, 3);
			dt = c.getTime();
			preparedStmt.setDate(5, new java.sql.Date(dt.getTime()));
			preparedStmt.execute();
			conn.close();
			redirectAttributes.addFlashAttribute("message",
					"You successfully entered Discount details");
		} catch (Exception e) {
			System.out.println("Exception occured " + e);
		}

		return "redirect:/uploadStatus";
	}
    
    //@RequestMapping(value = "/upload", method = RequestMethod.POST)
    @PostMapping("/upload") //new annotation since 4.3
    public String singleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            return "redirect:uploadStatus";
        }
        
        try {
        	
        	AbstractExcelItemReader itemReader = new PoiItemReader<>();
            itemReader.setLinesToSkip(1); //First line is column names
            itemReader.setRowMapper(new PassThroughRowMapper());
            itemReader.setSkippedRowsCallback(new RowCallbackHandler() {

                public void handleRow(RowSet rs) {
                    System.out.println("Skipping: " + StringUtils.arrayToCommaDelimitedString(rs.getCurrentRow()));
                }
            });
            
            Class.forName("com.mysql.jdbc.Driver");  
            
            Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPass);
            
            Statement stmt=conn.createStatement();  
            //ResultSet rs = stmt.executeQuery("select * from files_upload");  
            
         // create a sql date object so we can use it in our INSERT statement
            Calendar calendar = Calendar.getInstance();
            java.sql.Date startDate = new java.sql.Date(calendar.getTime().getTime());

            // the mysql insert statement
            String query = " insert into files_upload (file_name, file_data)"
              + " values (?, ?)";
            
            query = " insert into UPLOADED_FILES (FILE_NAME,FILE_TYPE,FILE_DATA,UPLOADED_USER_ID,UPLOADED_DATE,FILE_SIZE, FILE_STATUS)"
            		+ " values (?, ? ,? ,? ,? ,? ,? )";

            // create the mysql insert preparedstatement
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setString (1, file.getOriginalFilename());
            preparedStmt.setString(2, FilenameUtils.getExtension(file.getOriginalFilename()));
            preparedStmt.setBytes(3, file.getBytes());
            preparedStmt.setString(4, "ROHITH");
            java.util.Date today = new java.util.Date();
            preparedStmt.setDate(5, new java.sql.Date(today.getTime()));
            preparedStmt.setLong(6, file.getSize());
            preparedStmt.setString (7, "ACTIVE");
            // execute the preparedstatement
            preparedStmt.execute();
            
            conn.close();
            
            Resource resource = new InputStreamResource(file.getInputStream(), "File uploaded by user");
            itemReader.setResource(resource);
            itemReader.afterPropertiesSet();
            ExecutionContext executionContext = new ExecutionContext();
            itemReader.open(executionContext);
            int numberOfSheets = itemReader.getNumberOfSheets();
            
            for(int i=0 ; i< numberOfSheets; i++) {
            	Sheet sheet = itemReader.getSheet(i);
            	System.out.println(sheet.getName());
            	for(int j=0; j< sheet.getNumberOfRows() ; j++) {
            		String[] row = sheet.getRow(j);
            		System.out.println("Read: " + StringUtils.arrayToCommaDelimitedString(row));
            	}
            }
            
            /*
            String[] row;
            do {
                row = (String[]) itemReader.read();
                System.out.println("Read: " + StringUtils.arrayToCommaDelimitedString(row));
                if (row != null) {
                    //assertEquals(6, row.length);
                }
            } while (row != null);*/
            //int readCount = (Integer) ReflectionTestUtils.getField(itemReader, "currentItemCount" );
            //assertEquals(4321, readCount);
        
        
        	// Creates a workbook object from the uploaded excelfile
     		/*HSSFWorkbook workbook = new HSSFWorkbook(file.getInputStream());
     		int numberOfSheets = workbook.getNumberOfSheets();
            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
            Files.write(path, bytes);*/

            redirectAttributes.addFlashAttribute("message", "You successfully uploaded '" + file.getOriginalFilename() + "'");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/uploadStatus";
    }

    @PostMapping("/uploadMulti")
    public String multiFileUpload(@RequestParam("files") MultipartFile[] files,
                                  RedirectAttributes redirectAttributes) {

        StringJoiner sj = new StringJoiner(" , ");

        for (MultipartFile file : files) {

            if (file.isEmpty()) {
                continue; //next pls
            }

            try {

                byte[] bytes = file.getBytes();
                Path path = null;//Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
                Files.write(path, bytes);

                sj.add(file.getOriginalFilename());

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        String uploadedFileName = sj.toString();
        if (StringUtils.isEmpty(uploadedFileName)) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
        } else {
            redirectAttributes.addFlashAttribute("message", "You successfully uploaded '" + uploadedFileName + "'");
        }

        return "redirect:/uploadStatus";

    }


    @GetMapping("/uploadStatus")
    public String uploadStatus() {
        return "uploadStatus";
    }

    @GetMapping("/uploadMultiPage")
    public String uploadMultiPage() {
        return "uploadMulti";
    }

}