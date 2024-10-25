import {createRouter, createWebHistory} from 'vue-router'
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
            path: "/admin/login",
            name: "adminLogin",
            component: () => import("@/views/Admin-Login.vue"),
        },
        {
            path: "/",
            redirect: "/user/main"
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
        },
        {
            path: "/admin/layout",
            name: "layout",
            component: () => import("@/views/Admin-Layout.vue"),
            children: [
                {
                    path: "/admin/user",
                    name: "adminUser",
                    component: () => import("@/views/Admin-User.vue"),
                },
                {
                    path: "/admin/apply",
                    name: "adminApply",
                    component: () => import("@/views/Admin-Apply.vue"),
                },
            ]
        },
    ]
})

export default router

// 路由前置守卫
router.beforeEach((to, from, next) => {
    if (to.path === '/user/register' || to.path === '/admin/login') {}
        return next()
    if (getToken()) {
        // 已经登录并且token没有过期则不需要到登录界面
        if (to.path === '/user/login') {
            next({path: '/user/main'})
        } else {
            next()
        }
    } else {
        // 没有登录或者token过期则跳转到登录页
        if (to.path !== '/user/login') {
            next({path: '/user/login'})
        } else {
            next()
        }
    }
})