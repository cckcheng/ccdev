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

famtree = {
    LANG_CODE: 1,
    SYSTEM_NAME: 'Family Tree Builder',

    CHINESE_SIMPLIFIED: 1,
    CHINESE_TRADITIONAL: 2,
    ENGLISH: 5,
    
    PHRASE: []
};
var language = navigator.languages && navigator.languages[0] || // Chrome / Firefox
                    navigator.language ||   // All browsers
                    navigator.userLanguage; // IE <= 10
language = 'zh'; //for test chinese, remove this line when official

if(language.startsWith('en-')) {
    famtree.LANG_CODE = famtree.ENGLISH;
} else if(language === 'zh-tw' || language === 'zh-hk') {
    famtree.LANG_CODE = famtree.CHINESE_TRADITIONAL;
} else if(language.startsWith('zh')) {  // zh-cn, zh-sg, zh 
    famtree.LANG_CODE = famtree.CHINESE_SIMPLIFIED;
} else {
    famtree.LANG_CODE = famtree.ENGLISH;
}
                
famtree.PHRASE[famtree.CHINESE_SIMPLIFIED] = {
// the following is from web search
'Access pattern': '访问模式',
'Activation': '激活',
'Active border': '活动边框',
'Action': '操作',
'Advanced': '高级',
'Address': '地址',
'Add': '添加',
'Adjust Free Space': '调整可用空间',
'Always show': '始终显示',
'Associated': '相关',
'Apply': '申请',
'Application Software': '应用软件',
'Alignment': '对齐方式',
'Attribute': '属性',
'Audio': '音频',
'Automatically fix errors': '自动修复错误',
'Auto Arrange': '自动排列',
'Back': '返回',
'Back up': '备份',
'Bad Sectors': '坏扇区',
'Bar Chart': '条形图',
'BBS (Bulletin Board System)': '电子公告板',
'Binder': '活页夹',
'Bindings': '绑定',
'Bit': '位',
'Browser': '浏览器',
'Brief case': '公文包',
'Bus Network': '总线网',
'Button': '按钮',
'Byte': '字节',
'Capacity': '容量',
'Caption': '标题',
'Cancel': '取消',
'Category': '类别',
'Calculator': '计算器',
'CD(Compact Disc)': '光盘',
'CD-ROM': '只读光盘',
'Chip': '芯片',
'Change': '更改',
'Clear': '清空',
'Click': '点击',
'Clock': '时钟',
'Close': '关闭',
'Communication': '通信',
'Command line': '命令行',
'Compatible': '兼容',
'Compare': '比较',
'Components': '组件',
'Compress': '压缩',
'Configuration': '配置',
'Confirm': '确认',
'Contents': '目录',
'Control panel': '控制面板',
'Convert to Files': '转换到文件',
'Copy': '复制',
'Custom': '自定义',
'Cut': '剪切',
'CPU(Central Processing Unit)': '中央处理器',
'CRT(Cathode-Ray Tube)': '阴极射线管',
'Cross-Linked files': '交叉链接文件',
'Create Shortcut': '创建快捷方式',
'3D': '三维',
'Data': '数据',
'Default': '默认值',
'Delete': '删除',
'Desktop': '桌面',
'Details': '详细资料',
'Device Manager': '设备管理',
'Description': '说明',
'DDE (Development Data Exchange)': '动态数据交换',
'Dialog box': '对话框',
'Dial Up Network': '拨号网络',
'DIR': '目录',
'Display': '显示',
'Disk Defragmenter': '磁盘碎片整理程序',
'DMA ( Direct Memory Access )': '直接内存访问',
'Document': '文档',
'DOS(Disk Operating System)': '磁盘操作系统',
'Double Click': '双击',
'Download': '下载',
'Drag': '拖动',
'Drive': '驱动器',
'Drive Space': '磁盘空间管理',
'DVD(Digital Video Disc)': '数字视盘',
'Edit': '编辑',
'E-mail(Electronic mail)': '电子邮件',
'Empty Recycle Bin': '清空回收站',
'Energy Star': '能源之星',
'Errors': '错误',
'Exclude': '排除',
'Exit': '退出',
'Extension filename': '扩展名',
'Explorer': '资源管理器',
'File': '文件',
'File type': '文件类型',
'Filename': '文件名',
'Find': '查找',
'Find What': '查找目标',
'Finish': '完成',
'File manager': '文件管理器',
'Filtering': '筛选',
'Flip': '翻转',
'Floppy disk': '软磁盘',
'Folder': '文件夹',
'Font': '字体',
'Footer': '页脚',
'Format': '格式化',
'Format Result': '格式化结果',
'Free': '释放',
'Free form select': '裁剪',
'FTP (File Transfer Protocol)': '文件传输协议',
'Games': '游戏',
'Hardware': '硬件',
'HDD(Hard Disk Drive)': '硬盘',
'Help': '帮助',
'Header': '页眉',
'Hidden': '隐含',
'Home page': '主页',
'Hyperlink': '超链接',
'Hypertext': '超文本',
'Hyper terminal': '超级终端',
'Icon': '图标',
'Ignore': '忽略',
'Inbox': '收件箱',
'Include': '包含',
'Indicator light': '指示灯',
'Index': '索引',
'Information': '信息',
'Insert': '插入',
'Instruction': '指令',
'Install': '安装',
'Interaction': '交互',
'Internet': '因特网',
'Invert': '反向',
'IRQ( Interrupt Request )': '申请中断',
'ISP (Internet Service Provider)': '因特网服务提供商',
'IT (Information Technology)': '信息技术',
'Item': '项目',
'Kb ( Kilo Byte)': '千字节',
'Keyboard': '键盘',
'Label': '卷标',
'Landscape': '横向',
'LCD (Liquid Crystal Display)': '液晶显示器',
'Link': '链接',
'Line Up Icons': '行列对齐',
'List': '列表',
'Local Bus': '局部总线',
'Location': '位置',
'Log on': '登录',
'Macro': '宏',
'Mail server': '邮件服务器',
'Manuel': '手工',
'Margins': '页边距',
'Master clock-rate': '主频',
'Maximize': '最大化',
'Media Player': '媒体播放器',
'MB (Megabyte)': '兆字节',
'Memory': '存储器',
'Menu': '菜单',
'Microsoft': '微软',
'MIDI (Music Instrument Digital Interface)': '音乐设备数字化接口',
'Minimize': '最小化',
'Model': '型号',
'Modem': '调制解调器',
'Modified': '修改',
'Monitor': '显示器',
'Mouse': '鼠标',
'Mount': '装配',
'Multimedia': '多媒体',
'Mute': '静音',
'Netnews': '网络新闻',
'Needing Update': '需要更新',
'Network': '网络',
'Network Adapters': '网络适配器',
'Network Neighborhood': '网上邻居',
'Never': '不显示',
'Next': '下一步',
'Not Enough Memory': '内存不足',
'Object': '对象',
'OCR (Optical Character Recognition)': '光学字符识别',
'Open with': '打开方式',
'Operating System': '操作系统',
'Optimize': '优化',
'Options': '选项',
'Other Option': '其他选项',
'Overwrite': '覆盖',
'Page setup': '页面设置',
'Play back': '重现',
'Paste': '粘贴',
'Password': '密码',
'Path': '路径',
'PC(Personal Computer)': '个人电脑',
'Peer to Peer': '对等型',
'Pentium': '奔腾',
'Performance': '性能',
'Phone Dialer': '电话拨号程序',
'Pixel': '像素',
'Plotter': '绘图仪',
'Pointer': '指针',
'Portable': '简便',
'Portrait': '纵向',
'Power': '电源',
'Preview': '预览',
'Print': '打印',
'Printer': '打印机',
'Processor': '处理器',
'Program': '程序',
'Programs': '程序组',
'Program Manager': '程序管理器',
'Pro file': '配置文件',
'Quick': '快速',
'RAM(Random Access Memory)': '随机存取存储器',
'Read only': '只读',
'Record': '记录',
'Reconnect': '连接',
'Recommend': '推荐',
'Recycle Bin': '回收站',
'Refresh': '刷新',
'Regional settings': '地区设定',
'Reinstall': '重新安装',
'Replace': '替换',
'Remove': '删除',
'Rename': '改变（文件）名称',
'Restart': '重新启动',
'Reset': '复位',
'Resource': '资源',
'Restore': '还原',
'ROM(Read only Memory)': '只读存储器',
'Run': '运行',
'Ruler': '标尺',
'Save': '保存',
'Save as': '另存为',
'Scanner': '扫描仪',
'Scheme': '方案',
'Scan Disk': '磁盘扫描',
'Screen': '屏幕',
'Screen Saver': '屏幕保护程序',
'Scrap': '碎片',
'Search': '搜索',
'Select': '选定',
'Setting': '设定',
'Set Up': '设置',
'Send': '发送',
'Share': '共享',
'Shortcut': '快捷方式',
'Shut Down': '关闭系统',
'Skip': '跳过',
'Start': '开始',
'Standard': '标准',
'SMTP(Simple Mail Transfer Protocol)': '简单邮件传送协议',
'Software': '软件',
'Speech Recognition': '语音识别',
'Speed': '速度',
'Status Bar': '状态栏',
'Subfolder': '子文件夹',
'Submenu': '子菜单',
'System Software': '系统软件',
'System Area': '系统区',
'Sync copy In': '同步复制',
'Tabs': '制表符',
'Task Bar': '任务栏',
'Telnet': '远程登录',
'Text': '文字',
'Title Bar': '标题栏',
'Tool Bar': '工具栏',
'Topic': '主题',
'True Type': '标准字体',
'Troubleshooter': '疑难解答',
'Typical': '典型的',
'Uncompress': '解压缩',
'Undo': '恢复',
'Uninstall': '卸载',
'Update': '更新',
'Upgrade': '升级',
'Up one level': '向上一级',
'Using': '使用',
'Video': '视频',
'View': '查看',
'Virus': '病毒',
'Wall paper': '墙纸',
'Website': '网站',
'Wide band': '宽带',
'Window': '窗口',
'Wizard': '向导',
'WWW(World Wide Web)': '环球网',
'Word Pad': '写字板',
'Zoom': '缩放',

    // the following is for this project
    'Family Tree Builder': '炎黄家谱',
    'Create': '创建',
    'Pedigree': '家谱',
    'Welcome': '你好，',
    'Family': '家庭',
    'Family Tree': '家谱世系',
    'Family Name': '姓氏',
    'My': '我的',
    'Ok': '确定',
    'Old': '旧',
    'New': '新',
    'Login': '登录',
    'Logout': '退出',
    'Username': '用户名',
    'Email Address': '电邮地址',
    'Email': '电邮',
    'Name': '名称',
    'Time': '时间',
    'Success': '成功',
    'Failed': '失败',
    'Failure': '失败',
    'Warning': '警告',
    'Error': '错误',
    'Pedigree Name': '家谱'
};

