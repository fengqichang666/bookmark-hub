import { create } from 'zustand'

export const TOKEN_STORAGE_KEY = 'bookmark-hub-token'

export type AuthUser = {
  username: string
  displayName: string
  role: string
  teamId: number
}

type AuthState = {
  token: string | null
  user: AuthUser | null
  setSession: (token: string, user: AuthUser) => void
  clearSession: () => void
}

function readStoredToken() {
  return window.localStorage.getItem(TOKEN_STORAGE_KEY)
}

export const useAuthStore = create<AuthState>((set) => ({
  token: readStoredToken(),
  user: null,
  setSession: (token, user) => {
    window.localStorage.setItem(TOKEN_STORAGE_KEY, token)
    set({ token, user })
  },
  clearSession: () => {
    window.localStorage.removeItem(TOKEN_STORAGE_KEY)
    set({ token: null, user: null })
  },
}))

export function resetAuthStore() {
  useAuthStore.setState({ token: null, user: null })
}
