$.widget("ros.documentAnalysisWidget", {
    options: {
        analysisId: undefined,
        isAnalyzed: undefined,
        selectedSection: undefined,
        message: {}
    },

    _create: function () {
        console.log("Document analysis widget loaded successfully");

        let self = this;
        self.el = {};
        self.el.summaryDiv=$(".summaryDiv");
    },
    _init: function () {
        const self = this;

        self.loadAnalysisSummary();
    },
    loadAnalysisSummary: function () {
        const self = this;

        if (!self.options.isAnalyzed) {
            $.ajax({
                url: `/api/analysis/${self.options.analysisId}/summary${self.options.selectedSection ? '?section=' + self.options.selectedSection : ''}`,
                type: "POST",
                global: false,
                success: function (response) {
                    if (response && response.data) {
                        self.parseMarkDown(self.el.summaryDiv, response.data);
                    } else {
                        self.el.summaryDiv.html('Analysis not available.');
                    }
                },
                error: function (xhr) {
                    self.el.summaryDiv.html('Failed to generate analysis. Please reload page and try again.');
                }
            });
        } else {
            self.parseMarkDown(self.el.summaryDiv, self.el.summaryDiv.text());
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
