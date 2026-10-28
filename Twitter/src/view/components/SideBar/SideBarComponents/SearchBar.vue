<template>
  <div id="search_bar">
    <div class="box">
      <button type="button" class="icon" aria-label="搜索" @click="submit">
        <svg viewBox="0 0 24 24" aria-hidden="true">
          <g>
            <path :d="search_icon"></path>
          </g>
        </svg>
      </button>
      <input
        ref="input_el"
        v-model="query"
        type="text"
        placeholder="搜索"
        aria-label="搜索用户、帖子或话题"
        aria-autocomplete="list"
        :aria-expanded="panel_visible"
        autocomplete="off"
        @focus="onFocus"
        @blur="onBlur"
        @keydown.enter.prevent="submit"
        @keydown.esc.prevent="blurInput"
      />
      <button
        v-if="query"
        type="button"
        class="clear"
        aria-label="清空搜索"
        @click="clear"
      >
        ×
      </button>
    </div>

    <div
      v-if="suggestions && panel_visible"
      class="panel"
      role="listbox"
      aria-label="搜索建议"
      @mousedown.prevent
    >
      <div v-if="loading && !overview" class="hint">搜索中…</div>
      <div v-else-if="error" class="hint error" role="alert">{{ error }}</div>
      <template v-else-if="overview">
        <div v-if="!has_results" class="hint">没有找到与 “{{ query.trim() }}” 相关的结果</div>

        <div v-if="overview.users.length" class="group">
          <div class="group-title">用户</div>
          <button
            v-for="user in overview.users"
            :key="user.id"
            type="button"
            class="row"
            @click="goUser(user.id)"
          >
            <img :src="mediaUrl(user.avatarUrl) || fallback_avatar" :alt="user.name" />
            <span class="row-meta">
              <span class="row-name">{{ user.name }}</span>
              <span class="row-handle">@{{ user.id }}</span>
            </span>
            <span v-if="user.followsYou" class="relation">关注了你</span>
          </button>
        </div>

        <div v-if="overview.tweets.items.length" class="group">
          <div class="group-title">帖子</div>
          <button
            v-for="tweet in overview.tweets.items"
            :key="tweet.id"
            type="button"
            class="row tweet-row"
            @click="goTweet(tweet.id)"
          >
            <span class="tweet-meta">@{{ tweet.author.id }}</span>
            <span class="tweet-text">{{ preview(tweet.text) }}</span>
          </button>
        </div>

        <div v-if="overview.topics.length" class="group">
          <div class="group-title">话题</div>
          <button
            v-for="trend in overview.topics"
            :key="trend.category + trend.topic"
            type="button"
            class="row topic-row"
            @click="goTopic(trend.topic)"
          >
            <span class="topic-category">{{ trend.category }} 的趋势</span>
            <span class="topic-name">{{ trend.topic }}</span>
          </button>
        </div>

        <button type="button" class="all" @click="submit">
          搜索 “{{ query.trim() }}”
        </button>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import type { SearchOverview } from '@/types'
import { getSearchOverview } from '@/request/api'
import { mediaUrl } from '@/utils/media'

const props = withDefaults(defineProps<{ suggestions?: boolean }>(), { suggestions: true })

const router = useRouter()
const route = useRoute()
const fallback_avatar = `${import.meta.env.BASE_URL}images/img.png`
const search_icon = ref(
  'M10.25 3.75c-3.59 0-6.5 2.91-6.5 6.5s2.91 6.5 6.5 6.5c1.795 0 3.419-.726 4.596-1.904 1.178-1.177 1.904-2.801 1.904-4.596 0-3.59-2.91-6.5-6.5-6.5zm-8.5 6.5c0-4.694 3.806-8.5 8.5-8.5s8.5 3.806 8.5 8.5c0 1.986-.682 3.815-1.824 5.262l4.781 4.781-1.414 1.414-4.781-4.781c-1.447 1.142-3.276 1.824-5.262 1.824-4.694 0-8.5-3.806-8.5-8.5z',
)

const input_el = ref<HTMLInputElement | null>(null)
const query = ref('')
const focused = ref(false)
const loading = ref(false)
const overview = ref<SearchOverview | null>(null)
const error = ref('')
let debounce_timer: number | undefined
let request_seq = 0

const has_query = computed(() => query.value.trim().length > 0)
const panel_visible = computed(() => focused.value && has_query.value)
const has_results = computed(() => {
  const result = overview.value
  if (!result) return false
  return result.users.length > 0 || result.tweets.items.length > 0 || result.topics.length > 0
})

watch(
  () => route.query.q,
  (value) => {
    if (typeof value === 'string') {
      query.value = value
    }
  },
  { immediate: true },
)

watch(query, (value) => {
  window.clearTimeout(debounce_timer)
  const trimmed = value.trim()
  if (!trimmed) {
    request_seq += 1
    overview.value = null
    error.value = ''
    loading.value = false
    return
  }
  if (!props.suggestions) return
  request_seq += 1
  overview.value = null
  loading.value = true
  debounce_timer = window.setTimeout(() => {
    void loadSuggestions(trimmed)
  }, 250)
})

