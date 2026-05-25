<template>
  <div id="grok">
    <div class="page_main">
      <main>
        <div class="top">
          <button class="expand-btn">
            <svg viewBox="0 0 24 24">
              <g><path :d="icon_expand"></path></g>
            </svg>
          </button>
          <div class="top-actions">
            <button class="action-btn">
              <svg viewBox="0 0 24 24">
                <g><path :d="icon_history"></path></g>
              </svg>
              <span>历史</span>
            </button>
            <button class="action-btn">
              <svg viewBox="0 0 24 24">
                <g v-html="icon_private"></g>
              </svg>
              <span>私密</span>
            </button>
          </div>
        </div>
        <div class="content" v-if="!hasChat">
          <div class="center-area">
            <div class="brand">
              <div class="logo">
                <svg viewBox="0 0 33 32">
                  <g><path :d="logo_grok"></path></g>
                </svg>
              </div>
              <div class="intro-title">Grok</div>
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
            </div>
          </div>
        </div>
        <div class="disclaimer">Grok 可能会显示不准确或冒犯性信息，请勿分享个人信息。</div>
      </main>
    </div>
  </div>
</template>
<script setup lang="ts">
import { ref } from 'vue'

const icon_expand = ref(
  'M3 5.5C3 4.12 4.12 3 5.5 3H10v2H5.5c-.28 0-.5.22-.5.5V10H3V5.5zM14 3h4.5C19.88 3 21 4.12 21 5.5V10h-2V5.5c0-.28-.22-.5-.5-.5H14V3zM5 14v4.5c0 .28.22.5.5.5H10v2H5.5C4.12 21 3 19.88 3 18.5V14h2zm16 0v4.5c0 1.38-1.12 2.5-2.5 2.5H14v-2h4.5c.28 0 .5-.22.5-.5V14h2z',
)

const icon_history = ref(
  'M12 4C9.25 4 6.83 5.39 5.38 7.5H8v2H2v-6h2V6c1.82-2.43 4.73-4 8-4 5.52 0 10 4.48 10 10s-4.48 10-10 10c-4.76 0-8.74-3.33-9.75-7.78l1.95-.44C5.01 17.34 8.19 20 12 20c4.42 0 8-3.58 8-8s-3.58-8-8-8zm-1 4h2v3.59l3.21 3.2-1.42 1.42-3.79-3.8V8z',
)

const icon_private = ref(
  '<mask fill="currentColor" id="a"><ellipse cx="8.334" cy="8.541" rx="1.042" ry="1.458"/></mask><ellipse cx="8.334" cy="8.541" fill="currentColor" rx="1.042" ry="1.458"/><path d="M9.375 8.541H8.042c0 .157-.047.244-.056.257-.003.004.016-.026.074-.06.062-.037.157-.071.274-.071v2.666c.83 0 1.455-.473 1.82-.986.369-.514.554-1.157.554-1.806H9.375zM8.334 10V8.667c.116 0 .211.034.273.071.058.034.078.064.075.06-.01-.013-.057-.1-.057-.257H5.959c0 .649.186 1.292.553 1.806.366.513.992.986 1.822.986V10zM7.292 8.54h1.333c0-.157.047-.243.057-.256.003-.004-.017.026-.075.06-.062.036-.157.071-.273.071V5.75c-.83 0-1.456.473-1.822.985-.367.515-.553 1.158-.553 1.806h1.333zm1.042-1.458v1.333c-.117 0-.212-.035-.274-.071-.058-.034-.077-.064-.074-.06.009.013.056.1.056.256h2.666c0-.648-.185-1.29-.553-1.806-.366-.512-.991-.985-1.821-.985v1.333z" fill="currentColor" mask="url(#a)"/><mask fill="currentColor" id="b"><ellipse cx="11.667" cy="8.541" rx="1.042" ry="1.458"/></mask><ellipse cx="11.667" cy="8.541" fill="currentColor" rx="1.042" ry="1.458"/><path d="M12.708 8.541h-1.333c0 .157-.047.244-.056.257-.003.004.016-.026.074-.06.062-.037.157-.071.274-.071v2.666c.83 0 1.455-.473 1.82-.986.369-.514.554-1.157.554-1.806h-1.333zM11.667 10V8.667c.116 0 .211.034.273.071.058.034.078.064.075.06-.01-.013-.057-.1-.057-.257H9.292c0 .649.186 1.292.553 1.806.366.513.992.986 1.822.986V10zm-1.042-1.46h1.333c0-.157.047-.243.057-.256.003-.004-.017.026-.075.06-.062.036-.157.071-.273.071V5.75c-.83 0-1.456.473-1.822.985-.367.515-.553 1.158-.553 1.806h1.333zm1.042-1.458v1.333c-.117 0-.212-.035-.274-.071-.058-.034-.077-.064-.075-.06.01.013.057.1.057.256h2.666c0-.648-.185-1.29-.553-1.806-.366-.512-.992-.985-1.821-.985v1.333z" fill="currentColor" mask="url(#b)"/><path d="M10 3.333c-6.667 0-3.27 5.601-7.5 7.5 0 1.374 1.17 1.25 1.608 2.308.367.886-.545 2.658-.775 3.525h3.334L10 17.5l3.333-.834h3.334c-.315-1.066-.993-2.38-.771-3.521.227-1.172 1.604-.86 1.604-2.312-4.23-1.899-.833-7.5-7.5-7.5z" fill="none" stroke="currentColor" stroke-width="1.333"/>',
)

