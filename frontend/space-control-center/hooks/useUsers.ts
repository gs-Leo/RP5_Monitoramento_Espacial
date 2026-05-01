import useSWR from "swr"
import { UserAccessAPI, type UsuarioAcessoDTO } from "@/lib/api"

export function useUsers() {
  const { data, error, isLoading, mutate } = useSWR<UsuarioAcessoDTO[]>("/usuarios-acesso", () => UserAccessAPI.listar())

  return {
    users: data,
    isLoading,
    isError: error,
    mutate,
  }
}
