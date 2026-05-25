<template>
  <div id="user">
    <div class="page_main">
      <main>
        <div class="top">
          <div class="back" @click="$router.back()">
            <svg viewBox="0 0 24 24">
              <g><path :d="icon_back"></path></g>
            </svg>
          </div>
          <div class="header">
            <div class="name">{{ userName }}</div>
            <div class="post-count">{{ postCount }} 帖子</div>
          </div>
        </div>
        <div class="profile">
          <div class="cover">
            <img src="/public/images/64495434_p0.jpg" alt="" />
          </div>
          <div class="profile-info">
            <div class="avatar-row">
              <div class="avatar">
                <img src="/public/images/img.png" alt="" />
              </div>
              <div class="edit-btn-wrapper">
                <button class="edit-btn">编辑个人资料</button>
              </div>
            </div>
            <div class="display-name">{{ userName }}</div>
            <div class="handle">@{{ userId }}</div>
            <div class="bio" v-if="bio">{{ bio }}</div>
            <div class="join-date">
              <svg viewBox="0 0 24 24">
                <g><path :d="icon_calendar"></path></g>
              </svg>
              <span>{{ joinDate }}</span>
            </div>
            <div class="follow-info">
              <span class="following"
                ><strong>{{ following }}</strong> 正在关注</span
              >
              <span class="followers"
                ><strong>{{ followers }}</strong> 关注者</span
              >
            </div>
          </div>
        </div>
        <div class="tab">
          <TabControl :tab_list="tab_list" @tab-click="tab_click"></TabControl>
        </div>
        <div class="tweet_list">
          <TweetShow v-for="(tweet, index) in tweets" :key="index" :tweet="tweet"></TweetShow>
        </div>
        <div class="recommend-section">
          <RecommendFollowing></RecommendFollowing>
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
import TabControl from './components/TabControl.vue'
import TweetShow from './components/TweetShow.vue'
import SearchBar from './components/SideBar/SideBarComponents/SearchBar.vue'
import SubscribePremium from './components/SideBar/SideBarComponents/SubscribePremium.vue'
import RecommendFollowing from './components/SideBar/SideBarComponents/RecommendFollowing.vue'
import NavigationBar from './components/SideBar/SideBarComponents/NavigationBar.vue'
import { getuserInfo } from '@/request/api.js'
import type { Tweet } from '@/types'

const icon_back = ref(
  'M7.414 13l5.043 5.04-1.414 1.42L3.586 12l7.457-7.46 1.414 1.42L7.414 11H21v2H7.414z',
)
const icon_calendar = ref(
  'M7 4V3h2v1h6V3h2v1h1.5C19.88 4 21 5.12 21 6.5v12c0 1.38-1.12 2.5-2.5 2.5h-13C4.12 21 3 19.88 3 18.5v-12C3 5.12 4.12 4 5.5 4H7zm0 2H5.5c-.28 0-.5.22-.5.5v12c0 .28.22.5.5.5h13c.28 0 .5-.22.5-.5v-12c0-.28-.22-.5-.5-.5H17v1h-2V6H9v1H7V6zm0 5V9h2v2H7zm4 0V9h2v2h-2zm4 0V9h2v2h-2z',
)

const userName = ref('')
const userId = ref('')
getuserInfo().then((res) => {
  userName.value = res.username
  userId.value = res.userId
})

const bio = ref('')
const joinDate = ref('2024年1月加入')
const following = ref(0)
const followers = ref(0)
const postCount = ref(0)

const tab_list = ref(['帖子', '回复', '亮点', '文章', '媒体', '喜欢'])

const tweets = ref<Tweet[]>([])

function tab_click(tab: string) {
  // console.log(tab)
}
</script>
<style scoped>
#user {
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
  align-items: center;
}
.back {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  cursor: pointer;
  transition: background 0.2s;
}
.back:hover {
  background: rgba(15, 20, 25, 0.08);
}
.back svg {
  width: 20px;
  height: 20px;
}
.header {
  display: flex;
  flex-direction: column;
}
.header .name {
  font-size: 20px;
  font-weight: 700;
}
.header .post-count {
  font-size: 13px;
  color: var(--grey-color);
}
.profile {
  width: 100%;
}
.cover {
  width: 100%;
  height: 200px;
  background: linear-gradient(135deg, #1d9bf0 0%, #1a8cd8 50%, #0f6faf 100%);
  overflow: hidden;
}
.cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.profile-info {
  position: relative;
  padding: 12px 16px;
}
.avatar-row {
  display: flex;
  justify-content: flex-end;
  height: 65px;
  margin-bottom: 12px;
}
.avatar {
  position: absolute;
  top: -65px;
  left: 16px;
  width: 130px;
  height: 130px;
  border-radius: 50%;
  border: 4px solid #fff;
  overflow: hidden;
  background: #fff;
}
.avatar img {
  width: 130px;
  height: 130px;
  object-fit: cover;
}
.edit-btn {
  height: 34px;
  padding: 0 16px;
  align-self: flex-end;
  border-radius: 17px;
  border: var(--boundary-style);
  font-size: 14px;
  font-weight: 600;
  color: var(--black-color);
  background: #fff;
  cursor: pointer;
  transition: background 0.2s;
  border-color: rgba(0, 0, 0, 0.1);
}
.edit-btn:hover {
  background: rgba(15, 20, 25, 0.06);
}
.display-name {
  font-size: 20px;
  font-weight: 700;
  margin-bottom: 4px;
}
.handle {
  font-size: 15px;
  color: var(--grey-color);
  margin-bottom: 12px;
}
.bio {
  font-size: 15px;
  line-height: 1.4;
  margin-bottom: 12px;
}
.join-date {
  display: flex;
  align-items: center;
  font-size: 15px;
  color: var(--grey-color);
  margin-bottom: 12px;
}
.join-date svg {
  width: 18px;
  height: 18px;
  fill: var(--grey-color);
  margin-right: 4px;
}
.follow-info {
  display: flex;
  gap: 20px;
  font-size: 14px;
  color: var(--grey-color);
  margin-bottom: 12px;
}
.follow-info strong {
  color: var(--black-color);
}
.follow-info span {
  cursor: pointer;
}
.follow-info span:hover {
  text-decoration: underline;
}
.tab {
  height: 53px;
}
.tweet_list {
  min-height: 24px;
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
