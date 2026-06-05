<template>
  <div id="tweet-detail">
    <div class="page_main">
      <main>
        <div class="top">
          <div class="back" @click="$router.back()">
            <svg viewBox="0 0 24 24">
              <g><path :d="icon_back"></path></g>
            </svg>
          </div>
          <div class="title">帖子</div>
        </div>
        <div class="detail-content">
          <!-- 主推文 -->
          <div class="main-tweet" v-if="tweet">
            <div class="tweet-header">
              <div class="profile_photo">
                <img :src="tweet.author.profile_photo" alt="" />
              </div>
              <div class="author-info">
                <div class="name">{{ tweet.author.name }}</div>
                <div class="id">@{{ tweet.author.id }}</div>
              </div>
              <div class="tweet_more">
                <svg viewBox="0 0 24 24">
                  <g><path :d="tweet_more_icon"></path></g>
                </svg>
              </div>
            </div>
            <div class="tweet-body">
              <div class="text">{{ tweet.text }}</div>
              <div class="images" v-if="tweet.images">
                <img :src="tweet.images" alt="" />
              </div>
              <div class="meta">
                <span class="time">{{ formatTime(tweet.publishTime) }}</span>
                <span class="sep">·</span>
                <span class="view-count">{{ tweet.interaction.view }}</span><span> 查看</span>
              </div>
            </div>
            <div class="tweet-actions">
              <div class="interaction">
                <div v-for="(item, index) in interaction_list" :key="index">
                  <span class="icon">
                    <div></div>
                    <svg viewBox="0 0 24 24">
                      <g><path :d="item.path"></path></g>
                    </svg>
                  </span>
                  <span>{{ tweet.interaction[item.name] }}</span>
                </div>
                <div>
                  <span class="icon">
                    <div></div>
                    <svg viewBox="0 0 24 24">
                      <g><path :d="icon_bookmark"></path></g>
                    </svg>
                  </span>
                  <span>{{ tweet.interaction.bookmark || 0 }}</span>
                </div>
                <div>
                  <span class="icon">
                    <div></div>
                    <svg viewBox="0 0 24 24">
                      <g><path :d="icon_retweet"></path></g>
                    </svg>
                  </span>
                </div>
              </div>
            </div>
          </div>
          <!-- 回复输入框 -->
          <div class="reply-input">
            <div class="reply-avatar">
              <img src="/images/img.png" alt="" />
            </div>
            <input type="text" placeholder="发表你的回复" v-model="replyText" />
            <button class="reply-btn" @click="submitReply">回复</button>
          </div>
          <!-- 回复列表 -->
          <div class="replies" v-if="comments.length">
            <TweetShow v-for="(comment, index) in comments" :key="index" :tweet="comment"></TweetShow>
          </div>
          <div class="no-replies" v-else>
            <div class="empty-text">暂无回复</div>
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
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import SearchBar from './components/SideBar/SideBarComponents/SearchBar.vue'
import SubscribePremium from './components/SideBar/SideBarComponents/SubscribePremium.vue'
import RecommendFollowing from './components/SideBar/SideBarComponents/RecommendFollowing.vue'
import NavigationBar from './components/SideBar/SideBarComponents/NavigationBar.vue'
import TweetShow from './components/TweetShow.vue'
import type { Tweet } from '@/types'
import { getTweetList } from '@/request/api.js'

const route = useRoute()
const tweet = ref<Tweet | null>(null)
const comments = ref<Tweet[]>([])
const replyText = ref('')

const icon_back = ref(
  'M7.414 13l5.043 5.04-1.414 1.42L3.586 12l7.457-7.46 1.414 1.42L7.414 11H21v2H7.414z',
)
const tweet_more_icon = ref(
  'M3 12c0-1.1.9-2 2-2s2 .9 2 2-.9 2-2 2-2-.9-2-2zm9 2c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2zm7 0c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2z',
)
const icon_bookmark = ref(
  'M4 4.5C4 3.12 5.119 2 6.5 2h11C18.881 2 20 3.12 20 4.5v18.44l-8-5.71-8 5.71V4.5zM6.5 4c-.276 0-.5.22-.5.5v14.56l6-4.29 6 4.29V4.5c0-.28-.224-.5-.5-.5h-11z',
)
const icon_retweet = ref(
  'M12 2.59l5.7 5.7-1.41 1.42L13 6.41V16h-2V6.41l-3.3 3.3-1.41-1.42L12 2.59zM21 15l-.02 3.51c0 1.38-1.12 2.49-2.5 2.49H5.5C4.11 21 3 19.88 3 18.5V15h2v3.5c0 .28.22.5.5.5h12.98c.28 0 .5-.22.5-.5L19 15h2z',
)

