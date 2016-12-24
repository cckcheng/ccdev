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
famtree.del_cookie = function()
{
	var d = new Date();
	d.setTime ( d.getTime() - 10 );
	var c = document.cookie + ";";
	var re = /\s?(.*?)=(.*?);/g;
	var matches;
	while((matches = re.exec(c)) != null){
		var name = matches[1];
		var value = matches[2];
		document.cookie = name+"=; expires=" + d.toGMTString() + ";" + ";";
	}

};

famtree.msg = function(title, msg){
	Ext.Msg.show({
		title: famtree.getPhrase(title),
		msg: famtree.getPhrase(msg),
		minWidth: 200,
		modal: true,
		icon: Ext.Msg.INFO,
		buttons: Ext.Msg.OK
	});
};

famtree.enableContextMenu = function() {
	if(MyDesktop.global.enableContext == true) {
		return true;
	}
	return false;
};

famtree.formsubmit_fail = function (theform, action) {
	var json = action.result;
	if (json.failcode == -1) {
		famtree.logout();
	} else {
		var exit_fun = famtree.logout;
		if (json.failcode == -2) exit_fun = famtree.ignore;
		Ext.Msg.show({
			title: famtree.getPhrase('Error'),
			msg: famtree.getPhrase(json.message),
			buttons: Ext.Msg.OK,
			minWidth: 360,
			fn: exit_fun,
			animEl: 'elId',
			icon: Ext.MessageBox.QUESTION
		});
	}
};


famtree.formsubmit = function(url,bp,success_call,fail_call) {
	var fail_fn = famtree.formsubmit_fail;
	if (fail_call) fail_fn = fail_call;

	return {
		method:'POST',
		url:url,
		waitMsg:'Processing',
		params:bp,
		failure: fail_fn,
		success: function(form,action) {
			if (action.result.success == false || action.result.success == "false") {
				var exit_fun = famtree.logout;
				if (action.result.failcode == -2) exit_fun = famtree.ignore;
				Ext.Msg.show({
					title: famtree.getPhrase('Error'),
					msg: famtree.getPhrase(action.result.message),
					buttons: Ext.Msg.OK,
					minWidth: 360,
					fn: exit_fun,
					animEl: 'elId',
					icon: Ext.MessageBox.QUESTION
				});
			} else {
				if(success_call && typeof success_call == 'function') success_call(form,action);
			}
		}
	};
};

famtree.submitForm = function(url, form, params, callback, err_title, msg_winsize) {
	form.submit({
		url: url,
		params: params,
		waitMsg: 'Processing...',
		success: function(f,action) {
			if (action.result.success == false || action.result.success == "false") {
				if (msg_winsize) Ext.Msg.minWidth = msg_winsize;
				if (err_title)
					Ext.Msg.alert(famtree.getPhrase(err_title), famtree.getPhrase(action.result.message));
				else
					Ext.Msg.alert(famtree.getPhrase('Error'), famtree.getPhrase(action.result.message));
				Ext.Msg.minWidth = 200;
			} else {
				if(callback && typeof callback == 'function') callback(f, action);
			}
		},
		failure: function(f, action) {
			var msg = '';
			switch(action.failureType){
				case Ext.form.Action.CLIENT_INVALID:
					msg += 'Please amend the invalid field(s).';
					break;
				case Ext.form.Action.CONNECT_FAILURE:
					msg += 'Connection failure.';
					break;
				case Ext.form.Action.SERVER_INVALID:
					msg += 'Server is invalid.';
					break;
				case Ext.form.Action.LOAD_FAILURE:
					msg += action.result.message;
					break;
			}
			Ext.Msg.alert(famtree.getPhrase('Failure'), famtree.getPhrase(msg));
		}
	});
};

famtree.loadForm = function(form, params, callback) {
	form.load({
		url: 'actionServlet',
		params: params,
		success: function(f,action) {
			if(callback && typeof callback == 'function') callback(f, action);
		},
		failure: function(f, action) {
			var msg = '';
			switch(action.failureType){
				case Ext.form.Action.CLIENT_INVALID:
					msg += 'Please amend the invalid field(s).';
					break;
				case Ext.form.Action.CONNECT_FAILURE:
					msg += 'Connection failure.';
					break;
				case Ext.form.Action.SERVER_INVALID:
					msg += 'Server is invalid.';
					break;
				case Ext.form.Action.LOAD_FAILURE:
					msg += action.result.message;
					break;
			}
			Ext.Msg.alert(famtree.getPhrase('Failure'), famtree.getPhrase(msg));
		}
	});
};
/**
 * this is a alterative function of DataLoader
 *  the param is an object
 */
