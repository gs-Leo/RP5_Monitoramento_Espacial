import { clearStoredSession, loadStoredSession, type AuthSession } from "@/lib/auth"

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080"

export interface LoginRequest {
  username: string
  senha: string
}

export interface UsuarioAcessoDTO {
  id: string
  username: string
  role: "ADMIN" | "OPERADOR" | "ANALISTA"
  ativo: boolean
  operadorMissaoId?: string | null
  operadorMissaoNome?: string | null
}

export interface CriarUsuarioAcessoRequest {
  username: string
  senha: string
  role: "ADMIN" | "OPERADOR" | "ANALISTA"
  ativo: boolean
  operadorMissaoId?: number
}

export interface AtualizarUsuarioAcessoRequest {
  role: "ADMIN" | "OPERADOR" | "ANALISTA"
  ativo: boolean
  novaSenha?: string
  operadorMissaoId?: number
}

export interface AstronautDTO {
  id: string
  nome: string
  idade: number
  ativo: boolean
  nivelAptidaoMedica: "ALTO" | "MEDIO" | "BAIXO"
  missoesRealizadas: number
  tipoBiometria?: string
  valorBiometria?: string
  unidadeBiometria?: string
  registradoEm?: string
}

export interface CreateAstronautRequest {
  nome: string
  idade: number
  ativo: boolean
  nivelAptidaoMedica: "ALTO" | "MEDIO" | "BAIXO"
  missoesRealizadas: number
  tipoBiometria?: string
  valorBiometria?: string
  unidadeBiometria?: string
}

export interface EspaconaveDTO {
  id: string
  nome: string
  capacidade: number
  statusOperacional: "OPERACIONAL" | "EM_MANUTENCAO" | "DESATIVADA"
}

export interface SalvarEspaconaveRequest {
  nome: string
  capacidade: number
  statusOperacional: "OPERACIONAL" | "EM_MANUTENCAO" | "DESATIVADA"
}

export interface OperadorDeMissaoDTO {
  id: string
  nome: string
  idade: number
  turno: string
  areaEspecializacao: string
  ativo: boolean
}

export interface CriarOperadorRequest {
  nome: string
  idade: number
  turno: string
  areaEspecializacao: string
  ativo: boolean
}

export interface MissaoDTO {
  id: string
  nome: string
  objetivo: string
  dataInicio: string
  dataFim?: string
  status: "PLANEJADA" | "EM_ANDAMENTO" | "CONCLUIDA" | "FALHOU"
  tipoSimulacao?: "foguete" | "orbita" | "reentrada"
  tripulacao: AstronautDTO[]
  espaconave?: EspaconaveDTO
  operadorResponsavel?: OperadorDeMissaoDTO
}

export interface CriarMissaoRequest {
  nome: string
  objetivo: string
  dataInicio: string
  tipoSimulacao?: "foguete" | "orbita" | "reentrada"
  tripulacaoIds: number[]
  espaconaveId?: number
  operadorId?: number
}

export interface EventoDTO {
  id: string
  missaoId: string
  timestamp: string
  tipo: "INFO" | "ALERTA" | "ERRO_CRITICO"
  descricao: string
}

export interface ProtocoloEmergencialDTO {
  id: string
  missaoId: string
  tipo: "MEDICO" | "TECNICO" | "EVACUACAO"
  descricao: string
  acionadoEm: string
}

export interface AcionarProtocoloRequest {
  tipo: "MEDICO" | "TECNICO" | "EVACUACAO"
  descricao: string
}

async function parseError(response: Response) {
  try {
    const data = await response.json()
    return data?.message || data?.error || `Erro HTTP ${response.status}`
  } catch {
    const text = await response.text()
    return text || `Erro HTTP ${response.status}`
  }
}

function getSessionToken() {
  return loadStoredSession()?.token
}

async function apiFetch(path: string, init?: RequestInit, authenticated = true) {
  const headers = new Headers(init?.headers || {})

  if (!headers.has("Content-Type") && init?.body) {
    headers.set("Content-Type", "application/json")
  }

  if (authenticated) {
    const token = getSessionToken()
    if (!token) {
      throw new Error("Sessao expirada. Faca login novamente.")
    }
    headers.set("Authorization", `Bearer ${token}`)
  }

  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...init,
    headers,
  })

  if (response.status === 401) {
    clearStoredSession()
  }

  if (!response.ok) {
    throw new Error(await parseError(response))
  }

  return response
}

export class AuthAPI {
  static async login(data: LoginRequest): Promise<AuthSession> {
    const response = await apiFetch(
      "/auth/login",
      {
        method: "POST",
        body: JSON.stringify(data),
      },
      false,
    )

    return response.json()
  }
}

export class UserAccessAPI {
  static async listar(): Promise<UsuarioAcessoDTO[]> {
    const response = await apiFetch("/usuarios-acesso")
    return response.json()
  }

  static async criar(data: CriarUsuarioAcessoRequest): Promise<UsuarioAcessoDTO> {
    const response = await apiFetch("/usuarios-acesso", {
      method: "POST",
      body: JSON.stringify(data),
    })
    return response.json()
  }