type interactionType = 'reply' | 'transpond' | 'upvote'
const interaction_list = ref<{ name: interactionType; path: string }[]>([
  {
    name: 'reply',
    path: 'M1.751 10c0-4.42 3.584-8 8.005-8h4.366c4.49 0 8.129 3.64 8.129 8.13 0 2.96-1.607 5.68-4.196 7.11l-8.054 4.46v-3.69h-.067c-4.49.1-8.183-3.51-8.183-8.01zm8.005-6c-3.317 0-6.005 2.69-6.005 6 0 3.37 2.77 6.08 6.138 6.01l.351-.01h1.761v2.3l5.087-2.81c1.951-1.08 3.163-3.13 3.163-5.36 0-3.39-2.744-6.13-6.129-6.13H9.756z',
  },
  {
    name: 'transpond',
    path: 'M4.5 3.88l4.432 4.14-1.364 1.46L5.5 7.55V16c0 1.1.896 2 2 2H13v2H7.5c-2.209 0-4-1.79-4-4V7.55L1.432 9.48.068 8.02 4.5 3.88zM16.5 6H11V4h5.5c2.209 0 4 1.79 4 4v8.45l2.068-1.93 1.364 1.46-4.432 4.14-4.432-4.14 1.364-1.46 2.068 1.93V8c0-1.1-.896-2-2-2z',
  },
  {
    name: 'upvote',
    path: 'M16.697 5.5c-1.222-.06-2.679.51-3.89 2.16l-.805 1.09-.806-1.09C9.984 6.01 8.526 5.44 7.304 5.5c-1.243.07-2.349.78-2.91 1.91-.552 1.12-.633 2.78.479 4.82 1.074 1.97 3.257 4.27 7.129 6.61 3.87-2.34 6.052-4.64 7.126-6.61 1.111-2.04 1.03-3.7.477-4.82-.561-1.13-1.666-1.84-2.908-1.91zm4.187 7.69c-1.351 2.48-4.001 5.12-8.379 7.67l-.503.3-.504-.3c-4.379-2.55-7.029-5.19-8.382-7.67-1.36-2.5-1.41-4.86-.514-6.67.887-1.79 2.647-2.91 4.601-3.01 1.651-.09 3.368.56 4.798 2.01 1.429-1.45 3.146-2.1 4.796-2.01 1.954.1 3.714 1.22 4.601 3.01.896 1.81.846 4.17-.514 6.67z',
  },
])

function formatTime(time: string) {
  if (!time) return ''
  const pub = new Date(time)
  const hours = pub.getHours()
  const minutes = pub.getMinutes().toString().padStart(2, '0')
  const ampm = hours < 12 ? '上午' : '下午'
  const h = hours % 12 || 12
  const timeStr = `${ampm}${h}:${minutes}`
  const dateStr = `${pub.getFullYear()}年${pub.getMonth() + 1}月${pub.getDate()}日`
  return `${timeStr} · ${dateStr}`
}

function submitReply() {
  // TODO: API call
  replyText.value = ''
}

