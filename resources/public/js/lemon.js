function UpdateTableHeaders() {
    $("div.divTableWithFloatingHeader").each(function() {
        var originalHeaderRow = $(".tableFloatingHeaderOriginal", this);
        var floatingHeaderRow = $(".tableFloatingHeader", this);
        var offset = $(this).offset();
        var scrollTop = $(window).scrollTop();
        if ((scrollTop > offset.top) && (scrollTop < offset.top + $(this).height())) {
            floatingHeaderRow.css("visibility", "visible");
            floatingHeaderRow.css("top", Math.min(scrollTop - offset.top, $(this).height() - floatingHeaderRow.height()) + "px");

            // Copy cell widths from original header
            $("th", floatingHeaderRow).each(function(index) {
                var cellWidth = $("th", originalHeaderRow).eq(index).css('width');
                $(this).css('width', cellWidth);
            });

            // Copy row width from whole table
            floatingHeaderRow.css("width", $(this).css("width"));
        }
        else {
            floatingHeaderRow.css("visibility", "hidden");
            floatingHeaderRow.css("top", "0px");
        }
    });
}
$(function () {
    //$('#mytab a:first').tab('show');
    $('#engines div:first').addClass('active');
    //$("#area-statistics").tablesorter();
    $("#ds-statistics").tablesorter();
    $("table.tableWithFloatingHeader").each(function() {
        $(this).wrap("<div class=\"divTableWithFloatingHeader\" style=\"position:relative\"></div>");

        var originalHeaderRow = $("tr:first", this)
        originalHeaderRow.before(originalHeaderRow.clone());
    var clonedHeaderRow = $("tr:first", this)

        clonedHeaderRow.addClass("tableFloatingHeader");
    clonedHeaderRow.css("position", "absolute");
    clonedHeaderRow.css("top", "0px");
    clonedHeaderRow.css("left", $(this).css("margin-left"));
    clonedHeaderRow.css("visibility", "hidden");

    originalHeaderRow.addClass("tableFloatingHeaderOriginal");
    });
    UpdateTableHeaders();
    $(window).scroll(UpdateTableHeaders);
    $(window).resize(UpdateTableHeaders);
    var m = { "0": { "get": 20, "put": 30},
        "1": { "get" : 10, "put": 40}};
    "http://10.232.36.98:8080/monitor/mdb/mcomm-daily/area-json?start-ns=0&end-ns=1023";
    $.ajax({
        url: 'http://10.232.36.98:8080/monitor/area-json?eng=mdb&cluster-name=mcomm-daily&start-ns=0&end-ns=1023', 
        data: 'start-ns=0&end-ns=1023',
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
            })
            }
            });
})
