var myApp = angular.module('app',[]);

myApp.controller('paramTable', ['$scope', function($scope) {

    initStuff();

    $scope.$watch("serverUrl", function() {
      $scope.openWSUrl =  "ws://" + $scope.serverUrl + "/openWS"
  })

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
        myWebSocket1 = new WebSocket($scope.openWSUrl);
        myWebSocket1.onmessage = onM.bind(null, "dev1msg");

        // onO.bind creates a new function with only one parameter,
        //  and the first one will be set as "isDisabled1"
        myWebSocket1.onopen = onO.bind(null, "isDisabled1");
        myWebSocket1.onclose = onC.bind(null, "isDisabled1");
    };

    $scope.connect2 = function() {
        myWebSocket2 = new WebSocket($scope.openWSUrl);
        myWebSocket2.onmessage = onM.bind(null, "dev2msg");;
        myWebSocket2.onopen = onO.bind(null, "isDisabled2");
        myWebSocket2.onclose = onC.bind(null, "isDisabled2");
    };

    $scope.connect3 = function() {
        myWebSocket3 = new WebSocket($scope.openWSUrl);
        myWebSocket3.onmessage = onM.bind(null, "dev3msg");;
        myWebSocket3.onopen = onO.bind(null, "isDisabled3");
        myWebSocket3.onclose = onC.bind(null, "isDisabled3");
    };

    $scope.connect4 = function() {
        myWebSocket4 = new WebSocket($scope.openWSUrl);
        myWebSocket4.onmessage = onM.bind(null, "dev4msg");
        myWebSocket4.onopen = onO.bind(null, "isDisabled4");
        myWebSocket4.onclose = onC.bind(null, "isDisabled4");
    };

    $scope.disconnect1 = function() {myWebSocket1.send(createDisconnectJson());};
    $scope.disconnect2 = function() {myWebSocket2.send(createDisconnectJson());};
    $scope.disconnect3 = function() {myWebSocket3.send(createDisconnectJson());};
    $scope.disconnect4 = function() {myWebSocket4.send(createDisconnectJson());};

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
        ar.push(3)
        myWebSocket1.send(createDeliverJson(ar, $scope.dev1pl));
    }
    $scope.deliver2 = function() {myWebSocket2.send(createDeliverJson($scope.dev2deliver));}
    $scope.deliver3 = function() {myWebSocket3.send(createDeliverJson($scope.dev3deliver));}
    $scope.deliver4 = function() {myWebSocket4.send(createDeliverJson($scope.dev4deliver));}

    function initStuff() {

        $scope.serverUrl = "localhost:9000"
        //$scope.serverUrl = "thawing-escarpment-8488.herokuapp.com"

        $scope.openWSUrl = "ws://" + $scope.serverUrl + "/openWS"

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
        $scope.dev2se = "inner"
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

        $scope.dev1recip = "1"
        $scope.dev2recip = "0"
        $scope.dev3recip = "2"
        $scope.dev4recip = "3"
    }

}]);
