<template>
  <div id="grok">
    <div class="page_main">
      <main>
        <div class="top">
          <div class="title">Grok</div>
          <div class="icon">
            <svg viewBox="0 0 24 24">
              <g><path :d="icon_more"></path></g>
            </svg>
          </div>
        </div>
        <div class="content">
          <div class="intro">
            <div class="logo">
              <svg viewBox="0 0 33 32">
                <g><path :d="logo_grok"></path></g>
              </svg>
            </div>
            <div class="intro-title">Grok 由 xAI 提供支持</div>
            <div class="intro-text">
              Grok 会搜索并与其他人分享你的帖子，以帮助你更深入地了解你感兴趣的话题。
            </div>
          </div>
          <div class="suggestions">
            <div class="suggestions-title">试试这些提示</div>
            <div class="suggestion-list">
              <div
                class="suggestion"
                v-for="(item, index) in suggestions"
                :key="index"
              >
                <div class="suggestion-icon">
                  <svg viewBox="0 0 24 24">
                    <g><path :d="icon_prompt"></path></g>
                  </svg>
                </div>
                <div class="suggestion-text">{{ item }}</div>
              </div>
            </div>
          </div>
          <div class="input-area">
            <div class="input-box">
              <input type="text" placeholder="向 Grok 提问" />
              <button class="send-btn">
                <svg viewBox="0 0 24 24">
                  <g><path :d="icon_send"></path></g>
                </svg>
              </button>
            </div>
            <div class="disclaimer">
              Grok 可能会显示不准确或冒犯性信息，请勿分享个人信息。
            </div>
          </div>
        </div>
      </main>
    </div>
    <div class="side_bar">
      <div>
        <SubscribePremium></SubscribePremium>
        <RecommendFollowing></RecommendFollowing>
        <NavigationBar></NavigationBar>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { ref } from 'vue'
import SubscribePremium from './components/SideBar/SideBarComponents/SubscribePremium.vue'
import RecommendFollowing from './components/SideBar/SideBarComponents/RecommendFollowing.vue'
import NavigationBar from './components/SideBar/SideBarComponents/NavigationBar.vue'

const icon_more = ref(
  'M3 12c0-1.1.9-2 2-2s2 .9 2 2-.9 2-2 2-2-.9-2-2zm9 2c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2zm7 0c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2z',
)

const logo_grok = ref(
  'M12.745 20.54l10.97-8.19c.539-.4 1.307-.244 1.564.38 1.349 3.288.746 7.241-1.938 9.955-2.683 2.714-6.417 3.31-9.83 1.954l-3.728 1.745c5.347 3.697 11.84 2.782 15.898-1.324 3.219-3.255 4.216-7.692 3.284-11.693l.008.009c-1.351-5.878.332-8.227 3.782-13.031L33 0l-4.54 4.59v-.014L12.743 20.544m-2.263 1.987c-3.837-3.707-3.175-9.446.1-12.755 2.42-2.449 6.388-3.448 9.852-1.979l3.72-1.737c-.67-.49-1.53-1.017-2.515-1.387-4.455-1.854-9.789-.931-13.41 2.728-3.483 3.523-4.579 8.94-2.697 13.561 1.405 3.454-.899 5.898-3.22 8.364C1.49 30.2.666 31.074 0 32l10.478-9.466',
)

const icon_prompt = ref(
  'M9.5 4c-.276 0-.494.227-.562.495a6.004 6.004 0 0 0 5.567 5.567c.268-.068.495-.286.495-.562A5.5 5.5 0 0 0 9.5 4zM2 9.5C2 5.36 5.36 2 9.5 2c.832 0 1.5.524 1.5 1.37a7.504 7.504 0 0 1 6.63 6.63c.846 0 1.37.668 1.37 1.5 0 4.14-3.36 7.5-7.5 7.5a7.5 7.5 0 0 1-7.5-7.5c0-.832.524-1.5 1.37-1.5zM2 9.5H2zm18 7.5c0 2.485-2.015 4.5-4.5 4.5a4.497 4.497 0 0 1-3.901-2.232 8.46 8.46 0 0 0 2.185-.686A2.51 2.51 0 0 0 15.51 17c.975 0 1.808.556 2.222 1.374A4.462 4.462 0 0 0 20 17z',
)

const icon_send = ref(
  'M2.003 21.002L21.997 12 2.003 2.998v7.002L15 12l-12.997 2.002v7z',
)

const suggestions = ref([
  '今天有哪些热门话题？',
  '帮我总结最新的科技新闻',
  '推荐一些有趣的内容',
  '解释一下什么是人工智能',
])
</script>
<style scoped>
#grok {
  display: flex;
  width: 100%;

  --title-font-size: 20px;
  --title-font-weight: 700;
  --box-padding: 12px 16px;
}
.page_main {
  width: 100%;
  max-width: 600px;
}
main {
  width: 100%;
  min-height: 100vh;
  border-right: var(--boundary-style);
  display: flex;
  flex-direction: column;
}
.top {
  width: 100%;
  height: 53px;
  padding: 0 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.title {
  font-size: 20px;
  font-weight: 700;
}
.icon {
  display: flex;
  align-items: center;
}
svg {
  width: 20px;
  height: 20px;
}
.content {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 0 16px;
}
.intro {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40px 0 24px;
  border-bottom: var(--boundary-style);
}
.logo {
  width: 48px;
  height: 48px;
  margin-bottom: 16px;
}
.logo svg {
  width: 100%;
  height: 100%;
}
.intro-title {
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 8px;
}
.intro-text {
  font-size: 14px;
  color: var(--grey-color);
  text-align: center;
  line-height: 1.4;
  max-width: 400px;
}
.suggestions {
  padding: 24px 0;
  border-bottom: var(--boundary-style);
}
.suggestions-title {
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 16px;
}
.suggestion-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.suggestion {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  border: var(--boundary-style);
  border-radius: 12px;
  cursor: pointer;
  transition: background 0.2s;
}
.suggestion:hover {
  background: rgba(15, 20, 25, 0.04);
}
.suggestion-icon {
  width: 20px;
  height: 20px;
  margin-right: 12px;
  flex-shrink: 0;
}
.suggestion-icon svg {
  width: 100%;
  height: 100%;
  fill: var(--grey-color);
}
.suggestion-text {
  font-size: 14px;
}
.input-area {
  padding: 16px 0;
  margin-top: auto;
}
.input-box {
  display: flex;
  align-items: center;
  border: var(--boundary-style);
  border-radius: 24px;
  padding: 4px 4px 4px 16px;
}
.input-box:focus-within {
  border-color: var(--theme-color);
  border-width: 2px;
  padding-left: 15px;
}
.input-box input {
  flex: 1;
  height: 36px;
  border: none;
  outline: none;
  font-size: 14px;
  caret-color: var(--theme-color);
}
.send-btn {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--theme-color);
  transition: background 0.2s;
  flex-shrink: 0;
}
.send-btn:hover {
  background: rgba(29, 155, 240, 0.9);
}
.send-btn svg {
  width: 16px;
  height: 16px;
  fill: #fff;
}
.disclaimer {
  font-size: 12px;
  color: var(--grey-color);
  margin-top: 8px;
  text-align: center;
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
