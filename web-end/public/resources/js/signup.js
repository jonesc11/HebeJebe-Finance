var app = angular.module("MyApp", ['ngRoute']);

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

app.controller("SignupCtrl", function($scope, $http){
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
		if ($scope.page1) {
			$scope.nextOne();
			return;
		}

		$http({
			method: 'POST',
			url: '/signup',
			data: {
				firstName: $scope.firstName,
				lastName: $scope.lastName,
				email: $scope.email,
				pass: $scope.pw,
				accountName: $scope.accountName,
				accountAmount: $scope.accountAmnt,
				accountType: $scope.accountType
			}
		}).then (function (response) {
			window.location = '/login';
		});
	};

});
