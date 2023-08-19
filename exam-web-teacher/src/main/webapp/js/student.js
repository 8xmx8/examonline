var student = {}

student.toImports = function(){
    //先访问获得对话框中内容，打开对话框
    $.post('student/importsTemplate.html',{},function(view){
        var uploading = false ;
        main.showDialog({
            title:'导入学生信息',
            content:view,
            submit:function(){
                var fileInfo = $('#import-excel').val();
                if(!fileInfo){
                    alert('请选择要上传的excel文件')
                    return ;
                }
                if(uploading){
                    alert('正在上传，请耐心等待...') ;
                    return ;
                }
                $.ajax({
                    url:'student/imports',
                    type:'post',
                    data:new FormData($('#student-import-form')[0]),
                    cache:false,
                    processData: false,
                    contentType: false,
                    beforeSend:function(){
                        uploading = true ;
                    },
                    success:function(msg){
                        uploading = false ;

                        msg = msg.replace(/\|/g,'\r\n') ;
                        alert(msg) ;

                        main.closeDialog() ;

                        //刷新班级表格 （清空之前的数据，查询展示新的数据）
                    },
                    error:function(){
                        uploading = false ;
                    }
                }) ;
            }
        });

    })
}


student.chanageFile = function(){
    //此时选择了新文件
    var fileInfo = $('#import-excel').val();
    if(fileInfo.endsWith(".xls") || fileInfo.endsWith('.xlsx')){
        $('#fileMsg').html(fileInfo);
        return ;
    }
    alert('请选择excel文件');
    $('#import-excel').val('');
}

/**
 * 点击查询按钮，默认从第1页查，没有pageNo参数
 * 点击分页按钮，从指定页查，有pageNo
 *      分页查询本身也需要携带过滤条件
 * @param pageNo
 */
student.toClassQuery = function(pageNo){
    pageNo = pageNo?pageNo:1 ;

    var grade = $('#search-grade').val() ;
    var major = $('#search-major').val()
    var classNo = $('#search-classNo').val() ;

    if(!grade&!major&!classNo){
        alert('请输入查询条件');
        return ;
    }

    var param = {
        pageNo : pageNo ,
        grade:grade,
        major:major,
        classNo:classNo
    }

    $.post("student/classesTemplate.html",param,function(view){
        $('#classesTemplate').replaceWith(view);

        //多模块逻辑处理
        try{
            exam.classesHandleForSelectClasses();
            console.log('exam+student module')
        }catch(e){
            console.log('exam object not defined -- student module');
        }

    });




}

student.toClearClass = function(){
    $('#search-grade').val('');
    $('#search-major').val('');
    $('#search-classNo').val('');

    //重置班级表格+分页栏，回复默认
    $.post('student/classesDefaultTemplate.html',{},function(view){
        $('#classesTemplate').replaceWith(view);
    });
}

student.toPageClassQuery = function(pageNo){
    student.toClassQuery(pageNo)
}

student.toShowClassDetail = function(className){
    //所谓的显示详情，其实就是将className放置在学生搜索框中，然后搜索学生。
    $('#search-className').val(className) ;
    student.toStudentQuery();
}

student.toStudentQuery = function(){
    var className = $('#search-className').val() ;
    var code = $('#search-code').val() ;
    var sname = $('#search-sname').val() ;

    if(!className&!code&!sname){
        alert('请输入学生信息查询条件') ;
        return ;
    }

    var param = {
        className :className,
        code:code,
        sname:sname
    }

    $.post('student/studentsTemplate.html',param,function(view){
        $('#studentGrid').replaceWith(view);

        //多模块逻辑处理
        try{
            exam.studentsHandleForSelectClasses();
        }catch(e){

        }

    });
}

student.toClearStudent = function(){
    $('#search-sname').val('');
    $('#search-code').val('');
    $('#search-className').val('');

    $('#studentGrid tbody').html('') ;
}

student.toAdd = function(className){
    //className 可能有，可能没有
    $.post('student/formTemplate.html',{},function(view){
        main.showDialog({
            title:'新建学生信息',
            content:view,
            submit:function(){
                var param = {
                    code: $('#form-code').val(),
                    sname: $('#form-sname').val(),
                    grade: $('#form-grade').val(),
                    major: $('#form-major').val(),
                    classNo:$('#form-classNo').val()
                }

                $.post('student/save',param,function(f){
                    if(f == true){
                        alert('保存成功') ;
                        main.closeDialog();

                        //刷新班级表格信息（人数会变化）
                        if($('#search-grade').val()|$('#search-major').val()|$('#search-classNo').val()){
                            student.toClassQuery()
                        }

                        //刷新学生表格信息（原数据清空，只显示当前保存的信息）

                    }else{
                        alert('学号或名称重复，请检查') ;
                    }
                });
            }
        });

        if(className){
            //存在，为指定班级增加学生，班级信息固定，不能输入
            $('#form-grade').prop('readonly',true) ;
            $('#form-major').prop('readonly',true) ;
            $('#form-classNo').prop('readonly',true) ;

            var info = className.split('-') ;
            $('#form-grade').val(info[0]);
            $('#form-major').val(info[1]);
            $('#form-classNo').val(info[2]);
        }

    });
}

