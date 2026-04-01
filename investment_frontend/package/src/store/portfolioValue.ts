import { defineStore } from "pinia";
import api from '@/services/api.ts';
import { ref, computed } from 'vue';

export const usePortfolioStore = defineStore('portfolio', () => {
    const userSummary = ref<any[]>([]);
    const currentUserPortfolio = ref<any[]>([]);
    const lastUpdate = ref<Date | null>(null);
    const currentTime = ref(new Date());
    let intervalId: any = null;
    let tickerId: any = null;

    // Start a 1-second ticker to update the "Time Ago"
    const startTicker = () => {
        if (tickerId) return;
        tickerId = setInterval(() => {
            currentTime.value = new Date();
        }, 1000);
    };

    const timeAgo = computed(() => {
        if (!lastUpdate.value) return "Never";
        const diffInSeconds = Math.floor((currentTime.value.getTime() - lastUpdate.value.getTime()) / 1000);

        const mins = Math.floor(diffInSeconds / 60);
        const secs = diffInSeconds % 60;
        return `${mins}m ${secs}s ago`;
    });

    const lastUpdatedTime = computed(() => {
        if (!lastUpdate.value) return 'Never';
        return lastUpdate.value.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', second: '2-digit' });
    });

    const fetchAllData = async (userId: number | undefined) => {
        try {
            const [summaryRes, portfolioRes] = await Promise.all([
                api.get('/auth/users-summary'),
                userId ? api.get(`/portfolio/${userId}`) : Promise.resolve({ data: [] })
            ]);
            userSummary.value = summaryRes.data || [];
            currentUserPortfolio.value = portfolioRes.data || [];
            lastUpdate.value = new Date();
            startTicker(); // Start counting once we have data
        } catch (error) {
            console.error("Global data sync failed", error);
        }
    };

    const startPolling = (userId: number | undefined) => {
        if (intervalId) return;
        fetchAllData(userId);
        intervalId = setInterval(() => fetchAllData(userId), 180000);
    };

    return {
        userSummary,
        currentUserPortfolio,
        lastUpdate,
        lastUpdatedTime,
        timeAgo,
        fetchAllData,
        startPolling
    };
});