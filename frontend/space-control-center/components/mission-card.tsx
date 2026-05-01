"use client"

import { useState } from "react"
import { Calendar, MoreVertical, Play } from "lucide-react"
import { SimulationFullscreen } from "@/components/SimulationFullscreen"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card"
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from "@/components/ui/dropdown-menu"

interface Mission {
  id: string
  name: string
  destination: string
  launchDate: string
  status: string
  description: string
  operatorName?: string
}

const statusConfig: Record<string, { variant: "default" | "secondary" | "outline" | "destructive"; className: string; label: string }> = {
  PLANEJADA: { variant: "default", className: "bg-blue-500/10 text-blue-500 hover:bg-blue-500/20", label: "Planejada" },
  EM_ANDAMENTO: { variant: "secondary", className: "bg-yellow-500/10 text-yellow-500 hover:bg-yellow-500/20", label: "Em Andamento" },
  CONCLUIDA: { variant: "outline", className: "bg-green-500/10 text-green-500 hover:bg-green-500/20", label: "Concluida" },
  FALHOU: { variant: "destructive", className: "bg-red-500/10 text-red-500 hover:bg-red-500/20", label: "Falhou" },
}

interface MissionCardProps {
  mission: Mission
  onDelete: (id: string) => void
  onEdit?: () => void
  canManage?: boolean
}

export function MissionCard({ mission, onDelete, onEdit, canManage = false }: MissionCardProps) {
  const [isSimulationOpen, setIsSimulationOpen] = useState(false)

  const statusStyle = statusConfig[mission.status] || {
    variant: "outline" as const,
    className: "border-gray-200 bg-gray-500/10 text-gray-500",
    label: mission.status,
  }

  return (
    <>
      <Card className="flex h-full flex-col">
        <CardHeader>
          <div className="flex items-start justify-between">
            <div className="space-y-1">
              <CardTitle className="text-xl">{mission.name}</CardTitle>
              <CardDescription>{mission.destination}</CardDescription>
            </div>
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <Button variant="ghost" size="icon" className="h-8 w-8">
                  <MoreVertical className="h-4 w-4" />
                </Button>
              </DropdownMenuTrigger>
              <DropdownMenuContent align="end">
                {canManage ? <DropdownMenuItem onClick={onEdit}>Editar</DropdownMenuItem> : null}
                <DropdownMenuItem onClick={() => setIsSimulationOpen(true)}>Ver Simulacao</DropdownMenuItem>
                {canManage ? (
                  <DropdownMenuItem className="cursor-pointer text-destructive" onClick={() => onDelete(mission.id)}>
                    Excluir
                  </DropdownMenuItem>
                ) : null}
              </DropdownMenuContent>
            </DropdownMenu>
          </div>
        </CardHeader>
        <CardContent className="flex-1 space-y-4">
          <div className="flex items-center gap-2 text-sm text-muted-foreground">
            <Calendar className="h-4 w-4" />
            <span>Lancamento: {mission.launchDate}</span>
          </div>
          <div className="text-sm text-muted-foreground">Operador responsavel: {mission.operatorName || "A definir"}</div>
          <Badge variant={statusStyle.variant} className={statusStyle.className}>
            {statusStyle.label}
          </Badge>
          <p className="text-sm text-muted-foreground">{mission.description}</p>
        </CardContent>
        <CardFooter>
          <Button className="w-full" onClick={() => setIsSimulationOpen(true)} disabled={mission.status === "CONCLUIDA"}>
            <Play className="mr-2 h-4 w-4" /> Iniciar Simulacao
          </Button>
        </CardFooter>
      </Card>

      <Dialog open={isSimulationOpen} onOpenChange={setIsSimulationOpen}>
        <DialogContent
          className="h-screen max-h-full w-screen max-w-full overflow-hidden border-0 bg-black p-0"
          style={{ width: "100vw", height: "100vh", maxWidth: "none", maxHeight: "none" }}
        >
          <DialogHeader className="sr-only">
            <DialogTitle>Simulacao: {mission.name}</DialogTitle>
          </DialogHeader>
          <SimulationFullscreen mission={mission} />
        </DialogContent>
      </Dialog>
    </>
  )
}
