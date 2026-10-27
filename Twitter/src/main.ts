import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import { useAuthStore } from './stores/auth'

async function bootstrap() {
  const app = createApp(App)

  app.use(createPinia())

  // Restore the session from the HttpOnly refresh cookie before the first
  // navigation so the router guard does not flash the login page.
  await useAuthStore().ensureSession()

  app.use(router)
  await router.isReady()
  app.mount('#app')
}

bootstrap()
