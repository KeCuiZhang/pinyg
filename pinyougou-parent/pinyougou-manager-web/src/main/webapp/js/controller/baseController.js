app.controller('baseController',function($scope){
	//分页控件配置currentPage:当前页   totalItems :总记录数  itemsPerPage:每页记录数  perPageOptions :分页选项  onChange:当页码变更后自动触发的方法 
	$scope.paginationConf = {
		currentPage: 1,
		totalItems: 10,
		itemsPerPage: 10,
		perPageOptions: [10, 20, 30, 40, 50],
		onChange: function(){
			$scope.reloadList();
		}
	};
	
	//刷新列表
	$scope.reloadList=function(){
		$scope.search( $scope.paginationConf.currentPage ,  $scope.paginationConf.itemsPerPage );
	}
	
	//分页 
	$scope.findPage=function(page,size){
		brandService.findPage(page,size).success(
			function(response){
				$scope.list=response.rows;//显示当前页数据 	
				$scope.paginationConf.totalItems=response.total;//更新总记录数 
				
			}		
		);				
	}
	//更新复选id
	$scope.selectIds=[];
	$scope.updateSelection=function($event,id){
		if($event.target.checked){
			$scope.selectIds.push(id);//向集合中添加元素
		}else{
			var index=$scope.selectIds.indexOf(id)//id在集合中的位置
			$scope.selectIds.splice(index,1)//删除id;
		}
		
	}
    $scope.jsonToString=function(jsonString,key){
        var json=JSON.parse(jsonString);//将 json 字符串转换为 json 对象
        var value="";
        for(var i=0;i<json.length;i++){
            if(i>0){
                value+=","
            }
            value+=json[i][key];
        }
        return value;
    }
    //从集合中按照 key 查询对象
    $scope.searchObjectByKey=function(list,key,keyValue){
        for(var i=0;i<list.length;i++){
            if(list[i][key]==keyValue){
                return list[i];
            }
        }
        return null;
    }
});