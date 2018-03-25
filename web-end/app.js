//- Require and include all variables
var express = require ('express');
var app = express ();
var path = require ('path');
var bodyParser = require ('body-parser');
var server = require ('http').Server (app);
var fs = require ('fs');
var request = require ('request');
var cookieParser = require ('cookie-parser');

var keysObject = require ('./keys.json');
var accessKey = keysObject.AccessKey;
var secretKey = keysObject.SecretKey;

app.listen (80);

app.use (express.static (__dirname + '/public'));
app.use (bodyParser.json());
app.use (cookieParser());

app.get ('/', function (req, res) {
  res.sendFile (__dirname + '/pages/index.html');
});

app.get ('/request/:type', function (req, res) {
  var resourceType = req.params.type;

  if (resourceType === 'transactions') {
    var data = {
      Key: accessKey,
      Secret: secretKey,
      AccountId: req.cookies.accountRI,
      ActionType: "GetTransactions",
      Action: {
        Limit: req.params
      }
    };
return;

    var javaResponse = sendMessage (JSON.stringify (data));
    res.send (javaResponse);
  }
});

/* Sends a message to the Java API and returns the result.
    If there was a failure, null is returned */
function sendMessage (message) {
  return null;
}
