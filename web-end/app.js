//- Require and include all variables
var express = require ('express');
var app = express ();
var path = require ('path');
var bodyParser = require ('body-parser');
var server = require ('http').Server (app);
var io = require ('socket.io')(server);
var fs = require ('fs');

var app = express ();

var connections = [];

server.listen (80);

app.use (express.static (__dirname + 'public'));
app.use (bodyParser.json());

app.use ('/', function (req, res) {
  res.sendFile (__dirname + 'pages/index.html');
});

io.on ('connection', function (socket) {
  connections.push (socket);
  
  socket.on ('disconnect', function () {
    connections.splice (socket, 1);
  });
  
  //- Handle the requests, send the data to the server, and return the result to the client
  socket.on ('request', function (data) {
    //- Create the request to the server
    
    
    //- Return the result of the previous request to the user
    socket.emit({});
  });
});