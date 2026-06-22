"use client"

import { useState } from "react"
import { MoreHorizontal, ShieldCheck, UserPlus } from "lucide-react"
import { useAuth } from "@/components/auth-provider"
import { AddUserAccessDialog } from "@/components/add-user-access-dialog"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from "@/components/ui/dropdown-menu"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { useUsers } from "@/hooks/useUsers"
import { useToast } from "@/hooks/use-toast"
import { UserAccessAPI, type UsuarioAcessoDTO } from "@/lib/api"

export function UsersManagement() {
  const [isDialogOpen, setIsDialogOpen] = useState(false)
  const [editingUser, setEditingUser] = useState<UsuarioAcessoDTO | null>(null)
  const { users, isLoading, mutate } = useUsers()
  const { toast } = useToast()
  const { session, hasRole } = useAuth()

  const isAdmin = hasRole("ADMIN")

  const handleEdit = (user: UsuarioAcessoDTO) => {
    setEditingUser(user)
    setIsDialogOpen(true)
  }

  const handleDelete = async (id: string) => {
    try {
      await UserAccessAPI.deletar(id)
      mutate()
      toast({
        title: "Usuario removido",
        description: "O usuario de acesso foi removido com sucesso.",
      })
    } catch (error: any) {
      toast({
        title: "Erro ao remover",
        description: error?.message || "Nao foi possivel remover o usuario.",
        variant: "destructive",
      })
    }
  }

  const handleDialogClose = () => {
    setIsDialogOpen(false)
    setEditingUser(null)
  }

  if (!isAdmin) {
    return (
      <div className="flex min-h-[400px] items-center justify-center">
        <p className="text-muted-foreground">Apenas administradores podem gerenciar usuarios de acesso.</p>
      </div>
    )
  }

  if (isLoading) {
    return (
      <div className="flex min-h-[400px] items-center justify-center">
        <p className="text-muted-foreground">Carregando usuarios...</p>
      </div>
    )
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-3xl font-bold tracking-tight">Gerenciamento de Usuarios</h1>
        <Button onClick={() => setIsDialogOpen(true)}>
          <UserPlus className="mr-2 h-4 w-4" />
          Adicionar Usuario
        </Button>
      </div>

      <div className="rounded-lg border border-border bg-card">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Username</TableHead>
              <TableHead>Perfil</TableHead>
              <TableHead>Operador Vinculado</TableHead>
              <TableHead>Status</TableHead>
              <TableHead className="w-[70px]">Acoes</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {users && users.length > 0 ? (
              users.map((user) => (
                <TableRow key={user.id}>
                  <TableCell className="font-medium">{user.username}</TableCell>
                  <TableCell>
                    <Badge variant="outline" className="gap-1">
                      <ShieldCheck className="h-3 w-3" />
                      {user.role}
                    </Badge>
                  </TableCell>
                  <TableCell>{user.operadorMissaoNome || "-"}</TableCell>
                  <TableCell>
                    <Badge
                      variant={user.ativo ? "default" : "secondary"}
                      className={
                        user.ativo
                          ? "bg-green-500/10 text-green-500 hover:bg-green-500/20"
                          : "bg-gray-500/10 text-gray-500 hover:bg-gray-500/20"
                      }
                    >
                      {user.ativo ? "Ativo" : "Inativo"}
                    </Badge>
                  </TableCell>
                  <TableCell>
                    <DropdownMenu>
                      <DropdownMenuTrigger asChild>
                        <Button variant="ghost" size="icon" className="h-8 w-8">
                          <MoreHorizontal className="h-4 w-4" />
                        </Button>
                      </DropdownMenuTrigger>
                      <DropdownMenuContent align="end">
                        <DropdownMenuItem onClick={() => handleEdit(user)}>Editar</DropdownMenuItem>
                        <DropdownMenuItem
                          className="text-destructive"
                          disabled={session?.username === user.username}
                          onClick={() => handleDelete(user.id)}
                        >
                          Remover
                        </DropdownMenuItem>
                      </DropdownMenuContent>
                    </DropdownMenu>
                  </TableCell>
                </TableRow>
              ))
            ) : (
              <TableRow>
                <TableCell colSpan={5} className="py-8 text-center text-muted-foreground">
                  Nenhum usuario de acesso cadastrado
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </div>

      <AddUserAccessDialog open={isDialogOpen} onOpenChange={handleDialogClose} user={editingUser} onSuccess={mutate} />
    </div>
  )
}
