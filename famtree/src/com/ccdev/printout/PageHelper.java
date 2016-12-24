/*
 * Copyright (c) 2016, ccheng
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
package com.ccdev.printout;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.events.IndexEvents;
import java.util.HashMap;

/**
 *
 * @author Colin Cheng
 */
public class PageHelper extends PdfPageEventHelper{
	private Font fontPageNumber;
	private String leftHeader = "DAZUPU";
	private String rightHeader = "";
	private final Font fontHeader;
	private int layout = TreeToPDF.HORIZONTAL;

	public PageHelper(BaseFont bf, int layout) {
		this.fontPageNumber = new Font(bf, 10, Font.NORMAL);
		this.fontHeader = new Font(bf, 10, Font.BOLD);
		this.layout = layout;
	}

	public void setLeftHeader(String leftHeader) {
		this.leftHeader = leftHeader;
	}

	public void setRightHeader(String rightHeader) {
		this.rightHeader = rightHeader;
	}

	@Override
	public void onEndPage (PdfWriter writer, Document document) {
		PdfContentByte cb = writer.getDirectContent();
		float lt = document.left();
		float rt = document.right();
		float top = document.top();
		float bm = document.bottom();
		Rectangle rect = new Rectangle(lt, bm, rt, top);

		int pgNum = writer.getPageNumber();
		if(this.layout == TreeToPDF.HORIZONTAL) {
			float x0 = rect.getLeft();
			float x1 = rect.getRight();
			float y0 = rect.getTop() + 2;
			float y1 = rect.getBottom() - 12;
			cb.setLineWidth(0);
			cb.moveTo(x0, y0); cb.lineTo(x1, y0); cb.stroke();

			ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
					new Phrase(this.leftHeader, this.fontHeader),
					x0, y0+2, 0);
			ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT,
					new Phrase(this.rightHeader, this.fontHeader),
					x1, y0+2, 0);
			ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
					new Phrase(String.format("第%s页", TreeToPDF.ChineseNumber(pgNum)), this.fontPageNumber),
					(x0 + x1) / 2, y1, 0);
		} else if(this.layout == TreeToPDF.VERTICAL) {
			float x0 = rect.getLeft() - 22;
			float x1 = rect.getRight() + 2;
			float y0 = rect.getTop();
			float y1 = rect.getBottom();
			float h = y0 - y1;

			PdfPTable t = new PdfPTable(1);
			t.setTotalWidth(20f);

			PdfPCell c = new PdfPCell(new Phrase(this.rightHeader, this.fontHeader));
			c.setHorizontalAlignment(Element.ALIGN_RIGHT);
			c.setBorder(Rectangle.RIGHT);
			c.setFixedHeight(h / 2);
			c.setRotation(90);
			t.addCell(c);

			c = new PdfPCell(new Phrase(this.leftHeader, this.fontHeader));
			c.setBorder(Rectangle.RIGHT);
			c.setFixedHeight(h / 2);
			c.setRotation(90);
			t.addCell(c);

			t.writeSelectedRows(0, -1, x0, y0, cb);

			PdfPTable tp = new PdfPTable(1);
			tp.setTotalWidth(20f);
			c = new PdfPCell(new Phrase(String.format("第%s页", TreeToPDF.ChineseNumber(pgNum)), this.fontPageNumber));
			c.setFixedHeight(h);
			c.setBorder(0);
			c.setHorizontalAlignment(Element.ALIGN_CENTER);
			c.setRotation(90);
			tp.addCell(c);
			tp.writeSelectedRows(0, -1, x1, y0, cb);
		}
	}

	HashMap<String,Integer> index = new HashMap();

	public HashMap<String, Integer> getIndex() {
		return index;
	}

	@Override
	public void onGenericTag(PdfWriter writer, Document document, Rectangle rect, String text) {
		this.index.put(text, writer.getPageNumber());
	}

	@Override
	public void onCloseDocument(PdfWriter writer, Document document) {
//		for(String s : this.index.keySet()) {
//			System.out.println(s + " : " + this.index.get(s));
//		}
	}
}
