/**
 * Career Recommendation System for B.Tech CSE Students
 * Complete Vanilla JavaScript Frontend Engine
 * Handles Navigation, Session State, API Requests, Stepper, and Dynamic UI Rendering.
 */

const CareerApp = (function () {
    'use strict';

    // Current session cache
    let sessionData = null;

    /**
     * Determines application context path (e.g., "/CareerRecommendationSystem" or "")
     */
    function getContextPath() {
        const pathName = window.location.pathname;
        if (pathName.startsWith('/CareerRecommendationSystem')) {
            return '/CareerRecommendationSystem';
        }
        return '';
    }

    /**
     * Escapes HTML to prevent XSS in dynamic templates.
     */
    function escapeHtml(str) {
        if (str === null || str === undefined) return '';
        return String(str)
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#039;');
    }

    /**
     * Reusable JSON fetch wrapper with error handling and CSRF support
     */
    async function fetchJson(url, options = {}) {
        const defaultHeaders = {
            'Accept': 'application/json',
            'X-Requested-With': 'XMLHttpRequest'
        };

        if (sessionData && sessionData.csrfToken) {
            defaultHeaders['X-CSRF-Token'] = sessionData.csrfToken;
        }

        options.headers = Object.assign({}, defaultHeaders, options.headers || {});
        options.credentials = 'same-origin';

        try {
            const res = await fetch(url, options);

            // If session expired or unauthorized
            if (res.status === 401) {
                const data = await res.json().catch(() => ({}));
                if (data.redirect) {
                    window.location.href = data.redirect;
                    return null;
                }
            }

            if (!res.ok) {
                const errData = await res.json().catch(() => ({}));
                return { success: false, message: errData.message || `Server returned error (${res.status})` };
            }

            return await res.json();
        } catch (err) {
            console.error('[CareerApp] Fetch error:', err);
            return { success: false, message: 'Network connection error. Please verify your connection.' };
        }
    }

    /**
     * Fetches current session and initializes navigation bar across all pages
     */
    async function initSessionNavbar() {
        const ctx = getContextPath();
        const data = await fetchJson(ctx + '/api/session');
        if (data) {
            sessionData = data;
        }

        // Fill CSRF tokens in forms
        if (sessionData && sessionData.csrfToken) {
            document.querySelectorAll('input[name="csrfToken"], input.csrf-field, #csrfTokenProfile, #csrfTokenPassword').forEach(input => {
                input.value = sessionData.csrfToken;
            });
        }

        renderNavbar();
        return sessionData;
    }

    /**
     * Renders standard responsive navigation links and user actions
     */
    function renderNavbar() {
        const ctx = getContextPath();
        const navMenu = document.getElementById('siteNavMenu');
        const navActions = document.getElementById('siteNavActions');
        const currentPath = window.location.pathname;

        if (!navMenu || !navActions) return;

        const role = sessionData && sessionData.authenticated ? sessionData.role : null;

        if (role === 'STUDENT') {
            navMenu.innerHTML = `
                <li><a href="${ctx}/" class="nav-link ${currentPath === ctx + '/' || currentPath === ctx + '/index.html' ? 'active' : ''}">Home</a></li>
                <li><a href="${ctx}/student/dashboard" class="nav-link ${currentPath.includes('/student/dashboard') ? 'active' : ''}">Dashboard</a></li>
                <li><a href="${ctx}/student/assessment" class="nav-link ${currentPath.includes('/student/assessment') ? 'active' : ''}">Take Assessment</a></li>
                <li><a href="${ctx}/student/results" class="nav-link ${currentPath.includes('/student/results') ? 'active' : ''}">My Results</a></li>
                <li><a href="${ctx}/student/history" class="nav-link ${currentPath.includes('/student/history') ? 'active' : ''}">History</a></li>
                <li><a href="${ctx}/student/profile" class="nav-link ${currentPath.includes('/student/profile') ? 'active' : ''}">Profile</a></li>
            `;

            const initials = (sessionData.fullName || 'S').charAt(0).toUpperCase();
            navActions.innerHTML = `
                <div class="nav-user-pill">
                    <div class="nav-user-avatar">${initials}</div>
                    <span>${escapeHtml(sessionData.fullName || 'Student')}</span>
                </div>
                <a href="${ctx}/logout" class="btn btn-outline-white btn-sm">Logout</a>
                <button class="nav-toggle" aria-label="Toggle navigation">
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <line x1="3" y1="12" x2="21" y2="12"></line>
                        <line x1="3" y1="6" x2="21" y2="6"></line>
                        <line x1="3" y1="18" x2="21" y2="18"></line>
                    </svg>
                </button>
            `;
        } else if (role === 'ADMIN') {
            navMenu.innerHTML = `
                <li><a href="${ctx}/" class="nav-link ${currentPath === ctx + '/' || currentPath === ctx + '/index.html' ? 'active' : ''}">Home</a></li>
                <li><a href="${ctx}/admin/dashboard" class="nav-link ${currentPath.includes('/admin/dashboard') ? 'active' : ''}">Admin Dashboard</a></li>
                <li><a href="${ctx}/admin/questions" class="nav-link ${currentPath.includes('/admin/questions') ? 'active' : ''}">Questions</a></li>
                <li><a href="${ctx}/admin/students" class="nav-link ${currentPath.includes('/admin/students') ? 'active' : ''}">Students</a></li>
                <li><a href="${ctx}/admin/careers" class="nav-link ${currentPath.includes('/admin/careers') ? 'active' : ''}">Careers (35)</a></li>
            `;

            navActions.innerHTML = `
                <div class="nav-user-pill">
                    <div class="nav-user-avatar" style="background: var(--teal-600);">A</div>
                    <span>${escapeHtml(sessionData.username || 'Admin')} (Admin)</span>
                </div>
                <a href="${ctx}/admin/logout" class="btn btn-outline-white btn-sm">Logout</a>
                <button class="nav-toggle" aria-label="Toggle navigation">
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <line x1="3" y1="12" x2="21" y2="12"></line>
                        <line x1="3" y1="6" x2="21" y2="6"></line>
                        <line x1="3" y1="18" x2="21" y2="18"></line>
                    </svg>
                </button>
            `;
        } else {
            navMenu.innerHTML = `
                <li><a href="${ctx}/" class="nav-link ${currentPath === ctx + '/' || currentPath === ctx + '/index.html' ? 'active' : ''}">Home</a></li>
                <li><a href="${ctx}/register" class="nav-link ${currentPath.includes('/register') ? 'active' : ''}">Student Register</a></li>
                <li><a href="${ctx}/login" class="nav-link ${currentPath.includes('/login') && !currentPath.includes('/admin/login') ? 'active' : ''}">Student Login</a></li>
                <li><a href="${ctx}/admin/login" class="nav-link ${currentPath.includes('/admin/login') ? 'active' : ''}">Admin Portal</a></li>
            `;

            navActions.innerHTML = `
                <a href="${ctx}/login" class="btn btn-primary btn-sm">Get Started</a>
                <button class="nav-toggle" aria-label="Toggle navigation">
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <line x1="3" y1="12" x2="21" y2="12"></line>
                        <line x1="3" y1="6" x2="21" y2="6"></line>
                        <line x1="3" y1="18" x2="21" y2="18"></line>
                    </svg>
                </button>
            `;
        }

        initMobileNav();
    }

    /**
     * Initializes mobile hamburger toggle button
     */
    function initMobileNav() {
        const toggleBtn = document.querySelector('.nav-toggle');
        const navMenu = document.querySelector('.nav-menu');

        if (toggleBtn && navMenu) {
            toggleBtn.onclick = function (e) {
                e.stopPropagation();
                navMenu.classList.toggle('active');
            };

            document.addEventListener('click', function (e) {
                if (!navMenu.contains(e.target) && !toggleBtn.contains(e.target)) {
                    navMenu.classList.remove('active');
                }
            });
        }
    }

    /**
     * Modal dialog handlers
     */
    function openModal(id) {
        const modal = document.getElementById(id);
        if (modal) {
            modal.classList.add('active');
            document.body.style.overflow = 'hidden';
        }
    }

    function closeModal(id) {
        const modal = document.getElementById(id);
        if (modal) {
            modal.classList.remove('active');
            document.body.style.overflow = '';
        }
    }

    function initModals() {
        document.querySelectorAll('.modal').forEach(modal => {
            modal.addEventListener('click', function (e) {
                if (e.target === modal) {
                    closeModal(modal.id);
                }
            });
        });

        document.querySelectorAll('[data-close-modal]').forEach(btn => {
            btn.addEventListener('click', function () {
                const targetId = btn.getAttribute('data-close-modal');
                closeModal(targetId);
            });
        });
    }

    // Exported Public API
    return {
        getContextPath,
        escapeHtml,
        fetchJson,
        initSessionNavbar,
        renderNavbar,
        openModal,
        closeModal,
        initModals,
        getSessionData: () => sessionData
    };
})();

// Global helpers for inline HTML onclick attributes
function openModal(id) { CareerApp.openModal(id); }
function closeModal(id) { CareerApp.closeModal(id); }

// Initialize session and navbar once DOM is loaded
document.addEventListener('DOMContentLoaded', function () {
    CareerApp.initSessionNavbar();
    CareerApp.initModals();
});
