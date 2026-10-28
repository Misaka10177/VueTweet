<template>
  <div id="recommend-following">
    <div class="user-list">
      <ul v-if="user_list.length">
        <li class="user" v-for="user in user_list" :key="user.id" @click="goUser(user.id)">
          <div class="info">
            <div class="image">
              <img :src="mediaUrl(user.avatarUrl) || fallbackAvatar" :alt="user.name" />
            </div>
            <div class="name-id">
              <div class="name-row">
                <span class="name">{{ user.name }}</span>
                <FollowRelationBadge
                  :following="user.following"
                  :follows-you="user.followsYou"
                  :self="user.self"
                />
              </div>
              <div class="id">@{{ user.id }}</div>
            </div>
          </div>
          <div class="button">
            <FollowButton
              :user-id="user.id"
              :following="user.following"
              :self="user.self"
              @change="onFollowChange(user, $event)"
            />
          </div>
        </li>
      </ul>
      <div v-else-if="loading" class="hint">加载中…</div>
      <div v-else class="hint">暂时没有推荐用户</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import type { FollowState, FollowUser } from '@/types'
import { getFollowSuggestions } from '@/request/api'
import { mediaUrl } from '@/utils/media'
import FollowButton from '@/view/components/FollowButton.vue'
import FollowRelationBadge from '@/view/components/FollowRelationBadge.vue'

const router = useRouter()
const fallbackAvatar = `${import.meta.env.BASE_URL}images/img.png`
const user_list = ref<FollowUser[]>([])
const loading = ref(false)

async function load() {
  loading.value = true
  try {
    user_list.value = await getFollowSuggestions()
  } catch {
    user_list.value = []
  } finally {
    loading.value = false
  }
}

function goUser(userId: string) {
  void router.push(`/user/${encodeURIComponent(userId)}`)
}

function onFollowChange(user: FollowUser, state: FollowState) {
  user.following = state.following
  user.followsYou = state.followsYou
}

onMounted(load)
</script>

<style scoped>
li {
  width: 100%;
  padding: var(--box-padding);
  cursor: pointer;
}
li:hover {
  background-color: rgba(0, 0, 0, 0.03);
}
.user {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
}
.info {
  display: flex;
  align-items: center;
  min-width: 0;
}
.image {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  flex: none;
}
img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.name-id {
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: space-around;
  margin-left: 10px;
  min-width: 0;
}
.name-row {
  display: flex;
  align-items: center;
  gap: 4px;
  min-width: 0;
}
.name {
  font-weight: 600;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.name:hover {
  text-decoration: underline;
}
.id {
  color: var(--grey-color);
  font-size: 14px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.button {
  flex: none;
}
.hint {
  padding: 16px;
  color: var(--grey-color);
  font-size: 14px;
}
</style>
