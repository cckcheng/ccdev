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
Ext.namespace('famtree');

famtree.startApp = function() {
        famtree.global.dsModules = famtree.generalStore('actionServlet', {
            dowhat: 'Admin', action: 'getModules'
        }, 'results', true); 

	famtree.welcomeMsg = famtree.getPhrase(famtree.SYSTEM_NAME) + ' - '
                + famtree.getPhrase('Welcome') + ' ' + famtree.global.user.fullname;

	Ext.MessageBox.minWidth = 260;
	var wp = Ext.get('wallpaper');
	if(wp) wp.hide();

	function action(btn) {
		if(lastBtn != btn) {
			if(lastBtn) lastBtn.toggle(false);
			btn.toggle(true);
			lastBtn = btn;
		}
		var p = btn.ownerCt.ownerCt;
		var panel_id = btn.id + '-panel';
		var panel = p.getComponent(panel_id);
		if(!panel) {
			switch(btn.id) {
				case 'famtree-builder':
					p.add(new famtree.FamtreePanel());
					break;
				case 'famtree-admin':
					p.add(new famtree.adminPanel());
					break;

			}
		}
		if (btn.id == 'famtree-admin'){
			var admin_tabpanel = Ext.getCmp('famtree_admin_tabpanel_id');
			admin_tabpanel.setActiveTab(0);
		}

		p.layout.setActiveItem(panel_id);
	}

	var mainMenuItems = [];
	var mainPanels = [];
//	var activePanelID = 0;
	var lastBtn;

	var modules = famtree.global.modules;
	if(!modules) return;

	if(modules.builder) {
		mainMenuItems.push({
			id: 'famtree-builder',
			text: famtree.getPhrase('Family Tree'),
			iconCls: 'request'
		});
	}

	mainMenuItems.push('->');

	if(modules.Admin) {
		mainMenuItems.push({
			id: 'famtree-admin',
			text: famtree.getPhrase('Admin'),
			iconCls: 'edit'
		});
		mainMenuItems.push(' ');
		mainMenuItems.push(' ');
	}

        mainMenuItems.push({
                id: 'famtree-password',
                text: famtree.getPhrase('Change Password'),
                iconCls: 'edit',
                handler: function() {
                        famtree.change_password(false);
                }
        });
        mainMenuItems.push(' ');
        mainMenuItems.push(' ');

	mainMenuItems.push({
		text: famtree.getPhrase('Logout'),
		iconCls: 'logout',
		scope: this,
		handler: function(btn){
			Ext.TaskMgr.stopAll();
			var p = btn.ownerCt.ownerCt;
			p.destroy();
			famtree.logout();
		}
	});

//	mainMenuItems.push({
//		text: famtree.getPhrase('Help'),
//		iconCls: 'pdf',
//		scope: this,
//		handler: function(btn){
//			window.open('uploadServlet?filetype=help', 'download_win');
//		}
//	});

	var viewport = new Ext.Viewport({
		layout:'border',
		items:[
		new Ext.Panel({
			title: famtree.welcomeMsg,
			id:'main-panel',
			region:'center',
			margins:'0 3 0 3',
			border: false,
			tbar: famtree.MainMenu = new Ext.Toolbar({
				defaults: {
//					scale: 'medium',
					handler: action
				},
				items: mainMenuItems
			}),

			layout: 'card',
//			activeItem: activePanelID,
			items: mainPanels
		})
		]
	});

	if(famtree.global.firstTime) {
		famtree.change_password(true);
	} else {
		var firstMenuitem = famtree.MainMenu.get(0);
		if(firstMenuitem && firstMenuitem.rendered) action(firstMenuitem);
	}
};

function famtree_init() {
	Ext.Ajax.request({
		method:'POST',
		url: 'loginServlet',
		success: handleResponse,
		failure: handleFail,
		scope: this,
		params: {
			act:'getglobal'
		}
	});

	function handleResponse(response){
		this.transId = false;
		var json = response.responseText;
		try {
			var o = eval("("+json+")");
			famtree.global=o;
			if (o.failcode!=undefined) {
				famtree.login();
				return;
			}
			this.version=o.macro.VERSION;
		}catch(e){
			famtree.login();
			return;
		}
		famtree.startApp();
	}
	function handleFail() {
		famtree.login();
		return;
	}
};

Ext.onReady(function(){
	Ext.QuickTips.init();
	Ext.BLANK_IMAGE_URL = 'ext-3.0.0/resources/images/default/s.gif';
	famtree_init();
});

