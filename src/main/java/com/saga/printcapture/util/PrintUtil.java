package com.saga.printcapture.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.print.*;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashDocAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.PrintQuality;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Iterator;

/**
 * Created by holmes1214 on 08/11/2017.
 */
public class PrintUtil {
    private static Logger logger = LoggerFactory.getLogger(PrintUtil.class);

    public static boolean printImage(File file, String printedPath) {
        FileInputStream fin = null;
        try {
            DocFlavor dof = DocFlavor.INPUT_STREAM.PNG;

            PrintService[] printServices = PrintServiceLookup.lookupPrintServices(dof, null);
            PrintService ps = null;
            if (printServices != null && printServices.length > 0) {
                ps = printServices[0];
            } else {
                ps = PrintServiceLookup.lookupDefaultPrintService();
            }

            coppyToPrint(file, printedPath);

            if(ps==null){
                return false;
            }

            PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
            pras.add(OrientationRequested.PORTRAIT);

            pras.add(new Copies(1));
            pras.add(PrintQuality.HIGH);
            DocAttributeSet das = new HashDocAttributeSet();
            // 设置打印纸张的大小（以毫米为单位）
            das.add(new MediaPrintableArea(0, 0, 102, 152, MediaPrintableArea.MM));
            fin = new FileInputStream(file);
            Doc doc = new SimpleDoc(fin, dof, das);
            DocPrintJob job = ps.createPrintJob();
            job.print(doc, pras);
        } catch (Exception ie) {
            logger.error(ie.getMessage(), ie);
            return false;
        } finally {
            if (fin != null) {
                try {
                    fin.close();
                } catch (IOException e) {
                }
            }
        }
        return true;
    }

    private static void coppyToPrint(File file, String printedPath) {
        File dst = new File(printedPath);
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(file);
            dst.createNewFile();
            fos = new FileOutputStream(dst);
            byte[] buffer = new byte[2048];
            int count = -1;
            while ((count = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, count);
            }
        } catch (Exception e) {
            logger.error("copy error: ", e);
        } finally {
            try {
                if (fis != null) {
                }

                fis.close();

                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
            }
        }
    }

    public static String combine(String combinedPath, String savePath, String backgroundPath, String fileName, String back, int cutX, int cutY) {
        try {
            BufferedImage big = ImageIO.read(new File(savePath + fileName));
            BufferedImage small = ImageIO.read(new File(backgroundPath + back + ".png"));

            Graphics2D g = big.createGraphics();

            g.drawImage(small, 0, 0, 1620, 1080, null);
            g.dispose();
            String outFile = combinedPath + fileName;
            big = rotateImage(big, 90, null);
            big = big.getSubimage(0, 0, big.getWidth() - cutX, big.getHeight() - cutY);
            ImageIO.write(big, "png", new File(outFile));
            String url = "http://evtape.com/snap/upload";
            RestTemplate rest = new RestTemplate();
            FileSystemResource resource = new FileSystemResource(new File(outFile));
            MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
            param.add("file", resource);
            String result = rest.postForObject(url, param, String.class);
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static BufferedImage rotateImage(final BufferedImage image,
                                            int degree, Color bgcolor) {
        int iw = image.getWidth();// 原始图象的宽度
        int ih = image.getHeight();// 原始图象的高度
        int w = 0;
        int h = 0;
        int x = 0;
        int y = 0;
        degree = degree % 360;
        if (degree < 0)
            degree = 360 + degree;// 将角度转换到0-360度之间
        double ang = Math.toRadians(degree);// 将角度转为弧度

        /**
         * 确定旋转后的图象的高度和宽度
         */

        if (degree == 180 || degree == 0 || degree == 360) {
            w = iw;
            h = ih;
        } else if (degree == 90 || degree == 270) {
            w = ih;
            h = iw;
        } else {
            int d = iw + ih;
            w = (int) (d * Math.abs(Math.cos(ang)));
            h = (int) (d * Math.abs(Math.sin(ang)));
        }

        x = (w / 2) - (iw / 2);// 确定原点坐标
        y = (h / 2) - (ih / 2);
        BufferedImage rotatedImage = new BufferedImage(w, h, image.getType());
        Graphics2D gs = (Graphics2D) rotatedImage.getGraphics();
        if (bgcolor == null) {
            rotatedImage = gs.getDeviceConfiguration().createCompatibleImage(w,
                    h, Transparency.OPAQUE);
        } else {
            gs.setColor(bgcolor);
            gs.fillRect(0, 0, w, h);// 以给定颜色绘制旋转后图片的背景
        }
        gs.dispose();
        AffineTransform at = new AffineTransform();
        at.rotate(ang, w / 2, h / 2);// 旋转图象
        at.translate(x, y);
        AffineTransformOp op = new AffineTransformOp(at,
                AffineTransformOp.TYPE_BICUBIC);
        op.filter(image, rotatedImage);
        return rotatedImage;
    }

}
