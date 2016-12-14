/*
 * Author: Colin Cheng @ Fortinet Inc.
 */
Ext.namespace('famtree');

famtree.adminPanel = function() {
		var userpanel = new famtree.user();
        var grouppanel = new famtree.group();
        var detail = new Ext.TabPanel({
					id: 'famtree_admin_tabpanel_id',
					region: 'center',
					minTabWidth: 115,
					tabWidth:135,
					enableTabScroll:true,
					margins: '5 0 0 0',
					width:'100%',
					height:'100%',
					//activeTab: 0,
					defaults: {autoScroll:true},
					items:[ userpanel,
							grouppanel
					],
					xtype: 'tabpanel'
		});
        detail.on('tabchange',function(tabPanel,act) {
                	switch (act.panelType) {

                        case "user":
							userpanel.find_user.setValue('');
							var ds = userpanel.getStore();
							userpanel.getView().scroller.scrollTo('top',0);
							ds.baseParams.action = 'getUserList';
							ds.load();
							break;
                        case "group":
							var ds = grouppanel.getStore();
							ds.load();
							break;
                        default:
							break;
					}
        }, this);

	var config = {
		id: 'famtree-admin-panel',
		layout: 'fit',
		items:[detail],
		title: 'Admin'
		//,
		//listeners: {
		//	scope: this,
		//	'show': function(){
		//		var myuser = Ext.getCmp('famtree_user_grid_id');
		//		myuser.getStore().load();
		//	}
		//}

	};

	Ext.Panel.superclass.constructor.call(this,config);
}

Ext.extend(famtree.adminPanel, Ext.Panel, {});

