<!-- jquery -->
<script type="text/javascript" src="${contextPath}/resources/easyui/js/jquery-3.2.1.min.js"></script>
<!-- easyui -->
<script type="text/javascript" src="${contextPath}/resources/easyui/js/jquery.easyui.min.js"></script>
<!-- easyui中文语言包 -->
<script type="text/javascript" src="${contextPath}/resources/easyui/js/easyui-lang-zh_CN.js"></script>
<!-- easyui扩展 -->
<script type="text/javascript" src="${contextPath}/common/easyui/js/easyui_extend.js"></script>

<!-- 本项目全局js -->
<script type="text/javascript" src="${contextPath}/resources/js/main.js"></script>

<script type="text/javascript" src="${contextPath}/resources/easyui/js/easyui-datagrid-editor-sweetalert.js"></script>

<!-- 本项目公共js -->
<script type="text/javascript" src="${contextPath}/resources/js/common.js"></script>
<script type="text/javascript" src="${contextPath}/resources/js/commonValidate.js"></script>

<script src="${contextPath}/webjars/sweetalert2/7.28.10/dist/sweetalert2.min.js"></script>

<!-- 导出功能 -->
<#export></#export>
<!-- 框架提供的js -->
<#mainJs/>

<script>
    /**
     * 查看图片大图
     * @param url 图片路径
     * @param viewTagId 用于承载显示图片信息的控件(推荐用DIV)ID，不需要#号
     */
    function _showBigImg(url,viewTagId) {
        if (!url || !viewTagId) {
            return;
        }
        var imgHeight = document.documentElement.clientHeight;
        var imgWidth = document.documentElement.clientWidth;
        $('#' + viewTagId).dialog({
            title: '查看图片',
            width: parseInt(imgWidth * 0.95),
            height: parseInt(imgHeight * 0.95),
            closable: true,
            modal: true,
            content: '<div style="text-align:center;"><img src="'
                + url.substring(0, url.indexOf('?')) + '?imageView2/2/w/'
                + imgWidth + '/h/' + imgHeight + '" /></div>',
            toolbar: [{
                iconCls: 'icon-search',
                handler: function () {
                    window.open(url.substring(0, url.indexOf('?')));
                }
            }]
        });
        $('#' + viewTagId).dialog('open');
    }
</script>
