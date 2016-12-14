package com.ccdev.famtree.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/** CsvReader.class
 *		provide the general read function from a standard csv file
 *
 * @author Colin Cheng
 */
public class CsvReader {

	public final static int STATE_NORMAL = 0;
	public final static int STATE_EOL = 1;
	public final static int STATE_EOF = 2;
	private int state = STATE_NORMAL;
	private BufferedReader r;
	private char separatorChar;
	private char quoteChar;
	private char[] cbuf = new char[1024];
	private int offset = 0;
	private int chars_in_buf = 0;

	/**
	 * Constructor
	 *			default to comma separator, " for quote
	 *
	 * @param r		input reader
	 */
	public CsvReader(Reader r) {
		this(r, ',', '\"');
	}

	/**
	 * Constructor
	 *
	 * @param r					input reader
	 * @param separatorChar		field separator character, usually ',' in North America, ';' in Europe and sometimes
	 *                          '\t' for tab. Note this is a 'char' not a "string".
	 * @param quoteChar			char to use to enclose fields containing a separator, usually '\"'.
	 */
	public CsvReader(Reader r, char separatorChar, char quoteChar) {
		this.r = (r instanceof BufferedReader) ? (BufferedReader) r : new BufferedReader(r);    /* default buffer size is 8K */
		this.separatorChar = separatorChar;
		this.quoteChar = quoteChar;
	}

	public void close() throws IOException {
		if (r != null) {
			r.close();
			r = null;
		}
	}

	public int getState() {
		return state;
	}

	public String readLine() throws IOException {
		return r.readLine();
	}

	/**
	 * get a field
	 * @return a string
	 */
	public String get() throws IOException {
		StringBuilder field = new StringBuilder(512);
		this.state = CsvReader.STATE_NORMAL;

		if (!readOnDemand()) {
			return "";
		}
		char c = 0;
		if (this.quoteChar == 0) {
			while ((c = cbuf[this.offset]) != this.separatorChar && !isLineEnd(c)) {
				field.append(c);
				this.offset++;
				this.chars_in_buf--;
				if (!readOnDemand()) {
					return field.toString();
				}
			}
		} else {
			c = cbuf[this.offset];
			if (c != this.separatorChar && !isLineEnd(c)) {
				if (c != this.quoteChar) {
					// treat as no quote char
					do {
						field.append(c);
						this.offset++;
						this.chars_in_buf--;
						if (!readOnDemand()) {
							return field.toString();
						}
						c = cbuf[this.offset];
					}while(c != this.separatorChar && !isLineEnd(c));
				} else {
					while (true) {
						this.offset++;
						this.chars_in_buf--;
						if (!readOnDemand()) {
							return field.toString();
						}
						while ((c = cbuf[this.offset]) != this.quoteChar) {
							field.append(c);
							this.offset++;
							this.chars_in_buf--;
							if (!readOnDemand()) {
								return field.toString();
							}
						}
						this.offset++;
						this.chars_in_buf--;
						if (!readOnDemand()) {
							return field.toString();
						}
						c = cbuf[this.offset];
						if (c == this.quoteChar) {
							// it's a '"', continue reading
							field.append(c);
							continue;
						} else if (c == this.separatorChar || isLineEnd(c)) {
							// finished field
							break;
						} else {
							// sth's wrong, move to next line
							moveToNextLine();
							break;
						}
					}
				}
			}
		}
		this.offset++;
		this.chars_in_buf--;
		return field.toString();
	}

	private boolean isLineEnd(char c) throws IOException {
		if (c == '\r' || c == '\n') {
			int n = this.offset + 1;
			if(this.chars_in_buf <= 1){
				this.chars_in_buf = 0;
				if (!readOnDemand()) {
					return true;
				}
				n = 0;
			}
			char d = cbuf[n];
			if ((c == '\r' && d == '\n') || (c == '\n' && d == '\r')) {
				if(n > 0) {
					this.offset++;
					this.chars_in_buf--;
				}
			}
			this.state = CsvReader.STATE_EOL;
			return true;
		}
		return false;
	}

	private void moveToNextLine() throws IOException {
		char c = 0;
		do {
			this.offset++;
			this.chars_in_buf--;
			if(!readOnDemand()) break;
			c = this.cbuf[this.offset];
		} while(!isLineEnd(c));
	}

	private boolean readOnDemand() throws IOException {
		if (this.chars_in_buf <= 0) {
			this.offset = 0;
			this.chars_in_buf = r.read(cbuf);
			if (this.chars_in_buf <= 0) {
				this.state = CsvReader.STATE_EOF;
				return false;
			}
		}

		return true;
	}

	public boolean mark(int limitAhead) throws IOException{
		if(!r.markSupported()) return false;
		r.mark(limitAhead);
		return true;
	}

	public boolean reset() throws IOException{
		if(!r.markSupported()) return false;
		r.reset();
		return true;
	}
}