famtree.DataLoaderAlt = function(o) {
	famtree.DataLoader(o.url, o.param, o.scope, o.success, o.fail, o.timeout);
};

famtree.DataLoader = function(url,bp,scope,callback,fail_call, timeout){

	var fail_fn = handleFailure;
	if (fail_call) fail_fn = fail_call;
	else fail_fn = famtree.ignore;

	Ext.Ajax.request({
		method:'POST',
		url: url,
		success: handleResponse,
		failure: function(response){
			Ext.Msg.alert(famtree.getPhrase("Action Failed"), response.statusText + "<br>Status: " + response.status
				+ "<br>" + famtree.getPhrase("Please try again later"));
		//fail_fn(scope, response);
		},
		scope: this,
		timeout: timeout? timeout: 30000,	// default 30"
		params: bp
	});

	function handleResponse(response){
		this.transId = false;
		var json = response.responseText;
		try {
			var o = Ext.util.JSON.decode(json);
		}catch(e){
			fail_fn(scope,response);
			return;
		}
		callback(scope,o);
	}

	function handleFailure(response){
		alert("Fail:"+response);
	}
};

famtree.ignore = function(s,o){
	};

famtree.dataload_fail = function(s,o){
	Ext.Msg.show({
		title: famtree.getPhrase('Error'),
		msg: famtree.getPhrase(o.message),
		buttons: Ext.Msg.OK,
		minWidth: 360,
		fn: famtree.logout,
		animEl: 'elId',
		icon: Ext.MessageBox.QUESTION
	});
};


famtree.win_close = function(w) {
	w.close();
};

famtree.close_window = function() {
	MyDesktop.winlock=0;
	Ext.WindowMgr.each(famtree.win_close);
};

famtree.logout = function()
{
	famtree.del_cookie();
	famtree.DataLoader('loginServlet',{
		'act':'logout'
	},this, function(){
		window.location='index.html';
	});
	return;
};

famtree.handle_return_exception = function(event, options, response, error)
{
	var json = Ext.decode(response.responseText);
	if(json == undefined) {
		Ext.Msg.alert(famtree.getPhrase("Action Failed"), response.statusText + "<br>Status: " + response.status
			+ "<br>" + famtree.getPhrase("Please try again later"));
		return;
	}
	if (json.failcode == -1) {
		famtree.logout();
	} else {
		Ext.Msg.show({
			title: famtree.getPhrase('Error'),
			msg: famtree.getPhrase(json.message),
			buttons: Ext.Msg.OK,
			scope: this,
			fn: famtree.logout,
			animEl: 'elId',
			icon: Ext.MessageBox.QUESTION
		});
	}
};

famtree.handle_server_exception = function(event, options, response, error)
{
	var json = Ext.decode(response.responseText);
	if(json == undefined) {
		Ext.Msg.alert(famtree.getPhrase("Action Failed"), response.statusText + "<br>Status: " + response.status
			+ "<br>" + famtree.getPhrase("Please try again later"));
		return;
	}
	if (json.failcode == -1) {
		famtree.logout();
	} else {
		Ext.Msg.show({
			title: famtree.getPhrase('Error'),
			msg: famtree.getPhrase(json.message),
			buttons: Ext.Msg.OK,
			scope: this,
			fn: function() {
				if(typeof this.win_close == 'function') this.win_close();
			},
			animEl: 'elId',
			icon: Ext.MessageBox.QUESTION
		});
	}
};

