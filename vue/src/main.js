import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
const app = createApp(App)
import "@/assets/main.scss"

app.use(router)

app.mount('#app')
