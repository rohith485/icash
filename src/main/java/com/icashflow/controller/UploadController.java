package com.icashflow.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.icashflow.command.BuyerIcashCommand;
import com.icashflow.command.SellerIcashCommand;

@Controller
public class UploadController {

	final static Logger logger = Logger.getLogger(UploadController.class);

	private UploadControllerHelper uploadControllerHelper; 
    @GetMapping("/")
    public String index(Model model) {
    	BuyerIcashCommand buyerIcashCommand = new BuyerIcashCommand();
    	model.addAttribute("buyerIcashCommand", buyerIcashCommand);
    	SellerIcashCommand sellerIcashCommand = new SellerIcashCommand();
    	model.addAttribute("sellerIcashCommand", sellerIcashCommand);
        return "upload";
    }

    @PostMapping("/updatebuyerdata")
	public String processBuyerInput(@ModelAttribute("buyerIcashCommand") BuyerIcashCommand buyerIcashCommand, RedirectAttributes redirectAttributes) {
		MultipartFile file = buyerIcashCommand.getFile();
		if (file.isEmpty()) {
			logger.error("No file uploaded");
			redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
			return "redirect:uploadStatus";
		}
        try {
        	this.uploadControllerHelper.updateBuyerData(buyerIcashCommand);
        	logger.info("File successfully uploaded '" + file.getOriginalFilename());
			redirectAttributes.addFlashAttribute("message",
					"You successfully uploaded '" + file.getOriginalFilename() + "'");
		} catch (Exception e) {
			logger.info("Exception occured ", e);
			redirectAttributes.addFlashAttribute("message",
					"System exception occured whike processing your data");
		}

		return "redirect:/uploadStatus";
	}
    
    @PostMapping("/updatesellerdata")
	public String processSellerInput(@ModelAttribute("sellerIcashCommand") SellerIcashCommand sellerIcashCommand, RedirectAttributes redirectAttributes) {
		try {
			//TODO validate UI validations
			this.uploadControllerHelper.updateSellerData(sellerIcashCommand);
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
			this.uploadControllerHelper.generateRewardsFile();
			redirectAttributes.addFlashAttribute("message", "File Saved @ F:\\Freelancing\\Documents\\awardssheet.xls");
			logger.info("File Saved @ F:\\Freelancing\\Documents\\awardssheet.xls");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("message", "Failed to prepare file");
			e.printStackTrace();
			System.out.println("Exception occured " + e);
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

	/**
	 * @return the uploadControllerHelper
	 */
	public UploadControllerHelper getUploadControllerHelper() {
		return uploadControllerHelper;
	}

	/**
	 * @param uploadControllerHelper the uploadControllerHelper to set
	 */
	@Autowired
	public void setUploadControllerHelper(UploadControllerHelper uploadControllerHelper) {
		this.uploadControllerHelper = uploadControllerHelper;
	}
    
    

}