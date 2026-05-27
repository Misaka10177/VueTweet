
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
const mockResponses = {
  'GET /tweet': [
    {
      id: '1',
      text: '楽しかった！！！！\nアンヴィル…………',
      images: base + 'images/92162034_p3.png',
      interaction: { reply: 222, transpond: 333, upvote: 444, view: 999 },
    },
    {
      id: '2',
      text: '今日もいい天気ですね〜',
      images: base + 'images/96919363_p0.jpg',
      interaction: { reply: 55, transpond: 120, upvote: 310, view: 500 },
    },
    {
      id: '3',
      text: '新しいプロジェクトを始めました！\nVue.js + TypeScript で Twitter クローンを作っています。',
      images: base + 'images/92162034_p3.png',
      interaction: { reply: 88, transpond: 200, upvote: 500, view: 1200 },
    },
  ],
  'GET /trends': [
    { category: '中国香港特别行政区', topic: 'LING NIVEA BRAND FRIEND' },
    { category: '台湾', topic: '#skywongravee' },
    { category: '日本', topic: '#アニメ' },
    { category: '技术', topic: 'Vue.js' },
    { category: '全球', topic: '#OpenSource' },
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
