var stompClient = null;

function setConnected(connected) {
  $("#connect").prop("disabled", connected);
  $("#disconnect").prop("disabled", !connected);
  if (connected) {
    $("#conversation").show();
  } else {
    $("#conversation").hide();
  }
  $("#messages").html("");
}

function connect() {
  var socket = new SockJS("/my-websocket");
  stompClient = Stomp.over(socket);
  /*
  stompClient.connect({}, function (frame) {
    setConnected(true);
    console.log("Connected: " + frame);

    stompClient.subscribe("/topic/messages", function (message) {
      showMessage(
        JSON.parse(message.body).content,
        JSON.parse(message.body).username
      );
    });
  });
  */
  stompClient.connect({}, function (frame) {
      console.log("Connected: " + frame);
      setConnected(true);
      stompClient.subscribe("/topic/infoUpdates", function (message) {
        var data = JSON.parse(message.body);
        // Atualize os dados na página com base na mensagem recebida
        updateInfo(data);
      });
    });
    function updateInfo(data) {
        // Atualize os elementos HTML com as informações recebidas
        $("#d").text(data.d);
        $("#b").text(data.b);
        $("#m").text(data.m);
        $("#downloaders").text(data.downloaders);
        $("#barrels").text(data.barrels);
        $("#mapa").text(data.mapa);
      }

}
/*
$(function () {
  var socket = new SockJS("/my-websocket");
  var stompClient = Stomp.over(socket);

  stompClient.connect({}, function (frame) {
    console.log("Connected: " + frame);

    stompClient.subscribe("/topic/infoUpdates", function (message) {
      var data = JSON.parse(message.body);
      // Atualize os dados na página com base na mensagem recebida
      updateInfo(data);
    });
  });

  function updateInfo(data) {
    // Atualize os elementos HTML com as informações recebidas
    $("#d").text(data.d);
    $("#b").text(data.b);
    $("#m").text(data.m);

    // Atualize outros elementos HTML com base nas informações recebidas
    // ...

    // Atualize a tabela de downloaders
    var downloadersTable = $("#downloadersTable");
    downloadersTable.empty();
    for (var i = 0; i < data.downloaders.length; i++) {
      var downloader = data.downloaders[i];
      var row = "<tr><td>" + downloader.name + "</td><td>" + downloader.status + "</td></tr>";
      downloadersTable.append(row);
    }

    // Atualize a tabela de barrels
    var barrelsTable = $("#barrelsTable");
    barrelsTable.empty();
    for (var i = 0; i < data.barrels.length; i++) {
      var barrel = data.barrels[i];
      var row = "<tr><td>" + barrel.name + "</td><td>" + barrel.capacity + "</td></tr>";
      barrelsTable.append(row);
    }

    // Atualize outros elementos HTML com base nas informações recebidas
    // ...
  }
});
*/

function disconnect() {
  if (stompClient !== null) {
    stompClient.disconnect();
  }
  setConnected(false);
  console.log("Disconnected");
}

function sendMessage() {
  stompClient.send(
    "/app/message",
    {},
    JSON.stringify({
      content: $("#message").val(),
      username: $("#username").val(),
    })
  );
}

function showMessage(message, username) {
  $("#messages").append(
    "<tr><td>" + username + " -- " + message + "</td></tr>"
  );
}

$(function () {
  $("form").on("submit", function (e) {
    e.preventDefault();
  });
  /*
  $("#connect").click(function () {
    connect();
  });*/
  $(window.addEventListener("load", function() {
    connect();
  }
  });
  $("#disconnect").click(function () {
    disconnect();
  });
  $("#send").click(function () {
    sendMessage();

  });
});
