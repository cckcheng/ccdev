/*
 * Author: Colin Cheng @ Fortinet Inc.
 */
Ext.namespace('famtree');

famtree.startApp = function() {
	famtree.welcomeMsg = 'Fortinet Vendor Portal - Welcome ' + famtree.global.user.name;

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
				case 'famtree-bios-request':
					p.add(new famtree.biosRequestPanel());
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

	if(modules.Request) {
		mainMenuItems.push({
			id: 'famtree-bios-request',
			text: 'BIOS Request',
			iconCls: 'request'
		});
	}

	mainMenuItems.push('->');

	if(modules.Admin) {
		mainMenuItems.push({
			id: 'famtree-admin',
			text: 'Admin',
			iconCls: 'edit'
		});
		mainMenuItems.push(' ');
		mainMenuItems.push(' ');
	}

	if(famtree.global.ExternalUser) {
		mainMenuItems.push({
			id: 'famtree-password',
			text: 'Change Password',
			iconCls: 'edit',
			handler: function() {
				famtree.change_password(false);
			}
		});
		mainMenuItems.push(' ');
		mainMenuItems.push(' ');
	}

	mainMenuItems.push({
		text: 'Logout',
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
//		text: 'Help',
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
}

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
}

Ext.onReady(function(){
	Ext.QuickTips.init();
	Ext.BLANK_IMAGE_URL = 'ext-3.0.0/resources/images/default/s.gif';
	famtree_init();
});

