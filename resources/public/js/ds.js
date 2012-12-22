function update_ds() {
    $.ajax({
        url: '/monitor/ds-json',
        data: '&eng=' + encodeURIComponent($("#eng-type").val()) +
                '&cluster-name=' + encodeURIComponent($("#cluster-name").val()),
        dataType: 'json',
        success: function(json) {
            $.each(json, function(i, stm) {
                $("#area-statistics tbody tr:last").after("<tr>" +
                    "<td>" + i + "</td>" +
                    "<td>" + stm["getCount"] + "</td>" +
                    "<td>" + stm["hitCount"] + "</td>" +
                    "<td>" + (stm["getCount"] != 0 ? stm["hitCount"]/stm["getCount"] : 0.0).toFixed(2) + "</td>" +
                    "<td>" + stm["putCount"] + "</td>" +
                    "<td>" + stm["removeCount"] + "</td>" +
                    "<td>" + stm["evictCount"] + "</td>" +
                    "<td>" + stm["itemCount"] + "</td>" +
                    "<td>" + stm["dataSize"] + "</td>" +
                    "<td>" + Math.floor(stm["itemCount"] != 0 ? stm["dataSize"]/stm["itemCount"] : 0.0) + "</td>" +
                    "<td>" + stm["useSize"] + "</td>" +
                    "<td>" + stm["quota"] + "</td>" +
                    "</tr>");
            });
        }
    });
}
$(function () {
})