const logo_grok = ref(
  'M12.745 20.54l10.97-8.19c.539-.4 1.307-.244 1.564.38 1.349 3.288.746 7.241-1.938 9.955-2.683 2.714-6.417 3.31-9.83 1.954l-3.728 1.745c5.347 3.697 11.84 2.782 15.898-1.324 3.219-3.255 4.216-7.692 3.284-11.693l.008.009c-1.351-5.878.332-8.227 3.782-13.031L33 0l-4.54 4.59v-.014L12.743 20.544m-2.263 1.987c-3.837-3.707-3.175-9.446.1-12.755 2.42-2.449 6.388-3.448 9.852-1.979l3.72-1.737c-.67-.49-1.53-1.017-2.515-1.387-4.455-1.854-9.789-.931-13.41 2.728-3.483 3.523-4.579 8.94-2.697 13.561 1.405 3.454-.899 5.898-3.22 8.364C1.49 30.2.666 31.074 0 32l10.478-9.466',
)

const icon_send = ref('M2.003 21.002L21.997 12 2.003 2.998v7.002L15 12l-12.997 2.002v7z')

const hasChat = ref(false)
</script>
<style scoped>
#grok {
  display: flex;
  justify-content: center;
  width: 100%;
}
.page_main {
  width: 100%;
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
.expand-btn {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.2s;
}
.expand-btn:hover {
  background: rgba(15, 20, 25, 0.08);
}
.expand-btn svg {
  width: 20px;
  height: 20px;
}
.top-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}
.action-btn {
  height: 36px;
  padding: 0 12px;
  border-radius: 18px;
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  font-weight: 500;
  color: var(--black-color);
  transition: background 0.2s;
}
.action-btn:hover {
  background: rgba(15, 20, 25, 0.08);
}
.action-btn svg {
  width: 20px;
  height: 20px;
}
.content {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 0 16px;
}
.center-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  width: 100%;
  max-width: 800px;
}
.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
}
.logo {
  width: 48px;
  height: 48px;
}
.logo svg {
  width: 100%;
  height: 100%;
}
.intro-title {
  font-size: 28px;
  font-weight: 700;
}
.input-area {
  width: 100%;
}
.input-box {
  display: flex;
  align-items: center;
  border: var(--boundary-style);
  border-color: rgba(0, 0, 0, 0.2);
  border-radius: 24px;
  padding: 4px 4px 4px 16px;
  transition: border-color 0.2s;
}
.input-box:focus-within {
  border-color: rgba(0, 0, 0, 0.6);
}
.input-box input {
  flex: 1;
  height: 36px;
  border: none;
  outline: none;
  font-size: 14px;
  caret-color: #000;
}
.send-btn {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--black-color);
  transition: background 0.2s;
  flex-shrink: 0;
}
.send-btn:hover {
  background: rgba(15, 20, 25, 0.8);
}
.send-btn svg {
  width: 16px;
  height: 16px;
  fill: #fff;
}
.disclaimer {
  font-size: 12px;
  color: var(--grey-color);
  padding: 12px 16px;
  text-align: center;
}
</style>
