<template>
  <div id="tab-control">
    <div
      v-for="(tab, index) in tab_list"
      :key="index"
      @click="
        () => {
          $emit('tab-click', tab)
          change_tab_index(tab)
        }
      "
      :class="{ main_nav_now: tab === tab_index }"
    >
      <!-- :class="{ main_nav_now: index == page_main_top_nav_now_index }" -->
      {{ tab }}
      <div class="bottom_border"></div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { ref } from 'vue'

//      接收父组件参数
const prop = defineProps<{ tab_list: string[] }>()
const tab_list = ref(prop.tab_list)
//      index
const tab_index = ref(tab_list.value[0])
function change_tab_index(tab: string) {
  tab_index.value = tab
}
</script>
<style scoped>
#tab-control {
  height: 100%;
  /* height: 46px;
  line-height: 46px; */
  color: var(--grey-color);
  display: flex;
  align-items: center;
  /* font-size: 14px; */
  font-weight: 500;
  border-bottom: var(--boundary-style);
  user-select: none;
}
#tab-control > div {
  height: inherit;
  flex: 1;
  /* padding: 16px 0; */
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  transition: background 0.2s ease-in-out;
}
#tab-control div.main_nav_now {
  color: var(--black-color);
  font-weight: 700;
}
#tab-control div:hover {
  background: rgba(15, 20, 25, 0.1);
}
#tab-control div .bottom_border {
  position: absolute;
  width: 56px;
  height: 4px;
  background: var(--theme-color);
  bottom: 0;
  border-radius: 4px;
  display: none;
}
#tab-control div.main_nav_now .bottom_border {
  display: block;
}
</style>
