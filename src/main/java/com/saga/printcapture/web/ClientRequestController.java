package com.saga.printcapture.web;

import com.saga.printcapture.util.PrintUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/capture")
public class ClientRequestController {
	
	private static Logger logger = LoggerFactory.getLogger(ClientRequestController.class);

	@Value("${image.conf.originImagePath}")
	private String savePath;
	@Value("${image.conf.combinedImagePath}")
	private String combinedPath;

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public @ResponseBody  Object request(@RequestParam("image") CommonsMultipartFile file) throws Exception {
		String fileName=UUID.randomUUID().toString()+".jpg";
		Map<String,String> result=new HashMap<>();
		try {
			//获取输出流
			OutputStream os=new FileOutputStream(savePath+fileName);
			//获取输入流 CommonsMultipartFile 中可以直接得到文件的流
			InputStream is=file.getInputStream();
			int temp=-1;
			//一个一个字节的读取并写入
			while((temp=is.read())!=(-1))
			{
				os.write(temp);
			}
			os.flush();
			os.close();
			is.close();
			String qrCodeUrl=PrintUtil.combine(combinedPath,fileName,savePath+fileName);
			result.put("qrCode",qrCodeUrl);
			result.put("fileName",fileName);
		} catch (FileNotFoundException e) {
			logger.error("save error:", e);
			return null;
		}
		return result;
	}

	@RequestMapping(value = "/print", method = RequestMethod.POST)
	public @ResponseBody  Object request(@RequestParam("fileName") String fileName) throws Exception {
		if (PrintUtil.printImage(new File(combinedPath+fileName))){
			return 1;
		}else {
			return 0;
		}
	}
}
