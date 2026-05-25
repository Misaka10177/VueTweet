<template>
  <div id="login">
    <div class="form">
      <div class="top">
        <router-link to="/join"
          ><div class="back">
            <div class="icon">
              <svg viewBox="0 0 24 24" aria-hidden="true">
                <g>
                  <path :d="icon_back"></path>
                </g>
              </svg>
            </div></div
        ></router-link>
        <div class="logo">
          <div class="icon">
            <svg viewBox="0 0 24 24" aria-hidden="true">
              <g>
                <path :d="logo_x"></path>
              </g>
            </svg>
          </div>
        </div>
      </div>
      <div class="login" v-if="!user_exists">
        <div class="title">登入 X</div>
        <button class="login_option" v-for="(item, index) in login_option" :key="index">
          <div class="icon">
            <svg :viewBox="item.viewBox ? item.viewBox : '0 0 24 24'" aria-hidden="true">
              <g v-html="item.icon"></g>
            </svg>
          </div>
          <div>{{ item.title }}</div>
        </button>
        <!-- </div> -->
        <div class="or">
          <div></div>
          <span>或</span>
          <div></div>
        </div>
        <div class="userName">
          <input v-model="userName" type="text" placeholder="手机号码、邮件地址或用户名" />
        </div>
        <button class="next" @click="next">下一步</button>
        <button class="forget_password">忘记密码？</button>
      </div>
      <div class="login_ap" v-if="user_exists">
        <div class="info_input">
          <h1>输入你的密码</h1>
          <div class="account">
            <div>用户名</div>
            <div>{{ userName }}</div>
          </div>
          <div class="password">
            <input v-model="password" type="password" placeholder="密码" />
          </div>
          <div class="forget_password">忘记密码？</div>
        </div>
        <div class="login_input">
          <button @click="click_login" :disabled="password.length > 0 ? false : true">登录</button>
          <div class="tip">还没有账号？<span>注册</span></div>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { ref } from 'vue'
import { checkUser, login } from '@/request/api.js'

import { useTokenStore } from '@/stores/store'

import router from '@/router'

const user_exists = ref(false)

const logo_x = ref(
  'M21.742 21.75l-7.563-11.179 7.056-8.321h-2.456l-5.691 6.714-4.54-6.714H2.359l7.29 10.776L2.25 21.75h2.456l6.035-7.118 4.818 7.118h6.191-.008zM7.739 3.818L18.81 20.182h-2.447L5.29 3.818h2.447z',
)
const icon_back = ref(
  'M10.59 12L4.54 5.96l1.42-1.42L12 10.59l6.04-6.05 1.42 1.42L13.41 12l6.05 6.04-1.42 1.42L12 13.41l-6.04 6.05-1.42-1.42L10.59 12z',
)

const login_option = ref([
  {
    name: 'google',
    icon: '<path fill="#EA4335" d="M24 9.5c3.54 0 6.71 1.22 9.21 3.6l6.85-6.85C35.9 2.38 30.47 0 24 0 14.62 0 6.51 5.38 2.56 13.22l7.98 6.19C12.43 13.72 17.74 9.5 24 9.5z"></path><path fill="#4285F4" d="M46.98 24.55c0-1.57-.15-3.09-.38-4.55H24v9.02h12.94c-.58 2.96-2.26 5.48-4.78 7.18l7.73 6c4.51-4.18 7.09-10.36 7.09-17.65z"></path><path fill="#FBBC05" d="M10.53 28.59c-.48-1.45-.76-2.99-.76-4.59s.27-3.14.76-4.59l-7.98-6.19C.92 16.46 0 20.12 0 24c0 3.88.92 7.54 2.56 10.78l7.97-6.19z"></path><path fill="#34A853" d="M24 48c6.48 0 11.93-2.13 15.89-5.81l-7.73-6c-2.15 1.45-4.92 2.3-8.16 2.3-6.26 0-11.57-4.22-13.47-9.91l-7.98 6.19C6.51 42.62 14.62 48 24 48z"></path><path fill="none" d="M0 0h48v48H0z"></path>',
    title: '使用 Google 账号注册',
    viewBox: '0 0 48 48',
  },
  {
    name: 'apple',
    icon: '<path d="M16.365 1.43c0 1.14-.493 2.27-1.177 3.08-.744.9-1.99 1.57-2.987 1.57-.12 0-.23-.02-.3-.03-.01-.06-.04-.22-.04-.39 0-1.15.572-2.27 1.206-2.98.804-.94 2.142-1.64 3.248-1.68.03.13.05.28.05.43zm4.565 15.71c-.03.07-.463 1.58-1.518 3.12-.945 1.34-1.94 2.71-3.43 2.71-1.517 0-1.9-.88-3.63-.88-1.698 0-2.302.91-3.67.91-1.377 0-2.332-1.26-3.428-2.8-1.287-1.82-2.323-4.63-2.323-7.28 0-4.28 2.797-6.55 5.552-6.55 1.448 0 2.675.95 3.6.95.865 0 2.222-1.01 3.902-1.01.613 0 2.886.06 4.374 2.19-.13.09-2.383 1.37-2.383 4.19 0 3.26 2.854 4.42 2.955 4.45z"></path>',
    title: '使用 Apple 注册',
  },
])

