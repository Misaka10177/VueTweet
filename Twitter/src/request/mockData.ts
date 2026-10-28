// Mock data for GitHub Pages static deployment
import type {
  ActionState,
  AuthResponse,
  FollowPage,
  FollowState,
  FollowUser,
  Me,
  SearchOverview,
  Trend,
  TweetItem,
  TweetMedia,
  TweetPage,
  TweetStats,
  UserProfile,
} from '@/types'

const avatar = '/images/img.png'
const defaultStats: TweetStats = {
  replies: 0,
  reposts: 0,
  likes: 0,
  bookmarks: 0,
  views: 0,
}

function media(urls: string[]): TweetMedia[] {
  return urls.map((url) => ({ url, width: null, height: null, alt: null }))
}

function makeTweet(
  id: string,
  authorId: string,
  authorName: string,
  text: string,
  images: string[],
  createdAt: string,
  stats: Partial<TweetStats> = {},
  extra: Partial<TweetItem> = {},
): TweetItem {
  return {
    id,
    author: { id: authorId, name: authorName, avatarUrl: avatar },
    text,
    media: media(images),
    createdAt,
    editedAt: null,
    replyToId: null,
    replyTo: null,
    stats: { ...defaultStats, ...stats },
    viewer: { liked: false, reposted: false, bookmarked: false },
    ...extra,
  }
}

const tweet1 = makeTweet(
  '1',
  'Misaka',
  'Misaka Mikoto',
  '楽しかった！！！！\nアンヴィル…………',
  ['/images/92162034_p3.png'],
  '2026-06-04T02:00:00Z',
  { replies: 2, reposts: 333, likes: 444, bookmarks: 10, views: 999 },
)
const tweet2 = makeTweet(
  '2',
  'oO0Eve0Oo',
  'Eve',
  '今日もいい天気ですね〜',
  ['/images/93447255_p0.png'],
  '2026-06-05T10:00:00Z',
  { replies: 55, reposts: 120, likes: 310, bookmarks: 20, views: 500 },
)
const tweet3 = makeTweet(
  '3',
  'akakura1341',
  '赤倉🧸',
  '新しいプロジェクトを始めました！\nVue.js + TypeScript で Twitter クローンを作っています。',
  ['/images/92162034_p3.png', '/images/96919363_p0.jpg', '/images/93447255_p0.png'],
  '2026-06-03T15:00:00Z',
  { replies: 88, reposts: 200, likes: 500, bookmarks: 50, views: 1200 },
)
const reply1 = makeTweet(
  '4',
  'Misaka',
  'Misaka Mikoto',
  '这条推文太棒了！👍',
  [],
  '2026-06-05T16:27:29Z',
  { replies: 0, reposts: 0, likes: 0, bookmarks: 0, views: 0 },
  {
    replyToId: '1',
    replyTo: { id: '1', author: tweet1.author, text: tweet1.text, media: tweet1.media },
  },
)
const reply2 = makeTweet(
  '5',
  'oO0Eve0Oo',
  'Eve',
  'hhh',
  [],
  '2026-06-05T10:17:17Z',
  { replies: 0, reposts: 0, likes: 0, bookmarks: 0, views: 0 },
  {
    replyToId: '1',
    replyTo: { id: '1', author: tweet1.author, text: tweet1.text, media: tweet1.media },
  },
)
const tweet4 = makeTweet(
  '8',
  'Misaka',
  'Misaka Mikoto',
  '多图片测试 - 2张',
  ['/images/92162034_p3.png', '/images/96919363_p0.jpg'],
  '2026-06-14T01:00:00Z',
  { likes: 12, views: 100 },
)
const tweet5 = makeTweet(
  '9',
  'Misaka',
  'Misaka Mikoto',
  '多图片测试 - 3张',
  ['/images/92162034_p3.png', '/images/96919363_p0.jpg', '/images/93447255_p0.png'],
  '2026-06-14T01:00:00Z',
  { likes: 8, views: 80 },
)
const tweet6 = makeTweet(
  '10',
  'Misaka',
  'Misaka Mikoto',
  '多图片测试 - 4张',
  [
    '/images/92162034_p3.png',
    '/images/96919363_p0.jpg',
    '/images/93447255_p0.png',
    '/images/64495434_p0.jpg',
  ],
  '2026-06-14T01:00:00Z',
  { likes: 5, views: 60 },
)

