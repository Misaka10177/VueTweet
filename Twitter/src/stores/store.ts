import { computed, reactive, ref } from 'vue'
import { defineStore } from 'pinia'

import type { ActionState, Trend, TweetItem, TweetPage } from '@/types'

export const useSettingStore = defineStore('setting', () => {
  const last_home_tab_index = ref(0)

  return { last_home_tab_index }
})

export const useTrendStore = defineStore('trend', () => {
  const trends = ref<Trend[]>([])

  function setTrend(trendList: Trend[]) {
    if (!trends.value.length) {
      trends.value = trendList
    }
  }

  return { trends, setTrend }
})

export const useTweetStore = defineStore('tweet', () => {
  const byId = reactive(new Map<string, TweetItem>())
  const feedIds = ref<string[]>([])
  const nextCursor = ref<string | null>(null)
  const hasMore = ref(false)

  const tweets = computed(() =>
    feedIds.value
      .map((id) => byId.get(id))
      .filter((tweet): tweet is TweetItem => Boolean(tweet)),
  )

  function put(tweet: TweetItem) {
    byId.set(tweet.id, tweet)
  }

  function setFeed(page: TweetPage) {
    byId.clear()
    feedIds.value = []
    appendFeed(page)
  }

  function appendFeed(page: TweetPage) {
    for (const tweet of page.items) {
      put(tweet)
      if (!feedIds.value.includes(tweet.id)) {
        feedIds.value.push(tweet.id)
      }
    }
    nextCursor.value = page.nextCursor
    hasMore.value = page.hasMore
  }

  function prependTweet(tweet: TweetItem) {
    put(tweet)
    feedIds.value = [tweet.id, ...feedIds.value.filter((id) => id !== tweet.id)]
  }

  function replace(id: string, tweet: TweetItem) {
    put(tweet)
    if (!feedIds.value.includes(id)) {
      feedIds.value.unshift(id)
    }
  }

  function applyAction(state: ActionState) {
    const current = byId.get(state.tweetId)
    if (!current) return
    put({ ...current, stats: state.stats, viewer: state.viewer })
  }

  function updateTweet(id: string, data: Partial<TweetItem>) {
    const current = byId.get(id)
    if (current) {
      put({ ...current, ...data })
    }
  }

  function removeTweet(id: string) {
    byId.delete(id)
    feedIds.value = feedIds.value.filter((tweetId) => tweetId !== id)
  }

  function getTweet(id: string): TweetItem | undefined {
    return byId.get(id)
  }

  return {
    byId,
    tweets,
    nextCursor,
    hasMore,
    putTweet: put,
    setFeed,
    appendFeed,
    prependTweet,
    replace,
    applyAction,
    updateTweet,
    removeTweet,
    getTweet,
  }
})
