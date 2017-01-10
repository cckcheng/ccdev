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

famtree.FamtreePanel = function() {
    var dsPedigree = new Ext.data.JsonStore({
        url: 'actionServlet',
        autoLoad: false,
        baseParams: {
            dowhat: 'OptPedigree',
            action: 'getPedigreeList'
        },
        fields: ['id', 'pedigree_name', 'family_name', 'created', 'modified', 'totalIndividual', 'totalGeneration'],
        root: 'results',
        totalProperty: 'total'
    });
    
    var myPedigree = new Ext.grid.GridPanel({
        title: famtree.getPhrase('My Pedigree List'),
        store: dsPedigree,
        loadMask: true,
        columns: [{
            header: famtree.getPhrase('Pedigree Name'),
            width: 300,
            dataIndex: 'pedigree_name',
            scope: this,
            renderer: function(v, p, rec) {
                var htm = '<A href="#" onclick="javascript:famtree.FamtreePanel.prototype.showFamilyTree(\'{0}\',\'{1}\')">' + v + '</A>';
                return String.format(htm, rec.id, this.id);
            }
        }, {
            header: famtree.getPhrase('Family Name'),
            dataIndex: 'family_name'
        }, {
            header: famtree.getPhrase('Create Time'),
            dataIndex: 'created'
        }, {
            header: famtree.getPhrase('Action'),
            align: 'center',
            width: 150,
            dataIndex: 'id',
            renderer: function(v, p, rec) {
                var htm = '<img border=0 src="images/icons/treeicon.gif" style="cursor:hand" alt="download PDF"';
                htm += ' ext:qtip="Download PDF" onclick="javascript:famtree.FamtreePanel.prototype.printPedigree({0})" >';
                return String.format(htm, v);
            }
        }],
        tbar: [{
            text: famtree.getPhrase('Create Pedigree'),
            iconCls: 'add',
            handler: function() {
                famtree.doServerAction({
                    dowhat: 'OptPedigree',
                    action: 'manageUsers'
                }, function() {
                    Ext.Msg.alert('result', 'Success');
                });
            }
        }],
        bbar: new Ext.PagingToolbar({
            displayInfo: true,
            pageSize: this.pageSize,
            store: dsPedigree
        })
    });
    
    famtree.FamtreePanel.prototype.showFamilyTree = function(recId, panelId) {
        var p = Ext.getCmp(panelId);
        var rec = dsPedigree.getById(recId);
        p.viewFamilyTree(rec);
    };

    famtree.FamtreePanel.prototype.printPedigree = function(pedigreeId) {
        famtree.doServerAction({
            dowhat: 'OptPedigree',
            action: 'printOut',
            id: pedigreeId
        }, function() {
            Ext.Msg.alert(famtree.getPhrase('Success'), famtree.getPhrase('Processing...please try later.'));
        }, this, true);
    };

    var config = {
        id: 'famtree-builder-panel',
        enableTabScroll: true,
        activeTab: 0,
        items: [myPedigree]
    };
    
    Ext.Panel.superclass.constructor.call(this, config);
    this.on('render', function(p){
        dsPedigree.load({params: {limit: this.pageSize}});
    }, this);
};

Ext.extend(famtree.FamtreePanel, Ext.TabPanel, {
    pageSize: 50,
    
    viewFamilyTree: function(rec) {
        var tab_id = 'famtree-pedigree-' + rec.get('id');
        var tab = this.findById(tab_id);
        if(!tab) {
            this.add(new famtree.FamtreeView(this, tab_id, rec));
        }
        this.setActiveTab(tab_id);
    }
});

