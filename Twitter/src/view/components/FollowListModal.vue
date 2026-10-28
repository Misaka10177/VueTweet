<template>
  <Teleport to="body">
    <div v-if="open" class="follow-modal-backdrop" @click.self="close">
      <section class="follow-modal" role="dialog" aria-modal="true" :aria-label="title">
        <header>
          <button type="button" class="close" aria-label="关闭" @click="close">×</button>
          <h2>{{ title }}</h2>
        </header>
        <div class="list">
          <div v-if="loading && !users.length" class="hint">加载中…</div>
          <div v-else-if="error" class="hint error" role="alert">{{ error }}</div>
          <div v-else-if="!users.length" class="hint">还没有用户</div>
          <div v-for="user in users" :key="user.id" class="row" @click="goUser(user.id)">
            <img :src="mediaUrl(user.avatarUrl) || fallbackAvatar" :alt="user.name" />
            <div class="meta">
              <div class="name-row">
                <span class="name">{{ user.name }}</span>
                <FollowRelationBadge
                  :following="user.following"
                  :follows-you="user.followsYou"
                  :self="user.self"
                />
              </div>
              <div class="handle">@{{ user.id }}</div>
            </div>
            <FollowButton
              :user-id="user.id"
              :following="user.following"
              :self="user.self"
              @change="onFollowChange(user, $event)"
            />
          </div>
          <button
            v-if="hasMore"
            type="button"
            class="load-more"
            :disabled="loading"
            @click="loadMore"
          >
            {{ loading ? '加载中…' : '显示更多' }}
          </button>
        </div>
      </section>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import { computed, onUnmounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import type { FollowPage, FollowState, FollowUser } from '@/types'
import { getFollowers, getFollowing } from '@/request/api'
import { mediaUrl } from '@/utils/media'
import FollowButton from '@/view/components/FollowButton.vue'
import FollowRelationBadge from '@/view/components/FollowRelationBadge.vue'

const props = defineProps<{
  open: boolean
  userId: string
  mode: 'followers' | 'following'
}>()
const emit = defineEmits<{
  close: []
  'follow-change': [state: FollowState]
}>()

const router = useRouter()
const fallbackAvatar = `${import.meta.env.BASE_URL}images/img.png`
const users = ref<FollowUser[]>([])
const cursor = ref<string | null>(null)
const hasMore = ref(false)
const loading = ref(false)
const error = ref('')
let previousOverflow = ''

const title = computed(() => (props.mode === 'followers' ? '关注者' : '正在关注'))

async function load(reset: boolean) {
  if (loading.value) return
  if (reset) {
    users.value = []
    cursor.value = null
    hasMore.value = false
    error.value = ''
  }
  loading.value = true
  try {
    const fetchPage = props.mode === 'followers' ? getFollowers : getFollowing
    const page: FollowPage = await fetchPage(props.userId, cursor.value)
    const known = new Set(users.value.map((user) => user.id))
    users.value = [...users.value, ...page.items.filter((user) => !known.has(user.id))]
    cursor.value = page.nextCursor
    hasMore.value = page.hasMore
  } catch {
    error.value = '列表加载失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

function loadMore() {
  void load(false)
}

function close() {
  emit('close')
}

function goUser(userId: string) {
  close()
  void router.push(`/user/${encodeURIComponent(userId)}`)
}

function onFollowChange(user: FollowUser, state: FollowState) {
  user.following = state.following
  user.followsYou = state.followsYou
  emit('follow-change', state)
}

function onKeydown(event: KeyboardEvent) {
  if (event.key === 'Escape') close()
}

watch(
  [() => props.open, () => props.userId, () => props.mode],
  ([open], wasOpen) => {
    if (open) {
      if (wasOpen?.[0] !== true) {
        previousOverflow = document.body.style.overflow
        document.body.style.overflow = 'hidden'
        document.addEventListener('keydown', onKeydown)
      }
      void load(true)
    } else if (wasOpen?.[0] === true) {
      document.body.style.overflow = previousOverflow
      document.removeEventListener('keydown', onKeydown)
    }
  },
)

onUnmounted(() => {
  document.body.style.overflow = previousOverflow
  document.removeEventListener('keydown', onKeydown)
})
</script>

<style scoped>
.follow-modal-backdrop {
  position: fixed;
  inset: 0;
  z-index: 1000;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding: 8vh 16px;
}
.follow-modal {
  width: 100%;
  max-width: 440px;
  max-height: 74vh;
  display: flex;
  flex-direction: column;
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.2);
}
header {
  position: relative;
  height: 53px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-bottom: var(--boundary-style);
  flex: none;
}
h2 {
  font-size: 18px;
  font-weight: 700;
}
.close {
  position: absolute;
  left: 8px;
  width: 36px;
  height: 36px;
  border: none;
  border-radius: 50%;
  background: transparent;
  font-size: 24px;
  line-height: 1;
  cursor: pointer;
}
.close:hover {
  background: rgba(15, 20, 25, 0.08);
}
.list {
  overflow-y: auto;
}
.row {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  border-bottom: var(--boundary-style);
  cursor: pointer;
}
.row:hover {
  background: rgba(0, 0, 0, 0.03);
}
.row img {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  object-fit: cover;
  flex: none;
}
.meta {
  flex: 1;
  min-width: 0;
}
.name-row {
  display: flex;
  align-items: center;
  gap: 4px;
  min-width: 0;
}
.name {
  font-weight: 700;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.name:hover {
  text-decoration: underline;
}
.handle {
  color: var(--grey-color);
  font-size: 14px;
}
.hint {
  padding: 24px 16px;
  text-align: center;
  color: var(--grey-color);
  font-size: 14px;
}
.hint.error {
  color: #f4212e;
}
.load-more {
  display: block;
  width: 100%;
  padding: 14px;
  border: none;
  background: #fff;
  color: var(--theme-color);
  font-size: 14px;
  cursor: pointer;
}
.load-more:hover {
  background: rgba(0, 0, 0, 0.03);
}
.load-more:disabled {
  color: var(--grey-color);
  cursor: default;
}
</style>
