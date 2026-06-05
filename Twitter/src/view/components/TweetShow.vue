<template>
  <div id="tweet">
    <div class="repost">
      <span>
        <svg viewBox="0 0 24 24" aria-hidden="true">
          <g>
            <path :d="repost_tweet_icon"></path>
          </g>
        </svg>
      </span>
      <span>Misaka 10177</span>
      <span>已转帖</span>
    </div>
    <div class="box" @click="goDetail">
      <div class="profile_photo">
        <img src="/public/images/img.png" alt="" />
      </div>
      <div class="container">
        <div class="auther_name">
          <div>
            <span class="name">{{ tweet.author.name }}</span>
            <span class="id">@{{ tweet.author.id }}</span>
            <span class="dot">·</span>
            <span class="time">{{ displayTime }}</span>
          </div>
          <div>
            <div class="tweet_more">
              <svg viewBox="0 0 24 24" aria-hidden="true">
                <g>
                  <path :d="tweet_more_icon"></path>
                </g>
              </svg>
            </div>
          </div>
        </div>
        <div class="tweet_text">
          {{ tweet.text }}
        </div>
        <div class="tweet_tag">#お隣の天使様</div>
        <div class="tweet_images">
          <div class="img">
            <div>
              <img :src="tweet.images" alt="" />
            </div>
          </div>
        </div>
        <div class="tweet_interaction">
          <div>
            <div v-for="(item, index) in page_tweet_interaction_icon" :key="index">
              <span>
                <span class="icon">
                  <div></div>
                  <svg viewBox="0 0 24 24" aria-hidden="true">
                    <g>
                      <path :d="item.path"></path>
                    </g>
                  </svg>
                </span>
                <span>{{ tweet.interaction[item.name] }}</span>
              </span>
            </div>
          </div>
          <div>
            <div v-for="(item, index) in page_tweet_interaction_icon_1" :key="index">
              <span class="icon">
                <svg viewBox="0 0 24 24" aria-hidden="true">
                  <g>
                    <path :d="item.path"></path>
                  </g>
                </svg>
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import type { Tweet } from '@/types'
const prop = defineProps<{ tweet: Tweet }>()
const tweet = ref<Tweet>(prop.tweet)
const router = useRouter()

function goDetail() {
  router.push('/tweet/' + tweet.value.id)
}

const displayTime = computed(() => {
  if (!tweet.value.publishTime) return ''
  const now = new Date()
  const pub = new Date(tweet.value.publishTime)
  const diff = now.getTime() - pub.getTime()
  const seconds = Math.floor(diff / 1000)
  const minutes = Math.floor(seconds / 60)
  const hours = Math.floor(minutes / 60)

  if (seconds < 60) return '刚刚'
  if (minutes < 60) return `${minutes}分钟`
  if (hours < 24) return `${hours}小时`
  if (pub.getFullYear() === now.getFullYear()) {
    return `${pub.getMonth() + 1}月${pub.getDate()}日`
  }
  return `${pub.getFullYear()}年${pub.getMonth() + 1}月${pub.getDate()}日`
})

type interactionType = 'reply' | 'transpond' | 'upvote' | 'view'
const page_tweet_interaction_icon = ref<{ name: interactionType; path: string }[]>([
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
  {
    name: 'view',
    path: 'M8.75 21V3h2v18h-2zM18 21V8.5h2V21h-2zM4 21l.004-10h2L6 21H4zm9.248 0v-7h2v7h-2z',
  },
])

const page_tweet_interaction_icon_1 = ref([
  {
    name: 'bookmark',
    path: 'M4 4.5C4 3.12 5.119 2 6.5 2h11C18.881 2 20 3.12 20 4.5v18.44l-8-5.71-8 5.71V4.5zM6.5 4c-.276 0-.5.22-.5.5v14.56l6-4.29 6 4.29V4.5c0-.28-.224-.5-.5-.5h-11z',
  },
  {
    name: 'Retweet',
    path: 'M12 2.59l5.7 5.7-1.41 1.42L13 6.41V16h-2V6.41l-3.3 3.3-1.41-1.42L12 2.59zM21 15l-.02 3.51c0 1.38-1.12 2.49-2.5 2.49H5.5C4.11 21 3 19.88 3 18.5V15h2v3.5c0 .28.22.5.5.5h12.98c.28 0 .5-.22.5-.5L19 15h2z',
  },
])

