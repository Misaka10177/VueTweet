<template>
  <div id="global_nav">
    <div class="top">
      <div class="logo">
        <div>
          <div class="">
            <router-link to="/">
              <svg viewBox="0 0 24 24" aria-hidden="true">
                <g>
                  <path :d="logo_x"></path>
                </g>
              </svg>
            </router-link>
          </div>
        </div>
      </div>
      <div class="nav">
        <div
          v-for="(tab, index) in page_left_nav_list"
          :key="index"
          :class="{ nav_now: tab.to == $route.path }"
        >
          <div class="icon">
            <svg
              :viewBox="tab.to == $route.path ? tab.viewBox_fill : tab.viewBox || '0 0 24 24'"
              aria-hidden="true"
            >
              <g>
                <path :d="tab.to == $route.path ? tab.icon_fill : tab.icon"></path>
              </g>
            </svg>
          </div>
          <div class="title">{{ tab.title }}</div>
          <router-link :to="tab.to || '/'"></router-link>
        </div>
      </div>
      <div class="post">
        <div class="title">发帖</div>
      </div>
    </div>
    <div class="user" @click="click_user">
      <div class="profile_photo">
        <img src="/public/images/img.png" alt="" />
      </div>
      <div class="info">
        <div class="name">{{ username }}</div>
        <div class="id">@{{ userId }}</div>
      </div>
      <div class="icon">
        <svg viewBox="0 0 24 24" aria-hidden="true">
          <g>
            <path :d="icon_more"></path>
          </g>
        </svg>
      </div>
      <div class="option" v-if="user_option_show">
        <div>添加已有账号</div>
        <div @click="click_logout">登出 @{{ userId }}</div>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { ref } from 'vue'
import { getuserInfo, logout } from '@/request/api.js'
import router from '@/router'

