<template>
  <div class="basic_layout">
    <div>
      <!-- <header v-if="notLogin"> -->
      <header>
        <g-lobal-nav></g-lobal-nav>
      </header>
      <main>
        <router-view></router-view>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import GLobalNav from '@/view/GlobalNav.vue'
import { ref } from 'vue'
import { RouterView, useRoute } from 'vue-router'

const route = useRoute()
</script>

<style>
.basic_layout {
  --boundary-style: 1px solid rgb(239, 243, 244);
  --theme-color: rgb(29, 155, 240);
  --theme-color-light: rgba(29, 155, 240, 0.1);
  --black-color: rgb(15, 20, 25);
  --black-color-light: rgba(15, 20, 25, 0.1);
  --grey-color: rgb(83, 100, 113);
  --grey-color-light: rgba(83, 100, 113, 0.1);

  width: 100vw;
  /* height: 100vh; */
  display: flex;
  justify-content: center;
  background: #fff;
}
.basic_layout > div {
  width: 100%;
  max-width: 1300px;
  display: flex;
  position: absolute;
  /* justify-content: space-around; */
}

header {
  /* position: relative; */
  min-width: 275px;
  width: 275px;
  height: 100vh;
}
header > div {
  position: fixed;
}
main {
  border-left: var(--boundary-style);
  width: 1050px;
}
</style>
