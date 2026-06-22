"use client"

import { useEffect, useState, type FormEvent } from "react"
import {
  UserAccessAPI,
  type AtualizarUsuarioAcessoRequest,
  type CriarUsuarioAcessoRequest,
  type UsuarioAcessoDTO,
} from "@/lib/api"
import { useToast } from "@/hooks/use-toast"
import { Button } from "@/components/ui/button"
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { useOperators } from "@/hooks/useOperators"

interface AddUserAccessDialogProps {
  open: boolean
  onOpenChange: (open: boolean) => void
  user?: UsuarioAcessoDTO | null
  onSuccess?: () => void
}

const NO_OPERATOR_VALUE = "__none__"

export function AddUserAccessDialog({ open, onOpenChange, user, onSuccess }: AddUserAccessDialogProps) {
  const { toast } = useToast()
  const { operators, isLoading: isLoadingOperators } = useOperators()
  const [isLoading, setIsLoading] = useState(false)
  const [username, setUsername] = useState("")
  const [senha, setSenha] = useState("")
  const [role, setRole] = useState<"ADMIN" | "OPERADOR" | "ANALISTA">("ANALISTA")
  const [ativo, setAtivo] = useState(true)
  const [operadorMissaoId, setOperadorMissaoId] = useState<string>(NO_OPERATOR_VALUE)

  useEffect(() => {
    if (!open) {
      return
    }

    if (user) {
      setUsername(user.username)
      setSenha("")
      setRole(user.role)
      setAtivo(user.ativo)
      setOperadorMissaoId(user.operadorMissaoId ?? NO_OPERATOR_VALUE)
      return
    }

    setUsername("")
    setSenha("")
    setRole("ANALISTA")
    setAtivo(true)
    setOperadorMissaoId(NO_OPERATOR_VALUE)
  }, [user, open])

  useEffect(() => {
    if (role !== "OPERADOR" && !user) {
      setOperadorMissaoId(NO_OPERATOR_VALUE)
    }
  }, [role, user])

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault()

    if (role === "OPERADOR" && operadorMissaoId === NO_OPERATOR_VALUE) {
      toast({
        title: "Operador obrigatorio",
        description: "Usuarios com perfil OPERADOR precisam estar vinculados a um operador de missao.",
        variant: "destructive",
      })
      return
    }

    setIsLoading(true)

    try {
      const parsedOperadorMissaoId = operadorMissaoId === NO_OPERATOR_VALUE ? undefined : Number(operadorMissaoId)

      if (user) {
        const payload: AtualizarUsuarioAcessoRequest = {
          role,
          ativo,
          novaSenha: senha.trim() ? senha : undefined,
          operadorMissaoId: parsedOperadorMissaoId,
        }

        await UserAccessAPI.atualizar(user.id, payload)
        toast({
          title: "Usuario atualizado",
          description: "O usuario de acesso foi atualizado com sucesso.",
        })
      } else {
        const payload: CriarUsuarioAcessoRequest = {
          username,
          senha,
          role,
          ativo,
          operadorMissaoId: parsedOperadorMissaoId,
        }

        await UserAccessAPI.criar(payload)
        toast({
          title: "Usuario criado",
          description: "O usuario de acesso foi criado com sucesso.",
        })
      }

      onSuccess?.()
      onOpenChange(false)
    } catch (error: any) {
      toast({
        title: "Erro",
        description: error?.message || "Nao foi possivel salvar o usuario.",
        variant: "destructive",
      })
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-[520px]">
        <DialogHeader>
          <DialogTitle>{user ? "Editar Usuario de Acesso" : "Adicionar Usuario de Acesso"}</DialogTitle>
          <DialogDescription>
            {user
              ? "Atualize perfil, status, senha e o vinculo com operador de missao."
              : "Crie um novo acesso e, se for OPERADOR, vincule-o a um operador de missao."}
          </DialogDescription>
        </DialogHeader>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="username">Username</Label>
            <Input
              id="username"
              value={username}
              onChange={(event) => setUsername(event.target.value)}
              placeholder="Ex: controle.missao"
              required
              disabled={!!user}
            />
          </div>

          <div className="space-y-2">
            <Label htmlFor="role">Perfil</Label>
            <Select value={role} onValueChange={(value) => setRole(value as "ADMIN" | "OPERADOR" | "ANALISTA")}>
              <SelectTrigger id="role" className="w-full">
                <SelectValue placeholder="Selecione o perfil" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="ADMIN">ADMIN</SelectItem>
                <SelectItem value="OPERADOR">OPERADOR</SelectItem>
                <SelectItem value="ANALISTA">ANALISTA</SelectItem>
              </SelectContent>
            </Select>
          </div>

          <div className="space-y-2">
            <Label htmlFor="operador-missao">Operador de Missao Vinculado</Label>
            <Select value={operadorMissaoId} onValueChange={setOperadorMissaoId} disabled={isLoadingOperators}>
              <SelectTrigger id="operador-missao" className="w-full">
                <SelectValue placeholder="Selecione um operador" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value={NO_OPERATOR_VALUE}>
                  {role === "OPERADOR" ? "Selecione um operador" : "Sem vinculo"}
                </SelectItem>
                {(operators ?? []).map((operator) => (
                  <SelectItem key={operator.id} value={operator.id}>
                    {operator.nome}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
            <p className="text-xs text-muted-foreground">
              {role === "OPERADOR"
                ? "Obrigatorio para que esse usuario visualize e registre apenas as proprias missoes."
                : "Opcional para perfis que nao operam missoes diretamente."}
            </p>
          </div>

          <div className="space-y-2">
            <Label htmlFor="senha">{user ? "Nova Senha" : "Senha"}</Label>
            <Input
              id="senha"
              type="password"
              value={senha}
              onChange={(event) => setSenha(event.target.value)}
              placeholder={user ? "Preencha apenas se quiser trocar" : "Minimo 6 caracteres"}
              required={!user}
            />
          </div>

          <div className="space-y-3">
            <Label>Status</Label>
            <RadioGroup value={ativo ? "ativo" : "inativo"} onValueChange={(value) => setAtivo(value === "ativo")}>
              <div className="flex items-center space-x-2">
                <RadioGroupItem value="ativo" id="status-ativo" />
                <Label htmlFor="status-ativo" className="cursor-pointer font-normal">
                  Ativo
                </Label>
              </div>
              <div className="flex items-center space-x-2">
                <RadioGroupItem value="inativo" id="status-inativo" />
                <Label htmlFor="status-inativo" className="cursor-pointer font-normal">
                  Inativo
                </Label>
              </div>
            </RadioGroup>
          </div>

          <div className="flex justify-end gap-3 pt-4">
            <Button type="button" variant="outline" onClick={() => onOpenChange(false)} disabled={isLoading}>
              Cancelar
            </Button>
            <Button type="submit" disabled={isLoading}>
              {isLoading ? "Salvando..." : user ? "Atualizar" : "Adicionar"}
            </Button>
          </div>
        </form>
      </DialogContent>
    </Dialog>
  )
}
