function search() {
  var Country = document.getElementById("country").value;
  var days = document.getElementById("days").value;
  var daysCount = parseInt(days);

  $.get("http://localhost:8080/list?daysCount=" + daysCount + "&Country=" + Country).then(function (data) {
    document.getElementById("tbl").innerHTML = 
    "<tr>" +
      "<th>Country</th>" +
      "<th>Place</th>" +
      "<th>Magnitude</th>" +
      "<th>Date-Time</th>" +
    "</tr>";
    document.getElementById("zeroEq").innerHTML ="";
    if (data.length > 0) {
      data.map((item) => {
        $(".eqTable").append(`
        <tr>
          <td>${item.country}</td>
          <td>${item.place}</td>
          <td>${item.magnitude}</td>
          <td>${item.dateTime}</td>
        </tr>
      `);
      });
    } else {
      document.getElementById("zeroEq").innerHTML = "No Earthquakes were recorded past " + daysCount + " days";
    }
  });
}
