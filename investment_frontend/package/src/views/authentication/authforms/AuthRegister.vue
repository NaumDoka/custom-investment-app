<script setup lang="ts">
import {ref, watch, computed} from 'vue'
import { Icon } from '@iconify/vue';
import { countries, type Country } from "@/_mockApis/countries/countriesData.ts";
import Label from '@/components/ui/label/Label.vue'
import { Input } from '@/components/ui/input'
import Button from '@/components/ui/button/Button.vue'
import {useRouter} from "vue-router";
import { useAuth } from "@/store/auth.ts"
import api from "@/services/api.ts";
import {
  Select,
  SelectTrigger,
  SelectItem,
  SelectContent,
} from "@/components/ui/select";

const router = useRouter()
const authStore = useAuth()

const name = ref('')
const email = ref('')
const phoneDigits = ref('')
const selectedCountryCode = ref(countries[0].code)
const selectedCountry = computed((): Country =>
    countries.find(c => c.code === selectedCountryCode.value) || countries[0]
)
const fullPhoneNumber = computed(() => `${selectedCountry.value.prefix}${phoneDigits.value}`);
const phoneError = ref(false)
const phoneErrorMessage = ref('')
const password = ref('')
const passwordError = ref(false);
const passwordRequirements = computed(() => [
  { label: "Must be at least 6 characters long", satisfied: password.value.length > 6 },
  { label: "Missing an uppercase letter", satisfied: /[A-Z]/.test(password.value) },
  { label: "Missing a number", satisfied: /\d/.test(password.value) },
  { label: "Missing a special character", satisfied: /[@$!%*?&#^()_=+\[\]{}|;:,.<>\-]/.test(password.value) },
]);
const isPasswordValid = computed(() => passwordRequirements.value.every(req => req.satisfied));
const errorMessage = ref('')
const nameError = ref(false)
const isCheckingName = ref(false)
let debounceTimer: ReturnType<typeof setTimeout>

watch(phoneDigits, (val) => {
  const cleaned = val.replace(/[^0-9]/g, '');

  if (val !== cleaned) {
    phoneDigits.value = cleaned;
  }

  if (cleaned.length > 0 && cleaned.length < 5) {
    phoneError.value = true;
    phoneErrorMessage.value = "Phone number must be at least 5 digits.";
  } else if (cleaned.length > 15) {
    phoneError.value = true;
    phoneErrorMessage.value = "Phone number must not be longer than 15 digits.";
  } else {
    phoneError.value = false;
    phoneErrorMessage.value = "";
  }
});

watch(name, (newName) => {
  clearTimeout(debounceTimer)
  nameError.value = false

  if (newName.length < 3) return // Don't check tiny names

  isCheckingName.value = true

  // Wait 500ms after the user stops typing
  debounceTimer = setTimeout(async () => {
    try {
      const { data: isAvailable } = await api.get(`/auth/check-username?name=${newName}`)
      nameError.value = !isAvailable
    } catch (error) {
      console.error("Check failed", error)
    } finally {
      isCheckingName.value = false
    }
  }, 500)
})

async function onSubmit(e: Event) {
  e.preventDefault()
  errorMessage.value = ''
  nameError.value = false

  if (!isPasswordValid.value) {
    errorMessage.value = "Please satisfy all password requirements.";
    passwordError.value = true;
    return;
  }

  if (phoneDigits.value.length < 5) {
    errorMessage.value = "Phone number is too short."
    return;
  }
  if (phoneDigits.value.length > 15) {
    errorMessage.value = "Phone number is too long."
    return;
  }

  const result = await authStore.register(name.value, email.value, fullPhoneNumber.value, password.value)
  if (result.success) {
    await router.push('/Home');
  } else {
    if (result.message === "USERNAME_TAKEN") {
      errorMessage.value = "Username is already in use.";
      nameError.value = true;
    } else if (result.message === "EMAIL_TAKEN") {
      errorMessage.value = "An account with this email already exists.";
    } else {
      errorMessage.value = "Registration failed. Please try again.";
    }
  }
}
</script>

<template>
  <form @submit="onSubmit" class="mt-6">
    <p v-if="errorMessage" class="text-error text-sm mb-2">{{ errorMessage }}</p>
    <div class="mb-4">
      <div class="mb-2 block">
        <Label for="name" class="font-semibold">Name</Label>
      </div>
      <div class="relative">
        <Input
            id="name"
            v-model="name"
            :class="['form-control transition-colors', nameError ? 'border-red-500 bg-red-50 dark:bg-red-950/20' : '']"
            required
        />
        <div class="absolute right-3 top-1/2 -translate-y-1/2">
          <Icon v-if="nameError" icon="solar:danger-circle-bold" class="text-red-500" />
          <Icon v-else-if="name.length >= 3 && !isCheckingName" icon="solar:check-circle-bold" class="text-green-500" />
        </div>
      </div>
      <p v-if="nameError" class="text-red-500 text-xs mt-1">
        This username is already taken.
      </p>
    </div>

    <div class="mb-4">
      <div class="mb-2 block">
        <Label for="emadd" class="font-semibold">Email Address</Label>
      </div>
      <Input id="emadd" type="email" v-model="email" class="form-control" required />
    </div>

    <div class="mb-4">
      <Label for="phone" class="font-semibold mb-2 block">Phone Number</Label>

      <div class="flex items-center group">
        <div class="relative h-10">
          <Select v-model="selectedCountryCode">
            <SelectTrigger :class="['w-[110px] h-10 rounded-r-none border-r-0 bg-muted/30 focus:ring-0 focus:ring-offset-0',
              phoneError ? 'border-red-500' : ''
            ]"
            >
              <div class="flex items-center gap-2">
                <Icon :icon="selectedCountry.flag" width="20" class="shrink-0" />
                <span class="font-bold font-mono text-sm">{{ selectedCountry.prefix }}</span>
              </div>
            </SelectTrigger>

            <SelectContent class="bg-slate-900 border-slate-800 text-white shadow-2xl">
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
            placeholder="12345678"
            :class="[
            'rounded-l-none border-l-1 focus-visible:ring-0 focus-visible:ring-offset-0 h-10 flex-1 transition-colors',
            phoneError ? 'border-red-500 bg-red-50 dark:bg-red-950/20' : ''
          ]"
            required
        />
      </div>
      <p v-if="phoneError" class="text-red-500 text-xs mt-1">
        {{ phoneErrorMessage }}
      </p>
    </div>

    <div class="mb-6">
        <div class="mb-2 block">
        <Label for="userpwd" class="font-semibold">Password</Label>
      </div>
      <Input id="userpwd" type="password" v-model="password"
             :class="[
             'form-control transition-colors',
             passwordError && !isPasswordValid ? 'border-red-500 bg-red-50 dark:bg-red-950/2' : ''
             ]"
             required
      />
      <div v-if="password" class="grid grid-cols-1 sm:grid-cols-2 gap-x-4 gap-y-1 mt-3">
        <div v-for="req in passwordRequirements" :key="req.label"
             class="flex items-center gap-2 transition-all duration-300"
             :class="req.satisfied ? 'text-green-500' : 'text-red-500 opacity-70'"
        >
          <Icon :icon="req.satisfied ? 'solar:check-circle-bold' : 'solar:round-alt-arrow-right-bold'"
                :class="req.satisfied ? 'scale-110' : 'scale-90'"
                width="16" />
          <span class="text-xs font-medium">{{ req.label }}</span>
        </div>
      </div>
    </div>

    <Button type="submit" class="w-full ">
      Create Account
    </Button>
  </form>
</template>
