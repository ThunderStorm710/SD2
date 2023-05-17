var stompClient = null;

function connect() {
    var socket = new SockJS("/my-websocket");
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log("Connected: " + frame);
        stompClient.subscribe("/topic/dados", function (message) {
            console.log(message.body);
            atualizarDados(JSON.parse(message.body));
        });
    });
}

function atualizarDados(dados) {
    var table1 = document.getElementById("tabela1");
    var table2 = document.getElementById("tabela2");
    var table3 = document.getElementById("tabela3");

    table1.innerHTML = ""
    table2.innerHTML = ""
    table3.innerHTML = ""
    var row, i;
    let barrels = dados.barrels;
    for (i = 0; i < barrels.length; i++) {
        row = `<tr>
							<td>${barrels[i].ip}</td>
							<td>${barrels[i].porto}</td>
							<td>${barrels[i].gama}</td>
					  </tr>`
        table1.innerHTML += row
    }

    let downloaders = dados.downloaders;
    for (i = 0; i < downloaders.length; i++) {
        row = `<tr>
							<td>${downloaders[i].ip}</td>
							<td>${downloaders[i].porto}</td>
							<td>${downloaders[i].id}</td>
					  </tr>`
        table2.innerHTML += row
    }
    let mapa = dados.mapaPesquisas;
    let keys = Object.keys(mapa);
    for (i = 0; i < keys.length; i++) {
        row = `<tr>
							<td>${keys[i]}</td>
							<td>${mapa[keys[i]]}</td>
					  </tr>`
        table3.innerHTML += row
    }
}