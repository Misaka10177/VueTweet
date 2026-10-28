<template>
  <div id="user">
    <div class="page_main">
      <main>
        <div class="top">
          <div class="back" @click="goBack">
            <svg viewBox="0 0 24 24">
              <g><path :d="icon_back"></path></g>
            </svg>
          </div>
          <div class="header">
            <div class="name">{{ profile?.name || '个人资料' }}</div>
            <div class="post-count">{{ profile?.tweetCount ?? 0 }} 帖子</div>
          </div>
        </div>

        <div v-if="loading_profile" class="state">加载中…</div>
        <div v-else-if="profile_error" class="state error" role="alert">{{ profile_error }}</div>
        <template v-else-if="profile">
          <div class="profile">
            <div class="cover"></div>
            <div class="profile-info">
              <div class="avatar-row">
                <div class="avatar">
                  <img :src="avatarUrl" :alt="profile.name" />
                </div>
                <div class="action">
                  <button v-if="isSelf" class="edit-btn">编辑个人资料</button>
                  <FollowButton
                    v-else
                    :user-id="profile.id"
                    :following="profile.viewerFollowing"
                    @change="onFollowChange"
                  />
                </div>
              </div>
              <div class="display-name-row">
                <div class="display-name">{{ profile.name }}</div>
                <FollowRelationBadge
                  :following="profile.viewerFollowing"
                  :follows-you="profile.followsYou"
                  :self="profile.self"
                />
              </div>
              <div class="handle">@{{ profile.id }}</div>
              <div class="follow-info">
                <button type="button" class="follow-stat" @click="openList('following')">
                  <strong>{{ profile.followingCount }}</strong> 正在关注
                </button>
                <button type="button" class="follow-stat" @click="openList('followers')">
                  <strong>{{ profile.followerCount }}</strong> 关注者
                </button>
              </div>
            </div>
          </div>
          <div class="tab">
            <TabControl
              :key="profile.id + tab_list.join('|')"
              :tab_list="tab_list"
              @tab-click="tab_click"
            ></TabControl>
          </div>
          <div class="tweet_list">
            <div v-if="loading" class="state">加载中…</div>
            <div v-else-if="tab_error" class="state error" role="alert">{{ tab_error }}</div>
            <div v-else-if="!tweets.length" class="state">还没有帖子</div>
            <TweetShow v-for="tweet in tweets" :key="tweet.id" :tweet="tweet"></TweetShow>
          </div>
        </template>
      </main>
    </div>
    <div class="side_bar">
      <div>
        <SearchBar></SearchBar>
        <SubscribePremium></SubscribePremium>
        <RecommendFollowing></RecommendFollowing>
        <NavigationBar></NavigationBar>
      </div>
    </div>
    <FollowListModal
      :open="list_open"
      :user-id="profile?.id ?? ''"
      :mode="list_mode"
      @close="list_open = false"
      @follow-change="onListFollowChange"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import type { FollowState, TweetItem, TweetPage, UserProfile } from '@/types'
import { getLikes, getMe, getProfile, getUserReplies, getUserTweets } from '@/request/api'
import { useAuthStore } from '@/stores/auth'
import { useTweetStore } from '@/stores/store'
import { mediaUrl } from '@/utils/media'
import TabControl from './components/TabControl.vue'
import TweetShow from './components/TweetShow.vue'
import FollowButton from './components/FollowButton.vue'
import FollowRelationBadge from './components/FollowRelationBadge.vue'
import FollowListModal from './components/FollowListModal.vue'
import SearchBar from './components/SideBar/SideBarComponents/SearchBar.vue'
import SubscribePremium from './components/SideBar/SideBarComponents/SubscribePremium.vue'
import RecommendFollowing from './components/SideBar/SideBarComponents/RecommendFollowing.vue'
import NavigationBar from './components/SideBar/SideBarComponents/NavigationBar.vue'

const base = import.meta.env.BASE_URL
const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const tweetStore = useTweetStore()
const icon_back = ref(
  'M7.414 13l5.043 5.04-1.414 1.42L3.586 12l7.457-7.46 1.414 1.42L7.414 11H21v2H7.414z',
)

const profile = ref<UserProfile | null>(null)
const loading_profile = ref(false)
const profile_error = ref('')
const tweets = ref<TweetItem[]>([])
const loading = ref(false)
const tab_error = ref('')
const list_open = ref(false)
const list_mode = ref<'followers' | 'following'>('followers')
let profile_seq = 0
let tab_seq = 0

const isSelf = computed(() => profile.value?.self ?? false)
const tab_list = computed(() =>
  isSelf.value ? ['帖子', '回复', '媒体', '喜欢'] : ['帖子', '回复', '媒体'],
)
const avatarUrl = computed(
  () => mediaUrl(profile.value?.avatarUrl) || `${base}images/img.png`,
)

async function resolveUserId() {
  const param = route.params.id
  if (typeof param === 'string' && param) return param
  if (auth.user?.id) return auth.user.id
  return (await getMe()).id
}

async function loadProfile() {
  const seq = ++profile_seq
  loading_profile.value = true
  profile_error.value = ''
  tweets.value = []
  try {
    const userId = await resolveUserId()
    const data = await getProfile(userId)
    if (seq !== profile_seq) return
    profile.value = data
    await loadTab('帖子')
  } catch {
    if (seq !== profile_seq) return
    profile.value = null
    profile_error.value = '用户资料加载失败，请稍后重试'
  } finally {
    if (seq === profile_seq) loading_profile.value = false
  }
}

