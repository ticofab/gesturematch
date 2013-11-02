var myWebSocket1, myWebSocket2, myWebSocket3, myWebSocket4;

angular.module('app', []).controller('myctrl', function($scope) {
    // controls the disabling / enabling of the buttons
    $scope.isDisabled1 = true;
    $scope.isDisabled2 = true;
    $scope.isDisabled3 = true;
    $scope.isDisabled4 = true;

    // sets the initial values for the connection params
    $scope.ep1 = {title: "ws://localhost:9000/request?requestType=contact&latitude=12.00&longitude=12.0001&swipeStart=4&swipeEnd=3&deviceId=123&payload=payuno&equalityParam1=uno"};
    $scope.ep2 = {title: "ws://localhost:9000/request?requestType=contact&latitude=12.00&longitude=12.0001&swipeStart=2&swipeEnd=4&deviceId=456&payload=paydue&equalityParam1=uno"};
    $scope.ep3 = {title: "ws://localhost:9000/request?requestType=contact&latitude=12.00&longitude=12.0001&swipeStart=4&swipeEnd=1&deviceId=987&payload=paytre&equalityParam1=uno"};
    $scope.ep4 = {title: "ws://localhost:9000/request?requestType=contact&latitude=12.00&longitude=12.0001&swipeStart=0&swipeEnd=4&deviceId=654&payload=payquattro&equalityParam1=uno"};


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

    // websockets connection stuff
    function onM(evt) {console.log("got data: " + evt.data);}
    function onO(id, evt) {
        // here "id" is a string. equivalent to
        //   $scope.isDisabled1 === $scope["isDisabled1"]
        $scope[id] = false;
        console.log("Connection open ...");
        $scope.$apply();
    }
    function onC(evt) {console.log("Connection closed.");}

    $scope.connect1 = function() {
        myWebSocket1 = new WebSocket($scope.ep1.title);
        myWebSocket1.onmsg = onM;

        // onO.bind creates a new function with only one parameter,
        //  and the first one will be set as "isDisabled1"
        myWebSocket1.onopen = onO.bind(null, "isDisabled1");
        myWebSocket1.onclose = onC;
    };

    $scope.connect2 = function() {
        myWebSocket2 = new WebSocket($scope.ep2.title);
        myWebSocket2.onmsg = onM;
        myWebSocket2.onopen = onO.bind(null, "isDisabled2");
        myWebSocket2.onclose = onC;
    };

    $scope.connect3 = function() {
        myWebSocket3 = new WebSocket($scope.ep3.title);
        myWebSocket3.onmsg = onM;
        myWebSocket3.onopen = onO.bind(null, "isDisabled3");
        myWebSocket3.onclose = onC;
    };

    $scope.connect4 = function() {
        myWebSocket4 = new WebSocket($scope.ep4.title);
        myWebSocket4.onmsg = onM;
        myWebSocket4.onopen = onO.bind(null, "isDisabled4");
        myWebSocket4.onclose = onC;
    };

});

