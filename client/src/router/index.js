import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      redirect: "/user/login"
    },
    {
      path: "/user/login",
      name: "userLogin",
      component: () => import("@/views/user/Login.vue"),
    },
    {
      path: "/user/reg",
      name: "userReg",
      component: () => import("@/views/user/Reg.vue"),
    },
    {
      path: "/user/main",
      name: "userMain",
      component: () => import("@/views/user/Main.vue"),
    },
  ],
})

export default router
