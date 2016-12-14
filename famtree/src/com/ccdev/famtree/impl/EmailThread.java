/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ccdev.famtree.impl;

/**
 *
 * @author Colin Cheng
 */
public class EmailThread extends Thread {
	String from, mailto, filename, msg, subject;
	EmailThread(String from, String mailto, String filename, String msg, String subject) {
		this.from = from;
		this.mailto = mailto;
		this.filename = filename;
		this.msg = msg;
		this.subject = subject;
	}

	@Override
	public void run() {
		myUtil.sendEmail(this.from, this.mailto, this.filename, this.msg, this.subject);
	}
}
