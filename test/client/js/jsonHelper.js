function createDisconnectJson() {
    return JSON.stringify({"type":"disconnect"});
}

function createBreakMatchJson() {
    return JSON.stringify({"type":"breakMatch"});
}

function createDeliverJson(recipients, payload) {
    var deliver = {"type":"delivery",
                    "payload": payload,
                    "recipients": []}
    for (var i in recipients) {
        var recip = recipients[i]
        deliver.recipients.push({"recipient":recip})
    }
    return JSON.stringify(deliver)
}

// nv are the parametes in the order in which they're observed...
function createMatchJson(nv) {
    var matchRequest = {"type":"match",
                        "criteria":nv[10],
                        "apiKey":nv[8],
                        "appId":nv[9],
                        "latitude":nv[2],
                        "longitude":nv[3],
                        "areaStart":nv[4],
                        "areaEnd":nv[5],
                        "deviceId":nv[1],
                        "equalityParam":nv[7]
                    }
    return JSON.stringify(matchRequest)
}
