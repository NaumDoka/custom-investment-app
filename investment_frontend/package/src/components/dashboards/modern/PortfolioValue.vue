<script setup lang="ts">
import {onMounted, ref, computed, watch} from 'vue'
import { useAuth } from '@/store/auth.ts'
import { Card } from '@/components/ui/card'
import { Icon } from '@iconify/vue'
import VueApexCharts from "vue3-apexcharts";
import {usePortfolioStore} from "@/store/portfolioValue.ts";

const store = usePortfolioStore();
const { user } = useAuth();
const displayValue = computed(() => {
  return portfolioData.value.reduce((sum, p) => sum + (p.value || 0), 0);
});
const previousValue = ref(0);
const portfolioData = computed(() => store.currentUserPortfolio);

watch(displayValue, (newVal, oldVal) => {
  if (oldVal !== undefined && oldVal !== newVal) previousValue.value = oldVal;
});

const diffPercent = computed(() => {
  if (previousValue.value === 0) return 0;
  return ((displayValue.value - previousValue.value) / previousValue.value) * 100;
})

// Donut Chart Config
const chartOptions = computed(() => ({
  chart: { type: 'donut', fontFamily: 'inherit' },
  labels: portfolioData.value.map(p => p.asset),
  colors: ["#5D87FF", "#FFDF00", "#49BEFF"],
  plotOptions: { pie: { donut: { size: '75%' } } },
  dataLabels: { enabled: false },
  legend: { show: false },
  stroke: { show: false }
}));

const series = computed(() => portfolioData.value.map(p => p.value));

onMounted(() => {
  store.startPolling(user.value?.id);
});
</script>

<template>
  <Card class="p-6">
    <div class="grid grid-cols-12 gap-4">
      <div class="col-span-7">
        <h5 class="text-sm font-medium text-muted-foreground mb-4">Portfolio Breakup</h5>
        <h4 class="text-2xl font-bold mb-2">${{ displayValue.toLocaleString(undefined, { minimumFractionDigits: 2 }) }}</h4>

        <div class="flex items-center gap-2 mb-4">
          <span :class="['rounded-full p-1 flex items-center', diffPercent >= 0 ? 'bg-green-100' : 'bg-red-100']">
            <Icon :icon="diffPercent >= 0 ? 'tabler:arrow-up-left' : 'tabler:arrow-down-right'"
                  :class="diffPercent >= 0 ? 'text-green-600' : 'text-red-600'" />
          </span>
          <p class="text-sm font-semibold">{{ diffPercent >= 0 ? '+' : '' }}{{ diffPercent.toFixed(1) }}%</p>
          <p class="text-xs text-muted-foreground">last 3 mins</p>
        </div>

        <div class="flex flex-wrap gap-3 mt-4">
          <div v-for="(item, idx) in portfolioData" :key="item.asset" class="flex items-center">
            <Icon icon="tabler:point-filled" :style="{ color: chartOptions.colors[idx % 3] }" class="mr-1" />
            <span class="text-xs text-muted-foreground">{{ item.asset }}</span>
          </div>
        </div>
      </div>

      <div class="col-span-5 flex justify-center items-center">
        <VueApexCharts type="donut" height="150" :options="chartOptions" :series="series" />
      </div>
    </div>
  </Card>
</template>