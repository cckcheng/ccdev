
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfAction;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.events.PdfPCellEventForwarder;
import java.util.HashMap;

/**
 *
 * @author Colin Cheng
 */
public class CellConnector extends PdfPCellEventForwarder{
	public static final int SOURCE = 1;
	public static final int DESTINATION = 2;
	private final int type;
	private final String indId;
	private final HashMap<String, PdfTemplate> connectors;
	private final int layout;

	public CellConnector(int type, String indId, HashMap<String, PdfTemplate> connectors,
			int layout, Font font, float fontSizeName) {
		this.type = type;
		this.indId = indId;
		this.connectors = connectors;
		this.fontSizeName = fontSizeName;
		this.font = font;
		this.layout = layout;

		if(layout == TreeToPDF.HORIZONTAL) {
			this.w = font.getSize() * 5;
			this.h = fontSizeName * 2;
		} else {
			this.h = font.getSize() * 5;
			this.w = fontSizeName * 2;
		}
	}

	private float w, h, fontSizeName;
	private final Font font;
	private boolean runOnce = true;
	@Override
	public void cellLayout(PdfPCell cell, Rectangle position, PdfContentByte[] canvases) {
		super.cellLayout(cell, position, canvases);
		if(!runOnce) return;
		runOnce = false;
		PdfContentByte cb = canvases[PdfPTable.TEXTCANVAS];
		cb.saveState();
		if(this.type == SOURCE) {
			float tw = layout == TreeToPDF.HORIZONTAL ? w : h;
			float th = layout == TreeToPDF.HORIZONTAL ? h : w;
			cb.setAction(PdfAction.gotoLocalPage("to" + indId, false),
					position.getRight() - tw, position.getTop() - th, position.getRight(), position.getTop());

			PdfTemplate tpl = cb.createTemplate(w, h);
//			tpl.setAction(PdfAction.gotoLocalPage("to" + indId, false), 0, 0, w, h);
			this.connectors.put(indId, tpl);
			PdfPTable t = new PdfPTable(1);
			t.setTotalWidth(tw);
			try {
				PdfPCell c = new PdfPCell(Image.getInstance(tpl), true);
				if(layout == TreeToPDF.VERTICAL) {
					c.setHorizontalAlignment(Element.ALIGN_CENTER);
					c.setRotation(90);
				}
				c.setBorder(0);
				t.addCell(c);
				t.writeSelectedRows(0, -1, position.getRight() - tw, position.getTop(), cb);
			} catch (BadElementException ex) {
			}
		} else if(this.type == DESTINATION) {
			if(!this.connectors.containsKey(indId)) {
				// should never happen
				cb.restoreState();
				return;
			}

			PdfTemplate tpl = this.connectors.get(indId);
			Chunk chk = new Chunk("转" + cb.getPdfWriter().getPageNumber() + "页", this.font);
//			chk.setLocalGoto("to" + indId);	// this doesn't work!!!
//			tpl.setAction(PdfAction.gotoLocalPage("to" + indId, true), 0, 0, w, h); // this doesn't work either
			float x = 2;
			float y = (this.h - this.font.getSize())/2 + 1;
			if(layout == TreeToPDF.VERTICAL) {
				x = this.w - this.font.getSize() * 1.5f;
				y = this.h - 2;
			}
			ColumnText.showTextAligned(tpl, Element.ALIGN_LEFT, new Phrase(chk), x, y, 0);
		}

		cb.restoreState();
	}
}
