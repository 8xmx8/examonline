var template = {}

template.dynamic = {};
template.static = {};

template.dynamic.calculate = function () {
    var total_score = 0;
    //每完成一次输入操作，都重新计算
    //单选题处理
    var score = $('#dynamic-form-question1-score').val();
    var count1 = $('#dynamic-form-question1-level1').val();
    var count2 = $('#dynamic-form-question1-level2').val();
    var count3 = $('#dynamic-form-question1-level3').val();
    score = parseInt(score ? score : 0);
    count1 = parseInt(count1 ? count1 : 0);
    count2 = parseInt(count2 ? count2 : 0);
    count3 = parseInt(count3 ? count3 : 0);

    var count = count1 + count2 + count3;
    var total = count * score;
    $('#dynamic-question1-count').html(count + ' 题');
    $('#dynamic-question1-total').html(total + ' 分');
    total_score += total;

    //多选题处理
    var score = $('#dynamic-form-question2-score').val();
    var count1 = $('#dynamic-form-question2-level1').val();
    var count2 = $('#dynamic-form-question2-level2').val();
    var count3 = $('#dynamic-form-question2-level3').val();
    score = parseInt(score ? score : 0);
    count1 = parseInt(count1 ? count1 : 0);
    count2 = parseInt(count2 ? count2 : 0);
    count3 = parseInt(count3 ? count3 : 0);

    var count = count1 + count2 + count3;
    var total = count * score;
    $('#dynamic-question2-count').html(count + ' 题');
    $('#dynamic-question2-total').html(total + ' 分');
    total_score += total;

    //判断题处理
    var score = $('#dynamic-form-question3-score').val();
    var count1 = $('#dynamic-form-question3-level1').val();
    var count2 = $('#dynamic-form-question3-level2').val();
    var count3 = $('#dynamic-form-question3-level3').val();
    score = parseInt(score ? score : 0);
    count1 = parseInt(count1 ? count1 : 0);
    count2 = parseInt(count2 ? count2 : 0);
    count3 = parseInt(count3 ? count3 : 0);

    var count = count1 + count2 + count3;
    var total = count * score;
    $('#dynamic-question3-count').html(count + ' 题');
    $('#dynamic-question3-total').html(total + ' 分');
    total_score += total;

    //填空题处理
    var score = $('#dynamic-form-question4-score').val();
    var count1 = $('#dynamic-form-question4-level1').val();
    var count2 = $('#dynamic-form-question4-level2').val();
    var count3 = $('#dynamic-form-question4-level3').val();
    score = parseInt(score ? score : 0);
    count1 = parseInt(count1 ? count1 : 0);
    count2 = parseInt(count2 ? count2 : 0);
    count3 = parseInt(count3 ? count3 : 0);

    var count = count1 + count2 + count3;
    var total = count * score;
    $('#dynamic-question4-count').html(count + ' 题');
    $('#dynamic-question4-total').html(total + ' 分');
    total_score += total;

    //综合题
    var score = $('#dynamic-form-question5-score').val();
    var count1 = $('#dynamic-form-question5-level1').val();
    var count2 = $('#dynamic-form-question5-level2').val();
    var count3 = $('#dynamic-form-question5-level3').val();
    score = parseInt(score ? score : 0);
    count1 = parseInt(count1 ? count1 : 0);
    count2 = parseInt(count2 ? count2 : 0);
    count3 = parseInt(count3 ? count3 : 0);

    var count = count1 + count2 + count3;
    var total = count * score;
    $('#dynamic-question5-count').html(count + ' 题');
    $('#dynamic-question5-total').html(total + ' 分');
    total_score += total;

    $('#dynamic-form-totalScore').val(total_score);

}