onMounted(() => {
  const id = route.params.id as string
  getTweetList().then((res: Tweet[]) => {
    tweet.value = res.find((t: Tweet) => t.id === id) || null
  })
  // 模拟评论数据
  comments.value = [
    {
      id: '1',
      author: { id: 'Misaka', name: 'Misaka Mikoto', profile_photo: '/images/img.png' },
      text: 'ddd',
      images: '',
      publishTime: '2026-05-24T10:13:42',
      interaction: { reply: 0, transpond: 0, upvote: 0, view: 0, bookmark: 0 },
    },
  ]
})
</script>
<style scoped>
#tweet-detail {
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
  min-height: 100vh;
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
.title {
  font-size: 20px;
  font-weight: 700;
}
.detail-content {
  padding: 0 16px;
}
.main-tweet {
  padding: 16px 0;
  border-bottom: var(--boundary-style);
}
.tweet-header {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}
.tweet-header .profile_photo {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  margin-right: 12px;
}
.tweet-header img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.author-info .name {
  font-size: 15px;
  font-weight: 700;
}
.author-info {
  flex: 1;
}
.author-info .id {
  font-size: 14px;
  color: var(--grey-color);
}
.tweet-header .tweet_more {
  align-self: flex-start;
}
.tweet-header .tweet_more svg {
  width: 18px;
  height: 18px;
  fill: var(--grey-color);
}
.tweet-body .text {
  font-size: 17px;
  line-height: 1.4;
  margin-bottom: 16px;
  white-space: pre-wrap;
}
.tweet-body .images img {
  max-width: 60%;
  border-radius: 16px;
  margin-bottom: 16px;
}
.tweet-body .meta .view-count {
  color: var(--black-color);
  font-weight: 700;
}
.tweet-body .meta {
  font-size: 14px;
  color: var(--grey-color);
  padding-bottom: 16px;
  border-bottom: var(--boundary-style);
}
.tweet-body .meta .sep {
  margin: 0 4px;
}
.tweet-actions {
  padding-top: 12px;
}
.interaction {
  display: flex;
  justify-content: space-between;
}
.interaction > div {
  display: flex;
  align-items: center;
  color: var(--grey-color);
  font-size: 13px;
  gap: 4px;
  cursor: pointer;
  position: relative;
}
.interaction .icon {
  display: flex;
  align-items: center;
  position: relative;
}
.interaction .icon div {
  position: absolute;
  top: 0; bottom: 0; left: 0; right: 0;
  margin: -8px;
  border-radius: 50%;
  transition: background 0.2s;
}
.interaction > div:hover .icon div {
  background: var(--theme-color-light);
}
.interaction > div:nth-child(1) {
  --theme-color: rgb(29, 155, 240);
  --theme-color-light: rgba(29, 155, 240, 0.1);
}
.interaction > div:nth-child(2) {
  --theme-color: rgb(0, 186, 124);
  --theme-color-light: rgba(0, 186, 124, 0.1);
}
.interaction > div:nth-child(3) {
  --theme-color: rgb(249, 24, 128);
  --theme-color-light: rgba(249, 24, 128, 0.1);
}
.interaction svg {
  width: 18px;
  height: 18px;
  fill: var(--grey-color);
}
.interaction > div:hover svg {
  fill: var(--theme-color);
}
.interaction > div:hover {
  color: var(--theme-color);
}
.reply-input {
  display: flex;
  align-items: center;
  padding: 16px 0;
  border-bottom: var(--boundary-style);
  gap: 12px;
}
.reply-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
}
.reply-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.reply-input input {
  flex: 1;
  height: 40px;
  border: none;
  outline: none;
  font-size: 15px;
  caret-color: #000;
}
.reply-btn {
  height: 34px;
  padding: 0 16px;
  border-radius: 17px;
  font-size: 14px;
  font-weight: 600;
  color: #fff;
  background: var(--black-color);
  cursor: pointer;
}
.reply-btn:hover {
  background: rgba(15, 20, 25, 0.8);
}
.replies {
  padding-top: 8px;
}
.reply {
  border-bottom: var(--boundary-style);
}
.comment-box {
  display: flex;
  padding: 12px 0;
}
.comment-box .profile_photo {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  margin-right: 12px;
  flex-shrink: 0;
}
.comment-box img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.comment-content {
  flex: 1;
}
.comment-header {
  margin-bottom: 4px;
}
.comment-header .name {
  font-size: 14px;
  font-weight: 700;
}
.comment-header .id {
  font-size: 13px;
  color: var(--grey-color);
  margin-left: 4px;
}
.comment-text {
  font-size: 15px;
  line-height: 1.4;
}
.no-replies {
  padding: 40px 0;
  text-align: center;
  color: var(--grey-color);
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
