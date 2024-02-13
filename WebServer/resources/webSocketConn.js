"use strict";

window.addEventListener('load', function () {
    let ws = new WebSocket("ws://localhost:8080");
    let wsOpen = false;
    ws.onopen = handleOpenCB;
    ws.onmessage = handleMsgCB;

    let userNameInput = document.getElementById("username");
    let roomNameInput = document.getElementById("roomName");
    let joinBtn = document.getElementById("join");
    let msgInput = document.getElementById("inputBar");
    let sendBtn = document.getElementById("sendBtn");
    let redBox = document.getElementById("rightRectangle");
    let yellowBox = document.getElementById("leftRectangle");
    let leaveBtn = document.getElementById("leave");
    let username;
    let roomName;
    let sentMsg;


    userNameInput.addEventListener("keypress", function (event) {
        if (event.key === "Enter") {
            username = userNameInput.value;
            for (let char in username) {
                // console.log(char);
                if (char < 'a' | char > 'z') {
                    // window.alert("only accept lowercase letters")
                    break;
                }
            }
            // console.log(username);
        }
    })
    roomNameInput.addEventListener("keypress", function (event) {
        if (event.key === "Enter") {
            roomName = roomNameInput.value;
            for (let char in roomName) {
                // console.log(char);
                if (char < 'a' | char > 'z') {
                    // window.alert("only accept lowercase letters")
                    break;
                }
            }
            // console.log(roomName);
        }
    })
    msgInput.addEventListener("keypress", function (event) {
        if (event.key === "Enter") {
            sentMsg = msgInput.value;
            // console.log(sentMsg);
        }
    })

    joinBtn.addEventListener("click", function (event) {
        if (wsOpen) {
            ws.send("join " + username + " " + roomName);

        }
    })

    sendBtn.addEventListener("click", function (event) {
        if (wsOpen) {
            ws.send("message " + sentMsg);
        }
    })

    leaveBtn.addEventListener("click", function () {
        if (wsOpen) {
            ws.send("leave");
        }
    })

    function handleOpenCB() {
        wsOpen = true;

    }

    function handleMsgCB(event) {
        let uname;
        let uroom;
        let msg = event.data;
        console.log(msg);


        let msgJSON = JSON.parse(msg);
        uname = msgJSON.user;
        uroom = msgJSON.room;
        console.log(msgJSON);

        if (msgJSON.type === "join") {
            let ppl = document.createElement("p");
            let newText = document.createElement("p");
            newText.textContent = uname + " joins " + uroom;
            ppl.textContent = uname + " is in the room";
            yellowBox.appendChild(ppl);
            redBox.appendChild(newText);

        } else if (msgJSON.type === "leave") {
            let newText = document.createElement("p");
            let pplText = document.createElement("p");
            newText.textContent = uname + " leaves " + uroom;
            pplText.textContent = uname + " left";
            yellowBox.appendChild(pplText);
            redBox.appendChild(newText);
        } else if (msgJSON.type === "message") {
            let newText = document.createElement("p");
            newText.textContent = uname + ": " + msgJSON.message;
            redBox.appendChild(newText);
        }
// What is event?
// It contains the data about the message (event) that caused this //callback to be called.
    }

})
