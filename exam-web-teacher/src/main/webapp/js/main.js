var main = {}

main.exit = function () {
    if (!confirm('是否确认退出'))
        return;
    location.href = 'common/exit';
}

main.toUpdatePwd = function () {

    $.post('common/updatePwdTemplate.html', {}, function (view) {
        $('#modal-body').html(view);

        //展示模态框
        $('#myModal').modal('show');

    });

}

main.updatePwd = function () {
    var new_pass = $('#new-pass').val();
    var re_pass = $('#re-pass').val();
    var old_pass = $('#old-pass').val();

    if (new_pass != re_pass) {
        alert('两次密码不一致');
        return;
    }

    var param = {
        old_pass: old_pass,
        new_pass: new_pass
    }
    $.post('common/updatePwd', param, function (f) {
        if (f == true) {
            alert('密码修改成功');
            $('#myModal').modal('hide');
            return;
        }

        alert('原密码不正确');
    });

}

/**
 * eg.
 * showDialog({
 *      title:'新建教师信息',
 *      content:ajax-view,
 *      submit:function(){
 *
 *      }
 * });
 * @param config {title , content , submit}
 */
main.showDialog = function (config) {
    $('#common-modal-title').html(config.title);
    $('#common-modal-body').html(config.content);
    $('#common-modal-submit').off('click').click(function () {
        config.submit();
    });
    $('#common-modal').modal('show');
}

main.showLgDialog = function (config) {
    $('.modal .modal-dialog').addClass('modal-lg');
    main.showDialog(config);
}
main.showDefaultDialog = function (config) {
    $('.modal .modal-dialog').removeClass('modal-lg');
    main.showDialog(config);
}

main.closeDialog = function () {
    $('#common-modal').modal('hide');
}

/*富文本编辑器全局设置*/
var E = window.wangEditor;

/*---------------自定义填空按钮 准备---------------*/
const {BtnMenu, DropListMenu, PanelMenu, DropList, Panel, Tooltip} = E

class BlankMenu extends BtnMenu {
    constructor(editor) {
        const $elem = E.$(
            `<div class="w-e-menu" data-title="填空">
<i>【_】</i>
</div>`
        )
        super($elem, editor)
    }

    // 菜单点击事件
    clickHandler() {
        this.editor.txt.append('<span>【】</span>');
    }

    tryChangeActive() {
        this.active()
    }
}

// 全局注册菜单
// 菜单 key ，各个菜单不能重复
//const menuKey = 'blank'
//E.registerMenu(menuKey, BlankMenu);
/*-------------------------------------------------------*/

/**
 * 完成编辑器的一些初始设置，主要针对于菜单栏和图片上传
 * 默认： height:200
 *       uploadImgServer:'common/editor-upload-img'
 *       uploadFileName:'imgs'
 *       excludeMenus:[
 'emoticon',
 'link',
 'list',
 'todo',
 'justify',
 'quote',
 'video'
 ]
 *
 * */
function editorDefaultInit(editor, config) {
    config = config ? config : {};
    editor.config.height = config.height ? config.height : 200;
    editor.config.uploadImgServer = config.uploadImgServer ? config.uploadImgServer : 'common/editor-upload-img';
    editor.config.uploadFileName = config.uploadFileName ? config.uploadFileName : 'imgs';
    editor.config.excludeMenus = config.excludeMenus ? config.excludeMenus : [
        'emoticon',
        'link',
        'list',
        'todo',
        'justify',
        'quote',
        'video'
    ];
}

