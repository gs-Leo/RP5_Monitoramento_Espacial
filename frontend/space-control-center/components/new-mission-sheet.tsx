"use client"

import { useEffect, useState } from "react"
import { format } from "date-fns"
import { ptBR } from "date-fns/locale"
import { CalendarIcon, Check, ChevronsUpDown, Rocket } from "lucide-react"
import { useAuth } from "@/components/auth-provider"
import { Button } from "@/components/ui/button"
import { Calendar } from "@/components/ui/calendar"
import { Command, CommandEmpty, CommandGroup, CommandInput, CommandItem, CommandList } from "@/components/ui/command"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Sheet, SheetContent, SheetDescription, SheetFooter, SheetHeader, SheetTitle } from "@/components/ui/sheet"
import { Textarea } from "@/components/ui/textarea"
import { useToast } from "@/hooks/use-toast"
import {
  AstronautAPI,
  MissionAPI,
  OperatorAPI,
  SpaceshipAPI,
  type AstronautDTO,
  type CriarMissaoRequest,
  type EspaconaveDTO,
  type MissaoDTO,
  type OperadorDeMissaoDTO,
} from "@/lib/api"
import { cn } from "@/lib/utils"

interface NewMissionSheetProps {
  open: boolean
  onOpenChange: (open: boolean) => void
  onSuccess?: () => void
  mission?: MissaoDTO | null
}

const DEFAULT_SIMULATION_TYPE = "foguete" as const
const NO_OPERATOR_VALUE = "__none__"

