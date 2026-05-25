<template>
  <div id="trend_bar">
    <div class="title" v-if="prop.showTitle">有什么新鲜事</div>
    <div class="trend-list">
      <ul>
        <li v-for="(trend, index) in trends.slice(0, showMax)" :key="index">
          <div>{{ trend.category }} 的趋势</div>
          <div>{{ trend.topic }}</div>
        </li>
      </ul>
    </div>
  </div>
</template>
<script setup lang="ts">
import { ref } from 'vue'
import { getTrendList } from '@/request/api'
import { useRoute } from 'vue-router'
import { useTrendStore } from '@/stores/store'

import type { Trend } from '@/types'

const showMax = ref(4)
const route = useRoute()
if (route.path == '/explore') {
  showMax.value = 12
}

const trends = ref(<Trend[]>[
  {
    category: '中国香港特别行政区',
    topic: 'LING NIVEA BRAND FRIEND',
  },
  {
    category: '台湾',
    topic: '#skywongravee',
  },
])

const prop = withDefaults(defineProps<{ showTitle?: boolean }>(), { showTitle: true })
//          Pinia
const trendStore = useTrendStore()
if (trendStore.trends.length) {
  trends.value = trendStore.trends
} else {
  getTrendList().then((res: Trend[]) => {
    trends.value = res
    trendStore.setTrend(res)
  })
}
</script>
<style scoped>
#trend_bar {
}
.title {
  font-size: var(--title-font-size);
  font-weight: var(--title-font-weight);
  padding: var(--box-padding);
  padding-top: 0;
}
li {
  padding: var(--box-padding);
}
li:hover {
  background-color: rgba(0, 0, 0, 0.03);
}
li div:first-child {
  font-size: 13px;
  color: var(--grey-color);
  margin-bottom: 2px;
}
li div:last-child {
  font-weight: 700;
}
</style>
