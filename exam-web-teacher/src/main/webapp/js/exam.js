var exam = {};

exam.toQuery = function (pageNo) {
    pageNo = pageNo ? pageNo : 1;

    var name = $('#search-name').val();
    if (!name) {
        name = $('#search-type').val()
    }
    var status = $('#search-status').val();
    var startTime = $('#search-start-time').val();
    var endTime = $('#search-end-time').val();

    var param = {
        name: name,
        status: status,
        startTime: startTime,
        endTime: endTime,

        pageNo: pageNo
    }

    console.log(param);

    $.post('exam/examGridTemplate.html', param, function (view) {
        $('#part-2').replaceWith(view);
    });


}

exam.toClearQuery = function () {
    $('.search-box .form-control').val('');
    exam.toQuery();
}

exam.toPageQuery = function (pageNo) {
    exam.toQuery(pageNo);
}

exam.toAdd = function () {

    $.post('exam/form.html', {}, function (view) {
        main.showDialog({
            title: '新建考试信息',
            content: view,
            submit: function () {
                exam.save();
            }
        });
    });

}

exam.save = function (id) {
    var param = {
        name: $('#exam-form-name').val()
    };
    var url;
    if (id) {
        url = 'exam/update';
        param.id = id;
    } else {
        url = 'exam/save';
    }

    $.post(url, param, function (f) {
        if (f == true) {
            alert('操作成功');
            main.closeDialog();

            if (id) {
                //编辑，刷新fill填充页的名称展示部分
                $('#fill-exam-name').html(param.name);
            } else {
                //保存，刷新表格
                exam.toClearQuery();
            }

        } else {
            alert('名称重复，操作失败');
        }
    });
}

exam.toEdit = function () {
    var id = $('#fill-form-id').val()
    $.post("exam/form.html", {id: id}, function (view) {
        main.showDefaultDialog({
            title: '编辑考试名称',
            content: view,
            submit: function () {
                exam.save(id);
            }
        })
    });
}

exam.toSelectTemplate = function () {
    $.post('exam/templateSelect.html', {}, function (view) {
        main.showLgDialog({
            title: '选择模板',
            content: view,
            submit: function () {
                main.closeDialog();
            }
        });
        exam.defaultTemplate();

    });
}

exam.defaultTemplate = function () {
    $('#templateGrid tr td:nth-child(11)').remove();
    $('#templateGrid tr th:nth-child(11)').remove();


    $('#templateGrid tr td:nth-child(7) span').removeAttr('onContextMenu');

    $('#templateGrid tr').dblclick(function () {
        var templateId = $('td:eq(0) input', $(this)).val();
        var templateName = $('td:eq(4)', $(this)).text().trim();
        $('#fill-form-template-name').val(templateName);
        $('#fill-form-template-id').val(templateId);
        main.closeDialog();
    });


}

exam.toShowTemplateDetail = function () {
    var templateId = $('#fill-form-template-id').val();
    if (!templateId) {
        //没有选择
        alert('请先选择考试模板');
        return;
    }

    $.post('exam/templateDetail.html', {templateId: templateId}, function (view) {
        main.showLgDialog({
            title: '模板详情',
            content: view,
            submit: function () {
                main.closeDialog();
            }
        });
        //处理默认状态
        exam.defaultTemplateDetail();
    });

}

exam.defaultTemplateDetail = function () {
    $('#template-edit-back-btn').remove();

    $('#dynamic-pane .form-control').prop('readOnly', true);
    $('#dynamic-pane input').prop('readOnly', true);
    $('#dynamic-pane .btn').remove();


    $('#static-pane .form-control').prop('readOnly', true);
    $('#static-pane input').prop('readOnly', true);
    $('#static-pane .btn').remove();

}

exam.selectTime = function (no) {
    if (no == 1) {
        //选择区间方式
        $('#fill-form-duration').prop('disabled', true);

        $('#fill-form-start-time').prop('disabled', false);
        $('#fill-form-end-time').prop('disabled', false);
    } else {
        //选择了时长方式
        $('#fill-form-start-time').prop('disabled', true);
        $('#fill-form-end-time').prop('disabled', true);

        $('#fill-form-duration').prop('disabled', false);
    }
}

