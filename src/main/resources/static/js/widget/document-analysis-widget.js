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
        self.parseMarkDown(self.el.summaryDiv, self.el.summaryDiv.text());
    },
    loadAnalysisSummary: function () {
        const self = this;

        if (!self.options.isAnalyzed) {
            $.ajax({
                url: `/api/analysis/${self.options.analysisId}/summary`,
                type: "POST",
                beforeSend: function () {
                    Ros.APP.startLoading();
                },
                success: function (response) {
                    if (response && response.data) {
                        self.parseMarkDown(self.el.summaryDiv, response.data);
                    } else {
                        self.el.summaryDiv.html('Failed to generate analysis. Please reload page and try again.');
                    }
                },
                error: function (xhr) {
                    self.el.summaryDiv.html('Failed to generate analysis. Please reload page and try again.');
                }
            }).always(function () {
                Ros.APP.stopLoading();
            });
        }
    },
    parseMarkDown: function (containingElement, markdownContent) {
        const container = $(containingElement);

        let result = markdownContent || container.html();
        result = marked.parse(result);
        // result = DOMPurify.sanitize(result);

        container.html(result);
    }
});
