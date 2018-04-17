var app = angular.module("MyApp", ['ngRoute']);

$(document).ready (function (app) {

	app.config(["$routeProvider", function($routeProvider){
		$routeProvider
		.when("/", {
			controller: 'HomeCtrl',
			templateUrl: '/resources/views/home.html'
		})
		.when('/account', {
			controller: 'AccountsController',
			templateUrl: '/resources/views/accounts.html'
		})
		.when('/account/:id', {
			controller: 'AccountsController',
			templateUrl: '/resources/views/accounts.html'
		})
		.otherwise({redirectTo: "/"})
	}]);

	app.controller("NavBarCtrl", function($rootScope, $scope, $http){

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

	});

	app.controller('AccountsController', function ($rootScope, $scope, $http, $routeParams) {
		$scope.user = {};
		$scope.accountId = $routeParams.id;
		$http({
			method: 'GET',
			url: '/request/get/user',
			data: {}
		}).then (function (response) {
			$scope.user = response.data.User;
			if ($routeParams.id)
				$scope.noAccountSelected = false;
			else
				$scope.noAccountSelected = true;
			$http({
				method: 'GET',
				url: '/request/get/accounts',
				data: {
					GetFrom: $scope.user.ResourceIdentifier
				}
			}).then (function (response) {
				$scope.accounts = response.data.Account;
console.log ($scope.accounts);
				var retInd1 = 0, retInd2 = 0;
				for (var i = 0; i < $scope.accounts.length; ++i) {
					if ($scope.accounts[i].ResourceIdentifier == $scope.accountId)
						$scope.account = $scope.accounts[i];
					$http({
						url: '/request/get/transactions',
						method: 'GET',
						data: {
							GetFrom: $scope.accounts[i].ResourceIdentifier,
							Limit: 200
						}
					}).then (function (response) {
						$scope.accounts[retInd1].transactions = response.data.Transactions;
						retInd1++;
					});

					$http({
						url: '/request/get/subbalance',
						method: 'GET',
						data: {
							GetFrom: $scope.accounts[i].ResourceIdentifier
						}
					}).then (function (response) {
						$scope.accounts[retInd2].subbalances = response.data.Subbalances;
						if (!$scope.accounts[retInd2].subbalances)
							return;
						var retInd3 = 0;
						for (var j = 0; j < $scope.accounts[retInd2].subbalances.length; ++j) {
							$http({
								url: '/request/get/transactions',
								method: 'GET',
								data: {
									GetFrom: $scope.accounts[retInd2].subbalances[retInd3].ResourceIdentifier,
									Limit: 200
								}
							}).then (function (response) {
								$scope.accounts[retInd2].subbalances[retInd3].transactions = response.data.Transactions;
								retInd3++;
							});
						}
						retInd2++;
					});
				}
			});
		});

		$scope.selectAccountChange = function () {
			if ($scope.accountId) {
				$scope.noAccountSelected = false;
				for (var i = 0; i < $scope.accounts.length; ++i) {
					if ($scope.accounts[i].ResourceIdentifier == $scope.accountId) {
						$scope.account = $scope.accounts[i];
						break;
					}
				}
				$routeParams.id = $scope.accountId;
			} else {
				$scope.noAccountSelected = true;
				$scope.account = {};
			}
		}
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