template.dynamic.save = function () {
    var score1 = $('#dynamic-form-question1-score').val();
    var count11 = $('#dynamic-form-question1-level1').val();
    var count12 = $('#dynamic-form-question1-level2').val();
    var count13 = $('#dynamic-form-question1-level3').val();

    var score2 = $('#dynamic-form-question2-score').val();
    var count21 = $('#dynamic-form-question2-level1').val();
    var count22 = $('#dynamic-form-question2-level2').val();
    var count23 = $('#dynamic-form-question2-level3').val();

    var score3 = $('#dynamic-form-question3-score').val();
    var count31 = $('#dynamic-form-question3-level1').val();
    var count32 = $('#dynamic-form-question3-level2').val();
    var count33 = $('#dynamic-form-question3-level3').val();

    var score4 = $('#dynamic-form-question4-score').val();
    var count41 = $('#dynamic-form-question4-level1').val();
    var count42 = $('#dynamic-form-question4-level2').val();
    var count43 = $('#dynamic-form-question4-level3').val();

    var score5 = $('#dynamic-form-question5-score').val();
    var count51 = $('#dynamic-form-question5-level1').val();
    var count52 = $('#dynamic-form-question5-level2').val();
    var count53 = $('#dynamic-form-question5-level3').val();

    //正常还需要有验证，  name，course ， totalScore
    var param = {
        type: '动态模板',
        name: $('#dynamic-form-name').val(),
        course: $('#dynamic-form-course').val(),
        totalScore: $('#dynamic-form-totalScore').val(),

        score1: score1 ? score1 : 0,
        count11: count11 ? count11 : 0,
        count12: count12 ? count12 : 0,
        count13: count13 ? count13 : 0,

        score2: score2 ? score2 : 0,
        count21: count21 ? count21 : 0,
        count22: count22 ? count22 : 0,
        count23: count23 ? count23 : 0,

        score3: score3 ? score3 : 0,
        count31: count31 ? count31 : 0,
        count32: count32 ? count32 : 0,
        count33: count33 ? count33 : 0,

        score4: score4 ? score4 : 0,
        count41: count41 ? count41 : 0,
        count42: count42 ? count42 : 0,
        count43: count43 ? count43 : 0,

        score5: score5 ? score5 : 0,
        count51: count51 ? count51 : 0,
        count52: count52 ? count52 : 0,
        count53: count53 ? count53 : 0
    }


    var id = $('#dynamic-form-id').val();
    var url = '';
    var msg = '';
    if (id == '') {
        //没有id，保存
        url = 'template/dynamic/save';
        msg = '保存成功，继续添加';
    } else {
        //有id，修改
        url = 'template/dynamic/update'
        param.id = id;
        msg = '修改成功';
    }

    $.post(url, param, function (f) {
        if (f == true) {
            alert(msg);
            //需要清空组件数据
            if (id == '') {
                //保存成功，重置页面，准备下一次添加
                $('.dynamic-box .form-control:lt(2)').val('');
                $('.dynamic-box .form-control:gt(1)').val(0);
                //清空徽章信息（每道题数量和总分）
                $('.dynamic-box .badge:even').html('0 题');
                $('.dynamic-box .badge:odd').html('0 分');
            } else {
                //修改成功，返回列表页
                location.href = 'template/template.html';
            }

        } else {
            alert('修改保存失败，请检查名称是否重复');
        }
    });


}
//-----------------------动静分离----------------------------------------
var separator = '}-|-{';
var subject_editor;
var option_editors = [];
var answer_editor; //综合题答案编辑器

template.static.editorReset = function () {
    subject_editor = null;
    answer_editor = null;
    option_editors = [];
}

template.static.toAddQuestion = function () {
    template.static.editorReset();
    $('.modal-dialog').addClass('modal-lg');
    $.post('template/questionTemplate.html', {}, function (view) {
        main.showDialog({
            title: '添加试题',
            content: view,
            submit: function () {
                template.static.cacheQuestion();
            }
        });

        //初始化文本编辑器
        //题干
        subject_editor = new E('#static-question-subject');
        editorDefaultInit(subject_editor);
        subject_editor.create();

        //默认初始化单选题选项
        template.static.changeQuestion1();
    });

}

