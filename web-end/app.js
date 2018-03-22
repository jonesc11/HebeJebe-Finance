//- Require and include all variables
var express = require ('express');
var app = express ();
var path = require ('path');
var bodyParser = require ('body-parser');
var server = require ('http').Server (app);
var fs = require ('fs');

app.listen (80);

app.use (express.static (__dirname + '/public'));
app.use (bodyParser.json());

app.get ('/', function (req, res) {
  res.sendFile (__dirname + '/pages/index.html');
});
