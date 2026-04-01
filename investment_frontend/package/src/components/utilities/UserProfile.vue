<script setup>
import { ref, reactive, watch, computed } from 'vue'
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from '@/components/ui/dialog'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Icon } from '@iconify/vue'
import user1 from '@/assets/images/profile/user-1.jpg'
import CardBox from '../shared/CardBox.vue'
import { countries } from "@/_mockApis/countries/countriesData.ts";
import {
  Select,
  SelectTrigger,
  SelectItem,
  SelectContent,
} from "@/components/ui/select";
import {useAuth} from "@/store/auth.ts";
import api from "@/services/api.ts";
import {toast} from "vue-sonner";

const authStore = useAuth()
const user = authStore.user

const phoneDigits = ref('')
const selectedCountryCode = ref(countries[0].code)

const selectedCountry = computed(() =>
    countries.find(c => c.code === selectedCountryCode.value) || countries[0]
)

watch(phoneDigits, (val) => {
  const cleaned = val.replace(/[^0-9]/g, '');
  if (val !== cleaned) phoneDigits.value = cleaned;
})

const openModal = ref(false)
const modalType = ref(null) 

const personal = reactive({
  name: user.value?.name || 'User',
  email: user.value?.email || '',
  phone: user.value?.phone || '',
  role: user.value?.role || 'CLIENT',
  status: 'Active Trader'
})

const tempPersonal = reactive({ ...personal })

watch([openModal, modalType], () => {
  if (openModal.value && modalType.value === 'personal') {
    Object.assign(tempPersonal, personal)

    const foundCountry = countries.find(c => personal.phone.startsWith(c.prefix));
    if (foundCountry) {
      selectedCountryCode.value = foundCountry.code;
      phoneDigits.value = personal.phone.replace(foundCountry.prefix, '');
    } else {
      phoneDigits.value = personal.phone;
    }
  }
})

async function handleSave() {
  try {
    tempPersonal.phone = `${selectedCountry.value.prefix}${phoneDigits.value}`;
    const userId = user.value.id;
    const response = await api.put(`/auth/update-profile/${userId}`, tempPersonal);
    authStore.setUser(response.data, authStore.token.value)
    Object.assign(personal, response.data);

    openModal.value = false;
    toast.success("Profile updated successfully!");
  } catch (error) {
    console.error("Failed to update profile", error);
    toast.error("Error updating profile.");
  }
}

const socialLinks = [
  { href: personal.facebook, icon: 'streamline-logos:facebook-logo-2-solid' },
  { href: personal.twitter, icon: 'streamline-logos:x-twitter-logo-solid' },
  { href: personal.github, icon: 'ion:logo-github' },
  { href: personal.dribbble, icon: 'streamline-flex:dribble-logo-remix' }
]
</script>

