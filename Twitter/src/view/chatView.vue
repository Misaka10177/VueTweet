<template>
  <div id="chat">
    <div class="page_main">
      <main>
        <div class="left-panel">
          <div class="panel-top">
            <div class="title">聊天</div>
            <button class="new-msg-btn">
              <svg viewBox="0 0 24 24">
                <g><path :d="icon_new_message"></path></g>
              </svg>
            </button>
          </div>
          <div class="search">
            <SearchBar></SearchBar>
          </div>
          <div class="contact-list">
            <div
              class="contact"
              v-for="(contact, index) in contacts"
              :key="index"
              :class="{ active: activeContact === index }"
              @click="selectContact(index)"
            >
              <div class="avatar">
                <img :src="contact.avatar" alt="" />
              </div>
              <div class="info">
                <div class="name">{{ contact.name }}</div>
                <div class="preview">{{ contact.preview }}</div>
              </div>
              <div class="time">{{ contact.time }}</div>
            </div>
            <div class="empty-contacts" v-if="contacts.length === 0">
              <div class="empty-text">暂无消息</div>
            </div>
          </div>
        </div>
        <div class="right-panel">
          <div class="panel-top" v-if="activeContact !== null">
            <div class="contact-name">{{ contacts[activeContact]?.name }}</div>
            <div class="contact-handle">@{{ contacts[activeContact]?.handle }}</div>
          </div>
          <div class="message-area" v-if="activeContact !== null">
            <div class="messages">
              <div class="no-messages">暂无消息记录</div>
            </div>
            <div class="msg-input">
              <div class="input-box">
                <input type="text" placeholder="开始新消息" />
                <button class="send-btn">
                  <svg viewBox="0 0 24 24">
                    <g><path :d="icon_send"></path></g>
                  </svg>
                </button>
              </div>
            </div>
          </div>
          <div class="no-chat" v-else>
            <div class="no-chat-logo">
              <svg viewBox="0 0 24 24">
                <g><path :d="icon_env"></path></g>
              </svg>
            </div>
            <div class="no-chat-title">选择一条消息</div>
            <div class="no-chat-subtitle">
              从你的联系人或正在进行的对话中选择一条，然后开始聊天。
            </div>
            <button class="start-chat-btn">开始聊天</button>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>
<script setup lang="ts">
import { ref } from 'vue'
import SearchBar from './components/SideBar/SideBarComponents/SearchBar.vue'

const icon_env = ref(
  'M20.7 11.7c0-4.48-3.844-8.2-8.699-8.2-4.854 0-8.698 3.72-8.698 8.2v.015l-.001.014c-.02.667.09 1.225.25 1.767.083.28.176.545.276.839.098.285.202.595.288.918.177.663.284 1.401.156 2.271-.086.582-.274 1.191-.582 1.855 1.264.375 2.55.053 4.013-.599l.455-.203.437.242c1.07.594 1.917 1.08 3.406 1.08 4.855 0 8.7-3.72 8.7-8.199zm2 0c0 5.683-4.84 10.2-10.699 10.2-1.784 0-2.96-.555-3.95-1.095-1.876.768-4.02 1.2-6.245-.075l-.885-.505.524-.875c.54-.904.77-1.581.848-2.118.078-.526.02-.98-.11-1.463-.066-.25-.15-.502-.247-.788-.095-.277-.204-.59-.301-.92-.199-.674-.36-1.449-.332-2.39C1.322 6.002 6.154 1.5 12.002 1.5c5.859 0 10.7 4.518 10.7 10.2z',
)

const icon_new_message = ref(
  'M1.998 5.5c0-1.381 1.119-2.5 2.5-2.5h15c1.381 0 2.5 1.119 2.5 2.5v13c0 1.381-1.119 2.5-2.5 2.5h-15c-1.381 0-2.5-1.119-2.5-2.5v-13zm2.5-.5c-.276 0-.5.224-.5.5v2.764l8 3.638 8-3.636V5.5c0-.276-.224-.5-.5-.5h-15zm15.5 5.463l-8 3.636-8-3.638V18.5c0 .276.224.5.5.5h15c.276 0 .5-.224.5-.5v-8.037z',
)

const icon_send = ref('M2.003 21.002L21.997 12 2.003 2.998v7.002L15 12l-12.997 2.002v7z')

const activeContact = ref<number | null>(null)

const contacts = ref<
  { avatar: string; name: string; handle: string; preview: string; time: string }[]
>([])

function selectContact(index: number) {
  activeContact.value = index
}
</script>
<style scoped>
#chat {
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
}
.left-panel {
  width: 370px;
  min-width: 370px;
  border-right: var(--boundary-style);
  display: flex;
  flex-direction: column;
}
.panel-top {
  width: 100%;
  height: 53px;
  padding: 0 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.panel-top .title {
  font-size: 20px;
  font-weight: 700;
}
.panel-top .contact-name {
  font-size: 17px;
  font-weight: 600;
}
.panel-top .contact-handle {
  font-size: 13px;
  color: var(--grey-color);
}
.new-msg-btn {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.2s;
}
.new-msg-btn:hover {
  background: rgba(15, 20, 25, 0.08);
}
.new-msg-btn svg {
  width: 20px;
  height: 20px;
}
.search {
  padding: 8px 16px;
}
.contact-list {
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
}
.contact {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  cursor: pointer;
  transition: background 0.2s;
}
.contact:hover {
  background: rgba(15, 20, 25, 0.04);
}
.contact.active {
  background: rgba(29, 155, 240, 0.1);
}
.contact .avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  margin-right: 12px;
  flex-shrink: 0;
}
.contact .avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.contact .info {
  flex: 1;
  min-width: 0;
}
.contact .name {
  font-size: 15px;
  font-weight: 500;
  margin-bottom: 2px;
}
.contact .preview {
  font-size: 13px;
  color: var(--grey-color);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.contact .time {
  font-size: 13px;
  color: var(--grey-color);
  margin-left: 8px;
  flex-shrink: 0;
}
.empty-contacts {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}
.empty-text {
  font-size: 24px;
  font-weight: 700;
}
.start-chat-btn {
  height: 36px;
  padding: 0 20px;
  border-radius: 18px;
  font-size: 14px;
  font-weight: 600;
  color: #fff;
  background: var(--black-color);
  transition: background 0.2s;
}
.start-chat-btn:hover {
  background: rgba(15, 20, 25, 0.8);
}
.right-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
}
.right-panel .panel-top {
  border-bottom: var(--boundary-style);
}
.message-area {
  flex: 1;
  display: flex;
  flex-direction: column;
}
.messages {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}
.no-messages {
  font-size: 14px;
  color: var(--grey-color);
}
.msg-input {
  padding: 12px 16px;
  border-top: var(--boundary-style);
}
.msg-input .input-box {
  display: flex;
  align-items: center;
  border: var(--boundary-style);
  border-radius: 24px;
  padding: 4px 4px 4px 16px;
}
.msg-input .input-box:focus-within {
  border-color: rgba(0, 0, 0, 0.4);
  border-width: 2px;
  padding-left: 15px;
  padding-right: 3px;
}
.msg-input input {
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
.no-chat {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
}
.no-chat-logo {
  width: 48px;
  height: 48px;
  margin-bottom: 16px;
}
.no-chat-logo svg {
  width: 100%;
  height: 100%;
}
.no-chat-title {
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 8px;
}
.no-chat-subtitle {
  font-size: 14px;
  color: var(--grey-color);
  text-align: center;
  margin-bottom: 24px;
  max-width: 320px;
  line-height: 1.4;
}
</style>
