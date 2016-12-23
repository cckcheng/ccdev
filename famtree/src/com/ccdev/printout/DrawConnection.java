
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.events.PdfPTableEventForwarder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Colin Cheng
 */
public class DrawConnection extends PdfPTableEventForwarder{
	private final float fontSizeSmallName;

	static class ToSibling {
		int column;
		int row1, row2;

		public ToSibling(int column, int row1, int row2) {
			this.column = column;
			this.row1 = row1;
			this.row2 = row2;
		}
	}

	static class ToSon {
		int row;
		int column1, column2;
		float nameWidth;
		boolean isLast = false;

		public ToSon(int row, int column1, int column2, float nameWidth, boolean isLast) {
			this.column1 = column1;
			this.column2 = column2;
			this.row = row;
			this.nameWidth = nameWidth;
			this.isLast = isLast;
		}
	}

	static public int LINE = 0;
	static public int CIRCLE = 1;

	protected BaseFont bf;
	protected float font_size, name_offset;
	protected int start_row = 0;
	protected int connect_style = LINE;

	private List<ToSibling> sibling_connections = new ArrayList();
	private List<ToSon> son_connections = new ArrayList();

    DrawConnection(BaseFont bf, float font_size, float fontSizeSmallName) throws DocumentException, IOException {
//        this.bf = BaseFont.createFont(BaseFont.TIMES_BOLDITALIC, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
        this.bf = bf;
		this.font_size = font_size;
		this.name_offset = this.font_size;
		this.fontSizeSmallName = fontSizeSmallName;
    }

	public void addSiblingConnecion(int column, int row1, int row2) {
		this.sibling_connections.add(new ToSibling(column, row1, row2));
	}

	public void addSonConnecion(int row, int column1, int column2, float nameWidth, boolean isLast) {
		this.son_connections.add(new ToSon(row, column1, column2, nameWidth, isLast));
	}
	public void addSonConnecion(int row, int column1, int column2, float nameWidth) {
		this.son_connections.add(new ToSon(row, column1, column2, nameWidth, false));
	}

	public void setConnect_style(int connect_style) {
		this.connect_style = connect_style;
	}

	private void drawAllConnection(float[][] width, float[] height, int headerRows, PdfContentByte cb) {
		float r = 1.5f;

		cb.setLineWidth(0);
		int end_row = this.start_row + width.length - headerRows - 1;
		List<ToSibling> finishedSibling = new ArrayList();
		for(ToSibling o : this.sibling_connections) {
//			System.out.println(o.column + "--" + o.row1 + ":" + o.row2);
			if(o.row1 > end_row || o.row2 < this.start_row) continue;
			int r1 = Math.max(o.row1-this.start_row, 0) + headerRows;
			int r2 = Math.min(o.row2, end_row) - this.start_row + headerRows;
//			float x = width[0][o.column];
			float x = this.table_width[o.column];
			float y1 = height[r1];
			float y2 = height[r2+1];
			if(o.row1>=this.start_row) {
				y1 -= this.name_offset;
			}
			cb.moveTo(x, y1);
			if(o.row2<=end_row) {
				y2 = height[r2] - this.name_offset;
				cb.lineTo(x, y2);
				if(this.connect_style == LINE) {
					cb.lineTo(x+2, y2);
				} else if(this.connect_style == CIRCLE) {
					cb.circle(x+r, y2, r);
				}
				finishedSibling.add(o);
			} else {
				cb.lineTo(x, y2);
			}
		}
		this.sibling_connections.removeAll(finishedSibling);

		List<ToSon> finishedSon = new ArrayList();
		for(ToSon o : this.son_connections) {
			if(o.row > end_row || o.row < this.start_row) continue;
			int row = o.row - this.start_row + headerRows;
			float y = height[row] - this.name_offset;
			float x1 = this.table_width[o.column1] + o.nameWidth + 7;
			float x2 = this.table_width[o.column2];
			if(o.isLast) {
				x2 -= this.fontSizeSmallName * 5;
			} else {
				if(this.connect_style == LINE) {
					x2 += 2;
				} else if(this.connect_style == CIRCLE) {
					cb.circle(x2+r, y, r);
				}
			}
			cb.moveTo(x1, y);
			cb.lineTo(x2, y);
			finishedSon.add(o);
		}
		this.son_connections.removeAll(finishedSon);
		cb.stroke();
	}

	private boolean run_once = true;
	private float[] table_width;
	@Override
	public void splitTable(PdfPTable table) {
		super.splitTable(table);
//		int sz = table.size();
//		System.out.println("table size: " + table.size());
	}

	@Override
	public void tableLayout(PdfPTable table, float[][] width, float[] height, int headerRows, int rowStart, PdfContentByte[] canvases) {
		super.tableLayout(table, width, height, headerRows, rowStart, canvases);
//		System.out.println("start row: " + this.start_row);
//		System.out.println("cell padding top: " + table.getDefaultCell().getPaddingTop() + ":" + table.getDefaultCell().getEffectivePaddingTop());

		if(this.run_once) {
			// copy the widths of first row
			this.run_once = false;
			int len = width[0].length;
			this.table_width = new float[len];
			for(int i=0; i<len; i++) this.table_width[i] = width[0][i];
		}

		PdfContentByte cb = canvases[PdfPTable.BACKGROUNDCANVAS];
//		PdfContentByte cb = canvases[PdfPTable.LINECANVAS];
		cb.saveState();

//		cb.setRGBColorFill(0x9a, 0xe4, 0xe8);
//		cb.roundRectangle(width[0][0], height[2], width[0][3] - width[0][0], height[0] - height[2], 4);
//		cb.roundRectangle(width[0][1], height[2] + 3, width[0][3] - width[0][1] - 3, height[1] - height[2] - 6, 4);
//		cb.eoFill();
//		cb.beginText();
//		cb.setRGBColorStroke(0x9a, 0xe4, 0xe8);
//		cb.setFontAndSize(bf, 36);
//		cb.setTextRenderingMode(PdfContentByte.TEXT_RENDER_MODE_STROKE);
//		cb.showTextAligned(Element.ALIGN_LEFT, "\"", width[0][1] - 4, height[1] - 29, 0);
//		cb.endText();

		drawAllConnection(width, height, headerRows, cb);
		cb.restoreState();
		this.start_row += width.length - headerRows;
	}
}
