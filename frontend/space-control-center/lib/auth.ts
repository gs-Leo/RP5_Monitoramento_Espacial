export type UserRole = "ADMIN" | "OPERADOR" | "ANALISTA"

export interface AuthSession {
  token: string
  username: string
  role: UserRole
  expiresAt: number
}

export const AUTH_STORAGE_KEY = "space-control-auth"

export function loadStoredSession(): AuthSession | null {
  if (typeof window === "undefined") {
    return null
  }

  const raw = window.localStorage.getItem(AUTH_STORAGE_KEY)
  if (!raw) {
    return null
  }

  try {
    const session = JSON.parse(raw) as AuthSession
    if (!session?.token || !session?.username || !session?.role || !session?.expiresAt) {
      clearStoredSession()
      return null
    }

    if (session.expiresAt <= Date.now()) {
      clearStoredSession()
      return null
    }

    return session
  } catch {
    clearStoredSession()
    return null
  }
}

export function storeSession(session: AuthSession) {
  if (typeof window !== "undefined") {
    window.localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(session))
  }
}

export function clearStoredSession() {
  if (typeof window !== "undefined") {
    window.localStorage.removeItem(AUTH_STORAGE_KEY)
  }
}

export function hasAnyRole(role: UserRole | null | undefined, allowedRoles: UserRole[]) {
  return !!role && allowedRoles.includes(role)
}
