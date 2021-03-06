function initStuff(scope) {

    scope.serverUrl = "localhost"
    scope.protocol = "ws"
    scope.port = "9000"
    scope.openWSUrl = scope.protocol + "://" + scope.serverUrl + ":" + scope.port + "/v1/open"

    scope.dev1criteria = "pinch"
    scope.dev2criteria = "pinch"
    scope.dev3criteria = "swipe"
    scope.dev4criteria = "swipe"

    scope.dev1devid = "idjs1"
    scope.dev2devid = "idjs2"
    scope.dev3devid = "idjs3"
    scope.dev4devid = "idjs4"

    scope.dev1lat = "12.00"
    scope.dev2lat = "12.00"
    scope.dev3lat = "12.00"
    scope.dev4lat = "12.00"

    scope.dev1lon = "12.00"
    scope.dev2lon = "12.00"
    scope.dev3lon = "12.00"
    scope.dev4lon = "12.00"

    scope.dev1ss = "inner"
    scope.dev2ss = "inner"
    scope.dev3ss = "top"
    scope.dev4ss = "left"

    scope.dev1se = "right"
    scope.dev2se = "left"
    scope.dev3se = "right"
    scope.dev4se = "inner"

    scope.dev1pl = "payload_from_1"
    scope.dev2pl = "payload_from_2"
    scope.dev3pl = "payload_from_3"
    scope.dev4pl = "payload_from_4"

    scope.dev1ep = "blue"
    scope.dev2ep = "blue"
    scope.dev3ep = "blue"
    scope.dev4ep = "blue"

    scope.dev1recip = "0"
    scope.dev2recip = "1"
    scope.dev3recip = "2"
    scope.dev4recip = "3"

    scope.dev1groupId = "none"
    scope.dev2groupId = "none"
    scope.dev3groupId = "none"
    scope.dev4groupId = "none"

    scope.dev1idInGroup = "n/a"
    scope.dev2idInGroup = "n/a"
    scope.dev3idInGroup = "n/a"
    scope.dev4idInGroup = "n/a"

}