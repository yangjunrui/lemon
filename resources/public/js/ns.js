function update_ns () {
  $.ajax({
    url: '/monitor/ns-json',
  data: 'start-ns=0&end-ns=1023' +
    '&eng=' + encodeURIComponent($("#eng-type").val()) +
    '&cluster-name=' + encodeURIComponent($("#cluster-name").val()),
  dataType: 'json',
  success: function(json) {
    var st = $("#area-statistics tbody");
    $("#area-statistics tbody tr").remove();
    $.each(json, function(i, stm) {
      var filter = $("#area-filter");
      if (!filter.val() || filter.val() == i) {
        st.append("<tr>" +
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
      }
    });
  }
  });
}
$(function () {
  update_ns();
  setInterval(update_ns, 10000);
  //$("#area-filter").change(update_ns);
})
