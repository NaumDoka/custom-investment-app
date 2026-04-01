import { createRouter, createWebHistory } from 'vue-router'
import { useAuth } from '@/store/auth'
import MainRoutes from "@/router/MainRoutes.ts";

declare module 'vue-router' {
  interface RouteMeta {
    requiresAuth?: boolean;
    roles?: string[];
  }
}

const routes = [
  {
    path: '/auth',
    component: () => import('@/layouts/blank/BlankLayout.vue'),
    children: [
      {
        path: 'login2',
        name: 'Login',
        component: () => import('@/views/authentication/auth2/Login.vue')
      },
      {
        path: 'register2',
        name: 'Register',
        component: () => import('@/views/authentication/auth2/Register.vue')
      }
    ]
  },

    ...MainRoutes,
  { path: '/:pathMatch(.*)*', redirect: { name: 'Login' } }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// Use an underscore for 'from' to satisfy the linter
router.beforeEach((to, _, next) => {
  const authStore = useAuth();
  const user = authStore.user.value;
  const isAuthenticated = authStore.isAuthenticated.value;

  // 1. If route requires auth and user is NOT logged in
  if (to.meta.requiresAuth && !isAuthenticated) {
    return next({ name: 'Login' });
  }

  // 2. ROLE CHECK: If trying to access admin and role is NOT ADMIN
  if (to.meta.roles && to.meta.roles.length > 0 && user?.role !== 'ADMIN') {
    if (!user || !to.meta.roles.includes(user.role)) {
      return next({ name: 'Home' });
    }
  }

  // 3. If logged in, don't allow access to Log in/Register
  if ((to.name === 'Login' || to.name === 'Register') && isAuthenticated) {
    return next({ name: 'Home' });
  }

  // 4. Otherwise, proceed as normal
  next();
});

export default router