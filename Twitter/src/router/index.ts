import { createRouter, createWebHistory } from 'vue-router'

import JoinView from '../view/JoinView.vue'
import HomeView from '../view/HomeView.vue'
import ExploreView from '../view/ExploreView.vue'
import notificationsView from '../view/notificationsView.vue'
import chatView from '../view/chatView.vue'
import grokView from '../view/grokView.vue'
import bookmarksView from '../view/bookmarksView.vue'
import tweetDetailView from '../view/tweetDetailView.vue'
import userView from '../view/userView.vue'
import BasicLayout from '@/layouts/BasicLayout.vue'
import LoginView from '@/view/LoginView.vue'
import { useAuthStore } from '@/stores/auth'

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
        { path: 'tweet/:id', component: tweetDetailView },
        { path: 'user/:id?', component: userView },
      ],
    },
  ],
})

router.beforeEach(async (to) => {
  const auth = useAuthStore()

  if (to.meta.notRequiresAuth) {
    if (!auth.ready) {
      await auth.ensureSession()
    }
    if (auth.isAuthenticated && to.path.startsWith('/join')) {
      const redirect = typeof to.query.redirect === 'string'
        && to.query.redirect.startsWith('/')
        && !to.query.redirect.startsWith('//')
        && !to.query.redirect.startsWith('/join')
        ? to.query.redirect
        : '/'
      return { path: redirect }
    }
    return true
  }

  const authenticated = await auth.ensureSession()
  if (!authenticated) {
    return { path: '/join', query: { redirect: to.fullPath } }
  }
  return true
})

export default router
