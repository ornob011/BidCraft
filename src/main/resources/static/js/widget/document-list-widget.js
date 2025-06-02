// Widget definition
$.widget("ros.documentListWidget", {
    options: {
        projectId: null,
        fileTableSelector: 'tbody',
        uploadFormSelector: '#uploadForm',
        modalSelector: '#myModal',
    },

    _create: function () {
        const self = this;

        // Cache commonly used elements
        self.el = {};
        self.el.fileTable = $(self.options.fileTableSelector);
        self.el.uploadForm = $(self.options.uploadFormSelector);
        self.el.modal = $(self.options.modalSelector);

        // Bind upload form submit
        self._bindUploadForm();

        // Initial table load
        if (self.options.projectId) {
            self.refreshFileTable(self.options.projectId);
        }
    },

    // Formats date string to local
    formatDate: function(dateString) {
        const date = new Date(dateString);
        return date.toLocaleString();
    },

    // Loads file list into table
    refreshFileTable: function(projectId) {
        const self = this;
        $.ajax({
            url: '/get-files/' + projectId,
            type: 'GET',
            success: function(files) {
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

                files.forEach(function(file) {
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
                                <a href="/project/${file.projectId}/analysis?documentId=${file.id}" class="btn btn-outline-success">
                                    <i class="fas fa-brain me-2"></i> Analyze
                                </a>
                                <button type="button" class="btn btn-outline-danger">
                                    <i class="fas fa-trash-alt me-2"></i> Delete
                                </button>
                            </td>
                        </tr>
                    `);
                });
            },
            error: function(xhr, status, error) {
                alert('Error refreshing table: ' + error);
            }
        });
    },

    // Binds the upload form submit event
    _bindUploadForm: function() {
        const self = this;
        self.el.uploadForm.off('submit.rosDocList').on('submit.rosDocList', function(e) {
            e.preventDefault();
            const formData = new FormData(this);

            $.ajax({
                type: 'POST',
                url: $(this).attr('action'),
                data: formData,
                processData: false,
                contentType: false,
                cache: false,
                success: function(response) {
                    // Reset form and modal
                    self.el.uploadForm[0].reset();
                    self.el.modal.modal('hide');
                    // Refresh file table
                    self.refreshFileTable(self.options.projectId);
                },
                error: function(xhr, status, error) {
                    alert('Error uploading file: ' + error);
                    self.el.uploadForm[0].reset();
                    self.el.modal.modal('hide');
                }
            });
        });
    }
});
