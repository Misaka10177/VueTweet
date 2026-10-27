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
export type TweetAuthor = {
  id: string
  name: string
  profile_photo: string
}
export type Tweet = {
  id: string
  author: TweetAuthor
  text: string
  images: string[]
  publishTime: string
  replyTo?: Tweet
  interaction: {
    reply: number
    transpond: number
    upvote: number
    view: number
    bookmark: number
  }
  myUpvote?: boolean
  myTranspond?: boolean
  myBookmark?: boolean
}
export type Trend = {
  category: string
  topic: string
}
