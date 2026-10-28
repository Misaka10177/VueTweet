<template>
  <div id="search_page">
    <div class="page_main">
      <main>
        <div class="top">
          <SearchBar :suggestions="false" />
        </div>

        <div v-if="!query" class="state">
          <p class="state-title">搜索</p>
          <p>输入关键词，搜索用户、帖子或话题。</p>
        </div>
        <template v-else>
          <div class="result-summary">“{{ query }}” 的搜索结果</div>
          <div class="search-tabs">
            <button
              v-for="tab in tab_options"
              :key="tab.key"
              type="button"
              :class="{ active: active_tab === tab.key }"
              @click="selectTab(tab.key)"
            >
              {{ tab.label }}
            </button>
          </div>

          <div v-if="loading" class="state">搜索中…</div>
          <div v-else-if="error" class="state error" role="alert">{{ error }}</div>

          <template v-else-if="active_tab === 'top' && overview">
            <div v-if="!has_top" class="state">没有找到与 “{{ query }}” 相关的结果</div>

            <section v-if="overview.users.length" class="section">
              <div class="section-title">
                <span>用户</span>
                <button type="button" class="all-link" @click="selectTab('users')">查看全部</button>
              </div>
              <div
                v-for="user in overview.users"
                :key="user.id"
                class="user-row"
                @click="goUser(user.id)"
              >
                <img :src="mediaUrl(user.avatarUrl) || fallback_avatar" :alt="user.name" />
                <div class="user-meta">
                  <div class="name-row">
                    <span class="name">{{ user.name }}</span>
                    <FollowRelationBadge
                      :following="user.following"
                      :follows-you="user.followsYou"
                      :self="user.self"
                    />
                  </div>
                  <div class="handle">@{{ user.id }}</div>
                </div>
                <FollowButton
                  :user-id="user.id"
                  :following="user.following"
                  :self="user.self"
                  @change="onUserFollow(user, $event)"
                />
              </div>
            </section>

            <section v-if="overview.tweets.items.length" class="section">
              <div class="section-title">
                <span>帖子</span>
                <button type="button" class="all-link" @click="selectTab('tweets')">查看全部</button>
              </div>
              <TweetShow
                v-for="tweet in overview.tweets.items"
                :key="tweet.id"
                :tweet="tweet"
              ></TweetShow>
            </section>

            <section v-if="overview.topics.length" class="section">
              <div class="section-title">
                <span>话题</span>
                <button type="button" class="all-link" @click="selectTab('topics')">查看全部</button>
              </div>
              <button
                v-for="trend in overview.topics"
                :key="trend.category + trend.topic"
                type="button"
                class="topic-row"
                @click="searchTopic(trend.topic)"
              >
                <span class="topic-category">{{ trend.category }} 的趋势</span>
                <span class="topic-name">{{ trend.topic }}</span>
              </button>
            </section>
          </template>

          <template v-else-if="active_tab === 'users'">
            <div v-if="!users.length" class="state">没有找到相关用户</div>
            <div
              v-for="user in users"
              :key="user.id"
              class="user-row"
              @click="goUser(user.id)"
            >
              <img :src="mediaUrl(user.avatarUrl) || fallback_avatar" :alt="user.name" />
              <div class="user-meta">
                <div class="name-row">
                  <span class="name">{{ user.name }}</span>
                  <FollowRelationBadge
                    :following="user.following"
                    :follows-you="user.followsYou"
                    :self="user.self"
                  />
                </div>
                <div class="handle">@{{ user.id }}</div>
              </div>
              <FollowButton
                :user-id="user.id"
                :following="user.following"
                :self="user.self"
                @change="onUserFollow(user, $event)"
              />
            </div>
          </template>

          <template v-else-if="active_tab === 'tweets'">
            <div v-if="!tweets.length" class="state">没有找到相关帖子</div>
            <TweetShow v-for="tweet in tweets" :key="tweet.id" :tweet="tweet"></TweetShow>
            <button
              v-if="has_more"
              type="button"
              class="load-more"
              :disabled="loading_more"
              @click="loadMore"
            >
              {{ loading_more ? '加载中…' : '加载更多' }}
            </button>
          </template>

          <template v-else>
            <div v-if="!topics.length" class="state">没有找到相关话题</div>
            <button
              v-for="trend in topics"
              :key="trend.category + trend.topic"
              type="button"
              class="topic-row"
              @click="searchTopic(trend.topic)"
            >
              <span class="topic-category">{{ trend.category }} 的趋势</span>
              <span class="topic-name">{{ trend.topic }}</span>
            </button>
          </template>
        </template>
      </main>
    </div>

    <div class="side_bar">
      <div>
        <SubscribePremium></SubscribePremium>
        <TrendBar></TrendBar>
        <RecommendFollowing></RecommendFollowing>
        <NavigationBar></NavigationBar>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import type { FollowState, FollowUser, SearchOverview, Trend, TweetItem } from '@/types'
