<template>
  <div id="Home">
    <div class="page_main">
      <div class="main">
        <HomeMain></HomeMain>
      </div>
    </div>
    <div class="side_bar">
      <div>
        <SearchBar></SearchBar>
        <SubscribePremium></SubscribePremium>
        <Trendbar></Trendbar>
        <RecommendFollowing></RecommendFollowing>
        <NavigationBar></NavigationBar>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { ref } from 'vue'

import HomeMain from './components/HomeMain.vue'
import SearchBar from './components/SideBar/SideBarComponents/SearchBar.vue'
import SubscribePremium from './components/SideBar/SideBarComponents/SubscribePremium.vue'
import Trendbar from './components/SideBar/SideBarComponents/Trendbar.vue'
import RecommendFollowing from './components/SideBar/SideBarComponents/RecommendFollowing.vue'
import NavigationBar from './components/SideBar/SideBarComponents/NavigationBar.vue'
</script>

<style scoped>
#Home {
  display: flex;
  width: 100%;
  /* justify-content: space-around; */
}
.page_main {
  width: 100%;
  max-width: 600px;
}
.main {
  border-right: var(--boundary-style);
}

.tweets {
}

.side_bar {
  margin-left: 30px;
  margin-top: 10px;
}
.side_bar {
  --title-font-size: 20px;
  --title-font-weight: 700;
  --box-padding: 12px 16px;

  width: 350px;
}
.side_bar > div {
  width: inherit;
  position: fixed;
}
.side_bar > div > div {
  margin-top: 20px;
  width: 100%;
  padding-top: 26px;
  border: var(--boundary-style);
  border-radius: 20px;
}
</style>