const feed: TweetItem[] = [tweet1, tweet2, tweet3, tweet4, tweet5, tweet6]
const feedPage: TweetPage = { items: feed, nextCursor: null, hasMore: false }
const repliesPage: TweetPage = { items: [reply1, reply2], nextCursor: null, hasMore: false }
const emptyPage: TweetPage = { items: [], nextCursor: null, hasMore: false }

function action(
  id: string,
  patch: Partial<TweetItem['stats']>,
  key: 'liked' | 'reposted' | 'bookmarked',
): ActionState {
  const source = feed.find((tweet) => tweet.id === id) ?? tweet1
  const viewer = {
    ...(source.viewer ?? { liked: false, reposted: false, bookmarked: false }),
    [key]: true,
  }
  return {
    tweetId: id,
    stats: { ...source.stats, ...patch },
    viewer,
  }
}

const mockLogin: AuthResponse = {
  accessToken: 'mock-token-gh-pages',
  tokenType: 'Bearer',
  expiresIn: 86400,
  user: { id: 'Misaka10177', name: 'Misaka 10177', profilePhoto: '/images/img.png' },
}

const mockMe: Me = { id: 'Misaka10177', name: 'Misaka 10177', avatarUrl: '/images/img.png' }

const followUsers: FollowUser[] = [
  {
    id: 'Misaka',
    name: 'Misaka Mikoto',
    avatarUrl: avatar,
    following: false,
    followsYou: true,
    self: false,
  },
  {
    id: 'oO0Eve0Oo',
    name: 'Eve',
    avatarUrl: avatar,
    following: true,
    followsYou: true,
    self: false,
  },
  {
    id: 'akakura1341',
    name: '赤倉🧸',
    avatarUrl: avatar,
    following: false,
    followsYou: false,
    self: false,
  },
  {
    id: 'claude',
    name: 'Claude',
    avatarUrl: avatar,
    following: false,
    followsYou: true,
    self: false,
  },
]
const followPage: FollowPage = { items: followUsers, nextCursor: null, hasMore: false }
const emptyFollowPage: FollowPage = { items: [], nextCursor: null, hasMore: false }

function profile(
  id: string,
  name: string,
  self: boolean,
  followingCount: number,
  followerCount: number,
  tweetCount: number,
  viewerFollowing = false,
  followsYou = false,
): UserProfile {
  return {
    id,
    name,
    avatarUrl: avatar,
    followingCount,
    followerCount,
    tweetCount,
    viewerFollowing,
    followsYou,
    self,
  }
}

const mockProfile = profile('Misaka10177', 'Misaka 10177', true, 3, 12, 6)
const otherProfiles: Record<string, UserProfile> = {
  Misaka: profile('Misaka', 'Misaka Mikoto', false, 5, 88, 1, false, true),
  oO0Eve0Oo: profile('oO0Eve0Oo', 'Eve', false, 2, 41, 1, true, true),
  akakura1341: profile('akakura1341', '赤倉🧸', false, 7, 120, 1),
  claude: profile('claude', 'Claude', false, 1, 9, 0, false, true),
}

function followState(userId: string, following: boolean): FollowState {
  const base = otherProfiles[userId]?.followerCount ?? 0
  return {
    targetId: userId,
    following,
    followerCount: base + (following ? 1 : 0),
    followsYou: otherProfiles[userId]?.followsYou ?? false,
  }
}

const trends: Trend[] = [
  { category: '中国香港特别行政区', topic: 'LING NIVEA BRAND FRIEND' },
  { category: '台湾', topic: '#skywongravee' },
  { category: '日本', topic: '#アニメ' },
  { category: '技术', topic: 'Vue.js' },
  { category: '全球', topic: '#OpenSource' },
  { category: '科技', topic: '#TypeScript' },
  { category: '娱乐', topic: '#新番推荐' },
  { category: '游戏', topic: '#原神' },
  { category: '音乐', topic: '#JPop' },
  { category: '体育', topic: '#NBA' },
]

const mockSearchOverview: SearchOverview = {
  query: 'Vue',
  users: followUsers.slice(0, 3),
  tweets: feedPage,
  topics: trends.slice(0, 5),
}

