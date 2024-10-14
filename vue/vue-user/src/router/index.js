import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/user/login",
      name: "login",
      component: () => import("@/views/Login.vue"),
    },
    {
      path: "/",
      redirect: "/user/login"
    },
    {
      path: "/user/register",
      name: "register",
      component: () => import("@/views/Register.vue"),
    },
  ]
})

export default router
