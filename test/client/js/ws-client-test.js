var myApp = angular.module('app',[]);

myApp.controller('paramTable', ['$scope', function($scope) {

    initStuff($scope);

    $scope.$watch("serverUrl", function() {
      $scope.openWSUrl =  "ws://" + $scope.serverUrl + "/openWS"
    });

    $scope.$watchCollection("[dev1rtype,dev1devid,dev1lat,dev1lon,dev1ss,dev1se,dev1pl,dev1ep,dev1apikey,dev1appId,dev1criteria]", function(nv) {
        $scope.match1json = createMatchJson(nv)
    });

    $scope.$watchCollection("[dev2rtype,dev2devid,dev2lat,dev2lon,dev2ss,dev2se,dev2pl,dev2ep,dev2apikey,dev2appId,dev2criteria]", function(nv) {
        $scope.match2json = createMatchJson(nv)
    });

    $scope.$watchCollection("[dev3rtype,dev3devid,dev3lat,dev3lon,dev3ss,dev3se,dev3pl,dev3ep,dev3apikey,dev3appId,dev3criteria]", function(nv) {
        $scope.match3json = createMatchJson(nv)
    });

    $scope.$watchCollection("[dev4rtype,dev4devid,dev4lat,dev4lon,dev4ss,dev4se,dev4pl,dev4ep,dev4apikey,dev4appId,dev4criteria]", function(nv) {
        $scope.match4json = createMatchJson(nv)
    });

    var myWebSocket1, myWebSocket2, myWebSocket3, myWebSocket4;

    // controls the disabling / enabling of the buttons
    $scope.isDisabled1 = false;
    $scope.isDisabled2 = false;
    $scope.isDisabled3 = false;
    $scope.isDisabled4 = false;

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
        //$scope[id] = false;
        console.log("Connection open ...");
        $scope.$apply();
    }
    function onC(id, evt) {
        //$scope[id] = true;
        console.log("Connection closed.");
        $scope.$apply();
    }

    $scope.connect1 = function() {
        $scope.isDisabled1 = true;
        myWebSocket1 = new WebSocket($scope.openWSUrl);
        myWebSocket1.onmessage = onM.bind(null, "dev1msg");

        // onO.bind creates a new function with only one parameter,
        //  and the first one will be set as "isDisabled1"
        myWebSocket1.onopen = onO.bind(null, "isDisabled1");
        myWebSocket1.onclose = onC.bind(null, "isDisabled1");
    };

    $scope.connect2 = function() {
        $scope.isDisabled2 = true;
        myWebSocket2 = new WebSocket($scope.openWSUrl);
        myWebSocket2.onmessage = onM.bind(null, "dev2msg");;
        myWebSocket2.onopen = onO.bind(null, "isDisabled2");
        myWebSocket2.onclose = onC.bind(null, "isDisabled2");
    };

    $scope.connect3 = function() {
        $scope.isDisabled3 = true;
        myWebSocket3 = new WebSocket($scope.openWSUrl);
        myWebSocket3.onmessage = onM.bind(null, "dev3msg");;
        myWebSocket3.onopen = onO.bind(null, "isDisabled3");
        myWebSocket3.onclose = onC.bind(null, "isDisabled3");
    };

    $scope.connect4 = function() {
        $scope.isDisabled4 = true;
        myWebSocket4 = new WebSocket($scope.openWSUrl);
        myWebSocket4.onmessage = onM.bind(null, "dev4msg");
        myWebSocket4.onopen = onO.bind(null, "isDisabled4");
        myWebSocket4.onclose = onC.bind(null, "isDisabled4");
    };

    $scope.disconnect1 = function() {
        myWebSocket1.send(createDisconnectJson());
        $scope.isDisabled1 = false;
    };
    $scope.disconnect2 = function() {
        myWebSocket2.send(createDisconnectJson());
        $scope.isDisabled2 = false;
    };
    $scope.disconnect3 = function() {
        myWebSocket3.send(createDisconnectJson());
        $scope.isDisabled3 = false;
    };
    $scope.disconnect4 = function() {
        myWebSocket4.send(createDisconnectJson());
        $scope.isDisabled4 = false;
    };

    $scope.match1 = function() {myWebSocket1.send($scope.match1json)}
    $scope.match2 = function() {myWebSocket2.send($scope.match2json)}
    $scope.match3 = function() {myWebSocket3.send($scope.match3json)}
    $scope.match4 = function() {myWebSocket4.send($scope.match4json)}

    $scope.breakmatch1 = function() {myWebSocket1.send(createBreakMatchJson());}
    $scope.breakmatch2 = function() {myWebSocket2.send(createBreakMatchJson());}
    $scope.breakmatch3 = function() {myWebSocket3.send(createBreakMatchJson());}
    $scope.breakmatch4 = function() {myWebSocket4.send(createBreakMatchJson());}

    $scope.deliver1 = function() {
        var ar = [];
        ar.push(parseInt($scope.dev1recip))
        myWebSocket1.send(createDeliverJson(ar, $scope.dev1pl));
    }
    $scope.deliver2 = function() {
        var ar = [];
        ar.push(parseInt($scope.dev2recip))
        myWebSocket2.send(createDeliverJson(ar, $scope.dev2pl));
    }
    $scope.deliver3 = function() {
        var ar = [];
        ar.push(parseInt($scope.dev3recip))
        myWebSocket3.send(createDeliverJson(ar, $scope.dev3pl));
    }
    $scope.deliver4 = function() {
        var ar = [];
        ar.push(parseInt($scope.dev3recip))
        myWebSocket3.send(createDeliverJson(ar, $scope.dev3pl));
    }

    $scope.connect1();
    $scope.connect2();
    $scope.connect3();
    $scope.connect4();

}]);
