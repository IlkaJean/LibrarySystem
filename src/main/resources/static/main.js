// Library Management System - Main JavaScript

// Global configuration
const API_BASE_URL = '/api';

// Utility functions
const utils = {
    // Format date
    formatDate: function(dateString) {
        if (!dateString) return '';
        const date = new Date(dateString);
        return date.toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'long',
            day: 'numeric'
        });
    },

    // Format datetime
    formatDateTime: function(dateString) {
        if (!dateString) return '';
        const date = new Date(dateString);
        return date.toLocaleString('en-US', {
            year: 'numeric',
            month: 'long',
            day: 'numeric',
            hour: 'numeric',
            minute: 'numeric'
        });
    },

    // Show toast notification
    showToast: function(message, type = 'info') {
        const toastHtml = `
            <div class="toast align-items-center text-white bg-${type} border-0" role="alert" aria-live="assertive" aria-atomic="true">
                <div class="d-flex">
                    <div class="toast-body">
                        ${message}
                    </div>
                    <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
                </div>
            </div>
        `;

        const toastContainer = $('#toast-container');
        if (toastContainer.length === 0) {
            $('body').append('<div id="toast-container" class="toast-container position-fixed bottom-0 end-0 p-3"></div>');
        }

        const $toast = $(toastHtml);
        $('#toast-container').append($toast);

        const bsToast = new bootstrap.Toast($toast[0]);
        bsToast.show();

        $toast.on('hidden.bs.toast', function() {
            $(this).remove();
        });
    },

    // Confirm dialog
    confirm: function(message, onConfirm) {
        if (confirm(message)) {
            onConfirm();
        }
    },

    // Make AJAX request with auth token
    ajax: function(options) {
        const token = localStorage.getItem('token');
        const defaultOptions = {
            headers: {}
        };

        if (token) {
            defaultOptions.headers['Authorization'] = `Bearer ${token}`;
        }

        const mergedOptions = $.extend(true, defaultOptions, options);
        return $.ajax(mergedOptions);
    }
};

// Authentication functions
const auth = {
    isAuthenticated: function() {
        return !!localStorage.getItem('token');
    },

    logout: function() {
        localStorage.removeItem('token');
        window.location.href = '/login';
    },

    getToken: function() {
        return localStorage.getItem('token');
    },

    setToken: function(token) {
        localStorage.setItem('token', token);
    }
};

// Global event handlers
$(document).ready(function() {
    // Initialize tooltips
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function(tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });

    // Initialize popovers
    const popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'));
    popoverTriggerList.map(function(popoverTriggerEl) {
        return new bootstrap.Popover(popoverTriggerEl);
    });

    // Handle form validation
    const forms = document.querySelectorAll('.needs-validation');
    Array.prototype.slice.call(forms).forEach(function(form) {
        form.addEventListener('submit', function(event) {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add('was-validated');
        }, false);
    });

    // Add CSRF token to AJAX requests if available
    const token = $("meta[name='_csrf']").attr("content");
    const header = $("meta[name='_csrf_header']").attr("content");
    if (token && header) {
        $(document).ajaxSend(function(e, xhr, options) {
            xhr.setRequestHeader(header, token);
        });
    }

    // Auto-hide alerts after 5 seconds
    $('.alert:not(.alert-danger)').delay(5000).fadeOut('slow');

    // Loading indicator for AJAX requests
    $(document).ajaxStart(function() {
        $('#loading-indicator').show();
    }).ajaxStop(function() {
        $('#loading-indicator').hide();
    });

    // Handle unauthorized responses
    $(document).ajaxError(function(event, jqXHR, ajaxSettings, thrownError) {
        if (jqXHR.status === 401) {
            auth.logout();
        } else if (jqXHR.status === 403) {
            utils.showToast('You do not have permission to perform this action', 'danger');
        }
    });
});

// Common UI interactions
const ui = {
    // Show loading state
    showLoading: function(element) {
        $(element).addClass('loading');
        $(element).append('<div class="loading-overlay"><i class="fas fa-spinner fa-spin"></i></div>');
    },

    // Hide loading state
    hideLoading: function(element) {
        $(element).removeClass('loading');
        $(element).find('.loading-overlay').remove();
    },

    // Refresh page data
    refreshPage: function() {
        window.location.reload();
    },

    // Go back
    goBack: function() {
        window.history.back();
    }
};

// Make utils and auth available globally
window.utils = utils;
window.auth = auth;
window.ui = ui;