async function loadTab(tab: string) {
  if (!profile.value) return
  const targetId = profile.value.id
  const seq = ++tab_seq
  loading.value = true
  tab_error.value = ''
  try {
    let page: TweetPage
    if (tab === '喜欢' && isSelf.value) {
      page = await getLikes()
    } else if (tab === '回复') {
      page = await getUserReplies(targetId)
    } else if (tab === '媒体') {
      page = await getUserTweets(targetId, null, 50)
      page = { ...page, items: page.items.filter((tweet) => tweet.media.length > 0) }
    } else {
      page = await getUserTweets(targetId)
    }
    if (seq !== tab_seq || profile.value?.id !== targetId) return
    tweets.value = page.items
    tweets.value.forEach((item) => tweetStore.putTweet(item))
  } catch {
    if (seq !== tab_seq) return
    tweets.value = []
    tab_error.value = '帖子加载失败，请稍后重试'
  } finally {
    if (seq === tab_seq) loading.value = false
  }
}

function tab_click(tab: string) {
  void loadTab(tab)
}

function openList(mode: 'followers' | 'following') {
  list_mode.value = mode
  list_open.value = true
}

function onFollowChange(state: FollowState) {
  if (!profile.value || profile.value.id !== state.targetId) return
  profile.value = {
    ...profile.value,
    viewerFollowing: state.following,
    followsYou: state.followsYou,
    followerCount: state.followerCount,
  }
}

function onListFollowChange(state: FollowState) {
  if (!profile.value) return
  const followingCount = isSelf.value
    ? Math.max(0, profile.value.followingCount + (state.following ? 1 : -1))
    : profile.value.followingCount
  profile.value = {
    ...profile.value,
    followingCount,
    ...(state.targetId === profile.value.id
      ? {
          viewerFollowing: state.following,
          followsYou: state.followsYou,
          followerCount: state.followerCount,
        }
      : {}),
  }
}

function goBack() {
  if (window.history.length > 1) {
    router.back()
  } else {
    void router.push('/')
  }
}

onMounted(loadProfile)
watch(
  () => route.params.id,
  () => {
    void loadProfile()
  },
)
</script>
<style scoped>
#user {
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
  border-right: var(--boundary-style);
}
.top {
  width: 100%;
  height: 53px;
  padding: 0 16px;
  display: flex;
  align-items: center;
}
.back {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  cursor: pointer;
  transition: background 0.2s;
}
.back:hover {
  background: rgba(15, 20, 25, 0.08);
}
.back svg {
  width: 20px;
  height: 20px;
}
.header {
  display: flex;
  flex-direction: column;
}
.header .name {
  font-size: 20px;
  font-weight: 700;
}
.header .post-count {
  font-size: 13px;
  color: var(--grey-color);
}
.profile {
  width: 100%;
}
.cover {
  width: 100%;
  height: 200px;
  background: linear-gradient(135deg, #1d9bf0 0%, #1a8cd8 50%, #0f6faf 100%);
}
.profile-info {
  position: relative;
  padding: 12px 16px;
}
.avatar-row {
  display: flex;
  justify-content: flex-end;
  height: 65px;
  margin-bottom: 12px;
}
.avatar {
  position: absolute;
  top: -65px;
  left: 16px;
  width: 130px;
  height: 130px;
  border-radius: 50%;
  border: 4px solid #fff;
  overflow: hidden;
  background: #fff;
}
.avatar img {
  width: 130px;
  height: 130px;
  object-fit: cover;
}
.action {
  align-self: flex-end;
}
.edit-btn {
  height: 34px;
  padding: 0 16px;
  border-radius: 17px;
  border: var(--boundary-style);
  font-size: 14px;
  font-weight: 600;
  color: var(--black-color);
  background: #fff;
  cursor: pointer;
  transition: background 0.2s;
  border-color: rgba(0, 0, 0, 0.1);
}
.edit-btn:hover {
  background: rgba(15, 20, 25, 0.06);
}
.display-name-row {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 4px;
}
.display-name {
  font-size: 20px;
  font-weight: 700;
  min-width: 0;
  overflow-wrap: anywhere;
}
.handle {
  font-size: 15px;
  color: var(--grey-color);
  margin-bottom: 12px;
}
.follow-info {
  display: flex;
  gap: 20px;
  font-size: 14px;
  color: var(--grey-color);
  margin-bottom: 12px;
}
.follow-stat {
  border: none;
  background: none;
  padding: 0;
  color: var(--grey-color);
  font-size: 14px;
  cursor: pointer;
}
.follow-stat strong {
  color: var(--black-color);
}
.follow-stat:hover {
  text-decoration: underline;
}
.tab {
  height: 53px;
}
.tweet_list {
  min-height: 24px;
}
.state {
  padding: 24px 16px;
  text-align: center;
  color: var(--grey-color);
  font-size: 14px;
  border-bottom: var(--boundary-style);
}
.state.error {
  color: #f4212e;
}
.side_bar {
  margin-left: 30px;
  margin-top: 10px;
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
