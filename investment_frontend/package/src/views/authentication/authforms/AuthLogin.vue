<script setup lang="ts">
import {ref} from 'vue'
import {useRouter, RouterLink} from 'vue-router'
import {useAuth} from '@/store/auth'
import Button from '@/components/ui/button/Button.vue'
import Checkbox from '@/components/ui/checkbox/Checkbox.vue'
import Label from '@/components/ui/label/Label.vue'
import {Input} from '@/components/ui/input'

const router = useRouter()
const authStore = useAuth()

const name = ref('')
const password = ref('')
const rememberDevice = ref(false)
const errorMessage = ref('')

async function onSubmit(event: Event) {
  event.preventDefault()
  errorMessage.value = ''

  try {
    const success = await authStore.login(name.value, password.value)
    if (success) {
      router.push({ name: 'Home' })
    } else {
      errorMessage.value = 'Invalid credentials'
    }
  } catch (err) {
    errorMessage.value = 'Server connection failed'
  }
}
</script>

<template>
  <form @submit="onSubmit" class="mt-6">
    <p v-if="errorMessage" class="text-error text-sm mb-2">{{ errorMessage }}</p>

    <div class="mb-4">
      <div className="mb-2 block">
        <Label for="Username">Username</Label>
      </div>
      <Input id="name" v-model="name" class="form-control" required/>
    </div>

    <div class="mb-4">
      <div className="mb-2 block">
        <Label for="userpwd">Password</Label>
      </div>
      <Input id="userpwd" type="password" v-model="password" class="form-control" required/>
    </div>

    <div class="flex justify-between my-5 items-center">
      <div class="flex items-center gap-2">
        <Checkbox id="accept" v-model="rememberDevice" class="checkbox"/>
        <Label for="accept" class="opacity-90 font-normal cursor-pointer mt-2">
          Remember this Device
        </Label>
      </div>
      <RouterLink to="#" class="text-primary text-sm font-medium">
        Forgot Password?
      </RouterLink>
    </div>
    <Button type="submit" class="w-full">
      Log in
    </Button>
  </form>
</template>