Ros = window.Ros || {};

Ros.APP = {
    activeAjaxCalls: 0,
    isPageReady: false,

    checkAndHideLoader: function () {
        if (this.isPageReady && this.activeAjaxCalls <= 0) {
            $(document).find("div.se-pre-con").fadeOut("slow");
        }
    },

    startLoading: function () {
        if (this.activeAjaxCalls === 0) {
            $(document).find("div.se-pre-con").stop(true, true).show();
        }
        this.activeAjaxCalls++;
    },

    stopLoading: function () {
        this.activeAjaxCalls--;
        if (this.activeAjaxCalls < 0) {
            this.activeAjaxCalls = 0;
        }
        this.checkAndHideLoader();
    },

    markPageReady: function () {
        this.isPageReady = true;
        this.checkAndHideLoader();
    }
};
