var page = {}
page.timer = {};

page.timeHandle = function () {
    var type = $('#time-show').attr('type');
    if (type == 'time') {
        //从当前系统时间开始计时
        page.timer.startTime();
    } else {
        //从考试开始时间来计时
        page.timer.startDuration();
    }
}

page.timer.startTime = function () {
    page.timer.updateTime();
    page.timer.t = setInterval(function () {
        page.timer.updateTime();
    }, 1000);
}

page.timer.updateTime = function () {
    var time = page.timer.getCurrTime();
    //未来还需要判断结束时间，终止考试
    var endTime = $('#exam-endTime').val();
    endTime = endTime.split(" ")[1];
    console.log(time + ',' + endTime);
    if (time >= endTime) {
        page.submit();
        clearInterval(page.timer.t);
    }


    $('#time-show').html(time);
}

page.timer.getCurrTime = function () {
    var date = new Date();
    var year = date.getFullYear();
    var month = date.getMonth() + 1;
    month = month < 10 ? '0' + month : month;
    var day = date.getDate();
    day = day < 10 ? '0' + day : day;
    var hour = date.getHours();
    hour = hour < 10 ? '0' + hour : hour;
    var minute = date.getMinutes();
    minute = minute < 10 ? '0' + minute : minute;
    var second = date.getSeconds();
    second = second < 10 ? '0' + second : second;

    //return year + '-' + month + '-' + day + ' ' + hour + ':' + minute + ':' + second;
    return hour + ':' + minute + ':' + second;
}

page.timer.startDuration = function () {
    //计算出初始时间   当前系统时间 - startTime 差算出分钟和秒
    var start_ms = $('#page-startTime').val();
    start_ms = parseInt(start_ms);
    var curr_ms = new Date().getTime();

    var x = curr_ms - start_ms;
    var m = x / 1000 / 60;
    var s = x / 1000 % 60;
    m = parseInt(m);
    m = m < 10 ? '0' + m : m;
    s = parseInt(s);
    s = s < 10 ? '0' + s : s;
    $('#time-show').html(m + '分' + s + '秒');

    //定时改变时间
    page.timer.t = setInterval(function () {
        var str = $('#time-show').text().trim();
        var i1 = str.indexOf('分');
        var i2 = str.indexOf('秒');
        m = str.substring(0, i1);
        s = str.substring(i1 + 1, i2);
        m = parseInt(m);
        s = parseInt(s);
        s = s + 1;
        if (s == 60) {
            m++;
            s = 0;
        }
        m = m < 10 ? '0' + m : m;
        s = s < 10 ? '0' + s : s;
        //需要判断是否到达最终的时间，结束考试
        var duration = $('#exam-duration').val();
        //console.log(m+","+duration) ;
        if (m >= duration) {
            page.submit();
            clearInterval(page.timer.t);
        }

        $('#time-show').html(m + '分' + s + '秒');

    }, 1000);

}

page.changePrev = function () {
    //每道题都是一个url
    var question_ul = $('.page-question ul.active');
    if (question_ul.prev().length != 0) {
        //切换考题
        question_ul.removeClass('active');
        question_ul.prev().addClass('active');
        //切换题号
        var question_dd = $('.page-part-2 dd.active');
        var no = question_dd.text().trim();
        no = parseInt(no);
        $('.page-part-2 dd').eq(no - 2).addClass('active');
        question_dd.removeClass('active');
        question_dd.addClass('complete');
        //判断上一题是否默认勾选标记
        if ($('.page-part-2 dd').eq(no - 2).hasClass('mark')) {
            $('.markBtn').prop('checked', true);
        } else {
            $('.markBtn').prop('checked', false);
        }


    } else {
        alert('已经是第一个题了')
    }
}

page.changeNext = function () {
    var question_ul = $('.page-question ul.active');
    if (question_ul.next().length != 0) {
        //切换考题
        question_ul.removeClass('active');
        question_ul.next().addClass('active');
        //切换题号
        var question_dd = $('.page-part-2 dd.active');
        var no = question_dd.text().trim();
        no = parseInt(no);
        $('.page-part-2 dd').eq(no).addClass('active');
        question_dd.removeClass('active');
        //同时当前的考题需要标记成已完成
        question_dd.addClass('complete');

        //判断下一题的标记是否勾选
        //判断上一题是否默认勾选标记
        if ($('.page-part-2 dd').eq(no).hasClass('mark')) {
            $('.markBtn').prop('checked', true);
        } else {
            $('.markBtn').prop('checked', false);
        }
    } else {
        alert('已经是最后一题了')
    }
}