exam.toSelectClasses = function () {
    $.post('exam/selectClasses.html', {}, function (view) {
        main.showLgDialog({
            title: '选择班级',
            content: view,
            submit: function () {

            }
        });
        exam.initHandleForSelectClasses();
    });
}

exam.initHandleForSelectClasses = function () {
    $('#exam-use-classes-students .part-1 .search-box .right').remove();
    $('#exam-use-classes-students .part-2 .search-box .form-group:not(:eq(2))').remove();
    $('#search-className').prop('readOnly', true);
}

exam.classesHandleForSelectClasses = function () {
    for (var i = 1; i <= 2; i++)
        $('#classGrid tr td:nth-child(5) button:nth-child(1)').remove();

    exam.classesBindingHandle();

    //需要根据缓存默认选中已绑定的班级
    $.post('exam/loadCacheClasses', {id: $('#fill-form-id').val()}, function (cache) {
        for (className in cache) {
            $('#classGrid tbody tr td:nth-child(1) input:checkbox[value="' + className + '"]').prop("checked", true);
        }
    }, 'json');

}

//班级的绑定与解绑
exam.classesBindingHandle = function () {
    //全选按钮
    var classAllBtn = $('#classGrid thead tr th:eq(0) input:checkbox');
    classAllBtn.click(function () {
        var classNames = '';
        $('#classGrid tbody tr td:nth-child(1) input:checkbox').each(function (i, cb) {
            classNames += cb.value + ',';
        });

        var param = {
            classNames: classNames,
            id: $('#fill-form-id').val()
        }

        var f = classAllBtn.prop('checked');
        if (f) {
            //绑定当前表格中所有班级
            exam.classesBinding(param);

            //需要处理一下学生复选框的关联
            $('#studentGrid input:checkbox').prop('checked', true);

        } else {
            //解绑当前表格中的所有班级
            exam.classesUnbinding(param);

            //需要处理一下学生复选框的关联
            $('#studentGrid input:checkbox').prop('checked', false);
        }
    });

    //单选按钮
    $('#classGrid tbody tr td:nth-child(1) input:checkbox').click(function () {
        var className = $(this).val();
        var param = {
            classNames: className,
            id: $('#fill-form-id').val()
        }

        var f = $(this).prop('checked');
        if (f) {
            exam.classesBinding(param);

            //需要处理一下学生复选框的关联，需要判断下面显示的班级是否是当前绑定班级
            if (className == $('#search-className').val()) {
                $('#studentGrid input:checkbox').prop('checked', true);
            }

        } else {
            exam.classesUnbinding(param);

            //需要处理一下学生复选框的关联，需要判断下面显示的班级是否是当前解绑班级
            if (className == $('#search-className').val()) {
                $('#studentGrid input:checkbox').prop('checked', false);
            }
        }

    });

}

exam.classesBinding = function (param) {

    $.post('exam/bindClasses', param, function () {
        alert('班级绑定成功');
        //刷新外部的班级表格
        exam.flushRefClassGrid();
    });
}

exam.classesUnbinding = function (param) {

    $.post('exam/unbindClasses', param, function () {
        alert('班级解绑成功');
        //刷新外部的班级表格
        exam.flushRefClassGrid();
    });
}

exam.studentsHandleForSelectClasses = function () {
    $('#studentGrid tr td:nth-last-child(1)').remove();
    $('#studentGrid tr th:nth-last-child(1)').remove();

    exam.studentsBindingHandle();

    //需要根据缓存，默认勾选学生信息
    var param = {
        id: $('#fill-form-id').val(),
        className: $('#search-className').val()
    }
    $.post('exam/loadCacheStudents', param, function (cache) {
        //cache就是string "ALL", "1,2,3,4,5"
        if (!cache) return;

        if (cache == 'ALL') {
            $('#studentGrid input:checkbox').prop('checked', true);
        } else {
            var sids = cache.split(",");
            for (var i = 0; i < sids.length; i++) {
                var sid = sids[i];
                $('#studentGrid :checkbox[value="' + sid + '"]').prop('checked', true);
            }
        }

    });
}

