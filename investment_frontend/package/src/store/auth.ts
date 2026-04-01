import { reactive, computed } from 'vue';
import api from '@/services/api';

interface User {
    id: number;
    name: string;
    email: string;
    phone: string;
    password: string;
    role: string;
}

const state = reactive({
    user: JSON.parse(localStorage.getItem('user') || 'null') as User | null,
    token: localStorage.getItem('token') || null
});

export const useAuth = () => {
    const isTokenExpired = (token: string | null) => {
        if (!token) return true;
        try {
            const payload = JSON.parse(atob(token.split('.')[1]));
            return payload.exp * 1000 < Date.now();
        } catch (error) {
            return true;
        }
    };

    const isAuthenticated = computed(() => {
        return !!state.token && !isTokenExpired(state.token);
    })

    const setUser = (user: User | null, token: string | null = state.token) => {
        state.user = user;
        state.token = token;
        if (user) {
            localStorage.setItem('user', JSON.stringify(user));
        } else {
            localStorage.removeItem('user');
        }

        if (token) {
            localStorage.setItem('token', token);
        } else {
            localStorage.removeItem('token');
        }
    };

    const login = async (name: string, password: string) => {
        try {
            const response = await api.post('/auth/login', { name, password });
            const { user, token } = response.data;
            setUser(user, token);
            return true;
        } catch (error) {
            console.error("Login failed:", error);
            return false;
        }
    };

    const register = async (name: string, email: string, phone:string, password: string) => {
        try {
            const response = await api.post('/auth/register', { name, email, phone, password });
            const { user, token } = response.data;
            setUser(user, token);

            return { success: true };
        } catch (error: any) {
            let message = "FAILED";
            if (error.response?.status === 409) message = "USERNAME_TAKEN";
            if (error.response?.data === "EMAIL_TAKEN") message = "EMAIL_TAKEN";

            return { success: false, message };
        }
    };

    const logout = () => {
        setUser(null, null);
        window.location.href = "/auth/login2";
    }

    return {
        user: computed(() => state.user),
        token: computed(() => state.token),
        isAuthenticated,
        setUser,
        login,
        register,
        logout
    };
};