<script setup lang="ts">
import { Table, TableHeader, TableRow, TableHead, TableBody, TableCell } from "@/components/ui/table/index";
import { Badge } from "@/components/ui/badge";
import { Card } from "@/components/ui/card/index";
import Avatar from "../ui/avatar/Avatar.vue";
import AvatarFallback from "../ui/avatar/AvatarFallback.vue";
import api from "@/services/api.ts";
import {toast} from "vue-sonner";
import {Button} from "@/components/ui/button";
import { usePortfolioStore } from "@/store/portfolioValue.ts";
import { useAuth } from "@/store/auth.ts";

defineProps<{
  tableData: any[]
}>()

const portfolioStore = usePortfolioStore();
const { user } = useAuth();

const emit = defineEmits(['refresh']);

const toggleRole = async (userToChangeRole:any) => {
  try {
    const newRole = userToChangeRole.role === 'ADMIN' ? 'CLIENT' : 'ADMIN';
    await api.put(`/admin/update-user-role/${userToChangeRole.id}`, {
      ...user,
      role: newRole
    });

    toast.success(`Role updated to ${newRole} for ${userToChangeRole.name}`);
    await portfolioStore.fetchAllData(user.value?.id)
    emit('refresh');
  } catch (error) {
    console.error("Failed to update role", error);
  }
}

const deleteUser = async (userToDelete:any) => {
  try {
    await api.delete(`/admin/delete-user/${userToDelete.id}`);
    toast.success(`User deleted for ${userToDelete.name}`);

    await portfolioStore.fetchAllData(user.value?.id)
    emit('refresh');
  } catch (error) {
    console.error("Failed to delete user", error);
  }
}

</script>

<template>
  <Card class="border shadow-none p-0">
    <Table>
      <TableHeader>
        <TableRow class="hover:bg-transparent">
          <TableHead class="px-5 py-3">User</TableHead>
          <TableHead class="px-5 py-3">Email</TableHead>
          <TableHead class="px-5 py-3">Phone</TableHead>
          <TableHead class="px-5 py-3 text-center">Role</TableHead>
          <TableHead class="px-5 py-3 text-right">Actions</TableHead>
        </TableRow>
      </TableHeader>

      <TableBody>
        <TableRow v-for="user in tableData" :key="user.id" class="hover:bg-muted/10">
          <TableCell>
            <div class="flex items-center gap-3">
              <Avatar class="h-10 w-10">
                <AvatarFallback>{{ user.name?.charAt(0) || 'U' }}</AvatarFallback>
              </Avatar>
              <span class="font-semibold">{{ user.name }}</span>
            </div>
          </TableCell>

          <TableCell>{{ user.email }}</TableCell>

          <TableCell>{{ user.phone || 'N/A' }}</TableCell>

          <TableCell class="text-center">
            <Badge :variant="user.role === 'ADMIN' ? 'default' : 'secondary'">
              {{ user.role }}
            </Badge>
          </TableCell>

          <TableCell class="text-right">
            <Button variant="outline" size="sm" @click="toggleRole(user)">
              Change to {{ user.role === 'ADMIN' ? 'Client' : 'Admin' }}
            </Button>
            <Button variant="outlineerror" size="sm" @click="deleteUser(user)" class="ml-2">
              X
            </Button>
          </TableCell>
        </TableRow>
      </TableBody>
    </Table>
  </Card>
</template>