<template>
  <div class="flex flex-col gap-6">
    <!-- Profile Header -->
    <CardBox class="p-6 overflow-hidden">
      <div class="flex flex-col sm:flex-row items-center gap-6">
        <img :src="user1" class="rounded-full" width="70" height="70" />

        <div class="flex flex-wrap gap-4 justify-center sm:justify-between items-center w-full">
          <div class="flex flex-col sm:text-left text-center gap-1.5">
            <h5 class="card-title">{{ personal.name }}</h5>
            <div class="flex flex-wrap items-center gap-1 md:gap-3">
              <p class="text-sm text-gray-500 dark:text-gray-400">{{ personal.role }}</p>
              <div class="hidden h-4 w-px bg-gray-300 dark:bg-gray-700 xl:block"></div>
              <p class="text-sm text-gray-500 dark:text-gray-400">{{ personal.status }}</p>
            </div>
          </div>
          <div class="flex items-center gap-2">
            <a
              v-for="(item, index) in socialLinks"
              :key="index"
              :href="item.href"
              target="_blank"
              class="flex h-11 w-11 items-center justify-center gap-2 rounded-full shadow-md border border-ld hover:bg-gray-50 dark:hover:bg-white/[0.03] dark:hover:text-gray-200"
            >
              <Icon :icon="item.icon" width="20" height="20" />
            </a>
          </div>
        </div>
      </div>
    </CardBox>

    <!-- Info Cards -->
    <div class="grid grid-cols-1 xl:grid-cols-2 gap-6">
      <!-- Personal -->
      <div class="space-y-6 rounded-xl border border-ld md:p-6 p-4">
        <h5 class="card-title">Personal Information</h5>
        <div class="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:gap-7 2xl:gap-x-32">
          <div><p class="text-xs text-gray-500">UserName</p><p class="text-link dark:text-darklink">{{ personal.name }}</p></div>
          <div><p class="text-xs text-gray-500">Email</p><p class="text-link dark:text-darklink">{{ personal.email }}</p></div>
          <div><p class="text-xs text-gray-500">Phone</p><p class="text-link dark:text-darklink">{{ personal.phone }}</p></div>
          <div><p class="text-xs text-gray-500">Position</p><p class="text-link dark:text-darklink">{{ personal.role }}</p></div>
          <div><p class="text-xs text-gray-500">Status</p><p class="text-link dark:text-darklink">{{ personal.status }}</p></div>
        </div>
        <div class="flex justify-end">
          <Button size="sm" @click="modalType = 'personal'; openModal = true" class="flex items-center gap-1.5">
            <Icon icon="ic:outline-edit" width="18" height="18" /> Edit
          </Button>
        </div>
      </div>
    </div>

    <!-- Edit Modal -->
    <Dialog v-model:open="openModal" >
      <DialogContent class="sm:max-w-lg max-w-2xl overflow-y-auto" >
        <DialogHeader>
          <DialogTitle class="mb-4">
            {{ modalType === 'personal' ? 'Edit Personal Information' : 'Edit Address Details' }}
          </DialogTitle>
        </DialogHeader>

        <div class="grid grid-cols-1 gap-4 lg:grid-cols-2">
          <template v-if="modalType === 'personal'">
            <div class="flex flex-col gap-2">
              <Label for="firstName">Username</Label>
              <Input v-model="tempPersonal.name" placeholder="Username" id="username" />
            </div>
            <div class="flex flex-col gap-2">
              <Label for="email">Email</Label>
              <Input v-model="tempPersonal.email" placeholder="Email" id="email" />
            </div>
            <div class="flex flex-col gap-2">
              <Label for="phone">Phone Number</Label>
              <div class="flex items-center group">
                <div class="relative h-10">
                  <Select v-model="selectedCountryCode">
                    <SelectTrigger class="w-[100px] h-10 rounded-r-none border-r-0 bg-muted/30 focus:ring-0 focus:ring-offset-0">
                      <div class="flex items-center gap-2">
                        <Icon :icon="selectedCountry.flag" width="20" class="shrink-0" />
                        <span class="font-bold font-mono text-sm">{{ selectedCountry.prefix }}</span>
                      </div>
                    </SelectTrigger>
                    <SelectContent class="bg-slate-900 border-slate-800 text-white shadow-2xl z-[100]">
                      <SelectItem
                          v-for="country in countries"
                          :key="country.code"
                          :value="country.code"
                          class="focus:bg-primary/20 focus:text-primary-foreground cursor-pointer"
                      >
                        <div class="flex items-center gap-2 py-1">
                          <Icon :icon="country.flag" width="18" />
                          <span class="text-xs">{{ country.name }} ({{ country.prefix }})</span>
                        </div>
                      </SelectItem>
                    </SelectContent>
                  </Select>
                </div>
                <Input
                    id="phone"
                    type="text"
                    v-model="phoneDigits"
                    class="rounded-l-none border-l-1 focus-visible:ring-0 focus-visible:ring-offset-0 h-10 flex-1"
                />
              </div>
            </div>
            <div class="flex flex-col gap-2">
              <Label for="status">Status</Label>
              <Input v-model="tempPersonal.status" placeholder="Status" id="status" />
            </div>
          </template>
        </div>

        <DialogFooter class=" flex flex-wrap flex-row  gap-2 mt-4">
          <Button @click="handleSave">Save Changes</Button>
          <Button
           
            variant="lighterror"
            @click="openModal = false"
          >
            Close
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  </div>
</template>
