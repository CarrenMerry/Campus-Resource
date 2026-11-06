/**
 * Common UI components, Dynamic Navigation, Notifications, QR Entry Pass, Toasts, and Utilities.
 */

const Common = {
    init() {
        this.renderNavbar();
        this.renderFooter();

        if (typeof Auth !== 'undefined' && Auth.isLoggedIn()) {
            this.updateNotificationBadge();
            if (!window.__notifInterval) {
                window.__notifInterval = setInterval(() => this.updateNotificationBadge(), 30000);
            }
        }
    },

    getNotificationNavHtml() {
        return `
            <li class="nav-item dropdown ms-lg-2 me-lg-2 position-relative" id="nav-notifications-item">
                <a class="nav-link position-relative p-2" href="#" id="notificationBellBtn" role="button" data-bs-toggle="dropdown" aria-expanded="false" onclick="Common.loadNotifications()">
                    <i class="bi bi-bell-fill fs-5 text-secondary"></i>
                    <span id="notificationBadge" class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger d-none" style="font-size: 0.65rem;">
                        0
                    </span>
                </a>
                <div class="dropdown-menu dropdown-menu-end shadow-lg border-0 p-0 notification-dropdown-menu" style="width: 340px; max-height: 440px; overflow-y: auto;">
                    <div class="p-3 border-bottom d-flex justify-content-between align-items-center bg-light">
                        <span class="fw-bold fs-6 text-dark"><i class="bi bi-bell me-1 text-primary"></i> Notifications</span>
                        <button class="btn btn-link btn-sm text-decoration-none p-0" onclick="Common.markAllNotificationsRead(event)">Mark all read</button>
                    </div>
                    <div id="notificationList" class="p-2">
                        <div class="text-center text-muted py-3 small">Loading alerts...</div>
                    </div>
                </div>
            </li>
        `;
    },

    renderNavbar() {
        const navContainer = document.getElementById('navbar-placeholder');
        if (!navContainer) return;

        const user = typeof Auth !== 'undefined' ? Auth.getUser() : null;
        const role = user ? user.role : null;
        const currentPath = window.location.pathname.split('/').pop() || 'index.html';

        let navLinks = '';

        if (!user) {
            // Guest Navigation
            navLinks = `
                <li class="nav-item">
                    <a class="nav-link ${currentPath === 'index.html' ? 'active' : ''}" href="index.html">
                        <i class="bi bi-house"></i> Home
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link ${currentPath === 'resources.html' ? 'active' : ''}" href="resources.html">
                        <i class="bi bi-grid"></i> Resources
                    </a>
                </li>
                <li class="nav-item ms-lg-2">
                    <a class="btn btn-outline-custom btn-sm" href="login.html">
                        <i class="bi bi-box-arrow-in-right"></i> Login
                    </a>
                </li>
                <li class="nav-item ms-lg-2">
                    <a class="btn btn-primary-custom btn-sm" href="register.html">
                        <i class="bi bi-person-plus"></i> Register
                    </a>
                </li>
            `;
        } else if (role === 'ADMIN') {
            // Admin Navigation
            navLinks = `
                <li class="nav-item">
                    <a class="nav-link ${currentPath === 'admin-dashboard.html' ? 'active' : ''}" href="admin-dashboard.html">
                        <i class="bi bi-speedometer2"></i> Dashboard
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link ${currentPath === 'admin-users.html' ? 'active' : ''}" href="admin-users.html">
                        <i class="bi bi-people"></i> Users
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link ${currentPath === 'admin-resources.html' ? 'active' : ''}" href="admin-resources.html">
                        <i class="bi bi-building"></i> Resources
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link ${currentPath === 'admin-bookings.html' ? 'active' : ''}" href="admin-bookings.html">
                        <i class="bi bi-calendar-check"></i> Bookings
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link ${currentPath === 'admin-reports.html' ? 'active' : ''}" href="admin-reports.html">
                        <i class="bi bi-graph-up"></i> Reports
                    </a>
                </li>
                ${this.getNotificationNavHtml()}
                <li class="nav-item dropdown ms-lg-2">
                    <a class="nav-link dropdown-toggle d-flex align-items-center gap-2" href="#" role="button" data-bs-toggle="dropdown">
                        <span class="badge bg-danger rounded-pill">Admin</span>
                        <span>${this.escapeHtml(user.name)}</span>
                    </a>
                    <ul class="dropdown-menu dropdown-menu-end shadow-sm border-0">
                        <li><a class="dropdown-item" href="profile.html"><i class="bi bi-person me-2"></i> Profile</a></li>
                        <li><hr class="dropdown-divider"></li>
                        <li><a class="dropdown-item text-danger" href="javascript:void(0)" onclick="Auth.logout()"><i class="bi bi-box-arrow-right me-2"></i> Logout</a></li>
                    </ul>
                </li>
            `;
        } else if (role === 'FACULTY') {
            // Faculty Navigation
            navLinks = `
                <li class="nav-item">
                    <a class="nav-link ${currentPath === 'faculty-dashboard.html' ? 'active' : ''}" href="faculty-dashboard.html">
                        <i class="bi bi-speedometer2"></i> Dashboard
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link ${currentPath === 'resources.html' ? 'active' : ''}" href="resources.html">
                        <i class="bi bi-grid"></i> Resources
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link ${currentPath === 'booking.html' ? 'active' : ''}" href="booking.html">
                        <i class="bi bi-calendar-plus"></i> Book Facility
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link ${currentPath === 'my-bookings.html' ? 'active' : ''}" href="my-bookings.html">
                        <i class="bi bi-calendar-check"></i> My Bookings
                    </a>
                </li>
                ${this.getNotificationNavHtml()}
                <li class="nav-item dropdown ms-lg-2">
                    <a class="nav-link dropdown-toggle d-flex align-items-center gap-2" href="#" role="button" data-bs-toggle="dropdown">
                        <span class="badge bg-primary rounded-pill">Faculty</span>
                        <span>${this.escapeHtml(user.name)}</span>
                    </a>
                    <ul class="dropdown-menu dropdown-menu-end shadow-sm border-0">
                        <li><a class="dropdown-item" href="profile.html"><i class="bi bi-person me-2"></i> Profile</a></li>
                        <li><hr class="dropdown-divider"></li>
                        <li><a class="dropdown-item text-danger" href="javascript:void(0)" onclick="Auth.logout()"><i class="bi bi-box-arrow-right me-2"></i> Logout</a></li>
                    </ul>
                </li>
            `;
        } else {
            // Student Navigation
            navLinks = `
                <li class="nav-item">
                    <a class="nav-link ${currentPath === 'student-dashboard.html' ? 'active' : ''}" href="student-dashboard.html">
                        <i class="bi bi-speedometer2"></i> Dashboard
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link ${currentPath === 'resources.html' ? 'active' : ''}" href="resources.html">
                        <i class="bi bi-grid"></i> Resources & Timetables
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link ${currentPath === 'my-bookings.html' ? 'active' : ''}" href="my-bookings.html">
                        <i class="bi bi-calendar-check"></i> My Schedule
                    </a>
                </li>
                ${this.getNotificationNavHtml()}
                <li class="nav-item dropdown ms-lg-2">
                    <a class="nav-link dropdown-toggle d-flex align-items-center gap-2" href="#" role="button" data-bs-toggle="dropdown">
                        <span class="badge bg-info text-dark rounded-pill">Student</span>
                        <span>${this.escapeHtml(user.name)}</span>
                    </a>
                    <ul class="dropdown-menu dropdown-menu-end shadow-sm border-0">
                        <li><a class="dropdown-item" href="profile.html"><i class="bi bi-person me-2"></i> Profile</a></li>
                        <li><hr class="dropdown-divider"></li>
                        <li><a class="dropdown-item text-danger" href="javascript:void(0)" onclick="Auth.logout()"><i class="bi bi-box-arrow-right me-2"></i> Logout</a></li>
                    </ul>
                </li>
            `;
        }

        navContainer.innerHTML = `
            <nav class="navbar navbar-expand-lg navbar-custom sticky-top">
                <div class="container">
                    <a class="navbar-brand" href="index.html">
                        <div class="brand-icon-badge">
                            <i class="bi bi-mortarboard-fill"></i>
                        </div>
                        <span>Campus<span class="text-primary">Reserve</span></span>
                    </a>
                    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#mainNav">
                        <span class="navbar-toggler-icon"></span>
                    </button>
                    <div class="collapse navbar-collapse" id="mainNav">
                        <ul class="navbar-nav ms-auto align-items-lg-center">
                            ${navLinks}
                        </ul>
                    </div>
                </div>
            </nav>
        `;
    },

    renderFooter() {
        const footerContainer = document.getElementById('footer-placeholder');
        if (!footerContainer) return;

        footerContainer.innerHTML = `
            <footer class="footer-custom">
                <div class="container">
                    <div class="row g-4 align-items-center">
                        <div class="col-md-6 text-center text-md-start">
                            <div class="d-flex align-items-center gap-2 justify-content-center justify-content-md-start mb-2">
                                <div class="brand-icon-badge" style="width:28px; height:28px; font-size:0.9rem;">
                                    <i class="bi bi-mortarboard-fill"></i>
                                </div>
                                <span class="fw-bold text-dark">Campus Resource Booking System</span>
                            </div>
                            <p class="small text-muted mb-0">
                                Comprehensive Academic Scheduling & Facility Management Platform. Built for MCA Project Demonstration.
                            </p>
                        </div>
                        <div class="col-md-6 text-center text-md-end">
                            <p class="small text-muted mb-1">
                                <strong>Java Spring Boot + MySQL + REST APIs + QR Check-In</strong>
                            </p>
                            <p class="small text-muted mb-0">
                                Demonstrating OOP, Encapsulation, Inheritance, Polymorphism, Streams & Layered Architecture.
                            </p>
                        </div>
                    </div>
                </div>
            </footer>
        `;
    },

    // ==========================================
    // NOTIFICATION CENTER FUNCTIONS
    // ==========================================

    async updateNotificationBadge() {
        const badge = document.getElementById('notificationBadge');
        if (!badge || typeof API === 'undefined') return;

        try {
            const res = await API.getUnreadNotificationCount();
            const count = res && res.unreadCount ? res.unreadCount : 0;
            if (count > 0) {
                badge.textContent = count > 99 ? '99+' : count;
                badge.classList.remove('d-none');
            } else {
                badge.classList.add('d-none');
            }
        } catch (e) {
            // Ignore polling errors
        }
    },

    async loadNotifications() {
        const listContainer = document.getElementById('notificationList');
        if (!listContainer || typeof API === 'undefined') return;

        listContainer.innerHTML = '<div class="text-center text-muted py-3 small"><div class="spinner-border spinner-border-sm text-primary me-1"></div> Fetching alerts...</div>';

        try {
            const notifications = await API.getNotifications();
            if (!notifications || notifications.length === 0) {
                listContainer.innerHTML = '<div class="text-center text-muted py-4 small"><i class="bi bi-bell-slash fs-4 d-block mb-1 text-secondary"></i>No notifications yet</div>';
                return;
            }

            const iconMap = {
                'SUCCESS': 'bi-check-circle-fill',
                'WARNING': 'bi-exclamation-circle-fill',
                'DANGER': 'bi-x-circle-fill',
                'INFO': 'bi-info-circle-fill'
            };

            listContainer.innerHTML = notifications.map(n => `
                <div class="notification-item p-2 mb-1 rounded d-flex gap-2 align-items-start ${n.read ? '' : 'unread'}" 
                     onclick="Common.markNotificationRead(${n.notificationId}, event)" style="cursor: pointer;">
                    <div class="notif-icon icon-${n.type || 'INFO'}">
                        <i class="bi ${iconMap[n.type] || iconMap.INFO}"></i>
                    </div>
                    <div class="flex-grow-1" style="min-width: 0;">
                        <div class="d-flex justify-content-between align-items-center">
                            <strong class="text-dark small text-truncate d-block" style="max-width: 190px;">${this.escapeHtml(n.title)}</strong>
                            <span class="text-muted" style="font-size: 0.7rem;">${this.formatTimeAgo(n.createdAt)}</span>
                        </div>
                        <p class="mb-0 text-muted small text-break" style="font-size: 0.8rem; line-height: 1.25;">${this.escapeHtml(n.message)}</p>
                    </div>
                </div>
            `).join('');

            this.updateNotificationBadge();
        } catch (err) {
            listContainer.innerHTML = '<div class="text-center text-danger py-3 small">Failed to load notifications</div>';
        }
    },

    async markNotificationRead(id, event) {
        if (event) event.stopPropagation();
        try {
            await API.markNotificationRead(id);
            this.updateNotificationBadge();
            const item = event ? event.currentTarget : null;
            if (item) item.classList.remove('unread');
        } catch (e) {
            console.error(e);
        }
    },

    async markAllNotificationsRead(event) {
        if (event) event.stopPropagation();
        try {
            await API.markAllNotificationsRead();
            this.updateNotificationBadge();
            const list = document.getElementById('notificationList');
            if (list) {
                list.querySelectorAll('.notification-item').forEach(el => el.classList.remove('unread'));
            }
            this.showToast('All notifications marked as read', 'success');
        } catch (e) {
            this.showToast('Failed to mark notifications read', 'error');
        }
    },

    formatTimeAgo(dateStr) {
        if (!dateStr) return '';
        try {
            const now = new Date();
            const past = new Date(dateStr.replace(' ', 'T'));
            const diffMs = now - past;
            const diffMins = Math.floor(diffMs / 60000);
            if (diffMins < 1) return 'Just now';
            if (diffMins < 60) return `${diffMins}m ago`;
            const diffHours = Math.floor(diffMins / 60);
            if (diffHours < 24) return `${diffHours}h ago`;
            const diffDays = Math.floor(diffHours / 24);
            return `${diffDays}d ago`;
        } catch (e) {
            return '';
        }
    },

    // ==========================================
    // QR DIGITAL ENTRY PASS MODAL
    // ==========================================

    showQRPassModal(booking) {
        let modalEl = document.getElementById('qrPassModal');
        if (!modalEl) {
            modalEl = document.createElement('div');
            modalEl.id = 'qrPassModal';
            modalEl.className = 'modal fade';
            modalEl.setAttribute('tabindex', '-1');
            document.body.appendChild(modalEl);
        }

        const checkinCode = booking.checkinCode || `CRB-PASS-${booking.bookingId}`;
        const qrUrl = `https://api.qrserver.com/v1/create-qr-code/?size=220x220&data=${encodeURIComponent(checkinCode)}`;
        const isCheckedIn = booking.checkedIn;
        const checkinBadge = isCheckedIn 
            ? `<span class="badge bg-success-subtle text-success border border-success"><i class="bi bi-check-circle-fill me-1"></i> Checked-In at ${this.formatTime(booking.checkInTime ? booking.checkInTime.split('T')[1] : '')}</span>`
            : `<span class="badge bg-warning-subtle text-warning border border-warning"><i class="bi bi-clock-history me-1"></i> Awaiting Entrance Scan</span>`;

        modalEl.innerHTML = `
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content shadow-lg border-0" style="border-radius: var(--radius-lg);">
                    <div class="modal-header border-0 pb-0">
                        <h5 class="modal-title fw-bold text-primary"><i class="bi bi-ticket-perforated-fill me-2"></i> Digital Facility Entry Pass</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body p-4 text-center">
                        <div class="qr-pass-card p-4 mb-3">
                            <div class="d-flex align-items-center justify-content-between mb-3 border-bottom pb-2">
                                <span class="small fw-bold text-uppercase text-muted" style="letter-spacing: 0.05em;">Institutional Reservation Pass</span>
                                ${checkinBadge}
                            </div>
                            
                            <div class="qr-code-box my-2">
                                <img src="${qrUrl}" alt="QR Entry Code" width="180" height="180" class="img-fluid rounded" 
                                     onerror="this.parentElement.innerHTML='<div class=\\'p-4 bg-light text-primary font-mono fw-bold\\'>${checkinCode}</div>'">
                            </div>

                            <div class="font-mono fs-5 fw-bold text-dark my-2 bg-light p-2 rounded border" style="letter-spacing: 0.1em;">
                                ${checkinCode}
                            </div>

                            <div class="row g-2 text-start mt-3 pt-2 border-top">
                                <div class="col-12">
                                    <span class="text-muted small d-block">Facility / Resource:</span>
                                    <strong class="text-dark fs-6">${this.escapeHtml(booking.resourceName || 'Resource')}</strong>
                                </div>
                                <div class="col-6">
                                    <span class="text-muted small d-block">Reservation Date:</span>
                                    <span class="fw-semibold text-dark">${this.formatDate(booking.bookingDate)}</span>
                                </div>
                                <div class="col-6">
                                    <span class="text-muted small d-block">Time Slot:</span>
                                    <span class="fw-semibold text-dark">${this.formatTime(booking.startTime)} - ${this.formatTime(booking.endTime)}</span>
                                </div>
                                <div class="col-12 mt-2 pt-2 border-top">
                                    <span class="text-muted small d-block">Booked By:</span>
                                    <span class="small text-secondary">${this.escapeHtml(booking.userName || 'User')} (${booking.userRole || ''}) &bull; ${booking.attendeesCount || 1} attendees</span>
                                </div>
                            </div>
                        </div>
                        <p class="small text-muted mb-0">Present this QR code or Pass Code at the facility entrance terminal for automated check-in.</p>
                    </div>
                    <div class="modal-footer border-0 pt-0 justify-content-between">
                        <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Close</button>
                        <button type="button" class="btn btn-primary" onclick="window.print()">
                            <i class="bi bi-printer me-1"></i> Print / Save Pass
                        </button>
                    </div>
                </div>
            </div>
        `;

        const modal = new bootstrap.Modal(modalEl);
        modal.show();
    },

    // ==========================================
    // ADMIN CHECK-IN VERIFICATION MODAL
    // ==========================================

    showAdminVerifyCheckInModal(onSuccess) {
        let modalEl = document.getElementById('adminCheckInModal');
        if (!modalEl) {
            modalEl = document.createElement('div');
            modalEl.id = 'adminCheckInModal';
            modalEl.className = 'modal fade';
            modalEl.setAttribute('tabindex', '-1');
            document.body.appendChild(modalEl);
        }

        modalEl.innerHTML = `
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content shadow-lg border-0" style="border-radius: var(--radius-lg);">
                    <div class="modal-header">
                        <h5 class="modal-title fw-bold"><i class="bi bi-qr-code-scan text-primary me-2"></i> Verify Pass & Confirm Check-In</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body p-4">
                        <p class="text-muted small mb-3">Scan the attendee's QR entry pass barcode or enter their unique Pass Code (e.g. <code>CRB-A1B2C3D4</code>) below to confirm venue arrival.</p>
                        <div class="mb-3">
                            <label class="form-label fw-semibold">Check-In Pass Code</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="bi bi-upc-scan"></i></span>
                                <input type="text" id="passCodeInput" class="form-control font-mono" placeholder="CRB-..." autofocus>
                            </div>
                        </div>
                        <div id="checkInResult" class="d-none alert"></div>
                    </div>
                    <div class="modal-footer border-0">
                        <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="button" class="btn btn-success" id="confirmCheckInBtn">
                            <i class="bi bi-check-circle me-1"></i> Verify & Check-In
                        </button>
                    </div>
                </div>
            </div>
        `;

        const modal = new bootstrap.Modal(modalEl);
        modal.show();

        const input = document.getElementById('passCodeInput');
        const resultBox = document.getElementById('checkInResult');
        const confirmBtn = document.getElementById('confirmCheckInBtn');

        confirmBtn.onclick = async () => {
            const code = input.value.trim();
            if (!code) {
                resultBox.className = 'alert alert-danger';
                resultBox.textContent = 'Please enter a valid pass code.';
                resultBox.classList.remove('d-none');
                return;
            }

            confirmBtn.disabled = true;
            confirmBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-1"></span> Verifying...';

            try {
                const res = await API.verifyCheckinCode(code);
                resultBox.className = 'alert alert-success';
                resultBox.innerHTML = `<strong><i class="bi bi-check-circle-fill me-1"></i> Verified!</strong> Checked in <strong>${Common.escapeHtml(res.userName)}</strong> for <em>${Common.escapeHtml(res.resourceName)}</em>.`;
                resultBox.classList.remove('d-none');
                Common.showToast('Check-in confirmed!', 'success');
                if (typeof onSuccess === 'function') onSuccess(res);
                setTimeout(() => modal.hide(), 1600);
            } catch (err) {
                resultBox.className = 'alert alert-danger';
                resultBox.textContent = err.message || 'Pass verification failed.';
                resultBox.classList.remove('d-none');
            } finally {
                confirmBtn.disabled = false;
                confirmBtn.innerHTML = '<i class="bi bi-check-circle me-1"></i> Verify & Check-In';
            }
        };
    },

    showToast(message, type = 'info') {
        let container = document.getElementById('toast-container');
        if (!container) {
            container = document.createElement('div');
            container.id = 'toast-container';
            document.body.appendChild(container);
        }

        const toast = document.createElement('div');
        toast.className = `custom-toast toast-${type}`;

        const icons = {
            success: 'bi-check-circle-fill text-success',
            error: 'bi-exclamation-triangle-fill text-danger',
            warning: 'bi-exclamation-circle-fill text-warning',
            info: 'bi-info-circle-fill text-primary'
        };

        toast.innerHTML = `
            <i class="bi ${icons[type] || icons.info} fs-5"></i>
            <div class="flex-grow-1 text-sm fw-medium">${this.escapeHtml(message)}</div>
            <button type="button" class="btn-close btn-close-sm" style="font-size:0.75rem;" onclick="this.parentElement.remove()"></button>
        `;

        container.appendChild(toast);

        setTimeout(() => {
            if (toast.parentElement) {
                toast.style.opacity = '0';
                toast.style.transform = 'translateX(100%)';
                toast.style.transition = 'all 0.3s ease';
                setTimeout(() => toast.remove(), 300);
            }
        }, 4000);
    },

    escapeHtml(text) {
        if (!text) return '';
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    },

    formatDate(dateStr) {
        if (!dateStr) return '-';
        try {
            const parts = dateStr.split('-');
            if (parts.length === 3) {
                const d = new Date(parts[0], parts[1] - 1, parts[2]);
                return d.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
            }
            return dateStr;
        } catch (e) {
            return dateStr;
        }
    },

    formatTime(timeStr) {
        if (!timeStr) return '-';
        try {
            const parts = timeStr.split(':');
            let h = parseInt(parts[0], 10);
            const m = parts[1];
            const ampm = h >= 12 ? 'PM' : 'AM';
            h = h % 12 || 12;
            return `${h}:${m} ${ampm}`;
        } catch (e) {
            return timeStr;
        }
    },

    getUrlParam(param) {
        const urlParams = new URLSearchParams(window.location.search);
        return urlParams.get(param);
    }
};

document.addEventListener('DOMContentLoaded', () => {
    Common.init();
});
