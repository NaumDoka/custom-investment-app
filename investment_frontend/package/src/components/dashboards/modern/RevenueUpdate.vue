<script setup lang="ts">
import {ref, watch, onMounted, onUnmounted, computed} from 'vue'
import { createStompClient, subscribeTopic } from "@/services/ws.ts";
import VueApexCharts from 'vue3-apexcharts'
import {
  Select,
  SelectTrigger,
  SelectContent,
  SelectGroup,
  SelectItem,
  SelectValue,
} from '@/components/ui/select/index'
import Card from '@/components/ui/card/Card.vue'
import { getAssetIcon } from "@/_mockApis/dashboardData/assetIcons.ts";
import { Icon } from '@iconify/vue'
import api from '@/services/api.ts'

const isVolumeFocused = ref(false)
const selectedAsset = ref('BTC')
const assets = ref<any[]>([])

interface ChartDataPoint {
  x: number;
  y: number | number[];
}

interface ChartSeries {
  name: string;
  type: string;
  data: ChartDataPoint[];
}

const chartSeries = ref<ChartSeries[]>([
  { name: 'Price', type: 'candlestick', data: [] },
  { name: 'Volume', type: 'column', data: [] }
]);

let stompClient: any;
let unsubscribe: (() => void) | null = null;

const connectWS = async (assetName: string) => {
  if (unsubscribe) unsubscribe(); // Kill old subscription
  if (!stompClient) return;

  try {
    const res = await api.get(`/market/history/${assetName}`);
    const priceData: any[] = [];
    const volumeData: any[] = [];

    res.data.forEach((c: any) => {
      const ts = new Date(c.time).getTime();
      priceData.push({ x: ts, y: [c.open, c.high, c.low, c.close] });
      volumeData.push({ x: ts, y: c.volume });
    });

    chartSeries.value = [
      { name: 'Price', type: 'candlestick', data: priceData },
      { name: 'Volume', type: 'column', data: volumeData }
    ];
  } catch (error) {
    console.error("Failed to fetch history:", error);
  }

  unsubscribe = subscribeTopic(stompClient, `/topic/candles/${assetName}`, (candle) => {
    const ts = new Date(candle.time).getTime();
    const priceData = [...chartSeries.value[0].data];
    const volData = [...chartSeries.value[1].data];

    if (priceData.length > 0) {
      const lastIndex = priceData.length - 1;
      const lastPoint = priceData[lastIndex];

      if (lastPoint.x === ts) {
        // REPLACE: Update the current 1-minute candle (Turbo effect)
        priceData[lastIndex] = { x: ts, y: [candle.open, candle.high, candle.low, candle.close] };
        volData[lastIndex] = { x: ts, y: candle.volume };
      } else {
        // APPEND: The minute has changed, push a new candle
        priceData.push({ x: ts, y: [candle.open, candle.high, candle.low, candle.close] });
        volData.push({ x: ts, y: candle.volume });
      }
    } else {
      priceData.push({ x: ts, y: [candle.open, candle.high, candle.low, candle.close] });
      volData.push({ x: ts, y: candle.volume });
    }

    // Keep only the last 50 bars
    chartSeries.value = [
      { name: 'Price', type: 'candlestick', data: priceData.slice(-50) },
      { name: 'Volume', type: 'column', data: volData.slice(-50) }
    ];
  });
}