export const mockResponses = {
  'GET /tweets': feedPage,
  'POST /tweets': tweet1,
  'GET /tweets/1': tweet1,
  'GET /tweets/2': tweet2,
  'GET /tweets/3': tweet3,
  'GET /tweets/4': reply1,
  'GET /tweets/5': reply2,
  'GET /tweets/8': tweet4,
  'GET /tweets/9': tweet5,
  'GET /tweets/10': tweet6,
  'GET /tweets/1/replies': repliesPage,
  'GET /tweets/2/replies': emptyPage,
  'GET /tweets/3/replies': emptyPage,
  'PUT /tweets/1/like': action('1', { likes: 445 }, 'liked'),
  'DELETE /tweets/1/like': action('1', { likes: 444 }, 'liked'),
  'PUT /tweets/1/repost': action('1', { reposts: 334 }, 'reposted'),
  'DELETE /tweets/1/repost': action('1', { reposts: 333 }, 'reposted'),
  'PUT /tweets/1/bookmark': action('1', { bookmarks: 11 }, 'bookmarked'),
  'DELETE /tweets/1/bookmark': action('1', { bookmarks: 10 }, 'bookmarked'),
  'GET /trends': trends,
  'GET /search': mockSearchOverview,
  'GET /search/users': followUsers,
  'GET /search/tweets': feedPage,
  'GET /search/topics': trends,
  'GET /me': mockMe,
  'GET /me/bookmarks': { items: [tweet1], nextCursor: null, hasMore: false },
  'GET /me/likes': { items: [tweet2], nextCursor: null, hasMore: false },
  'GET /users/Misaka10177/tweets': { items: [tweet1], nextCursor: null, hasMore: false },
  'GET /users/Misaka10177/replies': { items: [reply1], nextCursor: null, hasMore: false },
  'GET /users/Misaka10177': mockProfile,
  'GET /users/Misaka10177/followers': followPage,
  'GET /users/Misaka10177/following': followPage,
  'GET /users/suggestions': followUsers,
  'GET /users/Misaka': otherProfiles.Misaka,
  'GET /users/Misaka/tweets': { items: [tweet1], nextCursor: null, hasMore: false },
  'GET /users/Misaka/replies': { items: [reply1], nextCursor: null, hasMore: false },
  'GET /users/Misaka/followers': followPage,
  'GET /users/Misaka/following': followPage,
  'GET /users/oO0Eve0Oo': otherProfiles.oO0Eve0Oo,
  'GET /users/oO0Eve0Oo/tweets': { items: [tweet2], nextCursor: null, hasMore: false },
  'GET /users/oO0Eve0Oo/replies': { items: [reply2], nextCursor: null, hasMore: false },
  'GET /users/oO0Eve0Oo/followers': followPage,
  'GET /users/oO0Eve0Oo/following': followPage,
  'GET /users/akakura1341': otherProfiles.akakura1341,
  'GET /users/akakura1341/tweets': { items: [tweet3], nextCursor: null, hasMore: false },
  'GET /users/akakura1341/replies': emptyPage,
  'GET /users/akakura1341/followers': emptyFollowPage,
  'GET /users/akakura1341/following': emptyFollowPage,
  'GET /users/claude': otherProfiles.claude,
  'GET /users/claude/tweets': emptyPage,
  'GET /users/claude/replies': emptyPage,
  'GET /users/claude/followers': emptyFollowPage,
  'GET /users/claude/following': emptyFollowPage,
  'PUT /users/Misaka/follow': followState('Misaka', true),
  'DELETE /users/Misaka/follow': followState('Misaka', false),
  'PUT /users/oO0Eve0Oo/follow': followState('oO0Eve0Oo', true),
  'DELETE /users/oO0Eve0Oo/follow': followState('oO0Eve0Oo', false),
  'PUT /users/akakura1341/follow': followState('akakura1341', true),
  'DELETE /users/akakura1341/follow': followState('akakura1341', false),
  'PUT /users/claude/follow': followState('claude', true),
  'DELETE /users/claude/follow': followState('claude', false),
  'POST /checkUser': { status: 'success' },
  'POST /auth/login': mockLogin,
  'POST /auth/refresh': mockLogin,
  'POST /auth/logout': { status: 'success' },
  'GET /auth/sessions': [],
} satisfies Record<string, unknown>