exam.studentsBindingHandle = function () {
    //全选按钮
    var studentAllBtn = $('#studentGrid thead tr th:eq(0) input:checkbox');
    studentAllBtn.click(function () {
        //全选学生，就相当于选单一班级
        var param = {
            classNames: $('#search-className').val(),
            id: $('#fill-form-id').val()
        }

        var f = studentAllBtn.prop('checked');
        if (f) {
            exam.classesBinding(param);

            //只要绑定了学生，对应的班级就一定需要绑定
            $('#classGrid tbody input:checkbox[value="' + param.classNames + '"]').prop('checked', true);

        } else {
            exam.classesUnbinding(param);

            //只要绑定了学生，对应的班级就一定需要绑定
            $('#classGrid tbody input:checkbox[value="' + param.classNames + '"]').prop('checked', false);

        }
    });

    //单选按钮
    $('#studentGrid tbody tr td:nth-child(1) input:checkbox').click(function () {
        var param = {
            id: $('#fill-form-id').val(),
            className: $('#search-className').val(),
            studentId: $(this).val(),
            classNames: $('#search-className').val(),
        }

        var f = $(this).prop('checked');
        if (f) {
            //选中了这个学生，有可能是全选中，也可能就是又增加了一个（不全）
            if ($('#studentGrid tbody input:not(:checked)').length == 0) {
                //没有没被选中的学生，也就是都被选中了。就变成了绑定班级
                //此时学生默认全选
                $('#studentGrid thead tr th:eq(0) input:checkbox').prop('checked', true);
                exam.classesBinding(param);
                return;
            }
            exam.studentBinding(param);

            //只要绑定了学生，对应的班级就一定需要绑定
            $('#classGrid tbody input:checkbox[value="' + param.classNames + '"]').prop('checked', true);


        } else {
            //解绑了这个学生
            if ($('#studentGrid tbody input:checked').length == 0) {
                exam.classesUnbinding(param);

                //只要绑定了学生，对应的班级就一定需要绑定
                $('#classGrid tbody input:checkbox[value="' + param.classNames + '"]').prop('checked', false);

                return;
            }
            exam.studentUnbinding(param);
        }
    });
}

exam.studentBinding = function (param) {
    $.post('exam/bindStudent', param, function () {
        alert('学生绑定成功');
        exam.flushRefClassGrid();
    });
}

exam.studentUnbinding = function (param) {
    $.post('exam/unbindStudent', param, function () {
        alert('学生解绑成功');
        exam.flushRefClassGrid();
    });
}

exam.flushRefClassGrid = function () {
    $.post('exam/refClassGrid.html', {id: $('#fill-form-id').val()}, function (view) {
        $('#refClassGrid').replaceWith(view);
    });
}

exam.toCreateClass = function () {
    $.post('exam/createClass.html', {}, function (view) {
        main.showDefaultDialog({
            title: '自定义班级',
            content: view,
            submit: function () {
                //将自定义班级存入缓存。
                var param = {
                    className: $('#exam-form-className').val(),
                    id: $('#fill-form-id').val()
                }
                $.post('exam/createClass', param, function (f) {
                    if (f == true) {
                        alert('班级创建成功');
                        main.closeDialog();
                        exam.flushRefClassGrid();
                    } else {
                        alert('班级名称重复，请重新填写');
                    }
                });
            }
        })
    });
}

exam.toImpotClasses = function () {
    var uploading = false;
    $.post('student/importsTemplate.html', {}, function (view) {
        main.showDefaultDialog({
            title: '导入班级学生',
            content: view,
            submit: function () {
                var fileInfo = $('#import-excel').val();
                if (!fileInfo) {
                    alert('请选择要上传的excel文件');
                    return;
                }
                if (uploading) {
                    alert("文件正在上传中，请稍候");
                    return false;
                }
                var formData = new FormData($('#student-import-form')[0]);
                formData.append('id', $('#fill-form-id').val());
                $.ajax({
                    url: 'exam/importClasses',
                    type: 'POST',
                    cache: false,
                    data: formData,
                    processData: false,
                    contentType: false,
                    beforeSend: function () {
                        uploading = true;
                    },
                    success: function (msg) {
                        uploading = false;
                        msg = msg.replace(/\|/g, "\r\n");
                        alert(msg);
                        main.closeDialog()
                        exam.flushRefClassGrid();
                    }
                });

            }
        });
    });
}

