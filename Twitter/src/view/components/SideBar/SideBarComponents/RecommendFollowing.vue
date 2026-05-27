<template>
  <div id="recommend-following">
    <div class="user-list">
      <ul>
        <li class="user" v-for="user in user_list" :key="user.id">
          <div class="info">
            <div class="image">
              <img :src="user.picture" :alt="user.name" />
            </div>
            <div class="name-id">
              <div class="name">{{ user.name }}</div>
              <div class="id">@{{ user.id }}</div>
            </div>
          </div>
          <div class="button">
            <button>关注</button>
          </div>
        </li>
        <li class="more">显示更多</li>
      </ul>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const user_list = ref([
  {
    id: 'Misaka',
    name: 'Misaka Mikoto',
    picture: '/images/img.png',
  },
  {
    id: 'oO0Eve0Oo',
    name: 'Eve',
    picture: '/images/img.png',
  },
])
</script>
<style scoped>
#recommend-following {
}
li {
  width: 100%;
  padding: var(--box-padding);
}
li:hover {
  background-color: rgba(0, 0, 0, 0.03);
}
li:last-child:hover {
  background: none;
}
.user {
  display: flex;
  justify-content: space-between;
}
.info {
  display: flex;
  align-items: center;
}
.image {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
}
img {
  width: 100%;
  height: 100%;
}
.name-id {
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: space-around;
  margin-left: 10px;
}
.name {
  font-weight: 600;
}
.id {
  color: var(--grey-color);
  font-size: 14px;
}
button {
  width: 62px;
  height: 32px;
  border-radius: 16px;
  font-weight: 500;
  color: #fff;
  background: #000;
}
.more {
  color: var(--theme-color);
}
</style>
