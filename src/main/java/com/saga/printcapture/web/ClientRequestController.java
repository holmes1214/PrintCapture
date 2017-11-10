package com.saga.printcapture.web;

import com.saga.printcapture.util.PrintUtil;
import com.sun.deploy.net.HttpResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Base64;
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

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public @ResponseBody  Object request(@RequestParam("backNumber") String backNumber, @RequestParam("imageData") String image, HttpServletResponse response) throws Exception {
		response.setHeader("Access-Control-Allow-Headers", "*");
		String fileName=UUID.randomUUID().toString()+".png";
		if(StringUtils.isBlank(image)){
			return null;
		}
		Base64.Decoder decoder=Base64.getDecoder();
		// Base64解码
		byte[] b = decoder.decode(image);
		for (int i = 0; i < b.length; ++i) {
			if (b[i] < 0) {// 调整异常数据
				b[i] += 256;
			}
		}

		Map<String,String> result=new HashMap<>();
		try {
			//获取输出流
			OutputStream os=new FileOutputStream(savePath+fileName);
			//获取输入流 CommonsMultipartFile 中可以直接得到文件的流
			InputStream is=new ByteArrayInputStream(b);
			int temp=-1;
			//一个一个字节的读取并写入
			while((temp=is.read())!=(-1))
			{
				os.write(temp);
			}
			os.flush();
			os.close();
			is.close();
			String qrCodeUrl=PrintUtil.combine(combinedPath,savePath,backgroundPath,fileName,backNumber);
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
