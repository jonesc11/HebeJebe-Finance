var app = angular.module("MyApp", []);

$(document).ready (function (app) {

	app.controller("PostsCtrl", function($scope, $http) {
		$http({
  			method: 'GET',
  			url: '/request/get/transactions',
  			data: {
  				"Limit": 30,
			      }
			}).
			then(function(success) {
				console.log(success.data);
				$scope.transactions = success.data.Transactions;
			}).
			then(function(error) {
				// log error
		});
	}); 

	app.controller("NewPanel", function($scope, $http) {
        var getAccounts = function () {
            $http({
                method: 'GET',
                url: '/request/get/accounts',
                data: {
                    "Limit": 30
                    }
                }).then(function(success) {
                    console.log (success.data);
                    $scope.accounts = success.data.Account;
                }).
                then(function(error) {
                    // log error
            });
        };
        
        getAccounts();

		$scope.createTransaction = function(){
	    	 $http({
  			method: 'POST',
  			url: '/request/create/transaction',
  			data: {
  				"Limit": 30,
				"TransactionType": $scope.transactionType, 
  				"Amount": $scope.amount,
  				"To": null,
  				"From": null,
  				"Description": $scope.transactionDescription,
  				"DateTime": $scope.transactionDate,
  				"Category": "<string>",
  				"AssociatedWith": $scope.transactionAccount,
  				"Recurring": $scope.recurring,
  				"RecurringUntil": null,
  				"RecurringFrequency": $scope.transactionRecurInterval,
			      }
			}).
			then(function(success) {
				console.log (success.data);
			}).
			then(function(error) {
				// log error
		});

		};
        
        $scope.createAccount = function () {
            $http ({
                method: 'POST',
                url: '/request/create/account',
                data: {
                    AccountName: $scope.createAccountName,
                    AccountBalance: $scope.createAccountBalance,
                    AccountType: $scope.createAccountType
                }
            }).then (function (success) {
                console.log (success.data);
                getAccounts();
                $("#createAccountModal").modal('toggle');
                $("input[name=create-account-name]").val("");
                $("input[name=create-account-balance]").val("");
            }).then (function (error) {
                // log error
            });
        };
	});
	 
}(app));
