export function setToken(token) {
  localStorage.setItem('token', token)
}
export function getToken() {
  return localStorage.getItem('token')
}
export function removeToken() {
  localStorage.removeItem('token')
}
export function setUserInfo(user) {
  localStorage.setItem('user', JSON.stringify(user))
}
export function getUserInfo() {
  const s = localStorage.getItem('user')
  return s ? JSON.parse(s) : null
}
export function removeUserInfo() {
  localStorage.removeItem('user')
}

// 新增：清空用户登录信息（解决报错）
export function clearUserInfo() {
  localStorage.removeItem('user')
}