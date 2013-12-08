function createDisconnectJson() {
    return JSON.stringify({"type":"disconnect"});
}

function createBreakMatchJson() {
    return JSON.stringify({"type":"leaveGroup", "groupId":"xxEXAMPLEGROUPxx"});
}

function createDeliverJson(recipients, payload) {
    var deliver = {"type":"delivery",
                    "groupId":"WRONGGROUP",
                    "payload": payload,
                    "recipients": []}
    for (var i in recipients) {
        var recip = recipients[i]
        deliver.recipients.push(recip)
    }
    return JSON.stringify(deliver)
}

// nv are the parametes in the order in which they're observed...
function createMatchJson(nv) {
    var matchRequest = {"type":"match",
                        "criteria":nv[10],
                        "apiKey":nv[8],
                        "appId":nv[9],
                        "latitude":parseFloat(nv[2]),
                        "longitude":parseFloat(nv[3]),
                        "areaStart":nv[4],
                        "areaEnd":nv[5],
                        "deviceId":nv[1],
                        "equalityParam":nv[7]
                    }
    return JSON.stringify(matchRequest)
}
