function createDisconnectJson() {
    return JSON.stringify({"type":"disconnect"});
}

function createBreakMatchJson() {
    return JSON.stringify({"type":"leaveGroup", "groupId":"xxEXAMPLEGROUPxx"});
}

function createDeliverJson(recipients, payload, groupId) {
    var deliveryId = Math.random().toString(36).substring(2, 7);
    var deliver = {"type":"delivery",
                    "groupId": groupId,
                    "payload": payload,
                    "deliveryId": deliveryId,
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
                        "criteria":nv[6],
                        "latitude":parseFloat(nv[1]),
                        "longitude":parseFloat(nv[2]),
                        "areaStart":nv[3],
                        "areaEnd":nv[4],
                        "equalityParam":nv[5]
                    }
    return JSON.stringify(matchRequest)
}
