"use client"

import { useEffect, useState } from "react"
import { Plus } from "lucide-react"
import { useAuth } from "@/components/auth-provider"
import { MissionCard } from "@/components/mission-card"
import { NewMissionSheet } from "@/components/new-mission-sheet"
import { Button } from "@/components/ui/button"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { useToast } from "@/hooks/use-toast"
import { MissionAPI, type MissaoDTO } from "@/lib/api"

export function MissionsDashboard() {
  const [isSheetOpen, setIsSheetOpen] = useState(false)
  const [missions, setMissions] = useState<MissaoDTO[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [editingMission, setEditingMission] = useState<MissaoDTO | null>(null)
  const { toast } = useToast()
  const { hasRole } = useAuth()

  const canCreateMission = hasRole("ADMIN", "OPERADOR")
  const canManageMissions = hasRole("ADMIN")

  const fetchMissions = async () => {
    try {
      setIsLoading(true)
      const data = await MissionAPI.listar()
      setMissions(data)
    } catch (error: any) {
      toast({
        title: "Erro ao carregar",
        description: error?.message || "Nao foi possivel buscar as missoes.",
        variant: "destructive",
      })
    } finally {
      setIsLoading(false)
    }
  }

  useEffect(() => {
    fetchMissions()
  }, [])

  const handleDeleteMission = async (id: string) => {
    try {
      await MissionAPI.deletar(id)
      toast({ title: "Missao removida", description: "A missao foi excluida com sucesso." })
      setMissions((prev) => prev.filter((m) => m.id !== id))
    } catch (error: any) {
      toast({
        title: "Erro",
        description: error?.message || "Nao foi possivel remover a missao.",
        variant: "destructive",
      })
    }
  }

  const handleEditMission = (mission: MissaoDTO) => {
    if (!canManageMissions) {
      return
    }
    setEditingMission(mission)
    setIsSheetOpen(true)
  }

  const handleSheetOpenChange = (open: boolean) => {
    setIsSheetOpen(open)
    if (!open) setEditingMission(null)
  }

  const filterMissions = (status?: string) => {
    if (!status) return missions
    return missions.filter((m) => m.status === status)
  }

  const mapToCardProps = (m: MissaoDTO) => ({
    id: m.id,
    name: m.nome,
    destination: "Espaco Profundo",
    launchDate: m.dataInicio,
    status: m.status,
    description: m.objetivo,
    operatorName: m.operadorResponsavel?.nome,
  })

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-3xl font-bold tracking-tight" data-testid="dashboard-title">
          Dashboard de Missoes
        </h1>
        {canCreateMission ? (
          <Button onClick={() => setIsSheetOpen(true)} data-testid="btn-new-mission">
            <Plus className="mr-2 h-4 w-4" />
            Nova Missao
          </Button>
        ) : null}
      </div>

      <Tabs defaultValue="overview" className="space-y-6">
        <TabsList>
          <TabsTrigger value="overview" data-testid="tab-overview">
            Visao Geral
          </TabsTrigger>
          <TabsTrigger value="PLANEJADA" data-testid="tab-planejada">
            Planejadas
          </TabsTrigger>
          <TabsTrigger value="EM_ANDAMENTO" data-testid="tab-em-andamento">
            Em Andamento
          </TabsTrigger>
          <TabsTrigger value="CONCLUIDA" data-testid="tab-concluida">
            Concluidas
          </TabsTrigger>
        </TabsList>

        <TabsContent value="overview" className="space-y-4">
          {isLoading ? (
            <div className="py-10 text-center text-muted-foreground" data-testid="loading-state">
              Carregando...
            </div>
          ) : missions.length === 0 ? (
            <div className="py-10 text-center text-muted-foreground" data-testid="empty-state">
              Nenhuma missao encontrada.
            </div>
          ) : (
            <div className="grid grid-cols-1 gap-6 md:grid-cols-2 lg:grid-cols-3" data-testid="missions-grid">
              {missions.map((mission) => (
                <MissionCard
                  key={mission.id}
                  mission={mapToCardProps(mission)}
                  onDelete={handleDeleteMission}
                  onEdit={() => handleEditMission(mission)}
                  canManage={canManageMissions}
                />
              ))}
            </div>
          )}
        </TabsContent>

        {["PLANEJADA", "EM_ANDAMENTO", "CONCLUIDA"].map((status) => (
          <TabsContent key={status} value={status} className="space-y-4">
            <div className="grid grid-cols-1 gap-6 md:grid-cols-2 lg:grid-cols-3">
              {filterMissions(status).map((mission) => (
                <MissionCard
                  key={mission.id}
                  mission={mapToCardProps(mission)}
                  onDelete={handleDeleteMission}
                  onEdit={() => handleEditMission(mission)}
                  canManage={canManageMissions}
                />
              ))}
            </div>
          </TabsContent>
        ))}
      </Tabs>

      <NewMissionSheet
        open={isSheetOpen}
        onOpenChange={handleSheetOpenChange}
        onSuccess={fetchMissions}
        mission={editingMission}
      />
    </div>
  )
}
