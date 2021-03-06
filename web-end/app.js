//- Require and include all variables
var express = require ('express');
var app = express ();
var path = require ('path');
var bodyParser = require ('body-parser');
var server = require ('http').Server (app);
var fs = require ('fs');
var request = require ('request');
var validator = require ('validator');
var session = require ('express-session');
var net = require ('net');

var keysObject = require ('./keys.json');
var accessKey = keysObject.AccessKey;
var secretKey = keysObject.SecretKey;

app.listen (80);

app.use (express.static (__dirname + '/public'));
app.use (bodyParser.json());
app.use (bodyParser.urlencoded({ extended: true }));
app.use (session({ secret: 'secret', resave: false, saveUninitialized: true }));

app.get ('/', function (req, res) {
  if (!req.session || !req.session.user || !req.session.user.email)
    res.redirect ('/signup');
  else
    res.sendFile (__dirname + '/pages/home.html');
});

app.get ('/login', function (req, res) {
  if (req.session && req.session.user && req.session.user.email)
    res.redirect ('/');
  else
    res.sendFile (__dirname + '/pages/login.html');
});

app.post ('/login', function (req, res) {
  var email = req.body.email;
  var pass = req.body.pass;

  var data = {
    Key: accessKey,
    Secret: secretKey,
    ActionType: "Login",
    Action: {
      UserIdentifier: email,
      Password: pass
    }
  };

  sendMessage(JSON.stringify (data)).then (function (returnData) {
    returnData = JSON.parse(returnData);
    if (returnData.Verified) {
      req.session.user = {};
      req.session.user.email = returnData.UserIdentifier;
      req.session.user.fname = returnData.FirstName;
      req.session.user.lname = returnData.LastName;
      req.session.user.rid = returnData.ResourceIdentifier;

      res.redirect ('/');
    } else {
      res.redirect ('/login');
    }
  });
});

app.get ('/signup', function (req, res) {
  if (req.session && req.session.user && req.session.user.email)
    res.redirect ('/');
  else
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
    var parsed = JSON.parse (returnData);
    var resourceId = parsed.ResourceIdentifier;

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

    sendMessage (JSON.stringify (data2)).then (function (returnData2) {
      res.redirect ('/login');
    });
  });
});

app.get ('/logout', function (req, res) {
  req.session.user = undefined;
  res.redirect ('/login');
});

app.get ('/request/:reqType/:resType', function (req, res) {
  var resourceType = req.params.resType;
  var requestType = req.params.reqType;
  var userRID = req.session.user.rid;
  var data = {};

  if (requestType === 'get' && resourceType === 'transactions')
    data = handleGetTransactions (req, userRID);
  else if (requestType === 'get' && resourceType === 'accounts')
    data = handleGetAccounts (req, userRID);
  else if (requestType === 'get' && resourceType === 'subbalance')
    data = handleGetSubbalance (req, userRID);
  else if (requestType === 'get' && resourceType === 'user')
    data = handleGetUser (req, userRID);
  else if (requestType === 'get' && resourceType === 'budget')
    data = handleGetBudgets (req, userRID);
  else if (requestType === 'get' && resourceType === 'projection')
    data = handleGetProjection (req, userRID);
  else if (requestType === 'get' && resourceType === 'savingsplan')
    data = handleGetSavingsPlan (req, userRID);

  if (data.ErrorMessage) {
    res.send (data);
    res.status (200);
  } else {
    sendMessage (JSON.stringify (data)).then(function (data) { res.send (data); res.status (data.ErrorMessage ? 400 : 200); });
  }
});

app.get ('/user', function (req, res) {res.send (1);});

