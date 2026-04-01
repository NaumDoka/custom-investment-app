<script setup lang="ts">
import { Table, TableHeader, TableRow, TableHead, TableBody, TableCell, TableFooter } from "@/components/ui/table/index";
import { Card } from "@/components/ui/card/index";
import { Button } from "@/components/ui/button";
import { Icon } from '@iconify/vue';
import api from "@/services/api.ts";
import { toast } from "vue-sonner";
import { computed, onMounted, onUnmounted, ref, watch } from "vue";
import { getAssetIcon } from "@/_mockApis/dashboardData/assetIcons.ts";
import { createStompClient, subscribeTopic } from "@/services/ws.ts";

const currentPage = ref(1);
const itemsPerPage = 5;
const props = defineProps<{ tableData: any[] }>();
const localAssets = ref([...props.tableData]);
const paginatedAssets = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage;
  const end = start + itemsPerPage;
  return localAssets.value.slice(start, end);
});

const totalPages = computed(() => Math.ceil(localAssets.value.length / itemsPerPage));

watch(() => props.tableData, (newData) => {
  localAssets.value = [...newData];
}, { deep: true });
let unsubscribe: () => void;

onMounted(() => {
  const client = createStompClient();
  client.onConnect = () => {
    unsubscribe = subscribeTopic(client, '/topic/update', (updatedAsset) => {
      const index = localAssets.value.findIndex(a => a.id === updatedAsset.id);
      if (index !== -1) {
        // This makes the number jump instantly in the UI
        localAssets.value[index].price = updatedAsset.price;
      }
    });
  };
});

onUnmounted(() => { if (unsubscribe) unsubscribe(); });

const strength = ref(0.002);
const emit = defineEmits(['refresh']);

const handleMarketMove = async (type: 'pump' | 'dump', assetName: string) => {
  try {
    const multiplier = type === 'pump' ? strength.value : -strength.value;

    // Calling the /admin/pump or /admin/dump endpoints for the specific asset
    await api.post(`/admin/${type}/${assetName}?amount=${multiplier}`);
    emit('refresh');
  } catch (error) {
    console.error(`Failed to move market`, error);
    toast.error("Action failed.");
  }
}
</script>

<template>
  <Card class="border shadow-none p-0 overflow-auto">
    <div class="flex items-center justify-between px-5 py-4 bg-muted/30 border-b">
      <div class="flex items-center gap-2">
        <Icon icon="solar:settings-bold-duotone" class="text-primary" width="22" />
        <span class="text-sm font-bold uppercase tracking-widest text-muted-foreground">Intensity Controls</span>
      </div>

      <div class="flex items-center gap-4">
        <div class="flex items-center gap-2">

          <div class="flex items-center bg-background border rounded-lg overflow-hidden h-9">
            <button
                @click="strength = Number(Math.max(0, strength - 0.001).toFixed(4))"
                class="h-full px-3 hover:bg-red-500/10 text-muted-foreground hover:text-red-500 transition-all border-r group"
                type="button"
            >
              <Icon icon="solar:minus-circle-bold" width="20" class="group-active:scale-90" />
            </button>

            <div class="relative flex items-center">
              <input
                  id="strength"
                  v-model.number="strength"
                  type="number"
                  step="0.001"
                  min="0"
                  max="0.1"
                  class="w-16 bg-transparent border-none focus:ring-0 text-sm font-mono text-center font-bold text-primary p-0 no-spinner"
              />
            </div>

            <button
                @click="strength = Number(Math.min(0.05, strength + 0.001).toFixed(4))"
                class="h-full px-3 hover:bg-green-500/10 text-muted-foreground hover:text-green-500 transition-all border-l group"
                type="button"
            >
              <Icon icon="solar:add-circle-bold" width="20" class="group-active:scale-90" />
            </button>
          </div>

          <div class="flex items-center justify-center px-3 py-2 bg-primary text-primary-foreground rounded-xl min-w-[65px] shadow-sm">
            <span class="text-xs font-black font-mono tracking-tighter">
              {{ (strength * 100).toFixed(2) }}%
            </span>
          </div>
        </div>
      </div>
    </div>

    <Table>
      <TableHeader>
        <TableRow class="hover:bg-transparent">
          <TableHead class="px-5 py-3">Asset Name</TableHead>
          <TableHead class="px-5 py-3">Current Price</TableHead>
          <TableHead class="px-5 py-3 text-right">Market Actions</TableHead>
        </TableRow>
      </TableHeader>

      <TableBody>
        <TableRow v-for="asset in paginatedAssets" :key="asset.id" class="hover:bg-muted/10">
          <TableCell>
            <div class="flex items-center gap-3">
              <div class="p-2 bg-primary/10 rounded-lg text-primary">
                <Icon :icon="getAssetIcon(asset.name)" width="20" />
              </div>
              <span class="font-bold text-lg">{{ asset.name }}</span>
            </div>
          </TableCell>

          <TableCell class="font-mono text-base">
            ${{ asset.price?.toLocaleString() }}
          </TableCell>

          <TableCell class="text-right">
            <div class="flex justify-end gap-2">
              <Button
                  variant="outline"
                  class="border-green-500 text-green-600 hover:bg-green-50 dark:hover:bg-green-950/20"
                  size="sm"
                  @click="handleMarketMove('pump', asset.name)"
              >
                <Icon icon="solar:double-alt-arrow-up-bold" class="mr-1" />
                Pump
              </Button>

              <Button
                  variant="outline"
                  class="border-red-500 text-red-600 hover:bg-red-50 dark:hover:bg-red-950/20"
                  size="sm"
                  @click="handleMarketMove('dump', asset.name)"
              >
                <Icon icon="solar:double-alt-arrow-down-bold" class="mr-1" />
                Dump
              </Button>
            </div>
          </TableCell>
        </TableRow>
      </TableBody>
      <TableFooter class="bg-muted/20 border-t">
        <TableRow class="hover:bg-transparent">
          <TableCell colspan="3" class="px-5 py-4">
            <div class="flex items-center justify-between">
              <div class="text-xs text-muted-foreground font-medium">
                Showing <span class="text-foreground">{{ (currentPage - 1) * itemsPerPage + 1 }}</span>
                to <span class="text-foreground">{{ Math.min(currentPage * itemsPerPage, localAssets.length) }}</span>
                / out of {{ localAssets.length }} assets
              </div>

              <div class="flex items-center gap-2">
                <Button
                    variant="outline"
                    size="sm"
                    :disabled="currentPage === 1"
                    @click="currentPage--"
                    class="h-8 w-8 p-0 rounded-lg"
                >
                  <Icon icon="solar:alt-arrow-left-bold" />
                </Button>

                <div class="flex items-center gap-1 mx-2">
                  <span class="text-xs font-bold text-primary px-2 py-1 bg-primary/10 rounded-md">
                    {{ currentPage }}
                  </span>
                  <span class="text-xs text-muted-foreground">/</span>
                  <span class="text-xs text-muted-foreground">{{ totalPages }}</span>
                </div>

                <Button
                    variant="outline"
                    size="sm"
                    :disabled="currentPage === totalPages || totalPages === 0"
                    @click="currentPage++"
                    class="h-8 w-8 p-0 rounded-lg"
                >
                  <Icon icon="solar:alt-arrow-right-bold" />
                </Button>
              </div>
            </div>
          </TableCell>
        </TableRow>
      </TableFooter>
    </Table>
  </Card>
</template>

<style scoped>
.no-spinner::-webkit-inner-spin-button,
.no-spinner::-webkit-outer-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

.no-spinner {
  -moz-appearance: textfield; /* Firefox */
}
</style>