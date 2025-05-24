$.widget("ros.documentAnalysisWidget", {
    options: {
        analysisId: undefined,
        isAnalyzed: undefined,
        message: {}
    },

    _create: function () {
        console.log("Document analysis widget loaded successfully");

        let self = this;
        self.el = {};
        self.el.summaryDiv=$("#summaryDiv");
    },
    _init: function () {
        const self = this;

        self.loadAnalysisSummary();
    },
    loadAnalysisSummary: function () {
        const self = this;

        if (!self.options.isAnalyzed) {
            $.ajax({
                url: `/api/analysis/${self.options.analysisId}/summary`,
                type: "POST",
                success: function (response) {
                    if (response && response.data) {
                        self.el.summaryDiv.html(response.data);
                    } else {
                        self.el.summaryDiv.html('Failed to generate analysis. Please reload page and try again.');
                    }
                },
                error: function (xhr) {
                    self.el.summaryDiv.html('Failed to generate analysis. Please reload page and try again.');
                }
            });
        }
    }
});