const logo_x = ref(
  'M21.742 21.75l-7.563-11.179 7.056-8.321h-2.456l-5.691 6.714-4.54-6.714H2.359l7.29 10.776L2.25 21.75h2.456l6.035-7.118 4.818 7.118h6.191-.008zM7.739 3.818L18.81 20.182h-2.447L5.29 3.818h2.447z',
)
const page_left_nav_list = ref([
  {
    icon: 'M21.591 7.146L12.52 1.157c-.316-.21-.724-.21-1.04 0l-9.071 5.99c-.26.173-.409.456-.409.757v13.183c0 .502.418.913.929.913h6.638c.511 0 .929-.41.929-.913v-7.075h3.008v7.075c0 .502.418.913.929.913h6.639c.51 0 .928-.41.928-.913V7.904c0-.301-.158-.584-.408-.758zM20 20l-4.5.01.011-7.097c0-.502-.418-.913-.928-.913H9.44c-.511 0-.929.41-.929.913L8.5 20H4V8.773l8.011-5.342L20 8.764z',
    icon_fill:
      'M21.591 7.146L12.52 1.157c-.316-.21-.724-.21-1.04 0l-9.071 5.99c-.26.173-.409.456-.409.757v13.183c0 .502.418.913.929.913H9.14c.51 0 .929-.41.929-.913v-7.075h3.909v7.075c0 .502.417.913.928.913h6.165c.511 0 .929-.41.929-.913V7.904c0-.301-.158-.584-.408-.758z',
    title: '主页',
    to: '/',
  },
  {
    icon: 'M10.25 3.75c-3.59 0-6.5 2.91-6.5 6.5s2.91 6.5 6.5 6.5c1.795 0 3.419-.726 4.596-1.904 1.178-1.177 1.904-2.801 1.904-4.596 0-3.59-2.91-6.5-6.5-6.5zm-8.5 6.5c0-4.694 3.806-8.5 8.5-8.5s8.5 3.806 8.5 8.5c0 1.986-.682 3.815-1.824 5.262l4.781 4.781-1.414 1.414-4.781-4.781c-1.447 1.142-3.276 1.824-5.262 1.824-4.694 0-8.5-3.806-8.5-8.5z',
    icon_fill:
      'M10.25 4.25c-3.314 0-6 2.686-6 6s2.686 6 6 6c1.657 0 3.155-.67 4.243-1.757 1.087-1.088 1.757-2.586 1.757-4.243 0-3.314-2.686-6-6-6zm-9 6c0-4.971 4.029-9 9-9s9 4.029 9 9c0 1.943-.617 3.744-1.664 5.215l4.475 4.474-2.122 2.122-4.474-4.475c-1.471 1.047-3.272 1.664-5.215 1.664-4.971 0-9-4.029-9-9z',
    title: '探索',
    to: '/explore',
  },
  {
    icon: 'M19.993 9.042C19.48 5.017 16.054 2 11.996 2s-7.49 3.021-7.999 7.051L2.866 18H7.1c.463 2.282 2.481 4 4.9 4s4.437-1.718 4.9-4h4.236l-1.143-8.958zM12 20c-1.306 0-2.417-.835-2.829-2h5.658c-.412 1.165-1.523 2-2.829 2zm-6.866-4l.847-6.698C6.364 6.272 8.941 4 11.996 4s5.627 2.268 6.013 5.295L18.864 16H5.134z',
    icon_fill:
      'M11.996 2c-4.062 0-7.49 3.021-7.999 7.051L2.866 18H7.1c.463 2.282 2.481 4 4.9 4s4.437-1.718 4.9-4h4.236l-1.143-8.958C19.48 5.017 16.054 2 11.996 2zM9.171 18h5.658c-.412 1.165-1.523 2-2.829 2s-2.417-.835-2.829-2z',
    title: '通知',
    to: '/notifications',
  },
  {
    icon: 'M1.998 5.5c0-1.381 1.119-2.5 2.5-2.5h15c1.381 0 2.5 1.119 2.5 2.5v13c0 1.381-1.119 2.5-2.5 2.5h-15c-1.381 0-2.5-1.119-2.5-2.5v-13zm2.5-.5c-.276 0-.5.224-.5.5v2.764l8 3.638 8-3.636V5.5c0-.276-.224-.5-.5-.5h-15zm15.5 5.463l-8 3.636-8-3.638V18.5c0 .276.224.5.5.5h15c.276 0 .5-.224.5-.5v-8.037z',
    icon_fill:
      'M12.001 1.5c5.858 0 10.7 4.518 10.7 10.2-.001 5.683-4.842 10.2-10.7 10.2-1.785 0-2.96-.555-3.95-1.095-1.876.768-4.02 1.2-6.245-.075l-.885-.505.523-.875c.54-.904.77-1.581.849-2.118.077-.526.02-.98-.11-1.463-.066-.25-.15-.502-.247-.788-.095-.277-.204-.59-.301-.92-.2-.674-.36-1.449-.332-2.39C1.319 6.002 6.153 1.5 12 1.5z',
    title: '聊天',
    to: '/chat',
  },
  {
    icon: 'M12.745 20.54l10.97-8.19c.539-.4 1.307-.244 1.564.38 1.349 3.288.746 7.241-1.938 9.955-2.683 2.714-6.417 3.31-9.83 1.954l-3.728 1.745c5.347 3.697 11.84 2.782 15.898-1.324 3.219-3.255 4.216-7.692 3.284-11.693l.008.009c-1.351-5.878.332-8.227 3.782-13.031L33 0l-4.54 4.59v-.014L12.743 20.544m-2.263 1.987c-3.837-3.707-3.175-9.446.1-12.755 2.42-2.449 6.388-3.448 9.852-1.979l3.72-1.737c-.67-.49-1.53-1.017-2.515-1.387-4.455-1.854-9.789-.931-13.41 2.728-3.483 3.523-4.579 8.94-2.697 13.561 1.405 3.454-.899 5.898-3.22 8.364C1.49 30.2.666 31.074 0 32l10.478-9.466',
    icon_fill:
      'M8 0C3.582 0 0 3.582 0 8v26c0 4.418 3.582 8 8 8h26c4.418 0 8-3.582 8-8V8c0-4.418-3.582-8-8-8H8zm19.997 17.35l-11.1 8.19 15.9-15.963v.015L37.391 5c-.082.117-.165.23-.248.345-3.49 4.804-5.194 7.153-3.826 13.03l-.009-.008c.943 4.001-.065 8.438-3.322 11.693-4.106 4.107-10.677 5.02-16.087 1.324l3.772-1.745c3.454 1.355 7.232.76 9.947-1.954 2.716-2.714 3.325-6.666 1.96-9.956-.259-.623-1.037-.78-1.58-.378zm-13.292-2.574c-3.314 3.31-3.983 9.047-.1 12.755l-.003.003L4 37c.663-.913 1.485-1.776 2.306-2.639l.04-.042c2.346-2.464 4.67-4.906 3.25-8.357-1.903-4.622-.795-10.038 2.73-13.56 3.664-3.66 9.06-4.583 13.568-2.729.998.37 1.867.897 2.545 1.387l-3.764 1.737c-3.505-1.47-7.52-.47-9.97 1.98z',
    title: 'Grok',
    viewBox: '0 0 33 32',
    viewBox_fill: '0 0 42 42',
    to: '/grok',
  },
  {
    icon: 'M4 4.5C4 3.12 5.119 2 6.5 2h11C18.881 2 20 3.12 20 4.5v18.44l-8-5.71-8 5.71V4.5zM6.5 4c-.276 0-.5.22-.5.5v14.56l6-4.29 6 4.29V4.5c0-.28-.224-.5-.5-.5h-11z',
    icon_fill: 'M4 4.5C4 3.12 5.119 2 6.5 2h11C18.881 2 20 3.12 20 4.5v18.44l-8-5.71-8 5.71V4.5z',
    title: '书签',
    to: '/bookmarks',
  },
  {
    icon: 'M5.651 19h12.698c-.337-1.8-1.023-3.21-1.945-4.19C15.318 13.65 13.838 13 12 13s-3.317.65-4.404 1.81c-.922.98-1.608 2.39-1.945 4.19zm.486-5.56C7.627 11.85 9.648 11 12 11s4.373.85 5.863 2.44c1.477 1.58 2.366 3.8 2.632 6.46l.11 1.1H3.395l.11-1.1c.266-2.66 1.155-4.88 2.632-6.46zM12 4c-1.105 0-2 .9-2 2s.895 2 2 2 2-.9 2-2-.895-2-2-2zM8 6c0-2.21 1.791-4 4-4s4 1.79 4 4-1.791 4-4 4-4-1.79-4-4z',
    icon_fill:
      'M17.863 13.44c1.477 1.58 2.366 3.8 2.632 6.46l.11 1.1H3.395l.11-1.1c.266-2.66 1.155-4.88 2.632-6.46C7.627 11.85 9.648 11 12 11s4.373.85 5.863 2.44zM12 2C9.791 2 8 3.79 8 6s1.791 4 4 4 4-1.79 4-4-1.791-4-4-4z',
    title: '个人资料',
    to: '/user',
  },
  {
    icon: 'M3.75 12c0-4.56 3.69-8.25 8.25-8.25s8.25 3.69 8.25 8.25-3.69 8.25-8.25 8.25S3.75 16.56 3.75 12zM12 1.75C6.34 1.75 1.75 6.34 1.75 12S6.34 22.25 12 22.25 22.25 17.66 22.25 12 17.66 1.75 12 1.75zm-4.75 11.5c.69 0 1.25-.56 1.25-1.25s-.56-1.25-1.25-1.25S6 11.31 6 12s.56 1.25 1.25 1.25zm9.5 0c.69 0 1.25-.56 1.25-1.25s-.56-1.25-1.25-1.25-1.25.56-1.25 1.25.56 1.25 1.25 1.25zM13.25 12c0 .69-.56 1.25-1.25 1.25s-1.25-.56-1.25-1.25.56-1.25 1.25-1.25 1.25.56 1.25 1.25z',
    title: '更多',
    // to: '/notifications',
  },
])
const icon_more = ref(
  'M3 12c0-1.1.9-2 2-2s2 .9 2 2-.9 2-2 2-2-.9-2-2zm9 2c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2zm7 0c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2z',
)
const user_option_show = ref(false)
function click_user() {
  user_option_show.value = !user_option_show.value
}
const username = ref('')
const userId = ref('')
getuserInfo().then((res) => {
  username.value = res.username
  userId.value = res.userId
  // console.log(res)
})
function click_logout() {
  logout().then((res) => {
    if (res.status !== 'error') {
      router.push('/join')
    }
  })
}
</script>
<style scoped>
a {
  text-decoration: none;
  color: inherit;
}
#global_nav {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  height: 100%;
}
.top {
  --icon-size: 1.75rem;
  width: 275px;
  display: flex;
  flex-direction: column;
}
svg {
  width: var(--icon-size);
  height: var(--icon-size);
}
.logo,
.nav {
  display: flex;
  flex-direction: column;
  align-items: center;
}
.top > div:nth-child(-n + 2) > div {
  width: 80%;
  height: 50px;
  display: flex;
  align-items: center;
  position: relative;
}
.top .nav a {
  position: absolute;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
}
.top .nav .icon {
  margin-right: 20px;
}
.top .nav div {
  font-weight: 400;
  font-size: 18px;
  line-height: 18px;
}
.top .nav div.nav_now .title {
  font-weight: 500;
}
.top .post {
  min-height: 52px;
  width: 230px;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 16px;
  font-weight: 700;
  align-items: center;
  justify-content: center;
  color: #fff;
  border-radius: 50px;
  background: var(--black-color);
}
.user {
  width: 90%;
  display: flex;
  align-items: center;
  position: relative;
  /* bottom: 14px; */
  /* left: 10px; */
  right: 10px;
  height: 60px;
  border-radius: 30px;
  padding: 0 10px;
  user-select: none;
  margin-bottom: 10px;
}
.user:hover {
  background: rgba(15, 20, 25, 0.08);
}
.profile_photo {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  margin-right: 8px;
}
.user .info {
  flex: 1;
  margin-left: 4px;
}
.info .id {
  color: #536471;
}
.user .name {
  font-weight: 500;
}
.user .icon svg {
  width: 18px;
  height: 18px;
}
.option {
  position: absolute;
  top: -180%;
  width: 100%;
  background: #fff;
  padding: 10px 0;
  box-sizing: border-box;
  box-shadow:
    rgba(101, 119, 134, 0.2) 0px 0px 15px,
    rgba(101, 119, 134, 0.15) 0px 0px 3px 1px;
  border-radius: 10px;
  font-weight: 600;
}
.option div {
  padding: 10px;
}
.option div:hover {
  background: rgba(15, 20, 25, 0.03);
}
</style>
