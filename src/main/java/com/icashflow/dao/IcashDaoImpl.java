package com.icashflow.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.icashflow.batch.item.excel.AbstractExcelItemReader;
import com.icashflow.batch.item.excel.Sheet;
import com.icashflow.batch.item.excel.mapping.PassThroughRowMapper;
import com.icashflow.batch.item.excel.poi.PoiItemReader;
import com.icashflow.to.BuyerInputDetails;
import com.icashflow.to.InvoiceDetails;
import com.icashflow.to.SellerInputDetails;

@Repository
@Qualifier("iCashDaoImpl")
public class IcashDaoImpl implements IcashDao{
	 @Autowired
	 private JdbcTemplate jdbcTemplate;
	 
	/**
	 * @return the jdbcTemplate
	 */
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	/**
	 * @param jdbcTemplate the jdbcTemplate to set
	 */
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public boolean updateBuyerInputs(BuyerInputDetails buyerInputDetails) throws SQLException, ClassNotFoundException {
		  Class.forName("com.mysql.jdbc.Driver");  
          
          Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPass);
		String query = " insert into BUYER_INPUTS (FILE_NAME,FILE_TYPE,FILE_DATA,"
				+ "UPLOADED_USER_ID,UPLOADED_DATE,FILE_SIZE, FILE_STATUS,"
				+ "MINIMUM_ROI,DESIRED_ROI, MINIMUM_RESERVE_AMOUNT, MAXIMUM_RESERVE_AMOUNT)"
				+ " values (?, ? ,? ,? ,? ,? ,? ,? ,? ,? ,?)";
				MultipartFile file = buyerInputDetails.getFile();
				PreparedStatement preparedStmt = conn.prepareStatement(query);
				preparedStmt.setString(1, file.getOriginalFilename());
				preparedStmt.setString(2, FilenameUtils.getExtension(file.getOriginalFilename()));
				try {
					preparedStmt.setBytes(3, file.getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//TODO set below value dynamically
				preparedStmt.setString(4, "BUYER1");
				java.util.Date today = new java.util.Date();
				preparedStmt.setDate(5, new java.sql.Date(today.getTime()));
				preparedStmt.setLong(6, file.getSize());
				preparedStmt.setString(7, "ACTIVE");
				preparedStmt.setFloat(8, buyerInputDetails.getMroi());
				preparedStmt.setFloat(9, buyerInputDetails.getDroi());
				preparedStmt.setFloat(10, buyerInputDetails.getMinReserveAmount());
				preparedStmt.setFloat(11, buyerInputDetails.getMaxReserveAmount());
				boolean result = preparedStmt.execute();
				conn.close();
				return result;
	}

	@Override
	public boolean updateSellerInputs(SellerInputDetails sellerInputDetails) {
		String query = " insert into SELLER_INPUTS (SELLER_ID,MIN_DISCOUNT,MAX_DISCOUNT,UPLOADED_DATE,DESIRED_PAYMEMT_DATE)"
				+ " values (? ,? ,? ,? ,? )";
		return jdbcTemplate.execute(query, new PreparedStatementCallback<Boolean>() {
			@Override
			public Boolean doInPreparedStatement(PreparedStatement preparedStmt)
					throws SQLException, DataAccessException {
				preparedStmt.setString(1, sellerInputDetails.getSellerId());
				preparedStmt.setFloat(2, sellerInputDetails.getMinDiscount());
				preparedStmt.setFloat(3, sellerInputDetails.getMaxDiscount());
				java.util.Date today = new java.util.Date();
				preparedStmt.setDate(4, new java.sql.Date(today.getTime()));
				Date dt = new Date();
				Calendar calender = Calendar.getInstance();
				calender.setTime(dt);
				calender.add(Calendar.DATE, 3);
				dt = calender.getTime();
				preparedStmt.setDate(5, new java.sql.Date(dt.getTime()));
				return preparedStmt.execute();
			}
		});  
	}
	

    private String dbURL = "jdbc:mysql://127.0.0.1:3306/icash";
    private String dbUser = "root";
    private String dbPass = "admin";

	@Override
	public boolean updateInvoiceDetails(BuyerInputDetails buyerInputDetails) throws Exception {
		MultipartFile file = buyerInputDetails.getFile();
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
        				+ "ORIGINAL_DUE_DATE ,BUYER_KEY, SELLER_KEY, ENTRY_DATE, INVOICE_DUE_DAYS)"
        				+ "values(? ,? ,? ,? ,? ,? ,?)";
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
    			preparedStmt.setInt(7, invoiceDueDays);
    			preparedStmt.execute();
    			System.out.println("Read: " + StringUtils.arrayToCommaDelimitedString(row));
        	}
        }
        conn.close();
        return true;
        }

	@Override
	public List<InvoiceDetails> getAllInvoicesUploadedToday() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPass);
		String userNameQuery = "select ICASH_LOGIN_USER_NAME from user_credentials where ICASH_LOGIN_KEY = 'BUYER1'";
		PreparedStatement loginNamepreparedStmt = conn.prepareStatement(userNameQuery);
		ResultSet loginResultSet = loginNamepreparedStmt.executeQuery();
		String userName = null;
		while(loginResultSet.next()) {
			userName = loginResultSet.getString("ICASH_LOGIN_USER_NAME");
		}
		
		String invoiceDetailsQuery = "select SUPPLIER_INVOICE_ID,ORIGINAL_DUE_DATE,INVOICE_DATE,INVOICE_AMOUNT,ENTRY_DATE,BUYER_KEY,SELLER_KEY,INVOICE_DUE_DAYS from INVOICE_DETAILS where ENTRY_DATE = ?;";
		PreparedStatement invoiceDetailsPrepareStmt = conn.prepareStatement(invoiceDetailsQuery);
		java.util.Date today = new java.util.Date();
		invoiceDetailsPrepareStmt.setDate(1, new java.sql.Date(today.getTime()));
		System.out.println(invoiceDetailsPrepareStmt.toString());
		ResultSet invoiceResultSet = invoiceDetailsPrepareStmt.executeQuery();
		List<InvoiceDetails> invoiceDetailsList = new ArrayList<>();
		while(invoiceResultSet.next()) {
			InvoiceDetails invoiceDetails = new InvoiceDetails();
			invoiceDetails.setBuyerKey(invoiceResultSet.getString("BUYER_KEY"));
			invoiceDetails.setEntryDate(invoiceResultSet.getDate("ENTRY_DATE"));
			invoiceDetails.setInvoiceAmount(invoiceResultSet.getFloat("INVOICE_AMOUNT"));
			invoiceDetails.setInvoiceDate(invoiceResultSet.getDate("INVOICE_DATE"));
			invoiceDetails.setOriginalDueDate(invoiceResultSet.getDate("ORIGINAL_DUE_DATE"));
			invoiceDetails.setSellerKey(invoiceResultSet.getString("SELLER_KEY"));
			invoiceDetails.setSupplierInvoceId(invoiceResultSet.getInt("SUPPLIER_INVOICE_ID"));
			//Difference of 1 day is noticed in this logic - Need to revisit
			/*long difference = invoiceDetails.getOriginalDueDate().getTime() - today.getTime();
			TimeUnit.DAYS.convert(difference, TimeUnit.DAYS);
			float daysBetween = (5108018440l / (1000 * 60 * 60 * 24));
			invoiceDetails.setInvoiceDueDays(Float.valueOf(daysBetween).intValue());
			*/
			invoiceDetails.setInvoiceDueDays(invoiceResultSet.getInt("INVOICE_DUE_DAYS"));
			invoiceDetailsList.add(invoiceDetails);
		}
		return invoiceDetailsList;
	}

	@Override
	public List<SellerInputDetails> getSellerTodayInputDetails() throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPass);
		java.util.Date today = new java.util.Date();
		String sellerInputQuery = "select * from seller_inputs where UPLOADED_DATE = ?";
		PreparedStatement preparedStmt = conn.prepareStatement(sellerInputQuery);
		preparedStmt.setDate(1, new java.sql.Date(today.getTime()));
		System.out.println(preparedStmt.toString());
		ResultSet resultSet = preparedStmt.executeQuery();
		
		List<SellerInputDetails> todaysInvoiceDetailsList = new ArrayList<>();
		while(resultSet.next()) {
			SellerInputDetails sellerInputDetails = new SellerInputDetails();
			todaysInvoiceDetailsList.add(sellerInputDetails);
			sellerInputDetails.setMinDiscount(resultSet.getFloat("MIN_DISCOUNT"));
			sellerInputDetails.setMaxDiscount(resultSet.getFloat("MAX_DISCOUNT"));
			String sellerKey = resultSet.getString("SELLER_ID");
			sellerInputDetails.setSellerId(sellerKey);
		}
		conn.close();
		return todaysInvoiceDetailsList;
	}

	@Override
	public BuyerInputDetails getBuyerInputDetails() throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPass);
		String buyerInputQuery = "select * from BUYER_INPUTS where FILE_STATUS = 'ACTIVE' "
				+ "AND UPLOADED_USER_ID = 'BUYER1' AND UPLOADED_DATE = ?";
		PreparedStatement buyerPreparedStmt = conn.prepareStatement(buyerInputQuery);
		java.util.Date today = new java.util.Date();
		buyerPreparedStmt.setDate(1, new java.sql.Date(today.getTime()));
		System.out.println(buyerPreparedStmt.toString());
		BuyerInputDetails buyerInputDetails = new BuyerInputDetails();
		ResultSet buyerResultSet = buyerPreparedStmt.executeQuery();
		float mroi = 0.0f;
		float droi = 0.0f;
		float miam = 0.0f;
		float maam = 0.0f;
		
		while(buyerResultSet.next()) {
			mroi = buyerResultSet.getFloat("MINIMUM_ROI");
			droi = buyerResultSet.getFloat("DESIRED_ROI");
			miam = buyerResultSet.getFloat("MINIMUM_RESERVE_AMOUNT");
			maam = buyerResultSet.getFloat("MAXIMUM_RESERVE_AMOUNT");
			
			buyerInputDetails.setMroi(mroi);
			buyerInputDetails.setDroi(droi);
			buyerInputDetails.setMinReserveAmount(miam);
			buyerInputDetails.setMaxReserveAmount(maam);
		}
		conn.close();
		return buyerInputDetails;
	}

	@Override
	public void updateAwardsFile(byte[] awardsFileArray) {/*
		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPass);
		String buyerInputQuery = "insert into AWARD_FILES (FILE_NAME,FILE_TYPE,FILE_DATA,"
				+	"UPLOADED_DATE, FILE_STATUS values(?, ? ,? ,? ,?";
		PreparedStatement buyerPreparedStmt = conn.prepareStatement(buyerInputQuery);
		java.util.Date today = new java.util.Date();
		buyerPreparedStmt.setDate(1, new java.sql.Date(today.getTime()));
		System.out.println(buyerPreparedStmt.toString());
		BuyerInputDetails buyerInputDetails = new BuyerInputDetails();
		ResultSet buyerResultSet = buyerPreparedStmt.executeQuery();
		float mroi = 0.0f;
		float droi = 0.0f;
		float miam = 0.0f;
		float maam = 0.0f;
		
		while(buyerResultSet.next()) {
			mroi = buyerResultSet.getFloat("MINIMUM_ROI");
			droi = buyerResultSet.getFloat("DESIRED_ROI");
			miam = buyerResultSet.getFloat("MINIMUM_RESERVE_AMOUNT");
			maam = buyerResultSet.getFloat("MAXIMUM_RESERVE_AMOUNT");
			
			buyerInputDetails.setMroi(mroi);
			buyerInputDetails.setDroi(droi);
			buyerInputDetails.setMinReserveAmount(miam);
			buyerInputDetails.setMaxReserveAmount(maam);
		}
		conn.close();
	*/}
	 

}
