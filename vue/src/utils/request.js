import axios from "axios"
import { getToken, removeToken} from "./cookie";
import router from "@/router";

// 设置请求的格式
axios.defaults.headers["Content-Type"] = "application/json;charset=utf-8";

// 创建axios实例
const service = axios.create({
    baseURL: "/api-dev",
    timeout: 60000,
});

// 请求拦截器
service.interceptors.request.use(
    (config) => {
        // 如果token存在则加上请求
        if (getToken()) {
            config.headers["Authorization"] = "Bearer " + getToken();
        }
        return config;
    },
    (error) => {
        Promise.reject(error);
    }
);

// 响应拦截器
service.interceptors.response.use(
    (res) => {
        const code = res.data.code;
        const msg = res.data.msg;

        if (code === 3000) {
            // 鉴权失败
            ElMessage.error(msg)
            removeToken() // 删除token
            router.push('/user/login') // 路由回登录界面
            return Promise.reject(new Error(msg));
        } else if (code !== 200) {
            // 操作失败
            ElMessage.error(msg)
            return Promise.reject(new Error(msg));
        } else {
            return Promise.resolve(res.data);
        }
    },
    (error) => {
        return Promise.reject(error);
    }
);

export default service;