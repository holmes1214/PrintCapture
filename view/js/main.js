/**
 * Created by holmes1214 on 09/11/2017.
 */
var backgroundNo="1";
$(document).on("click","#frame-button-layer",function () {
    $("#options-layer").fadeIn();
});

$(document).on("click","#capture-button-layer",function () {
    $("#stream-layer").show();
    $("#capture-button-layer").hide();
    $("#frame-button-layer").hide();
    $("#countdown-layer").show();
    for(var i=0;i<6;i++){
       setTimeout(setNumber,i*1000);
    }
});
var countdown=5;
function setNumber(){
    if(countdown>0){
        console.log(countdown);
        $(".countdown").hide();
        $("#countdown"+countdown+"-layer").show();
        countdown--;
    }else {
        $(".countdown").hide();
        $("#image-layer").show();
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
function uploadImg() {
    document.getElementById("image-layer").toBlob(function (blob) {
        console.log(blob.size)
        var formData = new FormData();
        formData.append("imageData",blob);
        formData.append("backNumber",backgroundNo);
        $.ajax({
            type: 'POST',
            url:  "http://127.0.0.1:8080/capture/upload",
            data: formData,
            processData: false,
            contentType: false,
            success: function (msg) {
                console.log(msg);
            },
            error: function (a,b,c) {
                console.log(c);
            }
        });
    });

}

$(document).on("click",".option",function () {
    $("#options-layer").fadeOut();
    backgroundNo=$(this).attr("background-number");
    $("#cover-layer").css("background-image","'img/front"+backgroundNo+".png'");
});



$(document).ready(function () {
    $("#cover-layer").css("background-image","url('img/front1.png')");

    for(var i=1;i<6;i++){
        $("#countdown"+i+"-layer").css("background-image","url('img/"+i+".png')").hide();
    }
    $("#printer-layer").css("background-image","url('img/printer.png')").hide();
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