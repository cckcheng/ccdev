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
Ext.namespace('famtree.admin');

famtree.group = function () {

    var config = {
        id: 'famtree_group_grid_id',
        title: 'Group',
        panelType: 'group',
        width: '80%',
        height: 400,
        shim: false,
        animCollapse: false,
        constrainHeader: true,
        loadMask: true,
        viewConfig: {
            forceFit: true,
            enableRowBody: true,
            showPreview: true,
            getRowClass: this.applyRowClass
        }
    };

    var refreshButton = new Ext.Button({
        iconCls: 'refresh',
        text: 'Refresh',
        width: 80

    });
    refreshButton.on('click', function () {
        this.getStore().load();
    }, this);
    this.sm = new Ext.grid.RowSelectionModel({singleSelect: true});
    this.cm = new Ext.grid.ColumnModel([
        {
            header: 'Group ID',
            dataIndex: 'id',
            width: 60,
            hidden: true,
            menuDisabled: true,
            align: 'left',
            sortable: false
        },
        {
            header: 'Group Name',
            dataIndex: 'name',
            width: 300,
            fixed: true,
            align: 'left',
            menuDisabled: true,
            sortable: true
        },
        {
            header: "Description",
            dataIndex: 'descript',
            width: 100,
            align: 'left',
            menuDisabled: true,
            sortable: false
        },
        {
            header: "Status",
            dataIndex: 'disabled',
            width: 100,
            fixed: true,
            align: 'left',
            renderer: this.renderStatus,
            menuDisabled: true,
            sortable: false
        },
        {
            header: "Action",
            dataIndex: 'tact',
            width: 60,
            fixed: true,
            align: 'center',
            renderer: this.renderAction,
            menuDisabled: true,
            sortable: false
        }
    ]);

    this.ds = new Ext.data.JsonStore({
        baseParams: {dowhat: 'Admin', action: 'getGroupList', disabled: 1},
        url: 'actionServlet',
        root: 'groups',
        fields: [
            {name: 'id'},
            {name: 'name'},
            {name: 'descript'},
            {name: 'user_mask'},
            {name: 'manager_mask'},
            {name: 'disabled'},
            {name: 'on_using'}
        ],

        sortInfo: {field: 'name', direction: "ASC"}
    }),
            this.ds.on({'loadexception': famtree.handle_return_exception, scope: this});
    this.bbar = [{
            iconCls: 'add',
            pressed: true,
            text: 'Add Group',
            handler: this.handle_addgroup,
            disabled: (famtree.global.user.level == famtree.global.macro.ADMIN_LEVEL) ? false : true,
            scope: this
        }, '->', refreshButton
    ];

    Ext.Panel.superclass.constructor.call(this, config);
};

Ext.extend(famtree.group, Ext.grid.GridPanel, {

    handle_delete: function (idx) {
        var grid = Ext.getCmp('famtree_group_grid_id');
        var ds = grid.getStore();
        famtree.deleteGroup(idx, ds);
    },
    handle_edit: function (idx) {
        var grid = Ext.getCmp('famtree_group_grid_id');
        var ds = grid.getStore();
        var record = ds.getById(idx);
        famtree.editGroup('edit', ds, record);
    },
    renderStatus: function (value, p, record, ridx, cindx, dd) {
        if (record.data.disabled == 0)
            return 'Enabled';
        else
            return 'Disabled';
    },
    renderAction: function (value, p, record, ridx, cindx, dd) {
        var html_s = '<table><tr>';
        html_s = html_s + '<td><img border=0 src="images/icons/fam/insert-before.gif"  style="cursor:hand" alt="Edit"';
        html_s = html_s + ' ext:qtip="Edit" onclick="javascript:famtree.group.prototype.handle_edit({0})" >';
        html_s = html_s + '</td>';

        if (record.data.on_using == 0 && famtree.global.user.level == famtree.global.macro.ADMIN_LEVEL) {
            html_s = html_s + '<td><img border=0 src="images/icons/fam/trash.gif" style="cursor:hand" alt="Remove"';
            html_s = html_s + ' ext:qtip="Remove" onclick="javascript:famtree.group.prototype.handle_delete({0})" >';
            html_s = html_s + '</td>';
        } else {
            html_s = html_s + '<td></td>';
        }
        html_s = html_s + '</tr></table>';

        return String.format(html_s, record.data.id, ridx);
    },

    handle_addgroup: function () {
        var grid = Ext.getCmp('famtree_group_grid_id');
        var ds = grid.getStore();
        famtree.editGroup('new', ds);
    }
});

