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
      <div class="empty" v-if="is_empty">
        <template v-if="active_tab === 'following'">
          <p>你还没有关注任何人</p>
          <p class="empty-hint">在右侧的推荐关注里关注一些人，这里就会出现他们的帖子。</p>
        </template>
        <p v-else>还没有帖子</p>
      </div>
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
import type { TweetItem } from '@/types'
import { getTweets } from '@/request/api'
import { useTweetStore } from '@/stores/store'
import PostComposer from './PostComposer.vue'
import TabControl from './TabControl.vue'
import TweetShow from './TweetShow.vue'

type Feed = 'recommended' | 'following'

const PAGE_SIZE = 20
const tab_list = ref(['为你推荐', '正在关注'])
const tweetStore = useTweetStore()
const tweet_list = computed(() => tweetStore.tweets)
const active_tab = ref<Feed>('recommended')
const loading = ref(false)
const loading_more = ref(false)
const load_error = ref('')
const next_cursor = ref<string | null>(null)
const has_more = ref(false)
let request_seq = 0

const is_empty = computed(
  () => !loading.value && !load_error.value && tweet_list.value.length === 0,
)

function feedParam(): 'following' | undefined {
  return active_tab.value === 'following' ? 'following' : undefined
}

async function loadTweets() {
  const seq = ++request_seq
  loading.value = true
  load_error.value = ''
  next_cursor.value = null
  has_more.value = false
  try {
    const page = await getTweets(null, PAGE_SIZE, feedParam())
    if (seq !== request_seq) return
    tweetStore.setFeed(page)
    next_cursor.value = page.nextCursor
    has_more.value = page.hasMore
  } catch {
    if (seq !== request_seq) return
    load_error.value =
      active_tab.value === 'following'
        ? '关注动态加载失败，请稍后重试'
        : '帖子加载失败，请确认后端已启动'
  } finally {
    if (seq === request_seq) loading.value = false
  }
}

async function loadMore() {
  if (!has_more.value || loading_more.value || loading.value) return
  const seq = request_seq
  loading_more.value = true
  load_error.value = ''
  try {
    const page = await getTweets(next_cursor.value, PAGE_SIZE, feedParam())
    if (seq !== request_seq) return
    tweetStore.appendFeed(page)
    next_cursor.value = page.nextCursor
    has_more.value = page.hasMore
  } catch {
    if (seq === request_seq) load_error.value = '加载更多失败，请稍后重试'
  } finally {
    loading_more.value = false
  }
}

function onPosted(tweet: TweetItem) {
  tweetStore.prependTweet(tweet)
  load_error.value = ''
}

function tab_click(tab: string) {
  const next: Feed = tab === '正在关注' ? 'following' : 'recommended'
  if (next === active_tab.value) return
  active_tab.value = next
  void loadTweets()
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
.empty {
  padding: 32px 24px;
  text-align: center;
  color: var(--black-color);
  border-bottom: var(--boundary-style);
}
.empty p {
  font-size: 15px;
  font-weight: 600;
}
.empty-hint {
  margin-top: 8px;
  font-weight: 400 !important;
  font-size: 14px !important;
  color: var(--grey-color);
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