template.static.changeQuestion = function () {
    //切换新的编辑器
    $('#static-question-subject').replaceWith('<div id="static-question-subject"></div>');
    var element = $('#static-question-subject').toArray()[0];
    subject_editor = new E(element);
    editorDefaultInit(subject_editor);
    subject_editor.create();

    //清空之前的选项编辑器数组
    option_editors = [];

    //清空之前的题型选项
    $('#question-template .row:gt(2)').remove();


    var type = $('#static-form-question-type').val();

    if (type == '单选题') {
        template.static.changeQuestion1();
    } else if (type == '多选题') {
        template.static.changeQuestion2();
    } else if (type == '判断题') {
        template.static.changeQuestion3();
    } else if (type == '填空题') {
        template.static.changeQuestion4();
    } else if (type == '综合题') {
        template.static.changeQuestion5();
    }
}

//添加考题时，切换至单选题
template.static.changeQuestion1 = function () {
    //清空题干内容（需要重新填写）
    //subject_editor.txt.html('');
    //增加选项（默认4个）
    //哪来的选项？（questin-clone-box)
    for (var i = 0; i < 4; i++) {
        var option = $('#question-clone-box .row:eq(0)').clone();
        $('#question-template').append(option);
    }
    //将所有的选项内容转换成编辑器
    $('#question-template .question1-option-content').each(function (i, element) {
        var e = new E(element);
        editorDefaultInit(e);
        e.create();
        option_editors.push(e);
    });

    //为选项排序
    template.static.optionSort('.question1-option')


    //增加一个添加选项的按钮
    $('#question-template').append(`
        <div style="text-align:center" class="row">
          <a class="btn btn-link" onclick="template.static.addOption1()">
               <span class="glyphicon glyphicon-plus"></span>增加选项
           </a>
        </div>
    `);
}

template.static.optionSort = function (classId) {
    $('#question-template ' + classId + ' span').each(function (i, span) {
        // 字符A变成数字， 然后加序号（0,1,2,3），再将数字转换成字符
        span.innerHTML = String.fromCharCode('A'.charCodeAt(0) + i);
    });

    //设置选项值（0,1,2,3）
    $('#question-template ' + classId + ' input').each(function (i, input) {
        // 字符A变成数字， 然后加序号（0,1,2,3），再将数字转换成字符
        input.value = i;
    });
}

template.static.addOption1 = function () {
    var option = $('#question-clone-box .row:eq(0)').clone();
    $('#question-template .row:last').before(option);
    var element = $('#question-template .question1-option-content:last').toArray()[0];
    var e = new E(element);
    editorDefaultInit(e);
    e.create();
    option_editors.push(e);

    template.static.optionSort('.question1-option');
}

template.static.removeOption1 = function (curr_a) {
    //确保至少有2个选项
    if ($('#question-template .question1').length == 2) {
        alert('至少保留2个选项')
        return;
    }


    if (!confirm('是否确认删除该选项')) {
        return;
    }

    //  a     <div part3> <div row>
    $(curr_a).parent().parent().remove();

    //重新计算选项
    template.static.optionSort('.question1-option');
}

//切换至多选题
template.static.changeQuestion2 = function () {
    //清空题干内容（需要重新填写）
    //subject_editor.txt.html('');

    //增加选项（默认4个）
    //哪来的选项？（questin-clone-box)
    for (var i = 0; i < 4; i++) {
        var option = $('#question-clone-box .row:eq(1)').clone();
        $('#question-template').append(option);
    }
    //将所有的选项内容转换成编辑器
    $('#question-template .question2-option-content').each(function (i, element) {
        var e = new E(element);
        editorDefaultInit(e);
        e.create();
        option_editors.push(e);
    });

    //为选项排序
    template.static.optionSort('.question2-option')


    //增加一个添加选项的按钮
    $('#question-template').append(`
        <div style="text-align:center" class="row">
          <a class="btn btn-link" onclick="template.static.addOption2()">
               <span class="glyphicon glyphicon-plus"></span>增加选项
           </a>
        </div>
    `);
}

