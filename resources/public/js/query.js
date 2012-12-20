$(function() {
$('#guru-form').submit(function() {
    $.ajax({
        url: '/monitor/query', 
    data: 'qstr=' + encodeURIComponent($("#qstr").val()) +
        '&eng=' + encodeURIComponent($("#eng-type").val()) +
        '&cluster-name=' + encodeURIComponent($("#cluster-name").val()),
    dataType: 'text',
    success: function(text) {
        $("#guru-result").val(text);
    }
    });
    return false;
});
})
