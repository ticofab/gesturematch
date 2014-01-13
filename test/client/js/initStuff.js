function initStuff(scope) {

    scope.serverUrl = "localhost:9000"
    //scope.serverUrl = "thawing-escarpment-8488.herokuapp.com"

    scope.openWSUrl = "ws://" + scope.serverUrl + "/open"

    scope.dev1rtype = "content"
    scope.dev2rtype = "content"
    scope.dev3rtype = "content"
    scope.dev4rtype = "content"

    scope.dev1criteria = "pinch"
    scope.dev2criteria = "pinch"
    scope.dev3criteria = "pinch"
    scope.dev4criteria = "presence"

    scope.dev1apikey = "limebamboo-swipematch-examples-android"
    scope.dev2apikey = "limebamboo-swipematch-examples-android"
    scope.dev3apikey = "abc"
    scope.dev4apikey = "def"

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
    scope.dev3ss = "inner"
    scope.dev4ss = "left"

    scope.dev1se = "right"
    scope.dev2se = "left"
    scope.dev3se = "left"
    scope.dev4se = "inner"

    scope.dev1pl = "[{\"type\":\"NAME\",\"content\":{\"value\":\"Fabio1\"}}]"
    scope.dev2pl = "[{\"type\":\"NAME\",\"content\":{\"value\":\"Fabio2\"}}]"
    scope.dev3pl = "[{\"type\":\"NAME\",\"content\":{\"value\":\"Fabio3\"}}]"
    scope.dev4pl = "[{\"type\":\"NAME\",\"content\":{\"value\":\"Fabio4\"}}]"

    scope.dev1ep = "blue"
    scope.dev2ep = "blue"
    scope.dev3ep = "uguale"
    scope.dev4ep = "uguale"

    scope.dev1appId = "swipepresence-example-android"
    scope.dev2appId = "swipepresence-example-android"
    scope.dev3appId = "swipeaim-example-android"
    scope.dev4appId = "swipeaim-example-android"

    scope.dev1recip = "1"
    scope.dev2recip = "0"
    scope.dev3recip = "2"
    scope.dev4recip = "3"
}