/* seems above functions famtree.handle_return_exception and famtree.handle_server_exception don't work in right way.
   famtree.handle_return_exception, will certernly logout; the this.win_close() in famtree.handle_server_exception need to be
   created inside the window class.
*/
famtree.handle_back_exception = function(event, options, response, error)
{
	var json = Ext.decode(response.responseText);
	if(json == undefined) {
		Ext.Msg.alert(famtree.getPhrase("Action Failed"), response.statusText + "<br>Status: " + response.status
			+ "<br>" + famtree.getPhrase("Please try again later"));
		return;
	}
	if (json.failcode == -1) {
		famtree.logout();
	} else {
		Ext.Msg.show({
			title: famtree.getPhrase('Error'),
			msg: famtree.getPhrase(json.message),
			buttons: Ext.Msg.OK,
			minWidth: 360,
			scope: this,
			fn: function() {
				//if(json.message.search(/Permission deny/i) >= 0) this.win_close();
				return;
			},
			animEl: 'elId',
			icon: Ext.MessageBox.QUESTION
		});
	}
};

famtree.doServerAction = function(param, doAction, scope, loadMask, timeout) {
	if(loadMask) Ext.Msg.wait(famtree.getPhrase('Processing') + '...', famtree.getPhrase('Please Wait'));
	famtree.DataLoaderAlt({
		url: 'actionServlet',
		param: param,
		scope: scope,
		success: function(scope, result) {
			if(result.success == 'false'){
				if(result.failcode != -2)
					famtree.logout();
				else
					Ext.Msg.alert(famtree.getPhrase('Fail'), famtree.getPhrase(result.message));
			} else {
				if(loadMask) Ext.Msg.hide();
				doAction(scope, result);
			}
		},
		fail: function(scope, result) {
			Ext.Msg.alert(famtree.getPhrase('Warning'), famtree.getPhrase('Server action failed') + ': ' + famtree.getPhrase(result.message));
		},
		timeout: timeout
	});
};

/****************************************************
 * doServerAction2 will call the call back function even the result.success is false
 * */
famtree.doServerAction2 = function(param, doAction, scope, loadMask) {
	if(loadMask) Ext.Msg.wait(famtree.getPhrase('Processing') + '...', famtree.getPhrase('Please Wait'));
	famtree.DataLoader('actionServlet', param, scope,
		function(scope, result) {
			if(result.success == 'false'){
				if(result.failcode != -2)
					famtree.logout();
				else {
                                        Ext.Msg.alert(famtree.getPhrase('Fail'), famtree.getPhrase(result.message));
					doAction(scope, result);
				}
			} else {
				if(loadMask) Ext.Msg.hide();
				doAction(scope, result);
			}
		},
		function(scope, result) {
			Ext.Msg.alert(famtree.getPhrase('Warning'), famtree.getPhrase('Server action failed') + ': ' + famtree.getPhrase(result.message));
		}
	);
};

// General message box for confirming action
// msg: the message to disply
// action: call action(scope, true) when user clicked "Yes", or action(scope, false) on clicking "No"
// buttons: Ext.MessageBox.YESNOCANCEL (default to Ext.MessageBox.YESNO)
famtree.confirmMessageBox = function(msg, action, scope, buttons) {
	if(scope == undefined) scope = this;
	Ext.MessageBox.buttonText = {
		ok : famtree.getPhrase("OK"),
		cancel : famtree.getPhrase("Cancel"),
		yes : famtree.getPhrase("Yes"),
		no : famtree.getPhrase("No")
	};

	Ext.MessageBox.show({
		scope: scope,
		title: famtree.getPhrase('Confirm'),
		msg: msg,
		width: 450,

		buttons: (buttons ? buttons : Ext.MessageBox.YESNO),
		fn: function(btn, text){
			if (btn=="yes") {
				action(scope, true);
				return true;
			}
			if(buttons == Ext.MessageBox.YESNOCANCEL && btn == "no") {
				action(scope, false);
				return true;
			}

			return false;
		},
		icon: Ext.MessageBox.QUESTION
	});
};

famtree.promptMessageBox = function(msg, action, scope) {
	if(scope == undefined) scope = this;
	Ext.MessageBox.buttonText = {
		ok : famtree.getPhrase("OK"),
		cancel : famtree.getPhrase("Cancel"),
		yes : famtree.getPhrase("Yes"),
		no : famtree.getPhrase("No")
	};

	Ext.Msg.prompt(famtree.getPhrase('Confirm'), msg,
		function(btn, text){
			if (btn=="yes" || btn=='ok') {
				action(scope, text);
				return true;
			}

			return false;
		}
		, scope);
};