famtree.editGroup = function (which, ds, record) {
    var str_macro_max = 255;
    if (famtree.global.macro.MAX_STRLEN)
        str_macro_max = famtree.global.macro.MAX_STRLEN;

    var saveButton = new Ext.Button({
        id: 'famtree_group_save_button_id',
        text: 'Save',
        minWidth: 80
    });
    saveButton.on('click', mysavehandler, this);

    var groupname = new Ext.form.TextField({
        fieldLabel: 'Group Name',
        name: 'name',
        maxLength: str_macro_max,
        allowBlank: false,
        msgTarget: 'side',
        disabled: (famtree.global.user.level == famtree.global.macro.ADMIN_LEVEL) ? false : true,
        anchor: '95%'
    });
    var descript = new Ext.form.TextField({
        fieldLabel: 'Description',
        name: 'descript',
        maxLength: str_macro_max,
        msgTarget: 'side',
        anchor: '95%'
    });
    var status = new Ext.form.Checkbox({
        width: 150,
        name: 'status',
        disabled: (famtree.global.user.level == famtree.global.macro.ADMIN_LEVEL) ? false : true,
        fieldLabel: 'Disabled'
    });
    var inuserds = new Ext.data.JsonStore({
        fields: [
            {name: 'id', type: 'int'},
            {name: 'name', type: 'string'}
        ]
    });

    var outuserds = new Ext.data.JsonStore({
        fields: [
            {name: 'id', type: 'int'},
            {name: 'name', type: 'string'}
        ]
    });

    var module_ds = famtree.global.dsModules;       
    var mask = new Ext.ux.form.LovCombo({
        anchor: '95%',
        hiddenName: 'user_mask',
        hideLabel: false,
        fieldLabel: 'Permission Mask',
        store: module_ds,
        mode: 'local',
        displayField: 'name',
        valueField: 'id',
        editable: false,
        selectOnFocus: true,
        triggerAction: 'all',
        listeners: {
            'select': changeUserManager
        }
    });

    var statusLabel = new Ext.form.Label({
        html: '<span style="font-size:small;">Disabled:&nbsp;&nbsp;</span>'
    });
    var statusPanel = new Ext.Panel({
        baseCls: 'x-plain',
        labelAlign: 'left',
        layout: 'column',
        items: [statusLabel, status]
    });

    var formPanel = new Ext.form.FormPanel
            ({
                baseCls: 'x-plain',
                labelWidth: 250,
                labelAlign: 'top',
                items: [groupname,
                    descript,
                    mask,
                    statusPanel,
                    //{   xtype:'panel',
                    //	baseCls:'x-plain',
                    //	layout:'column',
                    //	items: [status]
                    //},
                    {xtype: 'itemselector',
                        name: 'users',
                        id: 'group_itemselector_id',
                        fieldLabel: 'Set Users',
                        imagePath: 'ext-3.0.0/examples/ux/images',
                        drawUpIcon: false,
                        drawDownIcon: false,
                        drawLeftIcon: true,
                        drawRightIcon: true,
                        drawTopIcon: false,
                        drawBotIcon: false,

                        multiselects: [{
                                legend: "Users NOT in the group",
                                width: 265,
                                height: 200,
                                store: outuserds,
                                displayField: 'name',
                                valueField: 'id'
                            },
                            {
                                legend: "Users in the group",
                                width: 265,
                                height: 200,
                                store: inuserds,
                                displayField: 'name',
                                valueField: 'id',
                                tbar: [{
                                        text: 'clear',
                                        handler: function () {
                                            formPanel.getForm().findField('users').reset();
                                        }
                                    }]
                            }
                        ]
                    }
                ]
            });

    var window = new Ext.Window
            ({
                title: 'Edit Group',
                width: 580,
                height: 466,
                minWidth: 240,
                minHeight: 360,
                layout: 'form',
                plain: true,
                bodyStyle: 'padding:5px;',
                buttonAlign: 'center',
                constrain: true,
                modal: true,
                items: formPanel,
                buttons: [saveButton,
                    {
                        text: 'Cancel',
                        handler: function () {
                            window.close();
                        }
                    }]

            });
    window.show();
    if (which == 'new') {
        window.setTitle('Add Group');
        status.hide();
        famtree.DataLoader('actionServlet',
                {'dowhat': 'Admin', 'action': 'getUserList', 'shorted': 1},
                this,
                handle_newuserload,
                famtree.handle_server_exception
                );
    } else {
        var t_groupname = record.get('name');
        var t_descript = record.get('descript');
        var group_id = record.get('id');
        var tstatus = record.get('disabled');
        var tmask = record.get('user_mask');
        var tmanager = record.get('manager_mask');
        mask.setValue(tmask);

        groupname.setValue(t_groupname);
        descript.setValue(t_descript);
        if (tstatus == 1)
            status.setValue(true);
        famtree.DataLoader('actionServlet', {'dowhat': 'Admin', 'action': 'getGroup', 'id': group_id}, this,
                handle_edituserload, famtree.handle_server_exception);
    }
    function changeUserManager(combo, record, index)
    {

        var selected_id = record.get('id');
        var test_value = selected_id & 0x1000;
        if (test_value > 0 && record.data.checked == true) {
            var old_v = combo.getCheckedValue();
            if (old_v == '')
                combo.setValue(test_value);
            else {
                var u_id = selected_id - 0x1000;
                combo.setValue(old_v + ',' + u_id);
            }

        }
        if (test_value == 0 && record.data.checked == false) {
            var m_id = selected_id + 0x1000;
            var old_val = combo.getCheckedValue();
            if (old_v != '') {
                if (old_val.indexOf(',' + m_id) >= 0)
                    combo.setValue(old_val.replace(',' + m_id, ''));
                else if (old_val.indexOf(m_id + ',') >= 0)
                    combo.setValue(old_val.replace(m_id + ',', ''));
                else
                    combo.setValue(old_val.replace(m_id, ''));
            }
        }

    }
    function handle_edituserload(scope, json) {

        outuserds.loadData(json.group.outusers);
        inuserds.loadData(json.group.inusers);
    }

    function handle_newuserload(scope, json) {
        outuserds.loadData(json.users);
    }
    function mysavehandler() {
        if (formPanel.form.isValid() == false) {
            Ext.Msg.minWidth = 360;
            Ext.MessageBox.alert('Errors', 'Please fix too long length or required field.');
            return;
        }
        var values = formPanel.getForm().getValues();
        var para = new Object();
        para = values;
        if (which == 'new')
            para.action = 'addGroup';
        else {
            para.action = 'editGroup';
            var chk = status.getValue();
            if (chk == true)
                para.disabled = 1;
            else
                para.disabled = 0;
        }
        para.dowhat = 'Admin';
        if (record)
            para.id = record.get('id');

        var module_values = mask.getCheckedValue();
        var module = 0;
        var manager_module = 0;
        if (module_values != '') {
            var arr_module = module_values.split(',');
            for (var i = 0; i < arr_module.length; i++) {
                var val = parseInt(arr_module[i]);
                if (val > 0x800)
                    manager_module = manager_module + val - 0x1000;
                else
                    module = module + parseInt(arr_module[i]);
            }
        }
        para.mask = module;
        para.permission = manager_module;
        famtree.doServerAction(para, afterSave, this);
        function afterSave(scope, json) {
            window.close();
            ds.load({callback: function () {
                    var grid = Ext.getCmp('famtree_group_grid_id');
                    var id;
                    if (which == 'new')
                        id = json.id;
                    else
                        id = record.get('id');
                    famtree.setSelectedRow(grid, id);
                }
            });

        }
    }

};

famtree.deleteGroup = function (id, ds) {
    var mydelete = function (btn) {
        if ('yes' == btn) {
            var para = new Object();
            para.action = 'removeGroup';
            para.dowhat = 'Admin';
            para.id = id;
            famtree.doServerAction(para, afterRemove, this);

        }
    };
    var helpInfo = 'Are you sure you want to delete the item?';
    Ext.Msg.minWidth = 360;
    Ext.MessageBox.confirm('Message',
            helpInfo,
            mydelete
            );

    function afterRemove(scope, json) {
        ds.load(ds.lastOptions);
    }


};