import {
  getSearchOverview,
  searchTopics,
  searchTweets,
  searchUsers,
} from '@/request/api'
import { useTweetStore } from '@/stores/store'
import { mediaUrl } from '@/utils/media'
import SearchBar from './components/SideBar/SideBarComponents/SearchBar.vue'
import TweetShow from './components/TweetShow.vue'
import FollowButton from './components/FollowButton.vue'
import FollowRelationBadge from './components/FollowRelationBadge.vue'
import SubscribePremium from './components/SideBar/SideBarComponents/SubscribePremium.vue'
import TrendBar from './components/SideBar/SideBarComponents/TrendBar.vue'
import RecommendFollowing from './components/SideBar/SideBarComponents/RecommendFollowing.vue'
import NavigationBar from './components/SideBar/SideBarComponents/NavigationBar.vue'

type SearchTab = 'top' | 'users' | 'tweets' | 'topics'

const tab_options: { key: SearchTab; label: string }[] = [
  { key: 'top', label: '热门' },
  { key: 'users', label: '用户' },
  { key: 'tweets', label: '帖子' },
  { key: 'topics', label: '话题' },
]

const route = useRoute()
const router = useRouter()
const tweetStore = useTweetStore()
const fallback_avatar = `${import.meta.env.BASE_URL}images/img.png`

const query = computed(() =>
  typeof route.query.q === 'string' ? route.query.q.trim() : '',
)
const active_tab = computed<SearchTab>(() => {
  const type = route.query.type
  if (type === 'users' || type === 'tweets' || type === 'topics') {
    return type
  }
  return 'top'
})

const overview = ref<SearchOverview | null>(null)
const users = ref<FollowUser[]>([])
const tweets = ref<TweetItem[]>([])
const topics = ref<Trend[]>([])
const loading = ref(false)
const loading_more = ref(false)
const error = ref('')
const cursor = ref<string | null>(null)
const has_more = ref(false)
let request_seq = 0

const has_top = computed(() => {
  const result = overview.value
  if (!result) return false
  return (
    result.users.length > 0 ||
    result.tweets.items.length > 0 ||
    result.topics.length > 0
  )
})

function resetData() {
  overview.value = null
  users.value = []
  tweets.value = []
  topics.value = []
  cursor.value = null
  has_more.value = false
  error.value = ''
}

async function load() {
  const value = query.value
  if (!value) {
    request_seq += 1
    resetData()
    loading.value = false
    return
  }

  const seq = ++request_seq
  loading.value = true
  resetData()
  try {
    if (active_tab.value === 'top') {
      const result = await getSearchOverview(value)
      if (seq !== request_seq) return
      overview.value = result
      result.tweets.items.forEach((tweet) => tweetStore.putTweet(tweet))
    } else if (active_tab.value === 'users') {
      const result = await searchUsers(value, 50)
      if (seq !== request_seq) return
      users.value = result
    } else if (active_tab.value === 'tweets') {
      const page = await searchTweets(value, null, 20)
      if (seq !== request_seq) return
      page.items.forEach((tweet) => tweetStore.putTweet(tweet))
      tweets.value = page.items
      cursor.value = page.nextCursor
      has_more.value = page.hasMore
    } else {
      const result = await searchTopics(value, 50)
      if (seq !== request_seq) return
      topics.value = result
    }
  } catch {
    if (seq === request_seq) error.value = '搜索失败，请稍后重试'
  } finally {
    if (seq === request_seq) loading.value = false
  }
}

