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

	app.controller('ModalController', function ($rootScope, $scope, $http) {
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
				$rootScope.$broadcast('getAccounts');
				$("#createAccountModal").modal('toggle');
				$("input[name=create-account-name]").val("");
				$("input[name=create-account-balance]").val("");
			}).then (function (error) {
			});
        };

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
  					"Category": $scope.transactionCategory,
  					"AssociatedWith": $scope.transactionAccount,
  					"Recurring": $scope.recurring,
  					"RecurringUntil": null,
 	 				"RecurringFrequency": $scope.transactionRecurInterval,
				}
			}).
			then(function(success) {
				$('#createTransactionModal').modal('toggle');
				$rootScope.$broadcast ('getTransactions');
			}, function(error) {
				// log error
			});
		};
	});	

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

		$scope.$on('getAccounts', function () {
			$http({
				url: '/request/get/accounts',
				method: 'GET',
				data: {
					Limit: 30
				}
			}).then (function (success) {
				$scope.accounts = success.data.Account;
			}, function (error) {
				$scope.errorMessage = error.data.ErrorMessage;
			});
		});

		$http({
			method: 'GET',
			url: '/request/get/accounts',
			data: {
				"Limit": 30
			}
		}).then(function(success) {
			$scope.accounts = success.data.Account;
		}, function(error) {
			// log error
		});
	});

	app.controller('AccountsController', function ($rootScope, $scope, $http, $routeParams) {
		$scope.$on ('getTransactions', function () {
			$scope.getData ();
		});

		$scope.$on ('getAccounts', function () {
			$scope.getData();
		});

		$scope.user = {};
		$scope.accountId = $routeParams.id;
		$scope.getData = function () {
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
		};

		$scope.getData();

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
		$scope.$on ('getAccounts', function () {
			$http({
				url: '/request/get/accounts',
				method: 'GET',
				data: {
					Limit: 30
				}
			}).then (function (success) {
				$scope.accounts = success.data.Account;
			}, function (error) {
				$scope.errorMessage = error.data.ErrorMessage;
			});
		});

		$scope.$on('getTransactions', function () {
			$http({
				url: '/request/get/transactions',
				method: 'GET',
				data: {
					Limit: 30
				}
			}).then (function (success) {
				$scope.transactions = success.data.Transactions;
			}, function (error) {
				$scope.errorMessage = error.data.ErrorMessage;
			});
		});

		$http({
  			method: 'GET',
  			url: '/request/get/transactions',
  			data: {
  				"Limit": 30,
			}
		}).
		then(function(success) {
			$scope.transactions = success.data.Transactions;
		}, function(error) {
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
       	}, function(error) {
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
  					"Category": $scope.transactionCategory,
  					"AssociatedWith": $scope.transactionAccount,
  					"Recurring": $scope.recurring,
  					"RecurringUntil": null,
 	 				"RecurringFrequency": $scope.transactionRecurInterval,
				}
			}).then(function(success) {
			}, function(error) {
				// log error
			});
		};
	}); 
}(app));
