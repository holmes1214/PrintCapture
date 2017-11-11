package com.saga.printcapture.web;

import com.alibaba.fastjson.JSONObject;
import com.saga.printcapture.util.PrintUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/capture")
public class ClientRequestController {
	
	private static Logger logger = LoggerFactory.getLogger(ClientRequestController.class);

	@Value("${image.io.originImagePath}")
	private String savePath;
	@Value("${image.io.combinedImagePath}")
	private String combinedPath;
	@Value("${image.io.printedImagePath}")
	private String printedPath;
	@Value("${image.io.backgroundImagePath}")
	private String backgroundPath;
	@Value("${image.cutX}")
	private int cutX;
	@Value("${image.cutY}")
	private int cutY;

	@PostConstruct
	public void init(){
		File f1=new File(savePath);
		if (!f1.exists()){
			f1.mkdirs();
		}
		f1=new File(combinedPath);
		if (!f1.exists()){
			f1.mkdirs();
		}
		f1=new File(printedPath);
		if (!f1.exists()){
			f1.mkdirs();
		}
	}

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public @ResponseBody  Object request(@RequestParam("imageData") MultipartFile image,@RequestParam("backNumber") String backNumber,@RequestParam("suffix") String suffix, HttpServletResponse response) throws Exception {
		response.setHeader("Access-Control-Allow-Origin", "*");
		if(image.isEmpty()){
			return null;
		}
		String fileName=UUID.randomUUID().toString()+suffix;
		File dest = new File(savePath + fileName);
		logger.info("上传的后缀名为：" + fileName);
		Map<String,Object> result=new HashMap<>();
		try {
			image.transferTo(dest);
			String qrCodeUrl=PrintUtil.combine(combinedPath,savePath,backgroundPath,fileName,backNumber,cutX,cutY);
			result.put("qrCode", JSONObject.parseObject(qrCodeUrl));
			result.put("fileName",fileName);
		} catch (FileNotFoundException e) {
			logger.error("save error:", e);
			return null;
		}
		return result;
	}

	@RequestMapping(value = "/print", method = RequestMethod.POST)
	public @ResponseBody  Object request(@RequestParam("fileName") String fileName, HttpServletResponse response) throws Exception {
		response.setHeader("Access-Control-Allow-Origin", "*");
		if (PrintUtil.printImage(new File(combinedPath+fileName),printedPath+fileName)){
			return 1;
		}else {
			return 0;
		}
	}
}
