/**
 * Centralized REST API Client for Campus Resource Booking System.
 * Connects frontend fetch calls to Spring Boot backend endpoints.
 */

const API = {
    BASE_URL: '/api',

    getToken() {
        return localStorage.getItem('campus_token');
    },

    setToken(token) {
        localStorage.setItem('campus_token', token);
    },

    removeToken() {
        localStorage.removeItem('campus_token');
        localStorage.removeItem('campus_user');
    },

    getCurrentUser() {
        const u = localStorage.getItem('campus_user');
        return u ? JSON.parse(u) : null;
    },

    setCurrentUser(user) {
        localStorage.setItem('campus_user', JSON.stringify(user));
    },

    async request(endpoint, options = {}) {
        const url = `${this.BASE_URL}${endpoint}`;
        const headers = {
            'Content-Type': 'application/json',
            ...(options.headers || {})
        };

        const token = this.getToken();
        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        const config = {
            ...options,
            headers
        };

        try {
            const response = await fetch(url, config);
            const data = await response.json().catch(() => null);

            if (!response.ok) {
                // Handle 401 Unauthorized redirect
                if (response.status === 401 && !url.includes('/auth/login') && !url.includes('/auth/register')) {
                    this.removeToken();
                    if (!window.location.pathname.endsWith('login.html')) {
                        window.location.href = 'login.html?expired=true';
                    }
                }
                let errorMsg = `Request failed with status ${response.status}`;
                if (data) {
                    if (data.fieldErrors && Object.keys(data.fieldErrors).length > 0) {
                        errorMsg = Object.values(data.fieldErrors).join(', ');
                    } else if (data.message) {
                        errorMsg = data.message;
                    }
                }
                const err = new Error(errorMsg);
                err.status = response.status;
                err.response = data;
                throw err;
            }

            return data;
        } catch (error) {
            console.error(`API Error on ${endpoint}:`, error);
            throw error;
        }
    },

    // ==========================================
    // AUTH ENDPOINTS
    // ==========================================
    login(credentials) {
        return this.request('/auth/login', {
            method: 'POST',
            body: JSON.stringify(credentials)
        });
    },

    register(userData) {
        return this.request('/auth/register', {
            method: 'POST',
            body: JSON.stringify(userData)
        });
    },

    getMe() {
        return this.request('/auth/me');
    },

    logout() {
        return this.request('/auth/logout', {
            method: 'POST'
        }).catch(() => null);
    },

    // ==========================================
    // USER ENDPOINTS
    // ==========================================
    getProfile() {
        return this.request('/users/profile');
    },

    updateProfile(data) {
        return this.request('/users/profile', {
            method: 'PUT',
            body: JSON.stringify(data)
        });
    },

    changePassword(data) {
        return this.request('/users/change-password', {
            method: 'PUT',
            body: JSON.stringify(data)
        });
    },

    // ==========================================
    // RESOURCE ENDPOINTS
    // ==========================================
    getResources(params = {}) {
        const query = new URLSearchParams();
        if (params.categoryId) query.append('categoryId', params.categoryId);
        if (params.minCapacity) query.append('minCapacity', params.minCapacity);
        if (params.status) query.append('status', params.status);
        if (params.search) query.append('search', params.search);

        const qs = query.toString() ? `?${query.toString()}` : '';
        return this.request(`/resources${qs}`);
    },

    getResourceById(id) {
        return this.request(`/resources/${id}`);
    },

    getCategories() {
        return this.request('/resources/categories');
    },

    createCategory(category) {
        return this.request('/resources/categories', {
            method: 'POST',
            body: JSON.stringify(category)
        });
    },

    checkAvailability(resourceId, date, startTime, endTime) {
        return this.request(`/resources/${resourceId}/availability?date=${date}&startTime=${startTime}&endTime=${endTime}`);
    },

    getResourceSchedule(resourceId, date) {
        return this.request(`/resources/${resourceId}/schedule?date=${date}`);
    },

    createResource(resourceData) {
        return this.request('/resources', {
            method: 'POST',
            body: JSON.stringify(resourceData)
        });
    },

    updateResource(id, resourceData) {
        return this.request(`/resources/${id}`, {
            method: 'PUT',
            body: JSON.stringify(resourceData)
        });
    },

    updateResourceStatus(id, status) {
        return this.request(`/resources/${id}/status`, {
            method: 'PATCH',
            body: JSON.stringify({ status })
        });
    },

    deleteResource(id) {
        return this.request(`/resources/${id}`, {
            method: 'DELETE'
        });
    },

    // ==========================================
    // BOOKING ENDPOINTS
    // ==========================================
    getBookings(status = null) {
        const qs = status ? `?status=${status}` : '';
        return this.request(`/bookings${qs}`);
    },

    getBookingById(id) {
        return this.request(`/bookings/${id}`);
    },

    createBooking(bookingData) {
        return this.request('/bookings', {
            method: 'POST',
            body: JSON.stringify(bookingData)
        });
    },

    cancelBooking(id) {
        return this.request(`/bookings/${id}/cancel`, {
            method: 'PUT'
        });
    },

    approveBooking(id, remark = '') {
        return this.request(`/bookings/${id}/approve`, {
            method: 'PUT',
            body: JSON.stringify({ remark })
        });
    },

    rejectBooking(id, remark = '') {
        return this.request(`/bookings/${id}/reject`, {
            method: 'PUT',
            body: JSON.stringify({ remark })
        });
    },

    completeBooking(id) {
        return this.request(`/bookings/${id}/complete`, {
            method: 'PUT'
        });
    },

    checkInBooking(id) {
        return this.request(`/bookings/${id}/check-in`, {
            method: 'POST'
        });
    },

    verifyCheckinCode(code) {
        return this.request(`/bookings/verify-checkin?code=${encodeURIComponent(code)}`, {
            method: 'POST'
        });
    },

    // ==========================================
    // NOTIFICATION ENDPOINTS
    // ==========================================
    getNotifications() {
        return this.request('/notifications');
    },

    getUnreadNotificationCount() {
        return this.request('/notifications/unread-count');
    },

    markNotificationRead(id) {
        return this.request(`/notifications/${id}/read`, {
            method: 'PATCH'
        });
    },

    markAllNotificationsRead() {
        return this.request('/notifications/mark-all-read', {
            method: 'POST'
        });
    },

    // ==========================================
    // ADMIN ENDPOINTS
    // ==========================================
    getAdminUsers(params = {}) {
        const query = new URLSearchParams();
        if (params.role) query.append('role', params.role);
        if (params.search) query.append('search', params.search);

        const qs = query.toString() ? `?${query.toString()}` : '';
        return this.request(`/admin/users${qs}`);
    },

    toggleUserStatus(userId, active) {
        return this.request(`/admin/users/${userId}/status`, {
            method: 'PATCH',
            body: JSON.stringify({ active })
        });
    },

    getAdminDashboardSummary() {
        return this.request('/admin/reports/summary');
    },

    getAdminBookingReports(params = {}) {
        const query = new URLSearchParams();
        if (params.fromDate) query.append('fromDate', params.fromDate);
        if (params.toDate) query.append('toDate', params.toDate);
        if (params.resourceId) query.append('resourceId', params.resourceId);
        if (params.categoryId) query.append('categoryId', params.categoryId);
        if (params.status) query.append('status', params.status);

        const qs = query.toString() ? `?${query.toString()}` : '';
        return this.request(`/admin/reports/bookings${qs}`);
    },

    getAdminResourceUsage(params = {}) {
        const query = new URLSearchParams();
        if (params.fromDate) query.append('fromDate', params.fromDate);
        if (params.toDate) query.append('toDate', params.toDate);

        const qs = query.toString() ? `?${query.toString()}` : '';
        return this.request(`/admin/reports/resource-usage${qs}`);
    }
};
