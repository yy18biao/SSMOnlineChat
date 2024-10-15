import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import "@/assets/main.scss"
import store from "@/store/index.js";

const app = createApp(App)

app.use(router)
app.use(store);

app.mount('#app')

export default {
    watch: {
        '$route'(to, from) {
            // 在这里处理路由变化时的逻辑
            // 例如，重新获取数据或重置组件状态
            this.fetchData();
        }
    },
    methods: {
        fetchData() {
            // 获取数据的逻辑
        }
    },
    created() {
        // 在组件创建时获取数据
        this.fetchData();
    }
};
