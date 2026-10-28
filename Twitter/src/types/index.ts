export type AuthUser = {
  id: string
  name: string
  profilePhoto: string
}

export type AuthResponse = {
  accessToken: string
  tokenType: string
  expiresIn: number
  user: AuthUser
}

export type AuthSession = {
  id: string
  deviceName: string | null
  userAgent: string | null
  ipAddress: string | null
  createdAt: string
  lastUsedAt: string
  current: boolean
}

export type Trend = {
  category: string
  topic: string
}

export type TweetAuthor = {
  id: string
  name: string
  avatarUrl: string | null
}

export type TweetMedia = {
  url: string
  width: number | null
  height: number | null
  alt: string | null
}

export type TweetStats = {
  replies: number
  reposts: number
  likes: number
  bookmarks: number
  views: number
}

export type TweetViewer = {
  liked: boolean
  reposted: boolean
  bookmarked: boolean
}

export type CompactTweet = {
  id: string
  author: TweetAuthor
  text: string
  media: TweetMedia[]
}

export type TweetItem = {
  id: string
  author: TweetAuthor
  text: string
  media: TweetMedia[]
  createdAt: string
  editedAt: string | null
  replyToId: string | null
  replyTo: CompactTweet | null
  stats: TweetStats
  viewer: TweetViewer | null
}

export type TweetPage = {
  items: TweetItem[]
  nextCursor: string | null
  hasMore: boolean
}

export type Action = 'like' | 'repost' | 'bookmark'

export type ActionState = {
  tweetId: string
  stats: TweetStats
  viewer: TweetViewer
}

export type ApiError = {
  code: string
  message: string
  traceId?: string
  details?: unknown
}

export type Me = {
  id: string
  name: string
  avatarUrl: string | null
}

export type FollowUser = {
  id: string
  name: string
  avatarUrl: string | null
  following: boolean
  followsYou: boolean
  self: boolean
}

export type FollowPage = {
  items: FollowUser[]
  nextCursor: string | null
  hasMore: boolean
}

export type FollowState = {
  targetId: string
  following: boolean
  followerCount: number
  followsYou: boolean
}

export type UserProfile = {
  id: string
  name: string
  avatarUrl: string | null
  followingCount: number
  followerCount: number
  tweetCount: number
  viewerFollowing: boolean
  followsYou: boolean
  self: boolean
}

export type SearchOverview = {
  query: string
  users: FollowUser[]
  tweets: TweetPage
  topics: Trend[]
}

export type CheckUserResponse = {
  status: 'success' | 'error'
  message?: string
}
