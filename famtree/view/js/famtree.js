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

famtree.SYSTEM_NAME = 'Family Tree Builder';

famtree.userList = function(autoLoad) {
	return famtree.generalStore('actionServlet', {
			dowhat: 'admin',
			action: 'getUsers'
		}, 'results', autoLoad)
};

Ext.override(Ext.grid.GridView, {
	getEditorParent: function(ed) {
		//return this.mainWrap.dom;
		return Ext.getBody();
	}
});

Ext.override(Ext.ux.form.LovCombo, {
    beforeBlur: Ext.emptyFn
});

// the following override fixed a IE bug in ItemSelector
Ext.override(Ext.ux.form.ItemSelector, {
    destroy: function () {
        if (this.fromMultiselect) {
            this.fromMultiselect.destroy();
        }
        if (this.toMultiselect) {
            this.toMultiselect.destroy();
        }
    }

});
Ext.override(Ext.ux.form.MultiSelect, {
    destroy: function () {

        Ext.destroy(this.fs);
        if (this.dragZone) {
            Ext.dd.ScrollManager.unregister(this.dragZone.el);
            if (this.dragZone.destroy) {
                this.dragZone.destroy();
            }
        }
        if (this.dropZone) {
            Ext.dd.ScrollManager.unregister(this.dropZone.el);
            if (this.dropZone.destroy) {
                this.dropZone.destroy();
            }
        }

        Ext.ux.form.MultiSelect.superclass.destroy.call(this);
    }

});
// the following override fixed a bug in ItemSelector
// this bug will happen in both FF and IE when you drag one item to select window then immediately try to drag it back to available win
Ext.override(Ext.ux.form.MultiSelect.DropZone, {
    onNodeDrop: function (n, dd, e, data) {
        if (this.ms.fireEvent("drop", this, n, dd, e, data) === false) {
            return false;
        }
        var pt = this.getDropPoint(e, n, dd);
        if (n != this.ms.fs.body.dom)
            n = this.view.findItemFromChild(n);
        var insertAt = (this.ms.appendOnly || (n == this.ms.fs.body.dom)) ? this.view.store.getCount() - 1 : this.view.indexOf(n);
        if (pt == "below") {
            insertAt++;
        }

        var dir = false;

        // Validate if dragging within the same MultiSelect
        if (data.sourceView == this.view) {
            // If the first element to be inserted below is the target node, remove it
            if (pt == "below") {
                if (data.viewNodes[0] == n) {
                    data.viewNodes.shift();
                }
            } else {  // If the last element to be inserted above is the target node, remove it
                if (data.viewNodes[data.viewNodes.length - 1] == n) {
                    data.viewNodes.pop();
                }
            }

            // Nothing to drop...
            if (!data.viewNodes.length) {
                return false;
            }

            // If we are moving DOWN, then because a store.remove() takes place first,
            // the insertAt must be decremented.
            if (insertAt > this.view.store.indexOf(data.records[0])) {
                dir = 'down';
                insertAt--;
            }
        }

        for (var i = 0; i < data.records.length; i++) {
            var r = data.records[i];
            if (data.sourceView) {
                data.sourceView.store.remove(r);
            }
            this.view.store.insert(dir == 'down' ? insertAt : insertAt++, r);
            var si = this.view.store.sortInfo;
            if (si) {
                this.view.store.sort(si.field, si.direction);
            }
        }
        return true;
    }


});

