<template>
  <div id="navigation-bar">
    <ul>
      <li v-for="item in list" :key="item">
        {{ item }}
        <div></div>
      </li>
      <li class="more">
        更多
        <div>
          <svg viewBox="0 0 24 24">
            <g>
              <path
                d="M3 12c0-1.1.9-2 2-2s2 .9 2 2-.9 2-2 2-2-.9-2-2zm9 2c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2zm7 0c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2z"
              ></path>
            </g>
          </svg>
        </div>
      </li>
      <li>© 2026 X Corp.</li>
    </ul>
  </div>
</template>
<script setup lang="ts">
import { ref } from 'vue'

const list = ref(['服务条款', '隐私政策', 'Cookie 政策', '辅助功能', '广告信息'])
</script>
<style scoped>
#navigation-bar {
  /* width: 100%; */
  /* height: 50px; */
  border: none;
  margin-top: 5px;
  padding: var(--box-padding);
}
ul {
  max-width: 100%;
  height: 100%;
  display: flex;
  flex-wrap: wrap;
  overflow-wrap: break-word;
}
ul li {
  font-size: 11px;
  height: 20px;
  display: flex;
  color: var(--grey-color);
  align-items: center;
}
ul li:first-child {
  padding-left: 0px;
}
li div {
  width: 1px;
  height: 11px;
  margin: 0 14px;
  /* line-height: 11px; */
  background-color: rgba(130, 154, 171, 1);
}
.more div {
  width: 12px;
  height: 1em;
  margin-left: 1px;
  fill: rgba(130, 154, 171, 1);
  background-color: rgba(0, 0, 0, 0);
}
</style>
