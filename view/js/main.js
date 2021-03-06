/**
 * Created by holmes1214 on 09/11/2017.
 */
var backgroundNo="1";
$(document).on("click","#frame-button-layer",function () {
    $(".cover").fadeOut();
    if(backgroundNo==="1"){
        backgroundNo="2";
    }else if(backgroundNo==="2"){
        backgroundNo="3";
    }else{
        backgroundNo="1";
    }
    $("#cover"+backgroundNo+"-layer").fadeIn();
});

$(document).on("click","#capture-button-layer",function () {
    $("#capture-button-layer").fadeOut();
    $("#frame-button-layer").fadeOut();
    $("#countdown-layer").fadeIn();
    for(var i=0;i<6;i++){
       setTimeout(setNumber,i*1000);
    }
});
var countdown=5;
function setNumber(){
    if(countdown>0){
        console.log(countdown);
        $(".countdown").fadeOut();
        $("#countdown"+countdown+"-layer").fadeIn();
        countdown--;
    }else {
        $(".countdown").fadeOut();
        $("#image-layer").fadeIn();
        var video = document.getElementById("stream-layer");
        var canvas=document.getElementById("image-layer");
        var ctx=canvas.getContext("2d");
        var width = video.width;
        var height = video.height;
        canvas.width = width;
        canvas.height = height;
        ctx.drawImage(video, 0, 0,width,height);
        countdown=5;
        uploadImg();
    }
}

var fileName="";
function uploadImg() {
    document.getElementById("image-layer").toBlob(function (blob) {
        console.log(blob.size)
        var formData = new FormData();
        formData.append("imageData",blob);
        formData.append("backNumber",backgroundNo);
        formData.append("suffix",".png");
        $.ajax({
            type: 'POST',
            url:  "http://127.0.0.1:8080/capture/upload",
            data: formData,
            crossDomain: true,
            processData: false,
            contentType: false,
            success: function (msg) {
                console.log(msg);
                if(msg.qrCode.meta.code==0){
                    $("#qrcode-layer").fadeIn();
                    var url=msg.qrCode.data;
                    $("#qr-code").css("background-image","url('"+url+"')");
                    fileName=msg.fileName;
                }else {
                }
            },
            error: function (a,b,c) {
                console.log(c);
            }
        });
    });
}
$(document).on("click","#return-to-capture",function () {
    $("#qrcode-layer").fadeOut();
    $("#image-layer").fadeOut();
    $("#capture-button-layer").fadeIn();
    $("#frame-button-layer").fadeIn();
});

$(document).on("click","#print-pic",function () {
    $("#printer-layer").fadeIn();
    $.ajax({
        type: 'POST',
        url:  "http://127.0.0.1:8080/capture/print?fileName="+fileName,
        crossDomain: true,
        processData: false,
        contentType: false,
        success: function (msg) {
            console.log(msg);
            $("#printer-layer").fadeOut();
            $("#qrcode-layer").fadeOut();
            $("#image-layer").fadeOut();
            $("#capture-button-layer").fadeIn();
            $("#frame-button-layer").fadeIn();
        },
        error: function (a,b,c) {
            console.log(c);
        }
    });
});

$(document).on("click",".option",function () {
    $("#options-layer").fadeOut();
    backgroundNo=$(this).attr("background-number");
    $(".cover").fadeOut();
    $("#cover"+backgroundNo+"-layer").fadeIn();
});



$(document).ready(function () {
    $(".cover").fadeOut();
    $("#cover1-layer").fadeIn();
    for(var i=1;i<6;i++){
        $("#countdown"+i+"-layer").css("background-image","url('img/"+i+".png')").fadeOut();
    }
    $("#printer-layer").css("background-image","url('img/printer.png')").fadeOut();
    var video = document.getElementById("stream-layer");
    var constraints = window.constraints = {
        audio: false,
        video: { width: 1620, height: 1080 }
    };
    navigator.mediaDevices.getUserMedia(constraints).then(function(stream) {
            var videoTracks = stream.getVideoTracks();
            console.log('Got stream with constraints:', constraints);
            console.log('Using video device: ' + videoTracks[0].label);
            stream.onremovetrack = function() {
                console.log('Stream ended');
            };
            window.stream = stream; // make variable available to browser console
            video.srcObject = stream;
            video.src = URL.createObjectURL(stream);   // 将获取到的视频流对象转换为地址
            video.play();
        }).catch(function(error) {
            if (error.name === 'ConstraintNotSatisfiedError') {
                errorMsg('The resolution ' + constraints.video.width.exact + 'x' +
                    constraints.video.width.exact + ' px is not supported by your device.');
            } else if (error.name === 'PermissionDeniedError') {
                errorMsg('Permissions have not been granted to use your camera and ' +
                    'microphone, you need to allow the page access to your devices in ' +
                    'order for the demo to work.');
            }
            errorMsg('getUserMedia error: ' + error.name, error);
        });
});

function errorMsg(msg, error) {
    if (typeof error !== 'undefined') {
        console.error(error);
    }
}