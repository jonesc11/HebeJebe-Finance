//- Require and include all variables
var express = require ('express');
var app = express ();
var path = require ('path');
var bodyParser = require ('body-parser');
var server = require ('http').Server (app);
var fs = require ('fs');
var request = require ('request');
var cookieParser = require ('cookie-parser');
var net = require ('net');

var keysObject = require ('./keys.json');
var accessKey = keysObject.AccessKey;
var secretKey = keysObject.SecretKey;

var socketClient = new net.Socket();

socketClient.connect (9235, '127.0.0.1', function () {
  console.log ('Connected to Java server.');
});

socketClient.on ('close', function () {
  console.log ('Connection to Java server closed.');
});

app.listen (80);

app.use (express.static (__dirname + '/public'));
app.use (bodyParser.json());
app.use (cookieParser());

app.get ('/', function (req, res) {
  res.sendFile (__dirname + '/pages/index.html');
});

app.get ('/request/:reqType/:resType', function (req, res) {
  var resourceType = req.params.resType;
  var requestType = req.params.reqType;
  var userRID = req.cookies.accountRI;
  var data = {};

  if (requestType === 'get' && resourceType === 'transactions') {
    data = {
      Key: accessKey,
      Secret: secretKey,
      AccountId: req.cookies.accountRI,
      ActionType: "GetTransactions",
      Action: {
        Limit: req.query.Limit ? req.query.Limit : 25,
        ResourceIdentifier: req.query.ResourceIdentifier ? req.query.ResourceIdentifier : null,
        GetFrom: req.query.GetFrom ? req.query.GetFrom : userRID,
        TransactionType: req.query.TransactionType ? req.query.TransactionType : null,
        Category: req.query.Category ? req.query.Category : null,
        NextToken: req.query.NextToken ? req.query.NextToken : null
      }
    };
  } else if (requestType === 'get' && resourceType === 'accounts') {
    data = {
      Key: accessKey,
      Secret: secretKey,
      AccountId: req.cookies.accountRI,
      ActionType: "GetAccounts",
      Action: {
        Limit: req.query.Limit ? req.query.Limit : 25,
        ResourceIdentifier: req.query.ResourceIdentifier ? req.query.ResourceIdentifier : null,
        GetFrom: req.query.GetFrom ? req.query.GetFrom : userRID,
        NextToken: req.query.NextToken ? req.query.NextToken : null
      }
    };
  }

  var javaResponse = sendMessage (JSON.stringify (data));
  res.send (javaResponse);
});

app.post ('/request/:reqType/:resType', function (req, res) {
  var resourceType = req.params.resType;
  var requestType = req.params.reqType;
  var userRID = req.cookies.accountRI;
  var data = {};

  if (requestType === 'create' && resourceType === 'account') {
    data = {
      Key: accessKey,
      Secret: secretKey,
      AccountId: req.cookies.accountRI,
      ActionType: "CreateAccount",
      Action: {
        UserResourceIdentifier: req.query.UserResourceIdentifier ? req.query.UserResourceIdentifier : userRID,
        AccountName: req.query.AccountName ? req.query.AccountName : null,
        AccountBalance: req.query.AccountBalance ? req.query.AccountBalance : null
      }
    };
  } else if (requestType === 'create' && resourceType === 'transaction') {
    data = {
      Key: accessKey,
      Secret: secretKey,
      AccountId: req.cookies.accountRI,
      ActionType: "CreateTransaction",
      Action: {
        TransactionType: req.query.TransactionType ? req.query.TransactionType : null,
        Amount: req.query.Amount ? req.query.Amount : 0,
        To: req.query.To ? req.query.To : null,
        From: req.query.From ? req.query.From : null,
        Description: req.query.Description ? req.query.Description : null,
        DateTime: req.query.DateTime ? req.query.DateTime : null,
        Category: req.query.Category ? req.query.Category : null,
        AssociatedWith: req.query.AssociatedWith ? req.query.AssociatedWith : null,
        Recurring: req.query.Recurring ? req.query.Recurring : false,
        RecurringUntil: req.query.RecurringUntil ? req.query.RecurringUntil : null,
        RecurringFrequency: req.query.RecurringFrequency ? req.query.RecurringFrequency : null
      }
    };
  }

  var javaResponse = sendMessage (JSON.stringify (data));
  res.send (javaResponse);
});

/* Sends a message to the Java API and returns the result.
    If there was a failure, null is returned */
function sendMessage (message) {
  socketClient.write (message);
  var response = null;

  socketClient.on ('data', function (data) {
    response = JSON.parse(data);
  });

  while (response == null) {}

  return response;
}

function createError (message) {
  return { ErrorMessage: message };
}

function checkNonNullValues (req, arr = []) {
  for (var i = 0; i < arr.length; ++i)
    if (req.query[arr[i]] === undefined)
      return false;
  return true;
}
