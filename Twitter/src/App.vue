<template>
  <div>
    <router-view></router-view>
  </div>
</template>

<script setup lang="ts">
import { RouterView } from 'vue-router'
// import { getToken } from '@/request/api.js'
// import { useTokenStore } from '@/stores/store'

// getToken().then((res) => {
//   if (res.status == 'success') {
//     const tokenStore = useTokenStore()
//     tokenStore.setTokens(res.accessToken)
//   }
// })
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  border: none;
  list-style: none;
  font-size: 15px;
  background: none;
  caret-color: transparent;
  font-family:
    TwitterChirp,
    -apple-system,
    BlinkMacSystemFont,
    'Segoe UI',
    Roboto,
    Helvetica,
    Arial,
    sans-serif;
}
a {
  text-decoration: none;
}
</style>
