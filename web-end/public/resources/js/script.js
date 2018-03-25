$(document).ready (function () {
	var app = angular.module("MyApp", []);

	app.controller("PostsCtrl", function($scope, $http) {
		$http({
  			method: 'GET',
  			url: 'request/transaction.json',
  			data: {
  				"Limit": 30,
  				"GetFrom": "User",
			      }
			}).
			success(function(data, status, headers, config) {
				$scope.transactions = data;
				}).
			error(function(data, status, headers, config) {
				// log error
		});
	});  
});
