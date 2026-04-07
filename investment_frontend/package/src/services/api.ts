import axios from "axios";
import {useAuth} from "@/store/auth";

const API_BASE = (import.meta.env.VITE_API_BASE as string) || "http://localhost:8081";

const api = axios.create({
    baseURL: API_BASE,
    timeout: 60000,
    withCredentials: true
});

api.interceptors.request.use((config) => {
    const { token } = useAuth()
    if (token.value) {
        config.headers.Authorization = `Bearer ${token.value}`;
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