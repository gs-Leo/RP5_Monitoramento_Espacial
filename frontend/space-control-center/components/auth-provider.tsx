"use client"

import { createContext, useContext, useEffect, useState, type ReactNode } from "react"
import { AuthAPI } from "@/lib/api"
import {
  clearStoredSession,
  hasAnyRole,
  loadStoredSession,
  storeSession,
  type AuthSession,
  type UserRole,
} from "@/lib/auth"

interface AuthContextValue {
  session: AuthSession | null
  isLoading: boolean
  isAuthenticated: boolean
  login: (username: string, senha: string) => Promise<void>
  logout: () => void
  hasRole: (...roles: UserRole[]) => boolean
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [session, setSession] = useState<AuthSession | null>(null)
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    setSession(loadStoredSession())
    setIsLoading(false)
  }, [])

  const login = async (username: string, senha: string) => {
    const authSession = await AuthAPI.login({ username, senha })
    storeSession(authSession)
    setSession(authSession)
  }

  const logout = () => {
    clearStoredSession()
    setSession(null)
  }

  const value: AuthContextValue = {
    session,
    isLoading,
    isAuthenticated: !!session,
    login,
    logout,
    hasRole: (...roles: UserRole[]) => hasAnyRole(session?.role, roles),
  }

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (!context) {
    throw new Error("useAuth must be used within AuthProvider")
  }
  return context
}
