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
    .catch((err) => {
      tweet_list.value = [
        {
          id: '1',
          text: '楽しかった！！！！</br>アンヴィル…………',
          images: '/public/images/92162034_p3.png',
          interaction: {
            reply: 222,
            transpond: 333,
            upvote: 444,
            view: 999,
          },
        },
        {
          id: '2',
          text: '楽しかった！！！！</br>アンヴィル…………',
          images: '/public/images/92162034_p3.png',
          interaction: {
            reply: 777,
            transpond: 888,
            upvote: 333,
            view: 80,
          },
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
