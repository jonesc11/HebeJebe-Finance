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
		$scope.deleteAccount = function () {
 			$http({
				url: '/request/delete',
				method: 'POST',
				data: { ResourceIdentifier: $rootScope.deletingAcct.ResourceIdentifier }
			}).then (function (success) {
				$('#deleteAccountModal').modal('hide');
			}, function (error) {

			});
		};

		$scope.deleteTransaction = function () {
			$http({
				url: '/request/delete',
				method: 'POST',
				data: { ResourceIdentifier: $rootScope.deletingTrans.ResourceIdentifier }
			}).then (function (success) {
				$('#deleteTransactionModal').modal('toggle');
			}, function (error) {
				// log error
			});
		};

		
       		$scope.createAccount = function () {
			if($scope.createAccountBalance < 0){ 
				alert("Account balance cannot be less than zero");
				return;
			}


			if($scope.createAccountName == undefined || $scope.createAccountName == ""){
				alert("You must enter an account name");
				return;
			}

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

		$scope.editTransaction = function () {
			$http({
				url: '/request/modify',
				method: 'POST',
				data: {
					ResourceIdentifier: $rootScope.editingTrans.ResourceIdentifier,
					Changes: [
						{ Key: "AssociatedWith", Value: $rootScope.editingTrans.Account.AccountResourceIdentifier },
						{ Key: 'Description', Value: $rootScope.editingTrans.Description },
						{ Key: 'Category', Value: $rootScope.editingTrans.Category },
						{ Key: 'Amount', Value: $rootScope.editingTrans.Amount },
						{ Key: 'TransactionType', Value: $rootScope.editingTrans.TransactionType },
						{ Key: 'DateTime', Value: $rootScope.editingTrans.DateTime },
						{ Key: 'Recurring', Value: $rootScope.editingTrans.Recurring },
						{ Key: 'RecurringFrequency', Value: $rootScope.editingTrans.RecurringFrequency }
					]
				}
			}).then (function (success) {
				$rootScope.$broadcast ('getTransactions');
			}, function (error) {
				// log error
			});
		};

		$scope.createSubbalance = function () {
			if($scope.createSubbalanceName == undefined || $scope.createSubbalanceName == ""){
				alert("You must enter a sub-balance name");
				return;
			}

			if($scope.createSubbalanceBalance < 0){
				alert("Sub-balances cannot be negative");
				return;
			}

			$http({
				url: '/request/create/subbalance',
				method: 'POST',
				data: {
					AccountResourceIdentifier: $rootScope.accountResourceIdentifier,
					SubBalanceName: $scope.createSubbalanceName,
					SubBalanceBalance: $scope.createSubbalanceBalance
				}
			}).then (function (success) {
				$rootScope.$broadcast ('getSubbalances');
			}, function (error) {
				// log error
			});
		};

		$scope.editSubbalanceLoad = function () {
			$('#viewSubbalanceModal').modal('hide');
			$('#editSubbalanceModal').modal('show');
		};

		$scope.editSubbalance = function () {
			$http({
				url: '/request/modify',
				method: 'POST',
				data: {
					ResourceIdentifier: $rootScope.subbalance.ResourceIdentifier,
					Changes: [
						{ Key: 'Balance', Value: $rootScope.subbalance.Balance },
						{ Key: 'SubBalanceName', Value: $rootScope.subbalance.SubBalanceName }
					]
				}
			}).then (function (success) {
				$rootScope.$broadcast ('getSubbalances');
				$('#editSubbalanceModal').modal('hide');
			}, function (error) {
				// log error
			});
		};

		$scope.deleteSubbalance = function () {
			$http({
				url: '/request/delete',
				method: 'POST',
				data: { ResourceIdentifier: $rootScope.subbalance }
			}).then (function (success) {
				$rootScope.$broadcast ('getSubbalances');
				$('#deleteSubbalanceModal').modal('hide');
			}, function (error) {
				//log error
			});
		};

		$scope.deleteSubbalanceLoad = function () {
			$('#viewSubbalanceModal').modal('hide');
			$('#deleteSubbalanceModal').modal('show');
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

		$scope.$on ('getSubbalances', function () {
			$scope.getData();
		});

		$scope.deleteTransaction = function () {
			$rootScope.deletingTrans = this.transaction;
		};

		$scope.editTransaction = function () {
			$rootScope.editingTrans = this.transaction;
			$rootScope.editingTrans.DateTime = new Date (this.transaction.DateTime);
		};

		$scope.deleteSubbalance = function () {
			$rootScope.deletingSub = this.subbalance;
		};

		$scope.editSubbalance = function () {
			$rootScope.editingSub = this.subbalance;
		};

		$scope.deleteAccountLoad = function () {
			$rootScope.deletingAcct = $scope.account;
			$('#deleteAccountModal').modal('show');
		};

		$scope.viewSubbalance = function () {
			$('#viewSubbalanceModal').modal('show');
			$rootScope.subbalance = this.subbalance;
		}

		$scope.user = {};
		$scope.accountId = $routeParams.id;
		$rootScope.accountResourceIdentifier = $routeParams.id;
		$scope.getData = function () {
			$http({
				method: 'GET',
				url: '/request/get/user',
				params: {}
			}).then (function (response) {
				$scope.user = response.data.User;
				if ($routeParams.id)
					$scope.noAccountSelected = false;
				else
					$scope.noAccountSelected = true;
				$http({
					method: 'GET',
					url: '/request/get/accounts',
					params: {
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
							params: {
								GetFrom: $scope.accounts[i].ResourceIdentifier,
								Limit: 200
							}
						}).then (function (response) {
							$scope.accounts[retInd1].transactions = response.data.Transactions;
							for (var j = 0; j < $scope.accounts[retInd1].transactions.length; ++j) {
								$scope.accounts[retInd1].transactions[j].DateTime = new Date ($scope.accounts[retInd1].transactions[j].DateTime);
								$scope.accounts[retInd1].transactions[j].DateTimeString = $scope.accounts[retInd1].transactions[j].DateTime.getFullYear() + '-' + ($scope.accounts[retInd1].transactions[j].DateTime.getMonth() + 1) + '-' + $scope.accounts[retInd1].transactions[j].DateTime.getDate();
							}
							retInd1++;
						});

						$http({
							url: '/request/get/subbalance',
							method: 'GET',
							params: {
								GetFrom: $scope.accounts[i].ResourceIdentifier
							}
						}).then (function (response) {
							$scope.accounts[retInd2].subbalances = response.data.SubBalances;
							if (!$scope.accounts[retInd2].subbalances)
								return;
							var retInd3 = 0;
							var retInd4 = retInd2;
							for (var j = 0; j < $scope.accounts[retInd2].subbalances.length; ++j) {
								$http({
									url: '/request/get/transactions',
									method: 'GET',
									params: {
										GetFrom: $scope.accounts[retInd2].subbalances[j].ResourceIdentifier,
										Limit: 200
									}
								}).then (function (response) {
									$scope.accounts[retInd4].subbalances[retInd3].transactions = response.data.Transactions;
									for (var k = 0; k < $scope.accounts[retInd4].subbalances[retInd3].transactions.length; ++k) {
										if (!$scope.accounts[retInd4].subbalances || !$scope.accounts[retInd4].subbalances[retInd3].transactions)
											continue;
										$scope.accounts[retInd4].subbalances[retInd3].transactions[k].DateTime = new Date ($scope.accounts[retInd4].subbalances[retInd3].transactions[k].DateTime);
										$scope.accounts[retInd4].subbalances[retInd3].transactions[k].DateTimeString = $scope.accounts[retInd4].subbalances[retInd3].transactions[k].DateTime.getFullYear() + '-' + ($scope.accounts[retInd4].subbalances[retInd3].transactions[k].DateTime.getMonth() + 1) + '-' + $scope.accounts[retInd4].subbalances[retInd3].transactions[k].DateTime.getDate();
									}
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
				$rootScope.accountResourceIdentifier = $scope.accountId;
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
				$rootScope.$broadcast ('getTransactions');
			}, function (error) {
				$scope.errorMessage = error.data.ErrorMessage;
			});
		});

		$http({
  			method: 'GET',
  			url: '/request/get/savingsplan',
  			params: {
  				"Limit": 30,
			}
		}).
		then(function(success) {
			console.log ("savings plan:")
			console.log(success.data);
			$scope.SavingsPlan = success.data.SavingsPlan;
			$scope.savingsPercent = ($scope.SavingsPlan.Balance / $scope.SavingsPlan.Amount) * 100;
		}, function(error) {
			// log error
		});

		$http({
  			method: 'GET',
  			url: '/request/get/budget',
  			data: {
  				"Limit": 30,
			}
		}).
		then(function(success) {
			$scope.Budget = success.data.Budget;
			console.log($scope.Budget);
			$scope.budgetPercent = ($scope.Budget.Balance / $scope.Budget.Limit) * 100;
		}, function(error) {
			// log error
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
				$http({
  				method: 'GET',
  				url: '/request/get/budget',
  				data: {
  					"Limit": 30,
				}
			}).
			then(function(success) {
				$scope.Budget = success.data.Budget;
				console.log($scope.Budget);
				$scope.budgetPercent = ($scope.Budget.Balance / $scope.Budget.Limit) * 100;
			}, function(error) {
			// log error
			});

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
		
	$scope.createBudget = function(){
		if($scope.budgetLimit < 0){
			alert("Budget limits cannot be negative");
			return;
		}

		if($scope.budgetDescription == undefined || $scope.budgetDescription == ""){
			alert("You must enter a budget description");
			return;
		}

		console.log("creating a budget");
		$http ({
               		method: 'POST',
               		url: '/request/create/budget',
               		data: {
					Limit: $scope.budgetLimit,
					Description: $scope.budgetDescription,
					Balance: 0,
					Duration: $scope.budgetDuration
				}
			}).then (function (success) {
				$scope.Budget = success.data.Budget;
				console.log(success.data);
			}).then (function (error) {
			});

		}


		$scope.getProjection = function(){

		console.log("getting projection")
		$scope.showProjection = true;
		$http({
           		method: 'GET',
           		url: '/request/get/projection',
           		params: {
				"ProjectionDate": $scope.projectionDate
       			}

      		}).then(function(success) {
			console.log("got projection")
      			$scope.projection = success.data;
			console.log($scope.projection)
       		}, function(error) {
      			// log error
    		});


		};


		$scope.createSavings = function(){
			$http({
           			method: 'POST',
           			url: '/request/create/savingsplan',
           			data: {
					SavingsPlanName: $scope.newName,
					SavingsPlanAmount: $scope.newAmount,
					SavingsPlanDate: $scope.newDate
       				}
      			}).then(function(success) {
				$scope.SavingsPlan = success.data;
				console.log("created a savings plan")
       			}, function(error) {
      			// log error
    			});


		}

		$scope.createOrModifySavings = function(){
			if($scope.SavingsPlan == undefined || $scope.SavingsPlan.SavingsPlanName == undefined){
				$scope.createSavings();
			}else{

				$scope.modifySavings();
			}

		}

		$scope.modifySavings = function(){
			$http({
           			method: 'POST',
           			url: '/request/modify',
           			data: {
					"Changes": [{
							"Date": $scope.newDate,
							"Amount": $scope.newAmount,
							"SavingsPlanName": $scope.newName
						}]
       				}
      			}).then(function(success) {

       			}, function(error) {
      			// log error
    			});
		}
		
		$scope.getTotalBalance = function(){
			if($scope.accounts == undefined){
				return 0;
			}
			var totalBalance = 0;
			for(var i=0; i< $scope.accounts.length; i++){
				console.log("adding account balance");
				totalBalance += $scope.accounts[i].LatestBalance;
			}

			return totalBalance;
		};

			
		$scope.addMoney = function(){
			$http({
           			method: 'POST',
           			url: '/request/addto/savingsplan',
           			data: {
					"Amount": $scope.moneyToAdd,
					"AccountResourceIdentifier": $scope.moneyToAddAcc
       				}
      			}).then(function(success) {
				console.log("added money")
				console.log(success.data)
				$scope.SavingsPlan = success.data.SavingsPlan;
				$scope.SavingsPlan.Balance = success.data.SavingsPlan.SavingsPlanBalance;
				$scope.SavingsPlan.Amount = success.data.SavingsPlan.SavingsPlanAmount;
				$scope.savingsPercent = ($scope.SavingsPlan.Balance / $scope.SavingsPlan.Amount) * 100;
				$http({
					url: '/request/get/transactions',
					method: 'GET'
				}).then (function (success) {
					$scope.transactions = success.data.Transactions;
				});
       			}, function(error) {
      			// log error
    			});

		};

		$scope.totalBalance = $scope.getTotalBalance();

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