app.post ('/request/:reqType/:resType', function (req, res) {
  var resourceType = req.params.resType;
  var requestType = req.params.reqType;
  var userRID = req.session.user.rid;
  var data = {};

  if (requestType === 'create' && resourceType === 'account')
    data = handleCreateAccount (req, userRID);
  else if (requestType === 'create' && resourceType === 'transaction')
    data = handleCreateTransaction (req, userRID);
  else if (requestType === 'create' && resourceType === 'user')
    data = handleCreateUser (req, userRID);
  else if (requestType === 'create' && resourceType === 'subbalance')
    data = handleCreateSubbalance (req, userRID);
  else if (requestType === 'create' && resourceType === 'budget')
    data = handleCreateBudget (req, userRID);
  else if (requestType === 'create' && resourceType === 'savingsplan')
    data = handleCreateSavingsPlan (req, userRID);
  else if (requestType === 'addto' && resourceType === 'savingsplan')
    data = handleAddToSavingsPlan (req, userRID);

  if (data.ErrorMessage) {
    res.send (data);
    res.status (200);
  } else {
    sendMessage (JSON.stringify (data)).then(function (data) { res.send (data); res.status (data.ErrorMessage ? 400 : 200); });
  }
});

app.post ('/request/modify', function (req, res) {
  var userRID = req.session.user.rid;
  var data = handleModifyResource (req, userRID);

  if (data.ErrorMessage) {
    res.send (data);
    res.status (200);
  } else {
    sendMessage (JSON.stringify (data)).then (function (data) { res.send (data); res.status (data.ErrorMessage ? 400 : 200); });
  }
});

app.post ('/request/delete', function (req, res) {
  var userRID = req.session.user.rid;
  data = handleDeleteResource (req, userRID);

  if (data.ErrorMessage) {
    res.send (data);
    res.status (200);
  } else {
    sendMessage (JSON.stringify (data)).then (function (data) { res.send (data); res.status (data.ErrorMessage ? 400 : 200)});
  }
});

/* Handles GetProjection requests. Given input from AngularJS, convert it into valid input
    for the Java server. */
function handleGetProjection (req, userRID) {
  if (!req.query.ProjectionDate || req.query.ProjectionDate === null)
    return { ErrorMessage: "ProjectionDate must be defined." };
  
  var data = {
    Key: accessKey,
    Secret: secretKey,
    AccountId: userRID,
    ActionType: "GetProjection",
    Action: {
      ProjectionDate: req.query.ProjectionDate,
      GetFrom: req.query.GetFrom ? req.query.GetFrom : userRID
    }
  };

  return data;
}

/* Handles AddToSavingsPlan requests. Given input from AngularJS, convert it into valid input
    for the Java server. */
function handleAddToSavingsPlan (req, userRID) {
  if (!req.body.AccountResourceIdentifier || req.body.AccountResourceIdentifier === null)
    return { ErrorMessage: "AccountResourceIdentifier must be specified." };
  if (!req.body.Amount || req.body.Amount === null)
    return { ErrorMessage: "Amount must be specified." };

  var data = {
    Key: accessKey,
    Secret: secretKey,
    AccountId: userRID,
    ActionType: 'AddToSavingsPlan',
    Action: {
      UserResourceIdentifier: req.body.UserResourceIdentifier ? req.body.UserResourceIdentifier : userRID,
      AccountResourceIdentifier: req.body.AccountResourceIdentifier,
      Amount: req.body.Amount
    }
  };

  return data;
}

/* Handles GetSavingsPlan requests. Given input from AngularJS, convert it into valid input
    for the Java server. */
function handleGetSavingsPlan (req, userRID) {
  var data = {
    Key: accessKey,
    Secret: secretKey,
    AccountId: userRID,
    ActionType: 'GetSavingsPlan',
    Action: {
      GetFrom: req.query.GetFrom ? req.query.GetFrom : userRID
    }
  };

  return data;
}

/* Handles CreateSavingsPlan requests. Given input from AngularJS, convert it into valid input
    for the Java server. */
