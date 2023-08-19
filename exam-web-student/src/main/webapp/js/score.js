var score = {} ;

score.changeYear = function(){
    var year = $('#search-yearFlag').val();
    $.post('score/scoreGrid.html',{year:year},function(view){
        $('#scoreGrid').replaceWith(view);
    });
}