const userName = ref('')
const password = ref('')
function next() {
  checkUser({ username: userName.value }).then((res) => {
    if (res.status == 'success') {
      user_exists.value = true
    }
  })
}
function click_login() {
  login({ username: userName.value, password: password.value }).then((res) => {
    if (res.status == 'success') {
      const tokenStore = useTokenStore()
      tokenStore.setTokens(res.accessToken)
      router.push('/')
    }
  })
}
</script>
<style scoped>
#login {
  width: 100vw;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: rgba(180, 180, 182, 0.5);

  --boundary-style: 1px solid #dadce0;
  --theme-color: rgb(29, 155, 240);
}
.form {
  width: 600px;
  height: 600px;
  background-color: #fff;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  /* padding: 100px; */
  position: relative;
  border-radius: 15px;
}
.login {
  width: 300px;
  /* padding: 0 80px; */
  display: flex;
  flex-direction: column;
  align-items: center;
}
.login > div,
button {
  width: 100%;
  height: 42px;
}

.login .title {
  font-size: 30px;
  font-weight: 700;
  margin-bottom: 16px;
}
.top {
  width: 100%;
  display: flex;
  justify-content: center;
  position: absolute;
  top: 14px;
  left: 0;
  right: 0;
}
.top .back {
  position: absolute;
  left: 4px;
  padding: 10px;
  border-radius: 50%;
  transition: background 0.2s ease-in-out;
}
.back:hover {
  background: rgba(15, 20, 25, 0.08);
}
.back .icon {
  width: 20px;
  height: 20px;
}
.logo .icon {
  width: 30px;
  height: 30px;
}
.login .icon {
  width: 20px;
  height: 20px;
}
button {
  display: flex;
  justify-content: center;
  align-items: center;
  border: var(--boundary-style);
  border-radius: 20px;
  font-weight: 500;
  color: #3c4043;
  margin-top: 16px;
  transition: background 0.2s ease-in-out;
}
button:hover {
  background: rgba(15, 20, 25, 0.1);
}
button .icon {
  margin-right: 7px;
}
.or {
  display: flex;
  align-items: center;
}
.or span {
  margin: 0 6px;
}
.or div {
  flex: 1;
  height: 1px;
  background: rgba(0, 0, 0, 0.05);
}
input {
  caret-color: #000;
}
.login input {
  outline: none;
  width: 100%;
  height: 50px;
  border: var(--boundary-style);
  padding-left: 5px;
  border-radius: 5px;
}
.login input:focus {
  padding-left: 4px;
  border-width: 2px;
  border-color: var(--theme-color);
}
.next {
  background: #000;
  color: #fff;
  font-weight: 600;
  margin-top: 34px;
}
.next:hover {
  background: rgba(0, 0, 0, 0.8);
}
.next,
.forget_password {
  font-weight: 700;
}
.forget_password {
  color: #000;
}
.login_ap {
  width: 70%;
  height: 70%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
.login_ap h1 {
  font-size: 34px;
  font-weight: 700;
}
.info_input > div {
  height: 50px;
  margin-top: 20px;
  padding: 6px;
  display: flex;
  flex-direction: column;
  justify-content: space-around;
  border-radius: 7px;
}

.info_input .account {
  background-color: rgb(239, 243, 244);
  color: rgba(83, 100, 113, 1);
}
.info_input .account div {
  font-size: 11px;
}
.info_input .password {
  font-size: 14px;
  border: var(--boundary-style);
}
.password input {
  outline: none;
  width: 100%;
  height: 100%;
  border: none;
}
.info_input .password:focus-within {
  border-width: 2px;
  padding-left: 5px;
  border-color: var(--theme-color);
}
.info_input .forget_password {
  margin-top: 5px;
  font-size: 12px;
  padding-top: 0;
  color: var(--theme-color);
  justify-content: start;
}
.login_input button {
  height: 50px;
  border-radius: 25px;
  background: rgb(15, 20, 25);
  color: #fff;
  margin-bottom: 20px;
}
.login_input button:hover {
  background: rgba(15, 20, 25, 0.9);
}
.login_input button:disabled {
  background: rgba(15, 20, 25, 0.5);
}
.login_input .tip {
  font-size: 14px;
  color: rgba(83, 100, 113, 1);
}
.login_input .tip span {
  font-size: 14px;
  color: var(--theme-color);
}
</style>
