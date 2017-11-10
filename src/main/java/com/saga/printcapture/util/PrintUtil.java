package com.saga.printcapture.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;

/**
 * Created by holmes1214 on 08/11/2017.
 */
public class PrintUtil {
    private static Logger logger = LoggerFactory.getLogger(PrintUtil.class);

    public static boolean printImage(File file){

        try {
            DocFlavor dof = DocFlavor.INPUT_STREAM.JPEG;

            PrintService[] printServices = PrintServiceLookup.lookupPrintServices(dof, null);
            PrintService ps = null;
            if (printServices!=null&&printServices.length>0){
                ps=printServices[0];
            }else {
                ps=PrintServiceLookup.lookupDefaultPrintService();
            }

            PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
            pras.add(OrientationRequested.PORTRAIT);

            pras.add(new Copies(1));
            pras.add(PrintQuality.HIGH);
            DocAttributeSet das = new HashDocAttributeSet();

            // 设置打印纸张的大小（以毫米为单位）
            das.add(new MediaPrintableArea(0, 0, 102, 152, MediaPrintableArea.MM));
            FileInputStream fin = new FileInputStream(file);

            Doc doc = new SimpleDoc(fin, dof, das);

            DocPrintJob job = ps.createPrintJob();

            job.print(doc, pras);
            fin.close();
        } catch (Exception ie) {
            logger.error(ie.getMessage(),ie);
            return false;
        }
        return true;
    }

    public static String combine(String combinedPath, String savePath,String backgroundPath, String fileName,String back) {

        try {
            BufferedImage big = ImageIO.read(new File(savePath+fileName));
            BufferedImage small = ImageIO.read(new File(backgroundPath+back+".png"));
            Graphics2D g = big.createGraphics();
            int x = (big.getWidth() - small.getWidth()) / 2;
            int y = (big.getHeight() - small.getHeight()) / 2;
            g.drawImage(small, 0, 0, 1620,1080, null);
            g.dispose();
            String outFile=combinedPath+fileName;
            ImageIO.write(big, "png", new File(outFile));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return combinedPath;
    }
}
