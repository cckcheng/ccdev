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
package com.ccdev.famtree.impl;

import com.ccdev.famtree.bean.Config;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;

/**
 * init & load system configs
 * @author Colin Cheng
 */
@SuppressWarnings(value = {"unchecked", "serial"})
public class SystemConfig {
	// define all keys here, all keys are recommended to be in UPPERCASE
	public static final String SMTP = "SMTP";
	public static final String DEBUG_LEVEL = "DEBUG_LEVEL";
	public static final String DEVELOP_TEAM_EMAILS = "DEVELOP_TEAM_EMAILS";
        
        public static final String MAINTAIN_MODE = "MAINTAIN_MODE";
        public static final String USE_NEST_TABLE = "USE_NEST_TABLE";
        public static final String PRINT_TABLE_BORDER = "PRINT_TABLE_BORDER";

        public static final String KEEP_WORKING_FOLDER = "KEEP_WORKING_FOLDER";

        public static final String SEND_EMAIL = "SEND_EMAIL";
        public static final String IS_TEST_SERVER = "IS_TEST_SERVER";

	protected static final Map<String, String> configs = new HashMap<String, String>() {
            {
                put(DEBUG_LEVEL, "5");
                put(IS_TEST_SERVER, "Y");
                put(USE_NEST_TABLE, "N");
                put(PRINT_TABLE_BORDER, "N");
            }
	};

	/**
	 * get a config value for a specific key
	 * @param key
	 * @return config value
	 */
	public static String getConfig(String key) {
                if(!configs.containsKey(key)) return "";
                return configs.get(key).trim();
	}

	/**
	 * load configs from DB
	 * @param em
	 */
	public static void loadConfig(EntityManager em) {
		List<Config> rs = myUtil.getExistRecords("config", "", "id", em, Config.class);
		for(Config o : rs) {
			configs.put(o.getK().trim().toUpperCase(), o.getV());
			myUtil.dbg(3,o.getK().trim().toUpperCase()+"=>"+o.getV());
		}
                
	}

	public static int getIntConfig(String key) {
		return Integer.parseInt(getConfig(key));
	}

	public static boolean getBoolean(String key) {
            String v = getConfig(key).toLowerCase();
            return v.startsWith("y") || v.startsWith("t") || v.startsWith("on") ;
	}
        
        static void setConfig(String k, String v, EntityManager em){
                String key = k.trim().toUpperCase();
                String value = v.trim().toUpperCase();
                configs.put(key, value);
                Config r = (Config)myUtil.getExistRecord("config", "k='" + key + "'", "id", em, Config.class);
                if (r == null){
                    Config c = new Config();
                    c.setK(key);
                    c.setV(value);
                    em.persist(c);
                }else{
                    r.setV(value);
                    em.merge(r);
                }
        }
}
