/**
 * Created by holmes1214 on 09/11/2017.
 */
var backgroundNo="1";
$(document).on("click","#frame-button-layer",function () {
    $("#options-layer").fadeIn();
});

$(document).on("click",".option",function () {
    $("#options-layer").fadeOut();
    backgroundNo=$(this).attr("background-number");
    $("#cover-layer").css("background-image","'../img/front"+backgroundNo+".png'");
});