function handleCreateSavingsPlan (req, userRID) {
  if (!req.body.SavingsPlanName || req.body.SavingsPlanName === null)
    return { ErrorMessage: "SavingsPlanName must be defined." };
  if (!req.body.SavingsPlanAmount || req.body.SavingsPlanAmount === null)
    return { ErrorMessage: "SavingsPlanAmount must be defined." };
  if (!req.body.SavingsPlanDate || req.body.SavingsPlanDate === null)
    return { ErrorMessage: "SavingsPlanDate must be defined." };

  var data = {
    Key: accessKey,
    Secret: secretKey,
    AccountId: userRID,
    ActionType: 'CreateSavingsPlan',
    Action: {
      SavingsPlanName: req.body.SavingsPlanName,
      UserResourceIdentifier: req.body.UserResourceIdentifier ? req.body.UserResourceIdentifier : userRID,
      SavingsPlanAmount: req.body.SavingsPlanAmount,
      SavingsPlanDate: req.body.SavingsPlanDate
    }
  }
  return data;
}

/* Handles DeleteResource requests. Given input from AngularJS, convert it into valid input
    for the Java server. */
function handleDeleteResource (req, userRID) {
  if (req.body.ResourceIdentifier === undefined || req.body.ResourceIdentifier === null)
    return { ErrorMessage: "ResourceIdentifier must be defined." };

  var data = {
    Key: accessKey,
    Secret: secretKey,
    AccountId: userRID,
    ActionType: "DeleteResource",
    Action: {
      ResourceIdentifier: req.body.ResourceIdentifier
    }
  };

  return data;
}

/* Handles GetTransaction requests. Given input from AngularJS, convert it into valid input
    for the Java server. */
