
import { get, post, put } from './http';
// import { useTokenStore } from '@/stores/store';

export const getTweetList = () => get('/tweet');
export const getTweet = (tweetId) => get(`/tweet/${tweetId}`);
export const getTrendList = () => get('/trends');
export const getuserInfo = () => get('/currentUser');
export const getReplies = (tweetId) => get(`/tweet/${tweetId}/replies`);
export const addReply = (tweetId, p) => post(`/tweet/${tweetId}/reply`, p);
export const toggleUpvote = (tweetId) => post(`/tweet/${tweetId}/upvote`);
export const toggleTranspond = (tweetId) => post(`/tweet/${tweetId}/transpond`);
export const toggleBookmark = (tweetId) => post(`/tweet/${tweetId}/bookmark`);


export const checkUser = (p) => post('/checkUser', p);
export const login = (p) => post('/login', p);

export const getToken = () => post('/refresh');
export const logout = () => post('/logout');

// export const addSubscribe = p => {
//   let re = /^\d+$/
//   if (!re.test(p)) {
//     return "error"
//   }
//   get('/following').then((res) => {
//     let following_list = res.list
//     if (!following_list.includes(p)) {
//       following_list.push(p)
//       put('/following', { list: following_list })
//       return "success"
//     } else {
//       return "error"
//     }
//   });

// };



