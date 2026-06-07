<template>
  <div id="home_main">
    <div class="tab">
      <TabControl :tab_list="tab_list" @tab-click="tab_click"></TabControl>
    </div>
    <div class="send_tweet"></div>
    <div class="tweet_list">
      <div class="update_tweet">显示 333 帖子</div>
      <div class="tweets">
        <TweetShow v-for="(tweet, index) in tweet_list" :key="index" :tweet="tweet"></TweetShow>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { ref } from 'vue'
import TweetShow from './TweetShow.vue'
import TabControl from './TabControl.vue'

import { useTweetStore } from '@/stores/store'

import { getTweetList } from '../../request/api.js'

import type { Tweet } from '@/types'

const tab_list = ref(['为你推荐', '正在关注'])

const tweet_list = ref<Tweet[]>([])

const tweetStore = useTweetStore()
if (tweetStore.tweets.length) {
  tweet_list.value = tweetStore.tweets
} else {
  getTweetList()
    .then((res: Tweet[]) => {
      tweet_list.value = res
      tweetStore.setTweets(res)
    })
    .catch((err: any) => {
      tweet_list.value = [
        {
          id: '1',
          author: { id: 'Misaka', name: 'Misaka Mikoto', profile_photo: '/images/img.png' },
          text: '楽しかった！！！！</br>アンヴィル…………',
          images: '/images/92162034_p3.png',
          publishTime: '2026-06-04T02:00:00',
          interaction: { reply: 222, transpond: 333, upvote: 444, view: 999, bookmark: 10 },
        },
        {
          id: '2',
          author: { id: 'oO0Eve0Oo', name: 'Eve', profile_photo: '/images/img.png' },
          text: '今日もいい天気ですね〜',
          images: '/images/96919363_p0.jpg',
          publishTime: '2026-06-05T10:00:00',
          interaction: { reply: 55, transpond: 120, upvote: 310, view: 500, bookmark: 20 },
        },
        {
          id: '3',
          author: { id: 'akakura1341', name: '赤倉🧸', profile_photo: '/images/img.png' },
          text: '新しいプロジェクトを始めました！',
          images: '/images/92162034_p3.png',
          publishTime: '2026-06-03T15:00:00',
          interaction: { reply: 88, transpond: 200, upvote: 500, view: 1200, bookmark: 50 },
        },
      ]
    })
}

function tab_click(tab: string) {
  // console.log(tab)
}
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
.tab {
  height: 53px;
}
</style>
