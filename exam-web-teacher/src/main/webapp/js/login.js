var login = {};

login.toLogin = function () {
    var param = {
        tname: $('#tname').val(),
        pass: $('#pass').val()
    }

    $.post('common/login', param, function (f) {
        if (f) {
            //成功
            alert('登录成功');
            location.href = 'common/main.html';
        } else {
            //失败
            $('#msg').html('用户名密码错误');
            $('#pass').val('');
        }
    });

}