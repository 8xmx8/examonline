var page = {}

page.toStudentList = function(className){
    var param = {
        className:className,
        examId:$('#exam-id').val()
    }

    $.post('exam/studentPageList.html',param,function(view){
        $('#studentGrid').replaceWith(view);

        if(submitStatus == '提交'){
            page.setExamSubmitStatus();
        }
    });
}

page.changeStudentStatus = function(select,studentId){
    var param = {
        status : select.value,
        studentId : studentId,
        examId : $('#exam-id').val()
    }
    $.post('exam/changeStudentStatus',param,function(){
        alert('状态设置成功') ;
    });
}

page.toReview = function(studentId,sname){
    //'exam/examId/studentId/page.html'
    //'exam/8/347/page.html'
    var url = 'exam/page.html?sname='+sname+'&studentId='+studentId+'&examId='+$('#exam-id').val() ;
    window.open(url);
}

page.review = function(){
    //拿到所有的分数和批阅信息（隐藏），拼串，传递，更新。同时更新分数
    var separator = '}-|-{' ;
    var review4 = '';
    var review5 = '' ;

    $('.score-box-4').each(function(i,input){
        var endScore = input.value;
        var review = $('.review-hidden-box-4').eq(i).html();
        review4 += endScore +','+review + separator;
    });


    $('.score-box-5').each(function(i,input){
        var endScore = input.value;
        var review = $('.review-hidden-box-5').eq(i).html();
        review5 += endScore +','+review + separator;
    });

    var totalScore = 0 ;
    $('.score-box').each(function(i,input){
        var score = input.value ;
        //score = score&score!=''?score:0;
        score = parseInt(score) ;
        totalScore += score ;
    })

    $('#static-form-totalScore').html(totalScore);

    var param = {
        review4:review4,
        review5:review5,
        score:totalScore,
        examId:$('#page-exam-id').val(),
        studentId:$('#page-student-id').val()
    }

    //console.log(param);
    $.post('exam/review',param,function(){
        console.log('批阅已保存,可以右下角滑框处理');
    });

}

page.toStartReview = function(no){
    var defaultText = $('.left-part:eq('+no+') .left-options li:last .review-hidden-box').html();

    main.showDefaultDialog({
        title:'第'+(no+1)+'题批阅信息填写',
        content:'<div id="review-editor"></div>',
        submit:function(){
            //将文本输入框内容获得并写入隐藏区域（review-hidden-box）
            var newText = e.txt.html();
            $('.left-part:eq('+no+') .left-options li:last .review-hidden-box').html(newText);

            main.closeDialog();

            page.review() ;
        }
    });

    if(submitStatus == '提交'){
        $('#review-editor').html(defaultText);
        $('#common-modal-submit').remove();
        return ;
    }

    var e = new E('#review-editor');
    editorDefaultInit(e);
    e.create();
    e.txt.html(defaultText);

}

page.toSubmit = function(){
    if(!confirm('提交后不能再修改，是否确认提交？')){
        return ;
    }

    $.post('exam/submit',{examId:$('#exam-id').val()},function(){
        alert('提交成功') ;

        //需要处理提交后的批阅信息状态
        page.setExamSubmitStatus();
    });
}

page.setExamSubmitStatus = function(){
    // 取消提交按钮
    $('#exam-submit-btn').remove();
    // 固定学生考试状态，下拉框设置不可用
    $('.page-list-part-2 select').prop('disabled',true);
}


page.setPageSubmitStatus = function(){
    $('.score-box').prop('disabled',true);
}