template.static.addOption2 = function () {
    var option = $('#question-clone-box .row:eq(1)').clone();
    $('#question-template .row:last').before(option);
    var element = $('#question-template .question2-option-content:last').toArray()[0];
    var e = new E(element);
    editorDefaultInit(e);
    e.create();
    option_editors.push(e);

    template.static.optionSort('.question2-option');
}

template.static.removeOption2 = function (curr_a) {
    //确保至少有2个选项
    if ($('#question-template .question2').length == 2) {
        alert('至少保留2个选项')
        return;
    }


    if (!confirm('是否确认删除该选项')) {
        return;
    }

    //  a     <div part3> <div row>
    $(curr_a).parent().parent().remove();

    //重新计算选项
    template.static.optionSort('.question2-option');
}

//切换至判断题
template.static.changeQuestion3 = function () {

    var option = $('#question-clone-box .row:eq(2)').clone();
    $('#question-template').append(option)

}

//切换至综合题
template.static.changeQuestion5 = function () {

    var option = $('#question-clone-box .row:eq(4)').clone();
    $('#question-template').append(option)

    var element = $('#question-template .question5-option-content').toArray()[0];
    var e = new E(element);
    editorDefaultInit(e);
    e.create();
    answer_editor = e;

}

//切换至填空题
template.static.changeQuestion4 = function () {
    //填空题需要为编辑器增加换一个“填空按钮”
    //这需要重新构建编辑器
    $('#static-question-subject').replaceWith('<div id="static-question-subject"></div>');
    var element = $('#static-question-subject').toArray()[0];
    subject_editor = new E(element);
    editorDefaultInit(subject_editor);
    // 注册菜单
    subject_editor.menus.extend("blank", BlankMenu);
    subject_editor.config.menus = subject_editor.config.menus.concat('blank')
    subject_editor.create();

    //为题干编辑器增加一个监控时间，监控编辑器内容
    //每次内容变化，都检查【】符号的数量，并生成或取消对应的输入选项
    //每次添加和删除的选项都在最后
    subject_editor.txt.eventHooks.changeEvents.push(function () {
        //console.log(subject_editor.txt.html());
        //console.log('-------------------------');
        var text = subject_editor.txt.html();
        //计算【】符合的数量
        var count = 0;
        while (true) {
            var p = text.indexOf("【】");
            if (p != -1) {
                count++;
                text = text.substring(p + 1);
                continue;
            }
            break;
        }
        //检查填空选项的匹配。  count<现有数量，减少选项。 count>现有数量，增加选项，
        var option_count = $('#question-template .row:gt(2)').length;
        if (count < option_count) {
            //需要减少
            var down = option_count - count;
            for (var i = 0; i < down; i++) {
                $('#question-template .row:last').remove();
            }
        } else if (count > option_count) {
            //需要增加
            //count=6. option_count=4  增加2
            //1,2,3,4,4+1,4+2
            var up = count - option_count;
            for (var i = 1; i <= up; i++) {
                var option = $('#question-clone-box .row:eq(3)').clone();
                $('#question-template').append(option);
                $('#question-template .row:last .question4-option span').html(option_count + i);
            }
        }
    });
}

