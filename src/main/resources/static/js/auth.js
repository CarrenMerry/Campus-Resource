/**
 * Authentication and Authorization Guard for Campus Resource Booking System.
 * Enforces client-side route protection, JWT token lifecycle validation, and server-side revocation triggers.
 */

const Auth = {
    // Check if user has an unexpired token and user profile
    isAuthenticated() {
        const token = API.getToken();
        const user = API.getCurrentUser();
        if (!token || !user) {
            return false;
        }

        if (this.isTokenExpired(token)) {
            API.removeToken();
            return false;
        }

        return true;
    },

    isLoggedIn() {
        return this.isAuthenticated();
    },

    isTokenExpired(token) {
        if (!token) return true;
        try {
            const parts = token.split('.');
            if (parts.length !== 3) return true;
            const payload = JSON.parse(atob(parts[1].replace(/-/g, '+').replace(/_/g, '/')));
            if (!payload.exp) return false;
            return Date.now() >= payload.exp * 1000;
        } catch (e) {
            return true;
        }
    },

    getUser() {
        if (!this.isAuthenticated()) {
            return null;
        }
        return API.getCurrentUser();
    },

    getRole() {
        const u = this.getUser();
        return u ? u.role : null;
    },

    // Route guard to enforce role permissions on sensitive pages
    requireAuth(allowedRoles = []) {
        if (!this.isAuthenticated()) {
            const currentPath = window.location.pathname.split('/').pop() || 'index.html';
            window.location.href = 'login.html?redirect=' + encodeURIComponent(currentPath) + '&unauthorized=true';
            return false;
        }

        const user = this.getUser();
        if (allowedRoles.length > 0 && !allowedRoles.includes(user.role)) {
            // Redirect to appropriate dashboard if unauthorized for this specific role
            this.redirectToDashboard(user.role);
            return false;
        }

        return true;
    },

    redirectToDashboard(role) {
        if (role === 'ADMIN') {
            window.location.href = 'admin-dashboard.html';
        } else if (role === 'FACULTY') {
            window.location.href = 'faculty-dashboard.html';
        } else if (role === 'STUDENT') {
            window.location.href = 'student-dashboard.html';
        } else {
            window.location.href = 'index.html';
        }
    },

    async handleLogin(email, password) {
        try {
            const res = await API.login({ email, password });
            if (res.success && res.data) {
                API.setToken(res.data.token);
                API.setCurrentUser(res.data.user);
                Common.showToast('Login successful! Redirecting...', 'success');
                
                // Check if there is a pending redirect parameter
                const urlParams = new URLSearchParams(window.location.search);
                const redirect = urlParams.get('redirect');

                setTimeout(() => {
                    if (redirect && !redirect.includes('login.html') && !redirect.includes('register.html')) {
                        window.location.href = redirect;
                    } else {
                        this.redirectToDashboard(res.data.user.role);
                    }
                }, 600);
            }
        } catch (error) {
            Common.showToast(error.message || 'Login failed. Please check credentials.', 'error');
            throw error;
        }
    },

    async logout() {
        try {
            await API.logout();
        } catch (err) {
            console.warn('Server logout notification error:', err);
        } finally {
            API.removeToken();
            sessionStorage.clear();
            Common.showToast('Logged out successfully.', 'info');
            setTimeout(() => {
                window.location.href = 'login.html?loggedOut=true';
            }, 400);
        }
    }
};