export function NewMissionSheet({ open, onOpenChange, onSuccess, mission }: NewMissionSheetProps) {
  const { hasRole } = useAuth()
  const { toast } = useToast()
  const [nome, setNome] = useState("")
  const [objetivo, setObjetivo] = useState("")
  const [dataInicio, setDataInicio] = useState<Date>()
  const [tipoSimulacao, setTipoSimulacao] = useState<"foguete" | "orbita" | "reentrada">(DEFAULT_SIMULATION_TYPE)
  const [crewOpen, setCrewOpen] = useState(false)
  const [selectedCrew, setSelectedCrew] = useState<string[]>([])
  const [astronauts, setAstronauts] = useState<AstronautDTO[]>([])
  const [selectedSpaceship, setSelectedSpaceship] = useState("")
  const [spaceships, setSpaceships] = useState<EspaconaveDTO[]>([])
  const [operators, setOperators] = useState<OperadorDeMissaoDTO[]>([])
  const [selectedOperator, setSelectedOperator] = useState(NO_OPERATOR_VALUE)
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [errors, setErrors] = useState<Partial<Record<"nome" | "objetivo" | "dataInicio" | "espaconave" | "operador", string>>>({})

  const isAdmin = hasRole("ADMIN")

  useEffect(() => {
    if (!open) {
      return
    }

    AstronautAPI.listar()
      .then(setAstronauts)
      .catch(() => console.error("Erro ao carregar astronautas"))

    SpaceshipAPI.listar()
      .then(setSpaceships)
      .catch(() => console.error("Erro ao carregar espaconaves"))

    if (isAdmin) {
      OperatorAPI.listar()
        .then(setOperators)
        .catch(() => console.error("Erro ao carregar operadores"))
    }
  }, [open, isAdmin])

  useEffect(() => {
    if (!open) {
      return
    }

    setErrors({})

    if (mission) {
      setNome(mission.nome)
      setObjetivo(mission.objetivo)
      setTipoSimulacao(mission.tipoSimulacao || DEFAULT_SIMULATION_TYPE)
      setSelectedCrew(mission.tripulacao?.map((astronaut) => astronaut.id.toString()) ?? [])
      setSelectedSpaceship(mission.espaconave?.id?.toString() ?? "")
      setSelectedOperator(mission.operadorResponsavel?.id?.toString() ?? NO_OPERATOR_VALUE)

      if (mission.dataInicio) {
        const [ano, mes, dia] = mission.dataInicio.split("-").map(Number)
        setDataInicio(new Date(ano, mes - 1, dia))
      } else {
        setDataInicio(undefined)
      }
      return
    }

    setNome("")
    setObjetivo("")
    setDataInicio(undefined)
    setSelectedCrew([])
    setSelectedSpaceship("")
    setSelectedOperator(NO_OPERATOR_VALUE)
    setTipoSimulacao(DEFAULT_SIMULATION_TYPE)
    setErrors({})
  }, [mission, open])

  const toggleCrew = (astronautId: string) => {
    setSelectedCrew((prev) =>
      prev.includes(astronautId) ? prev.filter((id) => id !== astronautId) : [...prev, astronautId],
    )
  }

  const validate = () => {
    const nextErrors: Partial<Record<"nome" | "objetivo" | "dataInicio" | "espaconave" | "operador", string>> = {}

    if (!nome.trim()) {
      nextErrors.nome = "Informe o nome da missao."
    }

    if (!objetivo.trim()) {
      nextErrors.objetivo = "Informe o objetivo da missao."
    }

    if (!dataInicio) {
      nextErrors.dataInicio = "Informe a data de inicio."
    }

    if (!selectedSpaceship) {
      nextErrors.espaconave = "Selecione uma espaconave para a missao."
    }

    if (isAdmin && selectedOperator === NO_OPERATOR_VALUE) {
      nextErrors.operador = "Selecione o operador responsavel pela missao."
    }

    setErrors(nextErrors)

    if (Object.keys(nextErrors).length > 0) {
      toast({
        title: "Campos obrigatorios",
        description: "Preencha os campos destacados para salvar a missao.",
        variant: "destructive",
      })
      return false
    }

    return true
  }

  const buildPayload = (): CriarMissaoRequest => ({
    nome: nome.trim(),
    objetivo: objetivo.trim(),
    dataInicio: format(dataInicio!, "yyyy-MM-dd"),
    tipoSimulacao,
    tripulacaoIds: selectedCrew.map((id) => Number(id)),
    espaconaveId: Number(selectedSpaceship),
    operadorId: isAdmin && selectedOperator !== NO_OPERATOR_VALUE ? Number(selectedOperator) : undefined,
  })

  const handleSubmit = async () => {
    if (!validate()) {
      return
    }

    setIsSubmitting(true)
    try {
      const payload = buildPayload()

      if (mission) {
        await MissionAPI.atualizar(mission.id, payload)
        toast({ title: "Sucesso", description: "Missao atualizada." })
      } else {
        await MissionAPI.criar(payload)
        toast({ title: "Sucesso", description: "Missao criada." })
      }

      onOpenChange(false)
      onSuccess?.()
    } catch (error: any) {
      toast({
        title: "Erro",
        description: error.message || "Falha ao salvar missao.",
        variant: "destructive",
      })
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <Sheet open={open} onOpenChange={onOpenChange}>
      <SheetContent className="w-full overflow-y-auto p-0 sm:max-w-[640px]">
        <SheetHeader className="border-b border-border px-6 py-6">
          <SheetTitle>{mission ? "Editar Missao" : "Criar Nova Missao"}</SheetTitle>
          <SheetDescription>
            {isAdmin
              ? "Defina a missao e escolha quem sera o operador responsavel."
              : "As missoes criadas por voce ficam vinculadas automaticamente ao seu operador."}
          </SheetDescription>
        </SheetHeader>

        <div className="grid gap-6 px-6 py-6">
          <div className="grid gap-2">
            <Label htmlFor="nome">Nome da Missao</Label>
            <Input
              id="nome"
              data-testid="input-mission-name"
              value={nome}
              onChange={(event) => {
                setNome(event.target.value)
                setErrors((current) => ({ ...current, nome: undefined }))
              }}
              placeholder="Ex: Missao Marte I"
              aria-invalid={!!errors.nome}
            />
            {errors.nome ? <p className="text-sm text-destructive">{errors.nome}</p> : null}
          </div>

          <div className="grid gap-2">
            <Label htmlFor="objetivo">Objetivo</Label>
            <Textarea
              id="objetivo"
              data-testid="input-mission-objective"
              value={objetivo}
              onChange={(event) => {
                setObjetivo(event.target.value)
                setErrors((current) => ({ ...current, objetivo: undefined }))
              }}
              placeholder="Ex: Estabelecer base avancada"
              rows={4}
              aria-invalid={!!errors.objetivo}
            />
            {errors.objetivo ? <p className="text-sm text-destructive">{errors.objetivo}</p> : null}
          </div>

          <div className="grid gap-2">
            <Label>Tipo de Simulacao</Label>
            <Select
              value={tipoSimulacao}
              onValueChange={(value) => setTipoSimulacao(value as "foguete" | "orbita" | "reentrada")}
            >
              <SelectTrigger data-testid="select-simulation-type" className="w-full">
                <SelectValue placeholder="Selecione o tipo" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="foguete">Foguete - Lancamento</SelectItem>
                <SelectItem value="orbita">Orbita - Orbital</SelectItem>
                <SelectItem value="reentrada">Reentrada - Atmosferica</SelectItem>
              </SelectContent>
            </Select>
          </div>

          <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
            <div className="grid gap-2">
              <Label>Data de Inicio</Label>
              <Popover>
                <PopoverTrigger asChild>
                  <Button
                    data-testid="btn-calendar-trigger"
                    variant="outline"
                    className={cn(
                      "justify-start text-left font-normal",
                      !dataInicio && "text-muted-foreground",
                      errors.dataInicio && "border-destructive text-destructive",
                    )}
                    aria-invalid={!!errors.dataInicio}
                  >
                    <CalendarIcon className="mr-2 h-4 w-4" />
                    {dataInicio ? format(dataInicio, "PPP", { locale: ptBR }) : "Selecione"}
                  </Button>
                </PopoverTrigger>
                <PopoverContent className="w-auto p-0" align="start">
                  <Calendar
                    mode="single"
                    selected={dataInicio}
                    onSelect={(date) => {
                      setDataInicio(date)
                      setErrors((current) => ({ ...current, dataInicio: undefined }))
                    }}
                    initialFocus
                  />
                </PopoverContent>
              </Popover>
              {errors.dataInicio ? <p className="text-sm text-destructive">{errors.dataInicio}</p> : null}
            </div>

            <div className="grid gap-2">
              <Label>Espaconave</Label>
              <Select
                value={selectedSpaceship}
                onValueChange={(value) => {
                  setSelectedSpaceship(value)
                  setErrors((current) => ({ ...current, espaconave: undefined }))
                }}
              >
                <SelectTrigger
                  data-testid="select-mission-spaceship"
                  className={cn("w-full", errors.espaconave && "border-destructive")}
                  aria-invalid={!!errors.espaconave}
                >
                  <SelectValue placeholder="Selecione a nave" />
                </SelectTrigger>
                <SelectContent>
                  {spaceships.map((ship) => (
                    <SelectItem
                      key={ship.id}
                      value={ship.id.toString()}
                      disabled={ship.statusOperacional !== "OPERACIONAL"}
                      data-testid={`option-spaceship-${ship.id}`}
                    >
                      <span className="flex items-center gap-2">
                        <Rocket className="h-4 w-4" />
                        {ship.nome}
                        {ship.statusOperacional !== "OPERACIONAL" ? " (Indisp.)" : ""}
                      </span>
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
              {errors.espaconave ? <p className="text-sm text-destructive">{errors.espaconave}</p> : null}
            </div>
          </div>

          {isAdmin ? (
            <div className="grid gap-2">
              <Label>Operador Responsavel</Label>
              <Select
                value={selectedOperator}
                onValueChange={(value) => {
                  setSelectedOperator(value)
                  setErrors((current) => ({ ...current, operador: undefined }))
                }}
              >
                <SelectTrigger className={cn("w-full", errors.operador && "border-destructive")} aria-invalid={!!errors.operador}>
                  <SelectValue placeholder="Selecione o operador responsavel" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value={NO_OPERATOR_VALUE}>Selecione um operador</SelectItem>
                  {operators.map((operator) => (
                    <SelectItem key={operator.id} value={operator.id.toString()}>
                      {operator.nome}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
              {errors.operador ? <p className="text-sm text-destructive">{errors.operador}</p> : null}
              <p className="text-xs text-muted-foreground">
                Esse operador passara a visualizar e operar essa missao no perfil OPERADOR.
              </p>
            </div>
          ) : null}

          <div className="grid gap-2">
            <Label>Tripulacao ({selectedCrew.length} selecionados)</Label>
            <Popover open={crewOpen} onOpenChange={setCrewOpen}>
              <PopoverTrigger asChild>
                <Button
                  data-testid="btn-crew-combobox"
                  variant="outline"
                  role="combobox"
                  aria-expanded={crewOpen}
                  className="w-full justify-between bg-transparent"
                >
                  {selectedCrew.length > 0 ? `${selectedCrew.length} astronauta(s)` : "Selecionar tripulacao"}
                  <ChevronsUpDown className="ml-2 h-4 w-4 shrink-0 opacity-50" />
                </Button>
              </PopoverTrigger>
              <PopoverContent className="w-[400px] p-0" align="start">
                <Command>
                  <CommandInput placeholder="Buscar astronauta..." />
                  <CommandList>
                    <CommandEmpty>Nenhum astronauta encontrado.</CommandEmpty>
                    <CommandGroup>
                      {astronauts.map((astronaut) => (
                        <CommandItem key={astronaut.id} onSelect={() => toggleCrew(astronaut.id.toString())}>
                          <Check
                            className={cn(
                              "mr-2 h-4 w-4",
                              selectedCrew.includes(astronaut.id.toString()) ? "opacity-100" : "opacity-0",
                            )}
                          />
                          <div className="flex flex-col">
                            <span>{astronaut.nome}</span>
                            <span className="text-xs text-muted-foreground">
                              {astronaut.nivelAptidaoMedica} • {astronaut.ativo ? "Ativo" : "Inativo"}
                            </span>
                          </div>
                        </CommandItem>
                      ))}
                    </CommandGroup>
                  </CommandList>
                </Command>
              </PopoverContent>
            </Popover>
          </div>
        </div>

        <SheetFooter className="mt-auto border-t border-border px-6 py-4">
          <Button variant="outline" onClick={() => onOpenChange(false)} disabled={isSubmitting}>
            Cancelar
          </Button>
          <Button onClick={handleSubmit} disabled={isSubmitting}>
            {isSubmitting ? "Salvando..." : "Salvar"}
          </Button>
        </SheetFooter>
      </SheetContent>
    </Sheet>
  )
}
