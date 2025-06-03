$.widget("ros.projectFormWidget", {
    options: {
        message: {}
    },

    _create: function () {
        console.log("Dashboard form widget loaded successfully");

        let self = this;
        self.el = {};
        self.el.button = $("#new-project-btn");

        self.initNewProjectModal();
    },

    initNewProjectModal: function () {
        let self = this;

        self.el.button.on("click", function () {
            const modalHtml = `
                <form id="new-project-form">
                    <div id="project-error-box" class="alert alert-danger d-none"></div>

                    <div class="form-group mb-3">
                        <label for="projectName">Project Name</label>
                        <input type="text" class="form-control" id="projectName" name="name" required>
                    </div>
                    <div class="form-group mb-3">
                        <label for="projectDescription">Description</label>
                        <textarea class="form-control" id="projectDescription" name="description" rows="3" required></textarea>
                    </div>
                </form>
            `;

            let modal = bootbox.dialog({
                title: "Create New Project",
                message: modalHtml,
                closeButton: false,
                buttons: {
                    cancel: {
                        label: "Cancel",
                        className: "btn-secondary"
                    },
                    save: {
                        label: "Create",
                        className: "btn-success",
                        callback: function () {
                            const $form = $("#new-project-form");
                            const $errorBox = $("#project-error-box");

                            if (!$form.valid()) {
                                return false;
                            }

                            const projectDto = {
                                name: $("#projectName").val(),
                                description: $("#projectDescription").val()
                            };

                            $.ajax({
                                url: "/api/create-project",
                                type: "POST",
                                contentType: "application/json",
                                data: JSON.stringify(projectDto),
                                beforeSend: function () {
                                    Ros.APP.startLoading();
                                },
                                success: function (response) {
                                    if (response && response.data) {
                                        window.location.href = `/project/${response.data}/document-list`;
                                    } else {
                                        $errorBox
                                            .removeClass("d-none")
                                            .html("<strong>Unexpected response from server.</strong>");
                                    }
                                },
                                error: function (xhr) {
                                    let response = xhr.responseJSON;
                                    if (response && response.data && Array.isArray(response.data)) {
                                        const errorMessages = response.data.map(err => `<li>${err}</li>`).join("");
                                        $errorBox
                                            .removeClass("d-none")
                                            .html(`<strong>${response.message}</strong><ul>${errorMessages}</ul>`);
                                    } else {
                                        $errorBox
                                            .removeClass("d-none")
                                            .html(`<strong>Unexpected error occurred.</strong>`);
                                    }
                                }
                            }).always(function () {
                                Ros.APP.stopLoading();
                            });

                            return false;
                        }
                    }
                }
            });

            $("#new-project-form").validate({
                errorElement: 'div',
                errorClass: 'text-danger mt-1',
                errorPlacement: function (error, element) {
                    $(element).siblings('.text-danger').remove();
                    error.insertAfter(element);
                }
            });
        });
    }
});
