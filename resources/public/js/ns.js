$(function () {
    $.ajax({
        url: '/monitor/area-json', 
        data: 'start-ns=0&end-ns=1023',
        data: 'start-ns=0&end-ns=1023' +
                '&eng=' + encodeURIComponent($("#eng-type").val()) +
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
    setInterval(function () { if (!$("#area-filter").val()) {$("#area-filter").val(0); return ;}$("#area-filter").val(parseInt($("#area-filter").val()) + 1);}, 1000);
    $(document).keydown(
        function (ev) {
          if (!ev.ctrlKey && ev.which == 74 && !ev.shiftKey && !$(":focus").val()) {
            $("body").scrollTop($("body").scrollTop() + 30);
          }
          if (!ev.ctrlKey && ev.which == 75 && !ev.shiftKey && !$(":focus").val()) {
            $("body").scrollTop($("body").scrollTop() - 30);
          }
        }
        );
})
