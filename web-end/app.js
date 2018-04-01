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
  var userRID = "u0";//req.cookies.accountRI;
  var data = {};

  if (requestType === 'get' && resourceType === 'transactions')
    data = handleGetTransactions (req, userRID);
  else if (requestType === 'get' && resourceType === 'accounts')
    data = handleGetAccounts (req, userRID);
  else if (requestType === 'get' && resourceType === 'subbalance')
    data = handleGetSubbalance (req, userRID);

  sendMessage (JSON.stringify (data)).then(function (data) { res.send (data); res.status (data.ErrorMessage ? 400 : 200); });
});

app.post ('/request/:reqType/:resType', function (req, res) {
  var resourceType = req.params.resType;
  var requestType = req.params.reqType;
  var userRID = "u0";//req.cookies.accountRI;
  var data = {};
  
  if (requestType === 'create' && resourceType === 'account') {
    data = handleCreateAccount (req, userRID);
  } else if (requestType === 'create' && resourceType === 'transaction') {
    data = handleCreateTransaction (req, userRID);
  }

  sendMessage (JSON.stringify (data)).then(function (data) { res.send (data); res.status (data.ErrorMessage ? 400 : 200); });
});

/* Handles GetTransaction requests. Given input from AngularJS, convert it into valid input
    for the Java server. */
function handleGetTransactions (req, userRID) {
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

  if (data.Action.ResourceIdentifier !== null)
    data.Action.GetFrom = null;

  return data;
}

/* Handles GetAccounts requests. Given input from AngularJS, convert it into valid input
    for the Java server. */
function handleGetAccounts (req, userRID) {
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

  if (data.Action.ResourceIdentifier !== null)
    data.Action.GetFrom = null;

  return data;
}

/* Handles GetSubbalance requests. Given input from AngularJS, convert it into valid input
    for the Java server. */
function handleGetSubbalance (req, userRID) {
  data = {
    Key: accessKey,
    Secret: secretKey,
    AccountId: userRID,
    ActionType: "GetSubbalance"
    Action: {
      Limit: req.query.Limit ? req.query.Limit : 25,
      ResourceIdentifier: req.query.ResourceIdentifier ? req.query.ResourceIdentifier ? null,
      GetFrom: req.query.GetFrom ? req.query.GetFrom : userRID,
      NextToken: req.query.NextToken ? req.query.NextToken : null
    }
  };

  if (data.Action.ResourceIdentifier !== null)
    data.Action.GetFrom = null;

  return data;
}

/* Handles CreateAccount requests. Given input from AngularJS, convert it into valid input
    for the Java server. */
function handleCreateAccount (req, userRID) {
  if (!req.body.AccountName || req.body.AccountName === null)
    return { ErrorMessage: "Account name must be specified." };
  if (!req.body.AccountType || req.body.AccountType === null)
    return { ErrorMessage: "Account type must be specified." };

  data = {
    Key: accessKey,
    Secret: secretKey,
    AccountId: userRID,
    ActionType: "CreateAccount",
    Action: {
      UserResourceIdentifier: req.body.UserResourceIdentifier ? req.body.UserResourceIdentifier : userRID,
      AccountName: req.body.AccountName ? req.body.AccountName : null,
      AccountBalance: req.body.AccountBalance ? req.body.AccountBalance : 0,
      AccountType: req.body.AccountType ? req.body.AccountType : null
    }
  };

  return data;
}

/* Handles CreateTransaction requests. Given input from AngularJS, convert it into valid input
    for the Java server. */
function handleCreateTransaction (req, userRID) {
  if (!req.body.Amount || req.body.Amount === "" || req.body.Amount == 0)
    return { ErrorMessage: "Amount not specified." };
  if ((!req.body.To || req.body.To === null) && (!req.body.From || req.body.From === null)
    return { ErrorMessage: "Account(s) To and / or From must be specified." };
  if (!req.body.DateTime || req.body.DateTime === null)
    return { ErrorMessage: "Transaction date must be specified." };
  if (!req.body.AssociatedWith || req.body.AssociatedWith === null)
    return { ErrorMessage: "Transaction must be associated with some resource." };
  if (req.body.Recurring === true && (!req.body.RecurringFrequency || req.body.RecurringFrequency === null))
    return { ErrorMessage: "Recurring transactions must have specified Recurring Frequency periods." };

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

  return data;
}

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
