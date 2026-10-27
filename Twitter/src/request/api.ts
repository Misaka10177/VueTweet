import { del, get, post, postForm } from './http'
import type { AuthResponse, AuthSession, Tweet } from '@/types'

export const getTweetList = (page = 0, size = 20) => get(`/tweet?page=${page}&size=${size}`)
export const getTweet = (tweetId: string) => get(`/tweet/${tweetId}`)
export const getTrendList = () => get('/trends')
export const getuserInfo = () => get('/currentUser')
export const getReplies = (tweetId: string) => get(`/tweet/${tweetId}/replies`)
export const createTweet = (form: FormData) => postForm<Tweet>('/tweet', form)
export const addReply = (tweetId: string, p: object) => post(`/tweet/${tweetId}/reply`, p)
export const toggleUpvote = (tweetId: string) => post(`/tweet/${tweetId}/upvote`)
export const toggleTranspond = (tweetId: string) => post(`/tweet/${tweetId}/transpond`)
export const toggleBookmark = (tweetId: string) => post(`/tweet/${tweetId}/bookmark`)

export const checkUser = (p: object) => post('/checkUser', p)
export const login = (p: object) => post<AuthResponse>('/auth/login', p)
export const logout = () => post('/auth/logout')
export const refresh = () => post<AuthResponse>('/auth/refresh')

export const getSessions = () => get<AuthSession[]>('/auth/sessions')
export const revokeSession = (id: string) => del(`/auth/sessions/${id}`)
export const changePassword = (p: object) => post('/auth/change-password', p)