exam.removeRefClass = function (className) {
    if (!confirm('是否确认移除指定班级')) {
        return;
    }

    var param = {
        className: className,
        id: $('#fill-form-id').val()
    }

    $.post('exam/removeRefClass', param, function () {

        alert('移除成功');
        exam.flushRefClassGrid();
    });
}

exam.toImportStudents = function (className) {
    $.post('student/importsTemplate.html', {}, function (view) {
        var uploading = false;
        main.showDefaultDialog({
            title: '导入学生',
            content: view,
            submit: function () {
                var fileInfo = $('#import-excel').val();
                if (!fileInfo) {
                    alert('请选择要上传的excel文件');
                    return;
                }
                if (uploading) {
                    alert("文件正在上传中，请稍候");
                    return false;
                }
                var formData = new FormData($('#student-import-form')[0]);
                formData.append('id', $('#fill-form-id').val());
                formData.append('className', className)
                $.ajax({
                    url: 'exam/importStudents',
                    type: 'POST',
                    cache: false,
                    data: formData,
                    processData: false,
                    contentType: false,
                    beforeSend: function () {
                        uploading = true;
                    },
                    success: function (msg) {
                        uploading = false;
                        msg = msg.replace(/\|/g, "\r\n");
                        alert(msg);
                        main.closeDialog()
                        exam.flushRefClassGrid();
                    }
                });
            }
        });

        $('#student-import-form div:eq(1) a').attr('href', 'exam/downStudentTemplate');
    });
}

exam.toAdjustStudents = function (className) {
    var param = {
        id: $('#fill-form-id').val(),
        className: className
    }
    $.post('exam/adjustStudents.html', param, function (view) {
        main.showLgDialog({
            title: '调整【' + className + '】关联学生',
            content: view,
            submit: function () {

            }
        });
    });
}

/**
 * 查询指定班级中未绑定的学生信息 （需要参考当前这个班级已绑定的学生）
 * 当前这个班级绑定的学生在缓存中，只需要传递当前班级的名字即可。
 */
exam.toQueryUnbindStudents = function () {
    var param = {
        id: $('#fill-form-id').val(),
        className: $('#adjust-old-className').val(),
        searchName: $('#adjust-search-className').val()
    }

    $.post('exam/flushUnbindStudents.html', param, function (view) {
        $('#unbindGrid').replaceWith(view);
    });
}

exam.bindStudent = function (tr, sid) {
    var param = {
        id: $('#fill-form-id').val(),
        className: $('#adjust-old-className').val(),
        studentId: sid
    };

    $.post('exam/bindStudent', param, function () {
        alert('学生绑定成功');
        $(tr).appendTo($('#bindGrid tbody'));
        //移动后，tr的双击事件需要改变，由原来的绑定，改为解绑
        $(tr).attr('ondblclick', null).off('dblclick').dblclick(function () {
            exam.unbindStudent(tr, sid);
        });

        exam.flushRefClassGrid();
    });
}

exam.unbindStudent = function (tr, sid) {
    var param = {
        id: $('#fill-form-id').val(),
        className: $('#adjust-old-className').val(),
        studentId: sid
    };

    $.post('exam/unbindStudent', param, function () {
        alert('学生解绑成功');
        $(tr).appendTo($('#unbindGrid tbody'));
        $(tr).attr('ondblclick', null).off('dblclick').dblclick(function () {
            exam.bindStudent(tr, sid);
        });
        exam.flushRefClassGrid();
    });
}

exam.toFillSave = function () {
    var param = {
        id: $('#fill-form-id').val(),
        templateId: $('#fill-form-template-id').val()
    }
    var v = $('input[name="fill-form-time"]:checked').val();
    if (v == 1) {
        //区间
        param.startTime = $('#fill-form-start-time').val();
        param.endTime = $('#fill-form-end-time').val()
    } else {
        //时长
        param.duration = $('#fill-form-duration').val();
    }

    $.post('exam/fillSave', param, function (result) {
        //分析反馈信息。
        // 没有任何问题，就是保存成功提示
        // 但有问题的情况， 如出现了重复的学生。此时需要给出提示
        // 要求返回json格式  {code:0,msg:'保存成功'} , {code:1,msg:'【张三】重复关联【1班】【2班】|【李四】重复关联【1班】【2班】'}
        if (result.code == 0) {
            alert(result.msg);
            location.href = 'exam/exam.html';
        } else if (result.code == 1) {
            var msg = result.msg.replace(/\|/g, "\r\n");
            alert(msg);
        }
    }, 'json');

}