function handleGetTransactions (req, userRID) {
  var data = {
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

/* Handles GetBudgets requests. Given input from AngularJS, convert it into valid input
    for the Java server. */
function handleGetBudgets (req, userRID) {
  var data = {
    Key: accessKey,
    Secret: secretKey,
    AccountId: userRID,
    ActionType: 'GetBudget',
    Action: {
      UserResourceIdentifier: req.query.ResourceIdentifier ? req.query.ResourceIdentifier : userRID
    }
  };

  return data;
}

/* Handles GetAccounts requests. Given input from AngularJS, convert it into valid input
    for the Java server. */
function handleGetAccounts (req, userRID) {
  var data = {
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
  var data = {
    Key: accessKey,
    Secret: secretKey,
    AccountId: userRID,
    ActionType: "GetSubBalance",
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

/* Handles GetUser requests. Given input from AngularJS, convert it into valid input
    for the Java server. */
function handleGetUser (req, userRID) {
  var data = {
    Key: accessKey,
    Secret: secretKey,
    AccountId: userRID,
    ActionType: "GetUser",
    Action: {
      ResourceIdentifier: req.body.ResourceIdentifier ? req.body.ResourceIdentifier : userRID,
      UserIdentifier: req.body.UserIdentifier ? req.body.UserIdentifier : null
    }
  };

  return data;
}

/* Handles CreateAccount requests. Given input from AngularJS, convert it into valid input
    for the Java server. */
function handleCreateAccount (req, userRID) {
  if (!req.body.AccountName || req.body.AccountName === null)
    return { ErrorMessage: "Account name must be specified." };
  if (!req.body.AccountType || req.body.AccountType === null)
    return { ErrorMessage: "Account type must be specified." };

  var data = {
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

/* Handles CreateBudget requests. Given input from AngularJS, convert it into valid input
    for the Java server. */
function handleCreateBudget (req, userRID) {
  if (!req.body.Limit || req.body.Limit == null)
    return { ErrorMessage: 'A budget limit must be applied.' };
  if (!req.body.Duration || req.body.Duration == null)
    return { ErrorMessage: 'A budget duration must be applied.' };

  var data = {
    Key: accessKey,
    Secret: secretKey,
    AccountId: userRID,
    ActionType: 'CreateBudget',
    Action: {
      Limit: req.body.Limit,
      Description: req.body.Description ? req.body.Description : '',
      Duration: req.body.Duration,
      UserResourceIdentifier: req.body.UserResourceIdentifier ? req.bodyUserResourceIdentifier : userRID
    }
  };

  return data;
}

/* Handles CreateTransaction requests. Given input from AngularJS, convert it into valid input
    for the Java server. */
function handleCreateTransaction (req, userRID) {
  if (!req.body.Amount || req.body.Amount === "" || req.body.Amount == 0)
    return { ErrorMessage: "Amount not specified." };
  if ((!req.body.To || req.body.To === null) && (!req.body.From || req.body.From === null))
    return { ErrorMessage: "Account(s) To and / or From must be specified." };
  if (!req.body.DateTime || req.body.DateTime === null)
    return { ErrorMessage: "Transaction date must be specified." };
  if (!req.body.AssociatedWith || req.body.AssociatedWith === null)
    return { ErrorMessage: "Transaction must be associated with some resource." };
  if (req.body.Recurring === true && (!req.body.RecurringFrequency || req.body.RecurringFrequency === null))
    return { ErrorMessage: "Recurring transactions must have specified Recurring Frequency periods." };

  if (req.body.RecurringFrequency === 'Daily')
    req.body.RecurringFrequency = 1;
  if (req.body.RecurringFrequency === 'Weekly')
    req.body.RecurringFrequency = 7;
  if (req.body.RecurringFrequency === 'Monthly')
    req.body.RecurringFrequency = 30;

  var data = {
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

/* Handles CreateUser requests. Given input from AngularJS, convert it into valid input
    for the Java server. */
function handleCreateUser (req, userRID) {
  if (!req.body.UserIdentifier || req.body.UserIdentifier === null)
    return { ErrorMessage: "Email address must be specified." };
  if (!req.body.Password || req.body.Password === null)
    return { ErrorMessage: "Password must be specified." };
  if (!req.body.FirstName || req.body.FirstName === null)
    return { ErrorMessage: "First name must be specified." };
  if (!req.body.LastName || req.body.LastName === null)
    return { ErrorMessage: "Last name must be specified." };

  var data = {
    Key: accessKey,
    Secret: secretKey,
    AccountId: userRID,
    ActionType: "CreateUser",
    Action: {
      UserIdentifier: req.body.UserIdentifier,
      Password: req.body.Password,
      FirstName: req.body.FirstName,
      LastName: req.body.LastName
    }
  };

  return data;
}

/* Handles CreateSubbalance requests. Given input from AngularJS, convert it into valid input
    for the Java server. */
function handleCreateSubbalance (req, userRID) {
  if (!req.body.AccountResourceIdentifier || req.body.AccountResourceIdentifier === null)
    return { ErrorMessage: "Resource Identifier for the associated account is required." };
  if (!req.body.SubBalanceName || req.body.SubBalanceName === null)
    return { ErrorMessage: "Subbalance must have a name." };

  var data = {
    Key: accessKey,
    Secret: secretKey,
    AccountId: userRID,
    ActionType: "CreateSubBalance",
    Action: {
      AccountResourceIdentifier: req.body.AccountResourceIdentifier,
      SubBalanceName: req.body.SubBalanceName,
      SubBalanceBalance: req.body.SubBalanceBalance ? req.body.SubBalanceBalance : 0
    }
  };

  return data;
}

/* Handles ModifyResource requests. Given input from AngularJS, convert it into valid input
    for the Java server. */
function handleModifyResource (req, userRID) {
  if (!req.body.ResourceIdentifier || req.body.ResourceIdentifier === null)
    return { ErrorMessage: "ResourceIdentifier must be specified." };
  if (!req.body.Changes || req.body.Changes === null || !Array.isArray(req.body.Changes))
    return { ErrorMessage: "Changes is not present or incorrectly formatted." };

  for (var i = 0; i < req.body.Changes.length; ++i) {
    if (!req.body.Changes[i].Key || req.body.Changes[i].Key === null || !req.body.Changes[i].Value || req.body.Changes[i].Value === null)
      return { ErrorMessage: "Changes is not present or incorrectly formatted." };
  }

  var data = {
    Key: accessKey,
    Secret: secretKey,
    AccountId: userRID,
    ActionType: "Modify",
    Action: {
      ResourceIdentifier: req.body.ResourceIdentifier,
      Changes: req.body.Changes
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
