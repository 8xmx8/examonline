var exam = {};

exam.changeTimeFlag = function () {
    var timeFlag = $('#search-timeFlag').val();
    $.post('exam/examGrid.html', {timeFlag: timeFlag}, function (view) {
        $('#examGrid').replaceWith(view);
    });
}

exam.enterExam = function (examId) {
    //先检查是否可以进入考试页面
    $.post('exam/checkEnter', {examId: examId}, function (f) {
        if (f == 9) {
            alert('考试已丢弃，请持续关注通知');
            return;
        }
        if (f == 8) {
            alert('已经完成考试');
            return;
        }
        if (f == 7) {
            alert('考试已结束');
            return;
        }

        if (f == 6) {
            alert('考试还未开始');
            return;
        }

        alert('准备开始考试....');

        location.href = 'exam/page.html?examId=' + examId;

    });
}