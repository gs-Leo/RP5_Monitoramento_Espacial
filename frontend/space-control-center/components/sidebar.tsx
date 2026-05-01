"use client"

import Link from "next/link"
import { usePathname, useRouter } from "next/navigation"
import { Headset, LogOut, Menu, Rocket, ShieldCheck, Ship, User, X } from "lucide-react"
import { useState } from "react"
import { useAuth } from "@/components/auth-provider"
import { Button } from "@/components/ui/button"
import { cn } from "@/lib/utils"

const navigation = [
  { name: "Missoes", href: "/", icon: Rocket },
  { name: "Astronautas", href: "/astronautas", icon: User },
  { name: "Espaconaves", href: "/espaconaves", icon: Ship },
  { name: "Operadores", href: "/operadores", icon: Headset },
]

export function Sidebar() {
  const pathname = usePathname()
  const router = useRouter()
  const { session, logout, hasRole } = useAuth()
  const [isOpen, setIsOpen] = useState(false)
  const isAdmin = hasRole("ADMIN")
  const navItems = [...navigation, ...(isAdmin ? [{ name: "Usuarios", href: "/usuarios", icon: ShieldCheck }] : [])]

  const handleLogout = () => {
    logout()
    router.replace("/login")
  }

  return (
    <>
      <div className="fixed left-4 top-4 z-50 md:hidden">
        <Button variant="outline" size="icon" onClick={() => setIsOpen(!isOpen)} className="bg-card">
          {isOpen ? <X className="h-5 w-5" /> : <Menu className="h-5 w-5" />}
        </Button>
      </div>

      <aside
        className={cn(
          "fixed inset-y-0 left-0 z-40 w-64 border-r border-border bg-card transition-transform duration-300 ease-in-out md:translate-x-0",
          isOpen ? "translate-x-0" : "-translate-x-full",
        )}
      >
        <div className="flex h-full flex-col">
          <div className="border-b border-border p-6">
            <div className="flex items-center gap-3">
              <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-primary">
                <Rocket className="h-6 w-6 text-primary-foreground" />
              </div>
              <div>
                <h1 className="text-lg font-bold leading-tight">Centro de</h1>
                <h1 className="text-lg font-bold leading-tight">Controle Espacial</h1>
              </div>
            </div>
            {session ? (
              <div className="mt-4 rounded-lg border border-border bg-muted/40 p-3 text-sm">
                <p className="font-medium">{session.username}</p>
                <p className="text-muted-foreground">Perfil: {session.role}</p>
              </div>
            ) : null}
          </div>

          <nav className="flex-1 space-y-2 p-4">
            {navItems.map((item) => {
              const isActive = pathname === item.href
              return (
                <Link
                  key={item.name}
                  href={item.href}
                  onClick={() => setIsOpen(false)}
                  data-testid={`nav-${item.href.replace("/", "") || "home"}`}
                  className={cn(
                    "flex items-center gap-3 rounded-lg px-4 py-3 transition-colors",
                    isActive
                      ? "bg-primary text-primary-foreground"
                      : "text-muted-foreground hover:bg-accent hover:text-accent-foreground",
                  )}
                >
                  <item.icon className="h-5 w-5" />
                  <span className="font-medium">{item.name}</span>
                </Link>
              )
            })}
          </nav>

          <div className="border-t border-border p-4">
            <Button variant="outline" className="w-full justify-start gap-2 bg-transparent" onClick={handleLogout}>
              <LogOut className="h-4 w-4" />
              Sair
            </Button>
          </div>
        </div>
      </aside>

      {isOpen ? (
        <div
          className="fixed inset-0 z-30 bg-background/80 backdrop-blur-sm md:hidden"
          onClick={() => setIsOpen(false)}
        />
      ) : null}

      <div className="hidden w-64 flex-shrink-0 md:block" />
    </>
  )
}
