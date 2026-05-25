<template>
  <div id="search_bar">
    <div class="box">
      <span class="icon">
        <div></div>
        <svg viewBox="0 0 24 24" aria-hidden="true">
          <g>
            <path :d="search_icon"></path>
          </g>
        </svg>
      </span>
      <input type="text" placeholder="搜索" />
    </div>
  </div>
</template>
<script setup lang="ts">
import { ref } from 'vue'
const search_icon = ref(
  'M10.25 3.75c-3.59 0-6.5 2.91-6.5 6.5s2.91 6.5 6.5 6.5c1.795 0 3.419-.726 4.596-1.904 1.178-1.177 1.904-2.801 1.904-4.596 0-3.59-2.91-6.5-6.5-6.5zm-8.5 6.5c0-4.694 3.806-8.5 8.5-8.5s8.5 3.806 8.5 8.5c0 1.986-.682 3.815-1.824 5.262l4.781 4.781-1.414 1.414-4.781-4.781c-1.447 1.142-3.276 1.824-5.262 1.824-4.694 0-8.5-3.806-8.5-8.5z',
)
</script>
<style scoped>
#search_bar {
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
}
.box:focus-within {
  border-color: var(--theme-color);
  border-width: 2px;
  padding-left: 4px;
}
.icon {
  margin-left: 8px;
  display: flex;
  align-items: center;
}
.icon svg {
  width: 16px;
  height: 16px;
  fill: currentcolor;
  color: rgba(83, 100, 113, 1);
}
input {
  width: 100%;
  border: none;
  outline: none;
  font-size: 14px;
  margin-left: 2px;
}
input:focus {
  caret-color: var(--theme-color);
}
</style>
