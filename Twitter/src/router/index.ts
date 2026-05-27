import { createRouter, createWebHistory } from 'vue-router'

import JoinView from '../view/JoinView.vue'
import HomeView from '../view/HomeView.vue'
import ExploreView from '../view/ExploreView.vue'
import notificationsView from '../view/notificationsView.vue'
import chatView from '../view/chatView.vue'
import grokView from '../view/grokView.vue'
import bookmarksView from '../view/bookmarksView.vue'
import userView from '../view/userView.vue'
import BasicLayout from '@/layouts/BasicLayout.vue'
import LoginView from '@/view/LoginView.vue'

const router = createRouter({
  // history: createWebHistory(import.meta.env.BASE_URL),
  history: createWebHistory('/VueTweet/'),
  routes: [
    {
      path: '/join',
      meta: { notRequiresAuth: true },
      children: [
        { path: '', component: JoinView },
        { path: 'login', component: LoginView, meta: { notRequiresAuth: true } },
      ],
    },

    {
      path: '/',
      component: BasicLayout,
      children: [
        { path: '', component: HomeView },
        { path: 'explore', component: ExploreView },
        { path: 'notifications', component: notificationsView },
        { path: 'chat', component: chatView },
        { path: 'grok', component: grokView },
        { path: 'bookmarks', component: bookmarksView },
        { path: 'user', component: userView },
      ],
    },
  ],
})

// router.beforeEach((to, from, next) => {
//   if (to.meta.notRequiresAuth) {
//     next()
//   } else {
//     next('/login')
//   }
// })

export default router
