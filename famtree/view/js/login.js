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
famtree.login = function() {
	var logoPanel = new Ext.Panel({
		baseCls: 'x-plain',
		id: 'login-logo',
		//style: 'margin-top:20px;',
		region: 'center'
	});

	var username = new Ext.form.TextField({
			fieldLabel: 'Username: (Email Address)',
			labelSeparator:'',
			allowBlank: false,
			blankText: 'Please Enter Your Email Address',
			name: 'user'
		});
	var logo_label = new Ext.form.Label({
		html: '<div style="text-align:center;font-size:large;margin-bottom:30px;margin-top:5px;">Family Tree Builder&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</div>'
	})
	var formPanel = new Ext.FormPanel({
		baseCls: 'x-plain',
		baseParams: { act: 'login' },
		defaults: { width: 200 },
		defaultType: 'textfield',
		frame: false,
		height: 150,
		id: 'login-form',
		items: [logo_label, username, {
			fieldLabel: 'Password',
			inputType: 'password',
			allowBlank: false,
			blankText: 'Please Enter Password',
			name: 'password'
		}],
		labelWidth:150,
		listeners: {
			'actioncomplete': { fn: onActionComplete, scope: this },
			'actionfailed': { fn: onActionFailed, scope: this }
		},
		region: 'south',
		//region: 'center',
		url: 'loginServlet'
	});

	function onSubmit(){
		showMask();
		formPanel.form.submit({ reset: true });
	}

	function hideMask(){
		this.pMask.hide();
		win.buttons[0].enable();
	}

	function onActionComplete(f, a){
		hideMask();
		if(a && a.result){
			if (a.result.success&& a.result.success == 'true') {
				win.destroy(true);
				famtree.global = a.result;
				famtree.startApp();
			} else {
				Ext.Msg.alert('Login Failed', a.result.message);
			}
		}
	}
	function onActionFailed(){
		hideMask();   // hide the process mask
		formPanel.form.reset();
	}

	function showMask(msg){
		if(!this.pMask){
                                // using this.pMask, seems that using this.mask caused conflict
                        	// when this dialog is modal (uses this.mask also)
			this.pMask = new Ext.LoadMask(win.body, {
					msg: 'Please wait, Authenticating.....'
				});
		}
		this.pMask.msg = msg;
		this.pMask.show();
		win.buttons[0].disable();
	}

	var win = new Ext.Window({
		buttons: [{ handler: onSubmit, scope: this, text: 'Login' }],
		buttonAlign: 'right',
		closable: false,
		draggable: false,
		height: 280,
		id: 'login-win',
		keys: {
			key: [13], // Enter Key on the Keyboard.  ACII Signal
			fn: onSubmit,
			scope:this
		},
		layout: 'border',
		//iconCls: 'about',
		minHeight: 250,
		minWidth: 430,
		plain: false,
		resizable: false,
		items: [ logoPanel, formPanel ],
                title: 'Login',
                width: 480
	});
	win.show();
	username.focus(true, 200);
};

famtree.change_password = function(firstTime) {
	var winWidth = 320;
	var items = [];
	if(!firstTime) {
		items.push({
			fieldLabel: 'Old Password',
			name: 'oldpassword'
		});
	}
	items.push({
		fieldLabel: 'New Password',
		name: 'newpassword'
	}, {
		fieldLabel: 'Confirm New Password',
		name: 'confirmpassword'
	});
	var fp = new Ext.FormPanel({
		frame: true,
		width: winWidth,
		autoHeight: true,
		defaultType: 'textfield',
		labelWidth: 150,
		defaults: {
			anchor: '98%',
			inputType: 'password',
			minLength: 6,
			maxLength: 47,
			allowBlank: false
		},
		items: items,
		monitorValid: true,
		buttons: [{
			text: 'Ok',
			formBind: true,
			handler: function(){
				var values = fp.form.getValues();
				if(values.newpassword != values.confirmpassword) {
					Ext.Msg.alert('Error', 'New password and confirm new password does not match.');
					return;
				}

				famtree.submitForm('actionServlet', fp.form, {
						dowhat: 'Admin',
						action: 'changePassword'
					}, function(){
						win.close();
						Ext.Msg.alert('Success', 'Your password is changed.')
					});
			}
		}, {
			text: 'Cancel',
			hidden: firstTime,
			handler: function(){
				win.close();
			}
		}]
	});
	var win = famtree.CustomWindow({
		title: 'Change Password',
		closable: false,
		width: winWidth,
		items: fp
	});
	win.show();
}