//将此次录入/编辑的考题存入服务器缓存（session）
template.static.cacheQuestion = function () {
    var param = {
        id: $('#static-form-id').val(),
        index: $('#static-form-question-index').val(),
        type: $('#static-form-question-type').val(),
        level: $('#static-form-question-level').val(),
        subject: subject_editor.txt.html()
    }
    var options = '';
    var answer = '';

    //处理单选题和多选题的选项内容
    if (param.type == '单选题' || param.type == '多选题') {
        //有选项
        for (var i = 0; i < option_editors.length; i++) {
            var e = option_editors[i];
            options += e.txt.html() + separator;
        }
    }


    //处理答案
    if (param.type == '单选题') {
        answer = $('#question-template input[name="question1-option"]:checked').val();
    }
    if (param.type == '多选题') {
        $('#question-template input[name="question2-option"]:checked').each(function (i, input) {
            answer += input.value + separator;
        });
    }
    if (param.type == '判断题') {
        answer = $('#question-template input[name="question3-option"]:checked').val();
    }
    var f = true;
    if (param.type == '填空题') {
        $('#question-template input[name="question4-option"]').each(function (i, input) {
            if (input.value == '') {
                f = false;
                return;
            }
            answer += input.value + separator;
        });
    }

    if (param.type == '综合题') {
        answer = answer_editor.txt.html();
    }

    if (!answer || (param.type == '填空题' && !f)) {
        alert('请选择答案');
        return;
    }

    param.options = options;
    param.answer = answer;

    $.post('template/cacheQuestion', param, function (view) {
        if (param.index && param.index != '') {
            //编辑操作
            alert('考题编辑成功')
            $('#static-left-box .left-part').eq(param.index).replaceWith(view);
        } else {
            //添加操作
            alert('考题添加成功');
            $('.static-left-box .left-part:last').before(view);
        }
        template.static.calculate();
        main.closeDialog();
    });

}

template.static.calculate = function () {
    var count1 = 0;
    var count2 = 0;
    var count3 = 0;
    var count4 = 0;
    var blank_count4 = 0;
    var count5 = 0;

    var total1 = 0;
    var total2 = 0;
    var total3 = 0;
    var total4 = 0;
    var total5 = 0;


    $('.static-question-type').each(function (i, span) {
        var type = span.innerText.trim();
        switch (type) {
            case '单选题':
                count1++;
                break;
            case '多选题':
                count2++;
                break;
            case '判断题':
                count3++;
                break;
            case '填空题': {
                count4++;
                var left_part = $(span).parent().parent().parent().parent();
                blank_count4 += $('.left-option', left_part).length;
            }
                break;
            case '综合题':
                count5++;
                break;
        }
    });

    var score1 = $('#static-form-question1-score').val();
    var score2 = $('#static-form-question2-score').val();
    var score3 = $('#static-form-question3-score').val();
    var score4 = $('#static-form-question4-score').val();
    var score5 = $('#static-form-question5-score').val();
    score1 = parseInt(score1);
    score2 = parseInt(score2);
    score3 = parseInt(score3);
    score4 = parseInt(score4);
    score5 = parseInt(score5);

    total1 = score1 * count1;
    total2 = score2 * count2;
    total3 = score3 * count3;
    total4 = score4 * blank_count4;
    total5 = score5 * count5;

    $('#static-question1-total').html(total1);
    $('#static-question1-count').html(count1);

    $('#static-question2-total').html(total2);
    $('#static-question2-count').html(count2);

    $('#static-question3-total').html(total3);
    $('#static-question3-count').html(count3);

    $('#static-question4-total').html(total4);
    $('#static-question4-count').html(count4);
    ;
    $('#static-question4-blank-count').html(blank_count4);

    $('#static-question5-total').html(total5);
    $('#static-question5-count').html(count5);

    var totalScore = total1 + total2 + total3 + total4 + total5;
    $('#static-form-totalScore').html(totalScore);

}

template.static.removeQuestion = function (btn) {
    if (!confirm('是否确认移除当前试题')) {
        return;
    }

    //var index = $(btn).parent().parent().children().children().children('.static-question-index').html().trim();
    var left_title = $(btn).parent().parent();
    var index = $('.static-question-index', left_title).html().trim();

    //需要将后台缓存中对应考题也删除
    $.post('template/removeQuestion', {index: index, id: $('#static-form-id').val()}, function () {
        alert('考题移除成功');
        //在页面中删除这道题，只需要找到其对应的 .left-part标签部分，remove即可
        $(btn).parent().parent().parent().remove();

        //页面需要重新计算考题序号
        template.static.questionSort();

        //重新计算分数
        template.static.calculate();
    })

}

