"use client"

import { useState } from "react"
import { MoreHorizontal, UserPlus } from "lucide-react"
import { useAuth } from "@/components/auth-provider"
import { AddAstronautDialog } from "@/components/add-astronaut-dialog"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from "@/components/ui/dropdown-menu"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { useAstronauts } from "@/hooks/useAstronauts"
import { useToast } from "@/hooks/use-toast"
import { AstronautAPI, type AstronautDTO } from "@/lib/api"

export function AstronautsManagement() {
  const [isDialogOpen, setIsDialogOpen] = useState(false)
  const [editingAstronaut, setEditingAstronaut] = useState<AstronautDTO | null>(null)
  const { astronauts, isLoading, mutate } = useAstronauts()
  const { toast } = useToast()
  const { hasRole } = useAuth()

  const canManage = hasRole("ADMIN")

  const handleEdit = (astronaut: AstronautDTO) => {
    if (!canManage) {
      return
    }
    setEditingAstronaut(astronaut)
    setIsDialogOpen(true)
  }

  const handleDelete = async (id: string) => {
    try {
      await AstronautAPI.deletar(id)
      mutate()
      toast({ title: "Astronauta removido", description: "O astronauta foi removido com sucesso." })
    } catch (error: any) {
      toast({
        title: "Erro ao remover",
        description: error?.message || "Nao foi possivel remover o astronauta.",
        variant: "destructive",
      })
    }
  }

  const handleDialogClose = (open: boolean) => {
    setIsDialogOpen(open)
    if (!open) setEditingAstronaut(null)
  }

  if (isLoading) {
    return (
      <div className="flex min-h-[400px] items-center justify-center">
        <p className="text-muted-foreground">Carregando astronautas...</p>
      </div>
    )
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-3xl font-bold tracking-tight">Gerenciamento de Astronautas</h1>
        {canManage ? (
          <Button onClick={() => setIsDialogOpen(true)} data-testid="btn-add-astronaut">
            <UserPlus className="mr-2 h-4 w-4" />
            Adicionar Astronauta
          </Button>
        ) : null}
      </div>

      <div className="rounded-lg border border-border bg-card">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Nome</TableHead>
              <TableHead>Nivel de Aptidao</TableHead>
              <TableHead>Missoes</TableHead>
              <TableHead>Status</TableHead>
              <TableHead className="w-[70px]">Acoes</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {astronauts && astronauts.length > 0 ? (
              astronauts.map((astronaut) => (
                <TableRow key={astronaut.id}>
                  <TableCell className="font-medium">{astronaut.nome}</TableCell>
                  <TableCell>{astronaut.nivelAptidaoMedica}</TableCell>
                  <TableCell>{astronaut.missoesRealizadas}</TableCell>
                  <TableCell>
                    <Badge variant={astronaut.ativo ? "default" : "secondary"}>
                      {astronaut.ativo ? "Ativo" : "Inativo"}
                    </Badge>
                  </TableCell>
                  <TableCell>
                    {canManage ? (
                      <DropdownMenu>
                        <DropdownMenuTrigger asChild>
                          <Button variant="ghost" size="icon" className="h-8 w-8">
                            <MoreHorizontal className="h-4 w-4" />
                          </Button>
                        </DropdownMenuTrigger>
                        <DropdownMenuContent align="end">
                          <DropdownMenuItem onClick={() => handleEdit(astronaut)}>Editar Perfil</DropdownMenuItem>
                          <DropdownMenuItem className="text-destructive" onClick={() => handleDelete(astronaut.id)}>
                            Remover
                          </DropdownMenuItem>
                        </DropdownMenuContent>
                      </DropdownMenu>
                    ) : null}
                  </TableCell>
                </TableRow>
              ))
            ) : (
              <TableRow>
                <TableCell colSpan={5} className="py-8 text-center text-muted-foreground">
                  Nenhum astronauta cadastrado
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </div>

      <AddAstronautDialog open={isDialogOpen} onOpenChange={handleDialogClose} onSuccess={mutate} astronaut={editingAstronaut} />
    </div>
  )
}
