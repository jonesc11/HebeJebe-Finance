$(document).ready (function () {
	var app = angular.module("MyApp", []);

	app.controller("PostsCtrl", function($scope, $http) {
		$http({
  			method: 'GET',
  			url: 'request/transactions',
  			data: {
  				"Limit": 30,
  				"ResourceIdentifier": "tr",
  				"GetFrom": "User",
  				"TransactionType": null, 
  				"Category": null,
  				"NextToken": null 
			      }
			}).
			then(function(success) {
				$scope.transactions = data;
			}).
			then(function(error) {
				// log error
		});
	});  
}());