famtree.FamtreeView = function(owner, id, pedRec) {
    var dsIndividual = new Ext.data.JsonStore({
        url: 'actionServlet',
        remoteSort: true,
        sortInfo: {
            field: 'gen',
            dir: 'ASC'
        },
        fields: [
            'id', 'given_name', 'gen', 'info'
        ],
        totalProperty: 'total',
        root: 'results',
        baseParams: {
            dowhat: 'OptPedigree',
            action: 'getIndividualList',
            id: pedRec.get('id')
        }
    });

    function search() {
        var form = filter.form;
        if(!form) return;
        if(!form.isValid()){
                Ext.Msg.alert('Error', 'Please amend the invalid field(s).');
                return;
        }
        dsIndividual.baseParams = Ext.applyIf({
            dowhat: 'OptPedigree',
            action: 'getIndividualList',
            id: pedRec.get('id')
        }, form.getValues());
        dsIndividual.load({params: {limit: owner.pageSize}});
    }
    
    var filter = new Ext.FormPanel({
        region: 'north',
        title: famtree.getPhrase('Filter'),
        autoHeight: true,
        frame: true,
        layout: 'column',
        border: false,
        bodyBorder: false,
        hideBorders: true,

        keys: {
                key: [10,13],
                fn: search
        },

        defaults: {
                layout: 'form',
                border: false,
                labelAlign: 'right',
                labelWidth: 80
        },
        items: [{
                items: {
                        xtype: 'textfield',
                        name: 'given_name',
                        fieldLabel: famtree.getPhrase('Given Name')
                }
        },{
                width: 160,
                items: {
                        xtype: 'numberfield',
                        width: 60,
                        name: 'start_gen',
                        fieldLabel: famtree.getPhrase('Start Generation')
                }
        },{
                width: 160,
                items: {
                        xtype: 'numberfield',
                        width: 60,
                        name: 'end_gen',
                        fieldLabel: famtree.getPhrase('End Generation')
                }
        },{
                width: 300,
                items: {
                        xtype: 'textfield',
                        name: 'info',
                        width: 200,
                        fieldLabel: famtree.getPhrase('Introduction')
                }
        }],
        buttonAlign: 'left',
        buttons: [{
            text: famtree.getPhrase('Search'),
            handler: search
        }, {
            text: famtree.getPhrase('Reset'),
            handler: function() {
                filter.form.reset();
                search();
            }
        }]
    });
    
    var grid = new Ext.grid.GridPanel({
        region: 'center',
        store: dsIndividual,
        loadMask: true,
        autoExpandColumn: 'famtree-ind-introduction',
//        viewConfig: {forceFit: true},
        columns: [{
            header: famtree.getPhrase('Given Name'),
            sortable: true,
            dataIndex: 'given_name'
        }, {
            header: famtree.getPhrase('Generation'),
            sortable: true,
//            fixed: true,
            dataIndex: 'gen'
        }, {
            id: 'famtree-ind-introduction',
            width: 500,
            header: famtree.getPhrase('Introduction'),
            renderer: function(v, p, rec) {
                if(!v) return v;
                return '<div ext:qtip="' + famtree.htmlEncode(v) + '">' + v + '</div>';
            },
            dataIndex: 'info'
        }],
        bbar: new Ext.PagingToolbar({
            displayInfo: true,
            pageSize: owner.pageSize,
            store: dsIndividual
        })
    });
    var config = {
        id: id,
        title: pedRec.get('pedigree_name'),
        tbar: [{
            text: famtree.getPhrase('Express Import'),
            iconCls: 'settings',
            tooltip: famtree.getSentence('tooltip-express-import'),
            scope: this,
            handler: function() {
                famtree.batchImport(this, pedRec);
            }
        },{
            text: famtree.getPhrase('Export'),
            iconCls: 'export',
            tooltip: famtree.getSentence('tooltip-export-individual'),
            scope: this,
            handler: function() {
                var url = Ext.urlEncode({pedId: pedRec.get('id')}, 'uploadServlet?filetype=individual');
                window.open(url, "download_win");
            }
        },{
            text: famtree.getPhrase('Batch Update'),
            iconCls: 'edit',
            tooltip: famtree.getSentence('tooltip-batch-update'),
            scope: this,
            handler: function() {
                
            }
        }],
        layout: 'border',
        items: [filter, grid]
    };
    
    Ext.Panel.superclass.constructor.call(this, config);
    this.on('render', function(p){
            dsIndividual.load({params: {limit: owner.pageSize}});
    }, this);
};

Ext.extend(famtree.FamtreeView, Ext.Panel, {
    
});

famtree.batchImport = function(owner, pedRec) {
    var height = 480;
    var fm = new Ext.FormPanel({
        frame: true,
        forceLayout: true,
        autoHeight: true,
        labelAlign: 'top',
        items: [
            {
                xtype: 'textarea',
                name: 'input',
                anchor: '100%',
                height: height,
                allowBlank: false,
                hideLabel: true
            }
        ],
        
        monitorValid: true,
        buttons: [
            {
                text: famtree.getPhrase('Submit'),
                formBind: true,
                handler: function() {
                    famtree.submitForm('actionServlet', fm.form, {
                        dowhat: 'OptPedigree',
                        action: 'batchImport',
                        id: pedRec.get('id')
                    }, function() {
                        win.close();
                        Ext.Msg.alert(famtree.getPhrase('Message'), famtree.getPhrase('Success'));
                    });
                }
            }, {
                text: famtree.getPhrase('Cancel'),
                handler: function() {
                    win.close();
                }
            }, {
                text: famtree.getPhrase('Sample'),
                iconCls: 'help',
                handler: function() {
                    famtree.ExpressImportSample();
                }
            }
        ]
    });

    var win = famtree.CustomWindow({
        title: famtree.getPhrase('Express Import'),
        width: 370,
        items: fm
    });
    win.show();
};

famtree.ExpressImportSample = function() {
    var sample1 = {};
    var sample2 = {};
    sample1[famtree.ENGLISH] = '-1-<br>start ancester<br>-2-<br>';
    sample1[famtree.CHINESE_SIMPLIFIED] = '-1-<br>始祖<br>-2-<br>长子，次子<br>-3-<br>长孙，次孙<br>-<br>-4-<br>四世祖<br>-'
        + '<br><hr>说明：<br>同一代的总行数须与上一代的总人数相等，减号表示某支无子<br>-1-, -2-, -3- ... 为世数，必须连续';
    sample2[famtree.ENGLISH] = '-4-<br>fouth generation<br>-5-<br>';
    sample2[famtree.CHINESE_SIMPLIFIED] = '-4-<br>四世祖<br>-5-<br>长子，次子，三子<br>-6-<br>...'
        + '<br><hr>说明：<br>可从谱中现有成员接续，该成员在现有谱中应无子存在';
    var p1 = new Ext.Panel({
        title: famtree.getPhrase('Sample 1'),
        html: '<p>' + sample1[famtree.LANG_CODE] + '</p>'
    });
    var p2 = new Ext.Panel({
        title: famtree.getPhrase('Sample 2'),
        html: '<p>' + sample2[famtree.LANG_CODE] + '</p>'
    });
    var p = new Ext.TabPanel({
        activeTab: 0,
        height: 400,
        items: [p1, p2]
    });

    var win = famtree.CustomWindow({
        title: famtree.getPhrase('Sample'),
        width: 370,
        items: p
    });
    win.show();
};