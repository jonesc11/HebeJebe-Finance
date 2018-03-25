$(document).ready (function () {
	var app = angular.module("MyApp", []);

	app.controller("PostsCtrl", function($scope, $http) {
		$http({
  			method: 'GET',
  			url: 'request/transactions',
  			data: {
  				"Limit": 30,
  				"GetFrom": "User",
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