template.static.questionSort = function () {
    $('#static-left-box .static-question-index').each(function (i, span) {
        span.innerHTML = i + 1;
        $(span).prev().val(i + 1);
    });
}

template.static.checkAll = function (btn) {
    var f = btn.checked;
    $('#static-left-box .left-title :checkbox').prop('checked', f);
}

template.static.removeQuestions = function () {
    var indexes = '';
    $('#static-left-box .left-title :checked').each(function (i, checkbox) {
        indexes += checkbox.value + ',';
    });
    if (indexes == '') {
        alert('请选择要移出的考题');
        return;
    }
    if (!confirm('是否确认移除选中的考题')) {
        return;
    }

    $.post('template/removeQuestions', {indexes: indexes, id: $('#static-form-id').val()}, function () {
        alert('考题移除成功');

        //移除页面考题
        $('#static-left-box .left-title :checked').parent().parent().parent().parent().remove()

        template.static.questionSort();

        template.static.calculate();
    });

}

template.static.toEditQuestion = function (btn) {
    template.static.editorReset();

    $('.modal-dialog').addClass('modal-lg');
    $.post('template/questionTemplate.html', {}, function (view) {
        main.showDialog({
            title: '编辑试题',
            content: view,
            submit: function () {
                template.static.cacheQuestion();
            }
        });

        //初始化文本编辑器
        //题干
        subject_editor = new E('#static-question-subject');
        editorDefaultInit(subject_editor);
        subject_editor.create();

        //默认初始化待编辑的考题

        var left_title = $(btn).parent().parent();
        var index = $('.static-question-index', left_title).html().trim();
        template.static.editQuestionInit(index);
    });
}

//在编辑考题时，将待编辑的考题信息，展示在页面中。
template.static.editQuestionInit = function (index) {

    //获取指定题号的考题
    $.post('template/editQuestion', {index: index, id: $('#static-form-id').val()}, function (question) {
        console.log(question);

        $('#static-form-question-index').val(index);

        $('#static-form-question-type').prop('disabled', true);

        $('#static-form-question-type').val(question.type);

        subject_editor.txt.html(question.subject);
        if (question.type == '单选题') {
            template.static.changeQuestion1();

            var count = question.optionList.length;
            if (count < 4) {
                //减少选项
                while (count != $('#question-template .question1').length) {
                    $('#question-template .question1:last').remove();
                }
            } else if (count > 4) {
                //增加选项
                while (count != $('#question-template .question1').length) {
                    template.static.addOption1();
                }
            }

            //代码至此，选项数量一致了。
            var value = question.answerList[0];
            $('#question-template [name="question1-option"]').eq(value).prop('checked', true);

            //将选项的文本内容加入对应的编辑器
            for (var i = 0; i < question.optionList.length; i++) {
                var content = question.optionList[i];
                option_editors[i].txt.html(content);
            }
        } else if (question.type == '多选题') {
            template.static.changeQuestion2();

            var count = question.optionList.length;
            if (count < 4) {
                //减少选项
                while (count != $('#question-template .question2').length) {
                    $('#question-template .question2:last').remove();
                }
            } else if (count > 4) {
                //增加选项
                while (count != $('#question-template .question2').length) {
                    template.static.addOption2();
                }
            }

            //代码至此，选项数量一致了。
            for (var i = 0; i < question.answerList.length; i++) {
                var value = question.answerList[i];
                $('#question-template [name="question2-option"]').eq(value).prop('checked', true);
            }
            //将选项的文本内容加入对应的编辑器
            for (var i = 0; i < question.optionList.length; i++) {
                var content = question.optionList[i];
                option_editors[i].txt.html(content);
            }

        } else if (question.type == '判断题') {
            template.static.changeQuestion3();
            //装载答案
            var value = question.answerList[0];
            $('#question-template [name="question3-option"][value="' + value + '"]').prop('checked', true);
        } else if (question.type == '填空题') {
            template.static.changeQuestion4();
            //此时编辑器发生了变化（自定义填空按钮）
            subject_editor.txt.html(question.subject);
            //装载答案，让输入框处理一会（延迟加载）
            window.setTimeout(function () {
                while ($('#question-template [name="question4-option"]').length == question.answerList.length) {
                    for (var i = 0; i < question.answerList.length; i++) {
                        var value = question.answerList[i];
                        $('#question-template [name="question4-option"]').eq(i).val(value);
                    }
                    break;
                }
            }, 50);

        } else if (question.type == '综合题') {
            template.static.changeQuestion5();
            //装载答案,综合题的答案需要写在一个编辑器中，answer_editor
            var value = question.answerList[0];
            answer_editor.txt.html(value);
        }

    }, 'json');
}

