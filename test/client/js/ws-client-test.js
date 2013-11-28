var myApp = angular.module('app',[]);

myApp.controller('paramTable', ['$scope', function($scope) {

    initStuff();

    $scope.$watchCollection("[dev1rtype,dev1devid,dev1lat,dev1lon,dev1ss,dev1se,dev1pl,dev1ep,dev1apikey,dev1appId,serverUrl,dev1criteria]", function(nv) {
        var allParams = '?type=' + nv[0] + '&criteria=' + nv[11] + '&apiKey=' + nv[8] + '&appId=' + nv[9] + '&latitude=' + nv[2] + '&longitude=' + nv[3] + '&areaStart=' + nv[4] + '&areaEnd=' + nv[5] +'&deviceId=' + nv[1] + '&payload=' + nv[6] + '&equalityParam1=' + nv[7];
        $scope.dev1url = 'ws://' + nv[10] + '/requestWS' + allParams;
        $scope.dev1urlHTTP = 'http://' + nv[10] + '/requestHTTP?type=' + allParams;
    });

    $scope.$watchCollection("[dev2rtype,dev2devid,dev2lat,dev2lon,dev2ss,dev2se,dev2pl,dev2ep,dev2apikey,dev2appId,serverUrl]", function(nv) {
        var allParams = '?type=' + nv[0] + '&criteria=' + nv[11] + '&apiKey=' + nv[8] + '&appId=' + nv[9] + '&latitude=' + nv[2] + '&longitude=' + nv[3] + '&areaStart=' + nv[4] + '&areaEnd=' + nv[5] +'&deviceId=' + nv[1] + '&payload=' + nv[6] + '&equalityParam1=' + nv[7];
        $scope.dev2url = 'ws://' + nv[10] + '/requestWS' + allParams;
        $scope.dev2urlHTTP = 'http://' + nv[10] + '/requestHTTP?type=' + allParams;
    });

    $scope.$watchCollection("[dev3rtype,dev3devid,dev3lat,dev3lon,dev3ss,dev3se,dev3pl,dev3ep,dev3apikey,dev3appId,serverUrl]", function(nv) {
        var allParams = '?type=' + nv[0] + '&criteria=' + nv[11] + '&apiKey=' + nv[8] + '&appId=' + nv[9] + '&latitude=' + nv[2] + '&longitude=' + nv[3] + '&areaStart=' + nv[4] + '&areaEnd=' + nv[5] +'&deviceId=' + nv[1] + '&payload=' + nv[6] + '&equalityParam1=' + nv[7];
        $scope.dev3url = 'ws://' + nv[10] + '/requestWS' + allParams;
        $scope.dev3urlHTTP = 'http://' + nv[10] + '/requestHTTP?type=' + allParams;
    });

    $scope.$watchCollection("[dev4rtype,dev4devid,dev4lat,dev4lon,dev4ss,dev4se,dev4pl,dev4ep,dev4apikey,dev4appId,serverUrl]", function(nv) {
        var allParams = '?type=' + nv[0] + '&criteria=' + nv[11] + '&apiKey=' + nv[8] + '&appId=' + nv[9] + '&latitude=' + nv[2] + '&longitude=' + nv[3] + '&areaStart=' + nv[4] + '&areaEnd=' + nv[5] +'&deviceId=' + nv[1] + '&payload=' + nv[6] + '&equalityParam1=' + nv[7];
        $scope.dev4url = 'ws://' + nv[10] + '/requestWS' + allParams;
        $scope.dev4urlHTTP = 'http://' + nv[10] + '/requestHTTP?type=' + allParams;
    });

    var myWebSocket1, myWebSocket2, myWebSocket3, myWebSocket4;

    // controls the disabling / enabling of the buttons
    $scope.isDisabled1 = true;
    $scope.isDisabled2 = true;
    $scope.isDisabled3 = true;
    $scope.isDisabled4 = true;

    // sending messages stuff
    var sendGen = function(socket, msg) {socket.send(msg);};

    $scope.send1 = function() {
        sendGen(myWebSocket1, $scope.msg1);
        console.log("sending msg 1: " + $scope.msg1);
    };

    $scope.send2 = function() {
        sendGen(myWebSocket2, $scope.msg2);
        console.log("sending msg 2: " + $scope.msg2);
    };

    $scope.send3 = function() {
        sendGen(myWebSocket3, $scope.msg3);
        console.log("sending msg 3: " + $scope.msg3);
    };

    $scope.send4 = function() {
        sendGen(myWebSocket4, $scope.msg4);
        console.log("sending msg 4: " + $scope.msg4);
    };

    // websockets connection stuff
    function onM(id, evt) {
        $scope[id] = evt.data;
        console.log("got data: " + evt.data);
        $scope.$apply();
    }
    function onO(id, evt) {
        // here "id" is a string. equivalent to
        //   $scope.isDisabled1 === $scope["isDisabled1"]
        $scope[id] = false;
        console.log("Connection open ...");
        $scope.$apply();
    }
    function onC(id, evt) {
        $scope[id] = true;
        console.log("Connection closed.");
        $scope.$apply();
    }

    $scope.connect1 = function() {
        myWebSocket1 = new WebSocket($scope.dev1url);
        myWebSocket1.onmessage = onM.bind(null, "dev1msg");

        // onO.bind creates a new function with only one parameter,
        //  and the first one will be set as "isDisabled1"
        myWebSocket1.onopen = onO.bind(null, "isDisabled1");
        myWebSocket1.onclose = onC.bind(null, "isDisabled1");
    };

    $scope.connect2 = function() {
        myWebSocket2 = new WebSocket($scope.dev2url);
        myWebSocket2.onmessage = onM.bind(null, "dev2msg");;
        myWebSocket2.onopen = onO.bind(null, "isDisabled2");
        myWebSocket2.onclose = onC.bind(null, "isDisabled2");
    };

    $scope.connect3 = function() {
        myWebSocket3 = new WebSocket($scope.dev3url);
        myWebSocket3.onmessage = onM.bind(null, "dev3msg");;
        myWebSocket3.onopen = onO.bind(null, "isDisabled3");
        myWebSocket3.onclose = onC.bind(null, "isDisabled3");
    };

    $scope.connect4 = function() {
        myWebSocket4 = new WebSocket($scope.dev4url);
        myWebSocket4.onmessage = onM.bind(null, "dev4msg");
        myWebSocket4.onopen = onO.bind(null, "isDisabled4");
        myWebSocket4.onclose = onC.bind(null, "isDisabled4");
    };

    function callAjax(url){
    var xmlhttp;
    // compatible with IE7+, Firefox, Chrome, Opera, Safari
    xmlhttp = new XMLHttpRequest();
    xmlhttp.onreadystatechange = function(){
        if (xmlhttp.readyState == 4 && xmlhttp.status == 200){
            console.log(xmlhttp.responseText);
        }
    }
    xmlhttp.open("GET", url, true);
    xmlhttp.send();
}

$scope.connect1HTTP = function() {
    var res = callAjax($scope.dev1urlHTTP)
    console.log(res)
}

$scope.connect2HTTP = function() {
    var res = callAjax($scope.dev2urlHTTP)
    console.log(res)
}

$scope.connect3HTTP = function() {
    var res = callAjax($scope.dev3urlHTTP)
    console.log(res)
}

$scope.connect4HTTP = function() {
    var res = callAjax($scope.dev4urlHTTP)
    console.log(res)
}



    function initStuff() {

        $scope.serverUrl = "localhost:9000"
        //$scope.serverUrl = "thawing-escarpment-8488.herokuapp.com"

        $scope.dev1rtype = "content"
        $scope.dev2rtype = "content"
        $scope.dev3rtype = "content"
        $scope.dev4rtype = "content"

        $scope.dev1criteria = "presence"
        $scope.dev2criteria = "presence"
        $scope.dev3criteria = "presence"
        $scope.dev4criteria = "presence"

        $scope.dev1apikey = "contact-api-key"
        $scope.dev2apikey = "contact-api-key"
        $scope.dev3apikey = "contact-api-key"
        $scope.dev4apikey = "contact-api-key"

        $scope.dev1devid = "idjs1"
        $scope.dev2devid = "idjs2"
        $scope.dev3devid = "idjs3"
        $scope.dev4devid = "idjs4"

        $scope.dev1lat = "12.00"
        $scope.dev2lat = "12.00"
        $scope.dev3lat = "12.00"
        $scope.dev4lat = "12.00"

        $scope.dev1lon = "12.00"
        $scope.dev2lon = "12.00"
        $scope.dev3lon = "12.00"
        $scope.dev4lon = "12.00"

        $scope.dev1ss = "inner"
        $scope.dev2ss = "left"
        $scope.dev3ss = "left"
        $scope.dev4ss = "left"

        $scope.dev1se = "right"
        $scope.dev2se = "right"
        $scope.dev3se = "right"
        $scope.dev4se = "inner"

        $scope.dev1pl = "[{\"type\":\"NAME\",\"content\":{\"value\":\"Fabio1\"}}]"
        $scope.dev2pl = "[{\"type\":\"NAME\",\"content\":{\"value\":\"Fabio2\"}}]"
        $scope.dev3pl = "[{\"type\":\"NAME\",\"content\":{\"value\":\"Fabio3\"}}]"
        $scope.dev4pl = "[{\"type\":\"NAME\",\"content\":{\"value\":\"Fabio4\"}}]"

        $scope.dev1ep = "uguale"
        $scope.dev2ep = "uguale"
        $scope.dev3ep = "uguale"
        $scope.dev4ep = "uguale"

        $scope.dev1appId = "123-ABC"
        $scope.dev2appId = "123-ABC"
        $scope.dev3appId = "123-ABC"
        $scope.dev4appId = "123-ABC"
    }

}]);
