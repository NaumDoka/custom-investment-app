<script setup lang="ts">
import {onMounted, ref} from "vue";
import BaseBreadcrumb from "@/components/shared/BaseBreadcrumb.vue";
import HoverTable from "@/components/shadcn-tables/HoverTable.vue";
import api from "@/services/api.ts";

const users = ref<any[]>([]);
const page = ref({ title: "User Administration Table" });
const breadcrumbs = ref([
    {
        text: "Administration Table",
        disabled: true,
        href: "#",
    },
]);

const fetchUsers = async () => {
  try {
    const { data } = await api.get("/auth/users-summary");
    users.value = data;
  } catch (error) {
    console.error("Error fetching users:", error);
  }
}

onMounted(fetchUsers);
</script>

<template>
    <BaseBreadcrumb :title="page.title" :breadcrumbs="breadcrumbs"></BaseBreadcrumb>
    <HoverTable :tableData="users" @refresh="fetchUsers" />
</template>
