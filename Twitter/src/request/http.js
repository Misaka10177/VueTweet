
import axios from 'axios';
import router from '@/router';

import { useTokenStore } from '@/stores/store';

const isGitHubPages = import.meta.env.PROD

axios.defaults.baseURL = 'http://localhost:3000';

axios.defaults.timeout = 10000;

axios.defaults.responseType = 'json';

axios.defaults.headers.post['Content-Type'] = 'application/json;charset=UTF-8';

axios.defaults.withCredentials = true;

// Mock data for GitHub Pages static deployment
const base = import.meta.env.BASE_URL
const mockTweet1 = {
  id: '1',
  author: { id: 'Misaka', name: 'Misaka Mikoto', profile_photo: base + 'images/img.png' },
  text: '楽しかった！！！！\nアンヴィル…………',
  images: base + 'images/92162034_p3.png',
  publishTime: '2026-06-04T02:00:00',
  interaction: { reply: 2, transpond: 333, upvote: 444, view: 999, bookmark: 10 },
}
const mockTweet2 = {
  id: '2',
  author: { id: 'oO0Eve0Oo', name: 'Eve', profile_photo: base + 'images/img.png' },
  text: '今日もいい天気ですね〜',
  images: base + 'images/96919363_p0.jpg',
  publishTime: '2026-06-05T10:00:00',
  interaction: { reply: 55, transpond: 120, upvote: 310, view: 500, bookmark: 20 },
}
const mockTweet3 = {
  id: '3',
  author: { id: 'akakura1341', name: '赤倉🧸', profile_photo: base + 'images/img.png' },
  text: '新しいプロジェクトを始めました！\nVue.js + TypeScript で Twitter クローンを作っています。',
  images: base + 'images/92162034_p3.png',
  publishTime: '2026-06-03T15:00:00',
  interaction: { reply: 88, transpond: 200, upvote: 500, view: 1200, bookmark: 50 },
}
const mockReply1 = {
  id: '4',
  author: { id: 'Misaka', name: 'Misaka Mikoto', profile_photo: base + 'images/img.png' },
  text: '这条推文太棒了！👍',
  images: '',
  publishTime: '2026-06-05T16:27:29',
  interaction: { reply: 0, transpond: 0, upvote: 0, view: 0, bookmark: 0 },
}
const mockReply2 = {
  id: '5',
  author: { id: 'oO0Eve0Oo', name: 'Eve', profile_photo: base + 'images/img.png' },
  text: 'hhh',
  images: '',
  publishTime: '2026-06-05T10:17:17',
  interaction: { reply: 0, transpond: 0, upvote: 0, view: 0, bookmark: 0 },
}
const mockResponses = {
  'GET /tweet': [mockTweet1, mockTweet2, mockTweet3, mockReply1, mockReply2],
  'GET /tweet/1': { ...mockTweet1, replyTo: null },
  'GET /tweet/2': { ...mockTweet2, replyTo: null },
  'GET /tweet/3': { ...mockTweet3, replyTo: null },
  'GET /tweet/4': { ...mockReply1, replyTo: mockTweet1 },
  'GET /tweet/5': { ...mockReply2, replyTo: mockTweet1 },
  'GET /tweet/1/replies': [mockReply1, mockReply2],
  'GET /tweet/2/replies': [],
  'GET /tweet/3/replies': [],
  'POST /tweet/1/reply': { status: 'success', id: '6' },
  'GET /trends': [
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
    { category: '科技', topic: '#AI绘画' },
    { category: '全球', topic: '#ClimateAction' },
    { category: '日本', topic: '#東京リベンジャーズ' },
    { category: '中国台湾', topic: '#金马奖' },
    { category: '技术', topic: 'React vs Vue' },
    { category: '娱乐', topic: '#动漫电影' },
    { category: '游戏', topic: '#塞尔达传说' },
    { category: '科技', topic: '#GitHubCopilot' },
    { category: '全球', topic: '#WorldCup2026' },
    { category: '日本', topic: '#呪術廻戦' },
  ],
  'GET /currentUser': { username: 'Misaka 10177', userId: 'Misaka10177' },
  'POST /checkUser': { status: 'success' },
  'POST /login': { status: 'success', accessToken: 'mock-token-gh-pages' },
  'POST /refresh': { accessToken: 'mock-token-gh-pages' },
  'POST /logout': { status: 'success' },
}

function getMockKey(method, url) {
  return `${method.toUpperCase()} ${url}`
}

if (isGitHubPages) {
  axios.defaults.adapter = (config) => {
    const method = (config.method || 'get').toUpperCase()
    const url = config.url || ''
    const key = getMockKey(method, url)
    const data = mockResponses[key]

    if (data !== undefined) {
      return Promise.resolve({
        data,
        status: 200,
        statusText: 'OK',
        headers: {},
        config,
      })
    }
    return Promise.reject(new Error(`Mock: no response for ${key}`))
  }
}

axios.interceptors.request.use(cfg => {
  const token = useTokenStore().accessToken
  if (token) cfg.headers.Authorization = `Bearer ${token}`
  return cfg
})

export function get(url, params) {
  return new Promise((resolve, reject) => {
    axios.get(url, {
      params: params
    }).then(res => {
      resolve(res.data);
    }).catch(err => {
      reject(err.data)
    })
  });
}

export function put(url, params) {
  return new Promise((resolve, reject) => {
    axios.put(url, params).then(res => {
      resolve(res.data);
    }).catch(err => {
      reject(err.data)
    })
  });
}

export function post(url, params) {
  return new Promise((resolve, reject) => {
    axios.post(url, params).then(res => {
      resolve(res.data);
    }).catch(err => {
      reject(err.data)
    })
  });
}



// token过期处理

let refreshPromise = null

axios.interceptors.response.use(
  res => res,
  async err => {
    //防止多次请求刷新
    const original = err.config || {}
    if (original.url && original.url.includes('/refresh')) return Promise.reject(err)

    if (err.response?.status === 401) {
      if (original._retry) return Promise.reject(err)
      original._retry = true

      if (!refreshPromise) {
        refreshPromise = axios.post('/refresh', {})
          .then(r => {
            const token = r.data?.accessToken
            if (!token) throw new Error('no token')
            useTokenStore().setTokens(token)
            return token
          })
          .catch(e => { throw e })
          .finally(() => { refreshPromise = null })
      }

      //原请求换新token，失败跳转
      try {
        const token = await refreshPromise
        original.headers = original.headers || {}
        original.headers.Authorization = `Bearer ${token}`
        return axios(original)
      } catch (e) {
        router.push('/join')
        return Promise.reject(e)
      }
    }

    return Promise.reject(err)
  }
)