async function loadSuggestions(value: string) {
  const seq = ++request_seq
  loading.value = true
  error.value = ''
  try {
    const result = await getSearchOverview(value)
    if (seq !== request_seq) return
    overview.value = result
  } catch {
    if (seq !== request_seq) return
    overview.value = null
    error.value = '搜索失败，请稍后重试'
  } finally {
    if (seq === request_seq) loading.value = false
  }
}

function onFocus() {
  focused.value = true
  if (props.suggestions && has_query.value && !overview.value && !loading.value) {
    void loadSuggestions(query.value.trim())
  }
}

function onBlur() {
  window.setTimeout(() => {
    focused.value = false
  }, 150)
}

function blurInput() {
  input_el.value?.blur()
}

function clear() {
  query.value = ''
  overview.value = null
  error.value = ''
  input_el.value?.focus()
}

function submit() {
  const value = query.value.trim()
  if (!value) return
  focused.value = false
  blurInput()
  void router.push({ path: '/search', query: { q: value } })
}

function goUser(userId: string) {
  focused.value = false
  void router.push(`/user/${encodeURIComponent(userId)}`)
}

function goTweet(tweetId: string) {
  focused.value = false
  void router.push(`/tweet/${encodeURIComponent(tweetId)}`)
}

function goTopic(topic: string) {
  focused.value = false
  void router.push({ path: '/search', query: { q: topic, type: 'tweets' } })
}

function preview(text: string) {
  const normalized = text.replace(/\s+/g, ' ').trim()
  return normalized.length > 80 ? `${normalized.slice(0, 80)}…` : normalized
}

onBeforeUnmount(() => {
  window.clearTimeout(debounce_timer)
})
</script>

<style scoped>
#search_bar {
  position: relative;
  width: 100%;
  border: none;
  margin: 0;
  padding: 0;
}
.box {
  width: 100%;
  height: 42px;
  border: 1px solid rgba(83, 100, 113, 0.5);
  border-radius: 22px;
  display: flex;
  align-items: center;
  padding: 5px;
  background: #fff;
}
.box:focus-within {
  border-color: var(--theme-color);
  border-width: 2px;
  padding-left: 4px;
}
.icon {
  margin-left: 4px;
  display: flex;
  align-items: center;
  padding: 0;
  border: none;
  background: none;
  cursor: pointer;
}
.icon svg {
  width: 16px;
  height: 16px;
  fill: currentcolor;
  color: rgba(83, 100, 113, 1);
}
input {
  width: 100%;
  min-width: 0;
  border: none;
  outline: none;
  font-size: 14px;
  margin-left: 6px;
  background: transparent;
}
input:focus {
  caret-color: var(--theme-color);
}
.clear {
  width: 24px;
  height: 24px;
  margin-right: 4px;
  border: none;
  border-radius: 50%;
  background: var(--theme-color);
  color: #fff;
  font-size: 16px;
  line-height: 1;
  cursor: pointer;
  flex: none;
}
.panel {
  position: absolute;
  top: 46px;
  left: 0;
  right: 0;
  z-index: 80;
  max-height: 430px;
  overflow-y: auto;
  background: #fff;
  border: var(--boundary-style);
  border-radius: 16px;
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.16);
}
.hint {
  padding: 18px 16px;
  color: var(--grey-color);
  font-size: 14px;
  text-align: center;
}
.hint.error {
  color: #f4212e;
}
.group {
  border-bottom: var(--boundary-style);
}
.group-title {
  padding: 10px 16px 6px;
  color: var(--grey-color);
  font-size: 13px;
  font-weight: 700;
}
.row {
  display: flex;
  align-items: center;
  width: 100%;
  gap: 8px;
  padding: 9px 16px;
  border: none;
  background: #fff;
  text-align: left;
  cursor: pointer;
}
.row:hover {
  background: rgba(0, 0, 0, 0.03);
}
.row img {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
  flex: none;
}
.row-meta {
  display: flex;
  flex-direction: column;
  min-width: 0;
  flex: 1;
}
.row-name {
  font-weight: 700;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.row-handle {
  color: var(--grey-color);
  font-size: 13px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.relation {
  flex: none;
  padding: 1px 6px;
  border-radius: 4px;
  background: #eff3f4;
  color: #536471;
  font-size: 12px;
}
.tweet-row,
.topic-row {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 2px;
}
.tweet-meta,
.topic-category {
  color: var(--grey-color);
  font-size: 13px;
}
.tweet-text {
  color: var(--black-color);
  font-size: 14px;
  line-height: 1.35;
}
.topic-name {
  font-weight: 700;
}
.all {
  display: block;
  width: 100%;
  padding: 13px 16px;
  border: none;
  background: #fff;
  color: var(--theme-color);
  font-size: 14px;
  font-weight: 600;
  text-align: left;
  cursor: pointer;
}
.all:hover {
  background: rgba(29, 155, 240, 0.06);
}
</style>
