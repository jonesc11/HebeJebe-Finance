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

		$scope.createTransaction = function(){
	    	 $http({
  			method: 'POST',
  			url: 'request/create/transactions',
  			data: {
  				"Limit": 30,
				"TransactionType": $scope.transactionType, 
  				"Amount": $scope.amount,
  				"To": {"S": "<string>", "I": "<string>"},
  				"From": {"S": "<string>", "I": "<string>"},
  				"Description": "<string>",
  				"DateTime": "<datetime>",
  				"Category": "<string>",
  				"AssociatedWith": "<string>",
  				"Recurring": $scope.recurring,
  				"RecurringUntil": "<datetime>",
  				"RecurringFrequency": "<dayinterval>",
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
