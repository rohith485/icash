package com.icashflow.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringJoiner;
import java.util.stream.Collectors;

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
import com.icashflow.to.AwardsFileTO;
import com.icashflow.to.InvoiceDetails;
import com.icashflow.to.SellerInputDetails;

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
        	AbstractExcelItemReader itemReader = new PoiItemReader<>();
            itemReader.setRowMapper(new PassThroughRowMapper());
            Resource resource = new InputStreamResource(file.getInputStream(), "File uploaded by user");
            itemReader.setResource(resource);
            itemReader.afterPropertiesSet();
            ExecutionContext executionContext = new ExecutionContext();
            itemReader.open(executionContext);
            int numberOfSheets = itemReader.getNumberOfSheets();
            Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPass);
            for(int i=0 ; i< numberOfSheets; i++) {
            	Sheet sheet = itemReader.getSheet(i);
            	// skipping Zeroth row as it contains column names
            	for(int j=1; j< sheet.getNumberOfRows() ; j++) {
            		String[] row = sheet.getRow(j);
            		if(row.length < 3) {
            			// TODO throw exception
            		}
            		int sellerInvoiceId =Double.valueOf((row[0].trim())).intValue();
            		Double invoiceAmount = Double.valueOf(row[1]);
            		int invoiceDueDays = Double.valueOf((row[2])).intValue();
            		String supplierName = sheet.getName().trim().replaceAll(".0", "");
            		System.out.println(sheet.getName());
            		
            		String invoiceInsertQuery = " insert into INVOICE_DETAILS (SUPPLIER_INVOICE_ID,INVOICE_AMOUNT, "
            				+ "ORIGINAL_DUE_DATE ,BUYER_KEY, SELLER_KEY, ENTRY_DATE)"
            				+ "values(? ,? ,? ,? ,? ,?)";
            		Date dt = new Date();
        			Calendar c = Calendar.getInstance(); 
        			c.setTime(dt); 
        			c.add(Calendar.DATE, invoiceDueDays);
        			dt = c.getTime();
        			
        			PreparedStatement preparedStmt = conn.prepareStatement(invoiceInsertQuery);
        			preparedStmt.setInt(1, sellerInvoiceId);
        			preparedStmt.setDouble(2, invoiceAmount);
        			preparedStmt.setDate(3, new java.sql.Date(dt.getTime()));
        			preparedStmt.setString(4, "BUYER1");
        			preparedStmt.setString(5, sheet.getName());
        			java.util.Date today = new java.util.Date();
        			preparedStmt.setDate(6, new java.sql.Date(today.getTime()));
        			preparedStmt.execute();
        			System.out.println("Read: " + StringUtils.arrayToCommaDelimitedString(row));
            	}
            }
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
    
    
    
    @GetMapping("/generateawardsfile")
	public String generateawardsfile(RedirectAttributes redirectAttributes) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPass);
			AwardsFileTO awardsFileTO = new AwardsFileTO();
			String userNameQuery = "select ICASH_LOGIN_USER_NAME from user_credentials where ICASH_LOGIN_KEY = 'BUYER1'";
			PreparedStatement loginNamepreparedStmt = conn.prepareStatement(userNameQuery);
			ResultSet loginResultSet = loginNamepreparedStmt.executeQuery();
			String userName = null;
			while(loginResultSet.next()) {
				userName = loginResultSet.getString("ICASH_LOGIN_USER_NAME");
			}
			
			
			String invoiceDetailsQuery = "select SUPPLIER_INVOICE_ID,ORIGINAL_DUE_DATE,INVOICE_DATE,INVOICE_AMOUNT,ENTRY_DATE,BUYER_KEY,SELLER_KEY from INVOICE_DETAILS where ENTRY_DATE = '2017-12-26 00:00:00';";
			PreparedStatement invoiceDetailsPrepareStmt = conn.prepareStatement(invoiceDetailsQuery);
			java.util.Date today = new java.util.Date();
			//invoiceDetailsPrepareStmt.setDate(1, new java.sql.Date(today.getTime()));
			System.out.println(invoiceDetailsPrepareStmt.toString());
			ResultSet invoiceResultSet = invoiceDetailsPrepareStmt.executeQuery();
			List<InvoiceDetails> invoiceDetailsList = new ArrayList<>();
			while(invoiceResultSet.next()) {
				InvoiceDetails invoiceDetails = new InvoiceDetails();
				invoiceDetails.setBuyerKey(invoiceResultSet.getString("BUYER_KEY"));
				invoiceDetails.setEntryDate(invoiceResultSet.getDate("ENTRY_DATE"));
				invoiceDetails.setInvoiceAmount(invoiceResultSet.getDouble("INVOICE_AMOUNT"));
				invoiceDetails.setInvoiceDate(invoiceResultSet.getDate("INVOICE_DATE"));
				invoiceDetails.setOriginalDueDate(invoiceResultSet.getDate("ORIGINAL_DUE_DATE"));
				invoiceDetails.setSellerKey(invoiceResultSet.getString("SELLER_KEY"));
				invoiceDetails.setSupplierInvoceId(invoiceResultSet.getInt("SUPPLIER_INVOICE_ID"));
				long difference = invoiceDetails.getOriginalDueDate().getTime() - today.getTime();
				float daysBetween = (difference / (1000 * 60 * 60 * 24));
				invoiceDetails.setInvoiceDueDays(Float.valueOf(daysBetween).intValue());
				invoiceDetailsList.add(invoiceDetails);
			}
			
			Map<String, List<InvoiceDetails>> suppliersInvoiceMap = invoiceDetailsList.stream().collect(Collectors.groupingBy(InvoiceDetails::getSellerKey));
			
			
			
			for(Entry<String, List<InvoiceDetails>> supplierInvoice: suppliersInvoiceMap.entrySet()) {
				
				supplierInvoice.getKey();
				
				supplierInvoice.getValue();
				
			}
			
			
			
			System.out.println(suppliersInvoiceMap);
			
			String sellerInputQuery = "select * from seller_inputs where UPLOADED_DATE = '2017-12-26 00:00:00'";
			PreparedStatement preparedStmt = conn.prepareStatement(sellerInputQuery);
			//preparedStmt.setDate(1, new java.sql.Date(today.getTime()));
			System.out.println(preparedStmt.toString());
			ResultSet resultSet = preparedStmt.executeQuery();
			List<SellerInputDetails> sellerInputDetailsList = new ArrayList<>();
			while(resultSet.next()) {
				SellerInputDetails sellerInputDetails = new SellerInputDetails();
				sellerInputDetailsList.add(sellerInputDetails);
				sellerInputDetails.setMinDiscount(resultSet.getDouble("MIN_DISCOUNT"));
				sellerInputDetails.setMaxDiscount(resultSet.getDouble("MAX_DISCOUNT"));
				resultSet.getString("SELLER_ID");
			}
			
			System.out.println(sellerInputDetailsList);
			
			String buyerInputQuery = "select * from uploaded_files where FILE_STATUS = 'ACTIVE' "
					+ "AND UPLOADED_USER_ID = 'BUYER1' AND UPLOADED_DATE = '2017-12-26 00:00:00'";
			PreparedStatement buyerPreparedStmt = conn.prepareStatement(buyerInputQuery);
			//buyerPreparedStmt.setDate(1, new java.sql.Date(today.getTime()));
			System.out.println(buyerPreparedStmt.toString());
			
			ResultSet buyerResultSet = buyerPreparedStmt.executeQuery();
			
			while(buyerResultSet.next()) {
				awardsFileTO.setMroi(buyerResultSet.getDouble("MINIMUM_ROI"));
				awardsFileTO.setDroi(buyerResultSet.getDouble("DESIRED_ROI"));
				awardsFileTO.setMinReserveAmount(buyerResultSet.getDouble("MINIMUM_RESERVE_AMOUNT"));
				awardsFileTO.setMaxReserveAmount(buyerResultSet.getDouble("MAXIMUM_RESERVE_AMOUNT"));
			}
			
			
			
			
			conn.close();
			redirectAttributes.addFlashAttribute("message",
					"You successfully entered Discount details");
		} catch (Exception e) {
			e.printStackTrace();
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
    
    public static byte[] toByteArray(Object obj) throws IOException {
        byte[] bytes = null;
        ByteArrayOutputStream bos = null;
        ObjectOutputStream oos = null;
        try {
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            bytes = bos.toByteArray();
        } finally {
            if (oos != null) {
                oos.close();
            }
            if (bos != null) {
                bos.close();
            }
        }
        return bytes;
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