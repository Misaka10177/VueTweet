export interface TweetAuthor {
  id: string
  name: string
  profile_photo: string
}
export interface Tweet {
  id: string
  author: TweetAuthor
  text: string
  images: string
  publishTime: string
  interaction: {
    reply: number
    transpond: number
    upvote: number
    view: number
    bookmark: number
  }
}
export interface Trend {
  category: string
  topic: string
}
