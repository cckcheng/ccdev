import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Chunk;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Enumeration;
import java.util.HashMap;
import javax.swing.tree.DefaultMutableTreeNode;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.events.PdfPCellEventForwarder;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.tree.TreeNode;

/**
 *
 * @author Cheng
 */
public class TreeToPDF {
	public static final int HORIZONTAL = 0;
	public static final int VERTICAL = 1;
	private boolean noBorder = true;	// set to false for debug
	private boolean splitLate = false;
	private boolean hasGenerationHeader = false;	// indicate if there is a generation header for table

	private boolean use_nesttable = true;

	private int generationPerPage = 5;
	private int firstGeneration = 1;
	private int startLevel = 0;
	private int endLevel = generationPerPage - 1;
	private int current_row = 0;

	private float fontSizeName = 12f;
	private float fontSizeSmallName = 9f;
	private float fontSizeGeneration = 10f;
	private float fontSizeTitle = 14f;
	private float fontSizeInfo = 9f;
	private float fontSizeTemp = 0.1f;

	private float marginLeft = 55;
	private float marginRight = 30;
	private float marginTop = 40;
	private float marginBottom = 40;

	private String headerLeft;
	private String headerRight;
	private String title = "族谱";

	private float mainTableWidth = 90f;	// the main part of the table's width percentage

	private int lastPrintLevel = -1;
	private boolean hasGenerationRow = true;	// indicate if the table's first row is generation
	private int layout = HORIZONTAL;
	private boolean appendIndex = true;

	private float fTemp;
	private int iTemp;
	private float maxBodyHeight;
	private float maxBodyWidth;
	private float mainCellWidth;
	private float leadingCellWidth;
	private PageHelper page_event;
	private final HashMap<String, DefaultMutableTreeNode> individualList;

	private HashMap<TreeNode, NodePosition> nodePos = new HashMap();
	private StringBuilder err = new StringBuilder();
	private Document doc;
	private BaseFont bfChnMain;
	private Font fontName;
	private Font fontSmallName;
	private Font fontHeader;
	private Font fontInfo;
	private Font fontInfoBold;
	private Font fontGeneration;
	private Font fontTemp;
	private PdfWriter writer;

	private DrawConnection tableEvent;
	private final DefaultMutableTreeNode root;
	private final HashMap<String, PdfTemplate> connectors = new HashMap();
	private BaseFont bfSongH;
	private BaseFont bfSongV;
	private final String failyName;

//	private IndexEvents indices = new IndexEvents();

