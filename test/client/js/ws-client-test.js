var myApp = angular.module('app',[]);

myApp.controller('paramTable', ['$scope', function($scope) {

    initStuff();

    $scope.$watchCollection("[dev1rtype,dev1devid,dev1lat,dev1lon,dev1ss,dev1se,dev1pl,dev1ep,dev1apikey,dev1appId]", function(nv) {
        $scope.dev1url = 'ws://localhost:9000/request?type=' + nv[0] + '&apiKey=' + nv[8] + '&appId=' + nv[9] + '&latitude=' + nv[2] + '&longitude=' + nv[3] + '&swipeStart=' + nv[4] + '&swipeEnd=' + nv[5] +'&deviceId=' + nv[1] + '&payload=' + nv[6] + '&equalityParam1=' + nv[7];
    });

    $scope.$watchCollection("[dev2rtype,dev2devid,dev2lat,dev2lon,dev2ss,dev2se,dev2pl,dev2ep,dev2apikey,dev2appId]", function(nv) {
        $scope.dev2url = 'ws://localhost:9000/request?type=' + nv[0] + '&apiKey=' + nv[8] + '&appId=' + nv[9] + '&latitude=' + nv[2] + '&longitude=' + nv[3] + '&swipeStart=' + nv[4] + '&swipeEnd=' + nv[5] +'&deviceId=' + nv[1] + '&payload=' + nv[6] + '&equalityParam1=' + nv[7];
    });

    $scope.$watchCollection("[dev3rtype,dev3devid,dev3lat,dev3lon,dev3ss,dev3se,dev3pl,dev3ep,dev3apikey,dev3appId]", function(nv) {
        $scope.dev3url = 'ws://localhost:9000/request?type=' + nv[0] + '&apiKey=' + nv[8] + '&appId=' + nv[9] + '&latitude=' + nv[2] + '&longitude=' + nv[3] + '&swipeStart=' + nv[4] + '&swipeEnd=' + nv[5] +'&deviceId=' + nv[1] + '&payload=' + nv[6] + '&equalityParam1=' + nv[7];
    });

    $scope.$watchCollection("[dev4rtype,dev4devid,dev4lat,dev4lon,dev4ss,dev4se,dev4pl,dev4ep,dev4apikey,dev4appId]", function(nv) {
        $scope.dev4url = 'ws://localhost:9000/request?type=' + nv[0] + '&apiKey=' + nv[8] + '&appId=' + nv[9] + '&latitude=' + nv[2] + '&longitude=' + nv[3] + '&swipeStart=' + nv[4] + '&swipeEnd=' + nv[5] +'&deviceId=' + nv[1] + '&payload=' + nv[6] + '&equalityParam1=' + nv[7];
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

    function initStuff() {
        $scope.dev1rtype = "content"
        $scope.dev2rtype = "content"
        $scope.dev3rtype = "content"
        $scope.dev4rtype = "content"

        $scope.dev1apikey = "contact-api-key"
        $scope.dev2apikey = "contact-api-key"
        $scope.dev3apikey = "contact-api-key"
        $scope.dev4apikey = "contact-api-key"

        $scope.dev1devid = "id1"
        $scope.dev2devid = "id2"
        $scope.dev3devid = "id3"
        $scope.dev4devid = "id4"

        $scope.dev1lat = "12.00"
        $scope.dev2lat = "12.00"
        $scope.dev3lat = "12.00"
        $scope.dev4lat = "12.00"

        $scope.dev1lon = "12.00"
        $scope.dev2lon = "12.00"
        $scope.dev3lon = "12.00"
        $scope.dev4lon = "12.00"

        $scope.dev1ss = "4"
        $scope.dev2ss = "2"
        $scope.dev3ss = "0"
        $scope.dev4ss = "3"

        $scope.dev1se = "3"
        $scope.dev2se = "1"
        $scope.dev3se = "2"
        $scope.dev4se = "4"

        $scope.dev1pl = "pl1"
        $scope.dev2pl = "pl2"
        $scope.dev3pl = "pl3"
        $scope.dev4pl = "pl4"

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
