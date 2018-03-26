var app = angular.module("MyApp", []);

$(document).ready (function (app) {

	app.controller("PostsCtrl", function($scope, $http) {
		$http({
  			method: 'GET',
  			url: 'request/get/transactions',
  			data: {
  				"Limit": 30,
			      }
			}).
			then(function(success) {
				$scope.transactions = success.data;
			}).
			then(function(error) {
				// log error
		});
	}); 

	app.controller("NewPanel", function($scope, $http) {
		
		$http({
			method: 'GET',
			url: 'request/get/accounts',
			data: {
		  		"Limit": 30,
			      }
			}).then(function(success) {
				$scope.accounts = success.accounts;
			}).
			then(function(error) {
				// log error
		});

		$scope.createTransaction = function(){
	    	 $http({
  			method: 'POST',
  			url: 'request/create/transactions',
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

			}).
			then(function(error) {
				// log error
		});

		};
	});

	 
}(app));
