import axios from "axios";
import {useAuth} from "@/store/auth";

const API_BASE = import.meta.env.VITE_API_BASE;

if (!API_BASE) {
    throw new Error("VITE_API_BASE is not defined. Check your .env files.");
}

const api = axios.create({
    baseURL: API_BASE,
    timeout: 60000,
    withCredentials: true
});

api.interceptors.request.use((config) => {
    const authStore = useAuth();
    const token = authStore.token.value || localStorage.getItem('token');
    if (token  && token != 'null') {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
}, (error) => Promise.reject(error));

api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response && error.response.status === 401) {
            const auth = useAuth();
            auth.logout();
            // window.location.href = "/auth/login2";
        }
        return Promise.reject(error);
    }
);

export default api;