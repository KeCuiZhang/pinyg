app.controller('searchController',function($scope,$location,searchService){
	$scope.searchMap={"keywords":"","category":"","brand":"","spec":{},"price":"",
        "pageNo":1,"pageSize":20, "sort":"","sortField":""};
	//搜索
	$scope.search=function(){
        $scope.searchMap.pageNo= parseInt($scope.searchMap.pageNo);
		searchService.search($scope.searchMap).success(
			function(response){
				$scope.resultMap=response;
                buidPageLabel();
			}
		);		
	}
	$scope.addItem=function (key,value) {
        if(key=="category"||key=="brand"||key=="price"){
            $scope.searchMap[key]=value;
        }else{
            $scope.searchMap.spec[key]=value;
        }
        $scope.search();
    }
    $scope.remvoeItem=function (key) {
        if(key=="category"||key=="brand"||key=="price"){
            $scope.searchMap[key]="";
        }else{
           delete $scope.searchMap.spec[key];
        }
        $scope.search();
    }
    //分页
    buidPageLabel=function (pageNo) {
        $scope.pageLabel=[];//定义页码标签
        $scope.firstPage=1;
        $scope.lastPage=$scope.resultMap.totalPages;
        if($scope.resultMap.totalPages>5){//如果总页数大于5
            if($scope.searchMap.pageNo<=3){
                $scope.lastPage=5;
            }else if($scope.searchMap.pageNo>=$scope.resultMap.totalPages-2){
                $scope.firstPage=$scope.resultMap.totalPages-4;
            }else{
                $scope.firstPage=$scope.searchMap.pageNo-2;
                $scope.lastPage=$scope.searchMap.pageNo+2;
            }

        }

        for(var i=$scope.firstPage;i<=$scope.lastPage;i++){
            $scope.pageLabel.push(i);
        }
    }
    //分页查询
    $scope.queryByPage=function(pageNo){
        if(pageNo<1||pageNo>$scope.resultMap.totalPages){//页码验证
            return }
        $scope.searchMap.pageNo=pageNo;
        $scope.search();
    }
    //排序
    $scope.queryBySort=function (sort,sortField) {
        $scope.searchMap.sort=sort;
        $scope.searchMap.sortField=sortField;
        $scope.search();
    }
    //隐藏品牌列表
    $scope.itemIsBrand=function () {
        for(var i=0;i<$scope.resultMap.brandList.length;i++){
            if($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)>=0){
                return true;
            }
        }
        return false;
    }
    //接受首页的关键字
    $scope.receive=function () {
        var a=$location.search()['keywords'];
        $scope.searchMap.keywords=$location.search()['keywords'];
        $scope.search();
    }
	
	
});