async function loadMore() {
  if (!has_more.value || loading_more.value || !cursor.value) return
  const value = query.value
  const seq = request_seq
  loading_more.value = true
  try {
    const page = await searchTweets(value, cursor.value, 20)
    if (seq !== request_seq) return
    page.items.forEach((tweet) => tweetStore.putTweet(tweet))
    const known = new Set(tweets.value.map((tweet) => tweet.id))
    tweets.value = [...tweets.value, ...page.items.filter((tweet) => !known.has(tweet.id))]
    cursor.value = page.nextCursor
    has_more.value = page.hasMore
  } catch {
    if (seq === request_seq) error.value = '加载更多失败，请稍后重试'
  } finally {
    loading_more.value = false
  }
}

function selectTab(tab: SearchTab) {
  if (tab === active_tab.value) return
  const target: Record<string, string> = { q: query.value }
  if (tab !== 'top') target.type = tab
  void router.replace({ path: '/search', query: target })
}

function goUser(userId: string) {
  void router.push(`/user/${encodeURIComponent(userId)}`)
}

function searchTopic(topic: string) {
  void router.push({ path: '/search', query: { q: topic, type: 'tweets' } })
}

function onUserFollow(user: FollowUser, state: FollowState) {
  user.following = state.following
  user.followsYou = state.followsYou
}

watch(
  [() => route.query.q, () => route.query.type],
  () => {
    void load()
  },
  { immediate: true },
)
</script>

<style scoped>
#search_page {
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
  padding: 8px 16px;
}
.result-summary {
  padding: 8px 16px 12px;
  font-size: 19px;
  font-weight: 700;
  overflow-wrap: anywhere;
}
.search-tabs {
  display: flex;
  border-bottom: var(--boundary-style);
}
.search-tabs button {
  flex: 1;
  height: 53px;
  border: none;
  background: #fff;
  color: var(--grey-color);
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  position: relative;
}
.search-tabs button:hover {
  background: rgba(15, 20, 25, 0.04);
}
.search-tabs button.active {
  color: var(--black-color);
  font-weight: 700;
}
.search-tabs button.active::after {
  content: '';
  position: absolute;
  left: 50%;
  bottom: 0;
  width: 56px;
  height: 4px;
  border-radius: 4px;
  background: var(--theme-color);
  transform: translateX(-50%);
}
.section-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 16px 8px;
  font-size: 19px;
  font-weight: 700;
}
.all-link {
  border: none;
  background: none;
  color: var(--theme-color);
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
}
.user-row {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  border-bottom: var(--boundary-style);
  cursor: pointer;
}
.user-row:hover {
  background: rgba(0, 0, 0, 0.03);
}
.user-row > img {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  object-fit: cover;
  flex: none;
}
.user-meta {
  flex: 1;
  min-width: 0;
}
.name-row {
  display: flex;
  align-items: center;
  gap: 4px;
  min-width: 0;
}
.name {
  font-weight: 700;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.handle {
  color: var(--grey-color);
  font-size: 14px;
}
.topic-row {
  display: flex;
  flex-direction: column;
  gap: 2px;
  width: 100%;
  padding: 12px 16px;
  border: none;
  border-bottom: var(--boundary-style);
  background: #fff;
  text-align: left;
  cursor: pointer;
}
.topic-row:hover {
  background: rgba(0, 0, 0, 0.03);
}
.topic-category {
  color: var(--grey-color);
  font-size: 13px;
}
.topic-name {
  font-size: 15px;
  font-weight: 700;
}
.state {
  padding: 40px 24px;
  text-align: center;
  color: var(--grey-color);
  font-size: 14px;
}
.state-title {
  margin-bottom: 8px;
  color: var(--black-color);
  font-size: 20px;
  font-weight: 700;
}
.state.error {
  color: #f4212e;
}
.load-more {
  display: block;
  width: 100%;
  padding: 14px;
  border: none;
  border-bottom: var(--boundary-style);
  background: #fff;
  color: var(--theme-color);
  font-size: 14px;
  cursor: pointer;
}
.load-more:disabled {
  color: var(--grey-color);
  cursor: default;
}
.side_bar {
  margin-left: 30px;
  margin-top: 10px;
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