famtree.commentBox = function(title, msg, action, scope, initValue) {
	if(scope == undefined) scope = this;
	Ext.MessageBox.buttonText = {
		ok : famtree.getPhrase("OK"),
		cancel : famtree.getPhrase("Cancel"),
		yes : famtree.getPhrase("Yes"),
		no : famtree.getPhrase("No")
	};

	Ext.Msg.prompt(famtree.getPhrase(title), famtree.getPhrase(msg),
		function(btn, text){
			if (btn=="yes" || btn=='ok') {
				action(scope, text);
				return true;
			}

			return false;
		}
		, scope, true, initValue);
};

famtree.noteBox = function(title, msg, initValue, action, scope) {
	if(scope == undefined) scope = this;
	Ext.MessageBox.buttonText = {
		ok : famtree.getPhrase("OK"),
		cancel : famtree.getPhrase("Cancel"),
		yes : famtree.getPhrase("Yes"),
		no : famtree.getPhrase("No")
	};

	var vpSize = Ext.getBody().getViewSize();
	Ext.Msg.maxWidth = vpSize.width * 0.95;
	Ext.Msg.show({
		title: famtree.getPhrase(title),
		msg: famtree.getPhrase(msg),
		value: initValue,
		prompt: true,
		width: vpSize.width * 0.8,
		multiline: vpSize.height * 0.8,
		scope: scope,
		fn: function(buttonId, text, opt) {
			if (buttonId=="yes" || buttonId=='ok') {
				if(action && typeof action == 'function') action(text);
				return true;
			}

			return false;
		},
		buttons: action ? Ext.MessageBox.OKCANCEL : Ext.MessageBox.OK
	});
};

famtree.blankStore = function() {
	var gStore = new Ext.data.JsonStore({
		fields: [{
			name: 'id',
			type: 'int'
		}, {
			name: 'name',
			type: 'string'
		}]
	});
	return gStore;
};

famtree.generalStore = function(url, params, root, autoLoad, remoteSort) {
	var gStore = new Ext.data.JsonStore({
		url: url,
		baseParams: params,
		autoLoad: autoLoad ? autoLoad : false,
		root: root,
		fields: [{
			name: 'id',
			type: 'int'
		}, {
			name: 'name',
			type: 'string'
		}],
		remoteSort: (remoteSort == undefined ? false : remoteSort)
	});
//	if(autoLoad) gStore.load();
	gStore.on({
		'loadexception':famtree.handle_return_exception,
		scope:this
	});
	/*
		// place this hundler in your own function if needed
		gStore.on('load', function() {
			gStore.insert(0, new Ext.data.Record({'id':-1, 'name': 'Any'}));
		});
	*/
	return gStore;
};

famtree.simpleStore = function() {
	var gStore = new Ext.data.JsonStore({
		fields: [{
			name: 'id',
			type: 'int'
		}, {
			name: 'name',
			type: 'string'
		}]
	});
	return gStore;
};

famtree.userList = function(project_id, autoLoad) {
	var action = 'viewUsers';
	if(autoLoad == true)
		return famtree.generalStore('actionServlet',
		{
			dowhat: 'OptUser',
			action: action,
			start: -1,
			project_id: project_id
		},
		'users', true);
	else
		return famtree.generalStore('actionServlet',
		{
			dowhat: 'OptUser',
			action: action,
			start: -1,
			project_id: project_id
		},
		'users', false);
};

famtree.groupList = function(autoLoad) {
        return famtree.generalStore('actionServlet',
		{
			dowhat: 'Admin',
			action: 'getGroupList',
			shorted: 1
		},
		'groups', autoLoad);
};

famtree.getGlobal = function(){

	Ext.Ajax.request({
		method:'POST',
		url: 'loginServlet',
		success: handleResponse,
		failure: handleFailure,
		scope: this,
		params: {
			act:'getglobal'
		}
	});

	function handleResponse(response){
		this.transId = false;
		var json = response.responseText;
		try {
			var o = Ext.util.JSON.decode(json);
			MyDesktop.global=o;
			MyDesktop.version=o.macro.VERSION;
		}catch(e){
			Ext.Msg.show({
				title: famtree.getPhrase('Error'),
				msg: json,
				buttons: Ext.Msg.OK,
				minWidth: 360,
				fn: famtree.logout,
				animEl: 'elId',
				icon: Ext.MessageBox.QUESTION
			});

		}
	}

	function handleFailure(response){
		alert("Fail:"+response);
	}
};