template.static.toImportQuestions = function () {
    var uploading = false;
    $.post('template/importsTemplate.html', {}, function (view) {
        main.showDialog({
            title: '导入考题信息',
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
                var formData = new FormData($('#static-question-import-form')[0]);
                formData.append('id', $('#static-form-id').val());
                $.ajax({
                    url: 'template/importQuestions',
                    type: 'POST',
                    cache: false,
                    data: formData,
                    processData: false,
                    contentType: false,
                    beforeSend: function () {
                        uploading = true;
                    },
                    success: function (view) {
                        uploading = false;

                        $('#static-left-box .bottom-part').before(view);

                        var msg = $('.static-question-import-msg:last').val();
                        main.closeDialog();
                        msg = msg.replace(/\|/g, "\r\n");
                        alert(msg);

                        template.static.calculate();
                    }
                });
            }
        })

    });
}

template.static.chanageFile = function () {
    var fileInfo = $('#import-excel').val();
    if (fileInfo.endsWith('.xls') || fileInfo.endsWith('.xlsx')) {
        //展示
        $('#fileMsg').html(fileInfo);
    } else {
        alert('请选择正确的excel文件');
    }
}

template.static.cancelSave = function () {
    if (!confirm('取消后试题清空，是否确认取消')) {
        return;
    }

    //清空后台缓存
    var id = $('#static-form-id').val();
    $.post('template/cancelSave', {id: id}, function () {
        alert('操作成功');
        if (id != '') {
            //编辑操作，取消后，回显列表
            location.href = 'template/template.html';
        } else {
            //添加操作，取消后，清空前台页面
            template.static.clearView();
        }
    });
}

template.static.clearView = function () {
    $('#static-form-name').val('');
    $('#static-form-course').val('');
    $('#static-left-box .left-part:gt(0)').not(':last').remove();
    template.static.calculate();
}

template.static.save = function () {
    var param = {
        type: '静态模板',
        name: $('#static-form-name').val(),
        course: $('#static-form-course').val(),
        totalScore: $('#static-form-totalScore').html().trim(),
        question1: $('#static-form-question1-score').val(),
        question2: $('#static-form-question2-score').val(),
        question3: $('#static-form-question3-score').val(),
        question4: $('#static-form-question4-score').val(),
        question5: $('#static-form-question5-score').val(),
    }

    var id = $('#static-form-id').val();
    var url = '';
    var msg = '';
    if (id != '') {
        //修改
        url = 'template/static/update';
        param.id = id;
        msg = '模板修改成功';
    } else {
        //保存
        url = 'template/static/save';
        msg = '模板保存成功，请继续';
    }

    $.post(url, param, function (f) {
        if (f == true) {
            alert(msg);
            if (id != '') {
                location.href = 'template/template.html';
            } else {
                template.static.clearView();
            }
        } else {
            alert('模板名称重复，操作失败')
        }
    });
}

/**===========================================================================*/

