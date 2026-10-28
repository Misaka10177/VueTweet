<template>
  <button
    v-if="!self"
    type="button"
    class="follow-btn"
    :class="{ 'is-following': following }"
    :disabled="pending"
    :title="following ? '取消关注' : '关注'"
    @click.stop="toggle"
  >
    <template v-if="pending">…</template>
    <template v-else-if="following">
      <span class="label-idle">已关注</span>
      <span class="label-hover">取消关注</span>
    </template>
    <template v-else>关注</template>
  </button>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import type { FollowState } from '@/types'
import { followUser, unfollowUser } from '@/request/api'
import { useFollowStore } from '@/stores/follow'

const props = withDefaults(
  defineProps<{ userId: string; following: boolean; self?: boolean }>(),
  { self: false },
)
const emit = defineEmits<{ change: [state: FollowState] }>()

const followStore = useFollowStore()
const pending = ref(false)
const following = computed(() => followStore.isFollowing(props.userId, props.following))

async function toggle() {
  if (pending.value) return
  pending.value = true
  try {
    const state = following.value
      ? await unfollowUser(props.userId)
      : await followUser(props.userId)
    followStore.setFollowing(props.userId, state.following)
    emit('change', state)
  } catch {
    // Keep the server's previous state when the request fails.
  } finally {
    pending.value = false
  }
}
</script>

<style scoped>
.follow-btn {
  min-width: 68px;
  height: 32px;
  padding: 0 14px;
  border-radius: 16px;
  border: 1px solid #000;
  background: #000;
  color: #fff;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  transition: background 0.2s, color 0.2s, opacity 0.2s;
  white-space: nowrap;
}
.follow-btn:hover {
  background: #272c30;
}
.follow-btn.is-following {
  background: #fff;
  color: #0f1419;
  border-color: rgba(0, 0, 0, 0.25);
}
.follow-btn.is-following:hover {
  background: rgba(244, 33, 46, 0.1);
  color: #f4212e;
  border-color: rgba(244, 33, 46, 0.4);
}
.label-hover {
  display: none;
}
.follow-btn.is-following:hover .label-idle {
  display: none;
}
.follow-btn.is-following:hover .label-hover {
  display: inline;
}
.follow-btn:disabled {
  opacity: 0.55;
  cursor: default;
}
</style>