const chartOptions = computed(() => ({
  chart: {
    type: 'line',
    toolbar: { show: false },
    animations: {
      enabled: false,
      easing: 'linear',
      dynamicAnimation: { speed: 800 }
    },
    events: {
      legendClick: (_chartContext: any, seriesIndex: number) => {
        if (seriesIndex === 1) {
          isVolumeFocused.value = !isVolumeFocused.value;
        }
      }
    },
  },
  states: {
    hover: {
      filter: {
        type: 'lighten',
        value: 0.55,
      }
    }
  },
  stroke: { width: [1, 0] },
  fill: {
    type: 'solid',
    opacity: [1, isVolumeFocused.value ? 1.8 : 0.25],
    colors: ['#22C55E', '#49BEFF']
  },
  legend: {
    show: true,
    position: 'bottom',
    horizontalAlign: 'center',
    onItemClick: {
      toggleDataSeries: false
    }
  },
  xaxis: {
    type: 'datetime',
    labels: { datetimeUTC: false, format: 'HH:mm', style: { colors: '#94a3b8' } },
    range: 1000 * 60 * 30,
    tickAmount: 10
  },
  yaxis: [
    {
      seriesName: 'Price',
      forceNiceScale: true,
      title: { text: 'Price (€)' },
      labels: { formatter: (val: number) => val.toFixed(0), style: { colors: '#94a3b8' } }
    },
    {
      seriesName: 'Volume',
      opposite: true,
      title: { text: 'Volume' },
      max: (max: number) => max * 2.5,
      labels: { show: false }
    }
  ],
  plotOptions: {
    candlestick: {
      colors: { upward: '#22c55e', downward: '#ef4444' },
      wick: { useFillColor: true }
    },
    bar: {
      columnWidth: '70%',
      colors: {
        ranges: [{ from: 0, to: 999999999, color: '#49BEFF' }]
      }
    }
  },
  tooltip: {
    shared: true,
    theme: 'dark',
    custom: ({ seriesIndex: _, dataPointIndex, w }: any) => {
      const o = w.globals.seriesCandleO[0][dataPointIndex]
      const h = w.globals.seriesCandleH[0][dataPointIndex]
      const l = w.globals.seriesCandleL[0][dataPointIndex]
      const c = w.globals.seriesCandleC[0][dataPointIndex]
      const v = w.globals.series[1][dataPointIndex]

      return `<div class="p-3 dark:bg-slate-900 border border-slate-700 text-xs text-white rounded shadow-2xl">
        <div class="mb-2 text-gray-400 border-b border-slate-700 pb-1 font-mono">
          ${new Date(w.globals.seriesX[0][dataPointIndex]).toLocaleTimeString()}
        </div>
        <div class="grid grid-cols-2 gap-x-4 gap-y-1">
          <div>Open: <b class="text-sky-200">${o.toFixed(2)}</b></div>
          <div>High: <b class="text-green-400">${h.toFixed(2)}</b></div>
          <div>Low: <b class="text-red-400">${l.toFixed(2)}</b></div>
          <div>Close: <b class="text-sky-200">${c.toFixed(2)}</b></div>
        </div>
          <span>Vol:</span> <b style="color: #49BEFF">${Math.round(v).toLocaleString()}</b>
        </div>`
    }
  }
}))

onMounted(async () => {
  const res = await api.get('/assets');
  assets.value = res.data;
  stompClient = createStompClient(() => connectWS(selectedAsset.value));
});

// Switch chart when user selects a different asset
watch(selectedAsset, (newAsset) => connectWS(newAsset));

onUnmounted(() => { if (unsubscribe) unsubscribe(); });
</script>

<template>
  <Card>
    <div class="sm:flex justify-between mb-6 h-full">
      <div>
        <h5 class="card-title">Market Live Feed</h5>
        <p class="card-subtitle mb-5">Real-time asset valuation</p>
      </div>
      <div class="mt-4 sm:mt-0">
        <Select v-model="selectedAsset">
          <SelectTrigger class="font-bold">
            <div class="flex items-center gap-2">
              <Icon :icon="getAssetIcon(selectedAsset)" width="18" class="shrink-0" />
              <SelectValue placeholder="Select Asset" />
            </div>
          </SelectTrigger>
          <SelectContent>
            <SelectGroup>
              <SelectItem v-for="a in assets" :key="a.id" :value="a.name">
                <div class="flex items-center gap-2 py-1">
                  <Icon :icon="getAssetIcon(a.name)" width="18" />
                  <span class="font-bold">{{ a.name }}</span>
                </div>
              </SelectItem>
            </SelectGroup>
          </SelectContent>
        </Select>
      </div>
    </div>
    <VueApexCharts
        v-if="chartSeries[0].data.length > 0"
        type="line"
        :options="chartOptions"
        :series="chartSeries"
        height="350"
    />
    <div v-else class="h-[350px] flex items-center justify-center text-muted-foreground">
      Loading Market Data...
    </div>
  </Card>
</template>