template.toQuery = function (pageNo) {
    pageNo = pageNo ? pageNo : 1;

    var param = {
        pageNo: pageNo,
        name: $('#search-name').val(),
        course: $('#search-course').val(),
        type: $('#search-type').val(),
        status: $('#search-status').val(),
        shareid: $('#search-share').val()
    }

    $.post('template/templateGrid.html', param, function (view) {
        $('#part-2').replaceWith(view);

        try {
            //没有异常，可以使用exam，证明template 和examl在一起，证明是考试信息填充页
            exam.defaultTemplate();
        } catch (e) {
            //有异常，不能使用exam，证明就是template单独页面
            console.log('exam object not defined')
        }

    });


}

template.toClearQuery = function () {
    $('.search-box .form-control').val('');
    template.toQuery();
}

template.toPageQuery = function (pageNo) {
    template.toQuery(pageNo);
}

template.refresh = function () {
    var pageNo = $('.pagination .active').text().trim();
    template.toQuery(pageNo);
}

template.toDelete = function (id) {
    if (!confirm('是否确认删除')) {
        return;
    }

    $.post('template/delete', {id: id}, function (flag) {
        if (flag == true) {
            alert('删除成功');

            template.refresh();

            return;
        } else {
            alert('只能删除私有的模板');
        }
    });
}

template.toSetStatus = function (id, ev) {
    var e = ev || event;
    var x = e.clientX;
    var y = e.clientY;
    var div = $('<div></div>');
    div.css({
        position: 'absolute',
        width: 130,
        height: 110,
        left: x - 5,
        top: y - 5,
        background: '#fff',
        border: '1px solid #ccc',
        borderRadius: 5,
        boxShadow: '2px 2px 2px #ccc'
    });
    $('body').append(div);

    div.html(`
        <a class="btn btn-link" onclick="template.toSetPublic(${id})"><span class="glyphicon glyphicon-duplicate"></span> 设置公有</a>
        <a class="btn btn-link" onclick="template.toSetShare(${id})"><span class="glyphicon glyphicon-level-up"></span> 设置分享</a>
        <a class="btn btn-link" onclick="template.toSetLeave(${id})"><span class="glyphicon glyphicon-ban-circle"></span> 设置丢弃</a>
    `);

    div.mouseleave(function () {
        div.remove();
    });

    return false;
}

template.toSetPublic = function (id) {
    if (!confirm('是否确认设置模板为公有状态')) {
        return;
    }
    $.post('template/changePublic', {id: id}, function () {
        alert('设置成功');
        template.refresh();
    });
}

template.toSetShare = function (id) {


    $.post('template/teacherGrid.html', {}, function (view) {
        main.showDialog({
            title: '选择分享教师',
            content: view,
            submit: function () {
                main.closeDialog();
            }
        });

        $('#template-id').val(id);

        template.teacherHandleForShare();

    });

}

template.teacherHandleForShare = function () {
    $('#teacherGrid tr td:nth-child(6)').remove();
    $('#teacherGrid tr th:nth-child(6)').remove();

    $('#teacherGrid tbody tr').dblclick(function () {
        var teacherId = $('input', $(this)).val();
        var templateId = $('#template-id').val();

        var tname = $('td:eq(2)', $(this)).text().trim();

        if (!confirm('是否确认将模板分享给【' + tname + '】教师')) {
            return;
        }

        $.post('template/shareTemplate', {templateId: templateId, teacherId: teacherId}, function (flag) {
            if (flag == true) {
                alert('分享成功');

                main.closeDialog();

                template.refresh();
            } else {
                alert('不能分享给自己')
                return;
            }
        });

    });
}

template.toSetLeave = function (id) {
    if (!confirm('是否确认设置模板为丢弃状态')) {
        return;
    }
    $.post('template/changeLeave', {id: id}, function () {
        alert('设置成功');
        template.refresh();
    });
}

template.toEdit = function (id, tname, currName) {
    if (tname != currName) {
        //不是自己的模板，不能改
        alert('只能编辑修改自己的模板');
        return;
    }
    location.href = 'template/edit.html?id=' + id;
}

