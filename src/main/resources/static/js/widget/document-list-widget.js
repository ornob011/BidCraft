$.widget("ros.documentListWidget", {
    options: {
        projectId: null,
    },

    _create: function () {
        const self = this;
        self.el = {};
        self.el.fileTable = $("tbody");
        self.el.uploadForm = $("#uploadForm");
        self.el.modal = $("#myModal");

        self._bindUploadForm();
        self._bindDeleteHandler();
    },

    formatDate: function (dateString) {
        const date = new Date(dateString);
        return date.toLocaleString();
    },

    refreshFileTable: function (projectId) {
        const self = this;
        $.ajax({
            url: '/get-files/' + projectId,
            type: 'GET',
            success: function (files) {
                const $tbody = self.el.fileTable;
                $tbody.empty();

                if (files.length === 0) {
                    $tbody.append(`
                        <tr class="text-center">
                            <td colspan="4" class="fw-bold text-danger">No Data Found!</td>
                        </tr>
                    `);
                    return;
                }

                files.forEach(function (file) {
                    $tbody.append(`
                        <tr>
                            <td>
                                <a href="/api/document-viewer/${file.id}" class="text-dark fw-medium" target="_blank">
                                    <i class="mdi mdi-file-document font-size-16 align-middle text-primary me-2"></i>
                                    ${file.name}
                                </a>
                            </td>
                            <td>${self.formatDate(file.updatedAt)}</td>
                            <td>${Ros.ENUM.UploadedDocumentType[file?.type] || ''}</td>
                            <td class="text-center">
                                <a href="/project/${file.projectId}/analysis/document?documentId=${file.id}" class="btn btn-outline-success">
                                    <i class="fas fa-brain me-2"></i> Analyze
                                </a>
                                <button type="button" class="btn btn-outline-danger btn-delete-file" data-file-id="${file.id}">
                                    <i class="fas fa-trash-alt me-2"></i> Delete
                                </button>
                            </td>
                        </tr>
                    `);
                });
            },
            error: function (xhr, status, error) {
                bootbox.alert({
                    closeButton: false,
                    title: '<span class="text-danger"><i class="fas fa-exclamation-circle me-2"></i>Error</span>',
                    message: 'Sorry, we could not load your files at the moment. Please try again in a few moments or refresh the page.'
                });
            }
        });
    },

    _bindUploadForm: function () {
        const self = this;
        self.el.uploadForm.off('submit.rosDocList').on('submit.rosDocList', function (e) {
            e.preventDefault();
            const formData = new FormData(this);

            $.ajax({
                type: 'POST',
                url: $(this).attr('action'),
                data: formData,
                processData: false,
                contentType: false,
                cache: false,
                success: function (response) {
                    self.el.uploadForm[0].reset();
                    self.el.modal.modal('hide');
                    self.refreshFileTable(self.options.projectId);
                },
                error: function (xhr, status, error) {
                    bootbox.alert({
                        closeButton: false,
                        title: '<span class="text-danger"><i class="fas fa-exclamation-circle me-2"></i>Error</span>',
                        message: 'Sorry, your file could not be uploaded right now. Please try again in a few moments.'
                    });
                    self.el.uploadForm[0].reset();
                    self.el.modal.modal('hide');
                }
            });
        });
    },

    _bindDeleteHandler: function () {
        const self = this;
        self.el.fileTable.off('click.rosDocList').on('click.rosDocList', '.btn-delete-file', function (e) {
            e.preventDefault();
            const fileId = $(this).data('file-id');

            bootbox.confirm({
                closeButton: false,
                title: '<span class="text-danger"><i class="fas fa-exclamation-circle me-2"></i>Delete Document</span>',
                message: 'Are you sure you want to delete this file?',
                buttons: {
                    confirm: {
                        label: '<i class="fas fa-trash-alt me-2"></i> Delete',
                        className: 'btn-danger'
                    },
                    cancel: {
                        label: 'Cancel',
                        className: 'btn-secondary'
                    }
                },
                callback: function (result) {
                    if (result) {
                        $.ajax({
                            url: '/api/delete-file/' + fileId,
                            type: 'DELETE',
                            success: function () {
                                self.refreshFileTable(self.options.projectId);
                            },
                            error: function (xhr, status, error) {
                                bootbox.alert({
                                    closeButton: false,
                                    title: '<span class="text-danger"><i class="fas fa-exclamation-circle me-2"></i>Error</span>',
                                    message: 'Sorry, we could not delete this file right now. Please try again later.'
                                });
                            }
                        });
                    }
                }
            });
        });
    }
});
