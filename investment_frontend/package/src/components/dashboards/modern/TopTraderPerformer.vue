<template>
  <Card class="h-full w-full">
    <div class="flex flex-col gap-6">
      <!-- Header + Search -->
      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
        <div>
          <h5 class="card-title">Top Trading Performance</h5>
        </div>
      </div>
      <!-- Table -->
      <div class="flex flex-col">
        <div class="-m-1.5 overflow-x-auto">
          <div class="p-1.5 min-w-full inline-block align-middle">
            <div class="overflow-x-auto">
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead class="text-sm font-semibold">User</TableHead>
                    <TableHead class="text-sm font-semibold">Role</TableHead>
                    <TableHead class="text-sm font-semibold">Priority</TableHead>
                    <TableHead class="text-sm font-semibold min-w-28">Portfolio Value</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody class="divide-y divide-border dark:divide-darkborder">
                  <TableRow v-for="item in paginatedPerformers" :key="item.key">
                    <TableCell>
                    <div class="flex flex-col">
                      <span class="font-bold text-sm">{{ item.username }}</span>
                      <span class="text-xs text-muted-foreground mt-2">Active Trader</span>
                    </div>
                    </TableCell>
                    <TableCell>
                      <Badge variant="secondary" class="font-medium">{{ item.designation }}</Badge>
                    </TableCell>
                    <TableCell>
                      <Badge :variant="item.badgeVariant">
                        {{ item.priority }}
                      </Badge>
                    </TableCell>
                    <TableCell>
                      <div class="flex flex-col items-start">
                        <span class="font-bold text-sm">${{ item.budget }}</span>
                        <TooltipProvider :delay-duration="100">
                          <Tooltip>
                            <TooltipTrigger as-child>
                              <div class="flex items-center gap-1 mt-1 cursor-help group">
                                <span class="h-1.5 w-1.5 rounded-full bg-sky-500 animate-pulse"></span>
                                <span class="text-[10px] text-muted-foreground font-mono tabular-nums">{{ store.lastUpdatedTime }}</span>
                              </div>
                            </TooltipTrigger>
                            <TooltipContent
                                side="right"
                                class="bg-slate-900 text-white border-slate-800 px-3 py-1.5 shadow-xl z-50"
                            >
                              <div class="flex flex-col gap-1">
                                <p class="text-[10px] text-slate-400 uppercase font-bold tracking-wider">Sync Status</p>
                                <p class="text-xs font-mono text-sky-400">{{ store.timeAgo }}</p>
                                <p class="text-[9px] text-slate-500 border-t border-slate-800 pt-1 mt-1">
                                  Next refresh in: {{ getNextRefresh(store.lastUpdate) }}
                                </p>
                              </div>
                            </TooltipContent>
                          </Tooltip>
                        </TooltipProvider>
                      </div>
                    </TableCell>
                  </TableRow>
                </TableBody>
                <TableFooter class="bg-muted/20 border-t">
                  <TableRow class="hover:bg-transparent">
                    <TableCell colspan="4" class="px-5 py-4">
                      <div class="flex items-center justify-between">
                        <div class="text-xs text-muted-foreground font-medium">
                          Showing <span class="text-foreground">{{ (currentPage - 1) * itemsPerPage + 1 }}</span>
                          to <span class="text-foreground">{{ Math.min(currentPage * itemsPerPage, performersData.length) }}</span>
                          / out of {{ performersData.length }} traders
                        </div>

                        <div class="flex items-center gap-2">
                          <Button
                              variant="outline"
                              size="sm"
                              :disabled="currentPage === 1"
                              @click="currentPage--"
                              class="h-8 w-8 p-0 rounded-lg"
                          >
                            <Icon icon="solar:alt-arrow-left-bold" width="16" />
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
                            <Icon icon="solar:alt-arrow-right-bold" width="16" />
                          </Button>
                        </div>
                      </div>
                    </TableCell>
                  </TableRow>
                </TableFooter>
              </Table>
            </div>
          </div>
        </div>
      </div>
    </div>
  </Card>
</template>

<script setup lang="ts">
import { Card } from '@/components/ui/card/index'
import { Badge } from '@/components/ui/badge/index';
import { Button } from '@/components/ui/button';
import { Icon } from '@iconify/vue';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
  TableFooter
} from '@/components/ui/table';
import {
  Tooltip,
  TooltipContent,
  TooltipTrigger,
  TooltipProvider
} from "@/components/ui/tooltip";
import {computed, onMounted, ref} from "vue";
import { usePortfolioStore } from "@/store/portfolioValue.ts";
import { useAuth } from "@/store/auth.ts";

const store = usePortfolioStore();
const { user } = useAuth();
const currentPage = ref(1);
const itemsPerPage = 5;

const getNextRefresh = (lastUpdate: Date | null) => {
  if (!lastUpdate) return "Calculating...";
  const nextSync = new Date(lastUpdate.getTime() + 180000);
  const diff = Math.max(0, Math.floor((nextSync.getTime() - new Date().getTime()) / 1000));
  const m = Math.floor(diff / 60);
  const s = diff % 60;
  return `${m}m ${s}s`;
};

const getPriorityStyles = (val: number) => {
  if (val > 100000) return { label: "Critical", variant: "destructive" as const };
  if (val > 50000) return { label: "High", variant: "default" as const };
  if (val > 20000) return { label: "Medium", variant: "secondary" as const };
  return { label: "Low", variant: "outline" as const };
};

const performersData = computed(() => {
  return store.userSummary.map((u: any) => {
    const rawValue = u.totalPortfolioValue || 0;
    const styles = getPriorityStyles(rawValue);
    return {
      key: `user-${u.id}`,
      username: u.email,
      designation: u.role,
      priority: styles.label,
      badgeVariant: styles.variant,
      budget: rawValue.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })
    };
  });
});

const paginatedPerformers = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage;
  const end = start + itemsPerPage;
  return performersData.value.slice(start, end);
});

const totalPages = computed(() => Math.ceil(performersData.value.length / itemsPerPage) || 1);

onMounted(() => {
  store.startPolling(user.value?.id);
});
</script>
