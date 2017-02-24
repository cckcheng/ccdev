/*
 * Copyright (c) 2017, ccheng
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.ccdev.pdfutil;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.Properties;

/**
 *
 * @author ccheng
 */
public class CombineFiles {
    public void insertImage(Document doc, String imgName) throws BadElementException, IOException, DocumentException {
        Image img = Image.getInstance(imgName);
        Rectangle rect = doc.getPageSize();
        img.scaleToFit(rect.getWidth() - 50, rect.getHeight() - 50);
        img.setAlignment(Element.ALIGN_CENTER);
//        float w = img.getScaledHeight();
//        float h = img.getScaledHeight();
//        img.set.setAbsolutePosition(0, 0);
        doc.add(img);
    }
    
    public void combinePdf(Document doc, String path, String[] inputs, OutputStream outStream) throws IOException, DocumentException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        PdfCopy copy = new PdfCopy(doc, outStream);
        doc.open();
        for(String src : inputs) {
            PdfReader reader = new PdfReader(path + src);
            Field f = reader.getClass().getDeclaredField("encrypted");
            f.setAccessible(true);
            f.set(reader, false);
//            reader.consolidateNamedDestinations();
            int n = reader.getNumberOfPages();
            for (int i = 1; i <= n; i++) {
                copy.addPage(copy.getImportedPage(reader, i));
            }
            reader.close();
        }
        doc.close();
    }
    
    public void combineAll(String configFile) {
        Common c = new Common();
        Properties conf = c.loadConfigFile(configFile);
        if(c.hasError()) {
            System.out.println(c.getError());
            return;
        }
        String path = conf.getProperty("path");
        String output = conf.getProperty("output");
        String input = conf.getProperty("input");
        String type = conf.getProperty("type");
        if(output == null || input == null || type == null || output.isEmpty() || input.isEmpty() || type.isEmpty()) {
            System.out.println("Invalid config");
            return;
        }
        String[] inputs = input.split(",");
        if(inputs.length < 1) return;
        
        if(path != null && !path.isEmpty()) {
            path = path + "/";
        }
        Document doc = new Document(PageSize.A4);
        try {
            OutputStream outStream = new FileOutputStream(path + output);
            if(type.equalsIgnoreCase("pdf")) {
                this.combinePdf(doc, path, inputs, outStream);
            } else {
                PdfWriter writer = PdfWriter.getInstance(doc, outStream);
                doc.open();
                for(String src : inputs) {
                    src = path + src;
                    doc.newPage();
                    this.insertImage(doc, src);
                }
                doc.close();
            }
            System.out.println("Done");
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if(args.length != 1) {
            System.out.println("Usage: java -cp Pedigree.jar com.ccdev.pdfutil [config file]");
            System.exit(0);
        }
        new CombineFiles().combineAll(args[0]);
    }
}
