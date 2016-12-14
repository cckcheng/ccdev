/*
 * Author: Colin Cheng 
 */
Ext.namespace('famtree');

Ext.override(Ext.grid.GridView, {
	getEditorParent: function(ed) {
		//return this.mainWrap.dom;
		return Ext.getBody();
	}
});

famtree.SYSTEM_NAME = 'Family Tree';

famtree.userList = function(autoLoad) {
	return famtree.generalStore('actionServlet', {
			dowhat: 'admin',
			action: 'getUsers'
		}, 'results', autoLoad)
};

