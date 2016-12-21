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

