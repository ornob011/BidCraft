Ros = window.Ros || {};

Ros.APP = {
    startLoading: function () {
        $(document).find("div.se-pre-con").show();
    },
    stopLoading: function () {
        $(document).find("div.se-pre-con").fadeOut("slow");
    },
};
