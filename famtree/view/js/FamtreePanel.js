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
                var htm = '<img border=0 src="images/icons/treeicon.gif" style="cursor:hand" alt="generate PDF"';
                htm += ' ext:qtip="生成PDF" onclick="javascript:famtree.FamtreePanel.prototype.printPedigree({0})" >';
                return String.format(htm, v);
            }
        }],
        tbar: [{
            text: famtree.getPhrase('Create Pedigree'),
            iconCls: 'add',
            handler: function() {
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
            Ext.Msg.alert(famtree.getPhrase('Success'), famtree.getPhrase('The printout will be email to you.'));
        }, this, true);
    };

    var config = {
        id: 'famtree-builder-panel',
        enableTabScroll: true,
        activeTab: 0,
        items: [myPedigree]
    };
    
    Ext.Panel.superclass.constructor.call(this, config);
    dsPedigree.load({
        params: {
            limit: this.pageSize
        }
    });
};

Ext.extend(famtree.FamtreePanel, Ext.TabPanel, {
    pageSize: 50,
    
    viewFamilyTree: function(rec) {
        var tab_id = 'famtree-pedigree-' + rec.get('id');
        var tab = this.findById(tab_id);
        if(!tab) {
            this.add({
                id: tab_id,
                title: rec.get('pedigree_name')
            });
        }
        this.setActiveTab(tab_id);
    }
});
