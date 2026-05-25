export interface Tweet {
  id: string
  text: string
  images: string
  interaction: {
    reply: number
    transpond: number
    upvote: number
    view: number
  }
}
export interface Trend {
  category: string
  topic: string
}
