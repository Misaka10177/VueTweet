<template>
  <div id="bookmarks">
    <div class="page_main">
      <main>
        <div class="top">
          <div class="header">
            <div class="title">书签</div>
            <div class="subtitle">@{{ userId }}</div>
          </div>
          <div class="icon">
            <svg viewBox="0 0 24 24">
              <g><path :d="icon_more"></path></g>
            </svg>
          </div>
        </div>
        <div class="bookmark_list">
          <div class="empty" v-if="bookmarks.length === 0">
            <div class="empty-title">还没有保存任何书签</div>
            <div class="empty-subtitle">点击帖子上方的分享图标，再选择"添加书签"即可在此处保存。</div>
          </div>
          <div class="tweets" v-else>
            <TweetShow v-for="(tweet, index) in bookmarks" :key="index" :tweet="tweet"></TweetShow>
          </div>
        </div>
      </main>
    </div>
    <div class="side_bar">
      <div>
        <SearchBar></SearchBar>
        <SubscribePremium></SubscribePremium>
        <RecommendFollowing></RecommendFollowing>
        <NavigationBar></NavigationBar>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { ref } from 'vue'
import TweetShow from './components/TweetShow.vue'
import SearchBar from './components/SideBar/SideBarComponents/SearchBar.vue'
import SubscribePremium from './components/SideBar/SideBarComponents/SubscribePremium.vue'
import RecommendFollowing from './components/SideBar/SideBarComponents/RecommendFollowing.vue'
import NavigationBar from './components/SideBar/SideBarComponents/NavigationBar.vue'
import { getuserInfo } from '@/request/api.js'
import type { Tweet } from '@/types'

const icon_more = ref(
  'M3 12c0-1.1.9-2 2-2s2 .9 2 2-.9 2-2 2-2-.9-2-2zm9 2c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2zm7 0c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2z',
)

const userId = ref('')
getuserInfo().then((res) => {
  userId.value = res.userId
})

const bookmarks = ref<Tweet[]>([])
</script>
<style scoped>
#bookmarks {
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
.header {
  display: flex;
  flex-direction: column;
}
.title {
  font-size: 20px;
  font-weight: 700;
}
.subtitle {
  font-size: 13px;
  color: var(--grey-color);
}
.icon {
  display: flex;
  align-items: center;
}
svg {
  width: 20px;
  height: 20px;
}
.empty {
  padding: 40px 32px;
  text-align: center;
}
.empty-title {
  font-size: 20px;
  font-weight: 700;
  margin-bottom: 12px;
}
.empty-subtitle {
  font-size: 14px;
  color: var(--grey-color);
  line-height: 1.4;
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
