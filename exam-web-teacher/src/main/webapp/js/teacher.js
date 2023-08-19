var teacher = {}

teacher.toTeacherQuery = function (pageNo) {
    //如果没有指定页码，默认查询第一页。  过滤查询，清空查询不指定页码
    pageNo = pageNo ? pageNo : 1;

    var param = {
        pageNo: pageNo,
        tname: $('#search-tname').val()
    }

    $.post('teacher/pageTemplate.html', param, function (view) {
        $('#pageTemplate').replaceWith(view);

        //当前这个方法有2用可能。
        // 一个就是teacher自己使用不需要任何处理。
        // 另一个就是可能在模板的教师分享中也会使用，需要对教师表格做处理（增加双击，去掉操作列）
        try {
            //没有异常，证明teacher.js和template.js在同一个模块中（模板分享操作）
            template.teacherHandleForShare();
            console.log('template module');
        } catch (e) {
            //有异常，证明没有template。就只有teacher模块。不做任何处理
            console.log('teacher module');
        }
    });

}

teacher.toClearTeacherQuery = function () {
    $('#search-tname').val('');
    teacher.toTeacherQuery();
}

teacher.toPageTeacherQuery = function (pageNo) {
    teacher.toTeacherQuery(pageNo);
}

teacher.toAdd = function () {
    $.post('teacher/formTemplate.html', {}, function (view) {
        //设置此次对话框的 标题，内容，提交操作
        $('#teacher-modal-title').html('新建教师');
        $('#teacher-modal-body').html(view);
        $('#teacher-modal-submit').click(function () {
            //保存请求
            var param = {
                tname: $('#form-tname').val()
            }
            $.post('teacher/save', param, function (f) {
                if (f == true) {
                    alert('保存成功');

                    $('#teacher-modal').modal('hide');

                    //刷新表格内容
                    teacher.toClearTeacherQuery();
                } else {
                    alert('名称重复')
                }
            });
        });

        $('#teacher-modal').modal('show');
    });
}

teacher.toEdit = function (id) {
    $.post('teacher/formTemplate.html', {id: id}, function (view) {
        //设置此次对话框的 标题，内容，提交操作
        $('#teacher-modal-title').html('编辑教师');
        $('#teacher-modal-body').html(view);
        $('#teacher-modal-submit').click(function () {
            //ajax异步提交
            var param = {
                id: $('#form-id').val(),
                tname: $('#form-tname').val()
            }
            $.post('teacher/update', param, function (f) {
                if (f == true) {
                    alert('修改成功');
                    $('#teacher-modal').modal('hide');
                    //当前条件刷新
                    teacher.toTeacherQuery();
                } else {
                    alert('名称重复')
                }
            });
        });

        $('#teacher-modal').modal('show');
    });

}

teacher.toCheckAll = function () {
    var checked = $('#teacherGrid thead :checkbox').prop('checked');

    $('#teacherGrid tbody :checkbox').prop('checked', checked);

}

teacher.toDeleteOne = function (id) {

    let flag = confirm("是否删除该教师信息");
    if (!flag) {
        return;
    }
    $.post('teacher/toDeleteOne', {id: id}, function (f) {
        if (f) {
            alert("删除教师信息成功");
            var pageNo = $('.pagination .active').text();
            teacher.toTeacherQuery(pageNo);
        } else {
            alert("删除教师信息失败");
        }
    });

}

teacher.toDeleteAll = function () {

    if ($('#teacherGrid tbody :checked').length == 0) {
        //一行记录都没有没选中
        alert('请选择要删除的记录');
        return;
    }

    if (!confirm('是否确认删除选中的记录')) {
        //选择了取消
        return;
    }

    //将选中的要删除的记录id组成一个字符串
    /*
        for(var i=0;i<elements.length;i++){
            var element = elements[i] ;
            callback(i,element);
        }
     */
    var ids = '';
    $('#teacherGrid tbody :checked').each(function (i, element) {
        var id = element.value;
        ids += id + ",";
    });

    $.post('teacher/deleteAll', {ids: ids}, function () {
        alert('删除成功');

        //console.log($('.pagination .active').text());
        //console.log($('.pagination .active').html());

        var pageNo = $('.pagination .active').text();
        teacher.toTeacherQuery(pageNo);
    });
}

teacher.toImports = function () {
    var uploading = false;
    $.post('teacher/importsTemplate.html', {}, function (view) {
        $('#teacher-modal-title').html('导入教师信息');
        $('#teacher-modal-body').html(view);
        $('#teacher-modal-submit').click(function () {

            var fileInfo = $('#import-excel').val();
            if (!fileInfo) {
                alert('请选择要上传的excel文件');
                return;
            }
            if (uploading) {
                alert("文件正在上传中，请稍候");
                return false;
            }
            $.ajax({
                url: 'teacher/imports',
                type: 'POST',
                cache: false,
                data: new FormData($('#teacher-import-form')[0]),
                processData: false,
                contentType: false,
                beforeSend: function () {
                    uploading = true;
                },
                success: function (msg) {
                    uploading = false;
                    msg = msg.replace(/\|/g, "\r\n");
                    alert(msg);
                    $('#teacher-modal').modal('hide');
                    teacher.toClearTeacherQuery();
                }
            });

        });

        $('#teacher-modal').modal('show');
    });
}

teacher.chanageFile = function () {
    var fileInfo = $('#import-excel').val();
    if (fileInfo.endsWith('.xls') || fileInfo.endsWith('.xlsx')) {
        //展示
        $('#fileMsg').html(fileInfo);
    } else {
        alert('请选择正确的excel文件');
    }
}