//设置未发布状态下的 右键处理操作
exam.toSetStatus1 = function (id, ev) {
    var e = ev || event;
    var x = e.clientX;
    var y = e.clientY;
    var div = $('<div></div>');
    div.css({
        position: 'absolute',
        width: 130,
        height: 140,
        left: x - 5,
        top: y - 5,
        background: '#fff',
        border: '1px solid #ccc',
        borderRadius: 5,
        boxShadow: '2px 2px 2px #ccc'
    });
    $('body').append(div);

    div.html(`
        <a class="btn btn-link" onclick="exam.toGeneratePage(${id})"><span class="glyphicon glyphicon-duplicate"></span> 生成考卷</a>
        <a class="btn btn-link" onclick="exam.toRemovePage(${id})"><span class="glyphicon glyphicon-trash"></span> 删除考卷</a>
        <a class="btn btn-link" onclick="exam.toReleaseExam(${id})"><span class="glyphicon glyphicon-share"></span> 发布考试</a>
        <a class="btn btn-link" onclick="exam.toRemoveExam(${id})"><span class="glyphicon glyphicon-remove-circle"></span> 删除考试</a>
    `);

    var timer;
    div.mouseleave(function () {
        timer = window.setTimeout(function () {
            div.remove();
        }, 200);
    });
    div.mouseover(function () {
        if (timer) {
            window.clearTimeout(timer);
        }
    });

    return false;
}

exam.toGeneratePage = function (id) {
    if (!confirm('是否确认生成考卷')) {
        return;
    }
    //开启进度条
    var bg = $('<div>');
    $('body').append(bg);
    bg.css({
        width: '100%',
        height: '100%',
        position: 'absolute',
        left: 0,
        top: 0,
        background: '#000',
        zIndex: 999999,
        opacity: 0.5
    });
    $('#exam-progress').css({
        display: 'block',
        width: '60%',
        position: 'absolute',
        zIndex: 1000000,
        left: '20%',
        top: 300
    });

    var width = 0;
    var timer = setInterval(function () {
        $('#exam-progress div').css('width', width + '%');
        width = width >= 100 ? 0 : width += 5;
    }, 50);
    $.post('exam/generatePage', {id: id}, function (f) {
        if (f == true) {
            alert('考卷生成完毕');
        } else {
            alert('考卷生成失败，考卷已存在');
        }
        //结束进度条
        clearInterval(timer);
        bg.remove();
        $('#exam-progress div').css('width', '0');
        $('#exam-progress').css({display: 'none'});
    });
}

exam.toRemovePage = function (id) {
    if (!confirm('是否确认删除考卷?')) {
        return;
    }
    $.post('exam/removePage', {id: id}, function (f) {
        if (f == true) {
            alert('考卷删除成功');
        } else {
            alert('当前状态，考卷无法删除');
        }
    });
}

exam.toRemoveExam = function (id) {
    if (!confirm('是否确认删除此次考试信息?')) {
        return;
    }
    $.post('exam/removeExam', {id: id}, function (f) {
        if (f == true) {
            alert('考试信息删除成功');
            //刷新表格
            exam.flushExamGrid();
        } else {
            alert('当前状态，考试信息无法删除');
        }
    });
}

exam.flushExamGrid = function () {
    var pageNo = $('.pagination .active').text().trim();
    exam.toQuery(pageNo);
}

exam.toReleaseExam = function (id) {
    if (!confirm('考试信息发布后不能再修改，是否确认发布?')) {
        return;
    }
    $.post('exam/releaseExam', {id: id}, function (f) {
        if (f == true) {
            alert('考试信息发布成功');
            //刷新表格
            exam.flushExamGrid();
        } else {
            alert('还未生成考卷，考试信息发布失败');
        }
    });
}