famtree.htmlEncode = function(value) {
	var a = Ext.util.Format.htmlEncode(value);
	a = a.replace(/\t/g, '&nbsp;&nbsp;&nbsp;&nbsp;');
	return a.replace(/\n/g, '<br>');
};

famtree.autoWrap = function(value){
	return '<p style="white-space:normal; margin:0; padding:0;">' + famtree.htmlEncode(value) + '</p>'
};
famtree.autoWrapDiv = function(value){
	return '<div style="display:inline; white-space:normal; margin:0; padding:0;">' + famtree.htmlEncode(value) + '</div>'
};
famtree.informationCenter = function(title, format){
	function createBox(t, s){
		return ['<div class="msg">',
		'<div class="x-box-tl"><div class="x-box-tr"><div class="x-box-tc"></div></div></div>',
		'<div class="x-box-ml"><div class="x-box-mr"><div class="x-box-mc"><h3>', t, '</h3>', s, '</div></div></div>',
		'<div class="x-box-bl"><div class="x-box-br"><div class="x-box-bc"></div></div></div>',
		'</div>'].join('');
	}

	if(MyDesktop.mWin == undefined) {
		MyDesktop.mWin = new Ext.Window({
			width: 200,
			height: 50,
			closable: false,
			border: false
		});
		MyDesktop.mWin.show();
		MyDesktop.mWin.alignTo(MyDesktop.desktop.taskbar.tbPanel.getEl(), 'tl-br', [100, 100]);
	}

	var s = MyDesktop.mWin.getEl();
	var m = Ext.DomHelper.overwrite(s, {
		html:createBox(title, format)
	}, true);
	m.alignTo(MyDesktop.desktop.taskbar.tbPanel.getEl(), 'br-tr', [+60,-20]);
	m.slideIn('br').pause(5).ghost("br", {
		remove:true,
		duration: 1
	});
//(function(){win.close();}).defer(7000);
};

// This function removes non-numeric characters
famtree.stripNonNumeric = function( str ){
	str += '';
	var rgx = /^\d|\.|-$/;
	var out = '';
	for( var i = 0; i < str.length; i++ )
	{
		if( rgx.test( str.charAt(i) ) ){
			if( !( ( str.charAt(i) == '.' && out.indexOf( '.' ) != -1 ) ||
				( str.charAt(i) == '-' && out.length != 0 ) ) ){
				out += str.charAt(i);
			}
		}
	}
	return out;
};

famtree.popWindow = function(url, name) {
	var newwindow=window.open(url, name ,
		'centerscreen=yes, resizable=yes, scrollbars=yes, menubar=yes, location=yes');
	if (window.focus) {
		newwindow.focus();
	}
};

famtree.ellipsisString = function(ml, text, width){
	var tm = Ext.util.TextMetrics.createInstance(ml);

	var w = tm.getWidth(text);
	if(w < width) return text;
	var len = text.length;
	var num = Math.round(width / w * len);

	var tmp = text.substring(0, num);
	w = tm.getWidth(tmp);
	if(w > width){
		while(w > width){
			tmp = text.substring(0, --num);
			w = tm.getWidth(tmp);
		}
		return tmp.substring(0,num-3) + '...';
	}
	while(w < width){
		tmp = text.substring(0, ++num);
		if(num === len) return text;
		w = tm.getWidth(tmp);
	}
	return tmp.substring(0,num-4) + '...';
};

