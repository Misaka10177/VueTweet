<template>
  <div id="chat">
    <div class="page_main">
      <main>
        <div class="top">
          <div class="title">私信</div>
          <div class="icon">
            <svg viewBox="0 0 24 24">
              <g><path :d="icon_new_message"></path></g>
            </svg>
            <svg viewBox="0 0 24 24">
              <g><path :d="icon_more"></path></g>
            </svg>
          </div>
        </div>
        <div class="search">
          <SearchBar></SearchBar>
        </div>
        <div class="message_list">
          <div class="empty" v-if="messages.length === 0">
            <div class="empty-title">欢迎使用私信</div>
            <div class="empty-subtitle">
              发送和接收不公开的消息，与关注你的用户或你关注的人取得联系。
            </div>
            <button class="send-btn">发送私信</button>
          </div>
          <div class="conversations" v-else>
            <div class="conversation" v-for="(msg, index) in messages" :key="index">
              <div class="avatar">
                <img :src="msg.avatar" alt="" />
              </div>
              <div class="info">
                <div class="name">{{ msg.name }}</div>
                <div class="preview">{{ msg.preview }}</div>
              </div>
              <div class="time">{{ msg.time }}</div>
            </div>
          </div>
        </div>
      </main>
    </div>
    <div class="side_bar">
      <div>
        <SubscribePremium></SubscribePremium>
        <RecommendFollowing></RecommendFollowing>
        <NavigationBar></NavigationBar>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { ref } from 'vue'
import SearchBar from './components/SideBar/SideBarComponents/SearchBar.vue'
import SubscribePremium from './components/SideBar/SideBarComponents/SubscribePremium.vue'
import RecommendFollowing from './components/SideBar/SideBarComponents/RecommendFollowing.vue'
import NavigationBar from './components/SideBar/SideBarComponents/NavigationBar.vue'

const icon_new_message = ref(
  'M1.998 5.5c0-1.381 1.119-2.5 2.5-2.5h15c1.381 0 2.5 1.119 2.5 2.5v13c0 1.381-1.119 2.5-2.5 2.5h-15c-1.381 0-2.5-1.119-2.5-2.5v-13zm2.5-.5c-.276 0-.5.224-.5.5v2.764l8 3.638 8-3.636V5.5c0-.276-.224-.5-.5-.5h-15zm15.5 5.463l-8 3.636-8-3.638V18.5c0 .276.224.5.5.5h15c.276 0 .5-.224.5-.5v-8.037z',
)
const icon_more = ref(
  'M3 12c0-1.1.9-2 2-2s2 .9 2 2-.9 2-2 2-2-.9-2-2zm9 2c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2zm7 0c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2z',
)

const messages = ref<
  { avatar: string; name: string; preview: string; time: string }[]
>([])
</script>
<style scoped>
#chat {
  display: flex;
  width: 100%;

  --title-font-size: 20px;
  --title-font-weight: 700;
  --box-padding: 12px 16px;
}
.page_main {
  width: 100%;
  max-width: 600px;
}
main {
  width: 100%;
  border-right: var(--boundary-style);
}
.top {
  width: 100%;
  height: 53px;
  padding: 0 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.title {
  font-size: 20px;
  font-weight: 700;
}
.icon {
  display: flex;
  align-items: center;
  gap: 16px;
}
svg {
  width: 20px;
  height: 20px;
}
.search {
  padding: 8px 16px;
}
.empty {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  padding: 32px 16px;
}
.empty-title {
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 16px;
}
.empty-subtitle {
  font-size: 14px;
  color: var(--grey-color);
  line-height: 1.4;
  margin-bottom: 24px;
  max-width: 360px;
}
.send-btn {
  height: 40px;
  padding: 0 24px;
  border-radius: 20px;
  font-size: 15px;
  font-weight: 600;
  color: #fff;
  background: var(--theme-color);
  transition: background 0.2s;
}
.send-btn:hover {
  background: rgba(29, 155, 240, 0.9);
}

.side_bar {
  margin-left: 30px;
  margin-top: 10px;
}
.side_bar {
  --title-font-size: 20px;
  --title-font-weight: 700;
  --box-padding: 12px 16px;

  width: 350px;
}
.side_bar > div {
  width: inherit;
  position: fixed;
}
.side_bar > div > div {
  margin-top: 20px;
  width: 100%;
  padding-top: 26px;
  border: var(--boundary-style);
  border-radius: 20px;
}
</style>
