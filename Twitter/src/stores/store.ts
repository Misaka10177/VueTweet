import { ref } from 'vue'
import { defineStore } from 'pinia'

import type { Tweet, Trend } from '@/types'

export const useSettingStore = defineStore('setting', () => {
  const last_home_tab_index = ref(0)
  // const doubleCount = computed(() => count.value * 2)
  // function increment() {
  //   count.value++
  // }

  return { last_home_tab_index }
})

export const useTrendStore = defineStore('trend', () => {
  const trends = ref(<Trend[]>[])

  function setTrend(trendList: Trend[]) {
    if (!trends.value.length) {
      trends.value = trendList
    }
  }

  return { trends, setTrend }
})

export const useTweetStore = defineStore('tweet', () => {
  const tweets = ref(<Tweet[]>[])

  function setTweets(tweetList: Tweet[]) {
    const fetchedIds = new Set(tweetList.map((tweet) => tweet.id))
    const localOnly = tweets.value.filter((tweet) => !fetchedIds.has(tweet.id))
    tweets.value = [...localOnly, ...tweetList]
  }

  function updateTweet(id: string, data: Partial<Tweet>) {
    const index = tweets.value.findIndex((t) => t.id === id)
    const current = tweets.value[index]
    if (current) {
      tweets.value[index] = { ...current, ...data }
    }
  }

  function prependTweet(tweet: Tweet) {
    tweets.value = [tweet, ...tweets.value.filter((t) => t.id !== tweet.id)]
  }

  function appendTweets(tweetList: Tweet[]) {
    const existing = new Set(tweets.value.map((tweet) => tweet.id))
    const incoming = tweetList.filter((tweet) => !existing.has(tweet.id))
    tweets.value = [...tweets.value, ...incoming]
  }

  return { tweets, setTweets, updateTweet, prependTweet, appendTweets }
})
