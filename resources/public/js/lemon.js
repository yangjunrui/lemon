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
        }
    });
}
$(function () {
    //$('#mytab a:first').tab('show');
    $('#engines div:first').addClass('active');
    $("table.tableWithFloatingHeader").each(function() {
        $(this).wrap("<div class=\"divTableWithFloatingHeader\" style=\"position:relative\"></div>");

        var originalHeaderRow = $("tr:first", this)
        originalHeaderRow.before(originalHeaderRow.clone());
    var clonedHeaderRow = $("tr:first", this)

        clonedHeaderRow.addClass("tableFloatingHeader");
    clonedHeaderRow.css("position", "absolute");
    clonedHeaderRow.css("top", "45px");
    clonedHeaderRow.css("left", $(this).css("margin-left"));
    clonedHeaderRow.css("visibility", "hidden");

    originalHeaderRow.addClass("tableFloatingHeaderOriginal");
    });
    UpdateTableHeaders();
    $(window).scroll(UpdateTableHeaders);
    $(window).resize(UpdateTableHeaders);
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
    $("#area-statistics").tablesorter();
    $("#ds-statistics").tablesorter();
    setInterval(function () { if (!$("#area-filter").val()) {$("#area-filter").val(0); return ;}$("#area-filter").val(parseInt($("#area-filter").val()) + 1);}, 1000);
})
