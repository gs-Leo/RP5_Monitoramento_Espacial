"use client"

import { useEffect, useState, type ReactNode } from "react"
import { usePathname, useRouter } from "next/navigation"
import { Sidebar } from "@/components/sidebar"
import { useAuth } from "@/components/auth-provider"

export function AppShell({ children }: { children: ReactNode }) {
  const pathname = usePathname()
  const router = useRouter()
  const { isAuthenticated, isLoading } = useAuth()
  const [mounted, setMounted] = useState(false)

  const isLoginPage = pathname === "/login"

  useEffect(() => {
    setMounted(true)
  }, [])

  useEffect(() => {
    if (!mounted || isLoading) {
      return
    }

    if (!isAuthenticated && !isLoginPage) {
      router.replace("/login")
      return
    }

    if (isAuthenticated && isLoginPage) {
      router.replace("/")
    }
  }, [isAuthenticated, isLoading, isLoginPage, mounted, router])

  if (!mounted) {
    return null
  }

  if (isLoading || (!isAuthenticated && !isLoginPage)) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <p className="text-sm text-muted-foreground">Carregando sessao...</p>
      </div>
    )
  }

  if (isLoginPage) {
    return <main className="min-h-screen">{children}</main>
  }

  return (
    <div className="flex min-h-screen">
      <Sidebar />
      <main className="flex-1 overflow-auto p-6 md:p-8">{children}</main>
    </div>
  )
}