famtree.PHRASE[famtree.CHINESE_TRADITIONAL] = {
'Access pattern': '訪問模式',
'Activation': '激活',
'Active border': '活動邊框',
'Action': '操作',
'Advanced': '高級',
'Address': '地址',
'Add': '添加',
'Adjust Free Space': '調整可用空間',
'Always show': '始終顯示',
'Associated': '相關',
'Apply': '申請',
'Application Software': '應用軟件',
'Alignment': '對齊方式',
'Attribute': '屬性',
'Audio': '音頻',
'Automatically fix errors': '自動修復錯誤',
'Auto Arrange': '自動排列',
'Back': '返回',
'Back up': '備份',
'Bad Sectors': '壞扇區',
'Bar Chart': '條形圖',
'BBS (Bulletin Board System)': '電子公告板',
'Binder': '活頁夾',
'Bindings': '綁定',
'Bit': '位',
'Browser': '瀏覽器',
'Brief case': '公文包',
'Bus Network': '總線網',
'Button': '按鈕',
'Byte': '字節',
'Capacity': '容量',
'Caption': '標題',
'Cancel': '取消',
'Category': '類別',
'Calculator': '計算器',
'CD(Compact Disc)': '光盤',
'CD-ROM': '隻讀光盤',
'Chip': '芯片',
'Change': '更改',
'Clear': '清空',
'Click': '點擊',
'Clock': '時鐘',
'Close': '關閉',
'Communication': '通信',
'Command line': '命令行',
'Compatible': '兼容',
'Compare': '比較',
'Components': '組件',
'Compress': '壓縮',
'Configuration': '配置',
'Confirm': '確認',
'Contents': '目錄',
'Control panel': '控制面板',
'Convert to Files': '轉換到文件',
'Copy': '復制',
'Custom': '自定義',
'Cut': '剪切',
'CPU(Central Processing Unit)': '中央處理器',
'CRT(Cathode-Ray Tube)': '陰極射線管',
'Cross-Linked files': '交叉鏈接文件',
'Create Shortcut': '創建快捷方式',
'3D': '三維',
'Data': '數據',
'Default': '默認值',
'Delete': '刪除',
'Desktop': '桌面',
'Details': '詳細資料',
'Device Manager': '設備管理',
'Description': '說明',
'DDE (Development Data Exchange)': '動態數據交換',
'Dialog box': '對話框',
'Dial Up Network': '撥號網絡',
'DIR': '目錄',
'Display': '顯示',
'Disk Defragmenter': '磁盤碎片整理程序',
'DMA ( Direct Memory Access )': '直接內存訪問',
'Document': '文檔',
'DOS(Disk Operating System)': '磁盤操作系統',
'Double Click': '雙擊',
'Download': '下載',
'Drag': '拖動',
'Drive': '驅動器',
'Drive Space': '磁盤空間管理',
'DVD(Digital Video Disc)': '數字視盤',
'Edit': '編輯',
'E-mail(Electronic mail)': '電子郵件',
'Empty Recycle Bin': '清空回收站',
'Energy Star': '能源之星',
'Errors': '錯誤',
'Exclude': '排除',
'Exit': '退出',
'Extension filename': '擴展名',
'Explorer': '資源管理器',
'File': '文件',
'File type': '文件類型',
'Filename': '文件名',
'Find': '查找',
'Find What': '查找目標',
'Finish': '完成',
'File manager': '文件管理器',
'Filtering': '篩選',
'Flip': '翻轉',
'Floppy disk': '軟磁盤',
'Folder': '文件夾',
'Font': '字體',
'Footer': '頁腳',
'Format': '格式化',
'Format Result': '格式化結果',
'Free': '釋放',
'Free form select': '裁剪',
'FTP (File Transfer Protocol)': '文件傳輸協議',
'Games': '游戲',
'Hardware': '硬件',
'HDD(Hard Disk Drive)': '硬盤',
'Help': '幫助',
'Header': '頁眉',
'Hidden': '隱含',
'Home page': '主頁',
'Hyperlink': '超鏈接',
'Hypertext': '超文本',
'Hyper terminal': '超級終端',
'Icon': '圖標',
'Ignore': '忽略',
'Inbox': '收件箱',
'Include': '包含',
'Indicator light': '指示燈',
'Index': '索引',
'Information': '信息',
'Insert': '插入',
'Instruction': '指令',
'Install': '安裝',
'Interaction': '交互',
'Internet': '因特網',
'Invert': '反向',
'IRQ( Interrupt Request )': '申請中斷',
'ISP (Internet Service Provider)': '因特網服務提供商',
'IT (Information Technology)': '信息技術',
'Item': '項目',
'Kb ( Kilo Byte)': '千字節',
'Keyboard': '鍵盤',
'Label': '卷標',
'Landscape': '橫向',
'LCD (Liquid Crystal Display)': '液晶顯示器',
'Link': '鏈接',
'Line Up Icons': '行列對齊',
'List': '列表',
'Local Bus': '局部總線',
'Location': '位置',
'Log on': '登錄',
'Macro': '宏',
'Mail server': '郵件服務器',
'Manuel': '手工',
'Margins': '頁邊距',
'Master clock-rate': '主頻',
'Maximize': '最大化',
'Media Player': '媒體播放器',
'MB (Megabyte)': '兆字節',
'Memory': '存儲器',
'Menu': '菜單',
'Microsoft': '微軟',
'MIDI (Music Instrument Digital Interface)': '音樂設備數字化接口',
'Minimize': '最小化',
'Model': '型號',
'Modem': '調制解調器',
'Modified': '修改',
'Monitor': '顯示器',
'Mouse': '鼠標',
'Mount': '裝配',
'Multimedia': '多媒體',
'Mute': '靜音',
'Netnews': '網絡新聞',
'Needing Update': '需要更新',
'Network': '網絡',
'Network Adapters': '網絡適配器',
'Network Neighborhood': '網上鄰居',
'Never': '不顯示',
'Next': '下一步',
'Not Enough Memory': '內存不足',
'Object': '對象',
'OCR (Optical Character Recognition)': '光學字符識別',
'Open with': '打開方式',
'Operating System': '操作系統',
'Optimize': '優化',
'Options': '選項',
'Other Option': '其他選項',
'Overwrite': '覆蓋',
'Page setup': '頁面設置',
'Play back': '重現',
'Paste': '粘貼',
'Password': '密碼',
'Path': '路徑',
'PC(Personal Computer)': '個人電腦',
'Peer to Peer': '對等型',
'Pentium': '奔騰',
'Performance': '性能',
'Phone Dialer': '電話撥號程序',
'Pixel': '像素',
'Plotter': '繪圖儀',
'Pointer': '指針',
'Portable': '簡便',
'Portrait': '縱向',
'Power': '電源',
'Preview': '預覽',
'Print': '打印',
'Printer': '打印機',
'Processor': '處理器',
'Program': '程序',
'Programs': '程序組',
'Program Manager': '程序管理器',
'Pro file': '配置文件',
'Quick': '快速',
'RAM(Random Access Memory)': '隨機存取存儲器',
'Read only': '隻讀',
'Record': '記錄',
'Reconnect': '連接',
'Recommend': '推薦',
'Recycle Bin': '回收站',
'Refresh': '刷新',
'Regional settings': '地區設定',
'Reinstall': '重新安裝',
'Replace': '替換',
'Remove': '刪除',
'Rename': '改變（文件）名稱',
'Restart': '重新啟動',
'Reset': '復位',
'Resource': '資源',
'Restore': '還原',
'ROM(Read only Memory)': '隻讀存儲器',
'Run': '運行',
'Ruler': '標尺',
'Save': '保存',
'Save as': '另存為',
'Scanner': '掃描儀',
'Scheme': '方案',
'Scan Disk': '磁盤掃描',
'Screen': '屏幕',
'Screen Saver': '屏幕保護程序',
'Scrap': '碎片',
'Search': '搜索',
'Select': '選定',
'Setting': '設定',
'Set Up': '設置',
'Send': '發送',
'Share': '共享',
'Shortcut': '快捷方式',
'Shut Down': '關閉系統',
'Skip': '跳過',
'Start': '開始',
'Standard': '標准',
'SMTP(Simple Mail Transfer Protocol)': '簡單郵件傳送協議',
'Software': '軟件',
'Speech Recognition': '語音識別',
'Speed': '速度',
'Status Bar': '狀態欄',
'Subfolder': '子文件夾',
'Submenu': '子菜單',
'System Software': '系統軟件',
'System Area': '系統區',
'Sync copy In': '同步復制',
'Tabs': '制表符',
'Task Bar': '任務欄',
'Telnet': '遠程登錄',
'Text': '文字',
'Title Bar': '標題欄',
'Tool Bar': '工具欄',
'Topic': '主題',
'True Type': '標准字體',
'Troubleshooter': '疑難解答',
'Typical': '典型的',
'Uncompress': '解壓縮',
'Undo': '恢復',
'Uninstall': '卸載',
'Update': '更新',
'Upgrade': '升級',
'Up one level': '向上一級',
'Using': '使用',
'Video': '視頻',
'View': '查看',
'Virus': '病毒',
'Wall paper': '牆紙',
'Website': '網站',
'Wide band': '寬帶',
'Window': '窗口',
'Wizard': '向導',
'WWW(World Wide Web)': '環球網',
'Word Pad': '寫字板',
'Zoom': '縮放',

    'Family Tree Builder': '炎黃家譜',
    'Create': '創建',
    'Pedigree': '家譜',
    'Welcome': '你好，',
    'Family': '家庭',
    'Family Tree': '家譜世系',
    'Family Name': '姓氏',
    'My': '我的',
    'Ok': '確定',
    'Old': '舊',
    'New': '新',
    'Login': '登錄',
    'Logout': '退出',
    'Username': '用戶名',
    'Email Address': '電郵地址',
    'Email': '電郵',
    'Name': '名稱',
    'Time': '時間',
    'Success': '成功',
    'Failed': '失敗',
    'Failure': '失敗',
    'Warning': '警告',
    'Error': '錯誤',
    'Pedigree Name': '家譜'
};

