import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/admin/login",
      name: "login",
      component: () => import("@/views/Login.vue"),
    },
    {
      path: "/",
      redirect: '/admin/layout',
    },
    {
      path: "/admin/layout",
      name: "layout",
      component: () => import("@/views/Layout.vue"),
      children: [
        {
          path: "/admin/user",
          name: "user",
          component: () => import("@/views/User.vue"),
        },
        {
          path: "/admin/apply",
          name: "apply",
          component: () => import("@/views/Apply.vue"),
        },
      ]
    },
  ]
})

export default router
