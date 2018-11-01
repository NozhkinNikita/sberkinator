// Create a connection to http://localhost:9999/echo
var sock = new SockJS('http://192.168.8.104:8090/socket');
var chatIdsForMessaging = [];
var currentName;

// Open the connection
sock.onopen = function () {

    var command = "SUBSCRIBE";
    var vspId = "123";

    console.log("connect");

    var send = {
        command: command,
        vspId: vspId
    }
    sock.send(JSON.stringify(send));
};

sock.onclose = function () {
    console.log('close');
};

// On receive message from server
sock.onmessage = function (e) {

    // Get the content
    var content = JSON.parse(e.data);

    console.log(content);
    switch (content.command) {
        case "CHATS_LIST" : {
            $("#listOfChats").empty();
            for (key in content.activeChats) {
                $("#listOfChats").append(
                    '<li> <a href="#" onclick="showChat(' +
                    content.activeChats[key].chatId + ', \' ' +
                    content.activeChats[key].clientData.clientFirstName +

                    '\' )" >' +
                    content.activeChats[key].clientData.clientFirstName + '</a> </li>'
                );
            }
            break;
        }
        case "GET_CHAT" : {
            $("#messages").empty();
            for (key in content.messages) {
                var sender = content.messages[key].sender === "CLIENT" ? currentName : "BOT";
                $("#messages").append(
                    '<p class="media-body pb-3 mb-0 small lh-125 border-bottom border-gray">' +
                    '<strong class="d-block text-gray-dark">@' + sender + '</strong>' +
                    content.messages[key].message + '</p>'
                );
            }
            break;
        }

        case "SEND" : {
            if (currentChatId === content.chatId) {
                var sender = content.sender === "CLIENT" ? currentName : "BOT";
                $("#messages").append(
                    '<p class="media-body pb-3 mb-0 small lh-125 border-bottom border-gray">' +
                    '<strong class="d-block text-gray-dark">@' + sender + '</strong>' +
                    content.message + '</p>'
                );
            }
            break;
        }
    }
};

function showChat(chatId, name) {
    console.log(chatId)
    currentName = name;
    $("#headerUserName").text(name);
    $("#messages").empty();
    currentChatId = chatId;
    var command = "GET_CHAT";
    var send = {

        command: command,
        chatId: chatId
    }
    sock.send(JSON.stringify(send));
    if (!chatIdsForMessaging.includes(chatId)) {
        $('#textAreaForChat').prop('disabled', true);
        $('#connectToChat').text('Подключиться');
    } else {
        $('#textAreaForChat').prop('disabled', false);
        $('#connectToChat').text('Отправить');
    }
}

function startMessageWithChat() {
    if (chatIdsForMessaging.includes(currentChatId)) {
        var command = "SEND_MESSAGE";
        var send = {
            command: command,
            chatId: currentChatId,
            message: $("#textAreaForChat").val()
        };
        sock.send(JSON.stringify(send));
        $("#messages").append('<p  class="media-body pb-3 mb-0 small lh-125 border-bottom border-gray">' +
            '<strong class="d-block text-gray-dark">Отвественный сотрудник ВСП</strong>' +
            $("#textAreaForChat").val() + '</p>');
        $("#textAreaForChat").val('')
    } else {
        $('#textAreaForChat').prop('disabled', false);
        $('#connectToChat').text('Отправить');
        chatIdsForMessaging.push(currentChatId);
        var command = "KILL_BOT";
        var send = {
            command: command,
            chatId: currentChatId
        };
        sock.send(JSON.stringify(send));
    }
}