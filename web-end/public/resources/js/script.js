$(document).ready (function () {
	var app = angular.module("MyApp", []);

	app.controller("PostsCtrl", function($scope, $http) {
		$http.get('request/transaction.json').
			success(function(data, status, headers, config) {
				$scope.transactions = data;

				}).
			error(function(data, status, headers, config) {
				// log error
		});
	});  
});
