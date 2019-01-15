app.controller('brandController',function($scope,$controller,brandService){
	$controller('baseController',{$scope:$scope});
        	//查询品牌
        	$scope.findAll=function(){
        		brandService.findAll().success(
        				function(response){
        			$scope.list=response;
        		}
        				);
        	}
        
			
			
			$scope.save=function(){
				var object=null;
				if($scope.entity.id!=null){
					object=brandService.update($scope.entity);
				}else{
					object=brandService.add($scope.entity);
				}
				object.success(
				function(response){
					if(response.success){
						$scope.reloadList();
					}else{
						alert(response.message);
					}
				}		
				);
			}
			$scope.findOne=function(id){
				brandService.findOne(id).success(
					function(response){
						$scope.entity=response;
					}
				);
			}
			
			$scope.dele=function(){
				brandService.dele($scope.selectIds).success(
						function(response){
							if(response.success){
								$scope.reloadList();
							}
						});
			}
			$scope.searchEntity={};
			$scope.search=function(page,size){
				brandService.search(page,size,$scope.searchEntity).success(
				    function(response){
				    	$scope.list=response.rows;//给列变量赋值
				    	$scope.paginationConf.totalItems=response.total;//总记录数
				    }		
				)
			}


			
			
        });