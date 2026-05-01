"use client"

import { useState } from "react"
import { formatDistanceToNow } from "date-fns"
import { ptBR } from "date-fns/locale"
import {
  Activity,
  AlertTriangle,
  Bot,
  CheckCircle2,
  Info,
  Shield,
  Stethoscope,
  Users,
  Wrench,
  XCircle,
} from "lucide-react"
import { useAuth } from "@/components/auth-provider"
import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { ScrollArea } from "@/components/ui/scroll-area"
import { Separator } from "@/components/ui/separator"
import { useEvents } from "@/hooks/useEvents"
import { useMission } from "@/hooks/useMissions"
import { useProtocols } from "@/hooks/useProtocols"
import { useToast } from "@/hooks/use-toast"
import { MissionAPI, ProtocolAPI } from "@/lib/api"

interface MissionControlPanelProps {
  missionId: string
}

export function MissionControlPanel({ missionId }: MissionControlPanelProps) {
  const { mission, isLoading: missionLoading, mutate: mutateMission } = useMission(missionId)
  const { events, mutate: mutateEvents } = useEvents(missionId)
  const { protocols, mutate: mutateProtocols } = useProtocols(missionId)
  const { toast } = useToast()
  const { hasRole } = useAuth()

  const [isActivating, setIsActivating] = useState<string | null>(null)
  const [isCompleting, setIsCompleting] = useState(false)

  const canOperateMission = hasRole("ADMIN", "OPERADOR")

  const handleActivateProtocol = async (tipo: "MEDICO" | "TECNICO" | "EVACUACAO", descricao: string) => {
    setIsActivating(tipo)
    try {
      await ProtocolAPI.acionar(missionId, { tipo, descricao })
      toast({
        title: "Protocolo acionado",
        description: `Protocolo ${tipo} ativado com sucesso.`,
      })
      mutateProtocols()
      mutateEvents()
    } catch (error: any) {
      toast({
        title: "Erro",
        description: error?.message || "Falha ao acionar protocolo.",
        variant: "destructive",
      })
    } finally {
      setIsActivating(null)
    }
  }

  const handleCompleteMission = async () => {
    setIsCompleting(true)
    try {
      await MissionAPI.concluir(missionId)
      toast({ title: "Missao concluida", description: "A missao foi encerrada com sucesso." })
      mutateMission()
    } catch (error: any) {
      toast({
        title: "Erro",
        description: error?.message || "Falha ao concluir missao.",
        variant: "destructive",
      })
    } finally {
      setIsCompleting(false)
    }
  }

  const getEventIcon = (tipo: string) => {
    switch (tipo) {
      case "INFO":
        return <Info className="h-4 w-4" />
      case "ALERTA":
        return <AlertTriangle className="h-4 w-4" />
      case "ERRO_CRITICO":
        return <XCircle className="h-4 w-4" />
      default:
        return <Info className="h-4 w-4" />
    }
  }

  const getEventBadgeVariant = (tipo: string): "default" | "secondary" | "destructive" => {
    switch (tipo) {
      case "INFO":
        return "default"
      case "ALERTA":
        return "secondary"
      case "ERRO_CRITICO":
        return "destructive"
      default:
        return "default"
    }
  }

  const getStatusBadgeVariant = (status: string): "default" | "secondary" | "destructive" => {
    switch (status) {
      case "EM_ANDAMENTO":
        return "default"
      case "PLANEJADA":
        return "secondary"
      case "CONCLUIDA":
        return "default"
      case "FALHOU":
        return "destructive"
      default:
        return "default"
    }
  }

  if (missionLoading) {
    return (
      <div className="flex h-96 items-center justify-center">
        <div className="text-center">
          <div className="mx-auto mb-4 h-12 w-12 animate-spin rounded-full border-b-2 border-primary" />
          <p className="text-muted-foreground">Carregando dados da missao...</p>
        </div>
      </div>
    )
  }

  if (!mission) {
    return (
      <Alert variant="destructive">
        <AlertTriangle className="h-4 w-4" />
        <AlertTitle>Erro</AlertTitle>
        <AlertDescription>Missao nao encontrada.</AlertDescription>
      </Alert>
    )
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold">{mission.nome}</h1>
          <p className="mt-1 text-muted-foreground">{mission.objetivo}</p>
        </div>
        <div className="flex items-center gap-3">
          <Badge variant={getStatusBadgeVariant(mission.status)} className="text-sm">
            {mission.status === "EM_ANDAMENTO" ? "Em Andamento" : mission.status}
          </Badge>
          {mission.status === "EM_ANDAMENTO" && canOperateMission ? (
            <Button variant="destructive" onClick={handleCompleteMission} disabled={isCompleting}>
              {isCompleting ? "Encerrando..." : "Encerrar Missao"}
            </Button>
          ) : null}
        </div>
      </div>

      <div className="grid gap-6 lg:grid-cols-2">
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <Users className="h-5 w-5" />
              Tripulacao e Biometria
            </CardTitle>
            <CardDescription>Dados biometricos em tempo real</CardDescription>
          </CardHeader>
          <CardContent>
            <ScrollArea className="h-[400px] pr-4">
              <div className="space-y-4">
                {mission.tripulacao && mission.tripulacao.length > 0 ? (
                  mission.tripulacao.map((astronauta) => (
                    <div key={astronauta.id} className="space-y-3 rounded-lg border p-4">
                      <div className="flex items-center justify-between">
                        <div>
                          <p className="font-semibold">{astronauta.nome}</p>
                          <p className="text-sm text-muted-foreground">Missoes: {astronauta.missoesRealizadas}</p>
                        </div>
                        <Badge variant={astronauta.ativo ? "default" : "secondary"}>
                          {astronauta.ativo ? "Ativo" : "Inativo"}
                        </Badge>
                      </div>

                      {astronauta.tipoBiometria ? (
                        <>
                          <Separator />
                          <div className="flex items-center justify-between">
                            <div className="flex items-center gap-2">
                              <Activity className="h-4 w-4 text-primary" />
                              <span className="text-sm font-medium">{astronauta.tipoBiometria}</span>
                            </div>
                            <div className="flex items-center gap-2">
                              <span className="text-lg font-bold">{astronauta.valorBiometria}</span>
                              <span className="text-sm text-muted-foreground">{astronauta.unidadeBiometria}</span>
                            </div>
                          </div>
                          {astronauta.registradoEm ? (
                            <p className="text-xs text-muted-foreground">
                              Atualizado: {formatDistanceToNow(new Date(astronauta.registradoEm), { addSuffix: true, locale: ptBR })}
                            </p>
                          ) : null}
                        </>
                      ) : (
                        <div className="rounded bg-muted/30 p-2 text-center">
                          <p className="text-xs italic text-muted-foreground">Aguardando dados biometricos...</p>
                        </div>
                      )}

                      <div className="mt-2 flex items-center gap-2">
                        <span className="text-xs text-muted-foreground">Aptidao:</span>
                        <Badge variant="outline" className="text-xs">
                          {astronauta.nivelAptidaoMedica}
                        </Badge>
                      </div>
                    </div>
                  ))
                ) : (
                  <div className="flex flex-col items-center justify-center space-y-3 py-12 text-center">
                    <div className="rounded-full bg-blue-500/10 p-4">
                      <Bot className="h-12 w-12 text-blue-500" />
                    </div>
                    <div>
                      <h3 className="text-lg font-semibold">Missao nao tripulada</h3>
                      <p className="mx-auto max-w-xs text-sm text-muted-foreground">
                        Esta missao e operada remotamente ou automatizada via sonda ou satelite.
                      </p>
                    </div>
                  </div>
                )}
              </div>
            </ScrollArea>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <Activity className="h-5 w-5" />
              Log de Eventos
            </CardTitle>
            <CardDescription>Feed em tempo real</CardDescription>
          </CardHeader>
          <CardContent>
            <ScrollArea className="h-[400px] pr-4">
              <div className="space-y-3">
                {events && events.length > 0 ? (
                  events.map((event) => (
                    <div key={event.id} className="rounded-lg border p-3">
                      <div className="flex items-start gap-3">
                        <div className="mt-0.5">{getEventIcon(event.tipo)}</div>
                        <div className="flex-1 space-y-1">
                          <div className="flex items-center justify-between">
                            <Badge variant={getEventBadgeVariant(event.tipo)} className="text-xs">
                              {event.tipo}
                            </Badge>
                            <span className="text-xs text-muted-foreground">
                              {formatDistanceToNow(new Date(event.timestamp), { addSuffix: true, locale: ptBR })}
                            </span>
                          </div>
                          <p className="text-sm">{event.descricao}</p>
                        </div>
                      </div>
                    </div>
                  ))
                ) : (
                  <div className="py-8 text-center">
                    <CheckCircle2 className="mx-auto mb-2 h-12 w-12 text-muted-foreground opacity-50" />
                    <p className="text-muted-foreground">Nenhum evento registrado.</p>
                  </div>
                )}
              </div>
            </ScrollArea>
          </CardContent>
        </Card>
      </div>

      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Shield className="h-5 w-5" />
            Protocolos de Emergencia
          </CardTitle>
          <CardDescription>Acione protocolos manuais em caso de falha sistemica</CardDescription>
        </CardHeader>
        <CardContent className="space-y-6">
          {canOperateMission ? (
            <div className="grid gap-3 md:grid-cols-3">
              <Button
                variant="outline"
                className="h-auto flex-col gap-2 py-4 hover:border-blue-200 hover:bg-blue-50 hover:text-blue-600"
                onClick={() => handleActivateProtocol("MEDICO", "Protocolo medico de emergencia acionado manualmente")}
                disabled={isActivating !== null}
              >
                <Stethoscope className="h-6 w-6" />
                <span>Protocolo Medico</span>
              </Button>
              <Button
                variant="outline"
                className="h-auto flex-col gap-2 py-4 hover:border-orange-200 hover:bg-orange-50 hover:text-orange-600"
                onClick={() => handleActivateProtocol("TECNICO", "Protocolo tecnico de suporte acionado")}
                disabled={isActivating !== null}
              >
                <Wrench className="h-6 w-6" />
                <span>Protocolo Tecnico</span>
              </Button>
              <Button
                variant="outline"
                className="h-auto flex-col gap-2 border-destructive py-4 text-destructive hover:bg-destructive hover:text-white"
                onClick={() => handleActivateProtocol("EVACUACAO", "ALERTA: Protocolo de evacuacao total da nave iniciado")}
                disabled={isActivating !== null}
              >
                <AlertTriangle className="h-6 w-6" />
                <span>Evacuacao Total</span>
              </Button>
            </div>
          ) : (
            <Alert>
              <Shield className="h-4 w-4" />
              <AlertTitle>Leitura liberada</AlertTitle>
              <AlertDescription>
                Seu perfil pode acompanhar a missao, mas nao acionar protocolos nem concluir a operacao.
              </AlertDescription>
            </Alert>
          )}

          <div>
            <h4 className="mb-3 font-semibold">Historico</h4>
            <ScrollArea className="h-[150px]">
              <div className="space-y-2">
                {protocols && protocols.length > 0 ? (
                  protocols.map((protocol) => (
                    <div key={protocol.id} className="rounded-lg border bg-muted/20 p-3">
                      <div className="mb-1 flex items-center justify-between">
                        <Badge variant={protocol.tipo === "EVACUACAO" ? "destructive" : "secondary"} className="text-xs">
                          {protocol.tipo}
                        </Badge>
                        <span className="text-xs text-muted-foreground">
                          {formatDistanceToNow(new Date(protocol.acionadoEm), { addSuffix: true, locale: ptBR })}
                        </span>
                      </div>
                      <p className="text-sm font-medium">{protocol.descricao}</p>
                    </div>
                  ))
                ) : (
                  <p className="rounded border border-dashed bg-muted/10 py-4 text-center text-sm text-muted-foreground">
                    Nenhum protocolo acionado.
                  </p>
                )}
              </div>
            </ScrollArea>
          </div>
        </CardContent>
      </Card>
    </div>
  )
}