student.toEdit = function(id){
    $.post('student/formTemplate.html',{id:id},function(view){
        main.showDialog({
            title:'编辑学生信息',
            content:view,
            submit:function(){
                var param = {
                    id:$('#form-id').val(),
                    code: $('#form-code').val(),
                    sname: $('#form-sname').val(),
                    grade: $('#form-grade').val(),
                    major: $('#form-major').val(),
                    classNo:$('#form-classNo').val()
                }

                $.post('student/update',param,function(f){
                    if(f == true){
                        alert('修改成功') ;
                        main.closeDialog();

                        //刷新班级表格信息（人数会变化）
                        if($('#search-grade')|$('#search-major')|$('#search-classNo')){
                            student.toClassQuery()
                        }

                        //刷新学生表格信息
                        student.toStudentQuery() ;

                    }else{
                        alert('学号或名称重复，请检查') ;
                    }
                });
            }
        })
    });
}

student.classCheckAll = function(){
    var f = $('#classGrid thead :checkbox').prop('checked') ;
    $('#classGrid tbody :checkbox').prop('checked',f) ;
}

student.deleteClass = function(className){
    if(!confirm('是否确认删除')){
        return ;
    }

    $.post('student/deleteClass',{className:className},function(){
        alert('删除成功') ;
        var pageNo = $('#class-pagination .active').text().trim();
        student.toClassQuery(pageNo);
    });
}

student.deleteClasses = function(){
    if($('#classGrid tbody :checked').length == 0){
        alert('请选择要删除的记录');
        return ;
    }

    if(!confirm("是否确认删除选中的记录")){
        return ;
    }

    var classNames = '' ;
    $('#classGrid tbody :checked').each(function(i,element){
        classNames += element.value + "," ;
    });

    $.post('student/deleteClasses',{classNames:classNames},function(){
        alert('删除成功') ;
        var pageNo = $('#class-pagination .active').text().trim();
        student.toClassQuery(pageNo);
    });
}

student.studentCheckAll = function(){
    var f = $('#studentGrid thead :checkbox').prop('checked') ;
    $('#studentGrid tbody :checkbox').prop('checked',f) ;
}

student.deleteStudent = function(id){
    if(!confirm('是否确认删除')){
        return ;
    }

    $.post('student/deleteStudent',{id:id},function(){
        alert('删除成功') ;

        student.toStudentQuery() ;

        //刷新班级表格信息（人数会变化）
        if($('#search-grade').val()|$('#search-major').val()|$('#search-classNo').val()){
            student.toClassQuery()
        }
    });
}

student.deleteStudents = function(){
    if($('#studentGrid tbody :checked').length == 0){
        alert('请选择要删除的记录');
        return ;
    }

    if(!confirm('是否确认删除选中的记录')){
        return ;
    }

    var ids = '' ;
    $('#studentGrid tbody :checked').each(function(i,element){
        ids += element.value + ',' ;
    });
    //后端准备将ids直接拼入sql，所以需要完整的字符串，最后多余的逗号需要去掉
    ids = ids.substring(0,ids.length-1) ;

    $.post('student/deleteStudents',{ids:ids},function(){
        alert('删除成功') ;

        student.toStudentQuery() ;

        //刷新班级表格信息（人数会变化）
        if($('#search-grade').val()|$('#search-major').val()|$('#search-classNo').val()){
            student.toClassQuery()
        }
    });
}

student.toExportClasses = function(){
    var grade = '' ;
    var major = '' ;
    var classNo = '' ;

    var classNames = '' ;
    $('#classGrid tbody :checked').each(function(i,element){
       classNames += element.value +',' ;
    });


    if(classNames == ''){
        //没有勾选要导出班级记录
        //也不算错，因为还可以根据过滤条件查询导出
        grade = $('#search-grade').val();
        major = $('#search-major').val();
        classNo = $('#search-classNo').val();
        if(!grade&!major&!classNo){
            //没有勾选，也没有过滤
            alert('请选择要导出的班级信息') ;
            return ;
        }else{
            //有条件
            if(!confirm('将按照条件导出')){
                return ;
            }
        }
    }

    //代码至此，要么classNames有值，存储勾选的记录。要么grade等条件有值
    //此次是一个导出的请求，本质下载，不能使用ajax
    location.href='student/exportClasses?classNames='+classNames+'&grade='+grade+'&major='+major+'&classNo='+classNo ;

}