<template>
  <div id="home_main">
    <div class="tab">
      <TabControl :tab_list="tab_list" @tab-click="tab_click"></TabControl>
    </div>
    <div class="send_tweet">
      <PostComposer @posted="onPosted"></PostComposer>
    </div>
    <div class="tweet_list">
      <div class="update_tweet" v-if="loading">加载中…</div>
      <div class="update_tweet" v-else>显示 {{ tweet_list.length }} 帖子</div>
      <div class="load_error" v-if="load_error" role="alert">{{ load_error }}</div>
      <div class="tweets">
        <TweetShow v-for="tweet in tweet_list" :key="tweet.id" :tweet="tweet"></TweetShow>
      </div>
      <button
        v-if="has_more"
        type="button"
        class="load_more"
        :disabled="loading_more"
        @click="loadMore"
      >
        {{ loading_more ? '加载中…' : '加载更多' }}
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import type { Tweet } from '@/types'
import { getTweetList } from '@/request/api'
import { useTweetStore } from '@/stores/store'
import PostComposer from './PostComposer.vue'
import TabControl from './TabControl.vue'
import TweetShow from './TweetShow.vue'

const PAGE_SIZE = 20
const tab_list = ref(['为你推荐', '正在关注'])
const tweetStore = useTweetStore()
const tweet_list = computed(() => tweetStore.tweets)
const loading = ref(false)
const loading_more = ref(false)
const load_error = ref('')
const page = ref(0)
const has_more = ref(false)

async function loadTweets() {
  loading.value = true
  load_error.value = ''
  try {
    const res = (await getTweetList(0, PAGE_SIZE)) as Tweet[]
    const items = Array.isArray(res) ? res : []
    tweetStore.setTweets(items)
    page.value = 0
    has_more.value = items.length === PAGE_SIZE
  } catch {
    load_error.value = '帖子加载失败，请确认后端已启动'
  } finally {
    loading.value = false
  }
}

async function loadMore() {
  if (!has_more.value || loading_more.value) return
  loading_more.value = true
  load_error.value = ''
  try {
    const res = (await getTweetList(page.value + 1, PAGE_SIZE)) as Tweet[]
    const items = Array.isArray(res) ? res : []
    tweetStore.appendTweets(items)
    page.value += 1
    has_more.value = items.length === PAGE_SIZE
  } catch {
    load_error.value = '加载更多失败，请稍后重试'
  } finally {
    loading_more.value = false
  }
}

function onPosted(tweet: Tweet) {
  tweetStore.prependTweet(tweet)
  load_error.value = ''
}

function tab_click() {
  // 预留：切换“为你推荐 / 正在关注”
}

onMounted(loadTweets)
</script>

<style scoped>
.update_tweet {
  height: 40px;
  line-height: 40px;
  display: flex;
  justify-content: center;
  font-size: 14px;
  color: var(--theme-color);
  border-bottom: var(--boundary-style);
}
.load_error {
  padding: 12px 16px;
  color: #f4212e;
  font-size: 13px;
  border-bottom: var(--boundary-style);
}
.load_more {
  display: block;
  width: 100%;
  padding: 14px 16px;
  border: none;
  border-bottom: var(--boundary-style);
  background: #fff;
  color: var(--theme-color);
  font-size: 14px;
  cursor: pointer;
}
.load_more:disabled {
  color: var(--grey-color);
  cursor: default;
}
.tab {
  height: 53px;
}
</style>
