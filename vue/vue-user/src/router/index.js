import { createRouter, createWebHistory } from 'vue-router'
import {getToken} from "@/utils/cookie.js";

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
      redirect: "/user/messageSession"
    },
    {
      path: "/user/register",
      name: "register",
      component: () => import("@/views/Register.vue"),
    },
    {
      path: "/user/main",
      name: "main",
      component: () => import("@/views/Main.vue"),
      children: [
        {
          path: "/user/messageSession",
          name: "messageSession",
          component: () => import("@/views/MessageSession.vue"),
          children:[
              {
                  path: "/user/messageSession/chatSession",
                  name: "chatSession",
                  component: () => import("@/views/ChatSession.vue"),
              }
          ]
        },
        {
          path: "/user/friend",
          name: "friend",
          component: () => import("@/views/Friend.vue"),
          children:[
            {
              path: "/user/friendData",
              name: "friendData",
              component: () => import("@/views/FriendData.vue"),
            }
          ]
        },
        {
          path: "/user/set",
          name: "set",
          component: () => import("@/views/Set.vue"),
        },
      ]
    },
  ]
})

export default router


// 路由前置守卫
router.beforeEach((to, from, next) => {
  if(to.path === '/user/register')
    next()
  if (getToken()) {
    // 已经登录并且token没有过期则不需要到登录界面
    if (to.path === '/user/login') {
      next({ path: '/user/layout/' })
    } else {
      next()
    }
  } else {
    // 没有登录或者token过期则跳转到登录页
    if (to.path !== '/user/login') {
      next({ path: '/user/login' })
    } else {
      next()
    }
  }
})