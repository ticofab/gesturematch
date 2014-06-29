function createDisconnectJson() {
    return JSON.stringify({"type":"disconnect"});
}

function createBreakMatchJson(groupId) {
    return JSON.stringify({"type":"leaveGroup", "groupId": groupId});
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
                        "criteria":nv[5],
                        "latitude":parseFloat(nv[0]),
                        "longitude":parseFloat(nv[1]),
                        "areaStart":nv[2],
                        "areaEnd":nv[3],
                        "equalityParam":nv[4]
                    }
    return JSON.stringify(matchRequest)
}