page.mark = function (ck) {
    if (ck.checked) {
        $('.page-part-2 dd.active').addClass('mark');
    } else {
        $('.page-part-2 dd.active').removeClass('mark');
    }
}

page.changeIndex = function (dd) {
    $('.page-part-2 dd.active').addClass('complete')
    $('.page-part-2 dd.active').removeClass('active');

    $(dd).addClass('active');

    var no = $(dd).text().trim();
    no = parseInt(no);
    no = no - 1;

    $('.page-question ul.active').removeClass('active');
    $('.page-question ul').eq(no).addClass('active');

    if ($(dd).hasClass('mark')) {
        $('.markBtn').prop('checked', true);
    } else {
        $('.markBtn').prop('checked', false);
    }
}

page.optionHandle = function () {
    $('.page-option').click(function () {
        var span = $(this);
        var ul = span.parent().parent().parent();
        var typeStr = ul.children('li:eq(0)').text();
        if (typeStr.indexOf('单选题') != -1 || typeStr.indexOf('判断题') != -1) {
            //点击的是单选题或判断题选项
            $('.page-option.active', ul).removeClass('active');
            span.addClass('active');

        } else if (typeStr.indexOf('多选题') != -1) {
            //点击多选题选项
            span.toggleClass('active');
        } else {
            //点击填空题或综合题，选项误操作
        }
    });
}

//存储所有综合题答案的编辑器对象
var question5_editors = [];
page.question5Handle = function () {
    $('.question5-box').each(function (i, div) {
        //此时div可能有默认值
        var content = $(div).html();
        $(div).html('');
        var anwser_editor = new E(div);
        editorDefaultInit(anwser_editor);
        anwser_editor.create();
        question5_editors.push(anwser_editor)
        anwser_editor.txt.html(content);
    });
}

var temp_answers = '';
page.startAnwserCacheTimer = function () {
    setInterval(function () {

        var param = page.loadAnswer();

        var answers = param.answer1 + param.answer2 + param.answer3 + param.answer4 + param.answer5;
        if (answers == temp_answers) {
            //没有更改过答案
            console.log('答案没有变化，不需要更新');
            return;
        }
        temp_answers = answers;
        
        $.post('exam/updateAnswer', param, function () {
            console.log('答案信息已存储');
        });

    }, 10000);
}

page.loadAnswer = function () {
    var sp = '}-|-{';
    var answer1 = '';
    var answer2 = '';
    var answer3 = '';
    var answer4 = '';
    var answer5 = '';

    var index = 0;//存储综合题处理下标
    $('.page-question ul').each(function (i, ul) {
        var ul = $(ul);
        var typeStr = ul.children('li:eq(0)').text().trim();
        if (typeStr.indexOf('单选题') != -1) {
            var value = $('.page-option.active', ul).attr('value');
            value = value ? value : 'no';
            answer1 += value + sp;
        } else if (typeStr.indexOf('多选题') != -1) {
            var v = '';
            $('.page-option.active', ul).each(function (i, span) {
                var value = $(span).attr('value');
                v += value + ',';
            });
            console.log("v=" + v);
            v = v == '' ? 'no' : v;
            answer2 += v;
            answer2 += sp;
        } else if (typeStr.indexOf('判断题') != -1) {
            var value = $('.page-option.active', ul).attr('value');
            value = value ? value : 'no';
            answer3 += value + sp;
        } else if (typeStr.indexOf('填空题') != -1) {
            $('.question4-box', ul).each(function (i, input) {
                var value = $(input).val();
                value = value ? value : 'no';
                answer4 += value + ',';
            });
            answer4 += sp;
        } else if (typeStr.indexOf('综合题') != -1) {
            var value = question5_editors[index].txt.html();
            value = value ? value : 'no';
            answer5 += value + sp;
        }
    });

    // console.log(answer1);
    // console.log(answer2);
    // console.log(answer3);
    // console.log(answer4);
    // console.log(answer5);

    //将答案信息交给后台存储
    var param = {
        answer1: answer1,
        answer2: answer2,
        answer3: answer3,
        answer4: answer4,
        answer5: answer5,
        examId: $('#exam-id').val(),
        studentId: $('#student-id').val()
    }
    return param;
}

page.toSubmit = function () {
    if (!confirm('是否确认交卷?')) {
        return;
    }
    page.submit();
}
page.submit = function () {
    var param = page.loadAnswer();
    $.post('exam/submitPage', param, function () {
        alert('考试已完成，准备退出');
        location.href = 'exam/exam.html'
    });
}