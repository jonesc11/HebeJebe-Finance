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
var passport = require ('passport');
var strategy = require ('passport-local').Strategy;
var validator = require ('validator');

var keysObject = require ('./keys.json');
var accessKey = keysObject.AccessKey;
var secretKey = keysObject.SecretKey;

app.listen (80);

app.use (express.static (__dirname + '/public'));
app.use (bodyParser.json());
app.use (bodyParser.urlencoded({ extended: true });
app.use (cookieParser());

app.get ('/', function (req, res) {
  res.sendFile (__dirname + '/pages/home.html');
});

app.get ('/login', function (req, res) {
  res.sendFile (__dirname + '/pages/login.html');
});

app.post ('/login', function (req, res) {

});

app.get ('/signup', function (req, res) {
  res.sendFile (__dirname + '/pages/signup.html');
});

app.post ('/signup', function (req, res) {
  var firstName = req.body.firstName;
  var lastName = req.body.lastName;
  var email = req.body.email;
  var pass = req.body.pass;

  var accountName = req.body.accountName;
  var accountBalance = req.body.accountAmount;
  var accountType = req.body.accountType;

  var data = {
    Key: accessKey,
    Secret: secretKey,
    ActionType: "CreateUser",
    Action: {
      UserIdentifier: email,
      Password: pass,
      FirstName: firstName,
      LastName: lastName
    }
  };

  sendMessage (JSON.stringify (data)).then (function (returnData) {
    var resourceId = returnData.ResourceIdentifier;

    var data2 = {
      Key: accessKey,
      Secret: secretKey,
      AccountId: resourceId,
      ActionType: "CreateAccount",
      Action: {
        UserResourceIdentifier: resourceId,
        AccountName: accountName,
        AccountBalance: accountBalance,
        AccountType: accountType
      }
    };

    sendMessage (JSON.stringify (data)).then (function (data) {
      res.redirect ('/login');
    });
  });
});

app.get ('/logout', function (req, res) {

});

app.get ('/request/:reqType/:resType', function (req, res) {
  var resourceType = req.params.resType;
  var requestType = req.params.reqType;
  var userRID = "u0";//req.cookies.accountRI;
  var data = {};

  if (requestType === 'get' && resourceType === 'transactions') {
    data = {
      Key: accessKey,
      Secret: secretKey,
      AccountId: userRID,
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
      AccountId: userRID,
      ActionType: "GetAccounts",
      Action: {
        Limit: req.query.Limit ? req.query.Limit : 25,
        ResourceIdentifier: req.query.ResourceIdentifier ? req.query.ResourceIdentifier : null,
        GetFrom: req.query.GetFrom ? req.query.GetFrom : userRID,
        NextToken: req.query.NextToken ? req.query.NextToken : null
      }
    };
  }

  sendMessage (JSON.stringify (data)).then(function (data) { res.send (data); });
});

app.post ('/request/:reqType/:resType', function (req, res) {
  var resourceType = req.params.resType;
  var requestType = req.params.reqType;
  var userRID = "u0";//req.cookies.accountRI;
  var data = {};
  
  if (requestType === 'create' && resourceType === 'account') {
    data = {
      Key: accessKey,
      Secret: secretKey,
      AccountId: userRID,
      ActionType: "CreateAccount",
      Action: {
        UserResourceIdentifier: req.body.UserResourceIdentifier ? req.body.UserResourceIdentifier : userRID,
        AccountName: req.body.AccountName ? req.body.AccountName : null,
        AccountBalance: req.body.AccountBalance ? req.body.AccountBalance : null,
        AccountType: req.body.AccountType ? req.body.AccountType : null
      }
    };
  } else if (requestType === 'create' && resourceType === 'transaction') {
    data = {
      Key: accessKey,
      Secret: secretKey,
      AccountId: userRID,
      ActionType: "CreateTransaction",
      Action: {
        TransactionType: req.body.TransactionType ? req.body.TransactionType : null,
        Amount: req.body.Amount ? req.body.Amount : 0,
        To: req.body.To ? req.body.To : null,
        From: req.body.From ? req.body.From : null,
        Description: req.body.Description ? req.body.Description : null,
        DateTime: req.body.DateTime ? req.body.DateTime : null,
        Category: req.body.Category ? req.body.Category : null,
        AssociatedWith: req.body.AssociatedWith ? req.body.AssociatedWith : null,
        Recurring: req.body.Recurring ? req.body.Recurring : false,
        RecurringUntil: req.body.RecurringUntil ? req.body.RecurringUntil : null,
        RecurringFrequency: req.body.RecurringFrequency ? req.body.RecurringFrequency : null
      }
    };
  }

  sendMessage (JSON.stringify (data)).then(function (data) { res.send (data); });
});

/* Sends a message to the Java API and returns the result.
    If there was a failure, null is returned */
function sendMessage (message) {
  message += '\n';
  return new Promise (function (resolve, reject) {
    var socketClient = new net.Socket();

    socketClient.connect (9235, '127.0.0.1', function () {
      socketClient.write (message);
    });

    socketClient.on('data', function (data) {
      resolve(data.toString());
      socketClient.destroy();
    });

    socketClient.on('error', function (data) {
      reject(data);
    });
  });
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