Ext.applyIf(famtree.PHRASE[famtree.CHINESE_SIMPLIFIED], {
    'you': '你',
    'your': '你的',
    'and': '与',
    'does': ' ',
    'not': '不',
    'right': '对',
    'match': '匹配'
});

function addLowerKey(obj) {
    for(var k in obj) {
        var lk = k.toLowerCase();
        if(!obj[lk]) obj[lk] = obj[k];
    }
}

addLowerKey(famtree.PHRASE[famtree.CHINESE_SIMPLIFIED]);
addLowerKey(famtree.PHRASE[famtree.CHINESE_TRADITIONAL]);

famtree.getPhrase = function(phrase) {
    if(Ext.isEmpty(phrase)) return '';
    if(famtree.LANG_CODE === famtree.ENGLISH) return phrase;
    var localPhrase = famtree.PHRASE[famtree.LANG_CODE];
    if(!localPhrase) return phrase;

    var val = (localPhrase[phrase] || localPhrase[phrase.toLowerCase()]);
    if(val) return val;

    var ss = phrase.split(' ');
    if(ss.length <= 1) return phrase;
    var newPhrase = '';
    for(var x=0,n=ss.length; x<n; x++) {
        var lowcase = ss[x].toLowerCase();
        var lastChar = lowcase.slice(-1);
        if(lastChar.match(/[^\w\s]/)) {
            lowcase = lowcase.slice(0, lowcase.length-1);
        } else {
            lastChar = '';
        }
        
        switch(famtree.LANG_CODE) {
            case famtree.CHINESE_SIMPLIFIED:
            case famtree.CHINESE_TRADITIONAL:
                break;
            default:
                if(newPhrase !== '') newPhrase += ' ';
                break;
        }
        newPhrase += (localPhrase[lowcase] || lowcase);
        newPhrase += lastChar;
    }
    return newPhrase;
};

famtree.userList = function(autoLoad) {
	return famtree.generalStore('actionServlet', {
			dowhat: 'admin',
			action: 'getUsers'
		}, 'results', autoLoad);
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

