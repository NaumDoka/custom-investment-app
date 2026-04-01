const MainRoutes = [
    {
        path: '/',
        meta: { requiresAuth: true },
        component: () => import('../layouts/full/FullLayout.vue'),
        children: [
            {
                path: '/hidden-admin-9823',
                name: 'admin',
                component: () => import('@/components/dashboards/Admin.vue'),
            },
            {
                path: '/Home',
                name: 'Home',
                component: () => import('../views/dashboards/Home.vue'),
            },
            {
                path: '/user-profile',
                name: 'User Profile',
                component: () => import('../views/utilities/UserProfile.vue'),
            },
            {
                name: 'Administration Table',
                path: '/shadcn-table/administration-table',
                component: () => import('../views/shadcn-tables/AdministrationTable.vue')
            },
        ],
    },
];

export default MainRoutes;