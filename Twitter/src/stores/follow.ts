import { reactive } from 'vue'
import { defineStore } from 'pinia'

/**
 * Keeps follow buttons for the same user in sync across the page (timeline
 * sidebar, profile, follower lists). The value is always written from an API
 * response, so it is newer than the profile/timeline snapshot that rendered the
 * button initially.
 */
export const useFollowStore = defineStore('follow', () => {
  const overrides = reactive(new Map<string, boolean>())

  function has(userId: string) {
    return overrides.has(userId)
  }

  function isFollowing(userId: string, fallback: boolean) {
    return overrides.has(userId) ? Boolean(overrides.get(userId)) : fallback
  }

  function setFollowing(userId: string, following: boolean) {
    overrides.set(userId, following)
  }

  function reset() {
    overrides.clear()
  }

  return { overrides, has, isFollowing, setFollowing, reset }
})
