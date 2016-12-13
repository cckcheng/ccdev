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
package com.ccdev.pedigree;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

/**
 *
 * @author ccheng
 */
public class Common {
        public Common() {
        }
 
        private StringBuilder errmsg = new StringBuilder();
        public String getError() {
            return errmsg.toString();
        }
        public boolean hasError() {
            return errmsg.length() > 0;
        }

	public Properties loadConfigFile(String configFile) {
		Properties config = new Properties();

		try {
			config.load(new FileInputStream(configFile));
			return config;
		} catch (FileNotFoundException ex) {
			errmsg.append("\n").append(configFile).append(" not found.");
			return null;
		} catch (IOException ex) {
			errmsg.append("\nloadConfigFile: IO exception.");
			return null;
		}
	}

	public Connection getMysqlConnection(Properties conf) {
                errmsg.setLength(0);
		String ip = conf.getProperty("db_ip").trim();
		String dbname = conf.getProperty("db_name").trim();
		String connectionUrl = "jdbc:mysql://" + ip + ":3306/" + dbname;
		String username = conf.getProperty("db_username").trim();
		String password = conf.getProperty("db_password").trim();

		Connection conn = null;
		try {
			conn = DriverManager.getConnection(connectionUrl, username, password);
			return conn;
		} catch (Exception e) {
			errmsg.append("DB IP: ").append(ip).append(" - getMysqlConnection error\n");
			errmsg.append(e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
        
        static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        public static final String formatDate(Date dt) {
		if (dt == null) {
			return "";
		}
		return dateFormat.format(dt);
	}
        
        static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        public static final String formatTime(Date dt) {
		if (dt == null) {
			return "";
		}
		return timeFormat.format(dt);
	}
        
        public static final void cleanOldFiles(String path, int days) {
                Calendar cld = Calendar.getInstance();
                cld.add(Calendar.DAY_OF_YEAR, -days); 
                final long baseTime = cld.getTime().getTime();

                File archive_dir = new File(path);
                File[] filesRemove = archive_dir.listFiles(new FileFilter(){
                    @Override
                    public boolean accept(File f) {
                        return f.isFile() && f.lastModified() <= baseTime;
                    }
                });

                if(filesRemove == null) return;
                for(File f : filesRemove) {
                    f.delete();
                }
        }
}
