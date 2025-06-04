let pageReady = false;

// 1. When the DOM is ready, mark page as ready and show/hide loader accordingly
$(function() {
    pageReady = true;
    if ($.active > 0) {
        $("div.se-pre-con").show();
    } else {
        $("div.se-pre-con").fadeOut("slow");
    }
});

// 2. Show loader when any global AJAX starts, but only if page is ready
$(document).on("ajaxStart", function() {
    if (pageReady) {
        $("div.se-pre-con").show();
    }
});

// 3. Hide loader when all global AJAX have stopped, but only if page is ready
$(document).on("ajaxStop", function() {
    if (pageReady) {
        $("div.se-pre-con").fadeOut("slow");
    }
});
