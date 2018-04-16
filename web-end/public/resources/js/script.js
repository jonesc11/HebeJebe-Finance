var app = angular.module("MyApp", ['ngRoute']);

$(document).ready (function (app) {


	app.config(["$routeProvider", function($routeProvider){
		$routeProvider
		.when("/", {
			controller: 'HomeCtrl',
			templateUrl: '/resources/views/home.html'
		})
		.otherwise({redirectTo: "/"})

	}]);

	app.controller("NavBarCtrl", function($rootScope, $scope, $http){

	});

	
	app.controller("HomeCtrl", function($rootScope, $scope, $http){
		$scope.page1 = true;
		$scope.page2 = false;

		$scope.nextOne = function(){
			$scope.page1 = false
			$scope.page2 = true
		}	

		$scope.prevTwo = function(){
			$scope.page1 = true
			$scope.page2 = false
		}

		$scope.register = function(){
			$http({
				method: 'POST',
				url: '/signup',
				data: {
					"firstName": $scope.firstName,
					"lastName": $scope.lastName,
					"email": $scope.email,
					"pass": $scope.pw,
					"accountName": $scope.accountName,
					"accountAmount": $scope.accountAmnt,
					"accountType": $scope.accountType
				}
			})
		};



		$http({
  			method: 'GET',
  			url: '/request/get/transactions',
  			data: {
  				"Limit": 30,
			      }
			}).
			then(function(success) {
				$scope.transactions = success.data.Transactions;
			}).
			then(function(error) {
				// log error
		});


        	var getAccounts = function () {
            		$http({
                		method: 'GET',
                		url: '/request/get/accounts',
                		data: {
                    			"Limit": 30
                    		}
                	}).then(function(success) {
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
  				"To": $scope.transactionType == 'Expense' ? null : $scope.transactionAccount,
  				"From": $scope.transactionType == 'Income' ? null : $scope.transactionAccount,
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