// join two object to one
// keep obj1's values if there is same property in obj2
famtree.jointObject = function(obj1, obj2) {
	if(obj2 == undefined || obj2 == null){
		return obj1;
	}
	if(typeof obj1 != 'object' || typeof obj2 != 'object') {
		return {};
	}
	var str1 = Ext.util.JSON.encode(obj1);
	var str2 = Ext.util.JSON.encode(obj2);
	if(str1.length > 0 && str2.length > 0) {
		str1 = str2.substring(0, str2.length-1) + "," + str1.substring(1);
	}
	return Ext.util.JSON.decode(str1);
};
famtree.hasIllegalChar = function (inputStr){
		var regex = /[%,',\\,\/]/;
		return regex.test(inputStr);
};

famtree.is4DiditNumber = function(inputStr) {
	if(inputStr.length != 4) return false;
	var regex = /\D/;
	return !regex.test(inputStr);
};
famtree.isDidit = function(inputStr) {
	var regex = /\D/;
	return !regex.test(inputStr);
};
famtree.isFloat = function(str) {
	return str == (1.0 * str);
};
/**
 * return all the values of a object
 */
famtree.getObjectValues = function(obj) {
	var str = '';
	for(var o in obj) {
		str += ',' + obj[o];
	}
	return '{' + str.substring(1) + '}';
};

famtree.hideZero = function(v) {
	return v==0 ? '' : v;
};

/**
 * famtree.CustomWindow
 */
famtree.CustomWindow = function(conf) {
	var title = conf.title,
		items = conf.items,
		closeAction = conf.closeAction,
		anchor = conf.anchor,
		width = conf.width,
		resizable = conf.resizable,
		height = conf.height;

	if(anchor && typeof anchor == 'number') {
		if(anchor > 1) anchor = 1;
		if(anchor < 0.1) anchor = 0.1;
		var vpSize = Ext.getBody().getViewSize();
		width = vpSize.width * anchor;
		height = vpSize.height * anchor;
	}
	var config = {
		id: conf.id ? conf.id : 'famtree-custom-win',
		autoDestroy: true,
		title: title,
		layout:'fit',
		plain: true,
		resizable: resizable,
		constrain:true,
		items: items,
		autoScroll: true,
		listeners: {
			'close': function(p) {
				if(closeAction) closeAction(conf.scope);
			}
		},
		modal: (conf.modal==false || conf.modal) ? conf.modal : true
	};
//	if(anchor == 1) config.maximized = true;
	var itemNum = items.length;
	if(itemNum > 1) {
		config.layout = 'border';
	}

	if(width){
		config.width = width;
	} else {
		config.autoWidth = true;
	}
	if(height) {
		config.height = height;
	} else {
		config.autoHeight = true;
	}

	if(conf.closable != undefined) {
		config.closable = conf.closable;
	}
	return new Ext.Window(config);

};

famtree.setSplitter = function(config) {
	config.split = true;
	config.collapseMode = 'mini';
	config.collapsible = true;
};
famtree.countEnter4IE = function(str, to) {
	if (str.length == 0) return 0;
	if (str.length < to) to = str.length;
	var counter = 0;
	
	var prev = '';
	for (var i=0; i<to; i++){
		var curr = str.charAt(i);
		if ((prev == '\n' && curr == '\r') || (prev == '\r' && curr == '\n')) {
             prev = '';
			 counter++;

		}else if (prev == '\n' || prev == '\r') {
			 counter++;
			 prev = curr;
		}else{
			 prev = curr;
		}
		
	}
	return counter;
};
famtree.mac2long = function(str) {
	if (str=='0') return 0;
	if (str=='') return 0;
	var arr= str.split(':');
	if (arr.length != 6) return -1;
	var result = 0;
	for (var i=0; i<6; i++){
		result = result * 256;
		result += parseInt(arr[i],16);
		if (isNaN(result)) return -1;
	}
	return result;
};
famtree.long2mac = function(number) {
	if (number == '' || number == 0 || number == undefined) return '0:0:0:0:0:0';

	var den5 = 256*256*256*256*256;
	var c6 = Math.floor(number/den5);
	var m6 = c6.toString(16);

	number = number - c6*den5;

	var den4 = 256*256*256*256;
	var c5 = Math.floor(number/den4);
	var m5 = c5.toString(16);

	number = number - c5*den4;

	var den3 = 256*256*256;
	var c4 = Math.floor(number/den3);
	var m4 = c4.toString(16);

	number = number - c4*den3;

	var den2 = 256*256;
	var c3 = Math.floor(number/den2);
	var m3 = c3.toString(16);

	number = number - c3*den2;

	var den1 = 256;
	var c2 = Math.floor(number/den1);
	var m2 = c2.toString(16);

	number = number - c2*den1;
	var m1= number.toString(16);
	return "" + m6 + ":" + m5 + ":" + m4 + ":" + m3 + ":" + m2 + ":" + m1;

};
famtree.showinfo = function(msg) {
	Ext.Msg.show({
		title: famtree.getPhrase('Information'),
		msg: famtree.getPhrase(msg),
		buttons: Ext.Msg.OK,
		icon: Ext.MessageBox.INFO
	});
};


