// Create a connection to http://localhost:9999/echo
var sock = new SockJS('http://localhost:8080/socket');
//var sock = new SockJS('http://localhost:8080/hackaton/socket');
//var sock = new SockJS('http://192.168.200.60:8169/common-test-app/api/v1/stream');
//var sock = new SockJS('http://10.1.253.60:8171/common-test-app/api/v1/stream');

// Open the connection
sock.onopen = function() {
	console.log(sock);
  console.log('open');
};

// On connection close
sock.onclose = function() {
  console.log('close');
};

// On receive message from server
sock.onmessage = function(e) {
  // Get the content
  var content = JSON.parse(e.data);

  // Append the text to text area (using jQuery)
  $('#chat-content').val(function(i, text){
    return text + JSON.stringify(e);
  });
  
};

// Function for sending the message to server
function sendMessage(){
  // Get the content from the textbox
  var command = $('#command').val();
  var botUserName = $('#botUserName').val();
  var chatId = $('#chatId').val();
  var message = $('#message').val();//[$('#password').val()];

  // The object to send
  var send = {
    command: command,
    botUserName: botUserName,
    chatId: chatId,
	message: message
  };

  // Send it now
  console.log(send);
  sock.send(JSON.stringify(send));
}

// Function for sending the message to server
function sendMessageSubscribe(){
  // Get the content from the textbox
  var operation = $('#operationSubscribe').val();
  var data = [$('#security2Subs').val()];//[$('#password').val()];

  // The object to send
  var send = {
    operation: operation,
    data: data
  };

  // Send it now
  sock.send(JSON.stringify(send));
}

// Function for sending the message to server
function sendMessageUnSubscribe(){
  // Get the content from the textbox
  var operation = $('#operationUnSubscribe').val();
  var data = [$('#security2UnSubs').val()];//[$('#password').val()];

  // The object to send
  var send = {
    operation: operation,
    data: data
  };

  // Send it now
  sock.send(JSON.stringify(send));
}