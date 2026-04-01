<script setup lang="ts">
import RevenueUpdate from '@/components/dashboards/modern/RevenueUpdate.vue'
import PortfolioValue from '@/components/dashboards/modern/PortfolioValue.vue'
import ProfileWelcome from "@/components/dashboards/modern/ProfileWelcome.vue";
import RecentTransaction from "@/components/dashboards/modern/RecentTransaction.vue";
import TopTraderPerformer from '@/components/dashboards/modern/TopTraderPerformer.vue'
import { createStompClient, subscribeTopic } from '@/services/ws.ts';
import {onMounted, onUnmounted} from "vue";
import {useAuth} from "@/store/auth.ts";

const authStore = useAuth();
let stompClient: any;
let unsubscribe: () => void;

onMounted(() => {
  // Now authStore is defined and accessible
  if (authStore.user.value) {
    stompClient = createStompClient(() => {
      unsubscribe = subscribeTopic(
          stompClient,
          `/topic/portfolio/${authStore.user.value?.id}`,
          (data) => {
            console.log("Live Update Received:", data);
          }
      );
    });
  }
});

onUnmounted(() => {
  if (unsubscribe) unsubscribe();
  if (stompClient) stompClient.deactivate();
});
</script>

<template>

  <div class="grid grid-cols-12 gap-6">
    <div class="col-span-12">
      <ProfileWelcome />
    </div>
    <div class="lg:col-span-8 col-span-12">
      <RevenueUpdate />
    </div>
    <div class="lg:col-span-4 col-span-12">
      <div class="grid grid-cols-12 gap-6">
        <div class="col-span-12">
          <PortfolioValue />
        </div>
      </div>
    </div>
    <div class="lg:col-span-4 col-span-12">
      <RecentTransaction />
    </div>
    <div class="lg:col-span-8 col-span-12 flex">
      <TopTraderPerformer />
    </div>
  </div>
</template>