  static async atualizar(id: string, data: AtualizarUsuarioAcessoRequest): Promise<UsuarioAcessoDTO> {
    const response = await apiFetch(`/usuarios-acesso/${id}`, {
      method: "PUT",
      body: JSON.stringify(data),
    })
    return response.json()
  }

  static async deletar(id: string): Promise<void> {
    await apiFetch(`/usuarios-acesso/${id}`, {
      method: "DELETE",
    })
  }
}

export class AstronautAPI {
  static async listar(): Promise<AstronautDTO[]> {
    const response = await apiFetch("/astronautas")
    return response.json()
  }

  static async buscarPorId(id: string): Promise<AstronautDTO> {
    const response = await apiFetch(`/astronautas/${id}`)
    return response.json()
  }

  static async criar(data: CreateAstronautRequest): Promise<AstronautDTO> {
    const response = await apiFetch("/astronautas", {
      method: "POST",
      body: JSON.stringify(data),
    })
    return response.json()
  }

  static async atualizar(id: string, data: CreateAstronautRequest): Promise<AstronautDTO> {
    const response = await apiFetch(`/astronautas/${id}`, {
      method: "PUT",
      body: JSON.stringify(data),
    })
    return response.json()
  }

  static async deletar(id: string): Promise<void> {
    await apiFetch(`/astronautas/${id}`, {
      method: "DELETE",
    })
  }
}

export class SpaceshipAPI {
  static async listar(): Promise<EspaconaveDTO[]> {
    const response = await apiFetch("/espaconaves")
    return response.json()
  }

  static async buscarPorId(id: string): Promise<EspaconaveDTO> {
    const response = await apiFetch(`/espaconaves/${id}`)
    return response.json()
  }

  static async criar(data: SalvarEspaconaveRequest): Promise<EspaconaveDTO> {
    const response = await apiFetch("/espaconaves", {
      method: "POST",
      body: JSON.stringify(data),
    })
    return response.json()
  }

  static async atualizar(id: string, data: SalvarEspaconaveRequest): Promise<EspaconaveDTO> {
    const response = await apiFetch(`/espaconaves/${id}`, {
      method: "PUT",
      body: JSON.stringify(data),
    })
    return response.json()
  }

  static async deletar(id: string): Promise<void> {
    await apiFetch(`/espaconaves/${id}`, {
      method: "DELETE",
    })
  }
}

export class OperatorAPI {
  static async listar(): Promise<OperadorDeMissaoDTO[]> {
    const response = await apiFetch("/operadores")
    return response.json()
  }

  static async buscarPorId(id: string): Promise<OperadorDeMissaoDTO> {
    const response = await apiFetch(`/operadores/${id}`)
    return response.json()
  }

  static async criar(data: CriarOperadorRequest): Promise<OperadorDeMissaoDTO> {
    const response = await apiFetch("/operadores", {
      method: "POST",
      body: JSON.stringify(data),
    })
    return response.json()
  }

  static async atualizar(id: string, data: CriarOperadorRequest): Promise<OperadorDeMissaoDTO> {
    const response = await apiFetch(`/operadores/${id}`, {
      method: "PUT",
      body: JSON.stringify(data),
    })
    return response.json()
  }

  static async deletar(id: string): Promise<void> {
    await apiFetch(`/operadores/${id}`, {
      method: "DELETE",
    })
  }
}

export class MissionAPI {
  static async listar(): Promise<MissaoDTO[]> {
    const response = await apiFetch("/missoes")
    return response.json()
  }

  static async buscarPorId(id: string): Promise<MissaoDTO> {
    const response = await apiFetch(`/missoes/${id}`)
    return response.json()
  }

  static async criar(data: CriarMissaoRequest): Promise<MissaoDTO> {
    const response = await apiFetch("/missoes", {
      method: "POST",
      body: JSON.stringify(data),
    })
    return response.json()
  }

  static async atualizar(id: string, data: CriarMissaoRequest): Promise<MissaoDTO> {
    const response = await apiFetch(`/missoes/${id}`, {
      method: "PUT",
      body: JSON.stringify(data),
    })
    return response.json()
  }

  static async concluir(id: string): Promise<MissaoDTO> {
    const response = await apiFetch(`/missoes/${id}/concluir`, {
      method: "POST",
    })
    return response.json()
  }

  static async deletar(id: string): Promise<void> {
    await apiFetch(`/missoes/${id}`, {
      method: "DELETE",
    })
  }
}

export class EventAPI {
  static async listarPorMissao(missaoId: string): Promise<EventoDTO[]> {
    const response = await apiFetch(`/missoes/${missaoId}/eventos`)
    return response.json()
  }
}

export class ProtocolAPI {
  static async listarPorMissao(missaoId: string): Promise<ProtocoloEmergencialDTO[]> {
    const response = await apiFetch(`/missoes/${missaoId}/protocolos`)
    return response.json()
  }

  static async acionar(missaoId: string, data: AcionarProtocoloRequest): Promise<ProtocoloEmergencialDTO> {
    const response = await apiFetch(`/missoes/${missaoId}/protocolos`, {
      method: "POST",
      body: JSON.stringify(data),
    })
    return response.json()
  }
}