const tweet_more_icon = ref(
  'M3 12c0-1.1.9-2 2-2s2 .9 2 2-.9 2-2 2-2-.9-2-2zm9 2c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2zm7 0c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2z',
)

const repost_tweet_icon = ref(
  'M4.75 3.79l4.603 4.3-1.706 1.82L6 8.38v7.37c0 .97.784 1.75 1.75 1.75H13V20H7.75c-2.347 0-4.25-1.9-4.25-4.25V8.38L1.853 9.91.147 8.09l4.603-4.3zm11.5 2.71H11V4h5.25c2.347 0 4.25 1.9 4.25 4.25v7.37l1.647-1.53 1.706 1.82-4.603 4.3-4.603-4.3 1.706-1.82L18 15.62V8.25c0-.97-.784-1.75-1.75-1.75z',
)

function tab_click(tab) {}
</script>
<style scope>
#tweet {
  padding: 0 2%;
  border-bottom: var(--boundary-style);
}
.repost {
  display: flex;
  margin-top: 6px;
}
.repost span {
  font-size: 12px;
  line-height: 12px;
  color: var(--grey-color);
}
.repost span:first-child {
  width: 40px;
  margin-right: 8px;
  display: flex;
  flex-direction: row-reverse;
}
.repost svg {
  width: 12px;
  height: 12px;
  fill: var(--grey-color);
}

.profile_photo {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  margin-right: 8px;
}
.box {
  display: flex;
  /* font-size: 18px; */
}
.profile_photo img {
  width: inherit;
  height: inherit;
}
.container {
  flex: 1;
}
.auther_name {
  width: 100%;
  display: flex;
  justify-content: space-between;
  color: var(--grey-color);
}
.auther_name > div {
  /* white-space: nowrap; */
}
.auther_name span {
  margin-left: 4px;
  text-overflow: ellipsis;
  overflow: hidden;
}
.auther_name .name {
  color: var(--black-color);
  margin-left: 0;
  font-weight: 700;
}
.auther_name .tweet_more {
  display: flex;
}
.auther_name svg {
  width: 18px;
  height: 18px;
  line-height: 18px;
  fill: var(--grey-color);
}
.auther_name,
.tweet_text {
  line-height: 1.5em;
}
.tweet_text {
  white-space: pre-wrap;
}
.tweet_tag {
  color: var(--theme-color);
  margin-top: 1em;
}
.tweet_images {
  display: flex;
  margin-top: 1em;
  height: 400px;
  max-height: 500px;
}
.tweet_images .img {
  height: inherit;

  overflow: hidden;
  border-radius: 10px;
  /* position: relative; */
}
.tweet_images .img > div {
  height: inherit;
  /* position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0; */
}
.tweet_images .img img {
  height: inherit;
  object-fit: contain;
}
.tweet_interaction {
  /* width: 96%; */
  height: 40px;
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  line-height: 15px;
  color: var(--grey-color);
  position: relative;
}
.tweet_interaction span {
  font-size: 12px;
  /* margin-right: 2px; */
}
.tweet_interaction span:last-child {
  margin-right: 0;
}
.tweet_interaction div {
  display: flex;
  align-items: center;
}
.tweet_interaction > div:first-child {
  flex: 1;
}
.tweet_interaction > div:first-child div {
  flex: 1;
}
.tweet_interaction > div:last-child div:first-child {
  margin-right: 6px;
}
.tweet_interaction div div span {
  display: flex;
  align-items: center;
}
.tweet_interaction .icon {
  display: flex;
  align-items: center;
  position: relative;
}
.tweet_interaction .icon div {
  position: absolute;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
  margin: -6px;
  transition-duration: 0.2s;
  border-radius: 50%;
  z-index: -1;
}
.tweet_interaction .icon + span {
  padding: 0 4px;
}
.tweet_interaction > div > div > span:hover .icon div {
  background-color: var(--theme-color-light);
}
.tweet_interaction > div > div > span:hover svg {
  fill: var(--theme-color);
}
.tweet_interaction > div > div:hover span {
  color: var(--theme-color);
}
.tweet_interaction > div:first-child div:nth-child(2) {
  --theme-color: rgb(0, 186, 124);
  --theme-color-light: rgba(0, 186, 124, 0.1);
}
.tweet_interaction > div:first-child div:nth-child(3) {
  --theme-color: rgb(249, 24, 128);
  --theme-color-light: rgba(249, 24, 128, 0.1);
}
.tweet_interaction svg {
  width: 16px;
  height: 16px;
  fill: var(--grey-color);
}
</style>
