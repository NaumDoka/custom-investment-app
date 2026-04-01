<script setup lang="ts">
import { ref, onMounted } from 'vue'
import api from '@/services/api'
import AssetControlTable from '@/components/shadcn-tables/AssetControlTable.vue'
import BreadcrumbComp from '@/components/shared/BaseBreadcrumb.vue'

const assetList = ref([])
const breadcrumbs = ref([{ text: 'Admin', href: '#' }, { text: 'Market Administration' }])

const fetchAssets = async () => {
  try {
    const res = await api.get('/assets')
    assetList.value = res.data
  } catch (err) {
    console.error("Asset fetch failed", err)
  }
}

onMounted(fetchAssets)
</script>

<template>
  <div class="p-6 space-y-8">
    <BreadcrumbComp title="Market Administration" :breadcrumbs="breadcrumbs" />

    <div class="space-y-4">
      <h3 class="text-xl font-semibold">Live Asset Management</h3>
      <AssetControlTable :tableData="assetList" @refresh="fetchAssets" />
    </div>
  </div>
</template>