	private boolean parseFloat(String s) {
		try {
			fTemp = Float.parseFloat(s);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	private boolean parseInt(String s) {
		try {
			iTemp = Integer.parseInt(s);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private void setParam(String s) {
		String[] ss = s.split("=");
		if(ss.length < 2) return;
		String s0 = ss[0].trim();
		String s1 = ss[1].trim();
		if(s0.equalsIgnoreCase("layout")) {
			s1 = s1.toUpperCase();
			this.layout = s1.startsWith("V") ? VERTICAL : HORIZONTAL;
		} else if(s0.equalsIgnoreCase("generationPerPage")) {
			if(this.parseInt(s1)) this.generationPerPage = this.iTemp;
		} else if(s0.equalsIgnoreCase("firstGeneration")) {
			if(this.parseInt(s1)) this.firstGeneration = this.iTemp;
		} else if(s0.equalsIgnoreCase("fontSizeName")) {
			if(this.parseFloat(s1)) this.fontSizeName = this.fTemp;
		} else if(s0.equalsIgnoreCase("fontSizeSmallName")) {
			if(this.parseFloat(s1)) this.fontSizeSmallName = this.fTemp;
		} else if(s0.equalsIgnoreCase("fontSizeInfo")) {
			if(this.parseFloat(s1)) this.fontSizeInfo = this.fTemp;
		} else if(s0.equalsIgnoreCase("fontSizeTitle")) {
			if(this.parseFloat(s1)) this.fontSizeTitle = this.fTemp;
		} else if(s0.equalsIgnoreCase("fontSizeGeneration")) {
			if(this.parseFloat(s1)) this.fontSizeGeneration = this.fTemp;
		} else if(s0.equalsIgnoreCase("marginLegt")) {
			if(this.parseFloat(s1)) this.marginLeft = this.fTemp;
		} else if(s0.equalsIgnoreCase("marginRight")) {
			if(this.parseFloat(s1)) this.marginRight = this.fTemp;
		} else if(s0.equalsIgnoreCase("marginTop")) {
			if(this.parseFloat(s1)) this.marginTop = this.fTemp;
		} else if(s0.equalsIgnoreCase("marginBottom")) {
			if(this.parseFloat(s1)) this.marginBottom = this.fTemp;
		} else if(s0.equalsIgnoreCase("headerLeft")) {
			this.headerLeft = s1;
		} else if(s0.equalsIgnoreCase("headerRight")) {
			this.headerRight = s1;
		} else if(s0.equalsIgnoreCase("title")) {
			if(!s1.isEmpty())this.title = s1;
		} else if(s0.equalsIgnoreCase("index")) {
			s1 = s1.toUpperCase();
			this.appendIndex = s1.startsWith("Y") || s1.startsWith("T");
		}
	}

	public void parseConfigStr(String conf) {
		String[] params = conf.split("&");
		for(String s : params) {
			setParam(s);
		}
	}

	public boolean parseConfig(String confFileName) {
		BufferedReader reader = null;
		String s, s0, s1;
		try {
//			reader = new BufferedReader(new InputStreamReader(new FileInputStream(confFileName), Charset.forName("UTF-8")));
			reader = new BufferedReader(new FileReader(confFileName));
			while((s = reader.readLine()) != null) {
				setParam(s);
			}
		} catch (FileNotFoundException ex) {
			this.err.append("Config File '").append(confFileName).append("' not found.");
			return false;
		} catch (IOException ex) {
			this.err.append("IO exception.");
			return false;
		} finally {
			try {
				if(reader != null)reader.close();
			} catch (IOException ex) {
				this.err.append("IO exception.");
				return false;
			}
		}
		return true;
	}

	private void printGeneration() {
		if(this.startLevel == this.lastPrintLevel) {
			this.hasGenerationRow = false;
			return;
		}
		this.hasGenerationRow = true;
		this.lastPrintLevel = this.startLevel;
		
		int start = this.startLevel;
		if(this.startLevel > 0) {
			PdfPCell cell = new PdfPCell(new Phrase(""));
			cell.setBorder(0);
			this.table.addCell(cell);
			start++;
		}
		PdfPTable tb = this.table;
		if(this.use_nesttable) {
			tb = new PdfPTable(this.generationPerPage);
		}
		for(int i=start; i<=this.endLevel; i++) {
			PdfPCell cell = new PdfPCell(new Phrase(getChineseGeneration(i), this.fontGeneration));
			if(this.layout == TreeToPDF.VERTICAL) {
				cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell.setRotation(90);
			} else {
				cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
				cell.setPaddingLeft(6);
			}
			if(this.noBorder) cell.setBorder(0);
			tb.addCell(cell);
		}

		if(this.use_nesttable) {
			PdfPCell cell = new PdfPCell(tb);
			if(this.noBorder) cell.setBorder(0);
			if(this.startLevel == 0) cell.setColspan(2);
			this.table.addCell(cell);
		}
	}

	private void createTable() throws DocumentException, IOException {
		float min_height = this.fontSizeName * 6;
		if(this.startLevel == this.lastPrintLevel) {
			min_height = this.fontSizeName * 4;
		}
		float v_pos = this.writer.getVerticalPosition(false);
		if(v_pos - doc.bottom() < min_height) {
			this.doc.newPage();
		}

		if(this.startLevel == 0) {
			if(this.use_nesttable) {
				this.table = new PdfPTable(2);
				float w0 = 1f/this.generationPerPage;
				float[] width = {w0, 1-w0};
				this.table.setWidths(width);
			} else {
				this.table = new PdfPTable(this.generationPerPage);
			}
			this.table.setWidthPercentage(this.mainTableWidth);
			this.table.setHorizontalAlignment(Element.ALIGN_RIGHT);
			this.endLevel--;
		} else {
			int n = this.generationPerPage + 1;
			if(this.use_nesttable) {
				this.table = new PdfPTable(2);
			} else {
				this.table = new PdfPTable(n);
			}
			if(this.layout == HORIZONTAL) this.table.setSpacingBefore(6f);
			this.table.setWidthPercentage(100f);
			this.table.setHorizontalAlignment(Element.ALIGN_RIGHT);
			if(this.use_nesttable) {
				float[] width = {100f - this.mainTableWidth, this.mainTableWidth};
				this.table.setWidths(width);
			} else {
				float[] width = new float[n];
				width[0] = 100f - this.mainTableWidth;
				float w = this.mainTableWidth / this.generationPerPage;
				for(int i=1; i<n; i++) width[i] = w;
				this.table.setWidths(width);
			}
		}

		if(this.use_nesttable) {
			this.table.getDefaultCell().setPadding(0);
			if(this.noBorder) this.table.getDefaultCell().setBorder(0);
			this.table.setSplitLate(false);
		} else {
			this.tableEvent = new DrawConnection(this.bfChnMain, this.fontSizeName, this.fontSizeSmallName);
			this.tableEvent.setConnect_style(this.layout == VERTICAL ? DrawConnection.CIRCLE : DrawConnection.LINE);
			this.table.setTableEvent(this.tableEvent);
		}
	}

	private PdfPCell generalCell(String s, Font font, float width) throws BadElementException {
		return generalCell(s, font, -1, width, false, null);
	}

	private PdfPCell generalCell(String s, Font font, float width, boolean no_border) throws BadElementException {
		return generalCell(s, font, -1, width, no_border, null);
	}

	private PdfPCell generalCell(String s, Font font, float width, boolean no_border, String noteTag) throws BadElementException {
		return generalCell(s, font, -1, width, no_border, noteTag);
	}

	private PdfPCell generalCell(String s, Font font, int align, float width) throws BadElementException {
		return generalCell(s, font, align, width, false, null);
	}

	private PdfPCell generalCell(String s, Font font, int align, float width, boolean no_border, String noteTag) throws BadElementException {
		if(this.layout == HORIZONTAL) {
			Chunk chk = new Chunk(s, font);
			if(noteTag != null) chk.setLocalDestination(noteTag);
			PdfPCell c = new PdfPCell(new Phrase(chk));
			if(align > 0) c.setHorizontalAlignment(align);
			if(no_border) c.setBorder(0);
			return c;
		}

		float sz = font.getSize();
		float w = sz * 2;
		float h = width;
		float info_width = this.bfChnMain.getWidthPoint(s, sz);
		int info_lines = (int) Math.ceil(info_width / (h-sz));
		w = (info_lines) * sz + 2;
		PdfTemplate tpl = writer.getDirectContent().createTemplate(w, h);
		VerticalText vt = new VerticalText(tpl);
		vt.setVerticalLayout(w - sz/2 - 1, h - 2, h-2, MAX_LINES, sz);
		if(align > 0) vt.setAlignment(align);

		Paragraph ph = new Paragraph(s, font);
		vt.addText(ph);
		vt.go();

		Image img = Image.getInstance(tpl);
//		img.scalePercent(100);
		img.setRotationDegrees(90);
		Chunk chk = new Chunk(img, 0, 0, true);
		if(noteTag != null) chk.setLocalDestination(noteTag);
		PdfPCell c = new PdfPCell(new Phrase(chk));
		c.setPadding(0);
//		PdfPCell c = new PdfPCell(img, true);
//		c.setHorizontalAlignment(Element.ALIGN_CENTER);
//		c.setRotation(90);
		if(no_border) c.setBorder(0);
		return c;
	}

	private void printIndexByGeneration(HashMap<String, Integer> index, List<Individual> inds) throws DocumentException {
		doc.newPage();
		PdfPTable p = new PdfPTable(1);
		p.setWidthPercentage(100f);
		PdfPCell c = this.generalCell("索引（按世系）", this.fontName, this.layout == HORIZONTAL ? Element.ALIGN_CENTER : -1, this.maxBodyWidth);
		c.setPaddingBottom(5);
		c.setBorder(0);
		p.addCell(c);
		doc.add(p);

		p = new PdfPTable(2);
		p.setSplitLate(false);
		p.getDefaultCell().setPadding(0);
		p.setWidthPercentage(100f);
		float w1 = this.fontSizeSmallName * 7;
		float w2 = this.maxBodyWidth - w1;
		float wp = this.fontSizeSmallName * 5;
		float ws = this.maxBodyWidth - w1*3 - wp;
		float[] widths = {w1, w2};
		p.setWidths(widths);

		p.addCell(generalCell("父母", this.fontHeader, Element.ALIGN_CENTER, w1));
		PdfPTable tp = new PdfPTable(4);
		float[] tp_widths = {w1, wp, w1, ws};
		tp.setWidths(tp_widths);

		addIndexHeader(tp, w1, wp, ws);
		p.addCell(tp);
		p.setHeaderRows(1);

		p.addCell("");
//		c = generalCell(this.getChineseGeneration(0), this.fontName, Element.ALIGN_CENTER, w2);
		c = generalCell(this.getChineseGeneration(0), this.fontName, w2);
		c.setPadding(5);
		p.addCell(c);
		p.addCell("");
		tp = new PdfPTable(4);
		tp.setWidths(tp_widths);
		Individual ind = (Individual)root.getUserObject();
		printIndexLine(index, tp, ind, w1, wp, ws);
		p.addCell(tp);

		this.pendingNodes.add(root);
		int last_level = -1;
		while(!this.pendingNodes.isEmpty()) {
			DefaultMutableTreeNode node = this.pendingNodes.remove(0);
			if(node.getLevel() != last_level) {
				p.addCell("");
				int level = node.getLevel() + 1;
				String s = this.getChineseGeneration(level) + "（"
						+ this.ChineseNumber(this.indCount.get(level)) + "人）";
				c = generalCell(s, this.fontName, w2);
				c.setPadding(5);
				p.addCell(c);
				last_level = node.getLevel();
			}

			ind = (Individual)node.getUserObject();
			tp = new PdfPTable(1);
			tp.addCell(generalCell(ind.getPrintName(this.failyName), this.fontSmallName, w1, true));
			if(ind.hasSpouse()) {
				tp.addCell(generalCell(ind.getSpouseName(), this.fontSmallName, w1, true));
			}
			p.addCell(tp);

			tp = new PdfPTable(4);
			tp.setWidths(tp_widths);
			for(Enumeration n = node.children();n.hasMoreElements();) {
				node = (DefaultMutableTreeNode) n.nextElement();
				if(!node.isLeaf()) this.pendingNodes.add(node);
				ind = (Individual)node.getUserObject();
				printIndexLine(index, tp, ind, w1, wp, ws);
			}
			p.addCell(tp);
		}
		doc.add(p);
	}

	private void addIndexHeader(PdfPTable p, float w1, float w2, float w3) throws BadElementException {
		p.addCell(generalCell("名讳", this.fontHeader, Element.ALIGN_CENTER, w1));
		p.addCell(generalCell("页码", this.fontHeader, Element.ALIGN_CENTER, w2));
		p.addCell(generalCell("配偶", this.fontHeader, Element.ALIGN_CENTER, w1));
		p.addCell(generalCell("子女（页码）", this.fontHeader, Element.ALIGN_CENTER, w3));
	}

	private void printIndexLine(HashMap<String, Integer> index, PdfPTable p, Individual ind, float w1, float w2, float w3) throws BadElementException {
		StringBuilder s = new StringBuilder();
		String id = ind.getId();
		PdfPCell c = generalCell(ind.getPrintName(this.failyName), this.fontSmallName, w1);
		c.setCellEvent(new IndexCellEvent(id));
		p.addCell(c);

		p.addCell(generalCell(index.get(id) + "页", this.fontSmallName, w2));
		p.addCell(generalCell(ind.hasSpouse() ? ind.getSpouseName() : "", this.fontSmallName, w1));

		DefaultMutableTreeNode node = this.individualList.get(id);
		if(node.isLeaf()) {
			p.completeRow();
			return;
		}

		Enumeration n = node.children();
		while(n.hasMoreElements()) {
			node = (DefaultMutableTreeNode) n.nextElement();
			Individual son = (Individual)node.getUserObject();
			s.append(", ").append(son.getPrintName(this.failyName));
			if(index.containsKey(son.getId())) {
				s.append("（").append(index.get(son.getId())).append("）");
			}
		}
		p.addCell(generalCell(s.substring(2), this.fontSmallName, w3));
	}

	private void printIndexByName(HashMap<String, Integer> index, List<Individual> inds) throws DocumentException {
		doc.newPage();
//		writer.reorderPages(null);
//		writer.setPageEvent(null);

		PdfPTable p = new PdfPTable(1);
		p.setWidthPercentage(100f);
		PdfPCell c = this.generalCell("索引（按名讳）", this.fontName, this.layout == HORIZONTAL ? Element.ALIGN_CENTER : -1, this.maxBodyWidth);
		c.setPaddingBottom(5);
		c.setBorder(0);
		p.addCell(c);
		doc.add(p);

		p = new PdfPTable(4);
		p.setWidthPercentage(100f);
		float w1 = 0.15f;
		if(this.layout == VERTICAL) w1 = 0.12f;
		float w2 = 0.08f;
		float w3 = 1 - w1*2 - w2;
		float[] widths = {w1, w2, w1, w3};
		p.setWidths(widths);
		w1 *= this.maxBodyWidth;
		w2 *= this.maxBodyWidth;
		w3 *= this.maxBodyWidth;

		addIndexHeader(p, w1, w2, w3);
		p.setHeaderRows(1);

		for(Individual ind : inds) {
			printIndexLine(index, p, ind, w1, w2, w3);
		}
//		for(IndexEvents.Entry i : this.indices.getSortedEntries()) {
//			System.out.println(i.getPagenumbers().toString());
//			c = new PdfPCell(new Phrase(i.getIn1(), this.fontSmallName));
//			p.addCell(c);
//			c = new PdfPCell(new Phrase(i.getPageNumber() + "页", this.fontSmallName));
//			p.addCell(c);
//		}
		doc.add(p);
	}

	private void printIndices() throws DocumentException {
		HashMap<String, Integer> index = this.page_event.getIndex();
		if(!this.appendIndex || index.isEmpty()) return;

		List<Individual> inds = new ArrayList();
		for(String id : index.keySet()) {
			inds.add((Individual)this.individualList.get(id).getUserObject());
		}
		Collections.sort(inds);

		printIndexByName(index, inds);
		printIndexByGeneration(index, inds);
		System.out.println("Total Page: " + writer.getPageNumber());
	}

	private void printCover() throws DocumentException, BadElementException, MalformedURLException, IOException {
//		Image img = Image.getInstance(cover);
//		if(this.layout == VERTICAL) img.setRotationDegrees(90);
//		img.scaleToFit(this.maxBodyWidth, this.maxBodyHeight);
		PdfReader reader = new PdfReader(this.cover);
		PdfImportedPage page = writer.getImportedPage(reader, 1);
		Image img = Image.getInstance(page);
		Rectangle rect = doc.getPageSize();
		img.scaleToFit(rect.getWidth(), rect.getHeight());
		img.setAlignment(Element.ALIGN_CENTER);
		img.setAbsolutePosition(0, 0);
		doc.add(img);

		// print the title
		int len = this.title.length();
		int lines = 1;
		if(len >= 8) {
			len = (len+1)/2;
			lines = 2;
		}
		float h1 = 160, h2 = 230;
		float h = rect.getHeight() - h1 - h2;
		float sz = h / len;
		if(sz > 168) sz = 168;

//		BaseFont bfHeiV = BaseFont.createFont("MHei-Medium", "UniCNS-UCS2-V", false);
//		String ttf = "/usr/share/fonts/truetype/ttf-dejavu/DejaVuSansMono.ttf";
//		String ttf = "./Fzwbfw.ttf";
		String ttf = "simkai.ttf";
//		BaseFont bfKaiV = BaseFont.createFont(ttf, BaseFont.IDENTITY_V, false);
		Font font = new Font(this.bfSongV, sz, Font.BOLD);
//		Font font = new Font(bfKaiV, sz);
//		FontFactory.registerDirectory("./");
//		Font font = FontFactory.getFont(ttf, BaseFont.IDENTITY_V, sz);
		Paragraph phs = new Paragraph(this.title, font);
		phs.setAlignment(Element.ALIGN_JUSTIFIED);

		VerticalText vt = new VerticalText(this.writer.getDirectContent());
		float x = rect.getWidth()/2 + sz/2 * (lines-1);
		float y = rect.getHeight() - h1;
		vt.setVerticalLayout(x, y, h+sz/8, lines, sz);
//		vt.setAlignment(Element.ALIGN_CENTER);
		vt.addText(phs);
		vt.go();
	}

	private void printTitle() throws DocumentException {
		if(this.title == null || this.title.isEmpty()) return;

		Font fontTitle = new Font(bfChnMain, this.fontSizeTitle, Font.BOLD);
		doc.addTitle(title);

		PdfPTable t = new PdfPTable(1);
		t.setWidthPercentage(100f);
		Phrase p = new Phrase(title, fontTitle);
		PdfPCell c = new PdfPCell(p);
		if(this.layout == HORIZONTAL) {
			c.setHorizontalAlignment(Element.ALIGN_CENTER);
			c.setVerticalAlignment(Element.ALIGN_MIDDLE);
			c.setFixedHeight(this.fontSizeTitle * 3);
		} else {
			c.setHorizontalAlignment(Element.ALIGN_RIGHT);
			c.setFixedHeight(this.fontSizeTitle);
			c.setRotation(90);
		}
		if(this.noBorder) c.setBorder(0);
		t.addCell(c);
//		t.addCell(new Phrase("123"));
//		t.addCell(new Phrase("123"));

//		c = new PdfPCell();
//		addRotatedText(c, "中国abcd(人123)民中国abcd人123民中国abcd人123民中国abcd人123民中国abcd人123民中国abcd人123民中国abcd人123民中国abcd人123民中国abcd人123民中国abcd人123民", fontTitle);
//		t.addCell(c);
		doc.add(t);

		// test chunk skew
//		Chunk c = new Chunk("测试1", fontInfo);
//		c.setSkew(80, -80);
//		doc.add(c);
//		c = new Chunk("测试2", fontInfo);
////	c.setSkew(0, -20);
//		doc.add(c);
		// end of test chunk
	}

	private int idxNote0 = 0;
	private int idxNote1 = 0;
	private void printNotes() throws DocumentException {
		PdfPTable tb = new PdfPTable(1);
		tb.setWidthPercentage(100);
		float w = this.maxBodyWidth;
		while(!this.notes.isEmpty()) {
			List<String> ss = this.notes.remove(0);
			this.idxNote1 ++;
			tb.addCell(generalCell("注" + this.idxNote1 + "：", this.fontInfoBold, w, true, "note" + this.idxNote1));
			for(String s : ss) {
				tb.addCell(generalCell(s, this.fontInfo, w, true));
			}
		}

		doc.add(tb);
	}

	private HashMap<Integer, Integer> indCount = new HashMap<Integer, Integer>();
	private void countMemberByLevel(int level) {
		int n = 1;
		if(this.indCount.containsKey(level)) {
			n += this.indCount.get(level);
		}
		this.indCount.put(level, n);
	}

	static class NodePosition{
		int row, row_span;
		public NodePosition(int row, int row_span) {
			this.row = row;
			this.row_span = row_span;
		}
	}

	private URL cover;
	private URL ttfKaiTi;
	TreeToPDF(DefaultMutableTreeNode root, HashMap<String, DefaultMutableTreeNode> individualList) {
		this.root = root;
		this.failyName = ((Individual)root.getUserObject()).getFamilyName();
		this.individualList = individualList;

//		cover = this.getClass().getResource("logo.jpg");
		cover = this.getClass().getResource("coverpage.pdf");
		ttfKaiTi = this.getClass().getResource("simkai.ttf");
	}

	public boolean hasError() {
		return this.err.length() > 0;
	}

	public String getErrorMessage() {
		return err.toString();
	}

	public void setTitle(String title) {
		this.title = title;
	}

	private PdfPTable table;
	List<DefaultMutableTreeNode> pendingNodes = new ArrayList<DefaultMutableTreeNode>();
	List<List<String>> notes = new ArrayList<List<String>>();
	public void generatePDF(String outputName) {
		String pdfName = (this.layout == VERTICAL ? "tmp_" + outputName : outputName);
		this.doc = new Document(PageSize.A4);
		if(this.layout == HORIZONTAL){
			this.mainTableWidth = 90f;
		} else {
			this.mainTableWidth = 93f;

			if(this.marginLeft > this.marginRight) {
				fTemp = this.marginLeft;
				this.marginLeft = this.marginRight;
				this.marginRight = fTemp;
			}

			fTemp = this.marginLeft;
			this.marginLeft = this.marginTop;
			this.marginTop = this.marginRight;
			this.marginRight = this.marginBottom;
			this.marginBottom = fTemp;
		}
		this.doc.setMargins(marginLeft, marginRight, marginTop, marginBottom);

		try {
			String font_name = "STSongStd-Light";
			String font_encodeing = "UniGB-UCS2";

			this.bfSongH = BaseFont.createFont(font_name, font_encodeing + "-H", false);
			this.bfSongV = BaseFont.createFont(font_name, font_encodeing + "-V", false);

			this.bfChnMain = this.layout == TreeToPDF.VERTICAL ? bfSongV : bfSongH;
			this.fontName = new Font(bfChnMain, this.fontSizeName, Font.BOLD);
			this.fontHeader = new Font(bfChnMain, this.fontSizeSmallName, Font.BOLD);
			this.fontSmallName = new Font(bfChnMain, this.fontSizeSmallName, Font.NORMAL);
			this.fontInfo = new Font(bfChnMain, this.fontSizeInfo, Font.NORMAL);
			this.fontInfoBold = new Font(bfChnMain, this.fontSizeInfo, Font.BOLD);
			this.fontTemp = new Font(bfChnMain, this.fontSizeTemp, Font.NORMAL);
			this.fontGeneration = new Font(bfChnMain, this.fontSizeGeneration, Font.BOLD);

			this.writer = PdfWriter.getInstance(doc, new FileOutputStream(pdfName));
			doc.open();
			printCover();

			if(this.layout == VERTICAL){
				doc.setPageSize(PageSize.A4.rotate());
			}
			doc.newPage();

			this.maxBodyWidth = doc.right() - doc.left();
			this.maxBodyHeight = doc.top() - doc.bottom();
			this.mainCellWidth = this.maxBodyWidth * this.mainTableWidth / this.generationPerPage / 100f;
			this.leadingCellWidth = this.maxBodyWidth * (100 - this.mainTableWidth) / 100f;

			this.page_event = new PageHelper(bfSongH, this.layout);
			if(this.headerLeft != null) page_event.setLeftHeader(this.headerLeft);
			if(this.headerRight != null) page_event.setRightHeader(this.headerRight);
			this.writer.setPageEvent(page_event);
//			this.writer.setPageEvent(indices);

			doc.setPageCount(1);
			printTitle();

			this.pendingNodes.add(root);
//			this.splitLate = this.layout == VERTICAL;
			while(!pendingNodes.isEmpty()) {
				DefaultMutableTreeNode node = pendingNodes.remove(0);
				this.startLevel = node.getLevel();
				this.endLevel = this.startLevel + this.generationPerPage;

				if(this.startLevel != this.lastPrintLevel) {
					if(!this.notes.isEmpty()) printNotes();
				}
				createTable();
				printGeneration();
				if(this.hasGenerationHeader && this.hasGenerationRow) {
					this.current_row = 0;
					this.table.setHeaderRows(1);
				} else {
					this.current_row = this.hasGenerationRow ? 1 : 0;
				}

				if(this.use_nesttable) {
					printNode(this.table, node);
				} else {
					printNode(node);
				}
				doc.add(this.table);
			}
			if(!this.notes.isEmpty()) printNotes();

			System.out.println("current page: " + writer.getPageNumber());
			printIndices();
		} catch (DocumentException ex) {
			this.err.append(ex.getMessage());
		} catch (IOException ex) {
			this.err.append(ex.getMessage());
		}

		doc.close();

		try {
			if(this.layout == VERTICAL) {
				rotatePdf(pdfName, outputName, 90);
				new File(pdfName).deleteOnExit();
//			} else {
//				copyPdf(pdfName, "test.pdf");
			}
		} catch (IOException ex) {
			this.err.append(ex.getMessage());
		} catch (DocumentException ex) {
			this.err.append(ex.getMessage());
		}
	}

	public void testPDF() throws DocumentException, FileNotFoundException, IOException{
		this.doc = new Document(PageSize.A4);
		this.writer = PdfWriter.getInstance(doc, new FileOutputStream("test.pdf"));
		doc.open();
		int sec = 1;
		if(sec == 1) {
			String font_name = "STSongStd-Light";
			String font_encodeing = "UniGB-UCS2";

			this.bfSongH = BaseFont.createFont(font_name, font_encodeing + "-H", false);
			this.bfSongV = BaseFont.createFont(font_name, font_encodeing + "-V", false);

			String a = "我们abc123ABC１２３ＡＢＣａｂｃ";
			char[] chars = a.toCharArray();
			for(char c : chars) {
				System.out.println((int)c + " : " + this.bfSongH.getCidCode((int)c));
			}

			StringBuilder s = new StringBuilder();
//			for(int i=0x20000; i<=0x20000+100; i++){
//				int ucode = this.bfSongH.getUnicodeEquivalent(i);
//				System.out.println(i + " : " + ucode);
//				s.append((char)ucode);
//			}
//			System.out.println(s.toString());
//			doc.add(new Phrase(s.toString(), new Font(this.bfSongH,8)));

			s.setLength(0);
			for(int i=0x9FC4, n=i+50; i<=n; i++){
				s.append((char)i);
			}
			System.out.println(s.toString());
			doc.add(new Phrase(s.toString(), new Font(this.bfSongH,8)));
//			s.setLength(0);
//			for(int i=0xFE10, n=i+100; i<=n; i++){
//				s.append((char)i);
//			}
//			System.out.println(s.toString());
//			doc.add(new Phrase(s.toString(), new Font(this.bfSongH,8)));
//			s.setLength(0);
//			for(int i=0xFE30, n=i+100; i<=n; i++){
//				s.append((char)i);
//			}
//			System.out.println(s.toString());
//			doc.add(new Phrase(s.toString(), new Font(this.bfSongH,8)));
		} else if(sec == 2) {
			StringBuilder sb = new StringBuilder();
			for(int i=0; i<100; i++) {
				sb.append("Line: ").append(i).append("\n");
			}
			/*
			 *
			// test column text
			ColumnText ct = new ColumnText(writer.getDirectContent());
			ct.addText(new Phrase(sb.toString()));
			int status = ColumnText.START_COLUMN;
			while (ColumnText.hasMoreText(status)) {
				ct.setSimpleColumn(36, 36, 296, 806);
				status = ct.go();
				System.out.println(ct.getYLine());
				doc.newPage();
			}

			*/
			PdfPTable tb = new PdfPTable(2);
			tb.setWidthPercentage(100);
			int[] widths = {10, 20};
			tb.setWidths(widths);
			tb.getDefaultCell().setPadding(0);
			tb.addCell(new PdfPCell(new Phrase(sb.toString())));
			PdfPTable t1 = new PdfPTable(2);
//			t1.addCell(new PdfPCell(new Phrase(sb.toString())));
//			tb.addCell(t1);
//
//			t1 = new PdfPTable(1);
			tb.addCell(t1);
			t1.addCell(new PdfPCell(new Phrase("cell 0, 1")));
			t1.addCell(new PdfPCell(new Phrase("cell 1, 1")));
			t1.addCell(new PdfPCell(new Phrase("cell 1, 2")));
			t1.addCell(new PdfPCell(new Phrase("cell 1, 2")));
			doc.add(tb);
		} else {
			doc.add(new Paragraph("Hello! 你好!", this.fontName));
			StringBuilder sb = new StringBuilder();
			for(int i=0; i<100; i++) {
				sb.append("Line: ").append(i).append("\n");
			}
			PdfContentByte cb = writer.getDirectContent();
//			PdfGraphics2D g = (PdfGraphics2D) cb.createGraphics(100, 200);
//			g.drawString(sb.toString(), 10, 20);
			cb.beginText();
			cb.setFontAndSize(bfChnMain, 16);
			cb.setTextMatrix(10, 500);
			cb.showText(sb.toString());
			cb.endText();

			PdfTemplate template = cb.createTemplate(300, 300);
			template.beginText();
			template.setFontAndSize(bfChnMain, 14);
			template.showText(sb.toString());
			template.moveText(0, 20);
			template.newlineShowText("second");
			template.moveText(0, 10);
			template.showText("third");
//			template.moveText(0, 50);
			template.endText();
			cb.addTemplate(template, 100, 300);

			doc.newPage();
			cb.beginText();
			cb.setFontAndSize(bfChnMain, 16);
			cb.setTextMatrix(10, 200);
			cb.showText(sb.toString());
			cb.endText();
		}
		doc.close();
	}

	private void fillTable(DefaultMutableTreeNode node) throws DocumentException {
		if(node == null) return;

		Individual ind = (Individual) node.getUserObject();
		PdfPCell cell = new PdfPCell(new Phrase(ind.getPrintName(this.failyName), this.fontName));
		if(node.getLeafCount() > 1) cell.setRowspan(node.getLeafCount());
		table.addCell(cell);

		if(node.isLeaf()) return;
		printNode((DefaultMutableTreeNode) node.getFirstChild());
//		cell.setBorder(0);
//		Enumeration n = node.children();
//		while(n.hasMoreElements()) {
//			fillTable(table, (DefaultMutableTreeNode) n.nextElement());
//		}
	}

	private float getWidthPoint(String s, boolean isLeadingNode) {
		if(isLeadingNode) return this.bfChnMain.getWidthPoint(s, this.fontSizeSmallName);
		return this.bfChnMain.getWidthPoint(s, this.fontSizeName);
	}

	private void printNode(DefaultMutableTreeNode node) throws DocumentException {
		Individual ind = (Individual) node.getUserObject();
		boolean isLeadingNode = this.startLevel > 0 && this.startLevel == node.getLevel();

		PdfPCell cell = null;
		if(this.layout == TreeToPDF.VERTICAL) {
			cell = this.verticalLayoutCell(ind, isLeadingNode);
		} else {
			cell = this.horizontalLayoutCell(ind, isLeadingNode);
		}

		if(this.noBorder) cell.setBorder(0);

		if(node.getLevel() == this.endLevel) {
			this.nodePos.put(node, new NodePosition(this.current_row, 1));
			if(!node.isLeaf()) {
				int column1 = node.getLevel() - this.startLevel;
				this.tableEvent.addSonConnecion(this.current_row, column1, column1+1,
						this.getWidthPoint(ind.getPrintName(this.failyName), isLeadingNode), true);
				this.pendingNodes.add(node);

				cell.setCellEvent(new CellConnector(CellConnector.SOURCE, ind.getId(), this.connectors,
						this.layout, this.fontSmallName, this.fontSizeName));
			}
			table.addCell(cell);
			printSibling(node);
		} else {
			if(isLeadingNode) {
				cell.setCellEvent(new CellConnector(CellConnector.DESTINATION, ind.getId(), this.connectors,
						this.layout, this.fontSmallName, this.fontSizeName));
			}
			int rowSpan = getLeafCount(node);
			this.nodePos.put(node, new NodePosition(this.current_row, rowSpan));
			cell.setRowspan(rowSpan);
			table.addCell(cell);

			if(node.isLeaf()) {
				PdfPCell empty_cell = new PdfPCell(new Phrase(""));
				empty_cell.setBorder(0);
				empty_cell.setColspan(this.endLevel - node.getLevel());
				table.addCell(empty_cell);
				printSibling(node);
			} else {
				int column1 = node.getLevel() - this.startLevel;
				this.tableEvent.addSonConnecion(this.current_row, column1, column1+1,
						this.getWidthPoint(ind.getPrintName(this.failyName), isLeadingNode));
				printNode((DefaultMutableTreeNode) node.getFirstChild());
			}
		}
	}

	private void printNode(PdfPTable tb, DefaultMutableTreeNode node) throws DocumentException {
		Individual ind = (Individual) node.getUserObject();
		boolean isLeadingNode = this.startLevel > 0 && this.startLevel == node.getLevel();
		if(!isLeadingNode){
			countMemberByLevel(node.getLevel());
		}

		PdfPCell cell = null;
		if(this.layout == TreeToPDF.VERTICAL) {
			cell = this.verticalLayoutCell(ind, isLeadingNode);
		} else {
			cell = this.horizontalLayoutCell(ind, isLeadingNode);
		}

		ConnectNode conn = new ConnectNode(ind.getId(), isLeadingNode || node.isRoot(), !node.isLeaf(),
				node.getPreviousSibling() != null, node.getNextSibling() != null,
				this.getWidthPoint(ind.getPrintName(this.failyName), isLeadingNode));
		conn.setConnect_style(this.layout == VERTICAL ? conn.CIRCLE : conn.LINE);
		cell.setCellEvent(conn);
		if(this.noBorder) cell.setBorder(0);

		tb.addCell(cell);
//		System.out.println(tb.calculateHeights(false));
		if(node.getLevel() == this.endLevel) {
			if(!node.isLeaf()) {
				conn.setIs_last(true);
				this.pendingNodes.add(node);
			}
			return;
		}

		if(node.isLeaf()) {
			tb.completeRow();
			return;
		}

		int num = this.endLevel - node.getLevel() - 1;
		PdfPTable t0 = new PdfPTable(1);
		t0.setKeepTogether(false);
//		t0.setSplitRows(true);
		t0.setSplitLate(this.splitLate);
		t0.getDefaultCell().setPadding(0);
		if(this.noBorder) t0.getDefaultCell().setBorder(0);
		for(Enumeration n = node.children(); n.hasMoreElements();) {
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) n.nextElement();
			if(num == 0) {
				printNode(t0, child);
			} else {
				PdfPTable t = new PdfPTable(2);
				t.setSplitLate(false);
//				t.setTotalWidth(this.mainCellWidth * (num+1));
				float[] widths = {this.mainCellWidth, this.mainCellWidth * num};
				t.setWidths(widths);
				t.getDefaultCell().setPadding(0);
				if(this.noBorder) t.getDefaultCell().setBorder(0);
				printNode(t, child);
				t0.addCell(t);
			}
		}
		tb.addCell(t0);
//		if(tb.calculateHeights(false) < );
	}

	private void printSibling(DefaultMutableTreeNode node) throws DocumentException {
		if(node.getLevel() == this.startLevel) return;
		DefaultMutableTreeNode sibling = node.getNextSibling();
		if(sibling == null) {
			printSibling((DefaultMutableTreeNode) node.getParent());
		} else {
			this.current_row++;
			NodePosition pos = this.nodePos.get(node);
			this.tableEvent.addSiblingConnecion(node.getLevel() - this.startLevel, pos.row, this.current_row);
			printNode(sibling);
		}
	}

	private int getLeafCount(DefaultMutableTreeNode node) {
		if(node.isLeaf() || node.getLevel() == this.endLevel) return 1;

		int count = 0;
		Enumeration n = node.children();
		while(n.hasMoreElements()) {
			count += getLeafCount((DefaultMutableTreeNode) n.nextElement());
		}
		return count;
	}

	private PdfPCell horizontalLayoutCell(Individual ind, boolean isLeadingNode) throws BadElementException {
		String s = ind.getPrintName(this.failyName);
		Paragraph p = new Paragraph();
		Paragraph pName = null, pInfo = null;
//		p.setExtraParagraphSpace(0);
		if(isLeadingNode) { //the leading node
			p.setLeading(this.fontSizeSmallName);
			Chunk c = new Chunk(s, this.fontSmallName);
			c.setLocalDestination("to" + ind.getId());
			pName = new Paragraph(c);
			p.add(pName);
		} else {
			p.setLeading(this.fontSizeName);
			Chunk c = new Chunk(s, this.fontName);
			c.setLocalDestination(ind.getId());
//			c.setLocalGoto(ind.getId());
			c.setGenericTag(ind.getId());
			pName = new Paragraph(c);
			p.add(pName);
			if(ind.hasInfo()) {
				for(String info : ind.getInfo()){
					pInfo = new Paragraph(info, this.fontInfo);
					p.add(pInfo);
				}
			}

			if(ind.hasSpouse()) {
				pInfo = new Paragraph("配" + ind.getSpouseName(), this.fontInfo);
				p.add(pInfo);
			}

			if(ind.hasNote()) {
				this.idxNote0 ++;
				this.notes.add(ind.getNotes());
				c = new Chunk("（注" + this.idxNote0 + "）", this.fontInfo);
				c.setLocalGoto("note" + this.idxNote0);
				p.add(new Paragraph(c));
			}
		}

		PdfPCell cell = new PdfPCell();
		cell.setPadding(5f);
		cell.addElement(p);
		if(isLeadingNode) {
			cell.setPaddingTop(cell.getPaddingTop() + (this.fontSizeName - this.fontSizeSmallName)/2);
		}

		return cell;
	}

	private final int MAX_LINES = 50;
	private PdfPCell verticalLayoutCell(Individual ind, boolean isLeadingNode) throws BadElementException, DocumentException {
		float w = this.fontSizeName * 1.6f;
		float h = this.mainCellWidth;
		if(isLeadingNode) {
			h = this.leadingCellWidth;
		}

		PdfTemplate tpl = writer.getDirectContent().createTemplate(w, h);
		VerticalText vt = new VerticalText(tpl);
		vt.setVerticalLayout(w - this.fontSizeName, h-5, h-10, MAX_LINES, this.fontSizeName);

		Chunk chk = new Chunk(ind.getPrintName(this.failyName), isLeadingNode ? this.fontSmallName : this.fontName);
//		if(!isLeadingNode) {
//			chk.setLocalGoto(ind.getId());	// this doesn't work either?
//			chk.setGenericTag(ind.getId());	// why this doesn't work?
//		} else {
//			chk.setLocalDestination(ind.getId());	// this doesn't work either?
//		}
		vt.addText(chk);
		vt.go();

//		int nameLines = 1;
//		int startInfoLine = vt.getMaxLines();
//		w = (nameLines + 1) * this.fontSizeName + (startInfoLine - vt.getMaxLines()) * this.fontSizeInfo;
//		System.out.println("w=" + w);
//		tpl.setWidth(w);	// seems like this doesn't work

		Image img = Image.getInstance(tpl);
//		img.scalePercent(100);
		img.setRotationDegrees(90);
		chk = new Chunk(img, 0, 0, true);
		if(isLeadingNode) {
			chk.setLocalDestination("to" + ind.getId());
		} else if(this.appendIndex) {
			chk.setLocalDestination(ind.getId());
			chk.setGenericTag(ind.getId());
		}
		PdfPCell c = new PdfPCell(new Phrase(chk));
		c.setPadding(0);
		if(this.noBorder)c.setBorder(0);
		PdfPTable t = new PdfPTable(1);
		t.addCell(c);
		if(isLeadingNode) {
			return new PdfPCell(t);
		}

//		if(this.appendIndex) {
//			// this is a work around
//			chk = new Chunk(" ", this.fontTemp);
//			chk.setLocalDestination(ind.getId());
//			chk.setGenericTag(ind.getId());
//			PdfPCell temp = new PdfPCell(new Phrase(chk));
//			temp.setPaddingTop(0);
//			temp.setPaddingBottom(0);
//			temp.setBorder(0);
//			t.addCell(temp);
//		}

		for(String info : ind.getInfo()) {
			float info_width = this.bfChnMain.getWidthPoint(info, this.fontSizeInfo);
			int info_lines = (int) Math.ceil(info_width / (h-10-this.fontSizeInfo));
			w = (info_lines) * this.fontSizeInfo;

			tpl = writer.getDirectContent().createTemplate(w, h);
			vt = new VerticalText(tpl);
			vt.setVerticalLayout(w - this.fontSizeInfo/2, h - 5, h-10, MAX_LINES, this.fontSizeInfo);
			vt.addText(new Chunk(info, this.fontInfo));
			vt.go();
			img = Image.getInstance(tpl);
			img.setRotationDegrees(90);
			chk = new Chunk(img, 0, 0, true);
			c = new PdfPCell(new Phrase(chk));
			c.setPaddingTop(0);
			c.setPaddingLeft(0);
//			c = new PdfPCell(img, true);
//			c.setHorizontalAlignment(Element.ALIGN_RIGHT);
//			c.setRotation(90);
			if(this.noBorder)c.setBorder(0);
			t.addCell(c);
		}

		if(ind.hasSpouse()) {
			w = this.fontSizeInfo;
			tpl = writer.getDirectContent().createTemplate(w, h);
			vt = new VerticalText(tpl);
			vt.setVerticalLayout(w - this.fontSizeInfo/2, h - 5, h-10, MAX_LINES, this.fontSizeInfo);
			vt.addText(new Chunk("配" + ind.getSpouseName(), this.fontInfo));
			vt.go();
			img = Image.getInstance(tpl);
			img.setRotationDegrees(90);
			chk = new Chunk(img, 0, 0, true);
			c = new PdfPCell(new Phrase(chk));
			c.setPaddingTop(0);
			c.setPaddingLeft(0);
			if(this.noBorder)c.setBorder(0);
			t.addCell(c);
		}

		if(ind.hasNote()) {
			this.idxNote0 ++;
			this.notes.add(ind.getNotes());
			w = this.fontSizeInfo;
			tpl = writer.getDirectContent().createTemplate(w, h);
			vt = new VerticalText(tpl);
			vt.setVerticalLayout(w - this.fontSizeInfo/2, h - 5, h-10, MAX_LINES, this.fontSizeInfo);
			vt.addText(new Chunk("（注" + this.idxNote0 + "）", this.fontInfo));
			vt.go();
			img = Image.getInstance(tpl);
			img.setRotationDegrees(90);
			chk = new Chunk(img, 0, 0, true);
			chk.setLocalGoto("note" + this.idxNote0);
			c = new PdfPCell(new Phrase(chk));
			c.setPaddingTop(0);
			if(this.noBorder)c.setBorder(0);
			t.addCell(c);
		}

		return new PdfPCell(t);
	}

	public static final String[] CHINESE_NUMBER = {"〇", "一", "二", "三", "四", "五", "六", "七", "八", "九", "十"};
	public static String ChineseNumber(Integer n) {
		if(n == null) return "";
		if(n<0 || n>=1000) return "" + n;
		if(n>=100) return CHINESE_NUMBER[n/100] + CHINESE_NUMBER[(n/10)%10] + CHINESE_NUMBER[n%10];
		if(n<=10) return CHINESE_NUMBER[n];
		if(n<20) return CHINESE_NUMBER[10] + CHINESE_NUMBER[n-10];
		if(n%10 == 0) return CHINESE_NUMBER[n/10] + CHINESE_NUMBER[10];
		return CHINESE_NUMBER[n/10] + CHINESE_NUMBER[10] + CHINESE_NUMBER[n%10];
	}

	private String getChineseGeneration(int level) {
		int n = level + this.firstGeneration;
		if(n<=10) return "第" + ChineseNumber(n) + "世";
		return ChineseNumber(n) + "世";
	}

	public String convertCIDs(String text) {
		char cid[] = text.toCharArray();
		for (int k = 0; k < cid.length; ++k) {
			char c = cid[k];
			if (c == '\n')
				cid[k] = '\uff00';
			else
				cid[k] = (char) (c - ' ' + 8720);
		}
		return new String(cid);
	}

	public void addRotatedText(PdfPCell cell, String text, Font font) throws BadElementException {
		if(cell == null || text == null) return;
//		PdfPTable t;
//		PdfPCell c;
		PdfTemplate tpl;
		float sz = font.getSize();
		Phrase p = new Phrase();
		for(int i=0, n=text.length(); i<n; i++) {
			char ch = text.charAt(i);
			if(text.codePointAt(i) < 0x100) {
				p.add(new Chunk(ch, font));
				continue;
			}
			tpl = writer.getDirectContent().createTemplate(sz, sz);
			ColumnText.showTextAligned(tpl, Element.ALIGN_LEFT, new Phrase("" + ch, font), 0, 0, 0);
//			c = new PdfPCell(new Phrase("" + ch, font));
//			c.setRotation(90);
//			t = new PdfPTable(1);
//			t.setTotalWidth(font.getSize());
//			t.addCell(c);
			Image img = Image.getInstance(tpl);
			img.setRotationDegrees(90);
			img.scalePercent(100f);
			p.add(new Chunk(img,0,0,true));
		}
		cell.addElement(p);
	}

	public static void rotatePdf(String src, String dest, int degree) throws IOException, DocumentException {
		PdfReader reader = new PdfReader(src);
		int n = reader.getNumberOfPages();
		int rot;
		PdfDictionary pageDict;
		for (int i = 2; i <= n; i++) {
			rot = reader.getPageRotation(i);
			pageDict = reader.getPageN(i);
			pageDict.put(PdfName.ROTATE, new PdfNumber(rot + degree));
		}
		PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(dest));
		stamper.close();
    }

	public static void copyPdf(String src, String dest) throws IOException, DocumentException {
		Document document = new Document();
		PdfSmartCopy copy = new PdfSmartCopy(document, new FileOutputStream(dest));
		document.open();
		PdfReader reader = new PdfReader(src);
		reader.consolidateNamedDestinations();
		int n = reader.getNumberOfPages();
		for (int i = 1; i <= n; i++) {
			copy.addPage(copy.getImportedPage(reader, i));
		}
		document.close();
	}

	static class IndexCellEvent implements PdfPCellEvent {
		private final String indId;
//		private boolean runOnce = true;

		public IndexCellEvent(String indId) {
			this.indId = indId;
		}

		public void cellLayout(PdfPCell cell, Rectangle rect, PdfContentByte[] pcbs) {
//			if(!this.runOnce) return;
//			this.runOnce = false;

			PdfContentByte cb = pcbs[PdfPTable.BACKGROUNDCANVAS];
			cb.saveState();
			cb.setAction(PdfAction.gotoLocalPage(indId, false), rect.getLeft() + 2, rect.getBottom() + 2,
					rect.getRight() - 2, rect.getTop() - 2);
			cb.restoreState();
		}

	}

	class ConnectNode extends PdfPCellEventForwarder {
		public final int LINE = 0;
		public final int CIRCLE = 1;

		private final boolean is_first;
		private final boolean has_elder;
		private final boolean has_child;
		private final boolean has_younger;
		private boolean is_last = false;
		private final float name_offset;
		private final float name_width;
		private int connect_style = LINE;
		private boolean splited = false;
		private final String indId;

		public void setIs_last(boolean is_last) {
			this.is_last = is_last;
		}

		public void setConnect_style(int connect_style) {
			this.connect_style = connect_style;
		}

		public ConnectNode(String indId, boolean is_first, boolean has_child, boolean has_elder, boolean has_younger, float name_width) {
			this.is_first = is_first;
			this.has_child = has_child;
			this.has_elder = has_elder;
			this.has_younger = has_younger;

			this.name_offset = fontSizeName;
			this.name_width = name_width;
			this.indId = indId;
		}

		@Override
		public void cellLayout(PdfPCell cell, Rectangle rect, PdfContentByte[] pcbs) {
			PdfContentByte cb = pcbs[PdfPTable.BACKGROUNDCANVAS];
			cb.saveState();

			float x0 = rect.getLeft();
			float x1 = x0 + 2;
			float x2 = x1 + this.name_width + 5;
			float x3 = rect.getRight();
			if(x2 > x3 - 5) x2 = x3 - 10;

			float y0 = rect.getTop();
			float y1 = y0 - this.name_offset;
			float y2 = rect.getBottom();

			if(this.splited) {
				if(!this.is_first && this.has_younger){
					cb.moveTo(x0, y0);
					cb.lineTo(x0, y2);
					cb.stroke();
				}
				cb.restoreState();
				return;
			}

			if(this.has_child) {
				if(this.is_last) {
					float w = fontSizeSmallName * 5;
					float h = fontSizeName * 2;
					cb.setAction(PdfAction.gotoLocalPage("to" + this.indId, false),
							x3 - w + 10, y0 - h, x3 + 10, y0);

					PdfTemplate tpl;
					if(layout == HORIZONTAL) {
						tpl = cb.createTemplate(w, h);
					} else {
						tpl = cb.createTemplate(h, w);
					}
					connectors.put(indId, tpl);

					PdfPTable t = new PdfPTable(1);
					t.setTotalWidth(w);
					try {
						PdfPCell c = new PdfPCell(Image.getInstance(tpl), true);
						if(layout == VERTICAL) {
							c.setHorizontalAlignment(Element.ALIGN_CENTER);
							c.setRotation(90);
						}
						c.setBorder(0);
						t.addCell(c);
						t.writeSelectedRows(0, -1, x3 - w + 10, y0, cb);
					} catch (BadElementException ex) {
					}

					x3 -= w - 10;
				}

				cb.moveTo(x2, y1);
				cb.lineTo(x3, y1);
			}

			if(this.is_first) {
				if(connectors.containsKey(indId)) {
					float w = fontSizeSmallName * 5;
					float h = fontSizeName * 2;
					PdfTemplate tpl = connectors.get(indId);
					Chunk chk = new Chunk("转" + cb.getPdfWriter().getPageNumber() + "页", fontSmallName);
					float x = 2;
					float y = (h-fontSizeSmallName)/2 + 1;
					if(layout == VERTICAL) {
						x = h - fontSizeName;
						y = w - 2;
					}
					ColumnText.showTextAligned(tpl, Element.ALIGN_LEFT, new Phrase(chk), x, y, 0);
				}
			} else {
				if(this.connect_style == CIRCLE) {
					float r = 1.5f;
					cb.circle(x0 + r, y1, r);
				} else {
					cb.moveTo(x0, y1);
					cb.lineTo(x1, y1);
				}

				if(this.has_elder) {
					cb.moveTo(x0, y0);
					cb.lineTo(x0, y1);
				}

				if(this.has_younger) {
					cb.moveTo(x0, y1);
					cb.lineTo(x0, y2);
				}
			}
			cb.stroke();

			cb.restoreState();
			this.splited = true;
		}
	}
}
