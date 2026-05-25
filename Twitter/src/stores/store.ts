import { ref, computed } from 'vue'
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
    if (!tweets.value.length) {
      tweets.value = tweetList
    }
  }

  return { tweets, setTweets }
})

export const useTokenStore = defineStore('token', () => {
  const accessToken = ref(<string>'')

  function setTokens(token: string) {
    accessToken.value = token
  }

  return { accessToken, setTokens }
})
export const useUserInfoStore = defineStore('userInfo', () => {
  const userId = ref('')
  const username = ref('')

  function setUserid(id: string) {
    userId.value = id
  }
  function setUsername(name: string) {
    username.value = name
  }

  return { userId, username, setUserid, setUsername }
})