exam.toShowClassDetail = function (className) {
    var param = {
        id: $('#fill-form-id').val(),
        className: className
    }
    $.post('exam/classDetail.html', param, function (view) {
        main.showLgDialog({
            title: '【' + className + '】学生关联信息',
            content: view,
            submit: function () {

            }
        });
    });
}

//设置未发布状态下的 右键处理操作
exam.toSetStatus2 = function (id, ev) {
    var e = ev || event;
    var x = e.clientX;
    var y = e.clientY;
    var div = $('<div></div>');
    div.css({
        position: 'absolute',
        width: 130,
        height: 70,
        left: x - 5,
        top: y - 5,
        background: '#fff',
        border: '1px solid #ccc',
        borderRadius: 5,
        boxShadow: '2px 2px 2px #ccc'
    });
    $('body').append(div);

    div.html(`
        <a class="btn btn-link" onclick="exam.toFinishExam(${id})"><span class="glyphicon glyphicon-ban-circle"></span> 结束考试</a>
        <a class="btn btn-link" onclick="exam.toLeaveExam(${id})"><span class="glyphicon glyphicon-trash"></span> 丢弃考试</a>
    `);

    var timer;
    div.mouseleave(function () {
        timer = window.setTimeout(function () {
            div.remove();
        }, 200);
    });
    div.mouseover(function () {
        if (timer) {
            window.clearTimeout(timer);
        }
    });

    return false;
}

//设置考试中状态下的 右键处理操作
exam.toSetStatus3 = function (id, ev) {
    var e = ev || event;
    var x = e.clientX;
    var y = e.clientY;
    var div = $('<div></div>');
    div.css({
        position: 'absolute',
        width: 130,
        height: 40,
        left: x - 5,
        top: y - 5,
        background: '#fff',
        border: '1px solid #ccc',
        borderRadius: 5,
        boxShadow: '2px 2px 2px #ccc'
    });
    $('body').append(div);

    div.html(`
        <a class="btn btn-link" onclick="exam.toFinishExam(${id})"><span class="glyphicon glyphicon-ban-circle"></span> 结束考试</a>
    `);

    var timer;
    div.mouseleave(function () {
        timer = window.setTimeout(function () {
            div.remove();
        }, 200);
    });
    div.mouseover(function () {
        if (timer) {
            window.clearTimeout(timer);
        }
    });

    return false;
}

//设置已完成状态下的 右键处理操作
exam.toSetStatus4 = function (id, ev) {
    var e = ev || event;
    var x = e.clientX;
    var y = e.clientY;
    var div = $('<div></div>');
    div.css({
        position: 'absolute',
        width: 130,
        height: 70,
        left: x - 5,
        top: y - 5,
        background: '#fff',
        border: '1px solid #ccc',
        borderRadius: 5,
        boxShadow: '2px 2px 2px #ccc'
    });
    $('body').append(div);

    div.html(`
        <a class="btn btn-link" onclick="exam.toReviewPage(${id})"><span class="glyphicon glyphicon-eye-open"></span> 查阅考卷</a>
        <a class="btn btn-link" onclick="exam.toLeaveExam(${id})"><span class="glyphicon glyphicon-trash"></span> 丢弃考试</a>
    `);

    var timer;
    div.mouseleave(function () {
        timer = window.setTimeout(function () {
            div.remove();
        }, 200);
    });
    div.mouseover(function () {
        if (timer) {
            window.clearTimeout(timer);
        }
    });

    return false;
}

exam.toLeaveExam = function (id) {
    if (!confirm('丢弃后考试失效，是否继续?')) {
        return;
    }
    $.post('exam/leaveExam', {id: id}, function () {
        alert('设置成功');
        exam.flushExamGrid();
    });
}

exam.toFinishExam = function (id) {
    if (!confirm('是否确认结束当前考试?')) {
        return;
    }
    $.post('exam/finishExam', {id: id}, function (f) {
        if (f == true) {
            alert('设置成功');
            exam.flushExamGrid();
        } else {
            alert('设置失败，当前考试不能手动设置结束，需要自然结束');
        }
    });
}

exam.toReviewPage = function (id) {
    location.href = 'exam/pageList.html?id=' + id;
}