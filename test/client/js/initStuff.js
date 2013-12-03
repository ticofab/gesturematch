function initStuff(scope) {

    scope.serverUrl = "localhost:9000"
    //scope.serverUrl = "thawing-escarpment-8488.herokuapp.com"

    scope.openWSUrl = "ws://" + scope.serverUrl + "/openWS"

    scope.dev1rtype = "content"
    scope.dev2rtype = "content"
    scope.dev3rtype = "content"
    scope.dev4rtype = "content"

    scope.dev1criteria = "presence"
    scope.dev2criteria = "presence"
    scope.dev3criteria = "presence"
    scope.dev4criteria = "presence"

    scope.dev1apikey = "contact-api-key"
    scope.dev2apikey = "contact-api-key"
    scope.dev3apikey = "contact-api-key"
    scope.dev4apikey = "contact-api-key"

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
    scope.dev2ss = "left"
    scope.dev3ss = "left"
    scope.dev4ss = "left"

    scope.dev1se = "right"
    scope.dev2se = "inner"
    scope.dev3se = "right"
    scope.dev4se = "inner"

    scope.dev1pl = "[{\"type\":\"NAME\",\"content\":{\"value\":\"Fabio1\"}}]"
    scope.dev2pl = "[{\"type\":\"NAME\",\"content\":{\"value\":\"Fabio2\"}}]"
    scope.dev3pl = "[{\"type\":\"NAME\",\"content\":{\"value\":\"Fabio3\"}}]"
    scope.dev4pl = "[{\"type\":\"NAME\",\"content\":{\"value\":\"Fabio4\"}}]"

    scope.dev1ep = "uguale"
    scope.dev2ep = "uguale"
    scope.dev3ep = "uguale"
    scope.dev4ep = "uguale"

    scope.dev1appId = "123-ABC"
    scope.dev2appId = "123-ABC"
    scope.dev3appId = "123-ABC"
    scope.dev4appId = "123-ABC"

    scope.dev1recip = "1"
    scope.dev2recip = "0"
    scope.dev3recip = "2"
    scope.dev4recip = "3"
}